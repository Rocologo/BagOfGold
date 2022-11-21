package one.lindegaard.BagOfGold.rewards;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import one.lindegaard.CustomItemsLib.Core;


@SuppressWarnings("deprecation")
public class PlayerPickupItemEventListener implements Listener {

	private PickupRewards pickupRewards;

	public PlayerPickupItemEventListener(PickupRewards pickupRewards) {
		this.pickupRewards = pickupRewards;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupReward(PlayerPickupItemEvent event) {
		// This event is NOT called when the inventory is full.
		if (event.isCancelled())
			return;

		if (Core.getCoreRewardManager().canPickupMoney(event.getPlayer()))
			pickupRewards.rewardPlayer(event.getPlayer(), event.getItem(), event::setCancelled);
		else
			event.setCancelled(true);
	}

}
