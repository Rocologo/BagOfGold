package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.connorlinfoot.titleapi.TitleAPI;

import one.lindegaard.MobHunting.MobHunting;

public class TitleAPICompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public TitleAPICompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with TitelAPI is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.TitleAPI.getName());

			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with TitleAPI ("
					+ getTitleAPI().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getTitleAPI() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationTitleAPI;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationTitleAPI;
	}

	public static void sendTitles(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		if (supported)
			TitleAPI.sendTitle(player, fadeIn, stay, fadeOut, title, subtitle);
	}

	public static void sendTabTitle(Player player, String header, String footer) {
		if (supported)
			TitleAPI.sendTabTitle(player, header, footer);

	}
}
