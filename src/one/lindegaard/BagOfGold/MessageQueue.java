package one.lindegaard.BagOfGold;

import org.bukkit.entity.Player;

public class MessageQueue {

	Player player;
	String message;
	
	public MessageQueue(Player player, String message) {
		this.player=player;
		this.message=message;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	
}
