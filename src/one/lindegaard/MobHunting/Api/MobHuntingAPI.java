package one.lindegaard.MobHunting.Api;

import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.MobHuntingManager;

public class MobHuntingAPI {

	MobHunting instance;


	/**
	 * Constructor for MobHuntingAPI
	 */
	public MobHuntingAPI() {
		this.instance = getMobHunting();
	}

	/**
	 * Gets the MobHunting Instance
	 * 
	 * @return Instance
	 */
	private MobHunting getMobHunting() {
		return MobHunting.getInstance();
	}
	
	/**
	 * Gets the MobHuntingManager
	 * @return MobHuntingManger
	 */
	public MobHuntingManager getMobHuntingManager(){
		return MobHunting.getMobHuntingManager();
	}
	
	/**
	 * Test if MobHunting is enabled for Player
	 * @param player
	 * @return true if MobHunting is enabled for the player.
	 */
	public boolean isMobHuntingEnabled(Player player){
		return getMobHuntingManager().isHuntEnabled(player);
	}
	
	

}