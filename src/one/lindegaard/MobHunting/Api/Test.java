package one.lindegaard.MobHunting.Api;

import org.bukkit.entity.Player;

public class Test {
	
	public Test(){
	}

	//MobHuntingAPI mobHuntingAPI;
	
	/** This Method is only for test using the API
	 * 
	 * @param player
	 */
	public boolean test(Player player){
	
	//mobHuntingAPI = new MobHuntingAPI();
	
	@SuppressWarnings("unused")
	int n=MobHuntingAPI.getMobHuntingManager().getOnlinePlayersAmount();
	
	return MobHuntingAPI.isMobHuntingEnabled(player);
	
	}
}
