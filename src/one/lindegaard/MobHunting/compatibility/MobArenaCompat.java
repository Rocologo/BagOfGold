package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.garbagemule.MobArena.events.ArenaPlayerDeathEvent;
import com.garbagemule.MobArena.events.ArenaPlayerJoinEvent;
import com.garbagemule.MobArena.events.ArenaPlayerLeaveEvent;
import com.garbagemule.MobArena.events.ArenaKillEvent;
import com.garbagemule.MobArena.events.ArenaCompleteEvent;
import com.garbagemule.MobArena.events.ArenaEndEvent;
import com.garbagemule.MobArena.events.ArenaPlayerReadyEvent;
import com.garbagemule.MobArena.events.ArenaStartEvent;
import com.garbagemule.MobArena.events.NewWaveEvent;

import one.lindegaard.MobHunting.MobHunting;

public class MobArenaCompat implements Listener {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public MobArenaCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info(
					"Compatibility with MobArena is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("MobArena");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			MobHunting.getInstance()
					.getLogger()
					.info("Enabling compatibility with MobArena ("
							+ getMobArena().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getMobArena() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationMobArena;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMobArena;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	// Happens when the player joins the Arena /ma join
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerJoinEvent(ArenaPlayerJoinEvent event) {
		MobHunting.debug("[MH]Player %s joined MobArena: %s", event.getPlayer()
				.getName(), event.getArena());
		MobArenaHelper.startPlayingMobArena(event.getPlayer());
	}

	// Happens when the player leave the Arena /ma leave
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerLeaveEvent(ArenaPlayerLeaveEvent event) {
		MobHunting.debug("[MH]Player %s left MobArena: %s", event.getPlayer()
				.getName(), event.getArena());
		MobArenaHelper.stopPlayingMobArena(event.getPlayer());
	}

	// Happens when the player dies
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerDeathEvent(ArenaPlayerDeathEvent event) {
		// MobHunting.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when the player hits the Iron block (waiting for other player to
	// do the same)
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerReadyEvent(ArenaPlayerReadyEvent event) {
		// MobHunting.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when???
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaCompleteEvent(ArenaCompleteEvent event) {
		// MobHunting.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when a/the player kill a Mob
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaKillEvent(ArenaKillEvent event) {
		// MobHunting.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when the all players are ready and they enter the Arena
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaStartEvent(ArenaStartEvent event) {
		// MobHunting.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when the all players are dead and in "Jail"
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaEndEvent(ArenaEndEvent event) {
		// MobHunting.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens everytime a new wave begin
	@EventHandler(priority = EventPriority.NORMAL)
	private void onNewWareEvent(NewWaveEvent event) {
		// MobHunting.debug("[MH]Eventname: %s", event.getEventName());
	}

}
