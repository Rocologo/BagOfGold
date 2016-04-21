package one.lindegaard.MobHunting.bounty;

import org.bukkit.OfflinePlayer;

public class Bounty {
	private OfflinePlayer bountyOwner;
	private double prize;
	private String message;

	public Bounty() {
	}

	public Bounty(OfflinePlayer bountyOwner,
			double prize, String message) {
		this.bountyOwner = bountyOwner;
		this.prize = prize;
		this.message = message;
	}

	public OfflinePlayer getBountyOwner() {
		return bountyOwner;
	}

	public void setBountyOwner(OfflinePlayer bountyOwner) {
		this.bountyOwner = bountyOwner;
	}

	public double getPrize() {
		return prize;
	}

	public void setPrize(double prize) {
		this.prize = prize;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}