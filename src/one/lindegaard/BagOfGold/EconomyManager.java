package one.lindegaard.BagOfGold;

import org.bukkit.Bukkit;
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

	public enum EcoType {
		NONE, VAULT, RESERVE
	}

	public EconomyManager(BagOfGold plugin) {
		this.plugin = plugin;

		setupEconomyManager();

		if (plugin.getConfigManager().useBagOfGoldAsAnEconomyPlugin) {

			Plugin vaultPlugin = Bukkit.getPluginManager().getPlugin("Vault");
			if (vaultPlugin != null)
				BagOfGoldEconomyVault.hookVaultEconomy(Economy_BagOfGold.class, ServicePriority.Normal,
						"net.milkbowl.vault.economy.Economy");

			if (BagOfGold.ENABLE_RESERVE) {
				Plugin reservePlugin = Bukkit.getPluginManager().getPlugin("Reserve");
				if (reservePlugin != null)
					new BagOfGoldEconomyReserve(plugin);
			}
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

}
