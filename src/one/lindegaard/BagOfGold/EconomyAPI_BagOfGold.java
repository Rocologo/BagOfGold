package one.lindegaard.BagOfGold;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class EconomyAPI_BagOfGold {

	private Plugin plugin = null;
	protected BagOfGoldEconomyReserve reserveEconomy = null;

	public EconomyAPI_BagOfGold(Plugin plugin) {
		this.plugin = plugin;
		
		Bukkit.getServer().getPluginManager().registerEvents(new EconomyAPIListener(plugin, this), plugin);

	}

	public boolean isEnabled() {
		return true;
	}

}
