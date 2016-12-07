package one.lindegaard.MobHunting.bounty;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.achievements.AchievementManager;
import one.lindegaard.MobHunting.storage.IDataCallback;
import one.lindegaard.MobHunting.storage.UserNotFoundException;
import one.lindegaard.MobHunting.util.Misc;

public class BountyManager implements Listener {

	private MobHunting instance;

	private static final String MH_BOUNTY = "MH:bounty";

	// mBounties contains all bounties on the OfflinePlayer and the Bounties put
	// on other players
	private static Set<Bounty> mOpenBounties = new HashSet<Bounty>();

	public BountyManager(MobHunting instance) {
		this.instance = instance;
		// if (MobHunting.ENABLE_TEST_BOUNTY)
		// addTestData();
		initialize();
	}

	private void initialize() {
		if (MobHunting.getConfigManager().enableRandomBounty) {
			Bukkit.getPluginManager().registerEvents(this, instance);
			Bukkit.getScheduler().runTaskTimer(instance, new Runnable() {
				public void run() {
					createRandomBounty();
				}
			}, MobHunting.getConfigManager().timeBetweenRandomBounties * 20 * 60,
					MobHunting.getConfigManager().timeBetweenRandomBounties * 20 * 60);
			Bukkit.getScheduler().runTaskTimer(MobHunting.getInstance(), new Runnable() {
				public void run() {
					for (Bounty bounty : mOpenBounties) {
						if (bounty.getEndDate() < System.currentTimeMillis()
								&& bounty.getStatus().equals(BountyStatus.open)) {
							bounty.setStatus(BountyStatus.expired);
							MobHunting.getDataStoreManager().updateBounty(bounty);
							Messages.debug("BountyManager: Expired Bounty %s", bounty.toString());
							mOpenBounties.remove(bounty);
						}
					}
				}
			}, 600, 7200);
		}
	}

	public void shutdown() {
	}

