package au.com.mineauz.MobHunting;

import java.io.File;

import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppedEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mcstats.Metrics;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

import de.Keyle.MyPet.api.entity.MyPetEntity;
import au.com.mineauz.MobHunting.achievements.*;
import au.com.mineauz.MobHunting.commands.CheckGrindingCommand;
import au.com.mineauz.MobHunting.commands.ClearGrindingCommand;
import au.com.mineauz.MobHunting.commands.CommandDispatcher;
import au.com.mineauz.MobHunting.commands.LeaderboardCommand;
import au.com.mineauz.MobHunting.commands.ListAchievementsCommand;
import au.com.mineauz.MobHunting.commands.ReloadCommand;
import au.com.mineauz.MobHunting.commands.SelectCommand;
import au.com.mineauz.MobHunting.commands.TopCommand;
import au.com.mineauz.MobHunting.commands.WhitelistAreaCommand;
import au.com.mineauz.MobHunting.compatability.CitizensCompat;
import au.com.mineauz.MobHunting.compatability.CompatibilityManager;
import au.com.mineauz.MobHunting.compatability.MinigamesCompat;
import au.com.mineauz.MobHunting.compatability.MobArenaCompat;
import au.com.mineauz.MobHunting.compatability.MobArenaHelper;
import au.com.mineauz.MobHunting.compatability.MyPetCompat;
import au.com.mineauz.MobHunting.compatability.MythicMobsCompat;
import au.com.mineauz.MobHunting.compatability.PVPArenaCompat;
import au.com.mineauz.MobHunting.compatability.PVPArenaHelper;
import au.com.mineauz.MobHunting.compatability.WorldEditCompat;
import au.com.mineauz.MobHunting.compatability.WorldGuardCompat;
import au.com.mineauz.MobHunting.events.MobHuntEnableCheckEvent;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;
import au.com.mineauz.MobHunting.leaderboard.LeaderboardManager;
import au.com.mineauz.MobHunting.modifier.*;
import au.com.mineauz.MobHunting.storage.DataStore;
import au.com.mineauz.MobHunting.storage.DataStoreException;
import au.com.mineauz.MobHunting.storage.DataStoreManager;
import au.com.mineauz.MobHunting.storage.MySQLDataStore;
import au.com.mineauz.MobHunting.storage.SQLiteDataStore;
import au.com.mineauz.MobHunting.util.Misc;
import au.com.mineauz.MobHunting.util.Update;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class MobHunting extends JavaPlugin implements Listener {

	// Constants
	public final static String pluginName = "mobhunting";
	public final static String tablePrefix = "mh_";

	private Economy mEconomy;
	public static MobHunting instance;

	private WeakHashMap<LivingEntity, DamageInformation> mDamageHistory = new WeakHashMap<LivingEntity, DamageInformation>();
	private Config mConfig;

	private AchievementManager mAchievements = new AchievementManager();
	public static double cDampnerRange = 15;

	private Set<IModifier> mModifiers = new HashSet<IModifier>();

	private ArrayList<Area> mKnownGrindingSpots = new ArrayList<Area>();
	private HashMap<UUID, LinkedList<Area>> mWhitelistedAreas = new HashMap<UUID, LinkedList<Area>>();

	private ParticleManager mParticles = new ParticleManager();
	private Random mRand = new Random();

	private DataStore mStore;
	private DataStoreManager mStoreManager;

	private LeaderboardManager mLeaderboards;

	private boolean mInitialized = false;

	// Update object
	private Update updateCheck = null;

	@Override
	public void onLoad() {

	}

	// private boolean versionCheck() {
	// String version = Bukkit.getBukkitVersion();
	// if (version == null)
	// return true; // custom bukkit, whatever

	// String[] parts = version.split("\\-");
	// String[] verPart = parts[0].split("\\.");
	// int major = Integer.valueOf(verPart[0]);
	// int minor = Integer.valueOf(verPart[1]);
	// int revision = 0;
	// if(verPart.length == 3)
	// revision = Integer.valueOf(verPart[2]);

	// if(major >= 1 && minor >= 7 && revision >= 8)
	// return true;
	//
	// getLogger().severe("This version of MobHunting is for Bukkit 1.7.8 and up. Please update your bukkit.");
	// return false;
	// return true;
	// }

	@Override
	public void onEnable() {
		mInitialized = false;

		// if (!versionCheck()) {
		// instance = null;
		// getServer().getPluginManager().disablePlugin(this);
		// return;
		// }

		instance = this;

		// Move the old data folder
		// File oldData = new File(getDataFolder().getParentFile(),
		// "Mob Hunting");
		// if (oldData.exists()) {
		// try {
		// Files.move(oldData.toPath(), getDataFolder().toPath(),
		// StandardCopyOption.ATOMIC_MOVE);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }

		Messages.exportDefaultLanguages();

		mConfig = new Config(new File(getDataFolder(), "config.yml"));

		if (mConfig.loadConfig())
			mConfig.saveConfig();
		else
			throw new RuntimeException(Messages.getString(pluginName
					+ ".config.fail"));

		RegisteredServiceProvider<Economy> economyProvider = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (economyProvider == null) {
			instance = null;
			getLogger().severe(Messages.getString(pluginName + ".hook.econ"));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		mEconomy = economyProvider.getProvider();

		if (!loadWhitelist())
			throw new RuntimeException();

		if (mConfig.databaseType.equalsIgnoreCase("mysql"))
			mStore = new MySQLDataStore();
		else
			mStore = new SQLiteDataStore();

		try {
			mStore.initialize();
		} catch (DataStoreException e) {
			e.printStackTrace();

			try {
				mStore.shutdown();
			} catch (DataStoreException e1) {
				e1.printStackTrace();
			}
			setEnabled(false);
			return;
		}

		mStoreManager = new DataStoreManager(mStore);

		// Handle compatability stuff
		CompatibilityManager.register(WorldEditCompat.class, "WorldEdit");
		CompatibilityManager.register(MinigamesCompat.class, "Minigames");
		CompatibilityManager.register(MyPetCompat.class, "MyPet");
		CompatibilityManager.register(WorldGuardCompat.class, "WorldGuard");
		CompatibilityManager.register(MobArenaCompat.class, "MobArena");
		CompatibilityManager.register(PVPArenaCompat.class, "PVPArena");
		CompatibilityManager.register(MythicMobsCompat.class, "MythicMobs");
		CompatibilityManager.register(CitizensCompat.class, "Citizens");
		// CompatibilityManager.register(HeroesCompat.class, "Heroes");
		// CompatibilityManager.register(MobDungeonMainCompat.class,
		// "MobDungeon");
		// CompatibilityManager.register(WarCompat.class, "War");

		CommandDispatcher cmd = new CommandDispatcher("mobhunt",
				Messages.getString("mobhunting.command.base.description")
						+ getDescription().getVersion());
		getCommand("mobhunt").setExecutor(cmd);
		getCommand("mobhunt").setTabCompleter(cmd);

		cmd.registerCommand(new ReloadCommand());
		cmd.registerCommand(new ListAchievementsCommand());
		cmd.registerCommand(new CheckGrindingCommand());
		cmd.registerCommand(new TopCommand());
		cmd.registerCommand(new LeaderboardCommand());
		cmd.registerCommand(new ClearGrindingCommand());
		cmd.registerCommand(new WhitelistAreaCommand());

		if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
			cmd.registerCommand(new SelectCommand());
		}

		registerAchievements();
		registerModifiers();

		getServer().getPluginManager().registerEvents(this, this);

		if (mAchievements.upgradeAchievements())
			mStoreManager.waitForUpdates();

		for (Player player : Bukkit.getOnlinePlayers())
			mAchievements.load(player);

		mLeaderboards = new LeaderboardManager();
		mLeaderboards.initialize();

		mInitialized = true;

		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
			debug("Metrics started");
		} catch (IOException e) {
			debug("Failed to start Metrics!");
			// Failed to submit the stats :-(
		}

		// Check for updates asynchronously
		if (instance.mConfig.updateCheck) {
			checkUpdates();
			new BukkitRunnable() {
				int count = 0;

				@Override
				public void run() {
					if (count++ > 10) {
						instance.getLogger()
								.info("["
										+ pluginName
										+ "]No updates found. (No response from server after 10s)");
						this.cancel();
					} else {
						// Wait for the response
						if (updateCheck != null) {
							if (updateCheck.isSuccess()) {
								checkUpdatesNotify(null);
							} else {
								instance.getLogger().info(
										"[" + pluginName + "]No update.");
							}
							this.cancel();
						}
					}
				}
			}.runTaskTimer(instance, 0L, 20L); // Check status every second
		}

	}

	@Override
	public void onDisable() {
		if (!mInitialized)
			return;

		mLeaderboards.shutdown();

		mAchievements = new AchievementManager();
		mModifiers.clear();

		try {
			mStoreManager.shutdown();
			mStore.shutdown();
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
	}

	private void registerAchievements() {
		mAchievements.registerAchievement(new AxeMurderer());
		mAchievements.registerAchievement(new CreeperBoxing());
		mAchievements.registerAchievement(new Electrifying());
		mAchievements.registerAchievement(new RecordHungry());
		mAchievements.registerAchievement(new InFighting());
		mAchievements.registerAchievement(new ByTheBook());
		mAchievements.registerAchievement(new Creepercide());
		mAchievements.registerAchievement(new TheHuntBegins());
		mAchievements.registerAchievement(new ItsMagic());
		mAchievements.registerAchievement(new FancyPants());
		mAchievements.registerAchievement(new MasterSniper());
		mAchievements.registerAchievement(new WolfKillAchievement());

		for (ExtendedMobType type : ExtendedMobType.values()) {
			mAchievements.registerAchievement(new BasicHuntAchievement(type));
			mAchievements.registerAchievement(new SecondHuntAchievement(type));
			mAchievements.registerAchievement(new ThirdHuntAchievement(type));
			mAchievements.registerAchievement(new FourthHuntAchievement(type));
			mAchievements.registerAchievement(new FifthHuntAchievement(type));
			mAchievements.registerAchievement(new SixthHuntAchievement(type));
			mAchievements.registerAchievement(new SeventhHuntAchievement(type));
		}

		mAchievements.initialize();
	}

	private void registerModifiers() {
		mModifiers.add(new BrawlerBonus());
		mModifiers.add(new ProSniperBonus());
		mModifiers.add(new SniperBonus());
		mModifiers.add(new ReturnToSenderBonus());
		mModifiers.add(new ShoveBonus());
		mModifiers.add(new SneakyBonus());
		mModifiers.add(new FriendleFireBonus());
		mModifiers.add(new BonusMobBonus());
		mModifiers.add(new CriticalModifier());

		mModifiers.add(new FlyingPenalty());
		mModifiers.add(new GrindingPenalty());

		// Check if horses exist
		try {
			Class.forName("org.bukkit.entity.Horse");
			mModifiers.add(new MountedBonus());
		} catch (ClassNotFoundException e) {
		}
	}

	void registerKnownGrindingSpot(Area newArea) {
		for (Area area : mKnownGrindingSpots) {
			if (newArea.center.getWorld().equals(area.center.getWorld())) {
				double dist = newArea.center.distance(area.center);

				double remaining = dist;
				remaining -= area.range;
				remaining -= newArea.range;

				if (remaining < 0) {
					if (dist > area.range)
						area.range = dist;

					area.count += newArea.count;

					return;
				}
			}
		}

		mKnownGrindingSpots.add(newArea);
	}

	public Area getGrindingArea(Location location) {
		for (Area area : mKnownGrindingSpots) {
			if (area.center.getWorld().equals(location.getWorld())) {
				if (area.center.distance(location) < area.range)
					return area;
			}
		}

		return null;
	}

	public void clearGrindingArea(Location location) {
		Iterator<Area> it = mKnownGrindingSpots.iterator();
		while (it.hasNext()) {
			Area area = it.next();

			if (area.center.getWorld().equals(location.getWorld())) {
				if (area.center.distance(location) < area.range)
					it.remove();
			}
		}
	}

	public static Economy getEconomy() {
		return instance.mEconomy;
	}

	public static Config config() {
		return instance.mConfig;
	}

	public void registerModifier(IModifier modifier) {
		mModifiers.add(modifier);
	}

	public HuntData getHuntData(Player player) {
		HuntData data = null;
		if (!player.hasMetadata("MobHuntData")) {
			data = new HuntData();
			player.setMetadata("MobHuntData",
					new FixedMetadataValue(this, data));
		} else {
			if (!(player.getMetadata("MobHuntData").get(0).value() instanceof HuntData)) {
				player.getMetadata("MobHuntData").get(0).invalidate();
				player.setMetadata("MobHuntData", new FixedMetadataValue(this,
						new HuntData()));
			}

			data = (HuntData) player.getMetadata("MobHuntData").get(0).value();
		}

		return data;
	}

	public static boolean isHuntEnabled(Player player) {
		if (!player.hasMetadata("MH:enabled")) {
			debug("KillBlocked %s: Player doesnt have MH:enabled",
					player.getName());
			return false;
		}

		List<MetadataValue> values = player.getMetadata("MH:enabled");

		// Use the first value that matches the required type
		boolean enabled = false;
		for (MetadataValue value : values) {
			if (value.value() instanceof Boolean)
				enabled = value.asBoolean();
		}

		if (enabled && !player.hasPermission("mobhunting.enable")) {
			debug("KillBlocked %s: Player doesnt have permission",
					player.getName());
			return false;
		}

		if (!enabled) {
			debug("KillBlocked %s: MH:enabled is false", player.getName());
			return false;
		}

		MobHuntEnableCheckEvent event = new MobHuntEnableCheckEvent(player);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isEnabled())
			debug("KillBlocked %s: Plugin cancelled check", player.getName());
		return event.isEnabled();
	}

	public static boolean isHuntEnabledInWorld(World world) {
		for (String worldName : config().disabledInWorlds) {
			if (world.getName().equalsIgnoreCase(worldName))
				return false;
		}

		return true;
	}

	public static void setHuntEnabled(Player player, boolean enabled) {
		player.setMetadata("MH:enabled", new FixedMetadataValue(instance,
				enabled));
	}

	private boolean saveWhitelist() {
		YamlConfiguration whitelist = new YamlConfiguration();
		File file = new File(getDataFolder(), "whitelist.yml");

		for (Entry<UUID, LinkedList<Area>> entry : mWhitelistedAreas.entrySet()) {
			ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			for (Area area : entry.getValue()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("Center", Misc.toMap(area.center));
				map.put("Radius", area.range);
				list.add(map);
			}

			whitelist.set(entry.getKey().toString(), list);
		}

		try {
			whitelist.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private boolean loadWhitelist() {
		YamlConfiguration whitelist = new YamlConfiguration();
		File file = new File(getDataFolder(), "whitelist.yml");

		if (!file.exists())
			return true;

		try {
			whitelist.load(file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return false;
		}

		mWhitelistedAreas.clear();

		for (String worldId : whitelist.getKeys(false)) {
			UUID world = UUID.fromString(worldId);
			List<Map<String, Object>> list = (List<Map<String, Object>>) whitelist
					.getList(worldId);
			LinkedList<Area> areas = new LinkedList<Area>();

			if (list == null)
				continue;

			for (Map<String, Object> map : list) {
				Area area = new Area();
				area.center = Misc.fromMap((Map<String, Object>) map
						.get("Center"));
				area.range = (Double) map.get("Radius");
				areas.add(area);
			}

			mWhitelistedAreas.put(world, areas);
		}

		return true;
	}

	public static boolean isWhitelisted(Location location) {
		LinkedList<Area> areas = instance.mWhitelistedAreas.get(location
				.getWorld().getUID());

		if (areas == null)
			return false;

		for (Area area : areas) {
			if (area.center.distance(location) < area.range)
				return true;
		}

		return false;
	}

	public void whitelistArea(Area newArea) {
		LinkedList<Area> areas = mWhitelistedAreas.get(newArea.center
				.getWorld().getUID());

		if (areas == null) {
			areas = new LinkedList<Area>();
			mWhitelistedAreas.put(newArea.center.getWorld().getUID(), areas);
		}

		for (Area area : areas) {
			if (newArea.center.getWorld().equals(area.center.getWorld())) {
				double dist = newArea.center.distance(area.center);

				double remaining = dist;
				remaining -= area.range;
				remaining -= newArea.range;

				if (remaining < 0) {
					if (dist > area.range)
						area.range = dist;

					area.count += newArea.count;

					return;
				}
			}
		}

		areas.add(newArea);

		saveWhitelist();
	}

	public void unWhitelistArea(Location location) {
		LinkedList<Area> areas = mWhitelistedAreas.get(location.getWorld()
				.getUID());

		if (areas == null)
			return;

		Iterator<Area> it = areas.iterator();
		while (it.hasNext()) {
			Area area = it.next();

			if (area.center.getWorld().equals(location.getWorld())) {
				if (area.center.distance(location) < area.range)
					it.remove();
			}
		}

		if (areas.isEmpty())
			mWhitelistedAreas.remove(location.getWorld().getUID());

		saveWhitelist();
	}

	public static void debug(String text, Object... args) {
		if (instance.mConfig.killDebug)
			instance.getLogger().info("[Debug] " + String.format(text, args));
	}

	@EventHandler
	private void onWorldLoad(WorldLoadEvent event) {
		List<Area> areas = mWhitelistedAreas.get(event.getWorld().getUID());
		if (areas != null) {
			for (Area area : areas)
				area.center.setWorld(event.getWorld());
		}
	}

	@EventHandler
	private void onWorldUnLoad(WorldUnloadEvent event) {
		List<Area> areas = mWhitelistedAreas.get(event.getWorld().getUID());
		if (areas != null) {
			for (Area area : areas)
				area.center.setWorld(null);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerDeath(PlayerDeathEvent event) {
		if (!isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !isHuntEnabled(event.getEntity()))
			return;

		HuntData data = getHuntData(event.getEntity());
		if (data.getKillstreakLevel() != 0)
			event.getEntity()
					.sendMessage(
							ChatColor.RED
									+ ""
									+ ChatColor.ITALIC
									+ Messages
											.getString("mobhunting.killstreak.ended"));
		data.killStreak = 0;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (!isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !isHuntEnabled((Player) event.getEntity()))
			return;

		Player player = (Player) event.getEntity();
		HuntData data = getHuntData(player);
		if (data.getKillstreakLevel() != 0)
			player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC
					+ Messages.getString("mobhunting.killstreak.ended"));
		data.killStreak = 0;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onSkeletonShoot(ProjectileLaunchEvent event) {
		// TODO: can Skeleton use other weapons the Arrow?
		if (!(event.getEntity() instanceof Arrow)
				|| !(event.getEntity().getShooter() instanceof Skeleton)
				|| !isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;

		Skeleton shooter = (Skeleton) event.getEntity().getShooter();

		if (shooter.getTarget() instanceof Player
				&& isHuntEnabled((Player) shooter.getTarget())
				&& ((Player) shooter.getTarget()).getGameMode() != GameMode.CREATIVE) {
			DamageInformation info = null;
			info = mDamageHistory.get(shooter);

			if (info == null)
				info = new DamageInformation();

			info.time = System.currentTimeMillis();

			info.attacker = (Player) shooter.getTarget();

			info.attackerPosition = shooter.getTarget().getLocation().clone();
			mDamageHistory.put(shooter, info);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMobDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof LivingEntity)
				|| !isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;

		if (WorldGuardCompat.isWorldGuardSupported()) {
			if ((event.getDamager() instanceof Player)
					|| (MyPetCompat.isMyPetSupported() && event.getDamager() instanceof MyPetEntity)) {
				RegionManager regionManager = WorldGuardCompat
						.getWorldGuardPlugin().getRegionManager(
								event.getDamager().getWorld());
				ApplicableRegionSet set = regionManager
						.getApplicableRegions(event.getDamager().getLocation());
				if (set != null) {
					if (!set.allows(DefaultFlag.MOB_DAMAGE)) {
						debug("KillBlocked: %s is hiding in WG region with MOB_DAMAGE %s",
								event.getDamager().getName(),
								set.allows(DefaultFlag.MOB_DAMAGE));
						return;
					}
				}
			}
		}

		DamageInformation info = null;
		info = mDamageHistory.get(event.getEntity());
		if (info == null)
			info = new DamageInformation();

		info.time = System.currentTimeMillis();

		Player cause = null;
		ItemStack weapon = null;

		if (event.getDamager() instanceof Player)
			cause = (Player) event.getDamager();

		boolean projectile = false;
		if (event.getDamager() instanceof Projectile) {
			if (((Projectile) event.getDamager()).getShooter() instanceof Player)
				cause = (Player) ((Projectile) event.getDamager()).getShooter();

			if (event.getDamager() instanceof ThrownPotion)
				weapon = ((ThrownPotion) event.getDamager()).getItem();

			info.mele = false;
			projectile = true;
		} else
			info.mele = true;

		if (event.getDamager() instanceof Wolf
				&& ((Wolf) event.getDamager()).isTamed()
				&& ((Wolf) event.getDamager()).getOwner() instanceof Player) {
			cause = (Player) ((Wolf) event.getDamager()).getOwner();

			info.mele = false;
			info.wolfAssist = true;
		}

		if (weapon == null && cause != null)
			weapon = cause.getItemInHand();

		if (weapon != null)
			info.weapon = weapon;

		// Take note that a weapon has been used at all
		if (info.weapon != null
				&& (Misc.isSword(info.weapon) || Misc.isAxe(info.weapon)
						|| Misc.isPick(info.weapon) || projectile))
			info.usedWeapon = true;

		if (cause != null) {
			if (cause != info.attacker) {
				info.assister = info.attacker;
				info.lastAssistTime = info.lastAttackTime;
			}

			info.lastAttackTime = System.currentTimeMillis();

			info.attacker = cause;
			if (cause.isFlying() && !cause.isInsideVehicle())
				info.wasFlying = true;

			info.attackerPosition = cause.getLocation().clone();
			mDamageHistory.put((LivingEntity) event.getEntity(), info);
		}
	}

	@SuppressWarnings({ "deprecation", "unused" })
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMobDeath(EntityDeathEvent event) {
		LivingEntity killed = event.getEntity();
		Player killer = killed.getKiller();

		if (!isHuntEnabledInWorld(killed.getWorld())) {
			debug("KillBlocked %s(%d): Mobhunting disabled in world %s",
					killed.getType(), killed.getEntityId(), killed.getWorld()
							.getName());
			return;
		}

		if (WorldGuardCompat.isWorldGuardSupported()
				&& WorldGuardCompat.isEnabledInConfig()) {
			if (killer instanceof Player
					|| (MyPetCompat.isMyPetSupported() && killer instanceof MyPetEntity)) {
				RegionManager regionManager = WorldGuardCompat
						.getWorldGuardPlugin().getRegionManager(
								killer.getWorld());
				ApplicableRegionSet set = regionManager
						.getApplicableRegions(killer.getLocation());
				if (set.size() > 0) {
					debug("Found %s worldguard regions: flag is %s",
							set.size(), set.allows(DefaultFlag.MOB_DAMAGE));
					if (killer instanceof Player
							&& !set.allows(DefaultFlag.MOB_DAMAGE)) {
						debug("KillBlocked: %s is hiding in WG region with MOB_DAMAGE %s",
								killer.getName(),
								set.allows(DefaultFlag.MOB_DAMAGE));
						return;
					}
				}
			}
		}

		if (killed instanceof Player) {
			if (MobArenaCompat.isEnabledInConfig()
					&& MobArenaHelper.isPlayingMobArena((Player) killed)) {
				debug("KillBlocked: %s was killed while playing MobArena.",
						killed.getName());
				return;
			} else if (MobArenaCompat.isEnabledInConfig()
					&& PVPArenaHelper.isPlayingPVPArena((Player) killed)) {
				debug("KillBlocked: %s was killed while playing PvpArena.",
						killed.getName());
				return;
			} else if (killer instanceof Player && !mConfig.pvpAllowed) {
				debug("KillBlocked: PVP not allowed. %s killed %s.",
						killer.getName(), killed.getName());
				return;
			}
		}

		if (MythicMobsCompat.isMythicMobsSupported()) {
			if (killed.hasMetadata("MH:MythicMob"))
				if (killer instanceof Player)
					debug("%s killed a MythicMob", killer.getName());
		}

		if (CitizensCompat.isEnabledInConfig()
				&& CitizensCompat.isCitizensSupported()
				&& CitizensCompat.isNPC(killed)) {
			NPCRegistry registry = CitizensAPI.getNPCRegistry();
			NPC npc = registry.getNPC(killed);

			debug("A Citizens was killed - NPC is Sentry=%S ",
					npc.hasTrait(CitizensAPI.getTraitFactory().getTraitClass(
							"Sentry")));
		}

		if (killer instanceof Player) {
			if (MobArenaCompat.isEnabledInConfig()
					&& MobArenaHelper.isPlayingMobArena(killer)
					&& !mConfig.mobarenaGetRewards) {
				debug("KillBlocked: %s is currently playing MobArena.",
						killer.getName());
				return;
			} else if (PVPArenaCompat.isEnabledInConfig()
					&& PVPArenaHelper.isPlayingPVPArena(killer)
					&& !mConfig.pvparenaGetRewards) {
				debug("KillBlocked: %s is currently playing PvpArena.",
						killer.getName());
				return;
			}
		}

		if (mConfig.getBaseKillPrize(event.getEntity()) == 0
				&& mConfig.getKillConsoleCmd(killed).equals("")) {
			debug("KillBlocked %s(%d): There is no reward for this Mob/Player",
					killed.getType(), killed.getEntityId());
			return;
		}

		if (killed.hasMetadata("MH:blocked")) {
			debug("KillBlocked %s(%d): Mob has MH:blocked meta (probably spawned from a mob spawner)",
					killed.getType(), killed.getEntityId());
			return;
		}

		if (killer == null || killer.getGameMode() == GameMode.CREATIVE
				|| !isHuntEnabled(killer)) {
			if (killer != null && killer.getGameMode() == GameMode.CREATIVE)
				debug("KillBlocked %s: In creative mode", killer.getName());
			return;
		}

		DamageInformation info = null;
		if (killed instanceof LivingEntity
				&& mDamageHistory.containsKey((LivingEntity) killed)) {
			info = mDamageHistory.get(killed);

			if (System.currentTimeMillis() - info.time > mConfig.assistTimeout * 1000)
				info = null;
			else if (killer == null)
				killer = info.attacker;
		}

		EntityDamageByEntityEvent lastDamageCause = null;

		if (killed.getLastDamageCause() instanceof EntityDamageByEntityEvent)
			lastDamageCause = (EntityDamageByEntityEvent) killed
					.getLastDamageCause();

		if (info == null) {
			info = new DamageInformation();
			info.time = System.currentTimeMillis();
			info.lastAttackTime = info.time;
			info.attacker = killer;
			info.attackerPosition = killer.getLocation();
			info.usedWeapon = true;
		}

		if ((System.currentTimeMillis() - info.lastAttackTime) > mConfig.killTimeout * 1000) {
			debug("KillBlocked %s: Last damage was too long ago",
					killer.getName());
			return;
		}

		if (info.weapon == null)
			info.weapon = new ItemStack(Material.AIR);

		HuntData data = getHuntData(killer);

		Misc.handleKillstreak(killer);

		// Record kills that are still within a small area
		Location loc = killed.getLocation();

		Area detectedGrindingArea = getGrindingArea(loc);

		if (detectedGrindingArea == null)
			detectedGrindingArea = data.getGrindingArea(loc);

		// Slimes are except from grinding due to their splitting nature
		if (!(event.getEntity() instanceof Slime)
				&& mConfig.penaltyGrindingEnable
				&& !killed.hasMetadata("MH:reinforcement")
				&& !isWhitelisted(killed.getLocation())) {
			if (detectedGrindingArea != null) {
				data.lastKillAreaCenter = null;
				data.dampenedKills = detectedGrindingArea.count++;

				if (data.dampenedKills == 20)
					registerKnownGrindingSpot(detectedGrindingArea);
			} else {
				if (data.lastKillAreaCenter != null) {
					if (loc.getWorld().equals(
							data.lastKillAreaCenter.getWorld())) {
						if (loc.distance(data.lastKillAreaCenter) < cDampnerRange) {
							data.dampenedKills++;
							if (data.dampenedKills == 10)
								data.recordGrindingArea();
						} else {
							data.lastKillAreaCenter = loc.clone();
							data.dampenedKills = 0;
						}
					} else {
						data.lastKillAreaCenter = loc.clone();
						data.dampenedKills = 0;
					}
				} else {
					data.lastKillAreaCenter = loc.clone();
					data.dampenedKills = 0;
				}
			}

			if (data.dampenedKills > 14) {
				if (data.getKillstreakLevel() != 0)
					killer.sendMessage(ChatColor.RED
							+ Messages.getString("mobhunting.killstreak.lost"));
				data.killStreak = 0;
			}
		}

		double cash = mConfig.getBaseKillPrize(killed);
		debug("Mob Basic Prize=%s", cash);
		double multiplier = 1.0;

		// Apply the modifiers
		ArrayList<String> modifiers = new ArrayList<String>();
		for (IModifier mod : mModifiers) {
			if (mod.doesApply(killed, killer, data, info, lastDamageCause)) {
				double amt = mod.getMultiplier(killed, killer, data, info,
						lastDamageCause);

				if (amt != 1.0) {
					modifiers.add(mod.getName());
					multiplier *= amt;
				}
			}
		}

		multiplier *= data.getKillstreakMultiplier();

		String extraString = "";

		// Only display the multiplier if its not 1
		if (Math.abs(multiplier - 1) > 0.05)
			extraString += String.format("x%.1f", multiplier);

		// Add on modifiers
		for (String modifier : modifiers)
			extraString += ChatColor.WHITE + " * " + modifier;

		cash *= multiplier;

		if (cash >= 0.01) {
			MobHuntKillEvent event2 = new MobHuntKillEvent(data, info, killed,
					killer);
			Bukkit.getPluginManager().callEvent(event2);

			if (event2.isCancelled()) {
				debug("KillBlocked %s: MobHuntKillEvent was cancelled",
						killer.getName());
				return;
			}

			if (killed instanceof Player && killer instanceof Player) {
				if (!CitizensCompat.isNPC(killed)) {
					mEconomy.withdrawPlayer((Player) killed, cash);
					killed.sendMessage(ChatColor.GREEN
							+ ""
							+ ChatColor.ITALIC
							+ Messages.getString("mobhunting.moneylost",
									mEconomy.format(cash)));
					debug("%s lost %s", killed.getName(), mEconomy.format(cash));
				}
			}
			if (info.assister == null) {
				mEconomy.depositPlayer(killer, cash);
				debug("%s got a reward (%s)", killer.getName(),
						mEconomy.format(cash));
			} else {
				cash = cash / 2;
				mEconomy.depositPlayer(killer, cash);
				onAssist(info.assister, killer, killed, info.lastAssistTime);
				debug("%s got a ½ reward (%s)", killer.getName(),
						mEconomy.format(cash));
			}

			// TODO: record mythicmob kills
			if (ExtendedMobType.fromEntity(killed) != null)
				getDataStore().recordKill(killer,
						ExtendedMobType.fromEntity(killed),
						killed.hasMetadata("MH:hasBonus"));

			if (extraString.trim().isEmpty()) {
				killer.sendMessage(ChatColor.GREEN
						+ ""
						+ ChatColor.ITALIC
						+ Messages.getString("mobhunting.moneygain", "prize",
								mEconomy.format(cash)));
			} else
				killer.sendMessage(ChatColor.GREEN
						+ ""
						+ ChatColor.ITALIC
						+ Messages.getString("mobhunting.moneygain.bonuses",
								"prize", mEconomy.format(cash), "bonuses",
								extraString.trim()));
		} else
			debug("KillBlocked %s: Gained money was less than 1 cent (grinding or penalties) (%s)",
					killer.getName(), extraString);

		// Run console commands as a reward
		if (data.dampenedKills < 10) {
			if (!mConfig.getKillConsoleCmd(killed).equals("")) {
				if (mRand.nextInt(mConfig.getCmdRunProbabilityBase(killed)) < mConfig
						.getCmdRunProbability(killed)) {
					String worldname = killer.getWorld().getName();
					String prizeCommand = mConfig
							.getKillConsoleCmd(killed)
							.replaceAll("\\{player\\}", killer.getName())
							.replaceAll("\\{killed_player\\}", killed.getName())
							.replaceAll("\\{world\\}", worldname);
					if (!mConfig.getKillConsoleCmd(killed).equals("")) {
						String str = prizeCommand;
						do {
							if (str.contains("|")) {
								int n = str.indexOf("|");
								Bukkit.getServer().dispatchCommand(
										Bukkit.getServer().getConsoleSender(),
										str.substring(0, n));
								str = str.substring(n + 1, str.length())
										.toString();
							}
						} while (str.contains("|"));
						Bukkit.getServer().dispatchCommand(
								Bukkit.getServer().getConsoleSender(), str);
					}
					// send a message to the player
					if (!mConfig.getKillRewardDescription(killed).equals("")) {
						killer.sendMessage(ChatColor.GREEN
								+ ""
								+ ChatColor.ITALIC
								+ mConfig
										.getKillRewardDescription(killed)
										.replaceAll("\\{player\\}",
												killer.getName())
										.replaceAll("\\{killed_player\\}",
												killed.getName())
										.replaceAll("\\{world\\}", worldname));
					}
				}
			}
		}
	}

	private void onAssist(Player player, Player killer, LivingEntity killed,
			long time) {
		if (!mConfig.enableAssists
				|| (System.currentTimeMillis() - time) > mConfig.assistTimeout * 1000)
			return;

		double multiplier = mConfig.assistMultiplier;
		double ks = 1.0;
		if (mConfig.assistAllowKillstreak)
			ks = Misc.handleKillstreak(player);

		multiplier *= ks;
		double cash = 0;
		if (killed instanceof Player)
			cash = mConfig.getBaseKillPrize(killed) * multiplier / 2;
		else
			cash = mConfig.getBaseKillPrize(killed) * multiplier;

		if (cash >= 0.01) {
			getDataStore().recordAssist(player, killer,
					ExtendedMobType.fromEntity(killed),
					killed.hasMetadata("MH:hasBonus"));
			mEconomy.depositPlayer(player, cash);
			debug("%s got a on assist reward (%s)", player.getName(),
					mEconomy.format(cash));

			if (ks != 1.0)
				player.sendMessage(ChatColor.GREEN
						+ ""
						+ ChatColor.ITALIC
						+ Messages.getString("mobhunting.moneygain.assist",
								"prize", mEconomy.format(cash)));
			else
				player.sendMessage(ChatColor.GREEN
						+ ""
						+ ChatColor.ITALIC
						+ Messages.getString(
								"mobhunting.moneygain.assist.bonuses", "prize",
								mEconomy.format(cash), "bonuses",
								String.format("x%.1f", ks)));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		setHuntEnabled(event.getPlayer(), true);
	}

	public DamageInformation getDamageInformation(LivingEntity entity) {
		return mDamageHistory.get(entity);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void bonusMobSpawn(CreatureSpawnEvent event) {
		if (!isHuntEnabledInWorld(event.getLocation().getWorld())
				|| (mConfig.getBaseKillPrize(event.getEntity()) <= 0 && mConfig
						.getKillConsoleCmd(event.getEntity()).equals(""))
				|| event.getSpawnReason() != SpawnReason.NATURAL)
			return;

		if (event.getEntityType() == EntityType.ENDER_DRAGON)
			return;

		if (mRand.nextDouble() * 100 < mConfig.bonusMobChance) {
			mParticles
					.attachEffect(event.getEntity(), Effect.MOBSPAWNER_FLAMES);
			if (mRand.nextBoolean())
				event.getEntity().addPotionEffect(
						new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,
								Integer.MAX_VALUE, 3));
			else
				event.getEntity().addPotionEffect(
						new PotionEffect(PotionEffectType.SPEED,
								Integer.MAX_VALUE, 2));

			event.getEntity().setMetadata("MH:hasBonus",
					new FixedMetadataValue(this, true));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void spawnerMobSpawn(CreatureSpawnEvent event) {
		if (!isHuntEnabledInWorld(event.getLocation().getWorld())
				|| (mConfig.getBaseKillPrize(event.getEntity()) <= 0)
				&& mConfig.getKillConsoleCmd(event.getEntity()).equals(""))
			return;

		if (event.getSpawnReason() != SpawnReason.SPAWNER
				&& event.getSpawnReason() != SpawnReason.SPAWNER_EGG)
			return;

		if (!mConfig.allowMobSpawners)
			event.getEntity().setMetadata("MH:blocked",
					new FixedMetadataValue(this, true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void reinforcementMobSpawn(CreatureSpawnEvent event) {
		if (!isHuntEnabledInWorld(event.getLocation().getWorld())
				|| (mConfig.getBaseKillPrize(event.getEntity()) <= 0)
				&& mConfig.getKillConsoleCmd(event.getEntity()).equals(""))
			return;

		if (event.getSpawnReason() == SpawnReason.REINFORCEMENTS)
			event.getEntity().setMetadata("MH:reinforcement",
					new FixedMetadataValue(this, true));
	}

	public AchievementManager getAchievements() {
		return mAchievements;
	}

	public DataStoreManager getDataStore() {
		return mStoreManager;
	}

	public LeaderboardManager getLeaderboards() {
		return mLeaderboards;
	}

	// ***************************************************************************
	// UPDATECHECK - Check if there is a new version available at
	// https://api.curseforge.com/servermods/files?projectIds=63718
	// ***************************************************************************
	/**
	 * @return the updateCheck
	 */
	public Update getUpdateCheck() {
		return updateCheck;
	}

	/**
	 * Checks to see if there are any plugin updates Called when reloading
	 * settings too
	 */
	public void checkUpdates() {
		// Version checker
		getLogger().info("Checking for new updates...");
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				updateCheck = new Update(63718); // MobHunting
				if (!updateCheck.isSuccess()) {
					updateCheck = null;
				}
			}
		});
	}

	public void checkUpdatesNotify(Player p) {
		boolean update = false;
		final String pluginVersion = instance.getDescription().getVersion();
		// Check to see if the latest file is newer that this one
		String[] split = instance.getUpdateCheck().getVersionName().split(" V");
		// Only do this if the format is what we expect
		if (split.length == 2) {
			// Need to escape the period in the regex expression
			String[] updateVer = split[1].split("\\.");
			// CHeck the version #'s
			String[] pluginVer = pluginVersion.split("\\.");
			// Run through major, minor, sub
			for (int i = 0; i < Math.max(updateVer.length, pluginVer.length); i++) {
				try {
					int updateCheck = 0;
					if (i < updateVer.length) {
						updateCheck = Integer.valueOf(updateVer[i]);
					}
					int pluginCheck = 0;
					if (i < pluginVer.length) {
						pluginCheck = Integer.valueOf(pluginVer[i]);
					}
					// " plugin is " + pluginCheck);
					if (updateCheck < pluginCheck) {
						// getLogger().info("["+pluginName+"]DEBUG: plugin is newer!");
						// plugin is newer
						update = false;
						break;
					} else if (updateCheck > pluginCheck) {
						update = true;
						break;
					}
				} catch (Exception e) {
					getLogger().warning(
							"Could not determine update's version # ");
					getLogger().warning("Plugin version: " + pluginVersion);
					getLogger().warning(
							"Update version: "
									+ instance.getUpdateCheck()
											.getVersionName());
					return;
				}
			}
		}
		// Show the results
		if (p != null) {
			if (!update) {
				return;
			} else {
				// Player login
				p.sendMessage(ChatColor.GOLD
						+ instance.getUpdateCheck().getVersionName()
						+ " is available! You are running " + pluginVersion);
				p.sendMessage(ChatColor.RED
						+ "Update at: http://dev.bukkit.org/server-mods/mobhunting/");
			}
		} else {
			// Console
			if (!update) {
				getLogger().info("No updates available.");
				return;
			} else {
				getLogger().info(
						"Version " + instance.getUpdateCheck().getVersionName()
								+ " is available! You are running "
								+ pluginVersion);
				getLogger()
						.info("Update at: http://dev.bukkit.org/server-mods/mobhunting/");
			}
		}
	}

	// ************************************************************************************
	private Logger logger;

	@Plugin(id = "mobhuntingSponge", name = "MobHunting Project", version = "1.0")
	public class MobHuntingProject implements Listener {
		@Subscribe
		public void onServerStart(ServerStartedEvent event) {
			// Hey! The server has started!
			// Try instantiating your logger in here.
			// (There's a guide for that)
			logger.info("Hello World!");
		}

		@Subscribe
		public void onServerStop(ServerStoppedEvent event) {
			// Hey! The server has started!
			// Try instantiating your logger in here.
			// (There's a guide for that)
			logger.info("Goodbye World!");
		}
	}
	// ************************************************************************************

}
