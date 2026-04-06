package one.lindegaard.BagOfGold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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
import one.lindegaard.CustomItemsLib.storage.DataStoreException;
import one.lindegaard.CustomItemsLib.storage.IDataCallback;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.PlayerSettings;
import one.lindegaard.CustomItemsLib.Tools;
import one.lindegaard.CustomItemsLib.rewards.CoreCustomItems;
import one.lindegaard.CustomItemsLib.storage.UserNotFoundException;

public class PlayerBalanceManager implements Listener {

	private BagOfGold plugin;
	private HashMap<UUID, PlayerBalances> mBalances = new HashMap<UUID, PlayerBalances>();
	private final Set<UUID> loadingBalances = ConcurrentHashMap.newKeySet();
	private final Set<UUID> failedBalanceLoads = ConcurrentHashMap.newKeySet();
	private final ConcurrentHashMap<UUID, Integer> balanceLoadAttempts = new ConcurrentHashMap<UUID, Integer>();
	private static final long[] LOAD_RETRY_DELAYS_TICKS = new long[] { 100L, 300L, 600L };
	private static final int MAX_LOAD_RETRIES = 3;

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

	public PlayerBalance getPlayerBalance(OfflinePlayer offlinePlayer, String world) {
		return null;
	}

	public PlayerBalance getPlayerBalance(OfflinePlayer offlinePlayer) {
		if (offlinePlayer.isOnline()) {
			String worldGroup = Core.getWorldGroupManager().getCurrentWorldGroup(offlinePlayer);
			GameMode gamemode = Core.getWorldGroupManager().getCurrentGameMode(offlinePlayer);
			return getPlayerBalance(offlinePlayer, worldGroup, gamemode);
		} else {
			String worldGroup = Core.getPlayerSettingsManager().getPlayerSettings(offlinePlayer).getLastKnownWorldGrp();
			GameMode gamemode = Core.getWorldGroupManager().getDefaultGameMode();
			return getPlayerBalance(offlinePlayer, worldGroup, gamemode);
		}
	}

	public PlayerBalance getPlayerBalanceInWorld(OfflinePlayer offlinePlayer, String world, GameMode gamemode) {
		String worldGroup = Core.getWorldGroupManager().getWorldGroup(world);
		return getPlayerBalance(offlinePlayer, worldGroup, gamemode);
	}

	public PlayerBalance getPlayerBalance(OfflinePlayer offlinePlayer, String worldGroup, GameMode gamemode) {
		UUID uuid = offlinePlayer.getUniqueId();

		if (loadingBalances.contains(uuid) || failedBalanceLoads.contains(uuid)) {
			PlayerBalances cachedBalances = mBalances.get(uuid);
			if (cachedBalances != null && cachedBalances.has(worldGroup, gamemode)) {
				return cachedBalances.getPlayerBalance(worldGroup, gamemode);
			}
			plugin.getMessages().debug("SYNC_SKIPPED_NOT_READY player=%s uuid=%s", offlinePlayer.getName(), uuid);
			return new PlayerBalance(offlinePlayer, worldGroup, gamemode);
		}

		if (mBalances.containsKey(uuid)) {
			if (mBalances.get(uuid).has(worldGroup, gamemode)) {
				return mBalances.get(uuid).getPlayerBalance(worldGroup, gamemode);
			}

			plugin.getMessages().debug("PlayerBalanceManager: creating new %s and %s", worldGroup, gamemode);
			PlayerBalances ps = mBalances.get(uuid);
			PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
			ps.putPlayerBalance(pb);
			setPlayerBalance(offlinePlayer, pb);
			return pb;
		}

		PlayerBalances ps = new PlayerBalances();
		try {
			plugin.getMessages().debug("PlayerBalanceManager: loading %s balance (%s,%s) from DB",
					offlinePlayer.getName(), worldGroup, gamemode);
			ps = plugin.getStoreManager().loadPlayerBalances(offlinePlayer);
			mBalances.put(uuid, ps);
		} catch (UserNotFoundException e) {
			PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
			ps.putPlayerBalance(pb);
			mBalances.put(uuid, ps);
			plugin.getMessages().debug("PlayerBalanceManager: UserNotFoundException - creating:%s", pb.toString());
			setPlayerBalance(offlinePlayer, pb);
			return pb;
		} catch (DataStoreException e) {
			loadingBalances.remove(uuid);
			failedBalanceLoads.add(uuid);
			plugin.getMessages().debug("DB_READ_FAILED player=%s uuid=%s", offlinePlayer.getName(), uuid);
			return new PlayerBalance(offlinePlayer, worldGroup, gamemode);
		}

		if (!ps.has(worldGroup, gamemode)) {
			PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
			ps.putPlayerBalance(pb);
			setPlayerBalance(offlinePlayer, pb);
			return pb;
		}

		return ps.getPlayerBalance(worldGroup, gamemode);
	}

