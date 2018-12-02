package one.lindegaard.BagOfGold.rewards;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.BagOfGold.BagOfGold;

public class MoneyMergeEventListener implements Listener {

	private BagOfGold plugin;

	public MoneyMergeEventListener(BagOfGold plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onMoneyMergeEvent(ItemMergeEvent event) {
		// OBS: ItemMergeEvent does only exist in MC1.8 and newer

		if (event.isCancelled())
			return;

		Item item1 = event.getEntity();
		Item item2 = event.getTarget();
		ItemStack is2 = item2.getItemStack();
		if (Reward.isReward(item1) && Reward.isReward(item2)) {
			Reward reward1 = Reward.getReward(item1);
			Reward reward2 = Reward.getReward(item2);
			if (reward1.getRewardType().equals(reward2.getRewardType())
					&& (reward1.isBagOfGoldReward() || reward1.isItemReward())) {
				if (reward1.getMoney() + reward2.getMoney() != 0) {
					reward2.setMoney(reward1.getMoney() + reward2.getMoney());
					ItemMeta im = is2.getItemMeta();
					is2.setItemMeta(im);
					is2.setAmount(1);
					item2.setItemStack(is2);
					String displayName = plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
							? plugin.getEconomyManager().format(reward2.getMoney())
							: reward2.getDisplayname() + " (" + plugin.getEconomyManager().format(reward2.getMoney())
									+ ")";
					item2.setCustomName(
							ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + displayName);
					item2.setCustomNameVisible(true);
					item2.setMetadata(Reward.MH_REWARD_DATA,
							new FixedMetadataValue(BagOfGold.getInstance(), new Reward(reward2)));
					plugin.getMessages().debug("Rewards merged - new value=%s",
							plugin.getEconomyManager().format(reward2.getMoney()));
				} 
				if (plugin.getBagOfGoldItems().getDroppedMoney().containsKey(item1.getEntityId()))
					plugin.getBagOfGoldItems().getDroppedMoney().remove(item1.getEntityId());
			}
		}
	}
}
