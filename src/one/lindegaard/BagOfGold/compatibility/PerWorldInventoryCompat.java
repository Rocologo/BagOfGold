package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import me.ebonjaeger.perworldinventory.PerWorldInventory;
import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
import one.lindegaard.BagOfGold.util.Misc;

public class PerWorldInventoryCompat implements Listener {

	BagOfGold plugin;
	private static PerWorldInventory mPlugin;
	private static boolean supported = false;

	public PerWorldInventoryCompat() {
		plugin = BagOfGold.getInstance();
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "Compatibility with PerWorldInventory is disabled in config.yml");
		} else {
			mPlugin = (PerWorldInventory) Bukkit.getPluginManager().getPlugin(CompatPlugin.PerWorldInventory.getName());

			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
							+ "Enabling compatibility with PerWorldInventory ("
							+ getEssentials().getDescription().getVersion() + ")");
			Bukkit.getPluginManager().registerEvents(this, plugin);
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static PerWorldInventory getEssentials() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationPerWorldInventory;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	@EventHandler(priority = EventPriority.NORMAL)
	private void onInventoryChangeCompleted(InventoryLoadEvent event) {
		// TODO: Change to InventoryLoadCompleteEvent
		// private void onInventoryChangeComplated(InventoryLoadCompleteEvent
		// event) {

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				Player player = (Player) event.getPlayer();
				PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(player);
				double amountInInventory = plugin.getEconomyManager().getAmountInInventory(player);
				ps.setBalance(amountInInventory);
				plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
				if (Misc.round(ps.getBalanceChanges()) != 0) {
					plugin.getMessages().debug("PWI:%s balance changed while offline (%s), new balance is %s",
							player.getName(), Misc.round(ps.getBalanceChanges()),
							amountInInventory + Misc.round(ps.getBalanceChanges()));
					double change = ps.getBalanceChanges();
					if (change > 0)
						plugin.getEconomyManager().addBagOfGoldPlayer((Player) player, change);
					else
						plugin.getEconomyManager().removeBagOfGoldPlayer((Player) player, change);
					ps.setBalance(amountInInventory+Misc.round(ps.getBalanceChanges()));
					ps.setBalanceChanges(0);
					plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
				}

			}
		}, 3);
	}
}
