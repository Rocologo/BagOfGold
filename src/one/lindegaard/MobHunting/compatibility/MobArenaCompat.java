package one.lindegaard.MobHunting.compatibility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class MobArenaCompat implements Listener {

	private static Plugin mPlugin;
	private static List<UUID> playersPlayingMobArena = new ArrayList<UUID>();
	private static boolean supported = false;

	public MobArenaCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with MobArena is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.MobArena.getName());

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getLogger()
					.info("[MobHunting] Enabling compatibility with MobArena (" + getMobArena().getDescription().getVersion() + ")");
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

	private static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationMobArena;
	}

	@SuppressWarnings("unused")
	private static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMobArena;
	}
	
	/**
	 * Determine if the player is currently playing MobArena
	 * 
	 * @param player
	 * @return Returns true when the player is in game.
	 */
	public static boolean isPlayingMobArena(Player player) {
		if (isSupported())
			return playersPlayingMobArena.contains(player.getUniqueId());
		return false;
	}

	/**
	 * Add the player to the list of active MobArena players.
	 * 
	 * @param player
	 */
	public static void startPlayingMobArena(Player player) {
		playersPlayingMobArena.add(player.getUniqueId());
	}

	/**
	 * Remove the player from list of active MobArena players
	 * 
	 * @param player
	 */
	public static void stopPlayingMobArena(Player player) {
		if (!playersPlayingMobArena.remove(player.getUniqueId())) {
			Messages.debug("Player: %s is not playing MobArena", player.getName());
		}
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	// Happens when the player joins the Arena /ma join
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerJoinEvent(ArenaPlayerJoinEvent event) {
		Messages.debug("[MH]Player %s joined MobArena: %s", event.getPlayer().getName(), event.getArena());
		startPlayingMobArena(event.getPlayer());
	}

	// Happens when the player leave the Arena /ma leave
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerLeaveEvent(ArenaPlayerLeaveEvent event) {
		Messages.debug("[MH]Player %s left MobArena: %s", event.getPlayer().getName(), event.getArena());
		stopPlayingMobArena(event.getPlayer());
	}

	// Happens when the player dies
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerDeathEvent(ArenaPlayerDeathEvent event) {
		// Messages.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when the player hits the Iron block (waiting for other player to
	// do the same)
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerReadyEvent(ArenaPlayerReadyEvent event) {
		// Messages.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when???
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaCompleteEvent(ArenaCompleteEvent event) {
		// Messages.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when a/the player kill a Mob
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaKillEvent(ArenaKillEvent event) {
		// Messages.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when the all players are ready and they enter the Arena
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaStartEvent(ArenaStartEvent event) {
		// Messages.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens when the all players are dead and in "Jail"
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaEndEvent(ArenaEndEvent event) {
		// Messages.debug("[MH]Eventname: %s", event.getEventName());
	}

	// Happens everytime a new wave begin
	@EventHandler(priority = EventPriority.NORMAL)
	private void onNewWareEvent(NewWaveEvent event) {
		// Messages.debug("[MH]Eventname: %s", event.getEventName());
	}

}
