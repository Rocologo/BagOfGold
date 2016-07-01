package one.lindegaard.MobHunting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import net.milkbowl.vault.economy.Economy;
import one.lindegaard.MobHunting.achievements.*;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.bounty.BountyManager;
import one.lindegaard.MobHunting.bounty.BountyStatus;
import one.lindegaard.MobHunting.commands.BountyCommand;
import one.lindegaard.MobHunting.commands.CheckGrindingCommand;
import one.lindegaard.MobHunting.commands.ClearGrindingCommand;
import one.lindegaard.MobHunting.commands.CommandDispatcher;
import one.lindegaard.MobHunting.commands.DatabaseCommand;
import one.lindegaard.MobHunting.commands.HeadCommand;
import one.lindegaard.MobHunting.commands.LeaderboardCommand;
import one.lindegaard.MobHunting.commands.LearnCommand;
import one.lindegaard.MobHunting.commands.AchievementsCommand;
import one.lindegaard.MobHunting.commands.MuteCommand;
import one.lindegaard.MobHunting.commands.NpcCommand;
import one.lindegaard.MobHunting.commands.RegionCommand;
import one.lindegaard.MobHunting.commands.ReloadCommand;
import one.lindegaard.MobHunting.commands.SelectCommand;
import one.lindegaard.MobHunting.commands.TopCommand;
import one.lindegaard.MobHunting.commands.UpdateCommand;
import one.lindegaard.MobHunting.commands.VersionCommand;
import one.lindegaard.MobHunting.commands.WhitelistAreaCommand;
import one.lindegaard.MobHunting.compatibility.ActionBarCompat;
import one.lindegaard.MobHunting.compatibility.BarAPICompat;
import one.lindegaard.MobHunting.compatibility.BattleArenaCompat;
import one.lindegaard.MobHunting.compatibility.BattleArenaHelper;
import one.lindegaard.MobHunting.compatibility.BossBarAPICompat;
import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.CompatibilityManager;
import one.lindegaard.MobHunting.compatibility.DisguiseCraftCompat;
import one.lindegaard.MobHunting.compatibility.DisguisesHelper;
import one.lindegaard.MobHunting.compatibility.EssentialsCompat;
import one.lindegaard.MobHunting.compatibility.IDisguiseCompat;
import one.lindegaard.MobHunting.compatibility.LibsDisguisesCompat;
import one.lindegaard.MobHunting.compatibility.MinigamesCompat;
import one.lindegaard.MobHunting.compatibility.MobArenaCompat;
import one.lindegaard.MobHunting.compatibility.MobArenaHelper;
import one.lindegaard.MobHunting.compatibility.MobStackerCompat;
import one.lindegaard.MobHunting.compatibility.MyPetCompat;
import one.lindegaard.MobHunting.compatibility.MythicMobsCompat;
import one.lindegaard.MobHunting.compatibility.PVPArenaCompat;
import one.lindegaard.MobHunting.compatibility.PVPArenaHelper;
import one.lindegaard.MobHunting.compatibility.TitleAPICompat;
import one.lindegaard.MobHunting.compatibility.TitleManagerCompat;
import one.lindegaard.MobHunting.compatibility.VanishNoPacketCompat;
import one.lindegaard.MobHunting.compatibility.WorldEditCompat;
import one.lindegaard.MobHunting.compatibility.WorldGuardCompat;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;
import one.lindegaard.MobHunting.leaderboard.LeaderboardManager;
import one.lindegaard.MobHunting.modifier.*;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.storage.DataStoreManager;
import one.lindegaard.MobHunting.storage.IDataStore;
import one.lindegaard.MobHunting.storage.MySQLDataStore;
import one.lindegaard.MobHunting.storage.SQLiteDataStore;
import one.lindegaard.MobHunting.update.UpdateHelper;
import one.lindegaard.MobHunting.util.Misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class MobHunting extends JavaPlugin implements Listener {

	// Constants
	private final static String pluginName = "mobhunting";
	// private String pluginVersion = "";

	private static Economy mEconomy;
	private static MobHunting instance;

	private static MobHuntingManager mMobHuntingManager;
	private static AreaManager mAreaManager;
	private static LeaderboardManager mLeaderboardManager;
	private static AchievementManager mAchievementManager;
	private static BountyManager mBountyManager;
	private static ParticleManager mParticleManager = new ParticleManager();
	private static MetricsManager mMetricsManager;
	private static PlayerSettingsManager mPlayerSettingsManager;
	private static WorldGroup mWorldGroupManager;

	public static IDataStore mStore;
	private static DataStoreManager mStoreManager;
	private static ConfigManager mConfig;

	private static WeakHashMap<LivingEntity, DamageInformation> mDamageHistory = new WeakHashMap<LivingEntity, DamageInformation>();

	private Set<IModifier> mModifiers = new HashSet<IModifier>();

	// public Random mRand = new Random();

	private boolean mInitialized = false;

	// TODO: Remove this when SQL Database connections is stable. (After V3.2.5)
	public static int openConnections = 0;

	@Override
	public void onLoad() {
	}

	private void setInstance(MobHunting m) {
		instance = m;
	}

	@Override
	public void onEnable() {
		setInstance(this);

		Messages.exportDefaultLanguages();

		mConfig = new ConfigManager(new File(getDataFolder(), "config.yml"));

		if (mConfig.loadConfig()) {
			if (mConfig.dropMoneyOnGroundTextColor.equals("&0"))
				mConfig.dropMoneyOnGroundTextColor = "WHITE";
			mConfig.saveConfig();
		} else
			throw new RuntimeException(Messages.getString(pluginName + ".config.fail"));

		mMobHuntingManager = new MobHuntingManager(this);

		mWorldGroupManager = new WorldGroup();
		mWorldGroupManager.load();

		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider == null) {
			instance = null;
			getLogger().severe(Messages.getString(pluginName + ".hook.econ"));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		mEconomy = economyProvider.getProvider();

		mAreaManager = new AreaManager(this);

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

		UpdateHelper.setCurrentJarFile(instance.getFile().getName());

		mStoreManager = new DataStoreManager(mStore);

		mPlayerSettingsManager = new PlayerSettingsManager();

		// Handle compatability stuff
		registerPlugin(EssentialsCompat.class, "Essentials");
		registerPlugin(WorldEditCompat.class, "WorldEdit");
		registerPlugin(WorldGuardCompat.class, "WorldGuard");
		registerPlugin(MythicMobsCompat.class, "MythicMobs");
		registerPlugin(CitizensCompat.class, "Citizens");
		registerPlugin(MinigamesCompat.class, "Minigames");
		registerPlugin(MyPetCompat.class, "MyPet");
		registerPlugin(MobArenaCompat.class, "MobArena");
		registerPlugin(PVPArenaCompat.class, "PVPArena");
		registerPlugin(LibsDisguisesCompat.class, "LibsDisguises");
		registerPlugin(DisguiseCraftCompat.class, "DisguiseCraft");
		registerPlugin(IDisguiseCompat.class, "iDisguise");
		registerPlugin(BattleArenaCompat.class, "BattleArena");
		registerPlugin(VanishNoPacketCompat.class, "VanishNoPacket");
		registerPlugin(BossBarAPICompat.class, "BossBarAPI");
		registerPlugin(TitleAPICompat.class, "TitleAPI");
		registerPlugin(BarAPICompat.class, "BarAPI");
		registerPlugin(TitleManagerCompat.class, "TitleManager");
		registerPlugin(ActionBarCompat.class, "ActionBar");
		registerPlugin(MobStackerCompat.class, "MobStacker");

		// register commands
		CommandDispatcher cmd = new CommandDispatcher("mobhunt",
				Messages.getString("mobhunting.command.base.description") + getDescription().getVersion());
		getCommand("mobhunt").setExecutor(cmd);
		getCommand("mobhunt").setTabCompleter(cmd);
		cmd.registerCommand(new AchievementsCommand());
		cmd.registerCommand(new CheckGrindingCommand());
		cmd.registerCommand(new ClearGrindingCommand());
		cmd.registerCommand(new DatabaseCommand());
		cmd.registerCommand(new HeadCommand());
		cmd.registerCommand(new LeaderboardCommand());
		cmd.registerCommand(new LearnCommand());
		cmd.registerCommand(new MuteCommand());
		if (CompatibilityManager.isPluginLoaded(CitizensCompat.class)) {
			cmd.registerCommand(new NpcCommand());
		}
		cmd.registerCommand(new ReloadCommand());
		if (CompatibilityManager.isPluginLoaded(WorldGuardCompat.class))
			cmd.registerCommand(new RegionCommand());
		if (CompatibilityManager.isPluginLoaded(WorldEditCompat.class))
			cmd.registerCommand(new SelectCommand());
		cmd.registerCommand(new TopCommand());
		cmd.registerCommand(new WhitelistAreaCommand());
		cmd.registerCommand(new UpdateCommand());
		cmd.registerCommand(new VersionCommand());

		registerModifiers();

		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(new Rewards(), this);
		getServer().getPluginManager().registerEvents(new HeadCommand(),this);

		if (mMobHuntingManager.getOnlinePlayersAmount() > 0) {
			debug("Reloading %s online player settings from the database", mMobHuntingManager.getOnlinePlayersAmount());
			for (Player player : mMobHuntingManager.getOnlinePlayers()) {
				mPlayerSettingsManager.load(player);
			}
		}
		if (!mConfig.disablePlayerBounties) {
			mBountyManager = new BountyManager(this);
			if (!mConfig.disablePlayerBounties) {
				cmd.registerCommand(new BountyCommand());
			}
		}

		mAchievementManager = new AchievementManager();

		// this is only need when server owner upgrades from very old version of
		// Mobhunting
		if (mAchievementManager.upgradeAchievements())
			mStoreManager.waitForUpdates();

		for (Player player : mMobHuntingManager.getOnlinePlayers())
			mAchievementManager.load(player);

		mLeaderboardManager = new LeaderboardManager(this);

		UpdateHelper.hourlyUpdateCheck(getServer().getConsoleSender(), mConfig.updateCheck, false);

		if (!getServer().getName().toLowerCase().contains("glowstone")) {
			mMetricsManager = new MetricsManager(this);
			mMetricsManager.startMetrics();
		}

		mInitialized = true;

	}

	private void registerPlugin(@SuppressWarnings("rawtypes") Class c, String pluginName) {
		try {
			CompatibilityManager.register(c, pluginName);
		} catch (Exception e) {
			getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "[MobHunting][ERROR] MobHunting could not register with [" + pluginName
							+ "] please check if [" + pluginName + "] is compatible with the server ["
							+ getServer().getBukkitVersion() + "]");
			if (getConfigManager().killDebug)
				e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		if (!mInitialized)
			return;

		mLeaderboardManager.shutdown();
		mAreaManager.shutdown();
		if (!mConfig.disablePlayerBounties)
			mBountyManager.shutdown();

		mModifiers.clear();

		try {
			mStoreManager.shutdown();
			mStore.shutdown();
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
		CitizensCompat.shutdown();
		mWorldGroupManager.save();
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
		mModifiers.add(new Undercover());
		mModifiers.add(new CoverBlown());
		mModifiers.add(new RankBonus());
		mModifiers.add(new DifficultyBonus());
		mModifiers.add(new MountedBonus());
		mModifiers.add(new StackedMobBonus());
	}

	// ************************************************************************************
	// Managers and handlers
	// ************************************************************************************
	public static MobHunting getInstance() {
		return instance;
	}

	public static Economy getEconomy() {
		return mEconomy;
	}

	public static ConfigManager getConfigManager() {
		return mConfig;
	}

	/**
	 * Gets the MobHuntingHandler
	 * 
	 * @return MobHuntingManager
	 */
	public static MobHuntingManager getMobHuntingManager() {
		return mMobHuntingManager;
	}

	/**
	 * Gets the DamageInformation for a LivingEntity
	 * 
	 * @param entity
	 * @return
	 */
	public static DamageInformation getDamageInformation(LivingEntity entity) {
		return mDamageHistory.get(entity);
	}

	/**
	 * Get all Achievements for all players.
	 * 
	 * @return
	 */
	public static AchievementManager getAchievements() {
		return mAchievementManager;
	}

	/**
	 * Gets the Store Manager
	 * 
	 * @return
	 */
	public static IDataStore getStoreManager() {
		return mStore;
	}

	/**
	 * Gets the Database Store Manager
	 * 
	 * @return
	 */
	public static DataStoreManager getDataStoreManager() {
		return mStoreManager;
	}

	/**
	 * Gets the LeaderboardManager
	 * 
	 * @return
	 */
	public static LeaderboardManager getLeaderboardManager() {
		return mLeaderboardManager;
	}

	/**
	 * Get the BountyManager
	 * 
	 * @return
	 */
	public static BountyManager getBountyManager() {
		return mBountyManager;
	}

	/**
	 * Get the AreaManager
	 * 
	 * @return
	 */
	public static AreaManager getAreaManager() {
		return mAreaManager;
	}

	/**
	 * Get all WorldGroups and their worlds
	 * 
	 * @return
	 */
	public static WorldGroup getWorldGroupManager() {
		return mWorldGroupManager;
	}

	public static PlayerSettingsManager getPlayerSettingsmanager() {
		return mPlayerSettingsManager;
	}

	public void registerModifier(IModifier modifier) {
		mModifiers.add(modifier);
	}

	public static void debug(String text, Object... args) {
		if (mConfig.killDebug)
			instance.getLogger().info("[Debug] " + String.format(text, args));
	}

	public static void learn(Player player, String text, Object... args) {
		if (player != null && !CitizensCompat.isNPC(player)
				&& mPlayerSettingsManager.getPlayerSettings(player).isLearningMode())
			if (!mConfig.disableIntegrationBossBarAPI && BossBarAPICompat.isSupported()
					&& BossBarAPICompat.isEnabledInConfig()) {
				BossBarAPICompat.addBar(player, text);
			} else if (!mConfig.disableIntegrationBarAPI && BarAPICompat.isSupported()
					&& BarAPICompat.isEnabledInConfig()) {
				BarAPICompat.setMessageTime(player, text, 5);
			} else {
				player.sendMessage(ChatColor.AQUA + Messages.getString("mobhunting.learn.prefix") + " "
						+ String.format(text, args));
			}
	}

	public static void playerActionBarMessage(Player player, String message) {
		if (!mConfig.disableIntegrationTitleManager && TitleManagerCompat.isSupported()) {
			TitleManagerCompat.setActionBar(player, message);
		} else if (!mConfig.disableIntegrationActionBar && ActionBarCompat.isSupported()) {
			ActionBarCompat.setMessage(player, message);
		} else {
			player.sendMessage(message);
		}
	}

	// ************************************************************************************
	// EVENTS
	// ************************************************************************************
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerDeath(PlayerDeathEvent event) {
		if (!mMobHuntingManager.isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !mMobHuntingManager.isHuntEnabled(event.getEntity()))
			return;

		HuntData data = mMobHuntingManager.getHuntData(event.getEntity());
		if (data.getKillstreakLevel() != 0)
			playerActionBarMessage((Player) event.getEntity(),
					ChatColor.RED + "" + ChatColor.ITALIC + Messages.getString("mobhunting.killstreak.ended"));
		data.setKillStreak(0);

		double playerPenalty = 0;
		Player player = event.getEntity();

		if (CitizensCompat.isNPC(player))
			return;

		EntityDamageEvent cause = player.getLastDamageCause();
		if (cause instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent) cause).getDamager();
			Entity killer = (damager instanceof Projectile) ? (Entity) ((Projectile) damager).getShooter() : damager;

			if (!(killer instanceof Player)) {
				playerPenalty = MobHunting.getConfigManager().getPlayerKilledByMobPenalty(player);
				if (playerPenalty != 0) {
					boolean killed_muted = false;
					if (mPlayerSettingsManager.containsKey(player))
						killed_muted = mPlayerSettingsManager.getPlayerSettings((Player) player).isMuted();

					mEconomy.withdrawPlayer((Player) player, playerPenalty);
					if (!killed_muted)
						playerActionBarMessage((Player) player, ChatColor.RED + "" + ChatColor.ITALIC
								+ Messages.getString("mobhunting.moneylost", "prize", mEconomy.format(playerPenalty)));
					debug("%s was killed by %s and lost %s", player.getName(), killer.getType(),
							mEconomy.format(playerPenalty));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		if (!mMobHuntingManager.isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !mMobHuntingManager.isHuntEnabled((Player) event.getEntity()))
			return;

		Player player = (Player) event.getEntity();
		HuntData data = mMobHuntingManager.getHuntData(player);
		if (data.getKillstreakLevel() != 0)
			playerActionBarMessage(player,
					ChatColor.RED + "" + ChatColor.ITALIC + Messages.getString("mobhunting.killstreak.ended"));
		data.setKillStreak(0);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onSkeletonShoot(ProjectileLaunchEvent event) {
		// TODO: can Skeleton use other weapons than an Arrow?
		if (!(event.getEntity() instanceof Arrow) || !(event.getEntity().getShooter() instanceof Skeleton)
				|| !mMobHuntingManager.isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;

		Skeleton shooter = (Skeleton) event.getEntity().getShooter();

		if (shooter.getTarget() instanceof Player && mMobHuntingManager.isHuntEnabled((Player) shooter.getTarget())
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
				|| !mMobHuntingManager.isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();

		// check if damager or damaged is Sentry / Sentinel. Only Sentry gives a
		// reward.
		if (CitizensCompat.isNPC(damager) && !CitizensCompat.isSentryOrSentinel(damager))
			return;

		if (CitizensCompat.isNPC(damaged) && !CitizensCompat.isSentryOrSentinel(damaged))
			return;

		if (CompatibilityManager.isPluginLoaded(WorldGuardCompat.class) && WorldGuardCompat.isEnabledInConfig()) {
			if ((damager instanceof Player) || MyPetCompat.isMyPet(damager)) {
				RegionManager regionManager = WorldGuardCompat.getWorldGuardPlugin()
						.getRegionManager(damager.getWorld());
				ApplicableRegionSet set = regionManager.getApplicableRegions(damager.getLocation());
				if (set != null) {
					if (!set.allows(DefaultFlag.MOB_DAMAGE)) {
						debug("KillBlocked:(1) %s is hiding in WG region with MOB_DAMAGE %s", damager.getName(),
								set.allows(DefaultFlag.MOB_DAMAGE));
						return;
					}
				}
			}
		}

		DamageInformation info = null;
		info = mDamageHistory.get(damaged);
		if (info == null)
			info = new DamageInformation();

		info.time = System.currentTimeMillis();

		Player cause = null;
		ItemStack weapon = null;

		if (damager instanceof Player) {
			cause = (Player) damager;
		}

		boolean projectile = false;
		if (damager instanceof Projectile) {
			if (((Projectile) damager).getShooter() instanceof Player)
				cause = (Player) ((Projectile) damager).getShooter();

			if (damager instanceof ThrownPotion)
				weapon = ((ThrownPotion) damager).getItem();

			info.mele = false;
			projectile = true;
		} else
			info.mele = true;

		if (damager instanceof Wolf && ((Wolf) damager).isTamed() && ((Wolf) damager).getOwner() instanceof Player) {
			cause = (Player) ((Wolf) damager).getOwner();

			info.mele = false;
			info.wolfAssist = true;
		}

		if (weapon == null && cause != null) {
			if (Misc.isMC19OrNewer() && projectile) {
				PlayerInventory pi = cause.getInventory();
				if (pi.getItemInMainHand().getType() == Material.BOW)
					weapon = pi.getItemInMainHand();
				else
					weapon = pi.getItemInOffHand();
			} else {
				weapon = cause.getItemInHand();
			}
		}

		if (weapon != null)
			info.weapon = weapon;

		// Take note that a weapon has been used at all
		if (info.weapon != null
				&& (Misc.isSword(info.weapon) || Misc.isAxe(info.weapon) || Misc.isPick(info.weapon) || projectile))
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

			if (!info.playerUndercover)
				if (DisguisesHelper.isDisguised(cause)) {
					if (DisguisesHelper.isDisguisedAsAgresiveMob(cause)) {
						debug("[MobHunting] %s was under cover - diguised as an agressive mob", cause.getName());
						info.playerUndercover = true;
					} else
						debug("[MobHunting] %s was under cover - diguised as an passive mob", cause.getName());
					if (mConfig.removeDisguiseWhenAttacking) {
						DisguisesHelper.undisguiseEntity(cause);
						// if (cause instanceof Player)
						playerActionBarMessage(cause, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.undercover.message", "cause", cause.getName()));
						if (damaged instanceof Player)
							playerActionBarMessage((Player) damaged, ChatColor.GREEN + "" + ChatColor.ITALIC
									+ Messages.getString("bonus.undercover.message", "cause", cause.getName()));
					}
				}

			if (!info.mobCoverBlown)
				if (DisguisesHelper.isDisguised(damaged)) {
					if (DisguisesHelper.isDisguisedAsAgresiveMob(damaged)) {
						debug("[MobHunting] %s Cover blown, diguised as an agressive mob", damaged.getName());
						info.mobCoverBlown = true;
					} else
						debug("[MobHunting] %s Cover Blown, diguised as an passive mob", damaged.getName());
					if (mConfig.removeDisguiseWhenAttacked) {
						DisguisesHelper.undisguiseEntity(damaged);
						if (damaged instanceof Player)
							playerActionBarMessage((Player) damaged, ChatColor.GREEN + "" + ChatColor.ITALIC
									+ Messages.getString("bonus.coverblown.message", "damaged", damaged.getName()));
						if (cause instanceof Player)
							playerActionBarMessage(cause, ChatColor.GREEN + "" + ChatColor.ITALIC
									+ Messages.getString("bonus.coverblown.message", "damaged", damaged.getName()));
					}
				}

			mDamageHistory.put((LivingEntity) damaged, info);
		}
	}

	@SuppressWarnings({ "deprecation" })
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMobDeath(EntityDeathEvent event) {

		LivingEntity killed = event.getEntity();

		// TODO: Handle Mob kills a Mob or what if MyPet kills a mob?.
		Player killer = event.getEntity().getKiller();

		if (killer == null && !MyPetCompat.isKilledByMyPet(killed)) {
			// debug("onMobDeath: Mob not killed by Player or MyPet.");
			return;
		}

		// MobHunting is Disabled in World
		if (!mMobHuntingManager.isHuntEnabledInWorld(event.getEntity().getWorld())) {
			if (CompatibilityManager.isPluginLoaded(WorldGuardCompat.class) && WorldGuardCompat.isEnabledInConfig()) {
				if (killer != null || MyPetCompat.isMyPet(killer)) {
					ApplicableRegionSet set = WorldGuardCompat.getWorldGuardPlugin().getRegionManager(killer.getWorld())
							.getApplicableRegions(killer.getLocation());
					if (set.size() > 0) {
						if (set.allows(WorldGuardCompat.getMobHuntingFlag())) {
							debug("KillBlocked %s(%d): Mobhunting disabled in world '%s'"
									+ ",but MobHunting flag is (%s)", killed.getType(), killed.getEntityId(),
									killed.getWorld().getName(), set.allows(WorldGuardCompat.getMobHuntingFlag()));
						} else {
							debug("KillBlocked %s(%d): Mobhunting disabled in world '%s',"
									+ " and MobHunting flag is '%s')", killed.getType(), killed.getEntityId(),
									killed.getWorld().getName(), set.allows(WorldGuardCompat.getMobHuntingFlag()));
							learn(killer, Messages.getString("mobhunting.learn.disabled1"));
							return;
						}
					} else {
						debug("KillBlocked %s(%d): Mobhunting disabled in world %s, "
								+ "WG is supported, but player not in a WG region.", killed.getType(),
								killed.getEntityId(), killed.getWorld().getName());
						// learn(killer,
						// Messages.getString("mobhunting.learn.disabled2"));
						return;
					}
				}
				// killer is not a player - MobHunting is allowed
			} else {
				// MobHunting is NOT allowed in world and no support for WG
				// reject.
				debug("KillBlocked: MobHunting disabled in world and Worldguard is not supported");
				// learn(killer,
				// Messages.getString("mobhunting.learn.disabled2"));
				return;
			}

			// MobHunting is allowed in this world,
			// Continue to ned if... (Do NOTHING).
		}

		// MyPet Compatibility
		if (CompatibilityManager.isPluginLoaded(WorldGuardCompat.class) && WorldGuardCompat.isEnabledInConfig()) {
			if (killer != null || MyPetCompat.isMyPet(killer)) {

				ApplicableRegionSet set = WorldGuardCompat.getWorldGuardPlugin().getRegionManager(killer.getWorld())
						.getApplicableRegions(killer.getLocation());

				if (set.size() > 0) {
					debug("Found %s Worldguard region(s): MOB_DAMAGE flag is %s", set.size(),
							set.allows(DefaultFlag.MOB_DAMAGE));
					if (!set.allows(DefaultFlag.MOB_DAMAGE)) {
						debug("KillBlocked:(2) %s is hiding in WG region with MOB_DAMAGE %s", killer.getName(),
								set.allows(DefaultFlag.MOB_DAMAGE));
						learn(killer, Messages.getString("mobhunting.learn.mob-damage-flag"));
						return;
					} else if (!set.allows(WorldGuardCompat.getMobHuntingFlag())) {
						debug("KillBlocked: %s is hiding in WG region with MOBHUNTING FLAG %s", killer.getName(),
								set.allows(WorldGuardCompat.getMobHuntingFlag()));
						learn(killer, Messages.getString("mobhunting.learn.mobhunting-deny"));
						return;
					}
				}
			}
		}

		// Handle Muted mode
		boolean killer_muted = false;
		boolean killed_muted = false;
		if (mPlayerSettingsManager.containsKey(killer)) {
			killer_muted = mPlayerSettingsManager.getPlayerSettings(killer).isMuted();
		}
		if (mPlayerSettingsManager.containsKey(killed))
			killed_muted = mPlayerSettingsManager.getPlayerSettings((Player) killed).isMuted();

		// Player died while playing a Minigame: MobArena, PVPArena,
		// BattleArena, Suiside, PVP, penalty when Mobs kills player
		if (killed instanceof Player) {
			if (MobArenaCompat.isEnabledInConfig() && MobArenaHelper.isPlayingMobArena((Player) killed)
					&& !mConfig.mobarenaGetRewards) {
				debug("KillBlocked: %s was killed while playing MobArena.", killed.getName());
				learn(killer, Messages.getString("mobhunting.learn.mobarena"));
				return;
			} else if (PVPArenaCompat.isEnabledInConfig() && PVPArenaHelper.isPlayingPVPArena((Player) killed)
					&& !mConfig.pvparenaGetRewards) {
				debug("KillBlocked: %s was killed while playing PvpArena.", killed.getName());
				learn(killer, Messages.getString("mobhunting.learn.pvparena"));
				return;
			} else if (BattleArenaCompat.isEnabledInConfig()
					&& BattleArenaHelper.isPlayingBattleArena((Player) killed)) {
				debug("KillBlocked: %s was killed while playing BattleArena.", killed.getName());
				learn(killer, Messages.getString("mobhunting.learn.battlearena"));
				return;
			} else if (killer != null) {
				if (killed.equals(killer)) {
					// Suiside
					learn(killer, Messages.getString("mobhunting.learn.suiside"));
					debug("KillBlocked: Suiside not allowed (Killer=%s, Killed=%s)", killer.getName(),
							killed.getName());
					return;
				} else if (!mConfig.pvpAllowed) {
					// PVP
					learn(killer, Messages.getString("mobhunting.learn.nopvp"));
					debug("KillBlocked: PVP not allowed. %s killed %s.", killer.getName(), killed.getName());
					return;
				}
			}
		}

		// Player killed a MythicMob
		if (MythicMobsCompat.isSupported()) {
			if (killed.hasMetadata("MH:MythicMob"))
				if (killer != null)
					debug("%s killed a MythicMob", killer.getName());
		}

		// Player killed a Stacked Mob
		if (MobStackerCompat.isSupported()) {
			if (MobStackerCompat.isStackedMob(killed)) {
				if (mConfig.getRewardFromStackedMobs) {
					if (killer != null) {
						debug("%s killed a stacked mob (%s) No=%s", killer.getName(), killed.getType(),
								MobStackerCompat.getStackSize(killed));
						if (MobStackerCompat.killHoleStackOnDeath(killed) && MobStackerCompat.multiplyLoot()) {
							debug("Pay reward for no x mob");
						} else {
							// pay reward for one mob, if config allows
							debug("Pay reward for one mob");
						}
					}
				} else {
					debug("KillBlocked: Rewards from StackedMobs is disabled in Config.yml");
					return;
				}
			}
		}

		// Player killed a Citizens2 NPC
		if (killer != null && CitizensCompat.isNPC(killed) && CitizensCompat.isSentryOrSentinel(killed)) {
			debug("%s killed Sentinel or a Sentry npc-%s (name=%s)", killer.getName(), CitizensCompat.getNPCId(killed),
					CitizensCompat.getNPCName(killed));
		}

		// Player killed a mob while playing a minigame: MobArena, PVPVArena,
		// BattleArena
		// Player is in Godmode or Vanished
		// Player permission to Hunt (and get rewards)
		if (killer != null) {
			if (MobArenaCompat.isEnabledInConfig() && MobArenaHelper.isPlayingMobArena(killer)
					&& !mConfig.mobarenaGetRewards) {
				debug("KillBlocked: %s is currently playing MobArena.", killer.getName());
				learn(killer, Messages.getString("mobhunting.learn.mobarena"));
				return;
			} else if (PVPArenaCompat.isEnabledInConfig() && PVPArenaHelper.isPlayingPVPArena(killer)
					&& !mConfig.pvparenaGetRewards) {
				debug("KillBlocked: %s is currently playing PvpArena.", killer.getName());
				learn(killer, Messages.getString("mobhunting.learn.pvparena"));
				return;
			} else if (BattleArenaCompat.isEnabledInConfig() && BattleArenaHelper.isPlayingBattleArena(killer)) {
				debug("KillBlocked: %s is currently playing BattleArena.", killer.getName());
				learn(killer, Messages.getString("mobhunting.learn.battlearena"));
				return;
			} else if (EssentialsCompat.isSupported()) {
				if (EssentialsCompat.isGodModeEnabled(killer)) {
					debug("KillBlocked: %s is in God mode", killer.getName());
					learn(killer, Messages.getString("mobhunting.learn.godmode"));
					return;
				} else if (EssentialsCompat.isVanishedModeEnabled(killer)) {
					debug("KillBlocked: %s is in Vanished mode", killer.getName());
					learn(killer, Messages.getString("mobhunting.learn.vanished"));
					return;
				}
			} else if (VanishNoPacketCompat.isSupported()) {
				if (VanishNoPacketCompat.isVanishedModeEnabled(killer)) {
					debug("KillBlocked: %s is in Vanished mode", killer.getName());
					learn(killer, Messages.getString("mobhunting.learn.vanished"));
					return;
				}
			}

			if (!hasPermissionToKillMob(killer, killed)) {
				debug("KillBlocked: %s has not permission to kill %s.", killer.getName(), killed.getName());
				learn(killer, Messages.getString("mobhunting.learn.no-permission", "killed-mob", killed.getName()));
				return;
			}
		}

		// There is no reward and no penalty for this kill
		if (mConfig.getBaseKillPrize(event.getEntity()) == 0 && mConfig.getKillConsoleCmd(killed).equals("")) {
			// if (killed != null)
			debug("KillBlocked %s(%d): There is no reward and no penalty for this Mob/Player", killed.getType(),
					killed.getEntityId());
			learn(killer, Messages.getString("mobhunting.learn.no-reward", "killed", killed.getName()));
			return;
		}

		// The Mob/Player has MH:Blocked
		if (event.getEntity().hasMetadata("MH:blocked")) {
			if (killed != null) {
				debug("KillBlocked %s(%d): Mob has MH:blocked meta (probably spawned from a mob spawner)",
						event.getEntity().getType(), killed.getEntityId());
				learn(killer, Messages.getString("mobhunting.learn.mobspawner", "killed", killed.getName()));
			}
			return;
		}

		// MobHunting is disabled for the player
		if (killer != null && !mMobHuntingManager.isHuntEnabled(killer)) {
			debug("KillBlocked %s: Hunting is disabled for player", killer.getName());
			learn(killer, Messages.getString("mobhunting.learn.huntdisabled"));
			return;
		}

		// The player is in Creative mode
		if (killer != null && killer.getGameMode() == GameMode.CREATIVE) {
			debug("KillBlocked %s: In creative mode", killer.getName());
			learn(killer, Messages.getString("mobhunting.learn.creative"));
			return;
		}

		// Update DamageInformation
		DamageInformation info = null;
		info = mDamageHistory.get(event.getEntity());

		// DamageInformation info = null;
		if (killed instanceof LivingEntity && mDamageHistory.containsKey((LivingEntity) killed)) {
			info = mDamageHistory.get(killed);

			if (System.currentTimeMillis() - info.time > mConfig.assistTimeout * 1000)
				info = null;
			else if (killer == null)
				killer = info.attacker;
		}
		EntityDamageByEntityEvent lastDamageCause = null;
		if (killed.getLastDamageCause() instanceof EntityDamageByEntityEvent)
			lastDamageCause = (EntityDamageByEntityEvent) killed.getLastDamageCause();
		if (info == null) {
			info = new DamageInformation();
			info.time = System.currentTimeMillis();
			info.lastAttackTime = info.time;
			if (killer != null) {
				info.attacker = killer;
				info.attackerPosition = killer.getLocation();
			}
			info.usedWeapon = true;
		}
		if (((System.currentTimeMillis() - info.lastAttackTime) > mConfig.killTimeout * 1000) && (info.wolfAssist
				&& ((System.currentTimeMillis() - info.lastAttackTime) > mConfig.assistTimeout * 1000))) {
			debug("KillBlocked %s: Last damage was too long ago (%s sec.)", killer.getName(),
					(System.currentTimeMillis() - info.lastAttackTime) / 1000);
			return;
		}
		if (info.weapon == null)
			info.weapon = new ItemStack(Material.AIR);

		// Player or killed Mob is disguised
		if (!info.playerUndercover)
			if (DisguisesHelper.isDisguised(killer)) {
				if (DisguisesHelper.isDisguisedAsAgresiveMob(killer)) {
					info.playerUndercover = true;
				} else if (mConfig.removeDisguiseWhenAttacking) {
					DisguisesHelper.undisguiseEntity(killer);
					if (killer != null && !killer_muted)
						playerActionBarMessage(killer, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.undercover.message", "cause", killer.getName()));
					if (killed instanceof Player && !killed_muted)
						playerActionBarMessage((Player) killed, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.undercover.message", "cause", killer.getName()));
				}
			}
		if (!info.mobCoverBlown)
			if (DisguisesHelper.isDisguised(killed)) {
				if (DisguisesHelper.isDisguisedAsAgresiveMob(killed)) {
					info.mobCoverBlown = true;
				}
				if (mConfig.removeDisguiseWhenAttacked) {
					DisguisesHelper.undisguiseEntity(killed);
					if (killed instanceof Player && !killed_muted)
						playerActionBarMessage((Player) killed, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.coverblown.message", "damaged", killed.getName()));
					if (killer != null && !killer_muted)
						playerActionBarMessage(killer, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.coverblown.message", "damaged", killed.getName()));
				}
			}

		HuntData data;

		// if (killer != null)
		data = mMobHuntingManager.getHuntData(killer);
		// else
		// data = mMobHuntingManager.getHuntData(damager);

		// Killstreak
		Misc.handleKillstreak(killer);

		// Record kills that are still within a small area
		Location loc = killed.getLocation();

		// Grinding detection
		Area detectedGrindingArea = mAreaManager.getGrindingArea(loc);
		if (detectedGrindingArea == null)
			detectedGrindingArea = data.getGrindingArea(loc);
		// Slimes are except from grinding due to their splitting nature
		if (!(event.getEntity() instanceof Slime) && mConfig.penaltyGrindingEnable
				&& !killed.hasMetadata("MH:reinforcement") && !mAreaManager.isWhitelisted(killed.getLocation())) {
			MobHunting.debug("Checking if player is grinding mob in the same region within a range of %s blocks",
					data.getcDampnerRange());
			MobHunting.debug("DampendKills=%s ", data.getDampenedKills());

			if (detectedGrindingArea != null) {
				data.lastKillAreaCenter = null;
				data.setDampenedKills(detectedGrindingArea.count++);
				if (data.getDampenedKills() == 20)
					mAreaManager.registerKnownGrindingSpot(detectedGrindingArea);
			} else {
				if (data.lastKillAreaCenter != null) {
					if (loc.getWorld().equals(data.lastKillAreaCenter.getWorld())) {
						if (loc.distance(data.lastKillAreaCenter) < data.getcDampnerRange()) {
							if (!MobStackerCompat.isSupported()
									|| (MobStackerCompat.isSupported() && MobStackerCompat.isStackedMob(killed)
											&& !MobStackerCompat.isGrindingStackedMobsAllowed())) {
								data.setDampenedKills(data.getDampenedKills() + 1);
								if (data.getDampenedKills() == 10) {
									MobHunting
											.debug("Detected grinding. Killings too close, adding 1 to DampenedKills.");
									learn(killer, Messages.getString("mobhunting.learn.grindingnotallowed"));
									playerActionBarMessage(killer,
											ChatColor.RED + Messages.getString("mobhunting.grinding.detected"));
									data.recordGrindingArea();
								}
							}
						} else {
							data.lastKillAreaCenter = loc.clone();
							data.setDampenedKills(0);
						}
					} else {
						data.lastKillAreaCenter = loc.clone();
						data.setDampenedKills(0);
					}
				} else {
					data.lastKillAreaCenter = loc.clone();
					data.setDampenedKills(0);
				}
			}

			if (data.getDampenedKills() > 10 + 4) {
				if (data.getKillstreakLevel() != 0)
					playerActionBarMessage(killer, ChatColor.RED + Messages.getString("mobhunting.killstreak.lost"));
				data.setKillStreak(0);
			}
		}

		// Calculate basic the reward
		double cash = mConfig.getBaseKillPrize(killed);

		debug("Mob Basic Prize=%s", cash);
		double multiplier = 1.0;

		// Apply the modifiers to Basic reward
		ArrayList<String> modifiers = new ArrayList<String>();
		for (IModifier mod : mModifiers) {
			if (mod.doesApply(killed, killer, data, info, lastDamageCause)) {
				double amt = mod.getMultiplier(killed, killer, data, info, lastDamageCause);
				if (amt != 1.0) {
					modifiers.add(mod.getName());
					multiplier *= amt;
					data.addModifier(mod.getName(), amt);
					debug("Multiplier: %s = %s", mod.getName(), amt);
				}
			}
		}
		data.setReward(cash);

		multiplier *= data.getKillstreakMultiplier();

		String extraString = "";

		// Only display the multiplier if its not 1
		if (Math.abs(multiplier - 1) > 0.05)
			extraString += String.format("x%.1f", multiplier);

		// Add on modifiers
		for (String modifier : modifiers)
			extraString += ChatColor.WHITE + " * " + modifier;

		cash *= multiplier;

		// Handle Bounty Kills
		double reward = 0;
		if (killer != null && !mConfig.disablePlayerBounties && killed instanceof Player) {
			debug("This was a Pvp kill (killed=%s) no of bounties=%s", killed.getName(),
					mBountyManager.getAllBounties().size());
			OfflinePlayer wantedPlayer = (OfflinePlayer) killed;
			String worldGroupName = MobHunting.getWorldGroupManager().getCurrentWorldGroup(killer);
			if (BountyManager.hasBounties(worldGroupName, wantedPlayer)) {
				Set<Bounty> bounties = mBountyManager.getBounties(worldGroupName, wantedPlayer);
				for (Bounty b : bounties) {
					reward += b.getPrize();
					OfflinePlayer bountyOwner = b.getBountyOwner();
					mBountyManager.removeBounty(b);
					if (bountyOwner.isOnline())
						playerActionBarMessage(Misc.getOnLinePlayer(bountyOwner),
								Messages.getString("mobhunting.bounty.bounty-claimed", "killer", killer.getName(),
										"prize", b.getPrize(), "killed", killed.getName()));
					b.setStatus(BountyStatus.completed);
					getDataStoreManager().updateBounty(b);
				}
				// OBS: Bounty will be added to the Reward for killing/Robbing
				// the player
				playerActionBarMessage(killer, Messages.getString("mobhunting.moneygain-for-killing", "money",
						mEconomy.format(reward), "killed", killed.getName()));
				debug("%s got %s for killing %s", killer.getName(), reward, killed.getName());
				// TODO: call bounty event, and check if canceled.
				getEconomy().depositPlayer(killer, reward);
				getDataStoreManager().recordKill(killer, ExtendedMobType.getExtendedMobType(killed),
						killed.hasMetadata("MH:hasBonus"));
			} else {
				debug("There is no Bounty on %s", killed.getName());
			}
		}

		// Calculate the reward
		// cash += reward;

		if ((cash >= 0.01) || (cash <= -0.01)) {
			// TODO: This must be moved, only works for cash!=0

			// Handle MobHuntKillEvent
			MobHuntKillEvent event2 = new MobHuntKillEvent(data, info, killed, killer);
			Bukkit.getPluginManager().callEvent(event2);
			if (event2.isCancelled()) {
				debug("KillBlocked %s: MobHuntKillEvent was cancelled", killer.getName());
				return;
			}

			// Handle reward on PVP kill. (Robbing)
			if (killer != null && killed instanceof Player && !CitizensCompat.isNPC(killed) && mConfig.robFromVictim) {
				mEconomy.withdrawPlayer((Player) killed, cash);
				if (!killed_muted)
					playerActionBarMessage((Player) killed, ChatColor.RED + "" + ChatColor.ITALIC
							+ Messages.getString("mobhunting.moneylost", "prize", mEconomy.format(cash)));
				debug("%s lost %s", killed.getName(), mEconomy.format(cash));
			}

			// Reward for assisted kill
			if (info.assister == null || mConfig.enableAssists == false) {
				if (cash > 0) {
					if (mConfig.dropMoneyOnGroup) {
						Rewards.dropMoneyOnGround(killed, cash);
						debug("%s was droped on the ground", mEconomy.format(cash));
					} else {
						mEconomy.depositPlayer(killer, cash);
						debug("%s got a reward (%s)", killer.getName(), mEconomy.format(cash));
					}
				} else {
					mEconomy.withdrawPlayer(killer, -cash);
					debug("%s got a penalty (%s)", killer.getName(), mEconomy.format(cash));
				}
			} else {
				cash = cash / 2;
				if (cash > 0) {
					if (mConfig.dropMoneyOnGroup) {
						Rewards.dropMoneyOnGround(killed, cash);
						debug("%s was droped on the ground", mEconomy.format(cash));
					} else {
						mEconomy.depositPlayer(killer, cash);
						onAssist(info.assister, killer, killed, info.lastAssistTime);
						debug("%s got a ½ reward (%s)", killer.getName(), mEconomy.format(cash));
					}
				} else {
					mEconomy.withdrawPlayer(killer, -cash);
					onAssist(info.assister, killer, killed, info.lastAssistTime);
					debug("%s got a ½ penalty (%s)", killer.getName(), mEconomy.format(cash));
				}
			}

			if (killer != null)
				debug("RecordKill: %s killed a %s", killer.getName(), ExtendedMobType.getExtendedMobType(killed));
			// MythicMob Kill - update PlayerStats
			// TODO: record mythicmob kills as its own kind of mobs
			if (ExtendedMobType.getExtendedMobType(killed) != null)
				getDataStoreManager().recordKill(killer, ExtendedMobType.getExtendedMobType(killed),
						killed.hasMetadata("MH:hasBonus"));

			// Tell the player that he got the reward, unless muted
			if (!killer_muted && !getConfigManager().dropMoneyOnGroup)
				if (extraString.trim().isEmpty()) {
					if (cash > 0) {
						playerActionBarMessage(killer, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("mobhunting.moneygain", "prize", mEconomy.format(cash)));
					} else {
						playerActionBarMessage(killer, ChatColor.RED + "" + ChatColor.ITALIC
								+ Messages.getString("mobhunting.moneylost", "prize", mEconomy.format(cash)));
					}
				} else
					playerActionBarMessage(killer,
							ChatColor.GREEN + "" + ChatColor.ITALIC + Messages.getString("mobhunting.moneygain.bonuses",
									"prize", mEconomy.format(cash), "bonuses", extraString.trim()));
		} else
			debug("KillBlocked %s: Gained money was less than 1 cent (grinding or penalties) (%s)", killer.getName(),
					extraString);

		// Run console commands as a reward
		if (data.getDampenedKills() < 10) {
			if (!mConfig.getKillConsoleCmd(killed).equals("") && mConfig.getCmdRunProbabilityBase(killed) != 0) {
				if (getConfigManager().mRand.nextInt(mConfig.getCmdRunProbabilityBase(killed)) < mConfig
						.getCmdRunProbability(killed)) {
					String worldname = killer.getWorld().getName();
					String killerpos = killer.getLocation().getBlockX() + " " + killer.getLocation().getBlockY() + " "
							+ killer.getLocation().getBlockZ();
					String killedpos = killed.getLocation().getBlockX() + " " + killed.getLocation().getBlockY() + " "
							+ killed.getLocation().getBlockZ();
					String prizeCommand = mConfig.getKillConsoleCmd(killed).replaceAll("\\{player\\}", killer.getName())
							.replaceAll("\\{killed_player\\}", killed.getName())
							.replaceAll("\\{killer\\}", killer.getName()).replaceAll("\\{killed\\}", killed.getName())
							.replaceAll("\\{world\\}", worldname).replace("\\{prize\\}", mEconomy.format(cash))
							.replaceAll("\\{killerpos\\}", killerpos).replaceAll("\\{killedpos\\}", killedpos);
					debug("command to be run is:" + prizeCommand);
					if (!mConfig.getKillConsoleCmd(killed).equals("")) {
						String str = prizeCommand;
						do {
							if (str.contains("|")) {
								int n = str.indexOf("|");
								Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
										str.substring(0, n));
								str = str.substring(n + 1, str.length()).toString();
							}
						} while (str.contains("|"));
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), str);
					}
					// send a message to the player
					if (!mConfig.getKillRewardDescription(killed).equals("") && !killer_muted) {
						playerActionBarMessage(killer, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ mConfig.getKillRewardDescription(killed).replaceAll("\\{player\\}", killer.getName())
										.replaceAll("\\{killed_player\\}", killed.getName())
										.replaceAll("\\{killer\\}", killer.getName())
										.replaceAll("\\{killed\\}", killed.getName())
										.replace("\\{prize\\}", mEconomy.format(cash))
										.replaceAll("\\{world\\}", worldname).replaceAll("\\{killerpos\\}", killerpos)
										.replaceAll("\\{killedpos\\}", killedpos));
					}
				}
			}
		}
	}

	private void onAssist(Player player, Player killer, LivingEntity killed, long time) {
		if (!mConfig.enableAssists || (System.currentTimeMillis() - time) > mConfig.assistTimeout * 1000)
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
			getDataStoreManager().recordAssist(player, killer, ExtendedMobType.getExtendedMobType(killed),
					killed.hasMetadata("MH:hasBonus"));
			mEconomy.depositPlayer(player, cash);
			debug("%s got a on assist reward (%s)", player.getName(), mEconomy.format(cash));

			if (ks != 1.0)
				playerActionBarMessage(player, ChatColor.GREEN + "" + ChatColor.ITALIC
						+ Messages.getString("mobhunting.moneygain.assist", "prize", mEconomy.format(cash)));
			else
				playerActionBarMessage(player,
						ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("mobhunting.moneygain.assist.bonuses", "prize",
										mEconomy.format(cash), "bonuses", String.format("x%.1f", ks)));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		// final Player player = event.getPlayer();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerQuit(PlayerQuitEvent event) {
		// Player player = event.getPlayer();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void bonusMobSpawn(CreatureSpawnEvent event) {
		if (CitizensCompat.isNPC(event.getEntity()))
			return;

		if (event.getEntityType() == EntityType.ENDER_DRAGON)
			return;

		if (!mMobHuntingManager.isHuntEnabledInWorld(event.getLocation().getWorld())
				|| (mConfig.getBaseKillPrize(event.getEntity()) <= 0
						&& mConfig.getKillConsoleCmd(event.getEntity()).equals(""))
				|| event.getSpawnReason() != SpawnReason.NATURAL)
			return;

		if (getConfigManager().mRand.nextDouble() * 100 < mConfig.bonusMobChance) {
			mParticleManager.attachEffect(event.getEntity(), Effect.MOBSPAWNER_FLAMES);
			if (getConfigManager().mRand.nextBoolean())
				event.getEntity()
						.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 3));
			else
				event.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
			event.getEntity().setMetadata("MH:hasBonus", new FixedMetadataValue(this, true));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void spawnerMobSpawn(CreatureSpawnEvent event) {

		if (CitizensCompat.isNPC(event.getEntity()))
			return;

		if (!mMobHuntingManager.isHuntEnabledInWorld(event.getLocation().getWorld())
				|| (mConfig.getBaseKillPrize(event.getEntity()) <= 0)
						&& mConfig.getKillConsoleCmd(event.getEntity()).equals(""))
			return;

		if (event.getSpawnReason() != SpawnReason.SPAWNER && event.getSpawnReason() != SpawnReason.SPAWNER_EGG)
			return;

		if (!mConfig.allowMobSpawners)
			event.getEntity().setMetadata("MH:blocked", new FixedMetadataValue(this, true));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void reinforcementMobSpawn(CreatureSpawnEvent event) {
		if (!mMobHuntingManager.isHuntEnabledInWorld(event.getLocation().getWorld())
				|| (mConfig.getBaseKillPrize(event.getEntity()) <= 0)
						&& mConfig.getKillConsoleCmd(event.getEntity()).equals(""))
			return;

		if (event.getSpawnReason() == SpawnReason.REINFORCEMENTS)
			event.getEntity().setMetadata("MH:reinforcement", new FixedMetadataValue(this, true));
	}

	/**
	 * Checks if the player has permission to kill the mob
	 * 
	 * @param player
	 * @param mob
	 * @return true if the player has permission to kill the mob
	 */
	public boolean hasPermissionToKillMob(Player player, LivingEntity mob) {
		String permission_prefix = "*";
		if (MythicMobsCompat.isSupported() && MythicMobsCompat.isMythicMob(mob)) {
			permission_prefix = MythicMobsCompat.getMythicMobType(mob);
			if (player.isPermissionSet("mobhunting.mobs." + permission_prefix))
				return player.hasPermission("mobhunting.mobs." + MythicMobsCompat.getMythicMobType(mob));
			else {
				MobHunting.debug("Permission mobhunting.mobs.mythicmobtype not set, defaulting to True.");
				return true;
			}
		} else if (CitizensCompat.isCitizensSupported() && CitizensCompat.isSentryOrSentinel(mob)) {
			permission_prefix = "npc-" + CitizensCompat.getNPCId(mob);
			if (player.isPermissionSet("mobhunting.mobs." + permission_prefix))
				return player.hasPermission("mobhunting.mobs." + permission_prefix);
			else {
				MobHunting.debug("Permission mobhunting.mobs.'" + permission_prefix + "' not set, defaulting to True.");
				return true;
			}
		} else {
			permission_prefix = mob.getType().toString();
			if (player.isPermissionSet("mobhunting.mobs." + permission_prefix))
				return player.hasPermission("mobhunting.mobs." + permission_prefix);
			else {
				MobHunting.debug("Permission 'mobhunting.mobs.*' or 'mobhunting.mobs." + permission_prefix
						+ "' not set, defaulting to True.");
				return true;
			}
		}
	}

}
