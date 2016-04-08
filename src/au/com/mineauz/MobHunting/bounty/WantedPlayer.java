package au.com.mineauz.MobHunting.bounty;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class WantedPlayer{
	private OfflinePlayer wantedPlayer;
	private ArrayList<Bounty> bounties;// = new HashMap<Bounty>();

	// UUID is uuid from wantedPlayer

	public WantedPlayer() {
	}
	
	public WantedPlayer(UUID uuid) {
		wantedPlayer = Bukkit.getOfflinePlayer(uuid);
		bounties = new ArrayList<Bounty>();
	}
	
	@SuppressWarnings("deprecation")
	public WantedPlayer(String playername) {
		wantedPlayer = Bukkit.getOfflinePlayer(playername);
		bounties = new ArrayList<Bounty>();
	}
	
	public WantedPlayer(OfflinePlayer offlinePlayer) {
		wantedPlayer = offlinePlayer;
		bounties = new ArrayList<Bounty>();
	}
	
	public WantedPlayer(OfflinePlayer wantedPlayer, ArrayList<Bounty> bounties) {
		this.wantedPlayer=wantedPlayer;
		this.bounties = bounties;
	}
	
	public WantedPlayer(OfflinePlayer wantedPlayer, Bounty bounty) {
		this.wantedPlayer=wantedPlayer;
		bounties = new ArrayList<Bounty>();
		bounties.add(bounty);
	}

	public UUID getUniqueId(){
		return wantedPlayer.getUniqueId();
	}
	
	public String getName(){
		return wantedPlayer.getName();
	}

	public OfflinePlayer getWantedPlayer() {
		return wantedPlayer;
	}

	public void setWantedPlayer(Player wantedPlayer) {
		this.wantedPlayer = wantedPlayer;
	}

	public ArrayList<Bounty> getAllBounties() {
		return bounties;
	}
	
	public Bounty getBounty(OfflinePlayer bountyOwner) {
		for (int i=0; i<bounties.size();i++){
			if (bounties.get(i).equals(bountyOwner)){
				return  bounties.get(i);
			}
		}
		return null;
	}
	
	public void removeBounty(OfflinePlayer bountyOwner) {
		for (int i=0; i<bounties.size();i++){
			if (bounties.get(i).equals(bountyOwner)){
				bounties.remove(i);
			}
		}
	}

	public void setBounties(ArrayList<Bounty> bounties) {
		this.bounties = bounties;
	}

	public void putBounty(OfflinePlayer bountyOwner, double prize,
			String message) {
		bounties.add(new Bounty(bountyOwner, prize, message));
	}

	public double getSumOfBountiesOnWantedPlayer() {
		double sum = 0;
		for (int i = 0; i < bounties.size(); i++) {
			sum = sum + bounties.get(i).getPrize();
		}
		return sum;
	}
	
	public boolean hasBounties(){
		return !bounties.isEmpty();
	}
	
	public boolean hasBounty(OfflinePlayer bountyOwner){
		for (int i=0; i<bounties.size();i++){
			if (bounties.get(i).equals(bountyOwner)){
				return true;
			}
		}
		return false;
	}

	// ***************************************************************
	// write & read
	// ***************************************************************
	public void write(ConfigurationSection section) {
		section.set("wanted_player", wantedPlayer);
		section.set("bounties", getAllBounties());
	}

	public void read(ConfigurationSection section)
			throws InvalidConfigurationException, IllegalStateException {
		wantedPlayer = section.getOfflinePlayer("wanted_player");
		if (bounties.size() > 0)
			section.set("bounties", bounties);
	}

}