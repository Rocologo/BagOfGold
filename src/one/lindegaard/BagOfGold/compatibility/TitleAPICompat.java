package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.connorlinfoot.titleapi.TitleAPI;

import one.lindegaard.BagOfGold.BagOfGold;

public class TitleAPICompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public TitleAPICompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[MobHunting] " + ChatColor.RESET
					+ "Compatibility with TitelAPI is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.TitleAPI.getName());

			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[MobHunting] " + ChatColor.RESET
					+ "Enabling compatibility with TitleAPI (" + getTitleAPI().getDescription().getVersion() + ")");
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

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationTitleAPI;
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
