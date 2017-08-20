package one.lindegaard.MobHunting.compatibility;

import net.slipcor.pvparena.events.PADeathEvent;
import net.slipcor.pvparena.events.PAExitEvent;
import net.slipcor.pvparena.events.PAJoinEvent;
import net.slipcor.pvparena.events.PALeaveEvent;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class PVPArenaCompat implements Listener {

	private Plugin mPlugin;
	private static List<UUID> playersPlayingPVPArena = new ArrayList<UUID>();
	private static boolean supported = false;

	public PVPArenaCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with PvpArena is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.PVPArena.getName());
			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
			Bukkit.getLogger().info("[MobHunting] Enabling Compatibility with PVPArena ("
					+ mPlugin.getDescription().getVersion() + ")");
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
		return MobHunting.getConfigManager().disableIntegrationPvpArena;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationPvpArena;
	}

	/**
	 * Determine if the player is currently playing PVPArena
	 * 
	 * @param player
	 * @return Return true when the player is in game.
	 */
	public static boolean isPlayingPVPArena(Player player) {
		if (isSupported())
			return playersPlayingPVPArena.contains(player.getUniqueId());
		return false;
	}

	/**
	 * Add the player to the list of active PVPArena players
	 * 
	 * @param player
	 */
	public static void startPlayingPVPArena(Player player) {
		playersPlayingPVPArena.add(player.getUniqueId());
	}

	/**
	 * Remove the player from the list of active users playing PVPArena
	 * 
	 * @param player
	 */
	public static void stopPlayingPVPArena(Player player) {
		if (!playersPlayingPVPArena.remove(player.getUniqueId())) {
			Messages.debug("Player: %s is not in PVPArena", player.getName());
		}
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPvpPlayerJoin(PAJoinEvent event) {
		Messages.debug("[MH]Player %s joined PVPArena: %s", event.getPlayer().getName(), event.getArena());
		startPlayingPVPArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPvpPlayerDeath(PADeathEvent event) {
		Messages.debug("[MH]Player %s died in PVPArena: %s", event.getPlayer().getName(), event.getArena());
		//startPlayingPVPArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPvpPlayerLeave(PALeaveEvent event) {
		Messages.debug("[MH]Player %s left PVPArena: %s", event.getPlayer().getName(), event.getArena());
		stopPlayingPVPArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPAExit(PAExitEvent event) {
		Messages.debug("[MH]Player %s exit PVPArena: %s", event.getPlayer().getName(), event.getArena());
		stopPlayingPVPArena(event.getPlayer());
	}

	// More events at
	// https://github.com/slipcor/pvparena/tree/master/src/net/slipcor/pvparena/events

}
