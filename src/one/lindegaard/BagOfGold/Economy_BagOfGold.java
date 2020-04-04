package one.lindegaard.BagOfGold;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import one.lindegaard.BagOfGold.BagOfGoldEconomyVault;

public class Economy_BagOfGold extends AbstractEconomy {

	private Plugin plugin = null;
	protected BagOfGoldEconomyVault vaultEconomy = null;
	
	public Economy_BagOfGold(Plugin plugin) {
		this.plugin = plugin;

		Bukkit.getServer().getPluginManager().registerEvents(new EconomyListener(plugin, this), plugin);

	}

	@Override
	public boolean isEnabled() {
		if (vaultEconomy == null) 
			return false;
		return true;
	}

	@Override
	public String getName() {
		return "BagOfGold";
	}

	@Override
	public String format(double amount) {
		return vaultEconomy.format(amount);
	}

	@Override
	public String currencyNamePlural() {
		return vaultEconomy.currencyNamePlural();
	}

	@Override
	public String currencyNameSingular() {
		return vaultEconomy.currencyNameSingular();
	}

	@Override
	public int fractionalDigits() {
		return vaultEconomy.fractionalDigits();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean createPlayerAccount(String playername) {
		return vaultEconomy.createPlayerAccount(playername);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean createPlayerAccount(String playername, String world) {
		return vaultEconomy.createPlayerAccount(playername, playername);
	}

	@SuppressWarnings("deprecation")
	@Override
	public double getBalance(String playername) {
		return vaultEconomy.getBalance(playername);
	}

	@SuppressWarnings("deprecation")
	@Override
	public double getBalance(String playername, String world) {
		return vaultEconomy.getBalance(playername, world);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean has(String playername, double amount) {
		return vaultEconomy.has(playername, amount);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean has(String playername, String world, double amount) {
		return vaultEconomy.has(playername, world, amount);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse depositPlayer(String playername, double amount) {
		return vaultEconomy.depositPlayer(playername, amount);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse depositPlayer(String playername, String world, double amount) {
		return vaultEconomy.depositPlayer(playername, world, amount);
	}

	@Override
	public boolean hasBankSupport() {
		return vaultEconomy.hasBankSupport();
	}

	@Override
	public List<String> getBanks() {
		return vaultEconomy.getBanks();
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse createBank(String account, String playername) {
		return vaultEconomy.createBank(account, playername);
	}

	@Override
	public EconomyResponse deleteBank(String account) {
		return vaultEconomy.deleteBank(account);
	}

	@Override
	public EconomyResponse bankHas(String account, double amount) {
		return vaultEconomy.bankHas(account, amount);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean hasAccount(String playername) {
		return vaultEconomy.hasAccount(playername);
	}

	@Override
	public boolean hasAccount(String playername, String world) {
		return vaultEconomy.hasAccount(playername, world);
	}

	@Override
	public EconomyResponse bankBalance(String playername) {
		return vaultEconomy.bankBalance(playername);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse isBankOwner(String account, String playername) {
		return vaultEconomy.isBankOwner(account, playername);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse isBankMember(String account, String playername) {
		return vaultEconomy.isBankMember(account, playername);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse withdrawPlayer(String playername, double amount) {
		return vaultEconomy.withdrawPlayer(playername, amount);
	}

	@SuppressWarnings("deprecation")
	@Override
	public EconomyResponse withdrawPlayer(String playername, String world, double amount) {
		return vaultEconomy.withdrawPlayer(playername, world, amount);
	}

	@Override
	public EconomyResponse bankDeposit(String playername, double world) {
		return vaultEconomy.bankDeposit(playername, world);
	}

	@Override
	public EconomyResponse bankWithdraw(String playername, double amount) {
		return vaultEconomy.bankWithdraw(playername, amount);
	}

}
