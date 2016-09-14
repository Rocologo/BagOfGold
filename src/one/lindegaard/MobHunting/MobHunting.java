package one.lindegaard.MobHunting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import one.lindegaard.MobHunting.achievements.*;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.bounty.BountyManager;
import one.lindegaard.MobHunting.bounty.BountyStatus;
import one.lindegaard.MobHunting.commands.BountyCommand;
import one.lindegaard.MobHunting.commands.CheckGrindingCommand;
import one.lindegaard.MobHunting.commands.ClearGrindingCommand;
import one.lindegaard.MobHunting.commands.CommandDispatcher;
import one.lindegaard.MobHunting.commands.DatabaseCommand;
import one.lindegaard.MobHunting.commands.DebugCommand;
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
import one.lindegaard.MobHunting.compatibility.ActionAnnouncerCompat;
import one.lindegaard.MobHunting.compatibility.ActionBarAPICompat;
import one.lindegaard.MobHunting.compatibility.ActionbarCompat;
import one.lindegaard.MobHunting.compatibility.BarAPICompat;
import one.lindegaard.MobHunting.compatibility.BattleArenaCompat;
import one.lindegaard.MobHunting.compatibility.BattleArenaHelper;
import one.lindegaard.MobHunting.compatibility.BossBarAPICompat;
import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.CompatibilityManager;
import one.lindegaard.MobHunting.compatibility.CustomMobsCompat;
import one.lindegaard.MobHunting.compatibility.DisguiseCraftCompat;
import one.lindegaard.MobHunting.compatibility.DisguisesHelper;
import one.lindegaard.MobHunting.compatibility.EssentialsCompat;
import one.lindegaard.MobHunting.compatibility.GringottsCompat;
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
import one.lindegaard.MobHunting.compatibility.TARDISWeepingAngelsCompat;
import one.lindegaard.MobHunting.compatibility.TitleAPICompat;
import one.lindegaard.MobHunting.compatibility.TitleManagerCompat;
import one.lindegaard.MobHunting.compatibility.VanishNoPacketCompat;
import one.lindegaard.MobHunting.compatibility.WorldEditCompat;
import one.lindegaard.MobHunting.compatibility.WorldGuardCompat;
import one.lindegaard.MobHunting.compatibility.WorldGuardHelper;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;
import one.lindegaard.MobHunting.leaderboard.LeaderboardManager;
import one.lindegaard.MobHunting.modifier.*;
import one.lindegaard.MobHunting.rewards.RewardManager;
import one.lindegaard.MobHunting.rewards.Rewards;
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
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.flags.DefaultFlag;

public class MobHunting extends JavaPlugin implements Listener {

	// Constants
	private final static String pluginName = "mobhunting";

	private static MobHunting instance;

	private static RewardManager mRewardManager;
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

	@Override
	public void onEnable() {

		instance = this;

		Messages.exportDefaultLanguages(this);

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

		mRewardManager = new RewardManager(this);
		if (mRewardManager.getEconomy() != null) {

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
			registerPlugin(ActionbarCompat.class, "Actionbar");
			registerPlugin(ActionBarAPICompat.class, "ActionBarAPI");
			registerPlugin(ActionAnnouncerCompat.class, "ActionAnnouncer");
			registerPlugin(MobStackerCompat.class, "MobStacker");
			registerPlugin(GringottsCompat.class, "Gringotts");
			registerPlugin(TARDISWeepingAngelsCompat.class, "TARDISWeepingAngels");
			registerPlugin(CustomMobsCompat.class, "CustomMobs");

			// register commands
			CommandDispatcher cmd = new CommandDispatcher("mobhunt",
					Messages.getString("mobhunting.command.base.description") + getDescription().getVersion());
			getCommand("mobhunt").setExecutor(cmd);
			getCommand("mobhunt").setTabCompleter(cmd);
			cmd.registerCommand(new AchievementsCommand());
			cmd.registerCommand(new CheckGrindingCommand());
			cmd.registerCommand(new ClearGrindingCommand());
			cmd.registerCommand(new DatabaseCommand());
			cmd.registerCommand(new HeadCommand(this));
			cmd.registerCommand(new LeaderboardCommand(this));
			cmd.registerCommand(new LearnCommand());
			cmd.registerCommand(new MuteCommand());
			if (CompatibilityManager.isPluginLoaded(CitizensCompat.class) && CitizensCompat.isSupported()) {
				cmd.registerCommand(new NpcCommand(this));
			}
			cmd.registerCommand(new ReloadCommand());
			if (WorldGuardCompat.isSupported())
				cmd.registerCommand(new RegionCommand());
			if (CompatibilityManager.isPluginLoaded(WorldEditCompat.class) && WorldEditCompat.isSupported())
				cmd.registerCommand(new SelectCommand());
			cmd.registerCommand(new TopCommand());
			cmd.registerCommand(new WhitelistAreaCommand());
			cmd.registerCommand(new UpdateCommand());
			cmd.registerCommand(new VersionCommand());
			cmd.registerCommand(new DebugCommand());

			registerModifiers();

			if (mMobHuntingManager.getOnlinePlayersAmount() > 0) {
				Messages.debug("Reloading %s online player settings from the database",
						mMobHuntingManager.getOnlinePlayersAmount());
				for (Player player : mMobHuntingManager.getOnlinePlayers())
					mPlayerSettingsManager.load(player);
			}
			if (!mConfig.disablePlayerBounties) {
				mBountyManager = new BountyManager(this);
				cmd.registerCommand(new BountyCommand());
				if (mMobHuntingManager.getOnlinePlayersAmount() > 0)
					for (Player player : mMobHuntingManager.getOnlinePlayers())
						mBountyManager.loadOpenBounties(player);
			}

			mAchievementManager = new AchievementManager();

			// this is only need when server owner upgrades from very old
			// version of Mobhunting
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

			Bukkit.getPluginManager().registerEvents(this, this);

			mInitialized = true;
		}

	}

