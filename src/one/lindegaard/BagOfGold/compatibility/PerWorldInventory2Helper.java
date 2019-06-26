package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent;
import one.lindegaard.BagOfGold.BagOfGold;

public class PerWorldInventory2Helper {

	public static void registerPWI2Events(BagOfGold plugin) {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler(priority = EventPriority.HIGHEST)
			public void onInventoryLoad(InventoryLoadEvent event) {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						plugin.getMessages().debug("PerWorldInventoryCompat: onInventoryLoad");
						plugin.getEconomyManager().adjustAmountOfMoneyInInventoryToPlayerBalance(event.getPlayer());
					}
				}, 20);
			}
		}, plugin);
	}

}
