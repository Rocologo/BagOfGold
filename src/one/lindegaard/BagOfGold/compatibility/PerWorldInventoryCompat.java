package one.lindegaard.BagOfGold.compatibility;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.compatibility.CompatPlugin;

public class PerWorldInventoryCompat {

	BagOfGold plugin;
	private static Plugin mPlugin;
	private static boolean supported = false;
	private static boolean sync_economy = false;

	public PerWorldInventoryCompat() {
		plugin = BagOfGold.getInstance();
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "Compatibility with PerWorldInventory is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.PerWorldInventory.getName());

			if (mPlugin.getDescription().getVersion().compareTo("1.7.6") >= 0) {
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
								+ "Enabling compatibility with PerWorldInventory ("
								+ getPWI().getDescription().getVersion() + ")");

				sync_economy = pwi_sync_economy();

				if (sync_economy)
					pwi_sync_economy_warning();
				
				//if (mPlugin.getDescription().getVersion().compareTo("2.1.0") >= 0)
				//	Bukkit.getPluginManager().registerEvents(new Listener() {
				//		@EventHandler(priority = EventPriority.HIGHEST)
				//		public void onInventoryChangeCompleted(InventoryLoadCompleteEvent event) {
				//			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
				//				@Override
				//				public void run() {
				//					plugin.getMessages().debug("PerWorldInventoryCompat: onInventoryLoadCompleted");
				//					plugin.getEconomyManager().adjustAmountInInventoryToBalance(event.getPlayer());
				//				}
				//			}, 20);
				//		}
				//	}, plugin);
				//else
				if (mPlugin.getDescription().getVersion().compareTo("2.0") >= 0)
				PerWorldInventoryHelper.registerPWIEvents(plugin);
				
				supported = true;

			} else {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
						+ "You are using an old version and unsupported version of PerWorldInventory. Integration to PerWorldInventory is disabled");
			}

		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getPWI() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean pwi_sync_economy() {
		File datafolder = mPlugin.getDataFolder();
		File configfile = new File(datafolder + "/config.yml");
		if (configfile.exists()) {
			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(configfile);
				return config.getBoolean("player.economy");
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static YamlConfiguration getWorldsFile() {
		File datafolder = mPlugin.getDataFolder();
		File configfile = new File(datafolder + "/worlds.yml");
		if (configfile.exists()) {
			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(configfile);
				return config;
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
		return new YamlConfiguration();
	}

	public static void pwi_sync_economy_warning() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
				+ "=====================WARNING=============================");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "When you use PerWorldInventory, it is recommended");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "not to save and load players economy balances");
		Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Set player.economy: false in PWI config.yml");
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
				+ "=========================================================");
		long now = System.currentTimeMillis();
		while (System.currentTimeMillis() < now + 40L) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationPerWorldInventory;
	}

}
