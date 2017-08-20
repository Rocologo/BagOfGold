package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.actionannouncer.ActionAPI;
import one.lindegaard.MobHunting.MobHunting;

public class ActionAnnouncerCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/actionannouncer.1320/

	public ActionAnnouncerCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getConsoleSender()
					.sendMessage("[MobHunting] Compatibility with ActionAnnouncer is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.ActionAnnouncer.getName());

			Bukkit.getConsoleSender().sendMessage("[MobHunting] Enabling compatibility with ActionAnnouncer ("
					+ mPlugin.getDescription().getVersion() + ").");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getActionAnnouncer() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationActionAnnouncer;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationActionAnnouncer;
	}

	public static void setMessage(Player player, String text) {
		if (supported) {
			// actionbar message to a specific player
			ActionAPI.sendPlayerAnnouncement(player, text);
		}
	}

	public static void setBroadcastMessage(Player player, String text) {
		if (supported) {
			// send an actionbar message to the whole server
			ActionAPI.sendServerAnnouncement(text);
		}
	}

	public static void setBroadcastMessageTime(Plugin plugin, Player player, String text, int seconds) {
		if (supported) {
			// send an actionbar message to a specific player for a certain
			// amount of time
			ActionAPI.sendTimedPlayerAnnouncement(plugin, player, text, seconds);
		}
	}

}
