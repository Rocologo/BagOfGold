package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.HologramManager;

import net.Zrips.CMILib.CMILib;
import net.Zrips.CMILib.ActionBar.CMIActionBar;
import net.Zrips.CMILib.BossBar.BossBarInfo;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.compatibility.CompatPlugin;

public class CMICompat {

	private static Plugin mPlugin, mPlugin2;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/cmi-ranks-kits-portals-essentials-mysql-sqlite-bungeecord.3742/
	// https://www.spigotmc.org/resources/cmilib.87610/

	public CMICompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ "Compatibility with CMI is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.CMI.getName());

			if (mPlugin.getDescription().getVersion().compareTo("9.0") >= 0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
						+ "Enabling compatibility with CMI (" + mPlugin.getDescription().getVersion() + ").");

				mPlugin2 = Bukkit.getPluginManager().getPlugin(CompatPlugin.CMILib.getName());
				if (mPlugin2.getDescription().getVersion().compareTo("1.0") >= 0) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
							+ "Enabling compatibility with CMILib (" + mPlugin2.getDescription().getVersion() + ").");
					supported = true;
				} else
					Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
							+ "Your current version of CMILib (" + mPlugin2.getDescription().getVersion()
							+ ") is not supported by BagOfGold. BagOfGold does only support version 1.0 or newer.");

			} else
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
						+ "Your current version of CMI (" + mPlugin.getDescription().getVersion()
						+ ") is not supported by BagOfGold. BagOfGold does only support version 9.0 or newer.");

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
		CMIActionBar.send(player, text);
	}

	public static void sendBossBarMessage(Player player, String text) {
		BossBarInfo bossBar = new BossBarInfo(player, "...");
		bossBar.setSeconds(10);
		bossBar.setTitleOfBar(text);
		bossBar.setKeepForTicks(0);
		CMILib.getInstance().getBossBarManager().addBossBar(player, bossBar);
	}

}
