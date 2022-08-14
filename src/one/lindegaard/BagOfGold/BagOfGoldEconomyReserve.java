package one.lindegaard.BagOfGold;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import net.tnemc.core.economy.response.EconomyResponse;
import one.lindegaard.Core.Core;

public class BagOfGoldEconomyReserve implements EconomyAPI {

	// API:
	// https://github.com/TheNewEconomy/Reserve/blob/master/src/net/tnemc/core/economy/EconomyAPI.java

	private BagOfGold plugin;
	private EconomyAPI mEconomy;

	public BagOfGoldEconomyReserve(BagOfGold plugin) {
		this.plugin = plugin;

		mEconomy = this;
		Reserve.instance().registerProvider(mEconomy);

		if (!enabled()) {
			// BagOfGold is NOT used as an Economy plugin
			RegisteredServiceProvider<EconomyAPI> economyProvider = Bukkit.getServicesManager()
					.getRegistration(EconomyAPI.class);
			if (economyProvider == null) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Reserve]"
						+ plugin.getMessages().getString(plugin.getName().toLowerCase() + ".hook.econ.reserve"));
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
		return BagOfGold.getInstance().getDescription().getVersion();
	}

	/**
	 * @return Whether or not this implementation is enabled.
	 */
	@Override
	public boolean enabled() {
		return plugin.getConfigManager().useBagOfGoldAsAnEconomyPlugin;
	}

	/**
	 * Used to get the plural name of the default currency.
	 * 
	 * @return The plural name of the default currency.
	 */
	@Override
	public String currencyDefaultPlural() {
		return Core.getConfigManager().bagOfGoldName;
	}

	/**
	 * Used to get the singular name of the default currency.
	 * 
	 * @return The plural name of the default currency.
	 */
	@Override
	public String currencyDefaultSingular() {
		return Core.getConfigManager().bagOfGoldName;
	}

	/**
	 * Used to get the plural name of the default currency for a world.
	 * 
	 * @param world The world to be used in this check.
	 * @return The plural name of the default currency.
	 */
	@Override
	public String currencyDefaultPlural(String world) {
		return Core.getConfigManager().bagOfGoldName;
	}

	/**
	 * Used to get the singular name of the default currency for a world.
	 * 
	 * @param world The world to be used in this check.
	 * @return The plural name of the default currency.
	 */
	@Override
	public String currencyDefaultSingular(String world) {
		return Core.getConfigManager().bagOfGoldName;
	}

	/**
	 * Checks to see if a {@link Currency} exists with this name.
	 * 
	 * @param name The name of the {@link Currency} to search for.
	 * @return True if the currency exists, else false.
	 */
	@Override
	public boolean hasCurrency(String name) {
		return Core.getConfigManager().bagOfGoldName.equalsIgnoreCase(name)
				|| currencyDefaultPlural().equalsIgnoreCase(name);
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
	public boolean hasCurrency(String name, String world) {
		return Core.getConfigManager().bagOfGoldName.equalsIgnoreCase(name)
				|| currencyDefaultPlural().equalsIgnoreCase(name);
	}

	/**
	 * Checks to see if a {@link Currency} exists with this name.
	 * 
	 * @param name The name of the {@link Currency} to search for.
	 * @return True if the currency exists, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasCurrency(String name) {
		// TODO Auto-generated method stub
		CompletableFuture<Boolean> completableFuture = new CompletableFuture<Boolean>();
		Boolean b = Core.getConfigManager().bagOfGoldName.equalsIgnoreCase(name)
				|| currencyDefaultPlural().equalsIgnoreCase(name);
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;// completableFuture.complete(b);
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
	public CompletableFuture<Boolean> asyncHasCurrency(String name, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public boolean hasAccount(String name) {
		return enabled();
	}

	/**
	 * Checks to see if an account exists for this identifier. This method should be
	 * used for player accounts.
	 * 
	 * @param identifier The {@link UUID} of the account.
	 * @return True if an account exists for this player, else false.
	 */
	@Override
	public boolean hasAccount(UUID identifier) {
		return enabled();
	}

	/**
	 * Checks to see if an account exists for this identifier. This method should be
	 * used for non-player accounts.
	 * 
	 * @param identifier The identifier of the account.
	 * @return True if an account exists for this player, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasAccount(String identifier) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncHasAccount(UUID identifier) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public boolean createAccount(String name) {
		return true;
	}

	/**
	 * Attempts to create an account for this identifier. This method should be used
	 * for player accounts.
	 * 
	 * @param identifier The {@link UUID} of the account.
	 * @return True if an account was created, else false.
	 */
	@Override
	public boolean createAccount(UUID identifier) {
		return true;
	}

	/**
	 * Attempts to create an account for this identifier. This method should be used
	 * for non-player accounts.
	 * 
	 * @param identifier The identifier of the account.
	 * @return True if an account was created, else false.
	 */
	@Override
	public CompletableFuture<Boolean> asyncCreateAccount(String identifier) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCreateAccount(UUID identifier) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public boolean deleteAccount(String identifier) {
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
	public boolean deleteAccount(UUID identifier) {
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
	public CompletableFuture<Boolean> asyncDeleteAccount(String identifier) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncDeleteAccount(UUID identifier) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public boolean isAccessor(String identifier, String accessor) {
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
	public boolean isAccessor(String identifier, UUID accessor) {
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
	public boolean isAccessor(UUID identifier, String accessor) {
		return identifier.equals(UUID.fromString(accessor));
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
	public boolean isAccessor(UUID identifier, UUID accessor) {
		return identifier.equals(accessor);
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
	public boolean canWithdraw(String identifier, String accessor) {
		return identifier.equalsIgnoreCase(accessor);
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
	public boolean canWithdraw(String identifier, UUID accessor) {
		return identifier.equalsIgnoreCase(accessor.toString());
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
	public boolean canWithdraw(UUID identifier, String accessor) {
		return identifier.equals(UUID.fromString(accessor));
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
	public boolean canWithdraw(UUID identifier, UUID accessor) {
		return identifier.equals(accessor);
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
	public boolean canDeposit(String identifier, String accessor) {
		return identifier.equalsIgnoreCase(accessor);
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
	public boolean canDeposit(String identifier, UUID accessor) {
		return identifier.equalsIgnoreCase(accessor.toString());
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
	public boolean canDeposit(UUID identifier, String accessor) {
		return identifier.equals(UUID.fromString(accessor));
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
	public boolean canDeposit(UUID identifier, UUID accessor) {
		return identifier.equals(accessor);
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @return The balance of the account.
	 */
	@Override
	public BigDecimal getHoldings(String identifier) {
		return getHoldings(Bukkit.getOfflinePlayer(identifier).getUniqueId());
	}

	/**
	 * Used to get the balance of an account.
	 * 
	 * @param identifier The identifier of the account that is associated with this
	 *                   call.
	 * @return The balance of the account.
	 */
	@Override
	public BigDecimal getHoldings(UUID identifier) {
		return new BigDecimal(plugin.getRewardManager().getBalance(Bukkit.getOfflinePlayer(identifier)));
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
	public BigDecimal getHoldings(String identifier, String world) {
		return getHoldings(Bukkit.getOfflinePlayer(identifier).getUniqueId());
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
	public BigDecimal getHoldings(UUID identifier, String world) {
		return getHoldings(Bukkit.getOfflinePlayer(identifier).getUniqueId());
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
	public BigDecimal getHoldings(String identifier, String world, String currency) {
		return getHoldings(Bukkit.getOfflinePlayer(identifier).getUniqueId());
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
	public BigDecimal getHoldings(UUID identifier, String world, String currency) {
		return getHoldings(Bukkit.getOfflinePlayer(identifier).getUniqueId());
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
	public boolean hasHoldings(String identifier, BigDecimal amount) {
		return getHoldings(identifier).compareTo(amount) >= 1;
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
	public boolean hasHoldings(UUID identifier, BigDecimal amount) {
		return getHoldings(identifier).compareTo(amount) >= 1;
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
	public boolean hasHoldings(String identifier, BigDecimal amount, String world) {
		return getHoldings(identifier).compareTo(amount) >= 1;
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
	public boolean hasHoldings(UUID identifier, BigDecimal amount, String world) {
		return getHoldings(identifier).compareTo(amount) >= 1;
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
	public boolean hasHoldings(String identifier, BigDecimal amount, String world, String currency) {
		return getHoldings(identifier).compareTo(amount) >= 1;
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
	public boolean hasHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
		return getHoldings(identifier).compareTo(amount) >= 1;
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
	public boolean setHoldings(String identifier, BigDecimal amount) {
		BigDecimal diff = amount.subtract(getHoldings(identifier));
		if (diff.compareTo(BigDecimal.ZERO) > 0)
			plugin.getRewardManager().depositPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		else
			plugin.getRewardManager().withdrawPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		return true;
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
	public boolean setHoldings(UUID identifier, BigDecimal amount) {
		BigDecimal diff = amount.subtract(getHoldings(identifier));
		if (diff.compareTo(BigDecimal.ZERO) > 0)
			plugin.getRewardManager().depositPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		else
			plugin.getRewardManager().withdrawPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		return true;
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
	public boolean setHoldings(String identifier, BigDecimal amount, String world) {
		BigDecimal diff = amount.subtract(getHoldings(identifier));
		if (diff.compareTo(BigDecimal.ZERO) > 0)
			plugin.getRewardManager().depositPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		else
			plugin.getRewardManager().withdrawPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		return true;
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
	public boolean setHoldings(UUID identifier, BigDecimal amount, String world) {
		BigDecimal diff = amount.subtract(getHoldings(identifier));
		if (diff.compareTo(BigDecimal.ZERO) > 0)
			plugin.getRewardManager().depositPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		else
			plugin.getRewardManager().withdrawPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		return true;
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
	public boolean setHoldings(String identifier, BigDecimal amount, String world, String currency) {
		BigDecimal diff = amount.subtract(getHoldings(identifier));
		if (diff.compareTo(BigDecimal.ZERO) > 0)
			plugin.getRewardManager().depositPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		else
			plugin.getRewardManager().withdrawPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		return true;
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
	public boolean setHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
		BigDecimal diff = amount.subtract(getHoldings(identifier));
		if (diff.compareTo(BigDecimal.ZERO) > 0)
			plugin.getRewardManager().depositPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		else
			plugin.getRewardManager().withdrawPlayer(Bukkit.getOfflinePlayer(identifier), diff.doubleValue());
		return true;
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
	public boolean addHoldings(String identifier, BigDecimal amount) {
		return addHoldings(Bukkit.getOfflinePlayer(identifier).getUniqueId(), amount);
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
	public boolean addHoldings(UUID identifier, BigDecimal amount) {
		return plugin.getRewardManager().depositPlayer(Bukkit.getOfflinePlayer(identifier), amount.doubleValue());
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
	public boolean addHoldings(String identifier, BigDecimal amount, String world) {
		return addHoldings(identifier, amount);
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
	public boolean addHoldings(UUID identifier, BigDecimal amount, String world) {
		return addHoldings(identifier, amount);
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
	public boolean addHoldings(String identifier, BigDecimal amount, String world, String currency) {
		return addHoldings(identifier, amount);
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
	public boolean addHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
		return addHoldings(identifier, amount);
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
	public boolean canAddHoldings(String identifier, BigDecimal amount) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(identifier);
		if (offlinePlayer.isOnline())
			return plugin.getRewardManager().getSpaceForMoney((Player) offlinePlayer) <= amount.doubleValue();
		else
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
	public boolean canAddHoldings(UUID identifier, BigDecimal amount) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(identifier);
		if (offlinePlayer.isOnline())
			return plugin.getRewardManager().getSpaceForMoney((Player) offlinePlayer) <= amount.doubleValue();
		else
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
	public boolean canAddHoldings(String identifier, BigDecimal amount, String world) {
		return canAddHoldings(identifier, amount);
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
	public boolean canAddHoldings(UUID identifier, BigDecimal amount, String world) {
		return canAddHoldings(identifier, amount);
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
	public boolean canAddHoldings(String identifier, BigDecimal amount, String world, String currency) {
		return canAddHoldings(identifier, amount);
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
	public boolean canAddHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
		return canAddHoldings(identifier, amount);
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
	public CompletableFuture<Boolean> asyncCanAddHoldings(String identifier, BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID identifier, BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanAddHoldings(String identifier, BigDecimal amount, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID identifier, BigDecimal amount, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanAddHoldings(String identifier, BigDecimal amount, String world,
			String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID identifier, BigDecimal amount, String world,
			String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public boolean removeHoldings(String identifier, BigDecimal amount) {
		return plugin.getRewardManager().withdrawPlayer(Bukkit.getOfflinePlayer(identifier), amount.doubleValue());
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
	public boolean removeHoldings(UUID identifier, BigDecimal amount) {
		return plugin.getRewardManager().withdrawPlayer(Bukkit.getOfflinePlayer(identifier), amount.doubleValue());
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
	public boolean removeHoldings(String identifier, BigDecimal amount, String world) {
		return removeHoldings(identifier, amount);
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
	public boolean removeHoldings(UUID identifier, BigDecimal amount, String world) {
		return removeHoldings(identifier, amount);
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
	public boolean removeHoldings(String identifier, BigDecimal amount, String world, String currency) {
		return removeHoldings(identifier, amount);
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
	public boolean removeHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
		return removeHoldings(identifier, amount);
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
	public CompletableFuture<Boolean> asyncRemoveHoldings(String identifier, BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID identifier, BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncRemoveHoldings(String identifier, BigDecimal amount, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID identifier, BigDecimal amount, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncRemoveHoldings(String identifier, BigDecimal amount, String world,
			String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID identifier, BigDecimal amount, String world,
			String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public boolean canRemoveHoldings(String identifier, BigDecimal amount) {
		return getHoldings(identifier).compareTo(amount) >= 0;
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
	public boolean canRemoveHoldings(UUID identifier, BigDecimal amount) {
		return getHoldings(identifier).compareTo(amount) >= 0;
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
	public boolean canRemoveHoldings(String identifier, BigDecimal amount, String world) {
		return getHoldings(identifier).compareTo(amount) >= 0;
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
	public boolean canRemoveHoldings(UUID identifier, BigDecimal amount, String world) {
		return getHoldings(identifier).compareTo(amount) >= 0;
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
	public boolean canRemoveHoldings(String identifier, BigDecimal amount, String world, String currency) {
		return getHoldings(identifier).compareTo(amount) >= 0;
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
	public boolean canRemoveHoldings(UUID identifier, BigDecimal amount, String world, String currency) {
		return getHoldings(identifier).compareTo(amount) >= 0;
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
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String identifier, BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID identifier, BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String identifier, BigDecimal amount, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID identifier, BigDecimal amount, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String identifier, BigDecimal amount, String world,
			String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID identifier, BigDecimal amount, String world,
			String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncTransferHoldings(String fromIdentifier, String toIdentifier,
			BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	 *                       amount.
	 * @return True if the funds were transferred.
	 */
	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncTransferHoldings(String identifier, String toIdentifier, BigDecimal amount,
			String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	 * @param currency       The {@link Currency} associated with the balance.
	 * @return True if the funds were transferred.
	 */
	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount,
			String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncTransferHoldings(String fromIdentifier, String toIdentifier,
			BigDecimal amount, String world, String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID fromIdentifier, UUID toIdentifier, BigDecimal amount,
			String world, String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String fromIdentifier, String toIdentifier,
			BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID fromIdentifier, UUID toIdentifier,
			BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String fromIdentifier, String toIdentifier,
			BigDecimal amount, String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID fromIdentifier, UUID toIdentifier,
			BigDecimal amount, String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String fromIdentifier, String toIdentifier,
			BigDecimal amount, String world, String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID fromIdentifier, UUID toIdentifier,
			BigDecimal amount, String world, String currency) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	// ****************************************************************************
	// BANKS
	// ****************************************************************************

	/**
	 * @return A list containing the names of the banks currently in the server.
	 */
	@Override
	public List<String> getBanks() {
		ArrayList<String> list = new ArrayList<>();
		list.add(plugin.getConfigManager().bankname);
		return list;
	}

	/**
	 * @param world The name of the {@link World} to use for this call.
	 * @return A list containing the names of the banks currently in the specified
	 *         world
	 */
	@Override
	public List<String> getBanks(String world) {
		ArrayList<String> list = new ArrayList<>();
		list.add(plugin.getConfigManager().bankname);
		return list;
	}

	/**
	 * Asynchronous version of getBanks()
	 * 
	 * @return A list containing the names of the banks currently in the server.
	 */
	@Override
	public CompletableFuture<List<String>> asyncGetBanks() {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * Asynchronous version of getBanks(String world)
	 * 
	 * @param world The name of the {@link World} to use for this call.
	 * @return A list containing the names of the banks currently in the specified
	 *         world
	 */
	@Override
	public CompletableFuture<List<String>> asyncGetBanks(String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @return A list of currencies that are able to be used with banks.
	 */
	@Override
	public List<String> acceptedBankCurrencies() {
		ArrayList<String> list = new ArrayList<>();
		list.add(Core.getConfigManager().bagOfGoldName);
		return list;
	}

	/**
	 * @param world The name of the {@link World} to use for this call.
	 * @return A list of currencies that are able to be used with banks in the
	 *         specified world.
	 */
	@Override
	public List<String> acceptedBankCurrencies(String world) {
		return acceptedBankCurrencies();
	}

	/**
	 * @param world The name of the {@link World} to use for this call.
	 * @param bank  The name of the bank to use for this call.
	 * @return A list of currencies that are able to be used with the specified bank
	 *         in the specified world.
	 */
	@Override
	public List<String> acceptedBankCurrencies(String world, String bank) {
		return acceptedBankCurrencies();
	}

	/**
	 * @return A list of currencies that are able to be used with banks.
	 */
	@Override
	public CompletableFuture<List<String>> asyncAcceptedBankCurrencies() {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param world The name of the {@link World} to use for this call.
	 * @return A list of currencies that are able to be used with banks in the
	 *         specified world.
	 */
	@Override
	public CompletableFuture<List<String>> asyncAcceptedBankCurrencies(String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param world The name of the {@link World} to use for this call.
	 * @param bank  The name of the bank to use for this call.
	 * @return A list of currencies that are able to be used with the specified bank
	 *         in the specified world.
	 */
	@Override
	public CompletableFuture<List<String>> asyncAcceptedBankCurrencies(String world, String bank) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @return A List of UUIDs of bank accounts that the specified player has access
	 *         to.
	 */
	@Override
	public List<UUID> availableBankAccounts(UUID player) {
		ArrayList<UUID> list = new ArrayList<>();
		list.add(player);
		return list;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @return A List of UUIDs of bank accounts that the specified player has access
	 *         to in a specific world.
	 */
	@Override
	public List<UUID> availableBankAccounts(UUID player, String world) {
		return availableBankAccounts(player);
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @param bank   The name of the bank to use for this call.
	 * @return A List of UUIDs of bank accounts that the specified player has access
	 *         to in a specific bank in a specific world.
	 */
	@Override
	public List<UUID> availableBankAccounts(UUID player, String world, String bank) {
		return availableBankAccounts(player);
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @return A List of UUIDs of bank accounts that the specified player has access
	 *         to.
	 */
	@Override
	public CompletableFuture<List<UUID>> asyncAvailableBankAccounts(UUID player) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @return A List of UUIDs of bank accounts that the specified player has access
	 *         to in a specific world.
	 */
	@Override
	public CompletableFuture<List<UUID>> asyncAvailableBankAccounts(UUID player, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @param bank   The name of the bank to use for this call.
	 * @return A List of UUIDs of bank accounts that the specified player has access
	 *         to in a specific bank in a specific world.
	 */
	@Override
	public CompletableFuture<List<UUID>> asyncAvailableBankAccounts(UUID player, String world, String bank) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @return True if the specified player is the owner of a bank in the specified
	 *         world.
	 */
	@Override
	public boolean isBankOwner(UUID player, String world) {
		return true;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @param bank   The name of the bank to use for this call.
	 * @return True if the specified player is the owner of the specified bank in
	 *         the specified world.
	 */
	@Override
	public boolean isBankOwner(UUID player, String world, String bank) {
		return true;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @return True if the specified player is the owner of a bank in the specified
	 *         world.
	 */
	@Override
	public CompletableFuture<Boolean> asyncIsBankOwner(UUID player, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @param bank   The name of the bank to use for this call.
	 * @return True if the specified player is the owner of the specified bank in
	 *         the specified world.
	 */
	@Override
	public CompletableFuture<Boolean> asyncIsBankOwner(UUID owner, String world, String bank) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param owner The UUID of the owner of this bank account.
	 * @return An optional with a UUID of the created bank account if it was
	 *         created, otherwise an empty Optional.
	 */
	@Override
	public Optional<UUID> createBankAccount(UUID owner) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param owner The UUID of the owner of this bank account.
	 * @param world The name of the {@link World} to create this bank account in.
	 * @return An optional with a UUID of the created bank account if it was
	 *         created, otherwise an empty Optional.
	 */
	@Override
	public Optional<UUID> createBankAccount(UUID owner, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param owner The UUID of the owner of this bank account.
	 * @param world The name of the {@link World} to create this bank account in.
	 * @param bank  The name of the bank to create this bank account in.
	 * @return An optional with a UUID of the created bank account if it was
	 *         created, otherwise an empty Optional.
	 */
	@Override
	public Optional<UUID> createBankAccount(UUID owner, String world, String bank) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param owner The UUID of the owner of this bank account.
	 * @return An optional with a UUID of the created bank account if it was
	 *         created, otherwise an empty Optional.
	 */
	@Override
	public CompletableFuture<Optional<UUID>> asyncCreateBankAccount(UUID owner) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param owner The UUID of the owner of this bank account.
	 * @param world The name of the {@link World} to create this bank account in.
	 * @return An optional with a UUID of the created bank account if it was
	 *         created, otherwise an empty Optional.
	 */
	@Override
	public CompletableFuture<Optional<UUID>> asyncCreateBankAccount(UUID owner, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param owner The UUID of the owner of this bank account.
	 * @param world The name of the {@link World} to create this bank account in.
	 * @param bank  The name of the bank to create this bank account in.
	 * @return An optional with a UUID of the created bank account if it was
	 *         created, otherwise an empty Optional.
	 */
	@Override
	public CompletableFuture<Optional<UUID>> asyncCreateBankAccount(UUID owner, String world, String bank) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @return True if the specified player has a bank account.
	 */
	@Override
	public boolean hasBankAccount(UUID player) {
		return true;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @return True if the specified player has a bank account in the specified
	 *         world.
	 */
	@Override
	public boolean hasBankAccount(UUID player, String world) {
		return true;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @param bank   The name of the bank to use for this call.
	 * @return True if the specified player has a bank account in the specified bank
	 *         in the specified world.
	 */
	@Override
	public boolean hasBankAccount(UUID player, String world, String bank) {
		return true;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @return True if the specified player has a bank account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasBankAccount(UUID player) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @return True if the specified player has a bank account in the specified
	 *         world.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasBankAccount(UUID player, String world) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param player The UUID of the player to use for this call.
	 * @param world  The name of the {@link World} to use for this call.
	 * @param bank   The name of the bank to use for this call.
	 * @return True if the specified player has a bank account in the specified bank
	 *         in the specified world.
	 */
	@Override
	public CompletableFuture<Boolean> asyncHasBankAccount(UUID player, String world, String bank) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param player  The UUID of the player to use for this call.
	 * @return True if the specified player is the owner of the specified bank
	 *         account.
	 */
	@Override
	public boolean isBankAccountOwner(UUID account, UUID player) {
		return account.equals(player);
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param player  The UUID of the player to use for this call.
	 * @return True if the specified player is the owner of the specified bank
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncIsBankAccountOwner(UUID account, UUID player) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param player  The UUID of the player to use for this call.
	 * @return True if the specified player is a member of the specified bank
	 *         account.
	 */
	@Override
	public boolean isBankAccountMember(UUID account, UUID player) {
		return account.equals(player);
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param player  The UUID of the player to use for this call.
	 * @return True if the specified player is a member of the specified bank
	 *         account.
	 */
	@Override
	public CompletableFuture<Boolean> asyncIsBankAccountMember(UUID account, UUID player) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @return The balance of the bank account.
	 */
	@Override
	public BigDecimal getBankHoldings(UUID account) {
		return new BigDecimal(
				plugin.getRewardManager().bankBalance(Bukkit.getOfflinePlayer(account).getUniqueId().toString()));
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param world   The name of the {@link World} to use for this call.
	 * @return The balance of the bank account.
	 */
	@Override
	public BigDecimal getBankHoldings(UUID account, String world) {
		return getBankHoldings(account);
	}

	/**
	 * @param account  The UUID of the bank account to use for this call.
	 * @param world    The name of the {@link World} to use for this call.
	 * @param currency The name of the currency to use for this call.
	 * @return The balance of the bank account.
	 */
	@Override
	public BigDecimal getBankHoldings(UUID account, String world, String currency) {
		return getBankHoldings(account);
	}

	/**
	 * @param account  The UUID of the bank account to use for this call.
	 * @param world    The name of the {@link World} to use for this call.
	 * @param currency The name of the currency to use for this call.
	 * @param bank     The name of the bank to use for this call.
	 * @return The balance of the bank account.
	 */
	@Override
	public BigDecimal getBankHoldings(UUID account, String world, String currency, String bank) {
		return getBankHoldings(account);
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param player  The UUID of the player adding the funds to the account, null
	 *                if console.
	 * @param amount  The amount of funds to add to the account.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean bankAddHoldings(UUID account, UUID player, BigDecimal amount) {
		return plugin.getRewardManager().bankDeposit(account.toString(), amount.doubleValue());
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param player  The UUID of the player adding the funds to the account, null
	 *                if not associated with a player action.
	 * @param amount  The amount of funds to add to the account.
	 * @param world   The name of the {@link World} to use for this call.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean bankAddHoldings(UUID account, UUID player, BigDecimal amount, String world) {
		return bankAddHoldings(account, player, amount);
	}

	/**
	 * @param account  The UUID of the bank account to use for this call.
	 * @param player   The UUID of the player adding the funds to the account, null
	 *                 if not associated with a player action.
	 * @param amount   The amount of funds to add to the account.
	 * @param world    The name of the {@link World} to use for this call.
	 * @param currency The name of the currency to use for this call.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean bankAddHoldings(UUID account, UUID player, BigDecimal amount, String world, String currency) {
		return bankAddHoldings(account, player, amount);
	}

	/**
	 * @param account  The UUID of the bank account to use for this call.
	 * @param player   The UUID of the player adding the funds to the account, null
	 *                 if not associated with a player action.
	 * @param amount   The amount of funds to add to the account.
	 * @param world    The name of the {@link World} to use for this call.
	 * @param currency The name of the currency to use for this call.
	 * @param bank     The name of the bank to use for this call.
	 * @return True if the funds were added to the account, otherwise false.
	 */
	@Override
	public boolean bankAddHoldings(UUID account, UUID player, BigDecimal amount, String world, String currency,
			String bank) {
		return bankAddHoldings(account, player, amount);
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param player  The UUID of the player adding the funds to the account, null
	 *                if console.
	 * @param amount  The amount of funds to remove from the account.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean bankRemoveHoldings(UUID account, UUID player, BigDecimal amount) {
		return plugin.getRewardManager().bankWithdraw(account.toString(), amount.doubleValue());
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param player  The UUID of the player adding the funds to the account, null
	 *                if console.
	 * @param amount  The amount of funds to remove from the account.
	 * @param world   The name of the {@link World} to use for this call.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean bankRemoveHoldings(UUID account, UUID player, BigDecimal amount, String world) {
		return bankRemoveHoldings(account, player, amount);
	}

	/**
	 * @param account  The UUID of the bank account to use for this call.
	 * @param player   The UUID of the player adding the funds to the account, null
	 *                 if console.
	 * @param amount   The amount of funds to remove from the account.
	 * @param world    The name of the {@link World} to use for this call.
	 * @param currency The name of the currency to use for this call.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean bankRemoveHoldings(UUID account, UUID player, BigDecimal amount, String world, String currency) {
		return bankRemoveHoldings(account, player, amount);
	}

	/**
	 * @param account  The UUID of the bank account to use for this call.
	 * @param player   The UUID of the player adding the funds to the account, null
	 *                 if console.
	 * @param amount   The amount of funds to remove from the account.
	 * @param world    The name of the {@link World} to use for this call.
	 * @param currency The name of the currency to use for this call.
	 * @param bank     The name of the bank to use for this call.
	 * @return True if the funds were removed from the account, otherwise false.
	 */
	@Override
	public boolean bankRemoveHoldings(UUID account, UUID player, BigDecimal amount, String world, String currency,
			String bank) {
		return bankRemoveHoldings(account, player, amount);
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param amount  The amount to set the account's funds to
	 * @return True if the account's funds were set to the specified amount.
	 */
	@Override
	public boolean bankSetHoldings(UUID account, BigDecimal amount) {
		BigDecimal bal = getBankHoldings(account);
		if (bal.compareTo(amount) >= 0)
			return bankRemoveHoldings(account, account, bal.subtract(amount));
		else
			return bankAddHoldings(account, account, amount.subtract(bal));
	}

	/**
	 * @param account The UUID of the bank account to use for this call.
	 * @param amount  The amount to set the account's funds to
	 * @param world   The name of the {@link World} to use for this call.
	 * @return True if the account's funds were set to the specified amount.
	 */
	@Override
	public boolean bankSetHoldings(UUID account, BigDecimal amount, String world) {
		return bankSetHoldings(account, amount);
	}

	/**
	 * @param account  The UUID of the bank account to use for this call.
	 * @param amount   The amount to set the account's funds to
	 * @param world    The name of the {@link World} to use for this call.
	 * @param currency The name of the currency to use for this call.
	 * @return True if the account's funds were set to the specified amount.
	 */
	@Override
	public boolean bankSetHoldings(UUID account, BigDecimal amount, String world, String currency) {
		return bankSetHoldings(account, amount);
	}

	/**
	 * @param account  The UUID of the bank account to use for this call.
	 * @param amount   The amount to set the account's funds to
	 * @param world    The name of the {@link World} to use for this call.
	 * @param currency The name of the currency to use for this call.
	 * @param bank     The name of the bank to use for this call.
	 * @return True if the account's funds were set to the specified amount.
	 */
	@Override
	public boolean bankSetHoldings(UUID account, BigDecimal amount, String world, String currency, String bank) {
		return bankSetHoldings(account, amount);
	}

	/**
	 * Formats a monetary amount into a more text-friendly version.
	 * 
	 * @param amount The amount of currency to format.
	 * @return The formatted amount.
	 */
	@Override
	public String format(BigDecimal amount) {
		return plugin.getRewardManager().format(amount.doubleValue());
	}

	/**
	 * Formats a monetary amount into a more text-friendly version.
	 * 
	 * @param amount The amount of currency to format.
	 * @param world  The {@link World} in which this format operation is occurring.
	 * @return The formatted amount.
	 */
	@Override
	public String format(BigDecimal amount, String world) {
		return format(amount);
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
	public String format(BigDecimal amount, String world, String currency) {
		return format(amount);
	}

	/**
	 * Purges the database of accounts with the default balance.
	 * 
	 * @return True if the purge was completed successfully.
	 */
	@Override
	public boolean purgeAccounts() {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public boolean purgeAccountsUnder(BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
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
	public CompletableFuture<Boolean> asyncPurgeAccountsUnder(BigDecimal amount) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	/**
	 * Whether or not this API Implementation supports the Transaction System.
	 */
	public boolean supportTransactions() {
		return false;
	}

	@Override
	public EconomyResponse addHoldingsDetail(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse addHoldingsDetail(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse addHoldingsDetail(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse addHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse addHoldingsDetail(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse addHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canAddHoldingsDetail(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canAddHoldingsDetail(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canAddHoldingsDetail(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canAddHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canAddHoldingsDetail(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canAddHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canDepositDetail(String arg0, String arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canDepositDetail(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canDepositDetail(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canDepositDetail(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canRemoveHoldingsDetail(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canRemoveHoldingsDetail(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canRemoveHoldingsDetail(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canRemoveHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canRemoveHoldingsDetail(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canRemoveHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canWithdrawDetail(String arg0, String arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canWithdrawDetail(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canWithdrawDetail(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse canWithdrawDetail(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse createAccountDetail(String arg0) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse createAccountDetail(UUID arg0) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse deleteAccountDetail(String arg0) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse deleteAccountDetail(UUID arg0) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse hasAccountDetail(String arg0) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse hasAccountDetail(UUID arg0) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse removeHoldingsDetail(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse removeHoldingsDetail(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse removeHoldingsDetail(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse removeHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse removeHoldingsDetail(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse removeHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse setHoldingsDetail(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse setHoldingsDetail(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse setHoldingsDetail(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse setHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse setHoldingsDetail(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

	@Override
	public EconomyResponse setHoldingsDetail(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		plugin.getMessages().debug("This method is not implemented in BagOfGold yet");
		return null;
	}

}
