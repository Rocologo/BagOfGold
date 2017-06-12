package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;

public class BossBarAPICompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BossBarAPICompat() {
		if (isDisabledInConfig()) {
			Bukkit.getConsoleSender()
					.sendMessage("[MobHunting] Compatibility with BossBarAPI is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("BossBarAPI");

			Bukkit.getConsoleSender().sendMessage("[MobHunting] Enabling compatibility with BossBarAPI ("
					+ getBossBarAPI().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getBossBarAPI() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationBossBarAPI;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationBossBarAPI;
	}

	public static void setSupported(boolean b) {
		supported = b;
	}

	public static void addBar(Player player, String text) {
		BossBarAPICompatHelper.addBar(player, text);
	}
}
