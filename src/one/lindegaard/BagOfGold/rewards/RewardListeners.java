package one.lindegaard.BagOfGold.rewards;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.compatibility.PerWorldInventoryCompat;

public class RewardListeners implements Listener {

	private BagOfGold plugin;

	public RewardListeners(BagOfGold plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		if (player.isOnline() && player.isValid()) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				plugin.getMessages().debug(
						"RewardListener: InventoryCloseEvent adjusting Player Balance to Amount of BagOfGold in Inventory: %s",
						ps.toString());
				plugin.getEconomyManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
			} else {
				plugin.getMessages().debug(
						"RewardListener: InventoryCloseEvent adjusting Amount of BagOfGold in Inventory To Balance: %s",
						ps.toString());
				plugin.getEconomyManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameModeChange(PlayerGameModeChangeEvent event) {

		if (event.isCancelled() || PerWorldInventoryCompat.isSupported())
			return;

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				Player player = (Player) event.getPlayer();
				if (player.getGameMode() == GameMode.SURVIVAL) {
					plugin.getMessages().debug(
							"RewardListener: PlayerGameModeChange %s adjusting Player Balance to Amount of BagOfGold in Inventory");
					plugin.getEconomyManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
				} else {
					plugin.getMessages().debug(
							"RewardListener: PlayerGameModeChange %s adjusting Amount of BagOfGold in Inventory To Balance");
					plugin.getEconomyManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
				}
			}
		}, 3);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldChange(PlayerChangedWorldEvent event) {

		if (PerWorldInventoryCompat.isSupported())
			return;

		Player player = (Player) event.getPlayer();
		if (player.getGameMode() == GameMode.SURVIVAL) {
			plugin.getMessages().debug(
					"RewardListener: PlayerChangedWorld %s adjusting Player Balance to Amount of BagOfGold in Inventory");
			plugin.getEconomyManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
		} else {
			plugin.getMessages().debug(
					"RewardListener: PlayerChangedWorld %s adjusting Amount of BagOfGold in Inventory To Balance");
			plugin.getEconomyManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
		}
		plugin.getMessages().debug("RewardListernes: PlayerChangedWorld %s (from %s to %s) new balance is %s",
				player.getName(), event.getFrom(), event.getPlayer().getWorld(),
				plugin.getEconomyManager().getBalance(player));
	}

}
