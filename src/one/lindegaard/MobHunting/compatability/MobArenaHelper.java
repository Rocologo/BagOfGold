package one.lindegaard.MobHunting.compatability;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.MobHunting;

public class MobArenaHelper {

	// ***************************************************************************
	// Integration to MobArena
	// ***************************************************************************
	private static List<UUID> playersPlayingMobArena = new ArrayList<UUID>();

	/**
	 * Determine if the player is currently playing MobArena
	 * 
	 * @param player 
	 * @return Returns true when the player is in game.
	 */
	public static boolean isPlayingMobArena(Player player) {
		return playersPlayingMobArena.contains(player.getUniqueId());
	}

	/**
	 * Add the player to the list of active MobArena players.
	 * @param player 
	 */
	public static void startPlayingMobArena(Player player) {
		playersPlayingMobArena.add(player.getUniqueId());
	}

	/**
	 * Remove the player from list of active MobArena players  
	 * @param player 
	 */
	public static void stopPlayingMobArena(Player player) {
		if(!playersPlayingMobArena.remove(player.getUniqueId())){
			MobHunting.debug("Player: %s is not a the MobArena", player.getName());
		}
	}

}
