package one.lindegaard.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

//import be.maximvdw.actionbar.api.ActionBarAPI;

import one.lindegaard.MobHunting.MobHunting;

public class ActionBarCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	//https://www.spigotmc.org/resources/actionbar.1458/
	
	public ActionBarCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info("Compatability with ActionBar in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("ActionBar");

			MobHunting.getInstance().getLogger()
					.info("Enabling compatability with ActionBar (" + getActionBar().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getActionBar() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationActionBar;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationActionBar;
	}

	public static void setMessage(Player player, String text) {
		if (supported) {
			MobHunting.debug("[WARNING] ActionBarCompat: setMessage() is not made yet");
			player.sendMessage(text);
			//TODO: I need a copy of ActionBar.jar before I can implement anything.
			
			//Show a specific actionbar group
			//showActionbar(Player player, String actionbar);
			
			//Remove a specific actionbar group previously shown using the show command, API, announcement or trigger eve
			//removeActionbarOverride(Player player, String actionbar);
			
			//Removes all overrides.​
			//resetDefaultActionbar(Player player);
		}
	}

}
