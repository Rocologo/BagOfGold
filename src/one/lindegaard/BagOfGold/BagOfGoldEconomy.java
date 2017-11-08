package one.lindegaard.BagOfGold;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
import one.lindegaard.BagOfGold.util.Misc;

public class BagOfGoldEconomy implements Economy {

	private BagOfGold plugin;

	public BagOfGoldEconomy(BagOfGold plugin) {
		this.plugin = plugin;
	}

	/**
	 * Checks if economy method is enabled.
	 * 
	 * @return Success or Failure
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * Gets name of economy method
	 * 
	 * @return Name of Economy Method
	 */
	@Override
	public String getName() {
		return "BagOfGold";
	}

	/**
	 * Some economy plugins round off after a certain number of digits. This
	 * function returns the number of digits the plugin keeps or -1 if no
	 * rounding occurs.
	 * 
	 * @return number of digits after the decimal point kept
	 */
	@Override
	public int fractionalDigits() {
		return 5;
	}

	/**
	 * Format amount into a human readable String This provides translation into
	 * economy specific formatting to improve consistency between plugins.
	 *
	 * @param amount
	 *            to format
	 * @return Human readable string describing amount
	 */
	@Override
	public String format(double money) {
		return Misc.format(money);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)}
	 *             instead.
	 */
	@Override
	public double getBalance(String playername) {
		return getBalance(Bukkit.getOfflinePlayer(playername));
	}

	/**
	 * Gets balance of a player
	 * 
	 * @param player
	 *            of the player
	 * @return Amount currently held in players account
	 */
	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				Player player = (Player) offlinePlayer;
				double amountInInventory = 0;
				for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
					ItemStack is = player.getInventory().getItem(slot);
					if (Reward.isReward(is)) {
						Reward reward = Reward.getReward(is);
						if (reward.isBagOfGoldReward())
							amountInInventory = amountInInventory + reward.getMoney();
					}
				}
				if (Misc.round(amountInInventory) != Misc.round(ps.getBalance() + ps.getBalanceChanges())) {
					if (ps.getBalanceChanges() == 0) {
						plugin.getMessages().debug("Warning %s has a balance problem (%s,%s). Adjusting balance to %s",
								offlinePlayer.getName(), ps.getBalance(), amountInInventory,
								ps.getBalance() + ps.getBalanceChanges());
						if (amountInInventory > ps.getBalance() + ps.getBalanceChanges()) {
							plugin.getMessages().debug("removing %s from playerinventory",
									Misc.round(amountInInventory - (ps.getBalance() + ps.getBalanceChanges())));
							plugin.getEconomyManager().removeBagOfGoldPlayer_EconomyManager(player,
									Misc.round(amountInInventory - (ps.getBalance() + ps.getBalanceChanges())));
						} else {
							plugin.getMessages().debug("adding %s to playerinventory",
									ps.getBalance() + ps.getBalanceChanges() - amountInInventory);
							plugin.getEconomyManager().addBagOfGoldPlayer_EconomyManager(player,
									Misc.round(ps.getBalance() + ps.getBalanceChanges() - amountInInventory));
						}
						ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges()));
						ps.setBalanceChanges(0);
					} else {
						plugin.getMessages().debug(
								"%s - the balance changed while offline (%s+%s). Adjusting balance to %s",
								offlinePlayer.getName(), ps.getBalance(), ps.getBalanceChanges(),
								ps.getBalance() + ps.getBalanceChanges());
						double taken = 0;
						if (ps.getBalanceChanges() > 0)
							plugin.getEconomyManager().addBagOfGoldPlayer_EconomyManager(player,
									ps.getBalanceChanges());
						else
							taken = plugin.getEconomyManager().removeBagOfGoldPlayer_EconomyManager(player,
									ps.getBalanceChanges());
						plugin.getMessages().debug("New Changes=%s", ps.getBalance() + ps.getBalanceChanges() - taken);
						ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - taken));
						plugin.getMessages().debug("New balance=%s", ps.getBalance() + ps.getBalanceChanges());
						ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges()));
					}
					plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
					plugin.getDataStoreManager().updatePlayerSettings(player, ps);

				} else {
					if (ps.getBalanceChanges() != 0) {
						plugin.getMessages().debug("Updating balance %s with changes %s", Misc.round(ps.getBalance()),
								Misc.round(ps.getBalanceChanges()));
						ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges()));
						ps.setBalanceChanges(0);
						plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
						plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
					}
				}
			} else //player is known but not online
				return ps.getBalance() + ps.getBalanceChanges();
		}
		return 0;
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #getBalance(OfflinePlayer, String)} instead.
	 */
	@Override
	public double getBalance(String playername, String world) {
		return getBalance(playername);
	}

	/**
	 * Gets balance of a player on the specified world. IMPLEMENTATION SPECIFIC
	 * - if an economy plugin does not support this the global balance will be
	 * returned.
	 * 
	 * @param player
	 *            to check
	 * @param world
	 *            name of the world
	 * @return Amount currently held in players account
	 */
	@Override
	public double getBalance(OfflinePlayer offlinePlayer, String world) {
		return getBalance(offlinePlayer);
	}

	/**
	 * Gets the list of banks
	 * 
	 * @return the List of Banks
	 */
	@Override
	public List<String> getBanks() {
		return new ArrayList<String>();
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #createPlayerAccount(OfflinePlayer)} instead.
	 */
	@Override
	public boolean createPlayerAccount(String playername) {
		return createPlayerAccount(Bukkit.getServer().getOfflinePlayer(playername));
	}

	/**
	 * Attempts to create a player account for the given player. The player is
	 * auto-created when the player logon to the server.
	 * 
	 * @param player
	 *            OfflinePlayer
	 * @return if the account creation was successful
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
		return true;
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #createPlayerAccount(OfflinePlayer, String)} instead.
	 */
	@Override
	public boolean createPlayerAccount(String playername, String world) {
		return createPlayerAccount(Bukkit.getServer().getOfflinePlayer(playername), world);
	}

	/**
	 * Attempts to create a player account for the given player on the specified
	 * world IMPLEMENTATION SPECIFIC - if an economy plugin does not support
	 * this the global balance will be returned.
	 * 
	 * @param player
	 *            OfflinePlayer
	 * @param worldName
	 *            String name of the world
	 * @return if the account creation was successful
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String world) {
		return true;
	}

	/**
	 * Returns the name of the currency in plural form. If the economy being
	 * used does not support currency names then an empty string will be
	 * returned.
	 * 
	 * @return name of the currency (plural)
	 */
	@Override
	public String currencyNamePlural() {
		return plugin.getConfigManager().dropMoneyOnGroundSkullRewardNamePlural;
	}

	/**
	 * Returns the name of the currency in singular form. If the economy being
	 * used does not support currency names then an empty string will be
	 * returned.
	 * 
	 * @return name of the currency (singular)
	 */
	@Override
	public String currencyNameSingular() {
		return plugin.getConfigManager().dropMoneyOnGroundSkullRewardName;
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #depositPlayer(OfflinePlayer, double)} instead.
	 */
	@Override
	public EconomyResponse depositPlayer(String playername, double amount) {
		return depositPlayer(Bukkit.getOfflinePlayer(playername), amount);
	}

	/**
	 * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player
	 *            to deposit to
	 * @param amount
	 *            Amount to deposit
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		if (offlinePlayer != null) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
			plugin.getMessages().debug("Adding %s to balance %s", amount, ps.getBalance() + ps.getBalanceChanges());
			if (offlinePlayer.isOnline()) {
				ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() + amount));
				ps.setBalanceChanges(0);
				plugin.getEconomyManager().addBagOfGoldPlayer_EconomyManager((Player) offlinePlayer, amount);
			} else {
				ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() + amount));
			}
			plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			plugin.getPlayerSettingsManager().save(offlinePlayer);
			return new EconomyResponse(amount, ps.getBalance() + ps.getBalanceChanges(), ResponseType.SUCCESS, null);
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, "unknown player");
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #depositPlayer(OfflinePlayer, String, double)}
	 *             instead.
	 */
	@Override
	public EconomyResponse depositPlayer(String playername, String world, double amount) {
		return depositPlayer(playername, amount);
	}

	/**
	 * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
	 * IMPLEMENTATION SPECIFIC - if an economy plugin does not support this the
	 * global balance will be returned.
	 * 
	 * @param player
	 *            to deposit to
	 * @param worldName
	 *            name of the world
	 * @param amount
	 *            Amount to deposit
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
		return depositPlayer(offlinePlayer, amount);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)}
	 *             instead.
	 */
	@Override
	public boolean has(String playername, double amount) {
		return has(Bukkit.getOfflinePlayer(playername), amount);
	}

	/**
	 * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player
	 *            to check
	 * @param amount
	 *            to check for
	 * @return True if <b>player</b> has <b>amount</b>, False else wise
	 */
	@Override
	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		if (offlinePlayer == null)
			return false;
		return getBalance(offlinePlayer) >= amount;
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use @{link
	 *             {@link #has(OfflinePlayer, String, double)} instead.
	 */
	@Override
	public boolean has(String playername, String world, double amount) {
		return has(playername, amount);
	}

	/**
	 * Checks if the player account has the amount in a given world - DO NOT USE
	 * NEGATIVE AMOUNTS IMPLEMENTATION SPECIFIC - if an economy plugin does not
	 * support this the global balance will be returned.
	 * 
	 * @param player
	 *            to check
	 * @param worldName
	 *            to check with
	 * @param amount
	 *            to check for
	 * @return True if <b>player</b> has <b>amount</b>, False else wise
	 */
	@Override
	public boolean has(OfflinePlayer offlinePlayer, String world, double amount) {
		return has(offlinePlayer, amount);
	}

	/**
	 * 
	 * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)}
	 *             instead.
	 */
	@Override
	public boolean hasAccount(String playername) {
		return true;
	}

	/**
	 * Checks if this player has an account on the server yet This will always
	 * return true if the player has joined the server at least once as all
	 * major economy plugins auto-generate a player account when the player
	 * joins the server
	 * 
	 * @param player
	 *            to check
	 * @return if the player has an account
	 */
	@Override
	public boolean hasAccount(OfflinePlayer player) {
		return true;
	}

	/**
	 * Checks if this player has an account on the server yet on the given world
	 * This will always return true if the player has joined the server at least
	 * once as all major economy plugins auto-generate a player account when the
	 * player joins the server
	 * 
	 * @param player
	 *            to check in the world
	 * @param worldName
	 *            world-specific account
	 * @return if the player has an account
	 */
	@Override
	public boolean hasAccount(String playername, String world) {
		return true;
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
		return true;
	}

	/**
	 * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player
	 *            to withdraw from
	 * @param amount
	 *            Amount to withdraw
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		if (amount < 0)
			return new EconomyResponse(0, 0, ResponseType.FAILURE, "Cannot withdraw negative funds");

		if (offlinePlayer != null) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
			if (ps.getBalance() + ps.getBalanceChanges() >= amount) {
				plugin.getMessages().debug("Removing %s from balance %s", amount,
						ps.getBalance() + ps.getBalanceChanges());
				if (offlinePlayer.isOnline()) {
					ps.setBalance(ps.getBalance() + ps.getBalanceChanges() - amount);
					ps.setBalanceChanges(0);
					plugin.getEconomyManager().removeBagOfGoldPlayer_EconomyManager((Player) offlinePlayer, amount);
				} else {
					ps.setBalanceChanges(ps.getBalanceChanges() - amount);
				}
				plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
				plugin.getPlayerSettingsManager().save(offlinePlayer);
				return new EconomyResponse(0, ps.getBalance(), ResponseType.SUCCESS, null);
			} else
				return new EconomyResponse(0, ps.getBalance(), ResponseType.FAILURE, "Insufficient funds");
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, "unknown player");
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #withdrawPlayer(OfflinePlayer, double)} instead.
	 */
	@Override
	public EconomyResponse withdrawPlayer(String playername, double amount) {
		return withdrawPlayer(Bukkit.getOfflinePlayer(playername), amount);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #withdrawPlayer(OfflinePlayer, String, double)}
	 *             instead.
	 */
	@Override
	public EconomyResponse withdrawPlayer(String playername, String world, double amount) {
		return withdrawPlayer(playername, amount);
	}

	/**
	 * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE
	 * AMOUNTS IMPLEMENTATION SPECIFIC - if an economy plugin does not support
	 * this the global balance will be returned.
	 * 
	 * @param player
	 *            to withdraw from
	 * @param worldName
	 *            - name of the world
	 * @param amount
	 *            Amount to withdraw
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
		return withdrawPlayer(offlinePlayer, amount);
	}

	/**
	 * Returns true if the given implementation supports banks.
	 * 
	 * @return true if the implementation supports banks
	 */
	@Override
	public boolean hasBankSupport() {
		return false;
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #isBankMember(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse isBankMember(String account, String playername) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * Check if the player is a member of the bank account
	 * 
	 * @param name
	 *            of the account
	 * @param player
	 *            to check membership
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse isBankMember(String account, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #isBankOwner(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse isBankOwner(String account, String playername) {
		return isBankMember(account, Bukkit.getOfflinePlayer(playername));
	}

	/**
	 * Check if a player is the owner of a bank account
	 * 
	 * @param name
	 *            of the account
	 * @param player
	 *            to check for ownership
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse isBankOwner(String account, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * Returns the amount the bank has
	 * 
	 * @param name
	 *            of the account
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankBalance(String account) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param name
	 *            of the account
	 * @param amount
	 *            to deposit
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankDeposit(String account, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * Returns true or false whether the bank has the amount specified - DO NOT
	 * USE NEGATIVE AMOUNTS
	 * 
	 * @param name
	 *            of the account
	 * @param amount
	 *            to check for
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankHas(String account, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param name
	 *            of the account
	 * @param amount
	 *            to withdraw
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankWithdraw(String account, double amount) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #createBank(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse createBank(String account, String playername) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * Creates a bank account with the specified name and the player as the
	 * owner
	 * 
	 * @param name
	 *            of account
	 * @param player
	 *            the account should be linked to
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse createBank(String account, OfflinePlayer player) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

	/**
	 * Deletes a bank account with the specified name.
	 * 
	 * @param name
	 *            of the bank to delete
	 * @return if the operation completed successfully
	 */
	@Override
	public EconomyResponse deleteBank(String account) {
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "BagOfGold does not support bank accounts!");
	}

}
