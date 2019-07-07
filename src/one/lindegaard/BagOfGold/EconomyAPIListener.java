package one.lindegaard.BagOfGold;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import net.tnemc.core.Reserve;

public class EconomyAPIListener implements Listener {

	private Plugin plugin = null;
	EconomyAPI_BagOfGold economy = null;

	public EconomyAPIListener(Plugin plugin, EconomyAPI_BagOfGold economy) {
		this.plugin = plugin;
		this.economy = economy;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin eco = event.getPlugin();
		if (eco.getDescription().getName().equals("BagOfGold")) {
			BagOfGold bagofgold = (BagOfGold) plugin.getServer().getPluginManager().getPlugin("BagOfGold");
			if (BagOfGold.USE_RESERVE && economy.reserveEconomy == null) {
				economy.reserveEconomy = new BagOfGoldEconomyReserve(bagofgold);
				Reserve.instance().registerProvider(economy.reserveEconomy);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"[BagOfGold] " +ChatColor.RESET+"hooked into Reserve");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(PluginDisableEvent event) {
		if (event.getPlugin().getDescription().getName().equals("BagOfGold")) {
			if (economy != null && economy.reserveEconomy != null) {
				economy.reserveEconomy = null;
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD+"[BagOfGold] " +ChatColor.WHITE+"unhooked from Reserve");
			}
		}
	}
}
