package one.lindegaard.BagOfGold;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import one.lindegaard.BagOfGold.bank.BankManager;
import one.lindegaard.BagOfGold.bank.BankSign;
import one.lindegaard.BagOfGold.commands.BankCommand;
import one.lindegaard.BagOfGold.commands.CommandDispatcher;
import one.lindegaard.BagOfGold.commands.ConvertCommand;
import one.lindegaard.BagOfGold.commands.DebugCommand;
import one.lindegaard.BagOfGold.commands.MoneyCommand;
import one.lindegaard.BagOfGold.commands.MuteCommand;
import one.lindegaard.BagOfGold.commands.NpcCommand;
import one.lindegaard.BagOfGold.commands.ReloadCommand;
import one.lindegaard.BagOfGold.commands.UpdateCommand;
import one.lindegaard.BagOfGold.commands.VersionCommand;
import one.lindegaard.BagOfGold.compatibility.ActionAnnouncerCompat;
import one.lindegaard.BagOfGold.compatibility.ActionBarAPICompat;
import one.lindegaard.BagOfGold.compatibility.ActionbarCompat;
import one.lindegaard.BagOfGold.compatibility.BarAPICompat;
import one.lindegaard.BagOfGold.compatibility.BossBarAPICompat;
import one.lindegaard.BagOfGold.compatibility.CMICompat;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.Core.compatibility.CompatPlugin;
import one.lindegaard.BagOfGold.compatibility.CompatibilityManager;
import one.lindegaard.BagOfGold.compatibility.EssentialsCompat;
import one.lindegaard.BagOfGold.compatibility.PerWorldInventoryCompat;
import one.lindegaard.BagOfGold.compatibility.PlaceholderAPICompat;
import one.lindegaard.BagOfGold.compatibility.ProtocolLibCompat;
import one.lindegaard.BagOfGold.compatibility.TitleAPICompat;
import one.lindegaard.BagOfGold.compatibility.TitleManagerCompat;
import one.lindegaard.BagOfGold.config.ConfigManager;
import one.lindegaard.BagOfGold.rewards.BagOfGoldItems;
import one.lindegaard.BagOfGold.rewards.RewardManager;
import one.lindegaard.BagOfGold.rewards.GringottsItems;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.DataStoreManager;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.BagOfGold.storage.MySQLDataStore;
import one.lindegaard.BagOfGold.storage.SQLiteDataStore;
import one.lindegaard.BagOfGold.update.SpigetUpdater;
import one.lindegaard.Core.Server.Servers;
import one.lindegaard.Core.WorldGroupManager;
import one.lindegaard.Core.Messages.MessageManager;

public class BagOfGold extends JavaPlugin {

	private static BagOfGold instance;
	private File mFile = new File(getDataFolder(), "config.yml");

	private Messages mMessages;
	private EconomyManager mEconomyManager;
	private MetricsManager mMetricsManager;
	private ConfigManager mConfig;
	private CommandDispatcher mCommandDispatcher;
	private PlayerSettingsManager mPlayerSettingsManager;
	private IDataStore mStore;
	private DataStoreManager mStoreManager;
	private RewardManager mRewardManager;
	private WorldGroupManager mWorldGroupManager;
	private CompatibilityManager mCompatibilityManager;
	private BankManager mBankManager;
	private SpigetUpdater mSpigetUpdater;
	private PlayerBalanceManager mPlayerBalanceManager;
	private GringottsItems mGringottsItems;
	private BagOfGoldItems mBagOfGoldItems;
	private MessageManager mMessageManager;

