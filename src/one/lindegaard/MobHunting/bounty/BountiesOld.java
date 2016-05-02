package one.lindegaard.MobHunting.bounty;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;

public class BountiesOld {

	HashMap<OfflinePlayer, List<Bounty>> bounties = new HashMap<OfflinePlayer, List<Bounty>>();

	BountiesOld() {
	}

	/**
	 * Get all bounties on a wantedPlayer
	 * 
	 * @return a map of bountyOwners how has put a prize on the wantedPlayer
	 */
	public HashMap<OfflinePlayer, List<Bounty>> getBounties() {
		return bounties;
	}

	public HashMap<OfflinePlayer, List<Bounty>> getBounties(OfflinePlayer player) {
		//MobHunting.getDataStoreManager().requestBounties(BountyMode.Open, player,
			//	new IDataCallback<Set<Bounty>>());
				
		//MobHunting.getDataStoreManager().requestAllAchievements(player, new IDataCallback<Set<AchievementStore>>()
		//public void requestCompletedAchievements(OfflinePlayer player,
		//		final IDataCallback<List<Map.Entry<Achievement, Integer>>> callback)
		
		return bounties;
	}
	
	/**
	 * Set all bounties for the wantedPlayer
	 * 
	 * @param bounties
	 */
	public void setBounties(HashMap<OfflinePlayer, List<Bounty>> bounties) {
		this.bounties = bounties;
	}

	/**
	 * get the Bounty on the wantedPlayer set by the bountyOwner
	 * 
	 * @param bountyOwner
	 * @return
	 */
	public List<Bounty> getBounty(OfflinePlayer bountyOwner) {
		return bounties.get(bountyOwner);
	}

	/**
	 * put/add a bounty on the set of Bounties.
	 * 
	 * @param offlinePlayer
	 * @param bounty
	 */
	public void putBounty(OfflinePlayer offlinePlayer, Bounty bounty) {
		List<Bounty> bountyList;
		bountyList=bounties.get(offlinePlayer);
		
		if (bountyList.isEmpty()) {
			bountyList.add(bounty);
		} else {
			for (Bounty b: bountyList){
				if (b.getBountyId()==bounty.getBountyId()){
					b.setPrize(b.getPrize() + bounty.getPrize());
					b.setMessage(bounty.getMessage());
				}
			}
		}
		bounties.put(offlinePlayer, bountyList);
	}

	/**
	 * Check if the wantedPlayer has a bounty set by bountyOwner
	 * 
	 * @param bountyOwner
	 * @return true if the wantedPlayer has a bounty which was put by the
	 *         bountyOwner.
	 */
	public boolean hasBounties(OfflinePlayer bountyOwner) {
		return bounties.containsKey(bountyOwner);
	}
	
	public boolean hasBounty(OfflinePlayer killed) {
		if (bounties.containsKey(killed)){
			for (Bounty temp: bounties.get(killed)){
				if (temp.getWantedPlayer().equals(killed)) 
					return true;
			}
		}
		return false;
	}


	/**
	 * get all bountyOwners who has put a bounty on the wantedPlayer
	 * 
	 * @return
	 */
	public Set<OfflinePlayer> getBountyOwners() {
		return bounties.keySet();
	}

	/**
	 * Remove bounty on wantedPlayer, put by bountyOwner
	 * 
	 * @param bountyOwner
	 */
	public void removeBounty(OfflinePlayer bountyOwner) {
		bounties.remove(bountyOwner);
	}

}
