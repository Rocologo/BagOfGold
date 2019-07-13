package one.lindegaard.BagOfGold;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;
import net.tnemc.core.economy.ExtendedEconomyAPI;

public class BagOfGoldEconomyManager {

	private BagOfGold plugin;
	private Economy vaultEconomy = null;
	private EconomyAPI reserveEconomy = null;
	private EcoType Type = EcoType.NONE;
	private String version = "";

	public enum EcoType {
		NONE, VAULT, RESERVE
	}

	public BagOfGoldEconomyManager(BagOfGold plugin) {
		this.plugin = plugin;
		
		setupEconomyManager();
		
		if (plugin.getConfigManager().useBagOfGoldAsAnEconomyPlugin) {
			// Try to load BagOfGold
			Plugin vaultPlugin = Bukkit.getPluginManager().getPlugin("Vault");
			if (vaultPlugin != null)
				BagOfGoldEconomyVault.hookVaultEconomy(Economy_BagOfGold.class, ServicePriority.Normal, "net.milkbowl.vault.economy.Economy");

			Plugin reservePlugin = Bukkit.getPluginManager().getPlugin("Reserve");
			if (reservePlugin != null)
				new BagOfGoldEconomyReserve(plugin);
		}
		
	}

	public Boolean setupEconomyManager() {

		Plugin economyProvider = null;

		/*
		 * Attempt to find Vault for Economy handling
		 */
		try {
			RegisteredServiceProvider<Economy> vaultEcoProvider = plugin.getServer().getServicesManager()
					.getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (vaultEcoProvider != null) {
				/*
				 * Flag as using Vault hooks
				 */
				vaultEconomy = vaultEcoProvider.getProvider();
				setVersion(String.format("%s %s", vaultEcoProvider.getProvider().getName(), "via Vault"));
				Type = EcoType.VAULT;
				return true;
			}
		} catch (NoClassDefFoundError ex) {
		}

		/*
		 * Attempt to find Reserve for Economy handling
		 */
		economyProvider = plugin.getServer().getPluginManager().getPlugin("Reserve");
		if (economyProvider != null && ((Reserve) economyProvider).economyProvided()) {
			/*
			 * Flat as using Reserve Hooks.
			 */
			reserveEconomy = ((Reserve) economyProvider).economy();
			setVersion(String.format("%s %s", reserveEconomy.name(), "via Reserve"));
			Type = EcoType.RESERVE;
			return true;
		}

		/*
		 * No compatible Economy system found.
		 */
		return false;
	}

	private void setVersion(String version) {

		this.version = version;
	}

	public boolean isActive() {

		return (Type != EcoType.NONE);
	}

	public String getVersion() {

		return version;
	}

	/**
	 * Returns the relevant player's economy account
	 * 
	 * @param accountName - Name of the player's account (usually playername)
	 * @return - The relevant player's economy account
	 */
	private Object getEconomyAccount(String accountName) {

		switch (Type) {

		case RESERVE:
			if (reserveEconomy instanceof ExtendedEconomyAPI)
				return ((ExtendedEconomyAPI) reserveEconomy).getAccount(accountName);
			break;

		default:
			break;

		}

		return null;
	}

	/**
	 * Check if account exists
	 * 
	 * @param accountName
	 * @return
	 */
	public boolean hasEconomyAccount(String accountName) {

		switch (Type) {

		case RESERVE:
			return reserveEconomy.hasAccount(accountName);

		case VAULT:
			return vaultEconomy.hasAccount(accountName);

		default:
			break;

		}

		return false;
	}

	/**
	 * Attempt to delete the economy account.
	 */
	public void removeAccount(String accountName) {

		try {
			switch (Type) {

			case RESERVE:
				reserveEconomy.deleteAccount(accountName);
				break;

			case VAULT: // Attempt to zero the account as Vault provides no delete method.
				if (!vaultEconomy.hasAccount(accountName))
					vaultEconomy.createPlayerAccount(accountName);

				vaultEconomy.withdrawPlayer(accountName, (vaultEconomy.getBalance(accountName)));

				return;

			default:
				break;

			}

		} catch (NoClassDefFoundError e) {
		}

		return;
	}

