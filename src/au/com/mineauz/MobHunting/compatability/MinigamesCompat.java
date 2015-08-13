package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntEnableCheckEvent;
import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;

public class MinigamesCompat implements Listener {
	public MinigamesCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance.getLogger().info(
					"Compatability with MiniGames is disabled in config.yml");
		} else {
			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
			MobHunting.instance.getLogger().info(
					"Enabling compatability with Minigames");
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************
	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationMinigames;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationMinigames;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerJoinMinigame(MobHuntEnableCheckEvent event) {
		MobHunting.debug("onPlayerJoinMinigame was run...");
		MinigamePlayer player = Minigames.plugin.pdata.getMinigamePlayer(event
				.getPlayer());
		if (player != null && player.isInMinigame())
			event.setEnabled(false);
	}

}