	// TODO: remove parameter offlinePlayer
	public void setPlayerBalance(OfflinePlayer offlinePlayer, PlayerBalance playerBalance) {
		UUID uuid = offlinePlayer.getUniqueId();
		if (!mBalances.containsKey(uuid)) {
			plugin.getMessages().debug("PlayerBalanceManager - insert PlayerBlance to Memory");
			PlayerBalances ps = new PlayerBalances();
			ps.putPlayerBalance(playerBalance);
			mBalances.put(uuid, ps);
		} else {
			mBalances.get(uuid).putPlayerBalance(playerBalance);
		}

		if (loadingBalances.contains(uuid) || failedBalanceLoads.contains(uuid)) {
			plugin.getMessages().debug("SYNC_SKIPPED_NOT_READY player=%s uuid=%s", offlinePlayer.getName(), uuid);
			return;
		}

		plugin.getDataStoreManager().updatePlayerBalance(offlinePlayer, playerBalance);
	}

	/**
	 * Remove PlayerSettings from Memory minecraftMob.getFriendlyName()
	 * 
	 * @param offlinePlayer
	 */
	public void removePlayerBalance(OfflinePlayer offlinePlayer) {
		plugin.getMessages().debug("Removing %s from player settings cache", offlinePlayer.getName());
		UUID uuid = offlinePlayer.getUniqueId();
		mBalances.remove(uuid);
		loadingBalances.remove(uuid);
		failedBalanceLoads.remove(uuid);
		balanceLoadAttempts.remove(uuid);
	}

	/**
	 * Read PlayerSettings From database into Memory when player joins
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();

		if (!isBalanceReady(player)) {
			if (!loadingBalances.contains(uuid)) {
				loadingBalances.add(uuid);
				failedBalanceLoads.remove(uuid);
				balanceLoadAttempts.put(uuid, 0);
				load(player);
			}
			plugin.getMessages().debug("SYNC_SKIPPED_NOT_READY player=%s uuid=%s", player.getName(), uuid);
			return;
		}

		if (containsKey(player)) {
			plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
		}
	}

	/**
	 * Write PlayerSettings to Database when Player Quit and remove PlayerSettings
	 * from memory
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		PlayerSettings ps = Core.getPlayerSettingsManager().getPlayerSettings(player);
		ps.setLastKnownWorldGrp(Core.getWorldGroupManager().getCurrentWorldGroup(player));
		Core.getPlayerSettingsManager().setPlayerSettings(ps);

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
	 * Load PlayerSettings asynchronously from Database Misc
	 * 
	 * @param offlinePlayer
	 */
	public void load(final OfflinePlayer offlinePlayer) {
		UUID uuid = offlinePlayer.getUniqueId();
		loadingBalances.add(uuid);
		failedBalanceLoads.remove(uuid);
		balanceLoadAttempts.put(uuid, 0);
		load(offlinePlayer, 0);
	}

