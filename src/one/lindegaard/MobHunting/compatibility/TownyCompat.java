package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;

public class TownyCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	// http://towny.palmergames.com/

	public TownyCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with Towny in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("Towny");

			Bukkit.getLogger().info(
					"[MobHunting] Enabling compatibility with Towny (" + mPlugin.getDescription().getVersion() + ").");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getPlugin() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationTowny;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationTowny;
	}

	public static boolean isInHomeTome(Player player) {
		if (supported) {
			return TownyHelper.isInHomeTome(player);
		}
		return false;
	}

}
