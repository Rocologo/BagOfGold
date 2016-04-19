package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import au.com.mineauz.MobHunting.MobHunting;
import mc.alk.arena.events.events.EventCancelEvent;
import mc.alk.arena.events.events.EventCompletedEvent;
import mc.alk.arena.events.events.EventEvent;
import mc.alk.arena.events.events.EventFinishedEvent;
import mc.alk.arena.events.events.EventOpenEvent;
import mc.alk.arena.events.events.EventResultEvent;
import mc.alk.arena.events.events.EventStartEvent;
import mc.alk.arena.events.events.TeamJoinedEvent;

public class BattleArenaCompat implements Listener {

	private static Plugin mPlugin;
	private static boolean supported = false;

	public BattleArenaCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance.getLogger().info(
					"Compatability with BattleArena is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("BattleArena");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

			MobHunting.instance
					.getLogger()
					.info("Enabling compatability with BattleArena ("
							+ getBattleArena().getDescription().getVersion() + ")");
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
	private void onEventStartEvent(EventStartEvent event) {
		MobHunting.debug("BattleArenaCompat.StartEvent s%", event.getEventName());
		//MobBattleArenaHelper.startPlayingBattleArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onEventCancelEvent(EventCancelEvent event) {
		MobHunting.debug("BattleArenaCompat.CancelEvent s%", event.getEventName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onEventCompletedEvent(EventCompletedEvent event) {
		MobHunting.debug("BattleArenaCompat.CompletedEvent s%", event.getEventName());
		//BattleArenaHelper.stopPlayingBattleArena(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	private void onEventEvent(EventEvent event) {
		MobHunting.debug("BattleArenaCompat.Event s%", event.getEventName());
		
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	private void onEventFinishedEvent(EventFinishedEvent event) {
		MobHunting.debug("BattleArenaCompat.FinishedEvent s%", event.getEventName());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	private void onEventOpenEvent(EventOpenEvent event) {
		MobHunting.debug("BattleArenaCompat.OpenEvent s%", event.getEventName());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	private void onEventResultEvent(EventResultEvent event) {
		MobHunting.debug("BattleArenaCompat.ResultEvent s%", event.getEventName());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	private void onEventJoinedEvent(TeamJoinedEvent event) {
		MobHunting.debug("BattleArenaCompat.TeamJoinedEvent s%", event.getEventName());
	}
}
