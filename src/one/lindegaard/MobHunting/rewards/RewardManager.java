package one.lindegaard.MobHunting.rewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.util.Misc;

public class RewardManager implements Listener {

	private static Economy mEconomy;

	public RewardManager(MobHunting instance) {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager()
				.getRegistration(Economy.class);
		if (economyProvider == null) {
			Bukkit.getLogger().severe(Messages.getString(instance.getName().toLowerCase() + ".hook.econ"));
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		}
		mEconomy = economyProvider.getProvider();
		
		Bukkit.getPluginManager().registerEvents(new Rewards(), instance);
		if (Misc.isMC18OrNewer())
			Bukkit.getPluginManager().registerEvents(new ItemMergeEventListener(), MobHunting.getInstance());
		
	}

	public Economy getEconomy() {
		return mEconomy;
	}

	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		EconomyResponse result = mEconomy.withdrawPlayer(offlinePlayer, amount);
		if (!result.transactionSuccess() && offlinePlayer.isOnline())
			((Player) offlinePlayer).sendMessage(ChatColor.RED + "Unable to remove money: " + result.errorMessage);
		return result;
	}

	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		EconomyResponse result = mEconomy.depositPlayer(offlinePlayer, amount);
		if (!result.transactionSuccess() && offlinePlayer.isOnline())
			((Player) offlinePlayer).sendMessage(ChatColor.RED + "Unable to add money: " + result.errorMessage);
		return result;
	}

	public String format(double amount) {
		return mEconomy.format(amount);
	}

	public double getBalance(OfflinePlayer offlinePlayer) {
		return mEconomy.getBalance(offlinePlayer);
	}

	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		return mEconomy.has(offlinePlayer, amount);
	}

}
