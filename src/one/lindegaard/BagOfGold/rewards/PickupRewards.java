package one.lindegaard.BagOfGold.rewards;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.compatibility.ProtocolLibCompat;
import one.lindegaard.BagOfGold.compatibility.ProtocolLibHelper;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.Tools;
import one.lindegaard.CustomItemsLib.rewards.Reward;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public class PickupRewards {

	private BagOfGold plugin;

	public PickupRewards(BagOfGold plugin) {
		this.plugin = plugin;
	}

	public void rewardPlayer(Player player, Item item, CallBack callBack) {
		if (Reward.isReward(item)) {
			Reward reward = Reward.getReward(item);
			if (reward.isBagOfGoldReward() || reward.isItemReward()) {
				callBack.setCancelled(true);
				boolean succes = plugin.getEconomyManager().depositPlayer(player, reward.getMoney());
				if (succes) {
					item.remove();
					if (Core.getCoreRewardManager().getDroppedMoney().containsKey(item.getEntityId()))
						Core.getCoreRewardManager().getDroppedMoney().remove(item.getEntityId());
					if (ProtocolLibCompat.isSupported())
						ProtocolLibHelper.pickupMoney(player, item);

					if (reward.getMoney() == 0) {
						plugin.getMessages().debug("%s picked up a %s (# of rewards left=%s)", player.getName(),
								reward.isItemReward() ? "ITEM" : reward.getDisplayName(),
								Core.getCoreRewardManager().getDroppedMoney().size());
					} else {
						plugin.getMessages().debug(
								"%s picked up a %s with a value:%s (# of rewards left=%s)(PickupRewards)",
								player.getName(), reward.isItemReward() ? "ITEM" : reward.getDisplayName(),
								Tools.format(Tools.round(reward.getMoney())),
								Core.getCoreRewardManager().getDroppedMoney().size());
						if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
							plugin.getMessages().playerActionBarMessageQueue(player, plugin.getMessages().getString(
									"bagofgold.moneypickup", "money",
									Tools.format(reward.getMoney()), "rewardname",
									ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
											+ (reward.getDisplayName().isEmpty() ? Core.getConfigManager().bagOfGoldName
													: reward.getDisplayName())));
					}
				} else {
					callBack.setCancelled(true);
				}
			}
		}
	}

	public interface CallBack {

		void setCancelled(boolean canceled);

	}

}
