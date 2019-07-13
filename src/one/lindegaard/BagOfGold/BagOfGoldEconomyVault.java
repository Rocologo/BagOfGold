package one.lindegaard.BagOfGold;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import one.lindegaard.Core.Tools;

public class BagOfGoldEconomyVault implements Economy, Listener {

	private BagOfGold plugin;
	private Economy mEconomy;

	public BagOfGoldEconomyVault(BagOfGold plugin) {
		this.plugin = plugin;

		if (!isEnabled()) {
			// BagOfGold is NOT used as an Economy plugin
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager()
					.getRegistration(Economy.class);
			if (economyProvider == null) {
				Bukkit.getLogger().severe("[BagOfGold][Vault]"
						+ plugin.getMessages().getString(plugin.getName().toLowerCase() + ".hook.econ"));
				// Bukkit.getPluginManager().disablePlugin(plugin);
				return;
			}
			mEconomy = economyProvider.getProvider();
		}
		
		plugin.getMessages().debug("Number of Vault Economy Providers = %s",
				Bukkit.getServicesManager().getRegistrations(Economy.class).size());
		if (Bukkit.getServicesManager().getRegistrations(Economy.class).size() > 1) {
			for (RegisteredServiceProvider<Economy> registation : Bukkit.getServicesManager()
					.getRegistrations(Economy.class)) {
				plugin.getMessages().debug("Vault provider name=%s", registation.getProvider().getName());
			}
		}



	}

	// ************************************************************************************
	// Hook into Vault (Economy)
	// ************************************************************************************

