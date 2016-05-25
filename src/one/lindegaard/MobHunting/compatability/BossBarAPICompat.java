package one.lindegaard.MobHunting.compatability;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.inventivetalent.bossbar.BossBarAPI;
import org.inventivetalent.bossbar.BossBarAPI.Color;

import net.md_5.bungee.api.chat.TextComponent;
import one.lindegaard.MobHunting.MobHunting;

public class BossBarAPICompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BossBarAPICompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info("Compatability with BossBarAPI in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("BossBarAPI");

			MobHunting.getInstance().getLogger().info(
					"Enabling compatability with BossBarAPI (" + getBossBarAPI().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getBossBarAPI() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationBossBarAPI;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationBossBarAPI;
	}

	public static void addBar(Player player, String text) {
		if (supported)

			try {
				@SuppressWarnings({})
				Class<?> cls = Class.forName("org.inventivetalent.bossbar.BossBarAPI");
				// Create a new BossBar
				BossBarAPI.addBar(player, // The receiver of the BossBar
						new TextComponent(text), // Displayed message
						BossBarAPI.Color.BLUE, // Color of the bar
						BossBarAPI.Style.NOTCHED_20, // Bar style
						1.0f, // Progress (0.0 - 1.0)
						100, // Timeout in ticks
						2); // Timeout-interval
			} catch (ClassNotFoundException
					// | NoSuchMethodException
					| SecurityException e) {
				Bukkit.getLogger()
						.warning("[MobHunting] Your version of BossBarAPI is not compatible with MobHunting.");
				player.sendMessage(text);
				supported = false;
			}

	}
}
