package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.connorlinfoot.actionbarapi.ActionBarAPI;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.CustomItemsLib.compatibility.CompatPlugin;

public class ActionBarAPICompatDELETED {

	private static ActionBarAPI mPlugin;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/actionbarapi-1-8-1-9-1-10.1315/

	public ActionBarAPICompatDELETED() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ "Compatibility with ActionBarAPI is disabled in config.yml");
		} else {
			mPlugin = (ActionBarAPI) Bukkit.getPluginManager().getPlugin(CompatPlugin.ActionBarApi.getName());

			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
							+ "Enabling compatibility with ActionBarAPI ("
							+ getActionBarAPI().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public ActionBarAPI getActionBarAPI() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationActionBarAPI;
	}

	public static void setMessage(Player player, String text) {
		if (supported) {

			ActionBarAPI.sendActionBar(player, text);

			// ActionBarAPI.sendActionBar(player,"Action Bar Message",
			// duration);
		}
	}

}
