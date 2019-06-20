package one.lindegaard.BagOfGold;

import java.io.File;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;
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
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.BagOfGold.compatibility.CompatPlugin;
import one.lindegaard.BagOfGold.compatibility.CompatibilityManager;
import one.lindegaard.BagOfGold.compatibility.EssentialsCompat;
import one.lindegaard.BagOfGold.compatibility.PerWorldInventoryCompat;
import one.lindegaard.BagOfGold.compatibility.PlaceholderAPICompat;
import one.lindegaard.BagOfGold.compatibility.TitleAPICompat;
import one.lindegaard.BagOfGold.compatibility.TitleManagerCompat;
import one.lindegaard.BagOfGold.config.ConfigManager;
import one.lindegaard.BagOfGold.rewards.BagOfGoldItems;
import one.lindegaard.BagOfGold.rewards.EconomyManager;
import one.lindegaard.BagOfGold.rewards.GringottsItems;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.DataStoreManager;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.BagOfGold.storage.MySQLDataStore;
import one.lindegaard.BagOfGold.storage.SQLiteDataStore;
import one.lindegaard.BagOfGold.update.SpigetUpdater;
import one.lindegaard.Core.Server.Servers;
import one.lindegaard.Core.WorldGroupManager;

public class BagOfGold extends JavaPlugin {

	private static BagOfGold instance;
	private File mFile = new File(getDataFolder(), "config.yml");

	private Messages mMessages;
	private MetricsManager mMetricsManager;
	private ConfigManager mConfig;
	private CommandDispatcher mCommandDispatcher;
	private static ServicesManager mServiceManager;
	private PlayerSettingsManager mPlayerSettingsManager;
	private IDataStore mStore;
	private DataStoreManager mStoreManager;
	private EconomyManager mEconomyManager;
	private WorldGroupManager mWorldGroupManager;
	private CompatibilityManager mCompatibilityManager;
	private BankManager mBankManager;
	private SpigetUpdater mSpigetUpdater;
	private PlayerBalanceManager mPlayerBalanceManager;
	private GringottsItems mGringottsItems;
	private BagOfGoldItems mBagOfGoldItems;

	private boolean mInitialized = false;

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {

		// Plugin bagOfGoldCorePlugin =
		// Bukkit.getPluginManager().getPlugin("BagOfGoldCore");
		// if (bagOfGoldCorePlugin == null) {
		// Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " +
		// ChatColor.GREEN
		// + "BagOfGoldCore is missing. BagOfGold is dependend on BagGoldCore. It Will
		// now be downloaded. Restart your server when downloading has finished.");
		// SpigetUpdaterForced.setCurrentJarFile(this.getFile().getName());
		// SpigetUpdaterForced.ForceDownloadJar(this);
		// }

		instance = this;

		mServiceManager = Bukkit.getServicesManager();
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

		mEconomyManager = new EconomyManager(this);

		mCompatibilityManager = new CompatibilityManager(this);

		mBankManager = new BankManager(this);

		mCompatibilityManager.registerPlugin(PerWorldInventoryCompat.class, CompatPlugin.PerWorldInventory);
		if (Servers.isSpigotServer() || Servers.isPaperServer())
			mCompatibilityManager.registerPlugin(CitizensCompat.class, CompatPlugin.Citizens);
		mCompatibilityManager.registerPlugin(EssentialsCompat.class, CompatPlugin.Essentials);

		mCompatibilityManager.registerPlugin(TitleManagerCompat.class, CompatPlugin.TitleManager);
		mCompatibilityManager.registerPlugin(TitleAPICompat.class, CompatPlugin.TitleAPI);
		mCompatibilityManager.registerPlugin(ActionAnnouncerCompat.class, CompatPlugin.ActionAnnouncer);
		mCompatibilityManager.registerPlugin(ActionBarAPICompat.class, CompatPlugin.ActionBarApi);
		mCompatibilityManager.registerPlugin(ActionbarCompat.class, CompatPlugin.Actionbar);

		mCompatibilityManager.registerPlugin(PlaceholderAPICompat.class, CompatPlugin.PlaceholderAPI);

		if (!Servers.isGlowstoneServer()) {
			mMetricsManager = new MetricsManager(this);
			//mMetricsManager.start();
			mMetricsManager.startBStatsMetrics();
		}

		// Initialize BagOfGold Bank Signs
		new BankSign(this);

		if (mConfig.useBagOfGoldAsAnEconomyPlugin) {
			// Try to load BagOfGold
			hookEconomy(Economy_BagOfGold.class, ServicePriority.Normal, "one.lindegaard.BagOfGold.BagOfGoldEconomy");
		}

		if (PerWorldInventoryCompat.isSupported() && PerWorldInventoryCompat.pwi_sync_economy())
			PerWorldInventoryCompat.pwi_sync_economy_warning();

		if (!Servers.isMC113OrNewer())
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
					+ " version +2.0.0 is only for Minecraft 1.13! You should downgrade to 1.x");

		// Get random UUI>>>D's
		// for (int n = 0; n < 3; n++) {
		// getMessages().debug("UUID=%s", UUID.randomUUID().toString());
		// }

		mGringottsItems = new GringottsItems(this);
		mBagOfGoldItems = new BagOfGoldItems(this);
		mInitialized = true;

		// setEnabled(mInitialized);

	}

	@Override
	public void onDisable() {
		if (!mInitialized)
			return;

        mBankManager.shutdown();
        
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
	// Hook into Vault / Economy
	// ************************************************************************************

	public static void hookEconomy(Class<? extends Economy> hookClass, ServicePriority priority, String... packages) {
		try {
			if (packagesExists(packages)) {
				Economy economy = hookClass.getConstructor(Plugin.class).newInstance(BagOfGold.getInstance());
				mServiceManager.register(Economy.class, economy, Bukkit.getPluginManager().getPlugin("Vault"),
						ServicePriority.Normal);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET + String.format(
						"[BagOfGold][Economy] BagOfGold found: %s", economy.isEnabled() ? "Loaded" : "Waiting"));
			}
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET + String.format(
					"[Economy] There was an error hooking BagOfGold - check to make sure you're using a compatible version!"));
		}
	}

	/**
	 * Determines if all packages in a String array are within the Classpath This is
	 * the best way to determine if a specific plugin exists and will be loaded. If
	 * the plugin package isn't loaded, we shouldn't bother waiting for it!
	 * 
	 * @param packages String Array of package names to check
	 * @return Success or Failure
	 */
	private static boolean packagesExists(String... packages) {
		try {
			for (String pkg : packages) {
				Class.forName(pkg);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
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
	public EconomyManager getEconomyManager() {
		return mEconomyManager;
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

	public GringottsItems getgringottsItems() {
		return mGringottsItems;
	}

	public BagOfGoldItems getBagOfGoldItems() {
		return mBagOfGoldItems;
	}

}
