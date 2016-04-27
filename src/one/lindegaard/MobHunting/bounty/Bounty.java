package one.lindegaard.MobHunting.bounty;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import one.lindegaard.MobHunting.MobHunting;

public class Bounty {

	private OfflinePlayer bountyOwner;
	private String name;
	private double prize;
	private String message;

	public Bounty() {
	}

	/**
	 * Contructor for Bounty
	 * @param bountyOwner - the bounty owner
	 * @param prize - the prize set by bounty owner on the wanted player
	 * @param message - the message to the wanted owner
	 */
	public Bounty(OfflinePlayer bountyOwner, double prize, String message) {
		this.bountyOwner = bountyOwner;
		this.prize = prize;
		this.message = message;
	}

	/**
	 * Get the BountyOwner
	 * @return the bounty owner
	 */
	public OfflinePlayer getBountyOwner() {
		return bountyOwner;
	}

	/**
	 * Set the Bounty Owner.
	 * @param bountyOwner
	 */
	public void setBountyOwner(OfflinePlayer bountyOwner) {
		this.bountyOwner = bountyOwner;
	}

	/**
	 * Get the prize put on the wantedPlayer set by bounty owner
	 * @return prize
	 */
	public double getPrize() {
		return prize;
	}

	/**
	 * Set the prize on the wantedPlayer put by bountyOwner
	 * @param prize
	 */
	public void setPrize(double prize) {
		this.prize = prize;
	}

	/**
	 * Get the message for the wantedPlayer
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message for the wanted player
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	// ***************************************************************
	// write & read
	// ***************************************************************
	/**
	 * write the bounty to the configuration
	 * @param section
	 * @param uuid
	 */
	public void write(ConfigurationSection section, UUID uuid) {
		section.createSection("bounties." + bountyOwner.getUniqueId());
		section.set("bounties." + uuid + ".bounty.offlineplayer", bountyOwner);
		section.set("bounties." + uuid + ".bounty.name", bountyOwner.getName());
		section.set("bounties." + uuid + ".bounty.prize", prize);
		section.set("bounties." + uuid + ".bounty.message", message);
	}

	/**
	 * read the bounty from the configuration
	 * @param section
	 * @param uuid
	 * @throws InvalidConfigurationException
	 * @throws IllegalStateException
	 */
	public void read(ConfigurationSection section, UUID uuid)
			throws InvalidConfigurationException, IllegalStateException {
		bountyOwner = section.getOfflinePlayer(uuid + ".bounty.offlineplayer");
		name = section.getString(uuid + ".bounty.name");
		if (bountyOwner.getName().equals("")) {
			bountyOwner = Bukkit.getOfflinePlayer(name);
		}
		prize = section.getDouble(uuid + ".bounty.prize");
		message = section.getString(uuid + ".bounty.message");
		MobHunting.debug("Read: %s,%s,%s", bountyOwner.getName(), prize, message);
	}

}