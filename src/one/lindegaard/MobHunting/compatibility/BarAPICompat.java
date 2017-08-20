package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.confuser.barapi.BarAPI;
import one.lindegaard.MobHunting.MobHunting;

public class BarAPICompat {

	// https://dev.bukkit.org/projects/bar-api

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BarAPICompat() {
		if (isDisabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage("[MobHunting] Compatibility with BarAPI is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.BarApi.getName());
			if (mPlugin.getDescription().getVersion().compareTo("3.0") < 0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[MobHunting] Your current version of BarAPI ("
						+ mPlugin.getDescription().getVersion()
						+ ") is not supported by MobHunting. Mobhunting does only support version 3.0 or newer.");
			} else {
				Bukkit.getConsoleSender().sendMessage("[MobHunting] Enabling compatibility with BarAPI ("
						+ getBarAPI().getDescription().getVersion() + ")");
				supported = true;
			}
		}

	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getBarAPI() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationBarAPI;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationBarAPI;
	}

	@SuppressWarnings("deprecation")
	public static void setMessage(Player player, String text) {
		if (supported)
			BarAPI.setMessage(player, text);
	}

	@SuppressWarnings("deprecation")
	public static void setMessagePercent(Player player, String text, float percent) {
		if (supported)
			BarAPI.setMessage(player, text, percent);
	}

	@SuppressWarnings("deprecation")
	public static void setMessageTime(Player player, String text, int seconds) {
		if (supported)
			BarAPI.setMessage(player, text, seconds);
	}

	@SuppressWarnings("deprecation")
	public boolean hasBar(Player player) {
		if (supported)
			return BarAPI.hasBar(player);
		else
			return false;
	}

	@SuppressWarnings("deprecation")
	public static void removeBar(Player player) {
		if (supported)
			BarAPI.removeBar(player);
	}

	@SuppressWarnings("deprecation")
	public static void setHealth(Player player, float percent) {
		if (supported)
			BarAPI.setHealth(player, percent);
	}

}