	/**
	 * put/add a bounty on the set of Bounties.
	 * 
	 * @param offlinePlayer
	 * @param bounty
	 */
	public void addBounty(Bounty bounty) {
		if (!hasBounty(bounty)) {
			Messages.debug("Insert bounty=%s", bounty.toString());
			mOpenBounties.add(bounty);
			MobHunting.getDataStoreManager().insertBounty(bounty);
		} else {
			Messages.debug("Updating bounty=%s", bounty.toString());
			getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner()).setPrize(
					getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner()).getPrize()
							+ bounty.getPrize());
			getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner())
					.setMessage(bounty.getMessage());
			MobHunting.getDataStoreManager()
					.insertBounty(getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner()));
		}

	}

	public Set<Bounty> getAllBounties() {
		return mOpenBounties;
	}

	public Set<OfflinePlayer> getWantedPlayers() {
		Set<OfflinePlayer> wantedPlayers = new HashSet<OfflinePlayer>();
		for (Bounty b : mOpenBounties) {
			if (!wantedPlayers.contains(b.getWantedPlayer()))
				wantedPlayers.add(b.getWantedPlayer());

		}
		return wantedPlayers;
	}

	public Bounty getBounty(String worldGroup, OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mOpenBounties) {
			if ((bounty.getBountyOwner() == null || bounty.getBountyOwner().equals(bountyOwner))
					&& bounty.getWantedPlayer().equals(wantedPlayer) && bounty.getWorldGroup().equals(worldGroup))
				return bounty;
		}
		return null;
	}

	public Bounty getBounty(Bounty bounty) {
		for (Bounty b : mOpenBounties) {
			if (b.equals(bounty))
				return bounty;
		}
		return null;
	}

	public Set<Bounty> getBounties(String worldGroup, OfflinePlayer wantedPlayer) {
		Set<Bounty> bounties = new HashSet<Bounty>();
		for (Bounty bounty : mOpenBounties) {
			if (bounty.getWantedPlayer().equals(wantedPlayer) && bounty.getWorldGroup().equals(worldGroup)) {
				bounties.add(bounty);
			}
		}
		return bounties;
	}

	public void markBountyCompleted2(String worldGroup, OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mOpenBounties) {
			if ((bounty.getBountyOwner() == null || bounty.getBountyOwner().equals(bountyOwner))
					&& bounty.getWantedPlayer().equals(wantedPlayer) && bounty.getWorldGroup().equals(worldGroup)) {
				bounty.setStatus(BountyStatus.completed);
				mOpenBounties.remove(bounty);
				MobHunting.getDataStoreManager().insertBounty(bounty);
				break;
			}
		}
	}

	public void cancelBounty(Bounty bounty) {
		getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner())
				.setStatus(BountyStatus.canceled);
		MobHunting.getDataStoreManager()
				.insertBounty(getBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner()));

		Iterator<Bounty> it = mOpenBounties.iterator();
		while (it.hasNext()) {
			Bounty b = (Bounty) it.next();
			if (b.equals(bounty))
				it.remove();
		}
	}

	public void removeBounty(Bounty bounty) {
		MobHunting.getDataStoreManager().deleteBounty(bounty);

		Iterator<Bounty> it = mOpenBounties.iterator();
		while (it.hasNext()) {
			Bounty b = (Bounty) it.next();
			if (b.equals(bounty))
				it.remove();
		}
	}

	public void sort() {
		Set<Bounty> sortedSet = new TreeSet<Bounty>(new BountyComparator()).descendingSet();
		sortedSet.addAll(mOpenBounties);
		mOpenBounties = sortedSet;
	}

	class BountyComparator implements Comparator<Bounty> {
		@Override
		public int compare(Bounty b1, Bounty b2) {
			if (b1.equals(b2))
				return Double.compare(b1.getPrize(), b2.getPrize());
			else if (b1.getWantedPlayer().getName().equals(b2.getWantedPlayer().getName()))
				if (b1.getBountyOwner() == null)
					return -1;
				else if (b2.getBountyOwner() == null)
					return 1;
				else
					return b1.getBountyOwner().getName().compareTo(b2.getBountyOwner().getName());
			else
				return b1.getWantedPlayer().getName().compareTo(b2.getWantedPlayer().getName());
		}
	}

	// Tests
	public boolean hasBounty(String worldGroup, OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mOpenBounties) {
			if (bounty.getWantedPlayer().equals(wantedPlayer) && bounty.getWorldGroup().equals(worldGroup)) {
				if (bountyOwner == null)
					if (bounty.getBountyOwner() == null)
						return true;
					else
						return bounty.getBountyOwner().equals(bountyOwner);
				return true;
			} else
				return false;
		}
		return false;
	}

	public boolean hasBounty(Bounty bounty) {
		for (Bounty b : mOpenBounties) {
			if (b.equals(bounty))
				return true;
		}
		return false;
	}

	public static boolean hasBounties(String worldGroup, OfflinePlayer wantedPlayer) {
		for (Bounty bounty : mOpenBounties) {
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
			addMarkOnWantedPlayer(player);
			loadOpenBounties(player);
			if (hasBounties(worldGroupName, player)) {
				Messages.playerActionBarMessage(player, Messages.getString("mobhunting.bounty.youarewanted"));
				Messages.broadcast(
						Messages.getString("mobhunting.bounty.playeriswanted", "playername", player.getName()), player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerQuit(PlayerQuitEvent e) {
		// saveBounties(e.getPlayer());
		Set<Bounty> toBeRemoved = new HashSet<Bounty>();

		Iterator<Bounty> itr = getAllBounties().iterator();
		int n = 0;
		while (itr.hasNext()) {
			Bounty bounty = itr.next();
			if (bounty.getWantedPlayer().equals(e.getPlayer())) {
				toBeRemoved.add(bounty);
				n++;
			}
		}
		mOpenBounties.removeAll(toBeRemoved);
		Messages.debug("%s bounties on %s was removed when player quit", n, e.getPlayer().getName());
	}

	// ****************************************************************************
	// Save & Load
	// ****************************************************************************
	public void loadOpenBounties(final OfflinePlayer offlinePlayer) {
		MobHunting.getDataStoreManager().requestBounties(BountyStatus.open, offlinePlayer,
				new IDataCallback<Set<Bounty>>() {

					@Override
					public void onCompleted(Set<Bounty> data) {
						boolean sort = false;
						int n = 0;
						Iterator<Bounty> itr = data.iterator();
						while (itr.hasNext()) {
							Bounty bounty = itr.next();
							if (!hasBounty(bounty.getWorldGroup(), bounty.getWantedPlayer(), bounty.getBountyOwner())) {
								if (bounty.getEndDate() > System.currentTimeMillis()
										&& bounty.getStatus().equals(BountyStatus.open)) {
									mOpenBounties.add(bounty);
									n++;
								} else {
									//
									Messages.debug("BountyManager: Expired onLoad Bounty %s", bounty.toString());
									bounty.setStatus(BountyStatus.expired);
									MobHunting.getDataStoreManager().updateBounty(bounty);
									removeBounty(bounty);
								}
								sort = true;
							}
						}
						if (sort)
							sort();
						Messages.debug("%s bounties for %s was loaded.", n, offlinePlayer.getName());
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

	// *************************************************************************************
	// BOUNTY GUI
	// *************************************************************************************

	// private static Inventory inventory;

	public static void showOpenBounties(CommandSender sender, String worldGroupName, OfflinePlayer wantedPlayer,
			boolean useGui) {
		if (sender instanceof Player) {
			// Player player = (Player) sender;

			if (hasBounties(worldGroupName, wantedPlayer)) {
				Set<Bounty> bountiesOnWantedPlayer = MobHunting.getBountyManager().getBounties(worldGroupName,
						wantedPlayer);
				if (useGui) {
					Inventory inventory = Bukkit.createInventory(null, 54,
							ChatColor.BLUE + "" + ChatColor.BOLD + "Wanted:" + wantedPlayer.getName());
					int n = 0;
					for (Bounty bounty : bountiesOnWantedPlayer) {
						if (bounty.isOpen()) {
							if (bounty.getBountyOwner() != null)
								AchievementManager.addInventoryDetails(Misc.getPlayerHead(wantedPlayer), inventory, n,
										ChatColor.GREEN + wantedPlayer.getName(),
										new String[] { ChatColor.WHITE + "", Messages.getString(
												"mobhunting.commands.bounty.bounties", "bountyowner",
												bounty.getBountyOwner().getName(), "prize",
												MobHunting.getRewardManager().format(bounty.getPrize()), "wantedplayer",
												bounty.getWantedPlayer().getName(), "daysleft",
												(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)) });
							else
								AchievementManager.addInventoryDetails(Misc.getPlayerHead(wantedPlayer), inventory, n,
										ChatColor.GREEN + wantedPlayer.getName(),
										new String[] { ChatColor.WHITE + "", Messages.getString(
												"mobhunting.commands.bounty.bounties", "bountyowner", "Random Bounty",
												"prize", MobHunting.getRewardManager().format(bounty.getPrize()),
												"wantedplayer", bounty.getWantedPlayer().getName(), "daysleft",
												(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)) });
							if (n < 52)
								n++;
						}
					}
					if (sender instanceof Player)
						((Player) sender).openInventory(inventory);
					else
						Bukkit.getConsoleSender()
								.sendMessage(ChatColor.RED + "This command can not used in the console");

				} else {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties-header"));
					sender.sendMessage("-----------------------------------");
					for (Bounty bounty : bountiesOnWantedPlayer) {
						if (bounty.isOpen())
							if (bounty.getBountyOwner() != null)
								sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties",
										"bountyowner", bounty.getBountyOwner().getName(), "prize",
										MobHunting.getRewardManager().format(bounty.getPrize()), "wantedplayer",
										bounty.getWantedPlayer().getName(), "daysleft",
										(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)));
							else
								sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties",
										"bountyowner", "Random Bounty", "prize",
										MobHunting.getRewardManager().format(bounty.getPrize()), "wantedplayer",
										bounty.getWantedPlayer().getName(), "daysleft",
										(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)));
					}
				}
			} else {
				sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-bounties-player", "wantedplayer",
						wantedPlayer.getName()));
			}
		} else {
			sender.sendMessage("[MobHunting] You cant use this command in the console");
		}
	}

	public static void showMostWanted(CommandSender sender, String worldGroupName, boolean useGui) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			// Messages.debug("mOpenBounties.size, args);
			if (!mOpenBounties.isEmpty()) {
				// Set<Bounty> bounties =
				// MobHunting.getBountyManager().getAllBounties();
				if (useGui) {
					Inventory inventory = Bukkit.createInventory(player, 54,
							ChatColor.BLUE + "" + ChatColor.BOLD + "MostWanted:");
					int n = 0;
					for (Bounty bounty : mOpenBounties) {
						if (bounty.getBountyOwner() != null)
							AchievementManager.addInventoryDetails(Misc.getPlayerHead(bounty.getWantedPlayer()),
									inventory, n, ChatColor.GREEN + bounty.getWantedPlayer().getName(),
									new String[] { ChatColor.WHITE + "", Messages.getString(
											"mobhunting.commands.bounty.bounties", "bountyowner",
											bounty.getBountyOwner().getName(), "prize",
											MobHunting.getRewardManager().format(bounty.getPrize()), "wantedplayer",
											bounty.getWantedPlayer().getName(), "daysleft",
											(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)) });
						else
							AchievementManager.addInventoryDetails(Misc.getPlayerHead(bounty.getWantedPlayer()),
									inventory, n, ChatColor.GREEN + bounty.getWantedPlayer().getName(),
									new String[] { ChatColor.WHITE + "", Messages.getString(
											"mobhunting.commands.bounty.bounties", "bountyowner", "Random Bounty",
											"prize", MobHunting.getRewardManager().format(bounty.getPrize()),
											"wantedplayer", bounty.getWantedPlayer().getName(), "daysleft",
											(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)) });
						if (n < 52)
							n++;
					}
					if (sender instanceof Player)
						((Player) sender).openInventory(inventory);
					else
						Bukkit.getConsoleSender()
								.sendMessage(ChatColor.RED + "This command can not used in the console");
				} else {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties-header"));
					sender.sendMessage("-----------------------------------");
					for (Bounty bounty : mOpenBounties) {
						if (bounty.getBountyOwner() != null)
							sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties", "bountyowner",
									bounty.getBountyOwner().getName(), "prize",
									MobHunting.getRewardManager().format(bounty.getPrize()), "wantedplayer",
									bounty.getWantedPlayer().getName(), "daysleft",
									(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)));
						else
							sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties", "bountyowner",
									"Random Bounty", "prize", MobHunting.getRewardManager().format(bounty.getPrize()),
									"wantedplayer", bounty.getWantedPlayer().getName(), "daysleft",
									(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)));
					}
				}
			} else {
				sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-bounties"));
			}
		} else {
			sender.sendMessage("[MobHunting] You cant use this command in the console");
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Inventory inv = event.getInventory();
		if (ChatColor.stripColor(inv.getName()).startsWith("MostWanted:")
				|| ChatColor.stripColor(inv.getName()).startsWith("Wanted:")) {
			final Player player = (Player) event.getWhoClicked();
			event.setCancelled(true);
			Messages.debug("BountyManager: Player clicked on inventory - closing now");
			Bukkit.getScheduler().runTask(instance, new Runnable() {
				public void run() {
					player.closeInventory();
				}
			});
		}
	}

	// ***********************************************************
	// RANDOM BOUNTY
	// ***********************************************************

	public void createRandomBounty() {
		boolean createBounty = MobHunting.getMobHuntingManager().mRand
				.nextDouble() <= MobHunting.getConfigManager().chanceToCreateBounty;
		if (createBounty) {
			int noOfPlayers = MobHunting.getMobHuntingManager().getOnlinePlayersAmount();
			Player randomPlayer = null;
			if (MobHunting.getConfigManager().minimumNumberOfOnlinePlayers <= noOfPlayers) {

				int random = MobHunting.getMobHuntingManager().mRand.nextInt(noOfPlayers);
				int n = 0;
				for (Player player : MobHunting.getMobHuntingManager().getOnlinePlayers()) {
					if (n == random) {
						randomPlayer = player;
						break;
					} else
						n++;
				}
				if (randomPlayer != null) {
					String worldGroup = MobHunting.getWorldGroupManager().getCurrentWorldGroup(randomPlayer);
					Bounty randomBounty = new Bounty(worldGroup, randomPlayer,
							MobHunting.getConfigManager().getRandomPrice(MobHunting.getConfigManager().randomBounty),
							"Random Bounty");
					addBounty(randomBounty);
					for (Player player : MobHunting.getMobHuntingManager().getOnlinePlayers()) {
						if (player.getName().equals(randomPlayer.getName()))
							Messages.playerActionBarMessage(player,
									Messages.getString("mobhunting.bounty.randombounty.self", "prize",
											MobHunting.getRewardManager().format(randomBounty.getPrize())));
						else
							Messages.playerActionBarMessage(player,
									Messages.getString("mobhunting.bounty.randombounty", "prize",
											MobHunting.getRewardManager().format(randomBounty.getPrize()), "playername",
											randomPlayer.getName()));
					}
				}
			}
		}
	}

}