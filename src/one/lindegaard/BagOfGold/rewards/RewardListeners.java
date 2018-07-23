package one.lindegaard.BagOfGold.rewards;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.compatibility.PerWorldInventoryCompat;
import one.lindegaard.BagOfGold.util.Misc;

public class RewardListeners implements Listener {

	private BagOfGold plugin;

	public RewardListeners(BagOfGold plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalances(player);
		double amountInInventory = plugin.getEconomyManager().getAmountInInventory(player);
		if (Misc.round(amountInInventory) != Misc.round(ps.getBalance()) + Misc.round(ps.getBalanceChanges())) {
			ps.setBalance(Misc.round(ps.getBalance()) + Misc.round(ps.getBalanceChanges()));
			ps.setBalanceChanges(0);
			plugin.getPlayerBalanceManager().setPlayerBalance(player, ps);
			if (Misc.round(ps.getBalance()) > amountInInventory)
				plugin.getEconomyManager().addBagOfGoldPlayer(player, Misc.round(ps.getBalance()) - amountInInventory);
			else if (Misc.round(ps.getBalance()) < amountInInventory)
				plugin.getEconomyManager().removeBagOfGoldPlayer(player,
						amountInInventory - Misc.round(ps.getBalance()));
		}
		plugin.getMessages().debug("RewardListener: InventoryCloseEvent amt=%s, ps=%s", amountInInventory,
				ps.toString());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameChange(PlayerGameModeChangeEvent event) {

		if (event.isCancelled() || PerWorldInventoryCompat.isSupported())
			return;

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
				Player player = (Player) event.getPlayer();
				PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalances(player);
				double amountInInventory = plugin.getEconomyManager().getAmountInInventory(player);
				ps.setBalance(Misc.round(ps.getBalance()) + Misc.round(ps.getBalanceChanges()));
				ps.setBalanceChanges(0);
				plugin.getPlayerBalanceManager().setPlayerBalance(player, ps);
				if (Misc.round(ps.getBalance()) > amountInInventory)
					plugin.getEconomyManager().addBagOfGoldPlayer(player,
							Misc.round(ps.getBalance()) - amountInInventory);
				else if (Misc.round(ps.getBalance()) < amountInInventory)
					plugin.getEconomyManager().removeBagOfGoldPlayer(player,
							amountInInventory - Misc.round(ps.getBalance()));
				plugin.getMessages().debug("RewardListernes: PlayerGameModeChange %s (to %s) new balance is %s",
						player.getName(), event.getNewGameMode(), plugin.getEconomyManager().getBalance(player));
			}
		}, 3);
	}
}