	private boolean mInitialized = false;

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {

		instance = this;

		mMessages = new Messages(this);
		mConfig = new ConfigManager(this, mFile);

		if (mConfig.loadConfig()) {
			if (mConfig.backup)
				mConfig.backupConfig(mFile);
			mConfig.saveConfig();
		} else
			throw new RuntimeException(instance.getMessages().getString("bagofgold.config.fail"));

		if (isbStatsEnabled())
			instance.getMessages().debug("bStat is enabled");
		else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "=====================WARNING=============================");
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.RED + "The statistics collection is disabled. As developer I need the");
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.RED + "statistics from bStats.org. The statistics is 100% anonymous.");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "https://bstats.org/plugin/bukkit/bagofgold");
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.RED + "Please enable this in /plugins/bStats/config.yml and get rid of this");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "message. Loading will continue in 15 sec.");
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "=========================================================");
			long now = System.currentTimeMillis();
			while (System.currentTimeMillis() < now + 15000L) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}

		mWorldGroupManager = new WorldGroupManager(this);
		mWorldGroupManager.load();

		mSpigetUpdater = new SpigetUpdater(this);
		mSpigetUpdater.setCurrentJarFile(this.getFile().getName());

		// Register commands
		mCommandDispatcher = new CommandDispatcher(this, "bagofgold",
				instance.getMessages().getString("bagofgold.command.base.description") + getDescription().getVersion());
		getCommand("bagofgold").setExecutor(mCommandDispatcher);
		getCommand("bagofgold").setTabCompleter(mCommandDispatcher);
		mCommandDispatcher.registerCommand(new ReloadCommand(this));
		mCommandDispatcher.registerCommand(new NpcCommand(this));
		mCommandDispatcher.registerCommand(new UpdateCommand(this));
		mCommandDispatcher.registerCommand(new VersionCommand(this));
		mCommandDispatcher.registerCommand(new DebugCommand(this));
		mCommandDispatcher.registerCommand(new ConvertCommand(this));
		mCommandDispatcher.registerCommand(new MoneyCommand(this));
		mCommandDispatcher.registerCommand(new BankCommand(this));
		mCommandDispatcher.registerCommand(new MuteCommand(this));

		// Check for new BagOfGold updates
		mSpigetUpdater.hourlyUpdateCheck(getServer().getConsoleSender(), mConfig.updateCheck, false);

		if (mConfig.databaseType.equalsIgnoreCase("mysql"))
			mStore = new MySQLDataStore(this);
		else
			mStore = new SQLiteDataStore(this);

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

		mStoreManager = new DataStoreManager(this, mStore);

		mPlayerSettingsManager = new PlayerSettingsManager(this);
		mPlayerBalanceManager = new PlayerBalanceManager(this);

		mRewardManager = new RewardManager(this);

		mCompatibilityManager = new CompatibilityManager(this);

		mBankManager = new BankManager(this);

		mCompatibilityManager.registerPlugin(ProtocolLibCompat.class, CompatPlugin.ProtocolLib);
		mCompatibilityManager.registerPlugin(PerWorldInventoryCompat.class, CompatPlugin.PerWorldInventory);
		if (Servers.isSpigotServer() || Servers.isPaperServer())
			mCompatibilityManager.registerPlugin(CitizensCompat.class, CompatPlugin.Citizens);
		mCompatibilityManager.registerPlugin(EssentialsCompat.class, CompatPlugin.Essentials);

		mCompatibilityManager.registerPlugin(TitleManagerCompat.class, CompatPlugin.TitleManager);
		mCompatibilityManager.registerPlugin(TitleAPICompat.class, CompatPlugin.TitleAPI);
		mCompatibilityManager.registerPlugin(ActionAnnouncerCompat.class, CompatPlugin.ActionAnnouncer);
		mCompatibilityManager.registerPlugin(ActionBarAPICompat.class, CompatPlugin.ActionBarApi);
		mCompatibilityManager.registerPlugin(ActionbarCompat.class, CompatPlugin.Actionbar);
		mCompatibilityManager.registerPlugin(BossBarAPICompat.class, CompatPlugin.BossBarApi);
		mCompatibilityManager.registerPlugin(BarAPICompat.class, CompatPlugin.BarApi);
		mCompatibilityManager.registerPlugin(CMICompat.class, CompatPlugin.CMI);

		mCompatibilityManager.registerPlugin(PlaceholderAPICompat.class, CompatPlugin.PlaceholderAPI);

		if (!Servers.isGlowstoneServer()) {
			mMetricsManager = new MetricsManager(this);
			mMetricsManager.startBStatsMetrics();
		}

		// Initialize BagOfGold Bank Signs
		new BankSign(this);

		// start the Economy Service Provider using Vault or Reserve
		mEconomyManager = new EconomyManager(this);

		if (PerWorldInventoryCompat.isSupported() && PerWorldInventoryCompat.pwi_sync_economy())
			PerWorldInventoryCompat.pwi_sync_economy_warning();

		mGringottsItems = new GringottsItems(this);
		mBagOfGoldItems = new BagOfGoldItems(this);

		mRewardManager.loadAllStoredRewards();
		
		setEnabled(mInitialized);

	}

	@Override
	public void onDisable() {
		if (!mInitialized)
			return;

		mBankManager.shutdown();
		
		getMessages().debug("Saving all Reward Blocks to disk");
		mRewardManager.saveAllRewards();

		try {
			getMessages().debug("Shutdown StoreManager");
			mStoreManager.shutdown();
			getMessages().debug("Shutdown Store");
			mStore.shutdown();
		} catch (DataStoreException e) {
			e.printStackTrace();
		}

		instance.getMessages().debug("BagOfGold disabled.");
	}

	private boolean isbStatsEnabled() {
		File bStatsFolder = new File(instance.getDataFolder().getParentFile(), "bStats");
		File configFile = new File(bStatsFolder, "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		return config.getBoolean("enabled", true);
	}

	// ************************************************************************************
	// Managers and handlers
	// ************************************************************************************
	public static BagOfGold getInstance() {
		return instance;
	}

	public static BagOfGold getAPI() {
		return instance;
	}

	@Deprecated
	public static BagOfGold getApi() {
		return instance;
	}

	public ConfigManager getConfigManager() {
		return mConfig;
	}

	/**
	 * Get the MessagesManager
	 * 
	 * @return
	 */
	public Messages getMessages() {
		return mMessages;
	}

	/**
	 * setMessages
	 * 
	 * @param messages
	 */
	public void setMessages(Messages messages) {
		mMessages = messages;
	}

	public CommandDispatcher getCommandDispatcher() {
		return mCommandDispatcher;
	}

	/**
	 * Gets the Store Manager
	 * 
	 * @return
	 */
	public IDataStore getStoreManager() {
		return mStore;
	}

	/**
	 * Gets the Database Store Manager
	 * 
	 * @return
	 */
	public DataStoreManager getDataStoreManager() {
		return mStoreManager;
	}

	/**
	 * Get the PlayerSettingsManager
	 * 
	 * @return
	 */
	public PlayerSettingsManager getPlayerSettingsManager() {
		return mPlayerSettingsManager;
	}

	/**
	 * Get the EconomyManager
	 * 
	 * @return
	 */
	public RewardManager getRewardManager() {
		return mRewardManager;
	}

	public BankManager getBankManager() {
		return mBankManager;
	}

	public CompatibilityManager getCompatibilityManager() {
		return mCompatibilityManager;
	}

	public SpigetUpdater getSpigetUpdater() {
		return mSpigetUpdater;
	}

	/**
	 * Get all WorldGroups and their worlds
	 * 
	 * @return
	 */
	public WorldGroupManager getWorldGroupManager() {
		return mWorldGroupManager;
	}

	public PlayerBalanceManager getPlayerBalanceManager() {
		return mPlayerBalanceManager;
	}

	public GringottsItems getGringottsItems() {
		return mGringottsItems;
	}

	public BagOfGoldItems getBagOfGoldItems() {
		return mBagOfGoldItems;
	}

	public MessageManager getMessageManager() {
		return mMessageManager;
	}

	public EconomyManager getEconomyManager() {
		return mEconomyManager;
	}

}
