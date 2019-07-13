package one.lindegaard.BagOfGold;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.tnemc.core.economy.EconomyAPI;

public class EconomyAPI_BagOfGold {

	private BagOfGold plugin = null;
	protected BagOfGoldEconomyReserve reserveEconomy = null;

	public EconomyAPI_BagOfGold(BagOfGold plugin) {
		this.plugin = plugin;

		Bukkit.getServer().getPluginManager().registerEvents(new EconomyAPIListener(plugin, this), plugin);

		plugin.getMessages().debug("Number of Reserve Economy Providers = %s",
				Bukkit.getServicesManager().getRegistrations(EconomyAPI.class).size());
		if (Bukkit.getServicesManager().getRegistrations(EconomyAPI.class).size() > 1) {
			for (RegisteredServiceProvider<EconomyAPI> registation : Bukkit.getServicesManager()
					.getRegistrations(EconomyAPI.class)) {
				plugin.getMessages().debug("Reserve Economy Providername=%s", registation.getProvider().name());
			}
		}

	}

}
