package one.lindegaard.BagOfGold.rewards;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.util.Misc;

public class EconomyManager implements Listener {

	private BagOfGold plugin;

	public EconomyManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getPluginManager().registerEvents(new RewardListeners(plugin), plugin);
	}

	/**
	 * getBalance : calculate the player balance and checks if the player
	 * balance is equal with the amount of money in the inventory. If there is a
	 * difference it checks if there has been changes while the player was
	 * offline if not the balance / amount in inventory will be adjusted.
	 * 
	 * @param offlinePlayer
	 * @return
	 */
	public double getBalance(OfflinePlayer offlinePlayer) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		return ps.getBalance() + ps.getBalanceChanges();
	}

	/**
	 * depositPlayer : deposit the amount to the players balance and add the
	 * mount to the players inventory. Do not deposit negative amounts!
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return EconomyResponce containing amount, balance and ResponseType
	 *         (Success/failure)
	 */
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		if (amount == 0) {
			return new EconomyResponse(0, Misc.round(ps.getBalance() + ps.getBalanceChanges()), ResponseType.SUCCESS,
					null);
		} else if (amount > 0) {
			if (offlinePlayer.isOnline()) {
				addMoneyToPlayer((Player) offlinePlayer,
						// Misc.round(ps.getBalance()) +
						Misc.round(ps.getBalanceChanges()) + Misc.round(amount));
				ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() + amount));
				ps.setBalanceChanges(0);
			} else {
				ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() + amount));
			}
			plugin.getMessages().debug("Deposit %s to %s's account, new balance is %s", format(amount),
					offlinePlayer.getName(), format(ps.getBalance() + ps.getBalanceChanges()));
			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			if (offlinePlayer.isOnline() && ((Player) offlinePlayer).isValid())
				adjustAmountOfMoneyInInventoryToBalance((Player) offlinePlayer);
			return new EconomyResponse(amount, Misc.round(ps.getBalance() + ps.getBalanceChanges()),
					ResponseType.SUCCESS, null);
		} else {
			plugin.getMessages().debug("Could not deposit %s to %s's account, because the number is negative",
					format(amount), offlinePlayer.getName());
			return new EconomyResponse(0, Misc.round(ps.getBalance() + ps.getBalanceChanges()), ResponseType.FAILURE,
					null);
		}
	}

	/**
	 * withdrawPlayer : withdraw the amount from the players balance and remove
	 * the mount to the players inventory. Do not withdraw negative amounts.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return EconomyResponse containing amount withdraw, balance and
	 *         ResponseType (Success/Failure).
	 */
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		if (amount >= 0) {
			if (has(offlinePlayer, amount)) {
				if (offlinePlayer.isOnline()) {
					removeBagOfGoldPlayer((Player) offlinePlayer, amount + Misc.round(ps.getBalanceChanges()));
					ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() - amount));
					ps.setBalanceChanges(0);
				} else
					ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - amount));
				plugin.getMessages().debug("Withdraw %s from %s's account, new balance is %s", format(amount),
						offlinePlayer.getName(), format(ps.getBalance() + ps.getBalanceChanges()));
				plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
				if (offlinePlayer.isOnline() && ((Player) offlinePlayer).isValid())
					adjustAmountOfMoneyInInventoryToBalance((Player) offlinePlayer);
				return new EconomyResponse(amount, Misc.round(ps.getBalance() + ps.getBalanceChanges()),
						ResponseType.SUCCESS, null);
			} else {
				double remove = Misc.round(ps.getBalance() + ps.getBalanceChanges());
				plugin.getMessages().debug("%s has not enough bagofgold, Withdrawing only %s , new balance is %s",
						offlinePlayer.getName(), format(remove), format(0));
				if (remove > 0) {
					removeBagOfGoldPlayer((Player) offlinePlayer, remove);
					ps.setBalance(0);
					ps.setBalanceChanges(0);
					plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
					return new EconomyResponse(remove, 0, ResponseType.SUCCESS, null);
				}
				return new EconomyResponse(remove, 0, ResponseType.FAILURE,
						plugin.getMessages().getString("bagofgold.commands.money.not-enough-money", "money", remove));
			}
		} else
			return new EconomyResponse(0, ps.getBalance() + ps.getBalanceChanges(), ResponseType.SUCCESS, null);
	}

	/**
	 * has : checks if the player has amount of mount on his balance.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return true if the player has the amount on his money.
	 */
	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance pb = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		plugin.getMessages().debug("Check if %s has %s %s on the balance=%s)", offlinePlayer.getName(), format(amount),
				plugin.getConfigManager().dropMoneyOnGroundSkullRewardName,
				format(pb.getBalance() + pb.getBalanceChanges()));
		return Misc.round(pb.getBalance()) + Misc.round(pb.getBalanceChanges()) >= Misc.round(amount);
	}

	/**
	 * addBagOfGoldPlayer_EconomyManager: add amount to the player inventory,
	 * but NOT on player balance.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public double addMoneyToPlayer(Player player, double amount) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().addBagOfGoldMoneyToPlayer(player, amount);
		else if (plugin.getgringottsItems().isGringottsStyle())
			return plugin.getgringottsItems().addGringottsMoneyToPlayer(player, amount);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ plugin.getConfigManager().dropMoneyOnGroundItemtype + "'");
			return 0;
		}
	}

	/**
	 * removeBagOfGoldPlayer_EconomyManager: remove the amount from the player
	 * inventory but NOT from the player balance.
	 * 
	 * @param player
	 * @param amount
	 * @return
	 */
	public double removeBagOfGoldPlayer(Player player, double amount) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().removeBagOfGoldFromPlayer(player, amount);
		else if (plugin.getgringottsItems().isGringottsStyle())
			return plugin.getgringottsItems().removeGringottsMoneyFromPlayer(player, amount);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ plugin.getConfigManager().dropMoneyOnGroundItemtype + "'");
			return 0;
		}
	}

	/**
	 * dropMoneyOnGround_EconomyManager: drop the amount of money in the
	 * location
	 * 
	 * @param player
	 *            - not used in EconomyManager
	 * @param killedEntity
	 *            - not used in EconomyManager
	 * @param location
	 * @param money
	 */
	public void dropMoneyOnGround_EconomyManager(Player player, Entity killedEntity, Location location, double amount) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			plugin.getBagOfGoldItems().dropBagOfGoldMoneyOnGround(player, killedEntity, location, amount);
		else if (plugin.getgringottsItems().isGringottsStyle())
			plugin.getgringottsItems().dropGringottsMoneyOnGround(player, killedEntity, location, amount);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ plugin.getConfigManager().dropMoneyOnGroundItemtype + "'");
		}
	}

	/**
	 * bankDeposit: deposit the amount on the account.
	 * 
	 * @param account
	 *            - This is the player UUID as a String
	 * @param amount
	 * @return EconomyResponse containing amount, balance and ResponseType
	 *         (Success/Failure).
	 */
	@SuppressWarnings("deprecation")
	public EconomyResponse bankDeposit(String account, double amount) {
		OfflinePlayer offlinePlayer = Misc.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges() + amount));
				ps.setBankBalanceChanges(0);
			} else {
				ps.setBankBalanceChanges(ps.getBankBalanceChanges() + amount);
			}
			plugin.getMessages().debug("bankDeposit: %s", ps.toString());

			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			return new EconomyResponse(amount, ps.getBankBalance() + ps.getBankBalanceChanges(), ResponseType.SUCCESS,
					null);
		} else
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player has no bank account");
	}

	/**
	 * bankWithdraw: withdraw the amount from the account.
	 * 
	 * @param account
	 *            - This is the player UUID as a String
	 * @param amount
	 * @return EconomyResponse containing amount, balance and ResponseType
	 *         (Success/Failure).
	 */
	@SuppressWarnings("deprecation")
	public EconomyResponse bankWithdraw(String account, double amount) {
		OfflinePlayer offlinePlayer = Misc.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges() - amount));
				ps.setBankBalanceChanges(0);
			} else {
				ps.setBankBalanceChanges(ps.getBankBalanceChanges() - amount);
			}
			plugin.getMessages().debug("bankWithdraw: %s", ps.toString());

			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			return new EconomyResponse(amount, ps.getBankBalance() + ps.getBankBalanceChanges(), ResponseType.SUCCESS,
					null);
		} else
			return new EconomyResponse(0, 0, ResponseType.FAILURE, account + " has no bank account");
	}

	/**
	 * bankBalance: withdraw the amount from the account.
	 * 
	 * @param account
	 *            - This is the player UUID as a String
	 * @return EconomyResponse containing amount, bank balance and ResponseType
	 *         (Success/Failure).
	 */
	@SuppressWarnings("deprecation")
	public EconomyResponse bankBalance(String account) {
		OfflinePlayer offlinePlayer = Misc.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges()));
				ps.setBankBalanceChanges(0);
				plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			}
			return new EconomyResponse(0, ps.getBankBalance() + ps.getBankBalanceChanges(), ResponseType.SUCCESS, null);
		} else
			return new EconomyResponse(0, 0, ResponseType.FAILURE, account + " has no bank account");
	}

	/**
	 * deleteBank: Reset the account and set the bank balance to 0.
	 * 
	 * @param account
	 *            - this is the player UUID.
	 * @return ResponseType (Success/Failure)
	 */
	@SuppressWarnings("deprecation")
	public EconomyResponse deleteBank(String account) {
		OfflinePlayer offlinePlayer = Misc.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			ps.setBankBalance(0);
			ps.setBankBalanceChanges(0);
			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, "Bank account deleted");
		} else
			return new EconomyResponse(0, 0, ResponseType.FAILURE, account + " has no bank account");
	}

	/**
	 * Format the number
	 * 
	 * @param money
	 * @return
	 */
	public String format(double money) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().format(money);
		else if (plugin.getgringottsItems().isGringottsStyle())
			return plugin.getgringottsItems().format(money);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ plugin.getConfigManager().dropMoneyOnGroundItemtype + "'");
			return String.valueOf(money);
		}
	}

	/**
	 * getAmountInInventory: calculate the total BagOfGold in the player
	 * inventory.
	 * 
	 * @param player
	 * @return
	 */
	public double getAmountInInventory(Player player) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().getAmountOfBagOfGoldMoneyInInventory(player);
		else if (plugin.getgringottsItems().isGringottsStyle())
			return plugin.getgringottsItems().getAmountOfGringottsMoneyInInventory(player);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ plugin.getConfigManager().dropMoneyOnGroundItemtype + "'");
			return 0;
		}
	}

	/**
	 * removeMoneyFromBalance: remove the amount from the player balance without
	 * removing amount from the player inventory
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public void removeMoneyFromBalance(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		plugin.getMessages().debug("Removing %s from %s's balance %s", format(amount), offlinePlayer.getName(),
				format(ps.getBalance() + ps.getBalanceChanges()));

		if (offlinePlayer.isOnline()) {
			ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() - amount));
			ps.setBalanceChanges(0);
		} else {
			ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - amount));
		}
		plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
	}

	/**
	 * addMoneyToBalance: add amount to player balance but NOT on in the player
	 * inventory
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public void addMoneyToBalance(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		plugin.getMessages().debug("Adding %s to %s's balance %s", format(amount), offlinePlayer.getName(),
				format(ps.getBalance() + ps.getBalanceChanges()));
		ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() + amount));
		plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
	}

	public void adjustAmountOfMoneyInInventoryToBalance(Player player) {
		double amountInInventory = getAmountInInventory(player);
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		if (ps != null) {
			double diff = (Misc.round(ps.getBalance()) + Misc.round(ps.getBalanceChanges()))
					- Misc.round(amountInInventory);
			if (Misc.round(diff) != 0) {
				plugin.getMessages().debug("Adjusting amt to Balance: amt=%s, bal=%s(+%s)", amountInInventory,
						ps.getBalance(), ps.getBalanceChanges());
				addMoneyToPlayer(player, Misc.round(diff));
			}
		}
	}

	public void adjustBalanceToAmounOfMoneyInInventory(Player player) {
		double amountInInventory = getAmountInInventory(player);
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		ItemStack is = player.getItemOnCursor();
		double inHand = 0;
		if (Reward.isReward(is)) {
			Reward reward = Reward.getReward(is);
			if (reward.isBagOfGoldReward() || reward.isItemReward())
				inHand = reward.getMoney();
		}
		if (ps != null) {
			double diff = Misc.round(amountInInventory + inHand)
					- (Misc.round(ps.getBalance()) + Misc.round(ps.getBalanceChanges()));
			if (Misc.round(diff) != 0) {
				plugin.getMessages().debug("Adjusting Balance to amt: amt=%s, bal=%s(+%s)", amountInInventory,
						ps.getBalance(), ps.getBalanceChanges());
				addMoneyToBalance(player, Misc.round(diff));
			}
		}
	}

	public double getSpaceForMoney(Player player) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().getAmountOfBagOfGoldMoneyInInventory(player);
		else if (plugin.getgringottsItems().isGringottsStyle()) {
			return plugin.getgringottsItems().getSpaceForGringottsMoney(player);
		} else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ plugin.getConfigManager().dropMoneyOnGroundItemtype + "'");
			return 0;
		}
	}

}
