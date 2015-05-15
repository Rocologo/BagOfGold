package au.com.mineauz.MobHunting.compatability;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.MobHunting;

public class MobArenaHelper {

	// ***************************************************************************
	// Integration to MobArena
	// ***************************************************************************
	private static List<Player> playersPlayingMobArena = new ArrayList<Player>();

	/**
	 * Determine if the player p is currently playing MobArena
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isPlayingMobArena(Player p) {
		return playersPlayingMobArena.contains(p);
	}

	public static void startPlayingMobArena(Player p) {
		playersPlayingMobArena.add(p);
	}

	public static void stopPlayingMobArena(Player p) {
		if(!playersPlayingMobArena.remove(p)){
			MobHunting.debug("Player: %s is not a the MobArena", p.getName());
		}
	}

}
