package one.lindegaard.BagOfGold.compatibility;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderAPI;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.placeholder.BagOfGoldPlaceholderExpansion;
import one.lindegaard.BagOfGold.placeholder.PlaceHolderData;
import one.lindegaard.BagOfGold.placeholder.PlaceHolderManager;
import one.lindegaard.CustomItemsLib.compatibility.CompatPlugin;
//import one.lindegaard.CustomItemsLib.placeholder.BagOfGoldPlaceholderExpansion;
//import one.lindegaard.CustomItemsLib.placeholder.PlaceHolderData;
//import one.lindegaard.CustomItemsLib.placeholder.PlaceHolderManager;

public class PlaceholderAPICompat {

	private static Plugin mPlugin;
	private static boolean supported = false;
	private static PlaceHolderManager mPlaceHolderManager;

	// https://www.spigotmc.org/resources/placeholderapi.6245/
	// https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/PlaceholderExpansion#without-external-plugin

	public PlaceholderAPICompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ "Compatibility with PlaceholderAPI is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.PlaceholderAPI.getName());
			if (mPlugin.getDescription().getVersion().compareTo("2.11.1") >= 0) {
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
								+ "Enabling compatibility with PlaceholderAPI (" + mPlugin.getDescription().getVersion()
								+ ").");
				new BagOfGoldPlaceholderExpansion().register();
				mPlaceHolderManager = new PlaceHolderManager(BagOfGold.getInstance());
				supported = true;
			} else {
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
								+ "Your current version of PlaceholderAPI (" + mPlugin.getDescription().getVersion()
								+ ") is not supported by BagOfGold, please upgrade to 2.11.1 or newer.");
			}
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationPlaceholderAPI;
	}

	public static HashMap<UUID, PlaceHolderData> getPlaceHolders() {
		return mPlaceHolderManager.getPlaceHolders();
	}

	public static String setPlaceholders(Player player, String messages_with_placeholders) {
		if (isSupported())
			return PlaceholderAPI.setPlaceholders(player, messages_with_placeholders);
		return messages_with_placeholders;
	}

}
