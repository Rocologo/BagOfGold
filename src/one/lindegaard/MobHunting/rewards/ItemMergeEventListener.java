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

public class ItemMergeEventListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemMergeEvent(ItemMergeEvent e) {
		Item item1 = e.getEntity();
		Item item2 = e.getTarget();
		if (item1.hasMetadata(Rewards.MH_MONEY) || item2.hasMetadata(Rewards.MH_MONEY)) {
			double value1 = 0;
			if (item1.hasMetadata(Rewards.MH_MONEY)) {
				value1 = item1.getMetadata(Rewards.MH_MONEY).get(0).asDouble();
			}
			double value2 = 0;
			if (item2.hasMetadata(Rewards.MH_MONEY)) {
				value2 = item2.getMetadata(Rewards.MH_MONEY).get(0).asDouble();
			}
			if (value1 + value2 != 0) {
				item2.setMetadata(Rewards.MH_MONEY, new FixedMetadataValue(MobHunting.getInstance(), value1 + value2));
				item2.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ MobHunting.getRewardManager().format(value1 + value2));
				item2.setCustomNameVisible(true);
				Messages.debug("Rewards: Items merged - new value=%s", value1 + value2);
			}
		}
	}

}
