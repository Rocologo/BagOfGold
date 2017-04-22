package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;

public class ResidenceCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/residence-1-7-10-up-to-1-11.11480/

	public ResidenceCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with Residence in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("Residence");

			Bukkit.getLogger().info(
					"[MobHunting] Enabling compatibility with Residence (" + mPlugin.getDescription().getVersion() + ").");
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
		return MobHunting.getConfigManager().disableIntegrationResidence;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationResidence;
	}

	public static boolean isProtected(Player player) {
		if (supported) {
			return ResidenceHelper.isProtected(player);
		}
		return false;
	}

}
