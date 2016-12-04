package one.lindegaard.MobHunting.rewards;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class MoneyMergeEventListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMoneyMergeEvent(ItemMergeEvent e) {
		// ItemMergeEvent does only exist in MC1.8 and newer
		Item item1 = e.getEntity();
		Item item2 = e.getTarget();
		if (item1.hasMetadata(RewardManager.MH_MONEY) || item2.hasMetadata(RewardManager.MH_MONEY)) {
			double value1 = 0;
			if (item1.hasMetadata(RewardManager.MH_MONEY)) {
				value1 = item1.getMetadata(RewardManager.MH_MONEY).get(0).asDouble();
			}
			double value2 = 0;
			if (item2.hasMetadata(RewardManager.MH_MONEY)) {
				value2 = item2.getMetadata(RewardManager.MH_MONEY).get(0).asDouble();
			}
			if (value1 + value2 != 0) {
				item2.setMetadata(RewardManager.MH_MONEY,
						new FixedMetadataValue(MobHunting.getInstance(), value1 + value2));
				item2.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ MobHunting.getRewardManager().format(value1 + value2));
				item2.setCustomNameVisible(true);
				Messages.debug("Rewards merged - new value=%s", MobHunting.getRewardManager().format(value1 + value2));
			}
			if (RewardManager.getDroppedMoney().containsKey(item1.getEntityId()))
				RewardManager.getDroppedMoney().remove(item1.getEntityId());
		}
	}

}
