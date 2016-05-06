package one.lindegaard.MobHunting;

import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Api.MobHuntingAPI;

public class Test {
	
	public Test(){
	}

	MobHuntingAPI mobHuntingAPI;
	
	/** This Method is only for test using the API
	 * 
	 * @param player
	 */
	public boolean test(Player player){
	
	mobHuntingAPI = new MobHuntingAPI();
	
	@SuppressWarnings("unused")
	int n=mobHuntingAPI.getMobHuntingManager().getOnlinePlayersAmount();
	
	return mobHuntingAPI.isMobHuntingEnabled(player);
	
	}
}