	private void load(final OfflinePlayer offlinePlayer, final int attempt) {
		plugin.getDataStoreManager().requestPlayerBalances(offlinePlayer, new IDataCallback<PlayerBalances>() {

			@Override
			public void onCompleted(PlayerBalances ps) {
				final UUID uuid = offlinePlayer.getUniqueId();
				loadingBalances.remove(uuid);
				failedBalanceLoads.remove(uuid);
				balanceLoadAttempts.remove(uuid);
				mBalances.put(uuid, ps);

				String worldGroup;
				GameMode gamemode;
				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;
					worldGroup = Core.getWorldGroupManager().getCurrentWorldGroup(player);
					gamemode = player.getGameMode();
					// Next line is important, to adjust the AmountInInventory to Balance
					plugin.getRewardManager().getAmountInInventory(player);
				} else {
					worldGroup = Core.getWorldGroupManager().getDefaultWorldgroup();
					gamemode = Core.getWorldGroupManager().getDefaultGameMode();
				}

				if (!ps.has(worldGroup, gamemode)) {
					PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
					ps.putPlayerBalance(pb);
					setPlayerBalance(offlinePlayer, pb);
				}

				plugin.getMessages().debug("BALANCE_LOAD_READY player=%s uuid=%s", offlinePlayer.getName(), uuid);

				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						if (offlinePlayer.isOnline() && ((Player) offlinePlayer).isValid()) {
							if (!isBalanceReady(offlinePlayer)) {
								plugin.getMessages().debug("SYNC_SKIPPED_NOT_READY player=%s uuid=%s",
										offlinePlayer.getName(), offlinePlayer.getUniqueId());
								return;
							}
							double amountInInventory = plugin.getRewardManager()
									.getAmountInInventory((Player) offlinePlayer);
							PlayerBalance pb = getPlayerBalance(offlinePlayer);
							if (Tools.round(amountInInventory) != Tools.round(pb.getBalance())
									+ Tools.round(pb.getBalanceChanges())) {
								double change = pb.getBalanceChanges();
								plugin.getMessages().debug(
										"Balance was changed while %s was offline. New balance is %s.",
										offlinePlayer.getName(), pb.getBalance() + change);
								pb.setBalance(pb.getBalance() + change);
								pb.setBalanceChanges(0);
								setPlayerBalance(offlinePlayer, pb);
								plugin.getRewardManager()
										.adjustAmountOfMoneyInInventoryToPlayerBalance((Player) offlinePlayer);
							}
						}
					}
				}, 40L);

			}

			@Override
			public void onError(Throwable error) {
				final UUID uuid = offlinePlayer.getUniqueId();
				loadingBalances.remove(uuid);
				failedBalanceLoads.add(uuid);
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BagOfGold][ERROR] Could not load "
						+ offlinePlayer.getName() + "'s balance from the database.");
				plugin.getMessages().debug("DB_READ_FAILED player=%s uuid=%s", offlinePlayer.getName(), uuid);

				int nextAttempt = attempt + 1;
				if (nextAttempt <= MAX_LOAD_RETRIES) {
					balanceLoadAttempts.put(uuid, nextAttempt);
					plugin.getMessages().debug("BALANCE_LOAD_RETRY attempt=%s player=%s uuid=%s", nextAttempt,
							offlinePlayer.getName(), uuid);
					Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
						@Override
						public void run() {
							if (!offlinePlayer.isOnline()) {
								return;
							}
							failedBalanceLoads.remove(uuid);
							loadingBalances.add(uuid);
							load(offlinePlayer, nextAttempt);
						}
					}, LOAD_RETRY_DELAYS_TICKS[nextAttempt - 1]);
				} else {
					Bukkit.getConsoleSender()
							.sendMessage(ChatColor.RED + "[BagOfGold][ALERT] Could not load "
									+ offlinePlayer.getName() + "'s balance after " + MAX_LOAD_RETRIES
									+ " retries.");
				}
			}

		});
	}

	public void loadTop54(final CommandSender sender, final int n, final String worldGroup, final int gamemode) {
		plugin.getDataStoreManager().requestTop54PlayerBalances(n, worldGroup, gamemode,
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

	public boolean isBalanceReady(OfflinePlayer offlinePlayer) {
		if (offlinePlayer == null) {
			return false;
		}
		UUID uuid = offlinePlayer.getUniqueId();
		return mBalances.containsKey(uuid) && !loadingBalances.contains(uuid) && !failedBalanceLoads.contains(uuid);
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
			Player player = (Player) sender;
			if (!playerBalances.isEmpty()) {
				Inventory inventory = Bukkit.createInventory(null, 54,
						ChatColor.BLUE + "" + ChatColor.BOLD + "TOP wealth players");
				int n = 0;
				for (PlayerBalance playerBalance : playerBalances) {
					addInventoryDetails(
							CoreCustomItems.getPlayerHead(playerBalance.getPlayer().getUniqueId(),
									playerBalance.getPlayer().getName(), 1,
									playerBalance.getBalance() + playerBalance.getBalanceChanges()
											+ playerBalance.getBankBalance() + playerBalance.getBankBalanceChanges()),
							inventory, n, ChatColor.GREEN + playerBalance.getPlayer().getName(),

							// Lores
							new String[] { ChatColor.GRAY + "" + ChatColor.ITALIC,
									ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
											+ plugin.getMessages().getString("bagofgold.commands.money.top", "total",
													playerBalance.getBalance() + playerBalance.getBalanceChanges()
															+ playerBalance.getBankBalance()
															+ playerBalance.getBankBalanceChanges(),
													"rewardname", Core.getConfigManager().bagOfGoldName.trim())

									,
									ChatColor.DARK_PURPLE + "WorldGrp:" + ChatColor.GREEN
											+ Core.getWorldGroupManager().getCurrentWorldGroup(player) + " ",

									ChatColor.DARK_PURPLE + "Mode:" + ChatColor.GREEN + player.getGameMode().toString()

							});
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
		if (!event.getView().getTitle().isEmpty()
				&& ChatColor.stripColor(event.getView().getTitle()).startsWith("TOP wealth players")) {
			event.setCancelled(true);
			event.getWhoClicked().closeInventory();
		}
	}

}
