package au.com.mineauz.MobHunting.achievements;

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

import net.milkbowl.vault.economy.EconomyResponse;

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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.storage.AchievementStore;
import au.com.mineauz.MobHunting.storage.DataCallback;
import au.com.mineauz.MobHunting.storage.UserNotFoundException;

public class AchievementManager implements Listener {
	private HashMap<String, Achievement> mAchievements = new HashMap<String, Achievement>();

	private WeakHashMap<Player, PlayerStorage> mStorage = new WeakHashMap<Player, PlayerStorage>();

	public void initialize() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}

	public Achievement getAchievement(String id) {
		if (!mAchievements.containsKey(id))
			throw new IllegalArgumentException(
					"There is no achievement by the id: " + id);
		return mAchievements.get(id);
	}

	public void registerAchievement(Achievement achievement) {
		Validate.notNull(achievement);

		if (achievement instanceof ProgressAchievement) {
			if (((ProgressAchievement) achievement).inheritFrom() != null) {
				Validate.isTrue(mAchievements
						.containsKey(((ProgressAchievement) achievement)
								.inheritFrom()));
				Validate.isTrue(mAchievements
						.get(((ProgressAchievement) achievement).inheritFrom()) instanceof ProgressAchievement);
			}
		}

		mAchievements.put(achievement.getID(), achievement);

		if (achievement instanceof Listener)
			Bukkit.getPluginManager().registerEvents((Listener) achievement,
					MobHunting.instance);
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
		Validate.isTrue(a instanceof ProgressAchievement,
				"This achievement does not have progress");

		return getProgress((ProgressAchievement) a, player);
	}

	public int getProgress(ProgressAchievement achievement, Player player) {
		PlayerStorage storage = mStorage.get(player);
		if (storage == null)
			return 0;

		Integer progress = storage.progressAchievements
				.get(achievement.getID());

		if (progress == null)
			return (storage.gainedAchievements.contains(achievement.getID()) ? achievement
					.getMaxProgress() : 0);
		return progress;
	}

	public void requestCompletedAchievements(OfflinePlayer player,
			final DataCallback<List<Map.Entry<Achievement, Integer>>> callback) {
		if (player.isOnline()) {
			List<Map.Entry<Achievement, Integer>> achievements = new ArrayList<Map.Entry<Achievement, Integer>>();
			ArrayList<Map.Entry<Achievement, Integer>> toRemove = new ArrayList<Map.Entry<Achievement, Integer>>();

			for (Achievement achievement : mAchievements.values()) {
				if (hasAchievement(achievement, player.getPlayer())) {
					achievements
							.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(
									achievement, -1));

					// If the achievement is a higher level, remove the lower
					// level from the list
					if (achievement instanceof ProgressAchievement
							&& ((ProgressAchievement) achievement)
									.inheritFrom() != null)
						toRemove.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(
								getAchievement(((ProgressAchievement) achievement)
										.inheritFrom()), -1));
				} else if (achievement instanceof ProgressAchievement
						&& getProgress((ProgressAchievement) achievement,
								player.getPlayer()) > 0)
					achievements
							.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(
									achievement, getProgress(
											(ProgressAchievement) achievement,
											player.getPlayer())));
			}

			achievements.removeAll(toRemove);

			callback.onCompleted(achievements);
			return;
		}

		// Look through the data store for offline players
		MobHunting.instance.getDataStore().requestAllAchievements(player,
				new DataCallback<Set<AchievementStore>>() {
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
								Achievement achievement = mAchievements
										.get(stored.id);
								achievements
										.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(
												achievement, stored.progress));

								// If the achievement is a higher level, remove
								// the lower level from the list
								if (stored.progress == -1
										&& achievement instanceof ProgressAchievement
										&& ((ProgressAchievement) achievement)
												.inheritFrom() != null)
									toRemove.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(
											getAchievement(((ProgressAchievement) achievement)
													.inheritFrom()), -1));
							}
						}

						achievements.removeAll(toRemove);

						callback.onCompleted(achievements);
					}
				});
	}

	public Collection<Achievement> getAllAchievements() {
		return Collections.unmodifiableCollection(mAchievements.values());
	}
	
	public void listAllAchievements(CommandSender sender) {
		Iterator<Achievement> itr = Collections.unmodifiableCollection(mAchievements.values()).iterator();
		while (itr.hasNext()){
			Achievement a = itr.next();
			sender.sendMessage(a.getID()+"---"+a.getName()+"---"+ a.getDescription());
		}
	}

	public void awardAchievement(String achievement, Player player) {
		awardAchievement(getAchievement(achievement), player);
	}

	private void broadcast(String message, Player except) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.equals(except))
				continue;

			player.sendMessage(message);
		}
	}

	public void awardAchievement(Achievement achievement, Player player) {
		if (!achievementsEnabledFor(player)
				|| hasAchievement(achievement, player))
			return;

		PlayerStorage storage = mStorage.get(player);
		if (storage == null)
			return;

		MobHunting.instance.getDataStore().recordAchievement(player,
				achievement);

		storage.gainedAchievements.add(achievement.getID());
		player.sendMessage(ChatColor.GOLD
				+ Messages.getString(
						"mobhunting.achievement.awarded",
						"name",
						"" + ChatColor.WHITE + ChatColor.ITALIC
								+ achievement.getName()));
		player.sendMessage(ChatColor.BLUE + "" + ChatColor.ITALIC
				+ achievement.getDescription());
		player.sendMessage(ChatColor.WHITE
				+ ""
				+ ChatColor.ITALIC
				+ Messages.getString("mobhunting.achievement.awarded.prize",
						"prize",
						MobHunting.getEconomy().format(achievement.getPrize())));

		EconomyResponse result = MobHunting.getEconomy().depositPlayer(player,
				achievement.getPrize());
		if (!result.transactionSuccess())
			player.sendMessage(ChatColor.RED + "Unable to add prize money: "
					+ result.errorMessage);

		if (MobHunting.config().broadcastAchievement
				&& (!(achievement instanceof TheHuntBegins) || MobHunting
						.config().broadcastFirstAchievement))
			broadcast(
					ChatColor.GOLD
							+ Messages.getString(
									"mobhunting.achievement.awarded.broadcast",
									"player", player.getName(), "name",
									"" + ChatColor.WHITE + ChatColor.ITALIC
											+ achievement.getName()), player);

		// Run console commands as a reward
		String playername = player.getName();
		String worldname = player.getWorld().getName();
		String playerpos = player.getLocation().getBlockX() + " "
				+ player.getLocation().getBlockY() + " "
				+ player.getLocation().getBlockZ();
		String prizeCommand = achievement.getPrizeCmd()
				.replaceAll("\\{player\\}", playername)
				.replaceAll("\\{world\\}", worldname)
				.replaceAll("\\{killerpos\\}", playerpos);
		if (!achievement.getPrizeCmd().equals("")) {
			String str = prizeCommand;
			do {
				if (str.contains("|")) {
					int n = str.indexOf("|");
					Bukkit.getServer().dispatchCommand(
							Bukkit.getServer().getConsoleSender(),
							str.substring(0, n));
					str = str.substring(n + 1, str.length()).toString();
				}
			} while (str.contains("|"));
			Bukkit.getServer().dispatchCommand(
					Bukkit.getServer().getConsoleSender(), str);
		}
		if (!achievement.getPrizeCmdDescription().equals("")) {
			player.sendMessage(ChatColor.WHITE
					+ ""
					+ ChatColor.ITALIC
					+ achievement.getPrizeCmdDescription()
							.replaceAll("\\{player\\}", playername)
							.replaceAll("\\{world\\}", worldname));
		}

		//TODO: find the best Fireworksound
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, 1.0f,
				1.0f);
		FireworkEffect effect = FireworkEffect.builder()
				.withColor(Color.ORANGE, Color.YELLOW).flicker(true)
				.trail(false).build();
		Firework firework = player.getWorld().spawn(player.getLocation(),
				Firework.class);
		FireworkMeta meta = firework.getFireworkMeta();
		meta.setPower(1);
		meta.addEffect(effect);
		firework.setFireworkMeta(meta);
	}

	public void awardAchievementProgress(String achievement, Player player,
			int amount) {
		Achievement a = getAchievement(achievement);
		Validate.isTrue(a instanceof ProgressAchievement,
				"You need to award normal achievements with awardAchievement()");

		awardAchievementProgress((ProgressAchievement) a, player, amount);
	}

	public void awardAchievementProgress(ProgressAchievement achievement,
			Player player, int amount) {
		if (!achievementsEnabledFor(player)
				|| hasAchievement(achievement, player))
			return;

		Validate.isTrue(amount > 0);
		PlayerStorage storage = mStorage.get(player);
		if (storage == null)
			return;

		int curProgress = getProgress(achievement, player);

		while (achievement.inheritFrom() != null && curProgress == 0) {
			// This allows us to just mark progress against the highest level
			// version and have it automatically given to the lower level ones
			if (!hasAchievement(achievement.inheritFrom(), player)) {
				achievement = (ProgressAchievement) getAchievement(achievement
						.inheritFrom());
				curProgress = getProgress(achievement, player);
			} else {
				curProgress = ((ProgressAchievement) getAchievement(achievement
						.inheritFrom())).getMaxProgress();
			}
		}

		int maxProgress = achievement.getMaxProgress();
		int nextProgress = Math.min(maxProgress, curProgress + amount);

		if (nextProgress == maxProgress)
			awardAchievement(achievement, player);
		else {
			storage.progressAchievements.put(achievement.getID(), nextProgress);

			MobHunting.instance.getDataStore().recordAchievementProgress(
					player, achievement, nextProgress);

			int segment = Math.min(25, maxProgress / 2);

			if (curProgress / segment < nextProgress / segment
					|| curProgress == 0 && nextProgress > 0) {
				player.sendMessage(ChatColor.BLUE
						+ Messages.getString("mobhunting.achievement.progress",
								"name", "" + ChatColor.WHITE + ChatColor.ITALIC
										+ achievement.getName()));
				player.sendMessage(ChatColor.GRAY + "" + nextProgress + " / "
						+ maxProgress);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public boolean upgradeAchievements() {
		File file = new File(MobHunting.instance.getDataFolder(), "awards.yml");

		if (!file.exists())
			return false;

		MobHunting.instance.getLogger().info("Upgrading old awards.yml file");

		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);

			for (String player : config.getKeys(false)) {
				if (config.isList(player)) {
					for (Object obj : (List<Object>) config.getList(player)) {
						if (obj instanceof String) {
							MobHunting.instance.getDataStore()
									.recordAchievement(
											Bukkit.getOfflinePlayer(player),
											getAchievement((String) obj));
						} else if (obj instanceof Map) {
							Map<String, Integer> map = (Map<String, Integer>) obj;
							String id = map.keySet().iterator().next();
							MobHunting.instance
									.getDataStore()
									.recordAchievementProgress(
											Bukkit.getOfflinePlayer(player),
											(ProgressAchievement) getAchievement(id),
											(Integer) map.get(id));
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

		if (!player.hasPermission("mobhunting.achievements.disabled")) {

			MobHunting.instance.getDataStore().requestAllAchievements(player,
					new DataCallback<Set<AchievementStore>>() {
						@Override
						public void onError(Throwable error) {
							if (error instanceof UserNotFoundException)
								storage.enableAchievements = true;
							else {
								error.printStackTrace();
								player.sendMessage(Messages
										.getString("achievements.load-fail"));
								storage.enableAchievements = false;
							}
						}

						@Override
						public void onCompleted(Set<AchievementStore> data) {
							for (AchievementStore achievement : data) {
								if (achievement.progress == -1)
									storage.gainedAchievements
											.add(achievement.id);
								else
									storage.progressAchievements.put(
											achievement.id,
											achievement.progress);
							}

							// Fix achievement errors where an upper level
							// progress
							// achievement is in progress/complete, but a lower
							// level one is not
							HashSet<String> toRemove = new HashSet<String>();
							for (Entry<String, Integer> prog : storage.progressAchievements
									.entrySet()) {
								Achievement raw = getAchievement(prog.getKey());
								if (raw instanceof ProgressAchievement) {
									ProgressAchievement achievement = (ProgressAchievement) raw;
									while (achievement.inheritFrom() != null) {
										String parent = achievement
												.inheritFrom();

										if (storage.progressAchievements
												.containsKey(parent))
											toRemove.add(parent);
										achievement = (ProgressAchievement) getAchievement(parent);
									}
								}
							}

							storage.gainedAchievements.addAll(toRemove);
							for (String id : toRemove) {
								storage.progressAchievements.remove(id);
								MobHunting.instance.getDataStore()
										.recordAchievement(player,
												getAchievement(id));
							}

							storage.enableAchievements = true;
						}
					});
		} else {
			MobHunting
					.debug("achievements is disabled with permission 'mobhunting.achievements.disabled' for player %s",
							player.getName());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onPlayerJoin(PlayerLoginEvent event) {
		load(event.getPlayer());

	}
}
