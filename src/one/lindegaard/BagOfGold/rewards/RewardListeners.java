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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {

		Player player = (Player) event.getPlayer();
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		if (player.isOnline() && player.isValid()) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				plugin.getMessages().debug("onInventoryCloseEvent: Adjusting Player Balance to Amount of BagOfGold in Inventory: %s",
						ps.toString());
				plugin.getRewardManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
			} else if (player.getGameMode() == GameMode.SPECTATOR) {
				plugin.getMessages().debug("onInventoryCloseEvent: Player is in spectator mode. BagOfGold is not changed in Inventory: %s",
						ps.toString());
			} else {
				plugin.getMessages().debug("onInventoryCloseEvent: Adjusting Amount of BagOfGold in Inventory to Balance: %s", ps.toString());
				plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onGameModeChange(PlayerGameModeChangeEvent event) {

		if (event.isCancelled() || PerWorldInventoryCompat.isSupported())
			return;

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {

				Player player = event.getPlayer();
				if (player.getGameMode() == GameMode.SURVIVAL) {
					plugin.getMessages().debug(
							"onGameModeChange %s adjusting Player Balance to Amount of BagOfGold in Inventory",
							player.getName());
					plugin.getRewardManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
				} else if (player.getGameMode() == GameMode.SPECTATOR) {
					plugin.getMessages().debug(
							"onGameModeChange %s is in Spectator mode. BagOfGold is not changed.",
							player.getName());
				} else {
					plugin.getMessages().debug(
							"onGameModeChange %s adjusting Amount of BagOfGold in Inventory To Balance",
							player.getName());
					plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
				}
			}
		}, 3);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {

		if (PerWorldInventoryCompat.isSupported())
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.SURVIVAL) {
			plugin.getMessages().debug("onWorldChange: Adjusting %s's balance to amount of BagOfGold in Inventory", player.getName());
			plugin.getRewardManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
		} else if (player.getGameMode() == GameMode.SPECTATOR) {
			plugin.getMessages().debug("onWorldChange: %s is in spectator mode. BagOfGold is not changed.", player.getName());
		} else {
			plugin.getMessages().debug("onWorldChange: Adjusting %s's amount of BagOfGold in Inventory to balance", player.getName());
			plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
		}
		plugin.getMessages().debug("onWorldChange: Adjusting %s's balance from %s to %s, new balance is %s", player.getName(),
				event.getFrom(), event.getPlayer().getWorld(), plugin.getRewardManager().getBalance(player));
	}

}
