package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import one.lindegaard.MobHunting.MobHunting;

public class EssentialsCompat implements Listener {

	private static Essentials mPlugin;
	private static boolean supported = false;

	public EssentialsCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with Essentials is disabled in config.yml");
		} else {
			mPlugin = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with Essentials ("
					+ getEssentials().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Essentials getEssentials() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationEssentials;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationEssentials;
	}

	public static boolean isGodModeEnabled(Player player) {
		if (isSupported()) {
			User user = getEssentials().getUser(player);
			return user.isGodModeEnabled();
		}
		return false;
	}

	public static boolean isVanishedModeEnabled(Player player) {
		if (isSupported()) {
			User user = getEssentials().getUser(player);
			return user.isVanished();
		}
		return false;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
