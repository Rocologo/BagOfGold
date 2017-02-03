package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import one.lindegaard.MobHunting.MobHunting;

public class WorldGuardCompat {

	private static boolean supported = false;
	private static WorldGuardPlugin mPlugin;

	public WorldGuardCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with WorldGuard is disabled in config.yml");
		} else {
			mPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");

			if (mPlugin.getDescription().getVersion().compareTo("6.0") < 0) {
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				console.sendMessage(ChatColor.RED + "[MobHunting] Your current version of WorldGuard ("
						+ mPlugin.getDescription().getVersion()
						+ ") is not supported by MobHunting. Mobhunting does only support 6.0+");
			} else {

				WorldGuardHelper.addMobHuningFlag();

				Bukkit.getLogger().info("[MobHunting] Enabling compatibility with WorldGuard ("
						+ mPlugin.getDescription().getVersion() + ")");
				supported = true;

			}
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static WorldGuardPlugin getWorldGuardPlugin() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationWorldGuard;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationWorldGuard;
	}

}
