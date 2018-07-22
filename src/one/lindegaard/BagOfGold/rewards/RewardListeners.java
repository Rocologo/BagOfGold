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
		plugin.getMessages().debug("RewardListener: amt=%s, ps=%s", amountInInventory, ps.toString());
		if (Misc.round(amountInInventory) != Misc.round(ps.getBalance())) {
			ps.setBalance(amountInInventory);
			ps.setBalanceChanges(0);
			plugin.getPlayerBalanceManager().setPlayerBalance(player, ps);
			plugin.getMessages().debug("%s closed inventory: new balance is %s", player.getName(),
					plugin.getEconomyManager().getBalance(player));
		}
		//if (Misc.round(ps.getBalanceChanges()) != 0) {
		//	if (Misc.round(ps.getBalanceChanges()) > 0)
		//		plugin.getEconomyManager().depositPlayer(player, Misc.round(ps.getBalanceChanges()));
		//	else if (Misc.round(ps.getBalanceChanges())<0)
		//		plugin.getEconomyManager().withdrawPlayer(player, -Misc.round(ps.getBalanceChanges()));
		//}
		
		// plugin.getMessages().debug("bal=%s,%s amt=%s", ps.getBalance(),
		// ps.getBalanceChanges(), amountInInventory);
		// if (Misc.round(ps.getBalance()+ps.getBalanceChanges())<0) {
		// plugin.getEconomyManager().removeBagOfGoldPlayer(player,
		// -(Misc.round(ps.getBalance())+Misc.round(ps.getBalanceChanges())));
		// ps.setBalance(amountInInventory+(Misc.round(ps.getBalance())+Misc.round(ps.getBalanceChanges())));
		// ps.setBalanceChanges(0);
		// }
		// plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
		// plugin.getMessages().debug("%s closed inventory: new balance is %s",
		// player.getName(),
		// plugin.getEconomyManager().getBalance(player));
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
				ps.setBalance(amountInInventory);
				ps.setBalanceChanges(0);
				plugin.getPlayerBalanceManager().setPlayerBalance(player, ps);
				plugin.getMessages().debug("%s gamemodechange: new balance is %s", player.getName(),
						plugin.getEconomyManager().getBalance(player));
			}
		}, 3);
	}
}
