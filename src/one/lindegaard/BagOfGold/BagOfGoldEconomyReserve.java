package one.lindegaard.BagOfGold;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;

public class BagOfGoldEconomyReserve implements EconomyAPI {

	private BagOfGold plugin;
	private EconomyAPI mEconomy;

	public BagOfGoldEconomyReserve(BagOfGold plugin) {
		this.plugin = plugin;

		mEconomy = this;
		Reserve.instance().registerProvider(mEconomy);

		if (!enabled()) {
			RegisteredServiceProvider<EconomyAPI> economyProvider = Bukkit.getServicesManager()
					.getRegistration(EconomyAPI.class);
			if (economyProvider == null) {
				Bukkit.getLogger().severe("[BagOfGold][Reserve]"
						+ plugin.getMessages().getString(plugin.getName().toLowerCase() + ".hook.econ.reserve"));
				// Bukkit.getPluginManager().disablePlugin(plugin);
				return;
			}
			mEconomy = economyProvider.getProvider();
		}
	}

	/**
	 * @return The name of the Economy implementation.
	 */
	@Override
	public String name() {
		return "BagOfGold";
	}

	/**
	 * @return The version of Reserve the Economy implementation supports.
	 */
	@Override
	public String version() {
		return BagOfGold.getAPI().getDescription().getVersion();
	}

	/**
	 * @return Whether or not this implementation is enabled.
	 */
	@Override
	public boolean enabled() {
		return plugin.getConfigManager().useBagOfGoldAsAnEconomyPlugin;
	}