	/**
	 * Returns the accounts current balance
	 * 
	 * @param accountName
	 * @return double containing the total in the account
	 */
	public double getBalance(String accountName, World world) {

		switch (Type) {

		case RESERVE:
			if (!reserveEconomy.hasAccount(accountName))
				reserveEconomy.createAccount(accountName);

			return reserveEconomy.getHoldings(accountName, world.getName()).doubleValue();

		case VAULT:
			if (!vaultEconomy.hasAccount(accountName))
				vaultEconomy.createPlayerAccount(accountName);

			return vaultEconomy.getBalance(accountName);

		default:
			break;

		}

		return 0.0;
	}

	/**
	 * Returns true if the account has enough money
	 * 
	 * @param accountName
	 * @param amount
	 * @return true if there is enough in the account
	 */
	public boolean hasEnough(String accountName, Double amount, World world) {

		if (getBalance(accountName, world) >= amount)
			return true;

		return false;
	}

	/**
	 * Attempts to remove an amount from an account
	 * 
	 * @param accountName
	 * @param amount
	 * @return true if successful
	 */
	public boolean subtract(String accountName, Double amount, World world) {

		switch (Type) {

		case RESERVE:
			if (!reserveEconomy.hasAccount(accountName))
				reserveEconomy.createAccount(accountName);
			return reserveEconomy.removeHoldings(accountName, new BigDecimal(amount), world.getName());

		case VAULT:
			if (!vaultEconomy.hasAccount(accountName))
				vaultEconomy.createPlayerAccount(accountName);

			return vaultEconomy.withdrawPlayer(accountName, amount).type == EconomyResponse.ResponseType.SUCCESS;

		default:
			break;

		}

		return false;
	}

	/**
	 * Add funds to an account.
	 * 
	 * @param accountName
	 * @param amount
	 * @param world
	 * @return true if successful
	 */
	public boolean add(String accountName, Double amount, World world) {

		switch (Type) {

		case RESERVE:
			if (!reserveEconomy.hasAccount(accountName))
				reserveEconomy.createAccount(accountName);

			return reserveEconomy.addHoldings(accountName, new BigDecimal(amount), world.getName());

		case VAULT:
			if (!vaultEconomy.hasAccount(accountName))
				vaultEconomy.createPlayerAccount(accountName);

			return vaultEconomy.depositPlayer(accountName, amount).type == EconomyResponse.ResponseType.SUCCESS;

		default:
			break;

		}

		return false;
	}

	public boolean setBalance(String accountName, Double amount, World world) {

		switch (Type) {

		case RESERVE:
			if (!reserveEconomy.hasAccount(accountName))
				reserveEconomy.createAccount(accountName);
			return reserveEconomy.setHoldings(accountName, new BigDecimal(amount), world.getName());

		case VAULT:
			if (!vaultEconomy.hasAccount(accountName))
				vaultEconomy.createPlayerAccount(accountName);

			return vaultEconomy.depositPlayer(accountName,
					(amount - vaultEconomy.getBalance(accountName))).type == EconomyResponse.ResponseType.SUCCESS;

		default:
			break;

		}

		return false;
	}

	/**
	 * Format this balance according to the current economy systems settings.
	 * 
	 * @param balance
	 * @return string containing the formatted balance
	 */
	public String getFormattedBalance(double balance) {

		try {
			switch (Type) {

			case RESERVE:
				return reserveEconomy.format(new BigDecimal(balance));

			case VAULT:
				return vaultEconomy.format(balance);

			default:
				break;

			}

		} catch (Exception InvalidAPIFunction) {
		}

		return String.format("%.2f", balance);

	}

}
