package one.lindegaard.MobHunting.compatability;

import net.slipcor.pvparena.events.PADeathEvent;
import net.slipcor.pvparena.events.PAExitEvent;
import net.slipcor.pvparena.events.PAJoinEvent;
import net.slipcor.pvparena.events.PALeaveEvent;
import one.lindegaard.MobHunting.MobHunting;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PVPArenaCompat implements Listener {

	private static boolean supported = false;

	public PVPArenaCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info(
					"Compatability with PvpArena is disabled in config.yml");
		} else {
			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
			MobHunting.getInstance().getLogger().info(
					"Enabling PVPArena Compatability");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationPvpArena;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationPvpArena;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPvpPlayerJoin(PAJoinEvent event) {
		MobHunting.debug("[MH]Player %s joined PVPArena: %s", event.getPlayer()
				.getName(), event.getArena());
		MobArenaHelper.startPlayingMobArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPvpPlayerDeath(PADeathEvent event) {
		MobHunting.debug("[MH]Player %s died in PVPArena: %s", event
				.getPlayer().getName(), event.getArena());
		MobArenaHelper.startPlayingMobArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPvpPlayerLeave(PALeaveEvent event) {
		MobHunting.debug("[MH]Player %s left PVPArena: %s", event.getPlayer()
				.getName(), event.getArena());
		MobArenaHelper.startPlayingMobArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPAExit(PAExitEvent event) {
		MobHunting.debug("[MH]Player %s exit PVPArena: %s", event.getPlayer()
				.getName(), event.getArena());
		MobArenaHelper.startPlayingMobArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPADeath(PADeathEvent event) {
		MobHunting.debug("[MH]Player %s died in PVPArena: %s", event
				.getPlayer().getName(), event.getArena());
		MobArenaHelper.startPlayingMobArena(event.getPlayer());
	}

	// More events at
	// https://github.com/slipcor/pvparena/tree/master/src/net/slipcor/pvparena/events

}
