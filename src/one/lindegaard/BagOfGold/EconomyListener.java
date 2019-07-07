package one.lindegaard.BagOfGold;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class EconomyListener implements Listener {

	private Plugin plugin = null;
	Economy_BagOfGold economy = null;

	public EconomyListener(Plugin plugin, Economy_BagOfGold economy) {
		this.plugin = plugin;
		this.economy = economy;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin eco = event.getPlugin();
		if (eco.getDescription().getName().equals("BagOfGold")) {
			BagOfGold bagofgold = (BagOfGold) plugin.getServer().getPluginManager().getPlugin("BagOfGold");
			if (economy.vaultEconomy == null) {
				economy.vaultEconomy = new BagOfGoldEconomyVault(bagofgold);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"[BagOfGold] " +ChatColor.RESET+"hooked into Vault");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("BagOfGold")) {
			if (economy != null && economy.vaultEconomy != null) {
				economy.vaultEconomy = null;
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"[BagOfGold] " +ChatColor.WHITE+"unhooked from Vault");
			}
		}
	}
}
