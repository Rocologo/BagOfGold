package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.Minigames;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntEnableCheckEvent;

public class MinigamesCompat implements Listener {
	public MinigamesCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info(
					"Compatibility with MiniGames is disabled in config.yml");
		} else {
			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
			MobHunting.getInstance().getLogger().info(
					"Enabling compatibility with Minigames");
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
