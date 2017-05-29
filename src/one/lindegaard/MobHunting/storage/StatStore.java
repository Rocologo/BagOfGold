package one.lindegaard.MobHunting.storage;

import org.bukkit.OfflinePlayer;

import one.lindegaard.MobHunting.StatType;
import one.lindegaard.MobHunting.mobs.ExtendedMob;

public class StatStore {
	private StatType type;
	private ExtendedMob mob;
	private OfflinePlayer player;
	private int amount;
	private double cash;

	public StatStore(StatType type, ExtendedMob mob, OfflinePlayer player, int amount, double cash) {
		this.type = type;
		this.setMob(mob);
		this.player = player;
		this.amount = amount;
		this.setCash(cash);
	}

	public StatStore(StatType type, ExtendedMob mob, OfflinePlayer player) {
		this.type = type;
		this.setMob(mob);
		this.player = player;
		amount = 1; // add one kill.
		setCash(0);
	}

	public StatStore(StatType type, OfflinePlayer player, int amount, double cash) {
		this.type = type;
		this.player = player;
		this.amount = amount;
		this.setCash(cash);
	}

	public StatStore(StatType type, OfflinePlayer player, double reward) {
		this.type = type;
		this.player = player;
		amount = 1; // add one achievement.
		this.setCash(reward);
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

	public ExtendedMob getMob() {
		return mob;
	}

	public void setMob(ExtendedMob mob) {
		this.mob = mob;
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
	 * @return the # of kills
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
	 * Get the amount of money
	 * 
	 * @return
	 */
	public double getCash() {
		return cash;
	}

	/**
	 * Set the amount of money
	 * 
	 * @param reward
	 */
	public void setCash(double reward) {
		this.cash = reward;
	}

	/**
	 * convert data to a readable format
	 */
	@Override
	public String toString() {
		return String.format("StatStore: {player: %s type: %s amount: %d}", player != null ? player.getName() : "",
				type.getDBColumn(), amount);
	}

}
