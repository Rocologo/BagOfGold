package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class ActionbarCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	// http://dev.bukkit.org/bukkit-plugins/actionbar/
	// https://www.spigotmc.org/resources/actionbar.1458/

	public ActionbarCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info("Compatibility with Actionbar in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("Actionbar");

			MobHunting.getInstance().getLogger().info(
					"Enabling compatibility with Actionbar (" + getActionbar().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getActionbar() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationActionbar;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationActionbar;
	}

	public static void setMessage(Player player, String text) {
		if (supported) {
			Messages.debug(
					"[WARNING] ActionbarCompat: setMessage() is not made yet. I cant get access to source code or API.");
			player.sendMessage(text);
			// TODO: I need a copy of ActionBar.jar before I can implement
			// anything.

			// Show a specific actionbar group
			// showActionbar(Player player, String actionbar);

			// Remove a specific actionbar group previously shown using the show
			// command, API, announcement or trigger eve
			// removeActionbarOverride(Player player, String actionbar);

			// Removes all overrides.​
			// resetDefaultActionbar(Player player);
		}
	}

}
