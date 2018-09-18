package one.lindegaard.BagOfGold.rewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.Reward;
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
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		if (player.isOnline() && player.isValid()) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				plugin.getMessages().debug(
						"RewardListener: InventoryCloseEvent adjusting balance to Amount of BagOfGold in Inventory: %s",
						ps.toString());
				
				

				plugin.getEconomyManager().adjustBalanceToamountInInventory(player);
			} else {
				plugin.getMessages().debug(
						"RewardListener: InventoryCloseEvent adjusting Amount of BagOfGold in Inventory To Balance: %s",
						ps.toString());
				plugin.getEconomyManager().adjustAmountInInventoryToBalance(player);
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
				PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldChange(PlayerChangedWorldEvent event) {

		if (PerWorldInventoryCompat.isSupported())
			return;

		Player player = (Player) event.getPlayer();
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		double amountInInventory = plugin.getEconomyManager().getAmountInInventory(player);
		ps.setBalance(Misc.round(ps.getBalance()) + Misc.round(ps.getBalanceChanges()));
		ps.setBalanceChanges(0);
		plugin.getPlayerBalanceManager().setPlayerBalance(player, ps);
		if (Misc.round(ps.getBalance()) > amountInInventory)
			plugin.getEconomyManager().addBagOfGoldPlayer(player, Misc.round(ps.getBalance()) - amountInInventory);
		else if (Misc.round(ps.getBalance()) < amountInInventory)
			plugin.getEconomyManager().removeBagOfGoldPlayer(player, amountInInventory - Misc.round(ps.getBalance()));
		plugin.getMessages().debug("RewardListernes: PlayerChangedWorld %s (from %s to %s) new balance is %s",
				player.getName(), event.getFrom(), event.getPlayer().getWorld(),
				plugin.getEconomyManager().getBalance(player));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {

		if (event.isCancelled())
			return;

		if (event.getRightClicked().getLocation() == null)
			return;

		Player player = event.getPlayer();

		if (event.getRightClicked().getType() == EntityType.ITEM_FRAME
				&& Reward.isReward(player.getInventory().getItemInMainHand())) {
			Reward reward = Reward.getReward(player.getInventory().getItemInMainHand());
			if (reward.getMoney() != 0) {
				plugin.getMessages().debug("%s placed a BagOfGod in an ItemFrame", player.getName());
				plugin.getEconomyManager().removeMoneyFromBalance(player, reward.getMoney());
				plugin.getMessages().playerActionBarMessageQueue(player,
						ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ reward.getDisplayname() + plugin.getMessages().getString("bagofgold.moneydrop",
										"money", Misc.round(reward.getMoney())));
			}
		}
	}
}
