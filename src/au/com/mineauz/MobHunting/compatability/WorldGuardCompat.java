package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import au.com.mineauz.MobHunting.MobHunting;

public class WorldGuardCompat implements Listener {

	private static boolean supported = false;
	private static WorldGuardPlugin mPlugin;

	public WorldGuardCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance.getLogger().info(
					"Compatability with WorldGuard is disabled in config.yml");
		} else {
			mPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin(
					"WorldGuard");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

			if (getWorldGuardPlugin() != null)
				if (getWorldGuardPlugin().getDescription().getVersion()
						.startsWith("5")) {
					MobHunting.instance
							.getLogger()
							.warning(
									"Your current version of WorldGuard ("
											+ getWorldGuardPlugin()
													.getDescription()
													.getVersion()
											+ ") is not supported by MobHunting. Mobhunting does only support 6.0+");
				} else {

					MobHunting.instance.getLogger().info(
							"Enabling compatability with WorldGuard ("
									+ getWorldGuardPlugin().getDescription()
											.getVersion() + ")");
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

	public static boolean isWorldGuardSupported() {
		return supported;
	}
	
	public static boolean isDisabledInConfig(){
		return MobHunting.config().disableIntegrationWorldGuard;
	}

	public static boolean isEnabledInConfig(){
		return !MobHunting.config().disableIntegrationWorldGuard;
	}

}
