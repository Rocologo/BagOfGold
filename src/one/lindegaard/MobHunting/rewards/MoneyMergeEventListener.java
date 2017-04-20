package one.lindegaard.MobHunting.rewards;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class MoneyMergeEventListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMoneyMergeEvent(ItemMergeEvent event) {
		// OBS: ItemMergeEvent does only exist in MC1.8 and newer

		if (event.isCancelled())
			return;

		Item item1 = event.getEntity();
		Item item2 = event.getTarget();
		ItemStack is1 = item1.getItemStack();
		ItemStack is2 = item2.getItemStack();
		if ((MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
				&& is1.getType() == Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem)
				&& is2.getType() == Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem))
				|| (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")
						&& is1.getType() == Material.SKULL_ITEM && is2.getType() == Material.SKULL_ITEM)) {
			if (HiddenRewardData.hasHiddenRewardData(item1) && HiddenRewardData.hasHiddenRewardData(item2)) {
				HiddenRewardData hiddenRewardData1 = HiddenRewardData.getHiddenRewardData(item1);
				HiddenRewardData hiddenRewardData2 = HiddenRewardData.getHiddenRewardData(item2);
				if (hiddenRewardData1.getMoney() + hiddenRewardData2.getMoney() != 0) {
					hiddenRewardData2.setMoney(hiddenRewardData1.getMoney() + hiddenRewardData2.getMoney());
					ItemMeta im = is2.getItemMeta();
					is2.setItemMeta(im);
					is2.setAmount(0);
					item2.setItemStack(is2);
					String displayName = MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase(
							"ITEM") ? MobHunting.getRewardManager().format(hiddenRewardData2.getMoney())
									: hiddenRewardData2.getDisplayname() + "("
											+ MobHunting.getRewardManager().format(hiddenRewardData2.getMoney()) + ")";
					item2.setCustomName(
							ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor) + displayName);
					item2.setCustomNameVisible(true);
					item2.setMetadata(RewardManager.MH_HIDDEN_REWARD_DATA,
							new FixedMetadataValue(MobHunting.getInstance(), new HiddenRewardData(hiddenRewardData2)));
					Messages.debug("Rewards merged - new value=%s",
							MobHunting.getRewardManager().format(hiddenRewardData2.getMoney()));
				}
				if (RewardManager.getDroppedMoney().containsKey(item1.getEntityId()))
					RewardManager.getDroppedMoney().remove(item1.getEntityId());
			} else {
				Messages.debug("No hiddenData");
			}
		}
	}
}
