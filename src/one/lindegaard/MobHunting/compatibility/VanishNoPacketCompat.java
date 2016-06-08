package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.kitteh.vanish.VanishPlugin;

import one.lindegaard.MobHunting.MobHunting;

public class VanishNoPacketCompat implements Listener {

	private static VanishPlugin mPlugin;
	private static boolean supported = false;

	public VanishNoPacketCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info("Compatibility with VanishNoPacket is disabled in config.yml");
		} else {

			mPlugin = (VanishPlugin) Bukkit.getServer().getPluginManager().getPlugin("VanishNoPacket");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			MobHunting.getInstance().getLogger().info("Enabling compatibility with VanishNoPacket ("
					+ getVanishNoPacket().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static VanishPlugin getVanishNoPacket() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationVanishNoPacket;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationVanishNoPacket;
	}

	public static boolean isVanishedModeEnabled(Player player) {
		if (supported)
			return getVanishNoPacket().getManager().isVanished(player);
		else
			return false;
	}

}
