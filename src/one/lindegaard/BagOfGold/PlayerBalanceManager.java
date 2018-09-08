package one.lindegaard.BagOfGold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import one.lindegaard.BagOfGold.compatibility.EssentialsCompat;
import one.lindegaard.BagOfGold.rewards.CustomItems;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataCallback;
import one.lindegaard.BagOfGold.storage.UserNotFoundException;
import one.lindegaard.BagOfGold.util.Misc;

public class PlayerBalanceManager implements Listener {

	private BagOfGold plugin;
	private HashMap<UUID, PlayerBalances> mBalances = new HashMap<UUID, PlayerBalances>();

	/**
	 * Constructor for the PlayerBalanceManager
	 */
	PlayerBalanceManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public HashMap<UUID, PlayerBalances> getBalances() {
		return mBalances;
	}

	public PlayerBalance getPlayerBalance(OfflinePlayer offlinePlayer) {
		if (offlinePlayer.isOnline()) {
			String worldGroup = plugin.getWorldGroupManager().getCurrentWorldGroup(offlinePlayer);
			GameMode gamemode = plugin.getWorldGroupManager().getCurrentGameMode(offlinePlayer);
			return getPlayerBalance(offlinePlayer, worldGroup, gamemode);
		} else {
			String worldGroup = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer)
					.getLastKnownWorldGrp();
			GameMode gamemode = plugin.getWorldGroupManager().getDefaultGameMode();
			return getPlayerBalance(offlinePlayer, worldGroup, gamemode);
		}
	}

	public PlayerBalance getPlayerBalance(OfflinePlayer offlinePlayer, String worldGroup, GameMode gamemode) {
		if (mBalances.containsKey(offlinePlayer.getUniqueId()))
			// offlinePlayer is in the Database
			if (mBalances.get(offlinePlayer.getUniqueId()).has(worldGroup, gamemode)) {
				return mBalances.get(offlinePlayer.getUniqueId()).getPlayerBalance(worldGroup, gamemode);
			} else {
				plugin.getMessages().debug("PlayerBalanceManager: creating new %s and %s", worldGroup, gamemode);
				PlayerBalances ps = mBalances.get(offlinePlayer.getUniqueId());
				PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
				ps.putPlayerBalance(pb);
				setPlayerBalance(offlinePlayer, pb);
				return pb;
			}
		else {
			// offlinePlayer is NOT in memory, try loading from DB
			PlayerBalances ps = new PlayerBalances();
			PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
			try {
				plugin.getMessages().debug("PlayerBalanceManager: loading %s balance (%s,%s) from DB",
						offlinePlayer.getName(), worldGroup, gamemode);
				ps = plugin.getStoreManager().loadPlayerBalances(offlinePlayer);
				pb = ps.getPlayerBalance(worldGroup, gamemode);
			} catch (UserNotFoundException e) {
				plugin.getMessages().debug("PlayerBalanceManager: UserNotFoundException - setPlayerBalances:%s",
						pb.toString());
				setPlayerBalance(offlinePlayer, pb);
			} catch (DataStoreException e) {
				e.printStackTrace();
			}
			if (!ps.has(worldGroup, gamemode)) {
				plugin.getMessages().debug("PlayerBalanceManager: creating new balance:%s", pb.toString());
				setPlayerBalance(offlinePlayer, pb);
			}
			mBalances.put(offlinePlayer.getUniqueId(), ps);
			return pb;
		}
	}

	// TODO: remove parameter offlinePlayer
	public void setPlayerBalance(OfflinePlayer offlinePlayer, PlayerBalance playerBalance) {
		if (!mBalances.containsKey(offlinePlayer.getUniqueId())) {
			plugin.getMessages().debug("PlayerBalanceManager - insert PlayerBlance to Memory");
			PlayerBalances ps = new PlayerBalances();
			ps.putPlayerBalance(playerBalance);
			mBalances.put(offlinePlayer.getUniqueId(), ps);
		} else {
			mBalances.get(offlinePlayer.getUniqueId()).putPlayerBalance(playerBalance);
		}
		plugin.getDataStoreManager().updatePlayerBalance(offlinePlayer, playerBalance);
	}

	/**
	 * Remove PlayerSettings from Memory
	 * 
	 * @param offlinePlayer
	 */
	public void removePlayerBalance(OfflinePlayer offlinePlayer) {
		plugin.getMessages().debug("Removing %s from player settings cache", offlinePlayer.getName());
		mBalances.remove(offlinePlayer.getUniqueId());
	}

	/**
	 * Read PlayerSettings From database into Memory when player joins
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!containsKey(player)) {
			PlayerBalances playerBalances = new PlayerBalances();
			PlayerBalance playerBalance = new PlayerBalance(player);
			playerBalances.putPlayerBalance(playerBalance);
			mBalances.put(player.getUniqueId(), playerBalances);
			load(player);
		}
	}

	/**
	 * Write PlayerSettings to Database when Player Quit and remove
	 * PlayerSettings from memory
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();

		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(player);
		ps.setLastKnownWorldGrp(plugin.getWorldGroupManager().getCurrentWorldGroup(player));
		plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);

		// update Essentials balance
		if (EssentialsCompat.isSupported()) {
			final double balance = getPlayerBalance(player).getBalance();
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				@Override
				public void run() {
					EssentialsCompat.setEssentialsBalance(player, balance);
				}
			}, 100L);
		}
	}

	/**
	 * Load PlayerSettings asynchronously from Database
	 * 
	 * @param offlinePlayer
	 */
	public void load(final OfflinePlayer offlinePlayer) {
		plugin.getDataStoreManager().requestPlayerBalances(offlinePlayer, new IDataCallback<PlayerBalances>() {

			@Override
			public void onCompleted(PlayerBalances ps) {
				String worldGroup;
				GameMode gamemode;
				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;
					worldGroup = plugin.getWorldGroupManager().getCurrentWorldGroup(player);
					gamemode = player.getGameMode();
				} else {
					worldGroup = plugin.getWorldGroupManager().getDefaultWorldgroup();
					gamemode = plugin.getWorldGroupManager().getDefaultGameMode();
				}
				if (!ps.has(worldGroup, gamemode)) {
					PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
					ps.putPlayerBalance(pb);
					setPlayerBalance(offlinePlayer, pb);
				}
				mBalances.put(offlinePlayer.getUniqueId(), ps);

				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						if (offlinePlayer.isOnline() && ((Player) offlinePlayer).isValid()) {
							double amountInInventory = plugin.getEconomyManager()
									.getAmountInInventory((Player) offlinePlayer);
							PlayerBalance pb = getPlayerBalance(offlinePlayer);
							if (Misc.round(amountInInventory) != Misc.round(pb.getBalance())
									+ Misc.round(pb.getBalanceChanges())) {
								double change = pb.getBalanceChanges();
								plugin.getMessages().debug(
										"Balance was changed while %s was offline. New balance is %s.",
										offlinePlayer.getName(), pb.getBalance() + change);
								pb.setBalance(pb.getBalance() + change);
								pb.setBalanceChanges(0);
								setPlayerBalance(offlinePlayer, pb);
								plugin.getEconomyManager().adjustAmountInInventoryToBalance((Player) offlinePlayer);
							}
						}
					}
				}, 40L);

			}

			@Override
			public void onError(Throwable error) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BagOfGold][ERROR] Could not load "
						+ offlinePlayer.getName() + "'s balance from the database.");
				mBalances.put(offlinePlayer.getUniqueId(), new PlayerBalances());
			}

		});
	}

	public void loadTop25(final CommandSender sender, final int n, final String worldGroup, final int gamemode) {
		plugin.getDataStoreManager().requestTop25PlayerBalances(n, worldGroup, gamemode,
				new IDataCallback<List<PlayerBalance>>() {

					@Override
					public void onCompleted(List<PlayerBalance> playerBalances) {
						showTopPlayers(sender, playerBalances);
					}

					@Override
					public void onError(Throwable error) {

					}
				});
	}

	/**
	 * Test if PlayerSettings contains data for Player
	 * 
	 * @param player
	 * @return true if player exists in PlayerSettings in Memory
	 */
	public boolean containsKey(final OfflinePlayer player) {
		return mBalances.containsKey(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		ps.setBalance(0);
		ps.setBalanceChanges(0);
		setPlayerBalance(player, ps);
		plugin.getMessages().debug("PlayerBalancManager: player died balance=0");
	}

	private HashMap<CommandSender, Inventory> inventoryMap = new HashMap<CommandSender, Inventory>();

	public void showTopPlayers(CommandSender sender, List<PlayerBalance> playerBalances) {
		if (sender instanceof Player) {
			if (!playerBalances.isEmpty()) {
				CustomItems customItems = new CustomItems(plugin);
				Inventory inventory = Bukkit.createInventory(null, 54,
						ChatColor.BLUE + "" + ChatColor.BOLD + "TOP players");
				int n = 0;
				for (PlayerBalance playerBalance : playerBalances) {
					addInventoryDetails(
							customItems.getPlayerHead(playerBalance.getPlayer().getUniqueId(), 1,
									playerBalance.getBalance() + playerBalance.getBalanceChanges()
											+ playerBalance.getBankBalance() + playerBalance.getBankBalanceChanges()),
							inventory, n, ChatColor.GREEN + playerBalance.getPlayer().getName(),
							new String[] { ChatColor.GRAY + "" + ChatColor.ITALIC,
									ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
											+ plugin.getMessages().getString("bagofgold.commands.money.top", "total",
													playerBalance.getBalance() + playerBalance.getBalanceChanges()
															+ playerBalance.getBankBalance()
															+ playerBalance.getBankBalanceChanges(),
													"rewardname",
													plugin.getConfigManager().dropMoneyOnGroundSkullRewardName) });
					if (n < 53)
						n++;
				}
				inventoryMap.put((Player) sender, inventory);
				((Player) sender).openInventory(inventoryMap.get(sender));
			}
		} else {
			sender.sendMessage("[BagOgGold] You cant use this command in the console");
		}
	}

	public static void addInventoryDetails(ItemStack itemStack, Inventory inv, int Slot, String name, String[] lores) {
		final int max = 40;
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(name);
		ArrayList<String> lore = new ArrayList<String>();
		for (int n = 0; n < lores.length; n = n + 2) {
			String color = lores[n];
			String line, rest = lores[n + 1];
			while (!rest.isEmpty()) {
				if (rest.length() < max) {
					lore.add(color + rest);
					break;
				} else {
					int splitPos = rest.substring(0, max).lastIndexOf(" ");
					if (splitPos != -1) {
						line = rest.substring(0, splitPos);
						rest = rest.substring(splitPos + 1);
					} else {
						line = rest.substring(0, max);
						rest = rest.substring(max);
					}
					lore.add(color + line);
				}
			}
		}
		meta.setLore(lore);
		itemStack.setItemMeta(meta);

		inv.setItem(Slot, itemStack);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onInventoryClick(InventoryClickEvent event) {
		if (ChatColor.stripColor(event.getInventory().getName()).startsWith("TOP players")) {
			event.setCancelled(true);
			event.getWhoClicked().closeInventory();
		}
	}

}