	public static void hookVaultEconomy(Class<? extends Economy> hookClass, ServicePriority priority,
			String... packages) {
		try {
			if (packagesExists(packages)) {
				Economy vaultEconomy = hookClass.getConstructor(Plugin.class).newInstance(BagOfGold.getInstance());
				Plugin vaultPlugin = Bukkit.getPluginManager().getPlugin("Vault");
				if (vaultPlugin != null)
					Bukkit.getServicesManager().register(Economy.class, vaultEconomy, vaultPlugin,
							ServicePriority.Normal);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET + String.format(
						"[BagOfGold][Economy] Vault found: %s", vaultEconomy.isEnabled() ? "Loaded" : "Waiting"));
			}
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET + String.format(
					"[Economy] There was an error hooking into Vault - check to make sure you're using a compatible version!"));
		}
	}

	/**
	 * Determines if all packages in a String array are within the Classpath This is
	 * the best way to determine if a specific plugin exists and will be loaded. If
	 * the plugin package isn't loaded, we shouldn't bother waiting for it!
	 * 
	 * @param packages String Array of package names to check
	 * @return Success or Failure
	 */
	private static boolean packagesExists(String... packages) {
		try {
			for (String pkg : packages) {
				Class.forName(pkg);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Checks if economy method is enabled.
	 * 
	 * @return Success or Failure
	 */
	@Override
	public boolean isEnabled() {
		return plugin.getConfigManager().useBagOfGoldAsAnEconomyPlugin;
	}

	/**
	 * Gets name of economy method
	 * 
	 * @return Name of Economy Method
	 */
	@Override
	public String getName() {
		if (isEnabled())
			return "BagOfGold";
		else
			return mEconomy.getName();
	}

	/**
	 * Some economy plugins round off after a certain number of digits. This
	 * function returns the number of digits the plugin keeps or -1 if no rounding
	 * occurs.
	 * 
	 * @return number of digits after the decimal point kept
	 */
	@Override
	public int fractionalDigits() {
		if (isEnabled())
			return 5;
		else
			return mEconomy.fractionalDigits();
	}

	/**
	 * Format amount into a human readable String This provides translation into
	 * economy specific formatting to improve consistency between plugins.
	 *
	 * @param amount to format
	 * @return Human readable string describing amount
	 */
	@Override
	public String format(double money) {
		if (isEnabled())
			return Tools.format(money);
		else
			return mEconomy.format(money);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer)}
	 *             instead.
	 */
	@Override
	public double getBalance(String playername) {
		if (isEnabled())
			return getBalance(Bukkit.getOfflinePlayer(playername));
		else
			return mEconomy.getBalance(playername);
	}

	/**
	 * Gets balance of a player
	 * 
	 * @param player of the player
	 * @return Amount currently held in players account
	 */
	@Override
	public double getBalance(OfflinePlayer offlinePlayer) {
		if (isEnabled())
			return plugin.getRewardManager().getBalance(offlinePlayer);
		else
			return mEconomy.getBalance(offlinePlayer);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use {@link #getBalance(OfflinePlayer, String)}
	 *             instead.
	 */
	@Override
	public double getBalance(String playername, String world) {
		if (isEnabled())
			return getBalance(playername);
		else
			return mEconomy.getBalance(playername, world);
	}

	/**
	 * Gets balance of a player on the specified world. IMPLEMENTATION SPECIFIC - if
	 * an economy plugin does not support this the global balance will be returned.
	 * 
	 * @param player to check
	 * @param world  name of the world
	 * @return Amount currently held in players account
	 */
	@Override
	public double getBalance(OfflinePlayer offlinePlayer, String world) {
		if (isEnabled())
			return getBalance(offlinePlayer);
		else
			return mEconomy.getBalance(offlinePlayer, world);
	}

	/**
	 * Gets the list of banks
	 * 
	 * @return the List of Banks
	 */
	@Override
	public List<String> getBanks() {
		if (isEnabled())
			return new ArrayList<String>();
		else
			return mEconomy.getBanks();
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #createPlayerAccount(OfflinePlayer)} instead.
	 */
	@Override
	public boolean createPlayerAccount(String playername) {
		if (isEnabled())
			return createPlayerAccount(Bukkit.getServer().getOfflinePlayer(playername));
		else
			return mEconomy.createPlayerAccount(playername);
	}

	/**
	 * Attempts to create a player account for the given player. The player is
	 * auto-created when the player logon to the server.
	 * 
	 * @param player OfflinePlayer
	 * @return if the account creation was successful
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
		if (isEnabled())
			return true;
		else
			return mEconomy.createPlayerAccount(offlinePlayer);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #createPlayerAccount(OfflinePlayer, String)} instead.
	 */
	@Override
	public boolean createPlayerAccount(String playername, String world) {
		if (isEnabled())
			return createPlayerAccount(Bukkit.getServer().getOfflinePlayer(playername), world);
		else
			return mEconomy.createPlayerAccount(playername, world);
	}

	/**
	 * Attempts to create a player account for the given player on the specified
	 * world IMPLEMENTATION SPECIFIC - if an economy plugin does not support this
	 * the global balance will be returned.
	 * 
	 * @param player    OfflinePlayer
	 * @param worldName String name of the world
	 * @return if the account creation was successful
	 */
	@Override
	public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String world) {
		if (isEnabled())
			return true;
		else
			return mEconomy.createPlayerAccount(offlinePlayer, world);
	}

	/**
	 * Returns the name of the currency in plural form. If the economy being used
	 * does not support currency names then an empty string will be returned.
	 * 
	 * @return name of the currency (plural)
	 */
	@Override
	public String currencyNamePlural() {
		if (isEnabled())
			return plugin.getConfigManager().dropMoneyOnGroundSkullRewardNamePlural;
		else
			return mEconomy.currencyNamePlural();
	}

	/**
	 * Returns the name of the currency in singular form. If the economy being used
	 * does not support currency names then an empty string will be returned.
	 * 
	 * @return name of the currency (singular)
	 */
	@Override
	public String currencyNameSingular() {
		if (isEnabled())
			return plugin.getConfigManager().dropMoneyOnGroundSkullRewardName;
		else
			return mEconomy.currencyNameSingular();
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #depositPlayer(OfflinePlayer, double)} instead.
	 */
	@Override
	public EconomyResponse depositPlayer(String playername, double amount) {
		if (isEnabled())
			return depositPlayer(Bukkit.getOfflinePlayer(playername), amount);
		else
			return mEconomy.depositPlayer(playername, amount);
	}

	/**
	 * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player to deposit to
	 * @param amount Amount to deposit
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		if (isEnabled())
			return plugin.getRewardManager().depositPlayer(offlinePlayer, amount);
		else
			return mEconomy.depositPlayer(offlinePlayer, amount);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #depositPlayer(OfflinePlayer, String, double)} instead.
	 */
	@Override
	public EconomyResponse depositPlayer(String playername, String world, double amount) {
		if (isEnabled())
			return depositPlayer(playername, amount);
		else
			return mEconomy.depositPlayer(playername, world, amount);
	}

	/**
	 * Deposit an amount to a player - DO NOT USE NEGATIVE AMOUNTS IMPLEMENTATION
	 * SPECIFIC - if an economy plugin does not support this the global balance will
	 * be returned.
	 * 
	 * @param player    to deposit to
	 * @param worldName name of the world
	 * @param amount    Amount to deposit
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
		if (isEnabled())
			return depositPlayer(offlinePlayer, amount);
		else
			return mEconomy.depositPlayer(offlinePlayer, world, amount);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use {@link #has(OfflinePlayer, double)}
	 *             instead.
	 */
	@Override
	public boolean has(String playername, double amount) {
		if (isEnabled())
			return has(Bukkit.getOfflinePlayer(playername), amount);
		else
			return mEconomy.has(playername, amount);
	}

	/**
	 * Checks if the player account has the amount - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player to check
	 * @param amount to check for
	 * @return True if <b>player</b> has <b>amount</b>, False else wise
	 */
	@Override
	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		if (isEnabled())
			return getBalance(offlinePlayer) >= amount;
		else
			return mEconomy.has(offlinePlayer, amount);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use @{link
	 *             {@link #has(OfflinePlayer, String, double)} instead.
	 */
	@Override
	public boolean has(String playername, String world, double amount) {
		if (isEnabled())
			return has(playername, amount);
		else
			return mEconomy.has(playername, world, amount);
	}

	/**
	 * Checks if the player account has the amount in a given world - DO NOT USE
	 * NEGATIVE AMOUNTS IMPLEMENTATION SPECIFIC - if an economy plugin does not
	 * support this the global balance will be returned.
	 * 
	 * @param player    to check
	 * @param worldName to check with
	 * @param amount    to check for
	 * @return True if <b>player</b> has <b>amount</b>, False else wise
	 */
	@Override
	public boolean has(OfflinePlayer offlinePlayer, String world, double amount) {
		if (isEnabled())
			return has(offlinePlayer, amount);
		else
			return mEconomy.has(offlinePlayer, world, amount);
	}

	/**
	 * 
	 * @deprecated As of VaultAPI 1.4 use {@link #hasAccount(OfflinePlayer)}
	 *             instead.
	 */
	@Override
	public boolean hasAccount(String playername) {
		if (isEnabled())
			return true;
		else
			return mEconomy.hasAccount(playername);
	}

	/**
	 * Checks if this player has an account on the server yet This will always
	 * return true if the player has joined the server at least once as all major
	 * economy plugins auto-generate a player account when the player joins the
	 * server
	 * 
	 * @param offlinePlayer to check
	 * @return if the player has an account
	 */
	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer) {
		if (isEnabled())
			return true;
		else
			return mEconomy.hasAccount(offlinePlayer);
	}

	/**
	 * Checks if this player has an account on the server yet on the given world
	 * This will always return true if the player has joined the server at least
	 * once as all major economy plugins auto-generate a player account when the
	 * player joins the server
	 * 
	 * @param player    to check in the world
	 * @param worldName world-specific account
	 * @return if the player has an account
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean hasAccount(String playername, String world) {
		if (isEnabled())
			return true;
		else
			return mEconomy.hasAccount(playername, world);
	}

	@Override
	public boolean hasAccount(OfflinePlayer offlinePlayer, String world) {
		if (isEnabled())
			return true;
		else
			return mEconomy.hasAccount(offlinePlayer, world);
	}

	/**
	 * Withdraw an amount from a player - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param player to withdraw from
	 * @param amount Amount to withdraw
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		if (isEnabled())
			return plugin.getRewardManager().withdrawPlayer(offlinePlayer, amount);
		else
			return mEconomy.withdrawPlayer(offlinePlayer, amount);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #withdrawPlayer(OfflinePlayer, double)} instead.
	 */
	@Override
	public EconomyResponse withdrawPlayer(String playername, double amount) {
		if (isEnabled())
			return withdrawPlayer(Bukkit.getOfflinePlayer(playername), amount);
		else
			return mEconomy.withdrawPlayer(playername, amount);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {@link #withdrawPlayer(OfflinePlayer, String, double)} instead.
	 */
	@Override
	public EconomyResponse withdrawPlayer(String playername, String world, double amount) {
		if (isEnabled())
			return withdrawPlayer(playername, amount);
		else
			return mEconomy.withdrawPlayer(playername, world, amount);
	}

	/**
	 * Withdraw an amount from a player on a given world - DO NOT USE NEGATIVE
	 * AMOUNTS IMPLEMENTATION SPECIFIC - if an economy plugin does not support this
	 * the global balance will be returned.
	 * 
	 * @param player    to withdraw from
	 * @param worldName - name of the world
	 * @param amount    Amount to withdraw
	 * @return Detailed response of transaction
	 */
	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String world, double amount) {
		if (isEnabled())
			return withdrawPlayer(offlinePlayer, amount);
		else
			return mEconomy.withdrawPlayer(offlinePlayer, world, amount);
	}

	/**
	 * Returns true if the given implementation supports banks.
	 * 
	 * @return true if the implementation supports banks
	 */
	@Override
	public boolean hasBankSupport() {
		if (isEnabled())
			return true;
		else
			return mEconomy.hasBankSupport();
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #isBankMember(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse isBankMember(String account, String playername) {
		if (isEnabled())
			return isBankMember(account, Bukkit.getOfflinePlayer(playername));
		else
			return mEconomy.isBankMember(account, playername);
	}

	/**
	 * Check if the player is a member of the bank account
	 * 
	 * @param name          of the account
	 * @param offlinePlayer to check membership
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse isBankMember(String account, OfflinePlayer offlinePlayer) {
		if (isEnabled())
			return plugin.getRewardManager().isBankMember(account, offlinePlayer);
		else
			return mEconomy.isBankOwner(account, offlinePlayer);
	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #isBankOwner(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse isBankOwner(String account, String playername) {
		if (isEnabled())
			return isBankOwner(account, Bukkit.getOfflinePlayer(playername));
		else
			return mEconomy.isBankOwner(account, playername);
	}

	/**
	 * Check if a player is the owner of a bank account
	 * 
	 * @param name          of the account
	 * @param offlinePlayer to check for ownership
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse isBankOwner(String account, OfflinePlayer offlinePlayer) {
		if (isEnabled())
			return plugin.getRewardManager().isBankOwner(account, offlinePlayer);
		else
			return mEconomy.isBankOwner(account, offlinePlayer);
	}

	/**
	 * Returns the amount the bank has
	 * 
	 * @param name of the account
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankBalance(String account) {
		if (isEnabled())
			return plugin.getRewardManager().bankBalance(account);
		else
			return mEconomy.bankBalance(account);
	}

	/**
	 * Deposit an amount into a bank account - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param name   of the account
	 * @param amount to deposit
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankDeposit(String account, double amount) {
		if (isEnabled())
			return plugin.getRewardManager().bankDeposit(account, amount);
		else
			return mEconomy.bankDeposit(account, amount);
	}

	/**
	 * Returns true or false whether the bank has the amount specified - DO NOT USE
	 * NEGATIVE AMOUNTS
	 * 
	 * @param name   of the account
	 * @param amount to check for
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankHas(String account, double amount) {
		if (isEnabled()) {
			double bal = bankBalance(account).amount;
			if (bal > amount)
				return new EconomyResponse(amount, bal, ResponseType.SUCCESS, null);
			else
				return new EconomyResponse(amount, bal, ResponseType.FAILURE, null);
		} else
			return mEconomy.bankHas(account, amount);

	}

	/**
	 * Withdraw an amount from a bank account - DO NOT USE NEGATIVE AMOUNTS
	 * 
	 * @param name   of the account
	 * @param amount to withdraw
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse bankWithdraw(String account, double amount) {
		if (isEnabled())
			return plugin.getRewardManager().bankWithdraw(account, amount);
		else
			return mEconomy.bankWithdraw(account, amount);

	}

	/**
	 * @deprecated As of VaultAPI 1.4 use
	 *             {{@link #createBank(String, OfflinePlayer)} instead.
	 */
	@Override
	public EconomyResponse createBank(String account, String playername) {
		if (isEnabled())
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		else
			return mEconomy.createBank(account, playername);
	}

	/**
	 * Creates a bank account with the specified name and the player as the owner
	 * 
	 * @param name   of account
	 * @param player the account should be linked to
	 * @return EconomyResponse Object
	 */
	@Override
	public EconomyResponse createBank(String account, OfflinePlayer player) {
		if (isEnabled())
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, null);
		else
			return mEconomy.createBank(account, player);
	}

	/**
	 * Deletes a bank account with the specified name.
	 * 
	 * @param name of the bank to delete
	 * @return if the operation completed successfully
	 */
	@Override
	public EconomyResponse deleteBank(String account) {
		if (isEnabled())
			return plugin.getRewardManager().deleteBank(account);
		else
			return mEconomy.deleteBank(account);
	}

}
