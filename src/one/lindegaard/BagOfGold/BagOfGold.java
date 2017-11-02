package one.lindegaard.BagOfGold;

import java.io.File;

import one.lindegaard.BagOfGold.commands.CommandDispatcher;
import one.lindegaard.BagOfGold.commands.DebugCommand;
import one.lindegaard.BagOfGold.commands.ReloadCommand;
import one.lindegaard.BagOfGold.commands.UpdateCommand;
import one.lindegaard.BagOfGold.commands.VersionCommand;
import one.lindegaard.BagOfGold.update.Updater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BagOfGold extends JavaPlugin {

	// Constants
	private final static String pluginName = "bagofgold";

	private static BagOfGold instance;

	private Messages mMessages;
	private MetricsManager mMetricsManager;
	private static ConfigManager mConfig;
	private CommandDispatcher mCommandDispatcher;

	private boolean mInitialized = false;

	@Override
	public void onLoad() {
	}

	@Override
	public void onEnable() {

		instance = this;

		mMessages = new Messages(this);
		mMessages.exportDefaultLanguages(this);

		mConfig = new ConfigManager(new File(getDataFolder(), "config.yml"));

		if (!mConfig.loadConfig())
			throw new RuntimeException(Messages.getString(pluginName + ".config.fail"));

		if (isbStatsEnabled())
			Messages.debug("bStat is enabled");
		else {
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.RED + "[BagOfGold]=====================WARNING=============================");
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.RED + "The statistics collection is disabled. As developer I need the");
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.RED + "statistics from bStats.org. The statistics is 100% anonymous.");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "https://bstats.org/plugin/bukkit/bagofgold");
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.RED + "Please enable this in /plugins/bStats/config.yml and get rid of this");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "message. Loading will continue in 15 sec.");
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.RED + "[BagOfGold]=========================================================");
			long now = System.currentTimeMillis();
			while (System.currentTimeMillis() < now + 15000L) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}

		Updater.setCurrentJarFile(this.getFile().getName());

		// Register commands
		mCommandDispatcher = new CommandDispatcher(this, "bagofgold",
				Messages.getString("bagofgold.command.base.description") + getDescription().getVersion());
		getCommand("bagofgold").setExecutor(mCommandDispatcher);
		getCommand("bagofgold").setTabCompleter(mCommandDispatcher);
		mCommandDispatcher.registerCommand(new ReloadCommand(this));
		mCommandDispatcher.registerCommand(new UpdateCommand(this));
		mCommandDispatcher.registerCommand(new VersionCommand(this));
		mCommandDispatcher.registerCommand(new DebugCommand(this));

		// Check for new MobHuntig updates
		Updater.hourlyUpdateCheck(getServer().getConsoleSender(), mConfig.updateCheck, false);

		if (!getServer().getName().toLowerCase().contains("glowstone")) {
			mMetricsManager = new MetricsManager(this);
			mMetricsManager.startMetrics();

			mMetricsManager.startBStatsMetrics();
		}

		mInitialized = true;

	}

	@Override
	public void onDisable() {
		if (!mInitialized)
			return;
		Messages.debug("BagOfGold disabled.");
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

	public static ConfigManager getConfigManager() {
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

	public CommandDispatcher getCommandDispatcher() {
		return mCommandDispatcher;
	}

}
