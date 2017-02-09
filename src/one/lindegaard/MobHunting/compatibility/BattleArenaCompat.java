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

import mc.alk.arena.events.players.ArenaPlayerJoinEvent;
import mc.alk.arena.events.players.ArenaPlayerLeaveEvent;
import mc.alk.arena.objects.ArenaPlayer;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class BattleArenaCompat implements Listener {

	private static Plugin mPlugin;
	private static List<UUID> playersPlayingBattleArena = new ArrayList<UUID>();
	private static boolean supported = false;

	public BattleArenaCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with BattleArena is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("BattleArena");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with BattleArena ("
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
		return MobHunting.getConfigManager().disableIntegrationBattleArena;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationBattleArena;
	}

	/**
	 * Determine if the player is currently playing BattleArena
	 * 
	 * @param player
	 * @return Returns true when the player is in game.
	 */
	public static boolean isPlayingBattleArena(Player player) {
		if (isSupported())
			return playersPlayingBattleArena.contains(player.getUniqueId());
		return false;
	}

	/**
	 * Add the player to the list of active BattleArena players.
	 * 
	 * @param arenaPlayer
	 */
	public static void startPlayingBattleArena(ArenaPlayer arenaPlayer) {
		playersPlayingBattleArena.add(arenaPlayer.getID());
	}

	/**
	 * Remove the player from list of active BattleArena players
	 * 
	 * @param arenaPlayer
	 */
	public static void stopPlayingBattleArena(ArenaPlayer arenaPlayer) {
		if (!playersPlayingBattleArena.remove(arenaPlayer.getID())) {
			Messages.debug("Player: %s is not a the BattleArena", arenaPlayer.getName());
		}
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerJoinEvent(ArenaPlayerJoinEvent event) {
		Messages.debug("BattleArenaCompat.StartEvent s%", event.getEventName());
		startPlayingBattleArena(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onArenaPlayerLeaveEvent(ArenaPlayerLeaveEvent event) {
		Messages.debug("BattleArenaCompat.StartEvent %s", event.getEventName());
		stopPlayingBattleArena(event.getPlayer());
	}

}
