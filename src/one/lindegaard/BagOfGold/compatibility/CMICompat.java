package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.HologramManager;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.compatibility.CompatPlugin;

public class CMICompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/cmi-ranks-kits-portals-essentials-mysql-sqlite-bungeecord.3742/

	public CMICompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ "Compatibility with CMI is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.CMI.getName());
			if (mPlugin.getDescription().getVersion().compareTo("7.6") >= 0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
						+ "Enabling compatibility with CMI (" + mPlugin.getDescription().getVersion() + ").");
				supported = true;
			} else
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
						+ "Your current version of CMI (" + mPlugin.getDescription().getVersion()
						+ ") is not supported by MobHunting. Mobhunting does only support version 7.6 or newer.");

		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static CMI getCMIPlugin() {
		return (CMI) mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationCMI;
	}

	public static HologramManager getHologramManager() {
		return getCMIPlugin().getHologramManager();
	}
	
	public static void sendActionBarMessage(Player player, String text) {
		getCMIPlugin().getActionBar().send(player, text);
	}
	
	public static void sendBossBarMessage(Player player, String text) {
		getCMIPlugin().getBossBarManager().showBossBar(player, 20, 100.0, "", text, BarColor.BLUE);
	}
	
}
