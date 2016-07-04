package one.lindegaard.MobHunting.compatibility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class PVPArenaHelper {

	// ***************************************************************************
	// Integration to PVPArena
	// ***************************************************************************
	private static List<UUID> playersPlayingPVPArena = new ArrayList<UUID>();

	/**
	 * Determine if the player is currently playing PVPArena
	 * 
	 * @param player 
	 * @return Return true when the player is in game.
	 */
	public static boolean isPlayingPVPArena(Player player) {
		return playersPlayingPVPArena.contains(player.getUniqueId());
	}

	/**
	 * Add the player to the list of active PVPArena players
	 * @param player 
	 */
	public static void startPlayingPVPArena(Player player) {
		playersPlayingPVPArena.add(player.getUniqueId());
	}

	/**
	 * Remove the player from the list of active users playing PVPArena
	 * @param player
	 */
	public static void stopPlayingPVPArena(Player player) {
		if(!playersPlayingPVPArena.remove(player.getUniqueId())){
			Messages.debug("Player: %s is not in PVPArena", player.getName());
		}
	}

}
