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

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class MoneyMergeEventListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMoneyMergeEvent(ItemMergeEvent event) {
		// ItemMergeEvent does only exist in MC1.8 and newer
		if (event.isCancelled())
			return;
		
		Item item1 = event.getEntity();
		Item item2 = event.getTarget();
		ItemStack is1 = item1.getItemStack();
		ItemStack is2 = item2.getItemStack();
		if (is1.getType() == Material.SKULL_ITEM && is2.getType() == Material.SKULL_ITEM) {
			// Messages.debug("try to merge two SKULL_ITEM");
			if (HiddenRewardData.hasHiddenRewardData(is1) && HiddenRewardData.hasHiddenRewardData(is2)) {
				HiddenRewardData hiddenRewardData1 = HiddenRewardData.getHiddenRewardData(is1);
				HiddenRewardData hiddenRewardData2 = HiddenRewardData.getHiddenRewardData(is2);
				if (hiddenRewardData1.getMoney() + hiddenRewardData2.getMoney() != 0) {
					hiddenRewardData2.setMoney(hiddenRewardData1.getMoney() + hiddenRewardData2.getMoney());
					ItemMeta im = is2.getItemMeta();
					im.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
							+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " ("
							+ MobHunting.getRewardManager().format(hiddenRewardData2.getMoney()) + ")");
					im.setLore(hiddenRewardData2.getHiddenLore());
					is2.setItemMeta(im);
					item2.setItemStack(is2);
					Messages.debug("Rewards merged - new value=%s",
							MobHunting.getRewardManager().format(hiddenRewardData2.getMoney()));
				}
				if (RewardManager.getDroppedMoney().containsKey(item1.getEntityId()))
					RewardManager.getDroppedMoney().remove(item1.getEntityId());
			}
		}
	}
}