	private void registerPlugin(@SuppressWarnings("rawtypes") Class c, String pluginName) {
		try {
			CompatibilityManager.register(c, pluginName);
		} catch (Exception e) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "[MobHunting][ERROR] MobHunting could not register with [" + pluginName
							+ "] please check if [" + pluginName + "] is compatible with the server ["
							+ Bukkit.getServer().getBukkitVersion() + "]");
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
		mModifiers.add(new BonusMobBonus());
		mModifiers.add(new BrawlerBonus());
		mModifiers.add(new CoverBlown());
		mModifiers.add(new CriticalModifier());
		mModifiers.add(new DifficultyBonus());
		mModifiers.add(new FlyingPenalty());
		mModifiers.add(new FriendleFireBonus());
		mModifiers.add(new GrindingPenalty());
		mModifiers.add(new MountedBonus());
		mModifiers.add(new ProSniperBonus());
		mModifiers.add(new RankBonus());
		mModifiers.add(new ReturnToSenderBonus());
		mModifiers.add(new ShoveBonus());
		mModifiers.add(new SneakyBonus());
		mModifiers.add(new SniperBonus());
		mModifiers.add(new StackedMobBonus());
		mModifiers.add(new Undercover());
	}

	public void registerModifierXXX(IModifier modifier) {
		mModifiers.add(modifier);
	}

	// ************************************************************************************
	// Managers and handlers
	// ************************************************************************************
	public static MobHunting getInstance() {
		return instance;
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
	public static AchievementManager getAchievementManager() {
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

	/**
	 * Get the PlayerSettingsManager
	 * 
	 * @return
	 */
	public static PlayerSettingsManager getPlayerSettingsmanager() {
		return mPlayerSettingsManager;
	}

	/**
	 * Get the RewardManager
	 * 
	 * @return
	 */
	public static RewardManager getRewardManager() {
		return mRewardManager;
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
			Messages.playerActionBarMessage((Player) event.getEntity(),
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

					mRewardManager.withdrawPlayer(player, playerPenalty);
					if (!killed_muted)
						Messages.playerActionBarMessage((Player) player,
								ChatColor.RED + "" + ChatColor.ITALIC + Messages.getString("mobhunting.moneylost",
										"prize", mRewardManager.format(playerPenalty)));
					Messages.debug("%s was killed by %s and lost %s", player.getName(), killer.getType(),
							mRewardManager.format(playerPenalty));
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
			Messages.playerActionBarMessage(player,
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
			return;// ok
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();

		// check if damager or damaged is Sentry / Sentinel. Only Sentry gives a
		// reward.
		if (CitizensCompat.isNPC(damager) && !CitizensCompat.isSentryOrSentinel(damager))
			return;

		if (CitizensCompat.isNPC(damaged) && !CitizensCompat.isSentryOrSentinel(damaged))
			return;

		if (WorldGuardCompat.isSupported()
				&& !WorldGuardHelper.isAllowedByWorldGuard(damager, damaged, DefaultFlag.MOB_DAMAGE)) {
			// Messages.debug("KillBlocked:(1) %s is hiding in WG region with
			// mob-damage=DENY", damager.getName());
			return;
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
						Messages.debug("[MobHunting] %s was under cover - diguised as an agressive mob",
								cause.getName());
						info.playerUndercover = true;
					} else
						Messages.debug("[MobHunting] %s was under cover - diguised as an passive mob", cause.getName());
					if (mConfig.removeDisguiseWhenAttacking) {
						DisguisesHelper.undisguiseEntity(cause);
						// if (cause instanceof Player)
						Messages.playerActionBarMessage(cause, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.undercover.message", "cause", cause.getName()));
						if (damaged instanceof Player)
							Messages.playerActionBarMessage((Player) damaged, ChatColor.GREEN + "" + ChatColor.ITALIC
									+ Messages.getString("bonus.undercover.message", "cause", cause.getName()));
					}
				}

			if (!info.mobCoverBlown)
				if (DisguisesHelper.isDisguised(damaged)) {
					if (DisguisesHelper.isDisguisedAsAgresiveMob(damaged)) {
						Messages.debug("[MobHunting] %s Cover blown, diguised as an agressive mob", damaged.getName());
						info.mobCoverBlown = true;
					} else
						Messages.debug("[MobHunting] %s Cover Blown, diguised as an passive mob", damaged.getName());
					if (mConfig.removeDisguiseWhenAttacked) {
						DisguisesHelper.undisguiseEntity(damaged);
						if (damaged instanceof Player)
							Messages.playerActionBarMessage((Player) damaged, ChatColor.GREEN + "" + ChatColor.ITALIC
									+ Messages.getString("bonus.coverblown.message", "damaged", damaged.getName()));
						if (cause instanceof Player)
							Messages.playerActionBarMessage(cause, ChatColor.GREEN + "" + ChatColor.ITALIC
									+ Messages.getString("bonus.coverblown.message", "damaged", damaged.getName()));
					}
				}

			mDamageHistory.put((LivingEntity) damaged, info);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMobDeath(EntityDeathEvent event) {

		LivingEntity killed = event.getEntity();

		Player killer = event.getEntity().getKiller();

		// Killer is not a player and not a MyPet.
		if (killer == null && !MyPetCompat.isKilledByMyPet(killed)) {
			return;
		}

		// MobHunting is Disabled in World
		if (!mMobHuntingManager.isHuntEnabledInWorld(event.getEntity().getWorld())) {
			if (WorldGuardCompat.isSupported()) {
				if (killer != null || MyPetCompat.isMyPet(killer)) {
					if (WorldGuardHelper.isAllowedByWorldGuard(killer, killed, WorldGuardHelper.getMobHuntingFlag())) {
						Messages.learn(killer, Messages.getString("mobhunting.learn.overruled"));
						Messages.debug(
								"KillBlocked %s(%d): Mobhunting disabled in world '%s'"
										+ ",but MobHunting=ALLOW overrules.",
								killed.getType().getName(), killed.getEntityId(), killed.getWorld().getName());
					} else {
						Messages.debug("KillBlocked %s(%d): Mobhunting disabled in world '%s'",
								killed.getType().getName(), killed.getEntityId(), killed.getWorld().getName());
						Messages.learn(killer, Messages.getString("mobhunting.learn.disabled"));
						return;
					}

				}
				// killer is not a player - MobHunting is allowed
			} else {
				// MobHunting is NOT allowed in world and no support for WG
				// reject.
				Messages.debug("KillBlocked: MobHunting disabled in world and Worldguard is not supported");
				return;
			}

			// MobHunting is allowed in this world,
			// Continue to ned if... (Do NOTHING).
		}

		// MyPet Compatibility
		if (WorldGuardCompat.isSupported()) {
			if ((killer != null || MyPetCompat.isMyPet(killer)) && !CitizensCompat.isNPC(killer)) {
				if (WorldGuardHelper.isAllowedByWorldGuard(killer, killed, DefaultFlag.MOB_DAMAGE)) {
					if (WorldGuardHelper.isAllowedByWorldGuard(killer, killed, WorldGuardHelper.getMobHuntingFlag())) {
						Messages.debug(
								"KillAllowed:(1) %s is hiding in WG region, but this is overruled with MobHunting=allow",
								killer.getName());
					} else {
						Messages.debug("KillBlocked:(2) %s is hiding in WG region with mob-damage=DENY",
								killer.getName());
						Messages.learn(killer, Messages.getString("mobhunting.learn.mob-damage-flag"));
						return;
					}
				} else if (!WorldGuardHelper.isAllowedByWorldGuard(killer, killed,
						WorldGuardHelper.getMobHuntingFlag())) {
					Messages.debug("KillBlocked: %s is in a protected region mobhunting=DENY", killer.getName());
					Messages.learn(killer, Messages.getString("mobhunting.learn.mobhunting-deny"));
					return;
				}
			}
		}

		// Handle Muted mode
		boolean killer_muted = false;
		boolean killed_muted = false;
		if (mPlayerSettingsManager.containsKey(killer))
			killer_muted = mPlayerSettingsManager.getPlayerSettings(killer).isMuted();
		if (mPlayerSettingsManager.containsKey(killed))
			killed_muted = mPlayerSettingsManager.getPlayerSettings((Player) killed).isMuted();

		// Player died while playing a Minigame: MobArena, PVPArena,
		// BattleArena, Suiside, PVP, penalty when Mobs kills player
		if (killed instanceof Player) {
			if (MobArenaCompat.isEnabledInConfig() && MobArenaHelper.isPlayingMobArena((Player) killed)
					&& !mConfig.mobarenaGetRewards) {
				Messages.debug("KillBlocked: %s was killed while playing MobArena.", killed.getName());
				Messages.learn(killer, Messages.getString("mobhunting.learn.mobarena"));
				return;
			} else if (PVPArenaCompat.isEnabledInConfig() && PVPArenaHelper.isPlayingPVPArena((Player) killed)
					&& !mConfig.pvparenaGetRewards) {
				Messages.debug("KillBlocked: %s was killed while playing PvpArena.", killed.getName());
				Messages.learn(killer, Messages.getString("mobhunting.learn.pvparena"));
				return;
			} else if (BattleArenaCompat.isEnabledInConfig()
					&& BattleArenaHelper.isPlayingBattleArena((Player) killed)) {
				Messages.debug("KillBlocked: %s was killed while playing BattleArena.", killed.getName());
				Messages.learn(killer, Messages.getString("mobhunting.learn.battlearena"));
				return;
			} else if (killer != null) {
				if (killed.equals(killer)) {
					// Suicide
					Messages.learn(killer, Messages.getString("mobhunting.learn.suiside"));
					Messages.debug("KillBlocked: Suiside not allowed (Killer=%s, Killed=%s)", killer.getName(),
							killed.getName());
					return;
				} else if (!mConfig.pvpAllowed) {
					// PVP
					Messages.learn(killer, Messages.getString("mobhunting.learn.nopvp"));
					Messages.debug("KillBlocked: PVP not allowed. %s killed %s.", killer.getName(), killed.getName());
					return;
				}
			}
		}

		// Player killed a MythicMob
		if (MythicMobsCompat.isSupported() && killed.hasMetadata(MythicMobsCompat.MH_MYTHICMOBS)) {
			if (killer != null)
				Messages.debug("%s killed a MythicMob", killer.getName());
		}

		// Player killed a TARDISWeepingAngelMob
		if (TARDISWeepingAngelsCompat.isSupported() && TARDISWeepingAngelsCompat.isWeepingAngelMonster(killed)) {
			if (killer != null)
				Messages.debug("%s killed a TARDISWeepingAngelMob (%s)", killer.getName(),
						TARDISWeepingAngelsCompat.getWeepingAngelMonsterType(killed));
		}

		// Player killed a Stacked Mob
		if (MobStackerCompat.isSupported()) {
			if (MobStackerCompat.isStackedMob(killed)) {
				if (mConfig.getRewardFromStackedMobs) {
					if (killer != null) {
						Messages.debug("%s killed a stacked mob (%s) No=%s", killer.getName(),
								killed.getType().getName(), MobStackerCompat.getStackSize(killed));
						if (MobStackerCompat.killHoleStackOnDeath(killed) && MobStackerCompat.multiplyLoot()) {
							Messages.debug("Pay reward for no x mob");
						} else {
							// pay reward for one mob, if config allows
							Messages.debug("Pay reward for one mob");
						}
					}
				} else {
					Messages.debug("KillBlocked: Rewards from StackedMobs is disabled in Config.yml");
					return;
				}
			}
		}

		// Player killed a Citizens2 NPC
		if (killer != null && CitizensCompat.isNPC(killed) && CitizensCompat.isSentryOrSentinel(killed)) {
			Messages.debug("%s killed Sentinel or a Sentry npc-%s (name=%s)", killer.getName(),
					CitizensCompat.getNPCId(killed), CitizensCompat.getNPCName(killed));
		}

		// Player killed a mob while playing a minigame: MobArena, PVPVArena,
		// BattleArena
		// Player is in Godmode or Vanished
		// Player permission to Hunt (and get rewards)
		if (killer != null) {
			if (MobArenaCompat.isEnabledInConfig() && MobArenaHelper.isPlayingMobArena(killer)
					&& !mConfig.mobarenaGetRewards) {
				Messages.debug("KillBlocked: %s is currently playing MobArena.", killer.getName());
				Messages.learn(killer, Messages.getString("mobhunting.learn.mobarena"));
				return;
			} else if (PVPArenaCompat.isEnabledInConfig() && PVPArenaHelper.isPlayingPVPArena(killer)
					&& !mConfig.pvparenaGetRewards) {
				Messages.debug("KillBlocked: %s is currently playing PvpArena.", killer.getName());
				Messages.learn(killer, Messages.getString("mobhunting.learn.pvparena"));
				return;
			} else if (BattleArenaCompat.isEnabledInConfig() && BattleArenaHelper.isPlayingBattleArena(killer)) {
				Messages.debug("KillBlocked: %s is currently playing BattleArena.", killer.getName());
				Messages.learn(killer, Messages.getString("mobhunting.learn.battlearena"));
				return;
			} else if (EssentialsCompat.isSupported()) {
				if (EssentialsCompat.isGodModeEnabled(killer)) {
					Messages.debug("KillBlocked: %s is in God mode", killer.getName());
					Messages.learn(killer, Messages.getString("mobhunting.learn.godmode"));
					return;
				} else if (EssentialsCompat.isVanishedModeEnabled(killer)) {
					Messages.debug("KillBlocked: %s is in Vanished mode", killer.getName());
					Messages.learn(killer, Messages.getString("mobhunting.learn.vanished"));
					return;
				}
			} else if (VanishNoPacketCompat.isSupported()) {
				if (VanishNoPacketCompat.isVanishedModeEnabled(killer)) {
					Messages.debug("KillBlocked: %s is in Vanished mode", killer.getName());
					Messages.learn(killer, Messages.getString("mobhunting.learn.vanished"));
					return;
				}
			}

			if (!hasPermissionToKillMob(killer, killed)) {
				Messages.debug("KillBlocked: %s has not permission to kill %s.", killer.getName(),
						killed.getType().getName());
				Messages.learn(killer,
						Messages.getString("mobhunting.learn.no-permission", "killed-mob", killed.getType().getName()));
				return;
			}
		}

		// There is no reward and no penalty for this kill
		if (mConfig.getBaseKillPrize(event.getEntity()) == 0 && mConfig.getKillConsoleCmd(killed).equals("")) {
			// if (killed != null)
			Messages.debug("KillBlocked %s(%d): There is no reward and no penalty for this Mob/Player",
					killed.getType().getName(), killed.getEntityId());
			Messages.learn(killer,
					Messages.getString("mobhunting.learn.no-reward", "killed", killed.getType().getName()));
			return;
		}

		// The Mob/Player has MH:Blocked
		if (event.getEntity().hasMetadata("MH:blocked")) {
			if (killed != null) {
				Messages.debug("KillBlocked %s(%d): Mob has MH:blocked meta (probably spawned from a mob spawner)",
						event.getEntity().getType(), killed.getEntityId());
				Messages.learn(killer,
						Messages.getString("mobhunting.learn.mobspawner", "killed", killed.getType().getName()));
			}
			return;
		}

		// MobHunting is disabled for the player
		if (killer != null && !mMobHuntingManager.isHuntEnabled(killer)) {
			Messages.debug("KillBlocked %s: Hunting is disabled for player", killer.getName());
			Messages.learn(killer, Messages.getString("mobhunting.learn.huntdisabled"));
			return;
		}

		// The player is in Creative mode
		if (killer != null && killer.getGameMode() == GameMode.CREATIVE) {
			Messages.debug("KillBlocked %s: In creative mode", killer.getName());
			Messages.learn(killer, Messages.getString("mobhunting.learn.creative"));
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
			Messages.debug("KillBlocked %s: Last damage was too long ago (%s sec.)", killer.getName(),
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
						Messages.playerActionBarMessage(killer, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.undercover.message", "cause", killer.getName()));
					if (killed instanceof Player && !killed_muted)
						Messages.playerActionBarMessage((Player) killed, ChatColor.GREEN + "" + ChatColor.ITALIC
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
						Messages.playerActionBarMessage((Player) killed, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.coverblown.message", "damaged", killed.getName()));
					if (killer != null && !killer_muted)
						Messages.playerActionBarMessage(killer, ChatColor.GREEN + "" + ChatColor.ITALIC
								+ Messages.getString("bonus.coverblown.message", "damaged", killed.getName()));
				}
			}

		HuntData data;

		data = mMobHuntingManager.getHuntData(killer);

		// Killstreak
		mMobHuntingManager.handleKillstreak(killer);

		// Record kills that are still within a small area
		Location loc = killed.getLocation();

		// Grinding detection
		Area detectedGrindingArea = mAreaManager.getGrindingArea(loc);
		if (detectedGrindingArea == null)
			detectedGrindingArea = data.getGrindingArea(loc);
		// Slimes are except from grinding due to their splitting nature
		if (!(event.getEntity() instanceof Slime) && mConfig.penaltyGrindingEnable
				&& !killed.hasMetadata("MH:reinforcement") && !mAreaManager.isWhitelisted(killed.getLocation())) {
			Messages.debug("Checking if player is grinding mob in the same region within a range of %s blocks",
					data.getcDampnerRange());
			Messages.debug("DampendKills=%s", data.getDampenedKills());

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
									Messages.debug("Detected grinding. Killings too close, adding 1 to DampenedKills.");
									Messages.learn(killer, Messages.getString("mobhunting.learn.grindingnotallowed"));
									Messages.playerActionBarMessage(killer,
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
					Messages.playerActionBarMessage(killer,
							ChatColor.RED + Messages.getString("mobhunting.killstreak.lost"));
				data.setKillStreak(0);
			}
		}

		// Calculate basic the reward
		double cash = mConfig.getBaseKillPrize(killed);

		Messages.debug("Mob Basic Prize=%s for killing a %s", cash, killed.getType().getName());
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
					Messages.debug("Multiplier: %s = %s", mod.getName(), amt);
				}
			}
		}
		data.setReward(cash);

		Messages.debug("Killstreak=%s, level=%s, multiplier=%s ", data.getKillStreak(), data.getKillstreakLevel(),
				data.getKillstreakMultiplier());
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
			Messages.debug("This was a Pvp kill (killed=%s) no of bounties=%s", killed.getName(),
					mBountyManager.getAllBounties().size());
			OfflinePlayer wantedPlayer = (OfflinePlayer) killed;
			String worldGroupName = MobHunting.getWorldGroupManager().getCurrentWorldGroup(killer);
			if (BountyManager.hasBounties(worldGroupName, wantedPlayer)) {
				Set<Bounty> bounties = mBountyManager.getBounties(worldGroupName, wantedPlayer);
				for (Bounty b : bounties) {
					reward += b.getPrize();
					OfflinePlayer bountyOwner = b.getBountyOwner();
					mBountyManager.removeBounty(b);
					if (bountyOwner != null && bountyOwner.isOnline())
						Messages.playerActionBarMessage(Misc.getOnlinePlayer(bountyOwner),
								Messages.getString("mobhunting.bounty.bounty-claimed", "killer", killer.getName(),
										"prize", b.getPrize(), "killed", killed.getName()));
					b.setStatus(BountyStatus.completed);
					getDataStoreManager().updateBounty(b);
				}
				// OBS: Bounty will be added to the Reward for killing/Robbing
				// the player
				Messages.playerActionBarMessage(killer, Messages.getString("mobhunting.moneygain-for-killing", "money",
						mRewardManager.format(reward), "killed", killed.getName()));
				Messages.debug("%s got %s for killing %s", killer.getName(), reward, killed.getName());
				// TODO: call bounty event, and check if canceled.
				mRewardManager.depositPlayer(killer, reward);
				getDataStoreManager().recordKill(killer, ExtendedMobType.getExtendedMobType(killed),
						killed.hasMetadata("MH:hasBonus"));
			} else {
				Messages.debug("There is no Bounty on %s", killed.getName());
			}
		}

		// Calculate the reward
		// cash += reward;

		if ((cash >= getConfigManager().minimumReward) || (cash <= -getConfigManager().minimumReward)) {

			// Handle MobHuntKillEvent
			MobHuntKillEvent event2 = new MobHuntKillEvent(data, info, killed, killer);
			Bukkit.getPluginManager().callEvent(event2);
			if (event2.isCancelled()) {
				Messages.debug("KillBlocked %s: MobHuntKillEvent was cancelled", killer.getName());
				return;
			}

			// Handle reward on PVP kill. (Robbing)
			if (killer != null && killed instanceof Player && !CitizensCompat.isNPC(killed) && mConfig.robFromVictim) {
				mRewardManager.withdrawPlayer((Player) killed, cash);
				if (!killed_muted)
					Messages.playerActionBarMessage((Player) killed, ChatColor.RED + "" + ChatColor.ITALIC
							+ Messages.getString("mobhunting.moneylost", "prize", mRewardManager.format(cash)));
				Messages.debug("%s lost %s", killed.getName(), mRewardManager.format(cash));
			}

			// Reward/Penalty for assisted kill
			if (info.assister == null || mConfig.enableAssists == false) {
				if (cash > 0) {
					if (mConfig.dropMoneyOnGroup) {
						Rewards.dropMoneyOnGround(killed, cash);
						Messages.debug("%s was droped on the ground", mRewardManager.format(cash));
					} else {
						mRewardManager.depositPlayer(killer, cash);
						Messages.debug("%s got a reward (%s)", killer.getName(), mRewardManager.format(cash));
					}
				} else {
					mRewardManager.withdrawPlayer(killer, -cash);
					Messages.debug("%s got a penalty (%s)", killer.getName(), mRewardManager.format(cash));
				}
			} else {
				cash = cash / 2;
				if (cash > 0) {
					if (mConfig.dropMoneyOnGroup) {
						Rewards.dropMoneyOnGround(killed, cash);
						Messages.debug("%s was droped on the ground", mRewardManager.format(cash));
					} else {
						mRewardManager.depositPlayer(killer, cash);
						onAssist(info.assister, killer, killed, info.lastAssistTime);
						Messages.debug("%s got a ½ reward (%s)", killer.getName(), mRewardManager.format(cash));
					}
				} else {
					mRewardManager.withdrawPlayer(killer, -cash);
					onAssist(info.assister, killer, killed, info.lastAssistTime);
					Messages.debug("%s got a ½ penalty (%s)", killer.getName(), mRewardManager.format(cash));
				}
			}

			// Record the kill in the Database
			if (killer != null)
				Messages.debug("RecordKill: %s killed a %s", killer.getName(),
						ExtendedMobType.getExtendedMobType(killed));
			// TODO: record MyythicMobs kills as its own kind of mobs
			// TODO: record TARDISWeepingAngels kills as its own kind of mobs
			if (ExtendedMobType.getExtendedMobType(killed) != null)
				getDataStoreManager().recordKill(killer, ExtendedMobType.getExtendedMobType(killed),
						killed.hasMetadata("MH:hasBonus"));

			// Tell the player that he got the reward/penalty, unless muted
			if (!killer_muted)

				if (extraString.trim().isEmpty()) {
					if (cash > 0)
						if (!getConfigManager().dropMoneyOnGroup)
							Messages.playerActionBarMessage(killer, ChatColor.GREEN + "" + ChatColor.ITALIC
									+ Messages.getString("mobhunting.moneygain", "prize", mRewardManager.format(cash)));
						else
							Messages.playerActionBarMessage(killer, ChatColor.RED + "" + ChatColor.ITALIC
									+ Messages.getString("mobhunting.moneylost", "prize", mRewardManager.format(cash)));

				} else {
					if (cash > 0)
						Messages.playerActionBarMessage(killer,
								ChatColor.GREEN + "" + ChatColor.ITALIC
										+ Messages.getString("mobhunting.moneygain.bonuses", "prize",
												mRewardManager.format(cash), "bonuses", extraString.trim()));
					else
						Messages.playerActionBarMessage(killer,
								ChatColor.RED + "" + ChatColor.ITALIC
										+ Messages.getString("mobhunting.moneylost.bonuses", "prize",
												mRewardManager.format(cash), "bonuses", extraString.trim()));
				}
		} else
			Messages.debug("KillBlocked %s: Gained money was less than 1 cent (grinding or penalties) (%s)",
					killer.getName(), extraString);

		// Run console commands as a reward
		if (data.getDampenedKills() < 10) {
			if (mConfig.isCmdGointToBeExcuted(killed)) {
				String worldname = killer.getWorld().getName();
				String killerpos = killer.getLocation().getBlockX() + " " + killer.getLocation().getBlockY() + " "
						+ killer.getLocation().getBlockZ();
				String killedpos = killed.getLocation().getBlockX() + " " + killed.getLocation().getBlockY() + " "
						+ killed.getLocation().getBlockZ();
				String prizeCommand = mConfig.getKillConsoleCmd(killed).replaceAll("\\{player\\}", killer.getName())
						.replaceAll("\\{killer\\}", killer.getName()).replaceAll("\\{world\\}", worldname)
						.replace("\\{prize\\}", mRewardManager.format(cash)).replaceAll("\\{killerpos\\}", killerpos)
						.replaceAll("\\{killedpos\\}", killedpos);
				if (killed instanceof Player)
					prizeCommand = prizeCommand.replaceAll("\\{killed_player\\}", killed.getName())
							.replaceAll("\\{killed\\}", killed.getName());
				else
					prizeCommand = prizeCommand.replaceAll("\\{killed_player\\}", killed.getType().getName())
							.replaceAll("\\{killed\\}", killed.getType().getName());
				Messages.debug("command to be run is:" + prizeCommand);
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
					String message = ChatColor.GREEN + "" + ChatColor.ITALIC + mConfig.getKillRewardDescription(killed)
							.replaceAll("\\{player\\}", killer.getName()).replaceAll("\\{killer\\}", killer.getName())
							.replace("\\{prize\\}", mRewardManager.format(cash)).replaceAll("\\{world\\}", worldname)
							.replaceAll("\\{killerpos\\}", killerpos).replaceAll("\\{killedpos\\}", killedpos);

					if (killed instanceof Player)
						message = message.replaceAll("\\{killed_player\\}", killed.getName()).replaceAll("\\{killed\\}",
								killed.getName());
					else
						message = message.replaceAll("\\{killed_player\\}", killed.getType().getName())
								.replaceAll("\\{killed\\}", killed.getType().getName());
					Messages.debug("Description to be send:" + message);

					Messages.playerActionBarMessage(killer, message);
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
			ks = mMobHuntingManager.handleKillstreak(player);

		multiplier *= ks;
		double cash = 0;
		if (killed instanceof Player)
			cash = mConfig.getBaseKillPrize(killed) * multiplier / 2;
		else
			cash = mConfig.getBaseKillPrize(killed) * multiplier;

		if (cash >= 0.01) {
			ExtendedMobType mob = ExtendedMobType.getExtendedMobType(killed);
			if (mob != null) {
				getDataStoreManager().recordAssist(player, killer, mob, killed.hasMetadata("MH:hasBonus"));
				mRewardManager.depositPlayer(player, cash);
				Messages.debug("%s got a on assist reward (%s)", player.getName(), mRewardManager.format(cash));

				if (ks != 1.0)
					Messages.playerActionBarMessage(player, ChatColor.GREEN + "" + ChatColor.ITALIC
							+ Messages.getString("mobhunting.moneygain.assist", "prize", mRewardManager.format(cash)));
				else
					Messages.playerActionBarMessage(player,
							ChatColor.GREEN + "" + ChatColor.ITALIC
									+ Messages.getString("mobhunting.moneygain.assist.bonuses", "prize",
											mRewardManager.format(cash), "bonuses", String.format("x%.1f", ks)));
			} else {
				player.sendMessage(
						"[MobHunting] Error - please contact server owner and ask him to check the serverlog.");
				MobHunting.getInstance().getLogger().warning("[MobHunting] WARNING - Assisted kill of a "
						+ killed.getType() + ". Cant handle the entity type!");
			}

		}
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

		if (getMobHuntingManager().mRand.nextDouble() * 100 < mConfig.bonusMobChance) {
			mParticleManager.attachEffect(event.getEntity(), Effect.MOBSPAWNER_FLAMES);
			if (getMobHuntingManager().mRand.nextBoolean())
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
		String permission_postfix = "*";
		if (TARDISWeepingAngelsCompat.isSupported() && TARDISWeepingAngelsCompat.isWeepingAngelMonster(mob)) {
			permission_postfix = TARDISWeepingAngelsCompat.getWeepingAngelMonsterType(mob).name();
			if (player.isPermissionSet("mobhunting.mobs." + permission_postfix))
				return player.hasPermission("mobhunting.mobs." + permission_postfix);
			else {
				Messages.debug("Permission mobhunting.mobs." + permission_postfix + " not set, defaulting to True.");
				return true;
			}
		} else if (MythicMobsCompat.isSupported() && MythicMobsCompat.isMythicMob(mob)) {
			permission_postfix = MythicMobsCompat.getMythicMobType(mob);
			if (player.isPermissionSet("mobhunting.mobs." + permission_postfix))
				return player.hasPermission("mobhunting.mobs." + permission_postfix);
			else {
				Messages.debug("Permission mobhunting.mobs." + permission_postfix + " not set, defaulting to True.");
				return true;
			}
		} else if (CitizensCompat.isSupported() && CitizensCompat.isSentryOrSentinel(mob)) {
			permission_postfix = "npc-" + CitizensCompat.getNPCId(mob);
			if (player.isPermissionSet("mobhunting.mobs." + permission_postfix))
				return player.hasPermission("mobhunting.mobs." + permission_postfix);
			else {
				Messages.debug("Permission mobhunting.mobs.'" + permission_postfix + "' not set, defaulting to True.");
				return true;
			}
		} else {
			permission_postfix = mob.getType().toString();
			if (player.isPermissionSet("mobhunting.mobs." + permission_postfix))
				return player.hasPermission("mobhunting.mobs." + permission_postfix);
			else {
				Messages.debug("Permission 'mobhunting.mobs.*' or 'mobhunting.mobs." + permission_postfix
						+ "' not set, defaulting to True.");
				return true;
			}
		}
	}

}
