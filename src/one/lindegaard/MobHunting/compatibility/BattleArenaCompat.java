package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import mc.alk.arena.events.players.ArenaPlayerJoinEvent;
import mc.alk.arena.events.players.ArenaPlayerLeaveEvent;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class BattleArenaCompat implements Listener {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BattleArenaCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("Compatibility with BattleArena is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("BattleArena");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getLogger().info(
					"Enabling compatibility with BattleArena (" + getBattleArena().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getBattleArena() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationBattleArena;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationBattleArena;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerJoinEvent(ArenaPlayerJoinEvent event) {
		Messages.debug("BattleArenaCompat.StartEvent s%", event.getEventName());
		BattleArenaHelper.startPlayingBattleArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerLeaveEvent(ArenaPlayerLeaveEvent event) {
		Messages.debug("BattleArenaCompat.StartEvent s%", event.getEventName());
		BattleArenaHelper.stopPlayingBattleArena(event.getPlayer());
	}

}
