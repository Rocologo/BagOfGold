package one.lindegaard.MobHunting.bounty;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.achievements.AchievementManager;
import one.lindegaard.MobHunting.storage.IDataCallback;
import one.lindegaard.MobHunting.storage.UserNotFoundException;
import one.lindegaard.MobHunting.storage.asynch.BountyRetrieverTask.BountyMode;

public class BountyManager implements Listener {

	private MobHunting instance;

	private static final String MH_BOUNTY = "MH:bounty";

	// mBounties contains all bounties on the OfflinePlayer and the Bounties put
	// on other players
	private static Set<Bounty> mBounties = new HashSet<Bounty>();
	// private static HashMap<OfflinePlayer,Inventory> inventory = new
	// HashMap<OfflinePlayer,Inventory>();

	public BountyManager(MobHunting instance) {
		this.instance = instance;
		// if (MobHunting.ENABLE_TEST_BOUNTY)
		// addTestData();
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
	}

	@SuppressWarnings("deprecation")
	private void addTestData() {
		OfflinePlayer p1 = Bukkit.getOfflinePlayer("Gabriel333");
		OfflinePlayer p2 = Bukkit.getOfflinePlayer("JeansenDK");
		OfflinePlayer p3 = Bukkit.getOfflinePlayer("MrDanielBoy");

		MobHunting.debug("Loading p1,p2,p3 from database");
		loadBounties(p1);
		loadBounties(p2);
		loadBounties(p3);

		MobHunting.getInstance().getServer().getScheduler().runTaskLater(MobHunting.getInstance(), new Runnable() {
			public void run() {

				OfflinePlayer p1 = Bukkit.getOfflinePlayer("Gabriel333");
				OfflinePlayer p2 = Bukkit.getOfflinePlayer("JeansenDK");
				OfflinePlayer p3 = Bukkit.getOfflinePlayer("MrDanielBoy");

				Bounty b1 = new Bounty("Default", p3, p2, 101, "he he he");
				Bounty b2 = new Bounty("Default", p3, p1, 102, "ho ho ho");
				Bounty b3 = new Bounty("Default", p2, p3, 200, "ha ha ha");
				Bounty b4 = new Bounty("Default", p1, p3, 300, "hi hi hi");

				MobHunting.debug("BountyManager AddTestData if not exist. Adding b1,b2,b3,b4 size=%s",
						mBounties.size());
				if (!hasBounty(b1))
					addBounty(b1);
				if (!hasBounty(b2))
					addBounty(b2);
				if (!hasBounty(b3))
					addBounty(b3);
				if (!hasBounty(b4))
					addBounty(b4);
			}
		}, 300L);

	}

	public void shutdown() {
	}

	public void insertBountyOnWantedPlayer(Bounty bounty) {
		MobHunting.getDataStoreManager().insertBounty(bounty);
	}

