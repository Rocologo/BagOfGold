package one.lindegaard.MobHunting.bounty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;


public class BountyOwner{
	
	private HashMap <OfflinePlayer,Bounty> bountyOwners = new HashMap<OfflinePlayer, Bounty>();

	public BountyOwner() {
	}
	
	public HashMap<OfflinePlayer, Bounty> getBountyOwners() {
		return bountyOwners;
	}

	public void setBountyOwners(HashMap<OfflinePlayer, Bounty> bountyOwners) {
		this.bountyOwners = bountyOwners;
	}

	public Bounty getBountyOwners(OfflinePlayer bountyOwner) {
		return bountyOwners.get(bountyOwner);
	}

	
	// ***************************************************************
	// write & read
	// ***************************************************************


}