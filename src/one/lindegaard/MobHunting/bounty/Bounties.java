package one.lindegaard.MobHunting.bounty;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class Bounties {

	// index = BountyOwner
	HashMap<OfflinePlayer, Bounty> bounties = new HashMap<OfflinePlayer, Bounty>();

	Bounties() {
	}

	/**
	 * Get all bounties on a wantedPlayer
	 * @return a map of bountyOwners how has put a prize on the wantedPlayer
	 */
	public HashMap<OfflinePlayer, Bounty> getBounties() {
		return bounties;
	}

	/**
	 * Set all bounties for the wantedPlayer 
	 * @param bounties 
	 */
	public void setBounties(HashMap<OfflinePlayer, Bounty> bounties) {
		this.bounties = bounties;
	}

	/**
	 * get the Bounty on the wantedPlayer set by the bountyOwner
	 * @param bountyOwner
	 * @return
	 */
	public Bounty getBounty(OfflinePlayer bountyOwner) {
		return bounties.get(bountyOwner);
	}

	/**
	 * put/add a bounty on the wantedplayer.
	 * @param bountyOwner
	 * @param bounty
	 */
	public void putBounty(OfflinePlayer bountyOwner, Bounty bounty) {
		Bounty b;
		if (!bounties.containsKey(bountyOwner)) {
			b = bounty;
		} else {
			b = bounties.get(bountyOwner);
			b.setPrize(b.getPrize() + bounty.getPrize());
			b.setMessage(bounty.getMessage());
		}
		bounties.put(bountyOwner, b);
	}

	/**
	 * Check if the wantedPlayer has a bounty set by bountyOwner
	 * @param bountyOwner
	 * @return true if the wantedPlayer has a bounty which was put by the bountyOwner.
	 */
	public boolean hasBounty(OfflinePlayer bountyOwner) {
		return bounties.containsKey(bountyOwner);
	}

	/**
	 * get all bountyOwners who has put a bounty on the wantedPlayer
	 * @return
	 */
	public Set<OfflinePlayer> getBountyOwners() {
		return bounties.keySet();
	}

	/**
	 * Remove bounty on wantedPlayer, put by bountyOwner
	 * @param bountyOwner
	 */
	public void removeBounty(OfflinePlayer bountyOwner) {
		bounties.remove(bountyOwner);
	}

	// ***************************************************************
	// write & read
	// ***************************************************************

	/**
	 * write all Bounties section on a wantedPlayer into configuration
	 * @param section
	 */
	public void write(ConfigurationSection section) {
		section.createSection("bounties");
		for (OfflinePlayer b : bounties.keySet()) {
			section.createSection("bounties." + b.getUniqueId());
			bounties.get(b).write(section, b.getUniqueId());
		}
	}

	/**
	 * Read all bounties section for the wantedPlayer from the configuration
	 * @param section
	 * @throws InvalidConfigurationException
	 * @throws IllegalStateException
	 */
	public void read(ConfigurationSection section) throws InvalidConfigurationException, IllegalStateException {
		Set<String> keys = section.getKeys(false);
		for (String uuid : keys) {
			Bounty bounty = new Bounty();
			bounty.read(section,UUID.fromString(uuid));
			bounties.put(bounty.getBountyOwner(), bounty);
		}
	}

	}
