package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.CustomItemsLib.compatibility.CompatPlugin;

public class ShopkeepersCompat {

	BagOfGold plugin;
	private static Plugin mPlugin;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/shopkeepers.80756/
		
	public ShopkeepersCompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ "Compatibility with Shopkeepers is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.Shopkeepers.getName());
			if (getShopkeepers().getDescription().getVersion().compareTo("2.16.1") < 0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
						+ "Your current version of Shopkeepers (" + mPlugin.getDescription().getVersion()
						+ ") is not supported by BagOfGold. BagOfGold does only support version 2.16.1 or newer.");
			} else {
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
								+ "Enabling compatibility with Shopkeepers ("
								+ getShopkeepers().getDescription().getVersion() + ")");
				supported = true;
			}
		}

	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getShopkeepers() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationShopkeepersBETA;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