	/**
	 * put/add a bounty on the set of Bounties.
	 * 
	 * @param offlinePlayer
	 * @param bounty
	 */
	public void addBounty(Bounty bounty) {
		if (!hasBounty(bounty)) {
			// MobHunting.debug("Adding bounty=%s", bounty.toString());
			mBounties.add(bounty);
			MobHunting.getDataStoreManager().insertBounty(bounty);
		} else {
			// MobHunting.debug("Updating bounty=%s", bounty.toString());
			getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner()).setPrize(
					getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner()).getPrize()
							+ bounty.getPrize());
			getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner())
					.setMessage(bounty.getMessage());
			MobHunting.getDataStoreManager()
					.insertBounty(getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner()));
		}

	}

	public Set<Bounty> getBounties() {
		return mBounties;
	}

	public Set<OfflinePlayer> getWantedPlayers() {
		Set<OfflinePlayer> wantedPlayers = new HashSet<OfflinePlayer>();
		for (Bounty b : mBounties) {
			if (!wantedPlayers.contains(b.getWantedPlayer()))
				wantedPlayers.add(b.getWantedPlayer());

		}
		return wantedPlayers;
	}

	public Bounty getBounty(String worldGroup, OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mBounties) {
			if (bounty.getBountyOwner().equals(bountyOwner) && bounty.getWantedPlayer().equals(wantedPlayer)
					&& bounty.getWorldGroup().equals(worldGroup))
				return bounty;
		}
		return null;
	}

	public Bounty getBounty(Bounty bounty) {
		for (Bounty b : mBounties) {
			if (b.equals(bounty))
				return bounty;
		}
		return null;
	}

	public Set<Bounty> getBounties(String worldGroup, OfflinePlayer wantedPlayer) {
		Set<Bounty> bounties = new HashSet<Bounty>();
		for (Bounty bounty : mBounties) {
			if (bounty.getWantedPlayer().equals(wantedPlayer) && bounty.getWorldGroup().equals(worldGroup)) {
				bounties.add(bounty);
			}
		}
		return bounties;
	}

	public void markBountyCompleted(String worldGroup, OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mBounties) {
			if (bounty.getBountyOwner().equals(bountyOwner) && bounty.getWantedPlayer().equals(wantedPlayer)
					&& bounty.getWorldGroup().equals(worldGroup)) {
				bounty.setStatus(BountyStatus.completed);
				break;
			}
		}
	}

	public void cancelBounty(Bounty bounty) {
		getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner())
				.setStatus(BountyStatus.canceled);
		MobHunting.getDataStoreManager()
				.insertBounty(getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner()));

		Iterator<Bounty> it = mBounties.iterator();
		while (it.hasNext()) {
			Bounty b = (Bounty) it.next();
			if (b.equals(bounty))
				it.remove();
		}
	}

	public void removeBounty(Bounty bounty) {
		MobHunting.getDataStoreManager().deleteBounty(bounty);
		Iterator<Bounty> it = mBounties.iterator();
		while (it.hasNext()) {
			Bounty b = (Bounty) it.next();
			if (b.equals(bounty))
				it.remove();
		}
	}

	public void sort() {
		Set<Bounty> sortedSet = new TreeSet<Bounty>(new BountyComparator()).descendingSet();
		sortedSet.addAll(mBounties);
		mBounties = sortedSet;
	}

	class BountyComparator implements Comparator<Bounty> {
		@Override
		public int compare(Bounty b1, Bounty b2) {
			if (b1.equals(b2))
				return Double.compare(b1.getPrize(), b2.getPrize());
			else if (b1.getWantedPlayer().getName().equals(b2.getWantedPlayer().getName()))
				return b1.getBountyOwner().getName().compareTo(b2.getBountyOwner().getName());
			else
				return b1.getWantedPlayer().getName().compareTo(b2.getWantedPlayer().getName());
		}
	}

	// Tests
	public boolean hasBounty(String worldGroup, OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mBounties) {
			if (bounty.getBountyOwner().equals(bountyOwner) && bounty.getWantedPlayer().equals(wantedPlayer)
					&& bounty.getWorldGroup().equals(worldGroup))
				return true;
		}
		return false;
	}

	public boolean hasBounty(Bounty bounty) {
		for (Bounty b : mBounties) {
			if (b.equals(bounty))
				return true;
		}
		return false;
	}

	public boolean hasBounties(String worldGroup, OfflinePlayer wantedPlayer) {
		for (Bounty bounty : mBounties) {
			if (bounty.getWantedPlayer().equals(wantedPlayer))
				return true;
		}
		return false;
	}

	// Metadata Methods

	public void addMarkOnWantedPlayer(Player player) {
		player.setMetadata(MH_BOUNTY, new FixedMetadataValue(instance, true));
	}

	public void removeMarkFromWantedPlayer(Player player) {
		if (player.hasMetadata(MH_BOUNTY))
			player.removeMetadata(MH_BOUNTY, instance);
	}

	public boolean isMarkedWantedPlayer(Player player) {
		return player.hasMetadata(MH_BOUNTY);
	}

	// ****************************************************************************
	// Events
	// ****************************************************************************

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		if (!MobHunting.getConfigManager().disablePlayerBounties) {
			String worldGroupName = MobHunting.getWorldGroupManager().getCurrentWorldGroup(player);
			if (MobHunting.getBountyManager().hasBounties(worldGroupName, player)) {
				MobHunting.playerActionBarMessage(player, Messages.getString("mobhunting.bounty.youarewanted"));
			}
			addMarkOnWantedPlayer(player);
			loadBounties(player);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerQuitEvent e) {
		// saveBounties(e.getPlayer());
	}

	// ****************************************************************************
	// Save & Load
	// ****************************************************************************
	public void loadBounties(final OfflinePlayer offlinePlayer) {
		MobHunting.getDataStoreManager().requestBounties(BountyMode.Open, offlinePlayer,
				new IDataCallback<Set<Bounty>>() {

					@Override
					public void onCompleted(Set<Bounty> data) {
						boolean sort = false;
						for (Bounty bounty : data) {
							if (bounty.isOpen() && !hasBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(),
									bounty.getBountyOwner())) {
								mBounties.add(bounty);
								sort = true;
							}
						}
						if (sort)
							sort();
					}

					@Override
					public void onError(Throwable error) {
						if (error instanceof UserNotFoundException)
							if (offlinePlayer.isOnline()) {
								Player p = (Player) offlinePlayer;
								p.sendMessage(Messages.getString("mobhunting.bounty.user-not-found"));
							} else {
								error.printStackTrace();
								if (offlinePlayer.isOnline()) {
									Player p = (Player) offlinePlayer;
									p.sendMessage(Messages.getString("mobhunting.bounty.load-fail"));
								}
							}

					}

				});
	}

	// ***********************************************************
	// RANDOM BOUNTY
	// ***********************************************************

	public void randomBounty() {

	}

	// *************************************************************************************
	// BOUNTY GUI
	// *************************************************************************************

	private static Inventory inventory;

	public static void showOpenBounties(CommandSender sender, String worldGroupName, OfflinePlayer wantedPlayer,
			boolean useGui) {
		if (MobHunting.getBountyManager().hasBounties(worldGroupName, wantedPlayer)) {
			Set<Bounty> bounties = MobHunting.getBountyManager().getBounties(worldGroupName, wantedPlayer);
			if (useGui) {
				inventory = Bukkit.createInventory((InventoryHolder) sender, 54,
						ChatColor.BLUE + "" + ChatColor.BOLD + "Wanted:" + wantedPlayer.getName());
				int n = 0;
				for (Bounty bounty : bounties) {
					AchievementManager.addInventoryDetails(getPlayerHead(wantedPlayer), inventory, n,
							ChatColor.GREEN + wantedPlayer.getName(),
							new String[] { ChatColor.WHITE + "",
									Messages.getString("mobhunting.commands.bounty.bounties", "bountyowner",
											bounty.getBountyOwner().getName(), "prize",
											String.format("%.2f", bounty.getPrize()), "wantedplayer",
											bounty.getWantedPlayer().getName(), "daysleft",
											(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)) });
					if (n < 52)
						n++;
				}
				if (sender instanceof Player)
					((Player) sender).openInventory(inventory);
				else
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "This command can not used in the console");

			} else {
				sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties-header"));
				sender.sendMessage("-----------------------------------");
				for (Bounty bounty : bounties) {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties", "bountyowner",
							bounty.getBountyOwner().getName(), "prize", String.format("%.2f", bounty.getPrize()),
							"wantedplayer", bounty.getWantedPlayer().getName(), "daysleft",
							(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)));
				}
			}
		} else {
			sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-bounties-player", "wantedplayer",
					wantedPlayer.getName()));
		}
	}

	private static ItemStack getPlayerHead(OfflinePlayer wantedPlayer) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
		skull.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(wantedPlayer.getName());
		skull.setItemMeta(skullMeta);
		return skull;
	}

	public static void showMostWanted(CommandSender sender, String worldGroupName, boolean useGui) {
		if (!MobHunting.getBountyManager().getBounties().isEmpty()) {
			Set<Bounty> bounties = MobHunting.getBountyManager().getBounties();
			if (useGui) {
				Inventory inventory = Bukkit.createInventory((InventoryHolder) sender, 54,
						ChatColor.BLUE + "" + ChatColor.BOLD + "MostWanted:");
				int n = 0;
				for (Bounty bounty : bounties) {
					AchievementManager.addInventoryDetails(getPlayerHead(bounty.getWantedPlayer()), inventory, n,
							ChatColor.GREEN + bounty.getWantedPlayer().getName(),
							new String[] { ChatColor.WHITE + "",
									Messages.getString("mobhunting.commands.bounty.bounties", "bountyowner",
											bounty.getBountyOwner().getName(), "prize",
											String.format("%.2f", bounty.getPrize()), "wantedplayer",
											bounty.getWantedPlayer().getName(), "daysleft",
											(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)) });
					if (n < 52)
						n++;
				}
				if (sender instanceof Player)
					((Player) sender).openInventory(inventory);
				else
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "This command can not used in the console");
			} else {
				sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties-header"));
				sender.sendMessage("-----------------------------------");
				for (Bounty bounty : bounties) {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties", "bountyowner",
							bounty.getBountyOwner().getName(), "prize", String.format("%.2f", bounty.getPrize()),
							"wantedplayer", bounty.getWantedPlayer().getName(), "daysleft",
							(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)));
				}
			}
		} else {
			sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-bounties"));
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack clicked = event.getCurrentItem();
		Inventory inv = event.getInventory();
		if (inv != null && inventory != null)
			if (inv.getName().equals(inventory.getName())) {
				if (clicked != null && clicked.getType() == Material.DIRT) {
					// TODO:
				}
				event.setCancelled(true);
				player.closeInventory();
				// player.openInventory(inventory2);
			}
		// if (inv.getName().equals(inventory2.getName())) {
		// if (clicked != null && clicked.getType() == Material.DIRT) {
		// // TODO:
		// }
		// event.setCancelled(true);
		// player.closeInventory();
		// }
	}

}
