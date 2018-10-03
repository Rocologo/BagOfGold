package one.lindegaard.BagOfGold.rewards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.BagOfGold.BagOfGold;

public class GringottsItems implements Listener {

	BagOfGold plugin;

	public GringottsItems(BagOfGold plugin) {
		this.plugin = plugin;
		if (isGringottsStyle())
			Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public boolean isGringottsStyle() {
		return plugin.getConfigManager().dropMoneyOnGroundItemtype.equals("GRINGOTTS_STYLE");
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerDropReward(PlayerDropItemEvent event) {
		if (event.isCancelled())
			return;

		ItemStack is = event.getItemDrop().getItemStack();
		if (plugin.getConfigManager().gringottsDenomination.containsKey(is.getType().toString())) {
			Player player = event.getPlayer();
			plugin.getMessages().debug("%s dropped a %s with a value of %s", player.getName(), is.getType().toString(),
					plugin.getConfigManager().gringottsDenomination.get(is.getType().toString()));
			plugin.getEconomyManager().adjustBalanceToamountInInventory(player);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onRewardBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		ItemStack is = event.getItemInHand();

		if (plugin.getConfigManager().gringottsDenomination.containsKey(is.getType().toString())) {
			plugin.getMessages().debug("%s placed a %s with a value of %s", player.getName(), is.getType().toString(),
					plugin.getConfigManager().gringottsDenomination.get(is.getType().toString()));
			plugin.getEconomyManager().adjustBalanceToamountInInventory(player);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		// OBS: EntityPickupItemEvent does only exist in MC1.12 and newer

		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		ItemStack is = event.getItem().getItemStack();
		if (plugin.getConfigManager().gringottsDenomination.containsKey(is.getType().toString())) {
			plugin.getMessages().debug("%s picked up a %s with a value of %s", player.getName(),
					is.getType().toString(),
					plugin.getConfigManager().gringottsDenomination.get(is.getType().toString()));
			plugin.getEconomyManager().adjustBalanceToamountInInventory(player);
		}
	}

}
