package one.lindegaard.MobHunting.storage;

import org.bukkit.OfflinePlayer;

import one.lindegaard.MobHunting.StatType;

public class StatStore {
	private StatType type;
	private OfflinePlayer player;
	private int amount;

	public StatStore(StatType type, OfflinePlayer player, int amount) {
		this.type = type;
		this.player = player;
		this.amount = amount;
	}

	public StatStore(StatType type, OfflinePlayer player) {
		this.type = type;
		this.player = player;
		amount = 1; //add one kill first time.
	}

	/**
	 * @return the type
	 */
	public StatType getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(StatType type) {
		this.type = type;
	}

	/**
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * convert data to a readable format
	 */
	@Override
	public String toString() {
		return String.format("StatStore: {player: %s type: %s amount: %d}",
				player.getName(), type.getDBColumn(), amount);
	}
}
