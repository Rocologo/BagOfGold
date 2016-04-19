package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import au.com.mineauz.MobHunting.MobHunting;
import mc.alk.arena.events.players.ArenaPlayerJoinEvent;
import mc.alk.arena.events.players.ArenaPlayerLeaveEvent;

public class BattleArenaCompat implements Listener {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BattleArenaCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance.getLogger().info("Compatability with BattleArena is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("BattleArena");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

			MobHunting.instance.getLogger().info(
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
		return MobHunting.config().disableIntegrationBattleArena;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationBattleArena;
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
