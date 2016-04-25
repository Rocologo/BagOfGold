package one.lindegaard.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import mc.alk.arena.events.players.ArenaPlayerJoinEvent;
import mc.alk.arena.events.players.ArenaPlayerLeaveEvent;
import one.lindegaard.MobHunting.MobHunting;

public class BattleArenaCompat implements Listener {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BattleArenaCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info("Compatability with BattleArena is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("BattleArena");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			MobHunting.getInstance().getLogger().info(
					"Enabling compatability with BattleArena (" + getBattleArena().getDescription().getVersion() + ")");
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
		MobHunting.debug("BattleArenaCompat.StartEvent s%", event.getEventName());
		BattleArenaHelper.startPlayingBattleArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerLeaveEvent(ArenaPlayerLeaveEvent event) {
		MobHunting.debug("BattleArenaCompat.StartEvent s%", event.getEventName());
		BattleArenaHelper.stopPlayingBattleArena(event.getPlayer());
	}

}
