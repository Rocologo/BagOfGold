package one.lindegaard.BagOfGold.compatibility;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.ebonjaeger.perworldinventory.PerWorldInventory;
import me.ebonjaeger.perworldinventory.event.InventoryLoadCompleteEvent;
import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent;
import one.lindegaard.BagOfGold.BagOfGold;

public class PerWorldInventoryCompat implements Listener {

	BagOfGold plugin;
	private static PerWorldInventory mPlugin;
	private static boolean supported = false;
	private static boolean sync_economy = false;

	public PerWorldInventoryCompat() {
		plugin = BagOfGold.getInstance();
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "Compatibility with PerWorldInventory is disabled in config.yml");
		} else {
			mPlugin = (PerWorldInventory) Bukkit.getPluginManager().getPlugin(CompatPlugin.PerWorldInventory.getName());

			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
							+ "Enabling compatibility with PerWorldInventory ("
							+ getEssentials().getDescription().getVersion() + ")");
			Bukkit.getPluginManager().registerEvents(this, plugin);

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
			//					plugin.getMessages().debug("onInventoryLoadCompleted");
			//					plugin.getEconomyManager().adjustAmountInInventoryToBalance(event.getPlayer());
			//				}
			//			}, 40);
			//		}
			//	}, plugin);
			//else
				Bukkit.getPluginManager().registerEvents(new Listener() {
					@EventHandler(priority = EventPriority.HIGHEST)
					public void onInventoryLoad(InventoryLoadEvent event) {
						Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
							@Override
							public void run() {
								plugin.getMessages().debug("onInventoryLoad");
								plugin.getEconomyManager().adjustAmountInInventoryToBalance(event.getPlayer());
							}
						}, 40);
					}
				}, plugin);

			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static PerWorldInventory getEssentials() {
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

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
