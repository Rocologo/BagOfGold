package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntEnableCheckEvent;

public class MinigamesCompat implements Listener {
	
	private static boolean supported = false;
	
	public MinigamesCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info(
					"[MobHunting] Compatibility with MiniGames is disabled in config.yml");
		} else {
			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
			Bukkit.getLogger().info(
					"[MobHunting] Enabling compatibility with Minigames");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************
	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationMinigames;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMinigames;
	}

	public static boolean isSupported() {
		return supported;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerJoinMinigame(MobHuntEnableCheckEvent event) {
		Messages.debug("onPlayerJoinMinigame was run...");
		MinigamePlayer player = Minigames.plugin.pdata.getMinigamePlayer(event
				.getPlayer());
		if (player != null && player.isInMinigame())
			event.setEnabled(false);
	}

}
