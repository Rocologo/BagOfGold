package one.lindegaard.BagOfGold.rewards;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.compatibility.ProtocolLibCompat;
import one.lindegaard.BagOfGold.compatibility.ProtocolLibHelper;
import one.lindegaard.BagOfGold.util.Misc;

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
			double done = 0;
			Reward reward = Reward.getReward(item);
			if (reward.isBagOfGoldReward() || reward.isItemReward()) {
				callBack.setCancelled(true);
				done = plugin.getEconomyManager().depositPlayer(player, reward.getMoney()).amount;
				if (done > 0) {
					item.remove();
					if (plugin.getBagOfGoldItems().getDroppedMoney().containsKey(item.getEntityId()))
						plugin.getBagOfGoldItems().getDroppedMoney().remove(item.getEntityId());
					if (ProtocolLibCompat.isSupported())
						ProtocolLibHelper.pickupMoney(player, item);

					if (reward.getMoney() == 0) {
						plugin.getMessages().debug("%s picked up a %s (# of rewards left=%s)", player.getName(),
								plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM") ? "ITEM"
										: reward.getDisplayname(),
										plugin.getBagOfGoldItems().getDroppedMoney().size());
					} else {
						plugin.getMessages().debug(
								"%s picked up a %s with a value:%s (# of rewards left=%s)(PickupRewards)",
								player.getName(),
								plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM") ? "ITEM"
										: reward.getDisplayname(),
								plugin.getBagOfGoldItems().format(Misc.round(reward.getMoney())),
								plugin.getBagOfGoldItems().getDroppedMoney().size());
						if (!plugin.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
							plugin.getMessages().playerActionBarMessageQueue(player,
									plugin.getMessages().getString("bagofgold.moneypickup", "money",
											plugin.getBagOfGoldItems().format(reward.getMoney()), "rewardname",
											ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
													+ (reward.getDisplayname().isEmpty()
															? plugin.getConfigManager().dropMoneyOnGroundSkullRewardName
															: reward.getDisplayname())));
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
