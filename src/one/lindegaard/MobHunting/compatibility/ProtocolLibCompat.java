package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;

public class ProtocolLibCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/protocollib.1997/

	public ProtocolLibCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with ProtocolLib is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");

			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with ProtocolLib ("
					+ mPlugin.getDescription().getVersion() + ").");
			ProtocolLibHelper.enableProtocolLib();
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getProtocoloLib() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationProtocolLib;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationProtocolLib;
	}

}
