package one.lindegaard.BagOfGold;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;

import one.lindegaard.CustomItemsLib.Core;

public class PlayerBalance {

	private OfflinePlayer player;
	private String worldGroup;
	private GameMode gamemode;
	private double balance = 0;
	private double balanceChanges = 0;
	private double bankBalance = 0;
	private double bankBalanceChanges = 0;

	public PlayerBalance(OfflinePlayer player) {
		this.player = player;
		this.worldGroup = Core.getWorldGroupManager().getDefaultWorldgroup();
		this.gamemode = Core.getWorldGroupManager().getDefaultGameMode();
		this.setBalance(Core.getWorldGroupManager().getDefaultStartingBalance());
		this.setBalanceChanges(0);
		this.setBankBalance(0);
		this.setBankBalanceChanges(0);
	}

	public PlayerBalance(OfflinePlayer player, double balance) {
		this.player = player;
		this.worldGroup = Core.getWorldGroupManager().getDefaultWorldgroup();
		this.gamemode = Core.getWorldGroupManager().getDefaultGameMode();
		this.setBalance(balance);
		this.setBalanceChanges(0);
		this.setBankBalance(0);
		this.setBankBalanceChanges(0);
	}

	public PlayerBalance(OfflinePlayer player, String worldgroup, GameMode gamemode) {
		this.player = player;
		this.worldGroup = worldgroup;
		this.gamemode = gamemode;
		this.setBalance(Core.getWorldGroupManager().getCurrentStartingBalance(worldgroup));
		this.setBalanceChanges(0);
		this.setBankBalance(0);
		this.setBankBalanceChanges(0);
	}

	public PlayerBalance(OfflinePlayer player, String worldGroup, GameMode gamemode, double balance) {
		this.player = player;
		this.worldGroup = worldGroup;
		this.gamemode = gamemode;
		this.setBalance(balance);
		this.setBalanceChanges(0);
		this.setBankBalance(0);
		this.setBankBalanceChanges(0);
	}

	public PlayerBalance(OfflinePlayer player, String worldGroup, GameMode gamemode, double balance,
			double balanceChanges, double bankBalance, double bankBalanceChanges) {
		this.player = player;
		this.worldGroup = worldGroup;
		this.gamemode = gamemode;
		this.setBalance(balance);
		this.setBalanceChanges(balanceChanges);
		this.setBankBalance(bankBalance);
		this.setBankBalanceChanges(bankBalanceChanges);
	}

	public PlayerBalance(OfflinePlayer player, PlayerBalance ps) {
		this.player = ps.getPlayer();
		this.worldGroup = ps.getWorldGroup();
		this.gamemode = ps.getGamemode();
		this.setBalance(ps.getBalance());
		this.setBalanceChanges(ps.getBalanceChanges());
		this.setBankBalance(ps.getBankBalance());
		this.setBankBalanceChanges(ps.getBankBalanceChanges());
	}

	/**
	 * @return the worldGroup
	 */
	public String getWorldGroup() {
		return worldGroup;
	}

	/**
	 * @param worldGroup the worldGroup to set
	 */
	public void setWorldGroup(String worldGroup) {
		this.worldGroup = worldGroup;
	}

	/**
	 * @return the gamemode
	 */
	public GameMode getGamemode() {
		return gamemode;
	}

	/**
	 * @param gamemode the gamemode to set
	 */
	public void setGamemode(GameMode gamemode) {
		this.gamemode = gamemode;
	}

	/**
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	/**
	 * convert data to a readable format.
	 */
	@Override
	public String toString() {
		return String.format(
				"PlayerBalance: {player: Name:%s, WorldGrp:%s, GameMode:%s, Balance: %s(+%s), BankBalance: %s(+%s)}",
				player.getName(), worldGroup, gamemode, balance, balanceChanges, bankBalance, bankBalanceChanges);
	}

	/**
	 * Get the players balance
	 * 
	 * @return
	 */
	public double getBalance() {
		return balance;
	}

	/**
	 * Set the players balance
	 * 
	 * @param balance
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getBalanceChanges() {
		return balanceChanges;
	}

	public void setBalanceChanges(double balanceChanges) {
		this.balanceChanges = balanceChanges;
	}

	/**
	 * Get the players bank balance
	 * 
	 * @return
	 */
	public double getBankBalance() {
		return bankBalance;
	}

	/**
	 * Set the players bank balance
	 * 
	 * @param bankBalance
	 */
	public void setBankBalance(double bankBalance) {
		this.bankBalance = bankBalance;
	}

	public double getBankBalanceChanges() {
		return bankBalanceChanges;
	}

	public void setBankBalanceChanges(double bankBalanceChanges) {
		this.bankBalanceChanges = bankBalanceChanges;
	}

	/**
	 * Get the players total wealth
	 * 
	 * @return
	 */
	public double getTotalWealth() {
		return balance + balanceChanges + bankBalance + bankBalanceChanges;
	}

}