	// *******************************************************
	// NOT IMPLEMENTED
	// *******************************************************
	/**
	 * Used to get the plural name of the default currency.
	 * 
	 * @return The plural name of the default currency.
	 */
	@Override
	public String currencyDefaultPlural() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the singular name of the default currency.
	 * 
	 * @return The plural name of the default currency.
	 */
	@Override
	public String currencyDefaultPlural(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the plural name of the default currency for a world.
	 * 
	 * @param world The world to be used in this check.
	 * @return The plural name of the default currency.
	 */
	@Override
	public String currencyDefaultSingular() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the singular name of the default currency for a world.
	 * 
	 * @param world The world to be used in this check.
	 * @return The plural name of the default currency.
	 */
	@Override
	public String currencyDefaultSingular(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Checks to see if a {@link Currency} exists with this name.
	 * 
	 * @param name The name of the {@link Currency} to search for.
	 * @return True if the currency exists, else false.
	 */
	@Override
	public boolean hasCurrency(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Checks to see if a {@link Currency} exists with this name.
	 * 
	 * @param name  The name of the {@link Currency} to search for.
	 * @param world The name of the {@link World} to check for this {@link Currency}
	 *              in.
	 * @return True if the currency exists, else false.
	 */
	@Override
	public boolean hasCurrency(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Checks to see if a {@link Currency} exists with this name.
	 * 
	 * @param name The name of the {@link Currency} to search for.
	 * @return True if the currency exists, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasCurrency(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Checks to see if a {@link Currency} exists with this name.
	 * 
	 * @param name  The name of the {@link Currency} to search for.
	 * @param world The name of the {@link World} to check for this {@link Currency}
	 *              in.
	 * @return True if the currency exists, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasCurrency(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Checks to see if an account exists for this identifier. This method should be
	 * used for non-player accounts.
	 * 
	 * @param identifier The identifier of the account.
	 * @return True if an account exists for this player, else false.
	 */
	@Override
	public boolean hasAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Checks to see if an account exists for this identifier. This method should be
	 * used for player accounts.
	 * 
	 * @param identifier The {@link UUID} of the account.
	 * @return True if an account exists for this player, else false.
	 */
	@Override
	public boolean hasAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Checks to see if an account exists for this identifier. This method should be
	 * used for non-player accounts.
	 * 
	 * @param identifier The identifier of the account.
	 * @return True if an account exists for this player, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasAccount(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Checks to see if an account exists for this identifier. This method should be
	 * used for player accounts.
	 * 
	 * @param identifier The {@link UUID} of the account.
	 * @return True if an account exists for this player, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Attempts to create an account for this identifier. This method should be used
	 * for non-player accounts.
	 * 
	 * @param identifier The identifier of the account.
	 * @return True if an account was created, else false.
	 */
	@Override
	public boolean createAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Attempts to create an account for this identifier. This method should be used
	 * for player accounts.
	 * 
	 * @param identifier The {@link UUID} of the account.
	 * @return True if an account was created, else false.
	 */
	@Override
	public boolean createAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Attempts to create an account for this identifier. This method should be used
	 * for non-player accounts.
	 * 
	 * @param identifier The identifier of the account.
	 * @return True if an account was created, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCreateAccount(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Attempts to create an account for this identifier. This method should be used
	 * for player accounts.
	 * 
	 * @param identifier The {@link UUID} of the account.
	 * @return True if an account was created, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCreateAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Attempts to delete an account for this identifier. This method should be used
	 * for non-player accounts.
	 * 
	 * @param identifier The identifier of the account.
	 * @return True if an account was deleted, else false.
	 */
	@Override
	public boolean deleteAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Attempts to delete an account for this identifier. This method should be used
	 * for player accounts.
	 * 
	 * @param identifier The {@link UUID} of the account.
	 * @return True if an account was deleted, else false.
	 */
	@Override
	public boolean deleteAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Attempts to delete an account for this identifier. This method should be used
	 * for non-player accounts.
	 * 
	 * @param identifier The identifier of the account.
	 * @return True if an account was deleted, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncDeleteAccount(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Attempts to delete an account for this identifier. This method should be used
	 * for player accounts.
	 * 
	 * @param identifier The {@link UUID} of the account.
	 * @return True if an account was deleted, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncDeleteAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Determines whether or not a player is able to access this account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to access this account.
	 */
	@Override
	public boolean isAccessor(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to access this account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to access this account.
	 */
	@Override
	public boolean isAccessor(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to access this account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to access this account.
	 */
	@Override
	public boolean isAccessor(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to access this account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to access this account.
	 */
	@Override
	public boolean isAccessor(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to withdraw holdings from this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to withdraw holdings from this
	 *         account.
	 */
	@Override
	public boolean canWithdraw(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to withdraw holdings from this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to withdraw holdings from this
	 *         account.
	 */
	@Override
	public boolean canWithdraw(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to withdraw holdings from this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to withdraw holdings from this
	 *         account.
	 */
	@Override
	public boolean canWithdraw(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to withdraw holdings from this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to withdraw holdings from this
	 *         account.
	 */
	@Override
	public boolean canWithdraw(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to withdraw holdings from this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to withdraw holdings from this
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanWithdraw(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Determines whether or not a player is able to withdraw holdings from this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to withdraw holdings from this
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanWithdraw(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Determines whether or not a player is able to withdraw holdings from this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to withdraw holdings from this
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanWithdraw(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Determines whether or not a player is able to withdraw holdings from this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to withdraw holdings from this
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanWithdraw(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Determines whether or not a player is able to deposit holdings into this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to deposit holdings into this
	 *         account.
	 */
	@Override
	public boolean canDeposit(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to deposit holdings into this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to deposit holdings into this
	 *         account.
	 */
	@Override
	public boolean canDeposit(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to deposit holdings into this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to deposit holdings into this
	 *         account.
	 */
	@Override
	public boolean canDeposit(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to deposit holdings into this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to deposit holdings into this
	 *         account.
	 */
	@Override
	public boolean canDeposit(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Determines whether or not a player is able to deposit holdings into this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to deposit holdings into this
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanDeposit(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Determines whether or not a player is able to deposit holdings into this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to deposit holdings into this
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanDeposit(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Determines whether or not a player is able to deposit holdings into this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to deposit holdings into this
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanDeposit(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Determines whether or not a player is able to deposit holdings into this
	 * account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param accessor   The identifier of the user attempting to access this
	 *                   account.
	 * @return Whether or not the player is able to deposit holdings into this
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanDeposit(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @return The balance of the account.
	 */
	@Override
	public BigDecimal getHoldings(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @return The balance of the account.
	 */
	@Override
	public BigDecimal getHoldings(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param world      The name of the {@link World} associated with the balance.
	 * @return The balance of the account.
	 */
	@Override
	public BigDecimal getHoldings(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param world      The name of the {@link World} associated with the balance.
	 * @return The balance of the account.
	 */
	@Override
	public BigDecimal getHoldings(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param world      The name of the {@link World} associated with the balance.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return The balance of the account.
	 */
	@Override
	public BigDecimal getHoldings(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param world      The name of the {@link World} associated with the balance.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return The balance of the account.
	 */
	@Override
	public BigDecimal getHoldings(UUID arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @return The balance of the account.
	 */
	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @return The balance of the account.
	 */
	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param world      The name of the {@link World} associated with the balance.
	 * @return The balance of the account.
	 */
	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param world      The name of the {@link World} associated with the balance.
	 * @return The balance of the account.
	 */
	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param world      The name of the {@link World} associated with the balance.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return The balance of the account.
	 */
	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param world      The name of the {@link World} associated with the balance.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return The balance of the account.
	 */
	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(UUID arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public boolean hasHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public boolean hasHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public boolean hasHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public boolean hasHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public boolean hasHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public boolean hasHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if an account has at least an amount of funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to use for this check.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the account has at least the specified amount of funds,
	 *         otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public boolean setHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public boolean setHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public boolean setHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public boolean setHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public boolean setHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public boolean setHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to set the funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to set this accounts's funds to.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were set for the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean addHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean addHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean addHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean addHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean addHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean addHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to add funds to an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public boolean canAddHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public boolean canAddHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public boolean canAddHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public boolean canAddHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public boolean canAddHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public boolean canAddHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding addHoldings method would be
	 * successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to add to this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding addHoldings method would return
	 *         true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean removeHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean removeHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean removeHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean removeHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean removeHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean removeHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to remove funds from an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public boolean canRemoveHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public boolean canRemoveHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public boolean canRemoveHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public boolean canRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public boolean canRemoveHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public boolean canRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding removeHoldings method would
	 * be successful. This method does not affect an account's funds.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @param amount     The amount you wish to remove from this account.
	 * @param world      The name of the {@link World} associated with the amount.
	 * @param currency   The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding removeHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to transfer funds from one account to another.
	 *
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 *
	 * @return True if the funds were transferred.
	 */
	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(String arg0, String arg1, BigDecimal arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to transfer funds from one account to another.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @param world          The name of the {@link World} associated with the
	 *                       amount.
	 * @return True if the funds were transferred.
	 */
	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to transfer funds from one account to another.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @param world          The name of the {@link World} associated with the
	 *                       amount.
	 * @param currency       The {@link Currency} associated with the balance.
	 * @return True if the funds were transferred.
	 */
	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(String arg0, String arg1, BigDecimal arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to transfer funds from one account to another.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @return True if the funds were transferred.
	 */
	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to transfer funds from one account to another.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @param world          The name of the {@link World} associated with the
	 *                       amount.
	 * @return True if the funds were transferred.
	 */
	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(String arg0, String arg1, BigDecimal arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to transfer funds from one account to another.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @param world          The name of the {@link World} associated with the
	 *                       amount.
	 * @param currency       The {@link Currency} associated with the balance.
	 * @return True if the funds were transferred.
	 */
	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding transferHoldings method
	 * would be successful. This method does not affect an account's funds.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @return True if a call to the corresponding transferHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String arg0, String arg1, BigDecimal arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding transferHoldings method
	 * would be successful. This method does not affect an account's funds.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @param world          The name of the {@link World} associated with the
	 *                       amount.
	 * @return True if a call to the corresponding transferHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding transferHoldings method
	 * would be successful. This method does not affect an account's funds.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @param world          The name of the {@link World} associated with the
	 *                       amount.
	 * @param currency       The {@link Currency} associated with the balance.
	 * @return True if a call to the corresponding transferHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String arg0, String arg1, BigDecimal arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding transferHoldings method
	 * would be successful. This method does not affect an account's funds.
	 * 
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @return True if a call to the corresponding transferHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding transferHoldings method
	 * would be successful. This method does not affect an account's funds.
	 *
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @param world          The name of the {@link World} associated with the
	 *                       amount.
	 *
	 * @return True if a call to the corresponding transferHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String arg0, String arg1, BigDecimal arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Used to determine if a call to the corresponding transferHoldings method
	 * would be successful. This method does not affect an account's funds.
	 *
	 * @param fromIdentifier The identifier of the account that the holdings will be
	 *                       coming from.
	 * @param toIdentifier   The identifier of the account that the holdings will be
	 *                       going to.
	 * @param amount         The amount you wish to remove from this account.
	 * @param world          The name of the {@link World} associated with the
	 *                       amount.
	 * @param currency       The {@link Currency} associated with the balance.
	 *
	 * @return True if a call to the corresponding transferHoldings method would
	 *         return true, otherwise false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Formats a monetary amount into a more text-friendly version.
	 * 
	 * @param amount The amount of currency to format.
	 * @return The formatted amount.
	 */
	@Override
	public String format(BigDecimal arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Formats a monetary amount into a more text-friendly version.
	 * 
	 * @param amount The amount of currency to format.
	 * @param world  The {@link World} in which this format operation is occurring.
	 * @return The formatted amount.
	 */
	@Override
	public String format(BigDecimal arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Formats a monetary amount into a more text-friendly version.
	 * 
	 * @param amount   The amount of currency to format.
	 * @param world    The {@link World} in which this format operation is
	 *                 occurring.
	 * @param currency The {@link Currency} associated with the balance.
	 * @return The formatted amount.
	 */
	@Override
	public String format(BigDecimal arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Purges the database of accounts with the default balance.
	 * 
	 * @return True if the purge was completed successfully.
	 */
	@Override
	public boolean purgeAccounts() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Purges the database of accounts with a balance under the specified one.
	 * 
	 * @param amount The amount that an account's balance has to be under in order
	 *               to be removed.
	 * @return True if the purge was completed successfully.
	 */
	@Override
	public boolean purgeAccountsUnder(BigDecimal arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Purges the database of accounts with the default balance.
	 * 
	 * @return True if the purge was completed successfully.
	 */
	@Override
	public CompletableFuture<Boolean> asyncPurgeAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Purges the database of accounts with a balance under the specified one.
	 * 
	 * @param amount The amount that an account's balance has to be under in order
	 *               to be removed.
	 * @return True if the purge was completed successfully.
	 */
	@Override
	public CompletableFuture<Boolean> asyncPurgeAccountsUnder(BigDecimal arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Whether or not this API Implementation supports the Transaction System.
	 */
	public boolean supportTransactions() {
		return false;
	}

}
