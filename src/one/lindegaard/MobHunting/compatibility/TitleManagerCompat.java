package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.puharesource.mc.titlemanager.api.ActionbarTitleObject;
import one.lindegaard.MobHunting.MobHunting;

public class TitleManagerCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/titlemanager.1049/

	public TitleManagerCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("Compatibility with TitleManager is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("TitleManager");

			Bukkit.getLogger().info("Enabling compatibility with TitleManager ("
					+ getTtitleManager().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getTtitleManager() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationTitleManager;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationTitleManager;
	}

	public static void setActionBar(Player player, String message) {
		if (supported) {
			ActionbarTitleObject actionbar = new ActionbarTitleObject(message);
			actionbar.send(player);
		}
	}

}
