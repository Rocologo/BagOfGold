package au.com.mineauz.MobHunting.compatability;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.MobHunting;

public class BattleArenaHelper {

	// ***************************************************************************
	// Integration to BattleArena
	// ***************************************************************************
	private static List<UUID> playersPlayingBattleArena = new ArrayList<UUID>();

	/**
	 * Determine if the player is currently playing BattleArena
	 * 
	 * @param player 
	 * @return Returns true when the player is in game.
	 */
	public static boolean isPlayingBattleArena(Player player) {
		return playersPlayingBattleArena.contains(player.getUniqueId());
	}

	/**
	 * Add the player to the list of active BattleArena players.
	 * @param player 
	 */
	public static void startPlayingBattleArena(Player player) {
		playersPlayingBattleArena.add(player.getUniqueId());
	}

	/**
	 * Remove the player from list of active BattleArena players  
	 * @param player 
	 */
	public static void stopPlayingBattleArena(Player player) {
		if(!playersPlayingBattleArena.remove(player.getUniqueId())){
			MobHunting.debug("Player: %s is not a the BattleArena", player.getName());
		}
	}

}
