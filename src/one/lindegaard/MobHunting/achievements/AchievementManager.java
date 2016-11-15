package one.lindegaard.MobHunting.achievements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MinecraftMob;
import one.lindegaard.MobHunting.storage.AchievementStore;
import one.lindegaard.MobHunting.storage.IDataCallback;
import one.lindegaard.MobHunting.storage.UserNotFoundException;
import one.lindegaard.MobHunting.util.Misc;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class AchievementManager implements Listener {

	// String contains ID
	private HashMap<String, Achievement> mAchievements = new HashMap<String, Achievement>();
	private WeakHashMap<Player, PlayerStorage> mStorage = new WeakHashMap<Player, PlayerStorage>();

	public AchievementManager() {
		registerAchievements();
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
	}

	public Achievement getAchievement(String id) {
		if (!mAchievements.containsKey(id))
			throw new IllegalArgumentException("There is no achievement by the id: " + id);
		return mAchievements.get(id);
	}

	public void registerAchievement(Achievement achievement) {
		Validate.notNull(achievement);

		if (achievement instanceof ProgressAchievement) {
			if (((ProgressAchievement) achievement).inheritFrom() != null
					&& ((ProgressAchievement) achievement).getMaxProgress() != 0) {
				Validate.isTrue(mAchievements.containsKey(((ProgressAchievement) achievement).inheritFrom()));
				Validate.isTrue(mAchievements
						.get(((ProgressAchievement) achievement).inheritFrom()) instanceof ProgressAchievement);
			}
		}

		mAchievements.put(achievement.getID(), achievement);

		if (achievement instanceof Listener)
			Bukkit.getPluginManager().registerEvents((Listener) achievement, MobHunting.getInstance());
	}

	public void registerAchievements() {
		registerAchievement(new AxeMurderer());
		registerAchievement(new CreeperBoxing());
		registerAchievement(new Electrifying());
		registerAchievement(new RecordHungry());
		registerAchievement(new InFighting());
		registerAchievement(new ByTheBook());
		registerAchievement(new Creepercide());
		registerAchievement(new TheHuntBegins());
		registerAchievement(new ItsMagic());
		registerAchievement(new FancyPants());
		registerAchievement(new MasterSniper());
		registerAchievement(new JustInTime());
		registerAchievement(new WolfKillAchievement());

		for (MinecraftMob type : MinecraftMob.values()) {
			registerAchievement(new BasicHuntAchievement(type));
			registerAchievement(new SecondHuntAchievement(type));
			registerAchievement(new ThirdHuntAchievement(type));
			registerAchievement(new FourthHuntAchievement(type));
			registerAchievement(new FifthHuntAchievement(type));
			registerAchievement(new SixthHuntAchievement(type));
			registerAchievement(new SeventhHuntAchievement(type));
		}
	}

	public boolean hasAchievement(String achievement, Player player) {
		return hasAchievement(getAchievement(achievement), player);
	}

	public boolean hasAchievement(Achievement achievement, Player player) {
		PlayerStorage storage = mStorage.get(player);
		if (storage == null)
			return false;

		return storage.gainedAchievements.contains(achievement.getID());
	}

	public boolean achievementsEnabledFor(Player player) {
		PlayerStorage storage = mStorage.get(player);
		if (storage == null)
			return false;

		return storage.enableAchievements;
	}

	public int getProgress(String achievement, Player player) {
		Achievement a = getAchievement(achievement);
		Validate.isTrue(a instanceof ProgressAchievement, "This achievement does not have progress");

		return getProgress((ProgressAchievement) a, player);
	}

	public int getProgress(ProgressAchievement achievement, Player player) {
		PlayerStorage storage = mStorage.get(player);
		if (storage == null)
			return 0;

		Integer progress = storage.progressAchievements.get(achievement.getID());

		if (progress == null)
			return (storage.gainedAchievements.contains(achievement.getID()) ? achievement.getMaxProgress() : 0);
		return progress;
	}

	public void requestCompletedAchievements(OfflinePlayer player,
			final IDataCallback<List<Map.Entry<Achievement, Integer>>> callback) {
		if (player.isOnline()) {
			List<Map.Entry<Achievement, Integer>> achievements = new ArrayList<Map.Entry<Achievement, Integer>>();
			ArrayList<Map.Entry<Achievement, Integer>> toRemove = new ArrayList<Map.Entry<Achievement, Integer>>();

			for (Achievement achievement : mAchievements.values()) {
				if (hasAchievement(achievement, player.getPlayer())) {
					achievements.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(achievement, -1));

					// If the achievement is a higher level, remove the lower
					// level from the list
					if (achievement instanceof ProgressAchievement
							&& ((ProgressAchievement) achievement).inheritFrom() != null)
						toRemove.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(
								getAchievement(((ProgressAchievement) achievement).inheritFrom()), -1));
				} else if (achievement instanceof ProgressAchievement
						&& getProgress((ProgressAchievement) achievement, player.getPlayer()) > 0)
					achievements.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(achievement,
							getProgress((ProgressAchievement) achievement, player.getPlayer())));
			}

			achievements.removeAll(toRemove);

			callback.onCompleted(achievements);
			return;
		}

		// Look through the data store for offline players
		MobHunting.getDataStoreManager().requestAllAchievements(player, new IDataCallback<Set<AchievementStore>>() {
			@Override
			public void onError(Throwable error) {
				callback.onError(error);
			}

			@Override
			public void onCompleted(Set<AchievementStore> data) {
				List<Map.Entry<Achievement, Integer>> achievements = new ArrayList<Map.Entry<Achievement, Integer>>();
				ArrayList<Map.Entry<Achievement, Integer>> toRemove = new ArrayList<Map.Entry<Achievement, Integer>>();

				for (AchievementStore stored : data) {
					if (mAchievements.containsKey(stored.id)) {
						Achievement achievement = mAchievements.get(stored.id);
						achievements.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(achievement,
								stored.progress));

						// If the achievement is a higher level, remove
						// the lower level from the list
						if (stored.progress == -1 && achievement instanceof ProgressAchievement
								&& ((ProgressAchievement) achievement).inheritFrom() != null)
							toRemove.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(
									getAchievement(((ProgressAchievement) achievement).inheritFrom()), -1));
					}
				}

				achievements.removeAll(toRemove);

				callback.onCompleted(achievements);
			}
		});
	}

	/**
	 * Get a Collection of all Achievements
	 * 
	 * @return a Collection of achievements.
	 */
	public Collection<Achievement> getAllAchievements() {
		return Collections.unmodifiableCollection(mAchievements.values());
	}

	/**
	 * List all Achievements done by the player / command sender
	 * 
	 * @param sender
	 */
	public void listAllAchievements(CommandSender sender) {
		Iterator<Achievement> itr = Collections.unmodifiableCollection(mAchievements.values()).iterator();
		while (itr.hasNext()) {
			Achievement a = itr.next();
			sender.sendMessage(a.getID() + "---" + a.getName() + "---" + a.getDescription());
		}
	}

	/**
	 * Award the player when he make an Achievement
	 * 
	 * @param achievement
	 * @param player
	 */
	public void awardAchievement(String achievement, Player player) {
		awardAchievement(getAchievement(achievement), player);
	}

	/**
	 * Award the player if/when he make an Achievement
	 * 
	 * @param achievement
	 * @param player
	 */
	public void awardAchievement(Achievement achievement, Player player) {
		if (!achievementsEnabledFor(player)) {
			Messages.debug("[AchievementBlocked] Achievements is disabled for player %s", player.getName());
			return;
		}

		if (hasAchievement(achievement, player)) {
			return;
		}
		
		for (String world : MobHunting.getConfigManager().disableAchievementsInWorlds)
			if (world.equalsIgnoreCase(player.getWorld().getName())) {
				Messages.debug("[AchievementBlocked] Achievements is disabled in this world");
				return;
			}

		PlayerStorage storage = mStorage.get(player);
		if (storage == null)
			return;

		//MobHunting.getInstance();
		MobHunting.getDataStoreManager().recordAchievement(player, achievement);

		storage.gainedAchievements.add(achievement.getID());
		player.sendMessage(ChatColor.GOLD + Messages.getString("mobhunting.achievement.awarded", "name",
				"" + ChatColor.WHITE + ChatColor.ITALIC + achievement.getName()));
		player.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC + achievement.getDescription());
		player.sendMessage(
				ChatColor.WHITE + "" + ChatColor.ITALIC + Messages.getString("mobhunting.achievement.awarded.prize",
						"prize", MobHunting.getRewardManager().format(achievement.getPrize())));

		MobHunting.getRewardManager().depositPlayer(player, achievement.getPrize());

		if (MobHunting.getConfigManager().broadcastAchievement
				&& (!(achievement instanceof TheHuntBegins) || MobHunting.getConfigManager().broadcastFirstAchievement))
			Messages.broadcast(
					ChatColor.GOLD + Messages.getString("mobhunting.achievement.awarded.broadcast", "player",
							player.getName(), "name", "" + ChatColor.WHITE + ChatColor.ITALIC + achievement.getName()),
					player);

		// Run console commands as a reward
		String playername = player.getName();
		String worldname = player.getWorld().getName();
		String playerpos = player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + " "
				+ player.getLocation().getBlockZ();
		String prizeCommand = achievement.getPrizeCmd().replaceAll("\\{player\\}", playername)
				.replaceAll("\\{world\\}", worldname).replaceAll("\\{killerpos\\}", playerpos);
		if (!achievement.getPrizeCmd().equals("")) {
			String str = prizeCommand;
			do {
				if (str.contains("|")) {
					int n = str.indexOf("|");
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), str.substring(0, n));
					str = str.substring(n + 1, str.length()).toString();
				}
			} while (str.contains("|"));
			Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), str);
		}
		if (!achievement.getPrizeCmdDescription().equals("")) {
			player.sendMessage(ChatColor.WHITE + "" + ChatColor.ITALIC + achievement.getPrizeCmdDescription()
					.replaceAll("\\{player\\}", playername).replaceAll("\\{world\\}", worldname));
		}

		if (Misc.isMC19OrNewer())
			player.getWorld().playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1.0f, 1.0f);
		else
			player.getWorld().playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1.0f, 1.0f);

		FireworkEffect effect = FireworkEffect.builder().withColor(Color.ORANGE, Color.YELLOW).flicker(true)
				.trail(false).build();
		Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.setPower(1);
		meta.addEffect(effect);
		firework.setFireworkMeta(meta);
	}

	public void awardAchievementProgress(String achievement, Player player, int amount) {
		Achievement a = getAchievement(achievement);
		Validate.isTrue(a instanceof ProgressAchievement,
				"You need to award normal achievements with awardAchievement()");

		awardAchievementProgress((ProgressAchievement) a, player, amount);
	}

	public void awardAchievementProgress(ProgressAchievement achievement, Player player, int amount) {
		if (!achievementsEnabledFor(player) || hasAchievement(achievement, player))
			return;

		if (achievement.getExtendedMobType().getMax()==0) {
			Messages.debug(
					"[AchievementBlocked] ProgressAchievement for killing a %s is disabled (%s_level1 is 0 in config.yml)",
					achievement.getExtendedMobType().getExtendedMobType().toLowerCase(),
					achievement.getExtendedMobType().getExtendedMobType().toLowerCase());
			return;
		}


		Validate.isTrue(amount > 0);
		PlayerStorage storage = mStorage.get(player);
		if (storage == null)
			return;

		int curProgress = getProgress(achievement, player);

		while (achievement.inheritFrom() != null && curProgress == 0) {
			// This allows us to just mark progress against the highest level
			// version and have it automatically given to the lower level ones
			if (!hasAchievement(achievement.inheritFrom(), player)) {
				achievement = (ProgressAchievement) getAchievement(achievement.inheritFrom());
				curProgress = getProgress(achievement, player);
			} else {
				curProgress = ((ProgressAchievement) getAchievement(achievement.inheritFrom())).getMaxProgress();
			}
		}

		int maxProgress = achievement.getMaxProgress();
		int nextProgress = Math.min(maxProgress, curProgress + amount);

		if (nextProgress == maxProgress && maxProgress != 0)
			awardAchievement(achievement, player);
		else {
			storage.progressAchievements.put(achievement.getID(), nextProgress);

			MobHunting.getInstance();
			MobHunting.getDataStoreManager().recordAchievementProgress(player, achievement, nextProgress);

			int segment = Math.min(25, maxProgress / 2);

			if (curProgress / segment < nextProgress / segment || curProgress == 0 && nextProgress > 0) {
				player.sendMessage(ChatColor.BLUE + Messages.getString("mobhunting.achievement.progress", "name",
						"" + ChatColor.WHITE + ChatColor.ITALIC + achievement.getName()));
				player.sendMessage(ChatColor.GRAY + "" + nextProgress + " / " + maxProgress);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public boolean upgradeAchievements() {
		File file = new File(MobHunting.getInstance().getDataFolder(), "awards.yml");

		if (!file.exists())
			return false;

		MobHunting.getInstance().getLogger().info("Upgrading old awards.yml file");

		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);

			for (String player : config.getKeys(false)) {
				if (config.isList(player)) {
					for (Object obj : (List<Object>) config.getList(player)) {
						if (obj instanceof String) {
							MobHunting.getInstance();
							MobHunting.getDataStoreManager().recordAchievement(Bukkit.getOfflinePlayer(player),
									getAchievement((String) obj));
						} else if (obj instanceof Map) {
							Map<String, Integer> map = (Map<String, Integer>) obj;
							String id = map.keySet().iterator().next();
							MobHunting.getInstance();
							MobHunting.getDataStoreManager().recordAchievementProgress(Bukkit.getOfflinePlayer(player),
									(ProgressAchievement) getAchievement(id), (Integer) map.get(id));
						}
					}
				}
			}

			Files.delete(file.toPath());

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void load(final Player player) {
		final PlayerStorage storage = new PlayerStorage();
		storage.enableAchievements = false;

		mStorage.put(player, storage);

		if (!player.hasPermission("mobhunting.achievements.disabled") || player.hasPermission("*")) {

			MobHunting.getDataStoreManager().requestAllAchievements(player, new IDataCallback<Set<AchievementStore>>() {
				@Override
				public void onError(Throwable error) {
					if (error instanceof UserNotFoundException)
						storage.enableAchievements = true;
					else {
						error.printStackTrace();
						player.sendMessage(Messages.getString("achievements.load-fail"));
						storage.enableAchievements = false;
					}
				}

				@Override
				public void onCompleted(Set<AchievementStore> data) {
					for (AchievementStore achievement : data) {
						if (achievement.progress == -1)
							storage.gainedAchievements.add(achievement.id);
						else
							storage.progressAchievements.put(achievement.id, achievement.progress);
					}

					// Fix achievement errors where an upper level
					// progress
					// achievement is in progress/complete, but a lower
					// level one is not
					HashSet<String> toRemove = new HashSet<String>();
					for (Entry<String, Integer> prog : storage.progressAchievements.entrySet()) {
						Achievement raw = getAchievement(prog.getKey());
						if (raw instanceof ProgressAchievement) {
							ProgressAchievement achievement = (ProgressAchievement) raw;
							while (achievement.inheritFrom() != null) {
								String parent = achievement.inheritFrom();

								if (storage.progressAchievements.containsKey(parent))
									toRemove.add(parent);
								achievement = (ProgressAchievement) getAchievement(parent);
							}
						}
					}

					storage.gainedAchievements.addAll(toRemove);
					for (String id : toRemove) {
						storage.progressAchievements.remove(id);
						MobHunting.getInstance();
						MobHunting.getDataStoreManager().recordAchievement(player, getAchievement(id));
					}

					storage.enableAchievements = true;
				}
			});
		} else {
			Messages.debug("achievements is disabled with permission 'mobhunting.achievements.disabled' for player %s",
					player.getName());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onPlayerJoin(PlayerLoginEvent event) {
		load(event.getPlayer());
	}

	// *************************************************************************************
	// ACHIEVEMENTS GUI
	// *************************************************************************************
	// Inventory inventory, inventory2;
	HashMap<CommandSender, Inventory> inventoryMap = new HashMap<CommandSender, Inventory>();
	HashMap<CommandSender, Inventory> inventoryMap2 = new HashMap<CommandSender, Inventory>();

	public void showAllAchievements(final CommandSender sender, final OfflinePlayer otherPlayer, final boolean gui,
			final boolean self) {

		final Inventory inventory = Bukkit.createInventory(null, 54,
				ChatColor.BLUE + "" + ChatColor.BOLD + "Achievements:" + otherPlayer.getName());
		final Inventory inventory2 = Bukkit.createInventory(null, 54,
				ChatColor.BLUE + "" + ChatColor.BOLD + "Not started");

		requestCompletedAchievements(otherPlayer, new IDataCallback<List<Entry<Achievement, Integer>>>() {

			@Override
			public void onError(Throwable error) {
				if (error instanceof UserNotFoundException) {
					sender.sendMessage(ChatColor.GRAY + Messages.getString(
							"mobhunting.commands.listachievements.player-empty", "player", otherPlayer.getName()));
				} else {
					sender.sendMessage(ChatColor.RED + "An internal error occured while getting the achievements");
					error.printStackTrace();
				}
			}

			@Override
			public void onCompleted(List<Entry<Achievement, Integer>> data) {

				int outOf = 0;

				for (Achievement achievement : getAllAchievements()) {
					if (achievement instanceof ProgressAchievement
							&& ((ProgressAchievement) achievement).getMaxProgress() != 0) {
						if (((ProgressAchievement) achievement).inheritFrom() == null)
							++outOf;
					} else
						++outOf;
				}

				int count = 0;
				for (Map.Entry<Achievement, Integer> achievement : data) {
					if (achievement.getValue() == -1)
						++count;
				}

				// Build the output
				ArrayList<String> lines = new ArrayList<String>();

				if (!gui) {
					if (self)
						lines.add(ChatColor.GRAY
								+ Messages.getString("mobhunting.commands.listachievements.completed.self", "num",
										ChatColor.YELLOW + "" + count + ChatColor.GRAY, "max",
										ChatColor.YELLOW + "" + outOf + ChatColor.GRAY));
					else
						lines.add(ChatColor.GRAY
								+ Messages.getString("mobhunting.commands.listachievements.completed.other", "player",
										otherPlayer.getName(), "num", ChatColor.YELLOW + "" + count + ChatColor.GRAY,
										"max", ChatColor.YELLOW + "" + outOf + ChatColor.GRAY));
				}

				boolean inProgress = false;
				int n = 0;
				for (Map.Entry<Achievement, Integer> achievement : data) {
					if (achievement.getValue() == -1 && (achievement.getKey().getPrize() > 0
							|| MobHunting.getConfigManager().showAchievementsWithoutAReward)) {
						if (!gui) {
							lines.add(ChatColor.YELLOW + " " + achievement.getKey().getName());
							lines.add(
									ChatColor.GRAY + "    " + ChatColor.ITALIC + achievement.getKey().getDescription());
						} else if (sender instanceof Player)
							if (self)
								addInventoryDetails(achievement.getKey().getSymbol(), inventory, n,
										ChatColor.YELLOW + achievement.getKey().getName(),
										new String[] { ChatColor.GRAY + "" + ChatColor.ITALIC,
												achievement.getKey().getDescription(), "",
												Messages.getString(
														"mobhunting.commands.listachievements.completed.self", "num",
														ChatColor.YELLOW + "" + count + ChatColor.GRAY, "max",
														ChatColor.YELLOW + "" + outOf + ChatColor.GRAY) });
							else
								addInventoryDetails(achievement.getKey().getSymbol(), inventory, n,
										ChatColor.YELLOW + achievement.getKey().getName(),
										new String[] { ChatColor.GRAY + "" + ChatColor.ITALIC,
												achievement.getKey().getDescription(), "",
												Messages.getString(
														"mobhunting.commands.listachievements.completed.other",
														"player", otherPlayer.getName(), "num",
														ChatColor.YELLOW + "" + count + ChatColor.GRAY, "max",
														ChatColor.YELLOW + "" + outOf + ChatColor.GRAY) });
						if (n < 52)
							n++;
					} else
						inProgress = true;
				}
				if (inProgress) {
					if (!gui) {
						lines.add("");
						lines.add(
								ChatColor.YELLOW + Messages.getString("mobhunting.commands.listachievements.progress"));
					}

					for (Map.Entry<Achievement, Integer> achievement : data) {
						if (achievement.getValue() != -1 && achievement.getKey() instanceof ProgressAchievement
								&& (achievement.getKey().getPrize() > 0
										|| MobHunting.getConfigManager().showAchievementsWithoutAReward)
								&& ((ProgressAchievement) achievement.getKey()).getMaxProgress() != 0
								&& ((ProgressAchievement) achievement.getKey()).getExtendedMobType().getMax() != 0) {
							if (!gui)
								lines.add(ChatColor.GRAY + " " + achievement.getKey().getName() + ChatColor.WHITE + "  "
										+ achievement.getValue() + " / "
										+ ((ProgressAchievement) achievement.getKey()).getMaxProgress());
							else if (sender instanceof Player)
								addInventoryDetails(achievement.getKey().getSymbol(), inventory, n,
										ChatColor.YELLOW + achievement.getKey().getName(),
										new String[] { ChatColor.GRAY + "" + ChatColor.ITALIC,
												achievement.getKey().getDescription(), "",
												Messages.getString("mobhunting.commands.listachievements.progress")
														+ " " + ChatColor.WHITE + achievement.getValue() + " / "
														+ ((ProgressAchievement) achievement.getKey())
																.getMaxProgress() });
							if (n < 52)
								n++;
						} else
							inProgress = true;
					}
				}
				// Achievements NOT started.
				int m = 0;
				// Normal Achievement
				if (sender instanceof Player) {
					for (Achievement achievement : getAllAchievements()) {
						if (!(achievement instanceof ProgressAchievement)) {
							if (!isOnGoingOrCompleted(achievement, data)) {
								if (achievement.getPrize() > 0
										|| MobHunting.getConfigManager().showAchievementsWithoutAReward) {
									addInventoryDetails(achievement.getSymbol(), inventory2, m,
											ChatColor.YELLOW + achievement.getName(),
											new String[] { ChatColor.GRAY + "" + ChatColor.ITALIC,
													achievement.getDescription(), "", Messages.getString(
															"mobhunting.commands.listachievements.notstarted") });
									if (m < 52)
										m++;
									else
										Messages.debug("No room for achievement: %s", achievement.getName());
								}
							}
						}
					}
					// ProgressAchivement
					for (Achievement achievement : getAllAchievements()) {
						if ((achievement instanceof ProgressAchievement
								&& (achievement.getPrize() > 0
										|| MobHunting.getConfigManager().showAchievementsWithoutAReward)
								&& ((ProgressAchievement) achievement).getMaxProgress() != 0)) {
							boolean ongoing = isOnGoingOrCompleted(achievement, data);
							if (!ongoing) {
								boolean nextLevelBegun = isNextLevelBegun((ProgressAchievement) achievement, data);
								boolean previousLevelCompleted = isPreviousLevelCompleted3(
										(ProgressAchievement) achievement, data);
								if (!nextLevelBegun && previousLevelCompleted) {
									addInventoryDetails(achievement.getSymbol(), inventory2, m,
											ChatColor.YELLOW + achievement.getName(),
											new String[] { ChatColor.GRAY + "" + ChatColor.ITALIC,
													achievement.getDescription(), "", Messages.getString(
															"mobhunting.commands.listachievements.notstarted") });
									if (m < 52)
										m++;
									else
										Messages.debug("No room for achievement: %s", achievement.getName());
								}
							}
						}
					}
				}
				if (!gui)
					sender.sendMessage(lines.toArray(new String[lines.size()]));
				else if (sender instanceof Player) {
					inventoryMap.put(sender, inventory);
					inventoryMap2.put(sender, inventory2);
					((Player) sender).openInventory(inventoryMap.get(sender));
				}
			}

		});

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		final Inventory inv = event.getInventory();
		final Player player = (Player) event.getWhoClicked();
		if (inv != null && (ChatColor.stripColor(inv.getName()).startsWith("Achievements:"))) {
			event.setCancelled(true);
			Bukkit.getScheduler().runTask(MobHunting.getInstance(), new Runnable() {
				public void run() {
					player.closeInventory();
					inventoryMap.remove(player);
					player.openInventory(inventoryMap2.get(player));
				}
			});

		}
		if (inv != null && (ChatColor.stripColor(inv.getName()).startsWith("Not started"))) {
			event.setCancelled(true);
			Bukkit.getScheduler().runTask(MobHunting.getInstance(), new Runnable() {
				public void run() {
					player.closeInventory();
					inventoryMap2.remove(player);
				}
			});
		}
	}

	private boolean isNextLevelBegun(ProgressAchievement achievement, List<Entry<Achievement, Integer>> data) {
		if (achievement.nextLevelId() != null) {
			if (isOnGoingOrCompleted(achievement, data))
				return true;
			else
				return isNextLevelBegun((ProgressAchievement) getAchievement(achievement.nextLevelId()), data);
		} else
			return false;
	}

	private boolean isPreviousLevelCompleted3(ProgressAchievement achievement, List<Entry<Achievement, Integer>> data) {
		if (achievement.inheritFrom() != null) {
			if (isCompleted((ProgressAchievement) getAchievement(achievement.inheritFrom()), data))
				return true;
			else
				return false;
		} else
			return true;
	}

	private boolean isOnGoingOrCompleted(Achievement achievement, List<Entry<Achievement, Integer>> data) {
		for (Map.Entry<Achievement, Integer> achievement2 : data) {
			if (achievement.getID().equalsIgnoreCase(achievement2.getKey().getID())) {
				return true;
			}
		}
		return false;
	}

	private boolean isCompleted(Achievement achievement, List<Entry<Achievement, Integer>> data) {
		for (Map.Entry<Achievement, Integer> achievement2 : data) {
			if (achievement.getID().equalsIgnoreCase(achievement2.getKey().getID())) {
				return achievement2.getValue() == -1;
			}
		}
		return false;
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
}
