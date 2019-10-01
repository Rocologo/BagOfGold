package one.lindegaard.BagOfGold.rewards;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import one.lindegaard.BagOfGold.BagOfGold;

public class EntityPickupItemEventListener implements Listener {

	private PickupRewards pickupRewards;

	public EntityPickupItemEventListener(PickupRewards pickupRewards) {
		this.pickupRewards = pickupRewards;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		// OBS: EntityPickupItemEvent does only exist in MC1.12 and newer

		// This event is NOT called when the inventory is full. 
		if (event.isCancelled())
			return;

		if (!Reward.isReward(event.getItem()))
			return;

		Entity entity = event.getEntity();

		if (event.getEntity().getType() != EntityType.PLAYER) {
			// Entity is not a Player
			if (entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.SKELETON)
					|| entity.getType().equals(EntityType.PIG_ZOMBIE)
					|| entity.getType().equals(EntityType.WITHER_SKELETON)) {
				event.setCancelled(true);
			}
			return;
		}

		Player player = (Player) entity;
		if (BagOfGold.getAPI().getBagOfGoldItems().canPickupMoney(player))
			pickupRewards.rewardPlayer((Player) entity, event.getItem(), event::setCancelled);
		else
			event.setCancelled(true);
	}

}
