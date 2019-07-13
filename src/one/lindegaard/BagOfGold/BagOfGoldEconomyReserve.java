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

	/**
	 * @return Whether or not this implementation should have a default Vault
	 *         implementation.
	 */
	@Override
	public boolean vault() {
		// TODO: is correct ?
		return true;
	}

	// *******************************************************
	// NOT IMPLEMENTED
	// *******************************************************
	@Override
	public boolean addHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncAddHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanAddHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanDeposit(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanDeposit(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanDeposit(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanDeposit(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String arg0, String arg1, BigDecimal arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String arg0, String arg1, BigDecimal arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(String arg0, String arg1, BigDecimal arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanWithdraw(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanWithdraw(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanWithdraw(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCanWithdraw(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCreateAccount(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncCreateAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncDeleteAccount(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncDeleteAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<BigDecimal> asyncGetHoldings(UUID arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasAccount(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasCurrency(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasCurrency(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncHasHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncPurgeAccounts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncPurgeAccountsUnder(BigDecimal arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncSetHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(String arg0, String arg1, BigDecimal arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(String arg0, String arg1, BigDecimal arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(String arg0, String arg1, BigDecimal arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompletableFuture<Boolean> asyncTransferHoldings(UUID arg0, UUID arg1, BigDecimal arg2, String arg3,
			String arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canAddHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAddHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAddHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAddHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAddHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canAddHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDeposit(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDeposit(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDeposit(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDeposit(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRemoveHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRemoveHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRemoveHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRemoveHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canRemoveHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWithdraw(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWithdraw(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWithdraw(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canWithdraw(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String currencyDefaultPlural() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String currencyDefaultPlural(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String currencyDefaultSingular() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String currencyDefaultSingular(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String format(BigDecimal arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String format(BigDecimal arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String format(BigDecimal arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getHoldings(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getHoldings(UUID arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getHoldings(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getHoldings(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getHoldings(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BigDecimal getHoldings(UUID arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasAccount(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAccount(UUID arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCurrency(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasCurrency(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccessor(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccessor(String arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccessor(UUID arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAccessor(UUID arg0, UUID arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean purgeAccounts() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean purgeAccountsUnder(BigDecimal arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setHoldings(String arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setHoldings(UUID arg0, BigDecimal arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setHoldings(String arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setHoldings(UUID arg0, BigDecimal arg1, String arg2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setHoldings(String arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setHoldings(UUID arg0, BigDecimal arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
