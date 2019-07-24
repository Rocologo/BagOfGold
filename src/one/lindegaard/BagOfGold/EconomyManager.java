package one.lindegaard.BagOfGold;

import java.math.BigDecimal;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import net.milkbowl.vault.economy.Economy;
import net.tnemc.core.Reserve;
import net.tnemc.core.economy.EconomyAPI;

public class EconomyManager {

	private BagOfGold plugin;
	private Economy vaultEconomy = null;
	private EconomyAPI reserveEconomy = null;
	private EcoType Type = EcoType.NONE;
	private String version = "";

	private enum EcoType {
		NONE, VAULT, RESERVE
	}

	public EconomyManager(BagOfGold plugin) {
		this.plugin = plugin;

		if (plugin.getConfigManager().useBagOfGoldAsAnEconomyPlugin) {

			Plugin vaultPlugin = Bukkit.getPluginManager().getPlugin("Vault");
			if (vaultPlugin != null)
				BagOfGoldEconomyVault.hookVaultEconomy(Economy_BagOfGold.class, ServicePriority.Normal,
						"net.milkbowl.vault.economy.Economy");

			Plugin reservePlugin = Bukkit.getPluginManager().getPlugin("Reserve");
			if (reservePlugin != null)
				new BagOfGoldEconomyReserve(plugin);
			
			setupEconomyManager();

		}

	}
	
	public String getEconomyAPI() {
		switch (Type) {
		case RESERVE:
			return "Reserve";
		case VAULT:
			return "Vault";
		default:
			return "None";
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

	public boolean depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.addHoldings(offlinePlayer.getUniqueId(), new BigDecimal(amount));
		case VAULT:
			return vaultEconomy.depositPlayer(offlinePlayer, amount).transactionSuccess();
		default:
			return false;
		}
	}

	public boolean withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.removeHoldings(offlinePlayer.getUniqueId(), new BigDecimal(amount));
		case VAULT:
			return vaultEconomy.withdrawPlayer(offlinePlayer, amount).transactionSuccess();
		default:
			return false;
		}
	}

	public double getBalance(OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.getHoldings(offlinePlayer.getUniqueId()).doubleValue();
		case VAULT:
			return vaultEconomy.getBalance(offlinePlayer);
		default:
			return 0;
		}
	}

	public boolean setBalance(OfflinePlayer offlinePlayer, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.setHoldings(offlinePlayer.getUniqueId(),new BigDecimal(amount));
		case VAULT:
			return setBalance(offlinePlayer,amount);
		default:
			return false;
		}
	}

	public boolean hasMoney(OfflinePlayer offlinePlayer, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.hasHoldings(offlinePlayer.getUniqueId(), new BigDecimal(amount));
		case VAULT:
			return vaultEconomy.has(offlinePlayer, amount);
		default:
			return false;
		}
	}

	// **************************************************************************************************'
	// Banks
	// **************************************************************************************************
	public boolean hasBankSupport() {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.supportBanks();
		case VAULT:
			return vaultEconomy.hasBankSupport();
		default:
			return false;
		}
	}

	public boolean isBankAccountMember(String account, OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.isBankAccountMember(UUID.fromString(account), offlinePlayer.getUniqueId());
		case VAULT:
			return vaultEconomy.isBankMember(account, offlinePlayer).transactionSuccess();
		default:
			return false;
		}
	}

	public boolean isBankAccountOwner(String account, OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.isBankAccountOwner(UUID.fromString(account), offlinePlayer.getUniqueId());
		case VAULT:
			return vaultEconomy.isBankOwner(account, offlinePlayer).transactionSuccess();
		default:
			return false;
		}
	}

	public double bankBalance(String account) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.getBankHoldings(UUID.fromString(account)).doubleValue();
		case VAULT:
			return vaultEconomy.bankBalance(account).amount;
		default:
			return 0;
		}
	}

	/**
	public double setBankBalance(String account, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.setBankHoldings(UUID.fromString(account),new BigDecimal(amount));
		case VAULT:
			return vaultEconomy.bankBalance(account).amount;
		default:
			return 0;
		}
	}**/

	public boolean bankAccountHasAmount(String account, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.getBankHoldings(UUID.fromString(account)).doubleValue() > amount;
		case VAULT:
			return vaultEconomy.bankHas(account, amount).transactionSuccess();
		default:
			return false;
		}
	}

	public boolean bankAccountDeposit(String account, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.bankAddHoldings(UUID.fromString(account), UUID.fromString(account),
					new BigDecimal(amount));
		case VAULT:
			return vaultEconomy.bankDeposit(account, amount).transactionSuccess();
		default:
			return false;
		}
	}

	public boolean bankAccountWithdraw(String account, double amount) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.bankRemoveHoldings(UUID.fromString(account), UUID.fromString(account),
					new BigDecimal(amount));
		case VAULT:
			return vaultEconomy.bankWithdraw(account, amount).transactionSuccess();
		default:
			return false;
		}
	}

	public boolean createBankAccount(String account, OfflinePlayer offlinePlayer) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.createBankAccount(UUID.fromString(account)) != null;
		case VAULT:
			return vaultEconomy.createBank(account, offlinePlayer).transactionSuccess();
		default:
			return false;
		}
	}

	/**
	 * Delete a BankAccount
	 * 
	 * @param account
	 * @return
	 */
	public boolean deleteBankAccount(String account) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.deleteAccount(UUID.fromString(account));
		case VAULT:
			return vaultEconomy.deleteBank(account).transactionSuccess();
		default:
			return false;
		}
	}

	public String format(double d) {
		switch (Type) {
		case RESERVE:
			return reserveEconomy.format(new BigDecimal(d));
		case VAULT:
			return vaultEconomy.format(d);
		default:
			return "";
		}
	}
}
