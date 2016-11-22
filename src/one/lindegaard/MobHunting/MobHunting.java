package one.lindegaard.MobHunting;

import java.io.File;
import one.lindegaard.MobHunting.achievements.*;
import one.lindegaard.MobHunting.bounty.BountyManager;
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
import one.lindegaard.MobHunting.compatibility.BossBarAPICompat;
import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.CompatibilityManager;
import one.lindegaard.MobHunting.compatibility.CustomMobsCompat;
import one.lindegaard.MobHunting.compatibility.DisguiseCraftCompat;
import one.lindegaard.MobHunting.compatibility.EssentialsCompat;
import one.lindegaard.MobHunting.compatibility.GringottsCompat;
import one.lindegaard.MobHunting.compatibility.IDisguiseCompat;
import one.lindegaard.MobHunting.compatibility.LibsDisguisesCompat;
import one.lindegaard.MobHunting.compatibility.MinigamesCompat;
import one.lindegaard.MobHunting.compatibility.MobArenaCompat;
import one.lindegaard.MobHunting.compatibility.MobStackerCompat;
import one.lindegaard.MobHunting.compatibility.MyPetCompat;
import one.lindegaard.MobHunting.compatibility.MythicMobsCompat;
import one.lindegaard.MobHunting.compatibility.PVPArenaCompat;
import one.lindegaard.MobHunting.compatibility.TARDISWeepingAngelsCompat;
import one.lindegaard.MobHunting.compatibility.TitleAPICompat;
import one.lindegaard.MobHunting.compatibility.TitleManagerCompat;
import one.lindegaard.MobHunting.compatibility.VanishNoPacketCompat;
import one.lindegaard.MobHunting.compatibility.WorldEditCompat;
import one.lindegaard.MobHunting.compatibility.WorldGuardCompat;
import one.lindegaard.MobHunting.leaderboard.LeaderboardManager;
import one.lindegaard.MobHunting.mobs.ExtendedMobManager;
import one.lindegaard.MobHunting.rewards.RewardManager;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.storage.DataStoreManager;
import one.lindegaard.MobHunting.storage.IDataStore;
import one.lindegaard.MobHunting.storage.MySQLDataStore;
import one.lindegaard.MobHunting.storage.SQLiteDataStore;
import one.lindegaard.MobHunting.update.UpdateHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

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
	private static ExtendedMobManager mExtendedMobManager;
	private static IDataStore mStore;
	private static DataStoreManager mStoreManager;
	private static ConfigManager mConfig;

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

			UpdateHelper.setCurrentJarFile(this.getFile().getName());

			mStoreManager = new DataStoreManager(mStore);

			mPlayerSettingsManager = new PlayerSettingsManager();

			// Handle compatability stuff
			registerPlugin(EssentialsCompat.class, "Essentials");
			registerPlugin(GringottsCompat.class, "Gringotts");

			registerPlugin(WorldEditCompat.class, "WorldEdit");
			registerPlugin(WorldGuardCompat.class, "WorldGuard");
			registerPlugin(MyPetCompat.class, "MyPet");

			registerPlugin(MinigamesCompat.class, "Minigames");
			registerPlugin(MobArenaCompat.class, "MobArena");
			registerPlugin(PVPArenaCompat.class, "PVPArena");
			registerPlugin(BattleArenaCompat.class, "BattleArena");

			registerPlugin(LibsDisguisesCompat.class, "LibsDisguises");
			registerPlugin(DisguiseCraftCompat.class, "DisguiseCraft");
			registerPlugin(IDisguiseCompat.class, "iDisguise");
			registerPlugin(VanishNoPacketCompat.class, "VanishNoPacket");

			registerPlugin(BossBarAPICompat.class, "BossBarAPI");
			registerPlugin(TitleAPICompat.class, "TitleAPI");
			registerPlugin(BarAPICompat.class, "BarAPI");
			registerPlugin(TitleManagerCompat.class, "TitleManager");
			registerPlugin(ActionbarCompat.class, "Actionbar");
			registerPlugin(ActionBarAPICompat.class, "ActionBarAPI");
			registerPlugin(ActionAnnouncerCompat.class, "ActionAnnouncer");

			registerPlugin(CitizensCompat.class, "Citizens");
			registerPlugin(MythicMobsCompat.class, "MythicMobs");
			registerPlugin(TARDISWeepingAngelsCompat.class, "TARDISWeepingAngels");
			registerPlugin(CustomMobsCompat.class, "CustomMobs");
			registerPlugin(MobStackerCompat.class, "MobStacker");

			mExtendedMobManager = new ExtendedMobManager();

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

			getMobHuntingManager().registerModifiers();

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

			Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				public void run() {
					Messages.injectMissingMobNamesToLangFiles();
				}
			}, 20 * 5); // 20ticks/sec * 3 sec.

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

		getMobHuntingManager().getModifiers().clear();

		try {
			mStoreManager.shutdown();
			mStore.shutdown();
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
		CitizensCompat.shutdown();
		mWorldGroupManager.save();
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

	/**
	 * Get the ParticleManager
	 * 
	 * @return
	 */
	public static ParticleManager getParticleManager() {
		return mParticleManager;
	}

	/**
	 * Get the MobManager
	 * 
	 * @return
	 */
	public static ExtendedMobManager getExtendedMobManager() {
		return mExtendedMobManager;
	}

}
