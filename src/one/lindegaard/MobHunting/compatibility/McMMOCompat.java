package one.lindegaard.MobHunting.compatibility;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class McMMOCompat implements Listener {

	// https://www.spigotmc.org/resources/conquesita-mobs.21307/

	private static boolean supported = false;
	private static Plugin mPlugin;
	public static final String MH_MCMMO = "MH:MCMMO";

	public McMMOCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with McMMO is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("mcMMO");

			if (mPlugin.getDescription().getVersion().compareTo("1.5.00") >= 0) {
				Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
				Bukkit.getLogger().info("[MobHunting] Enabling Compatibility with McMMO ("
						+ getCustomMobs().getDescription().getVersion() + ")");
				supported = true;
			} else {
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				console.sendMessage(ChatColor.RED + "[MobHunting] Your current version of McMMO ("
						+ mPlugin.getDescription().getVersion()
						+ ") is not supported by MobHunting. Please update McMMO to version 1.5.00 or newer.");
			}
		}

	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static Plugin getCustomMobs() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isMcMMO(Entity entity) {
		if (isSupported())
			return entity.hasMetadata(MH_MCMMO);
		return false;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationMcMMO;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMcMMO;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************


}
