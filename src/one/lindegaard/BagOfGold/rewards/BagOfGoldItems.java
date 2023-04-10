package one.lindegaard.BagOfGold.rewards;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.Tools;
import one.lindegaard.CustomItemsLib.rewards.CoreCustomItems;
import one.lindegaard.CustomItemsLib.rewards.Reward;
import one.lindegaard.CustomItemsLib.rewards.RewardType;
import one.lindegaard.CustomItemsLib.server.Servers;

public class BagOfGoldItems implements Listener {

	BagOfGold plugin;

	public BagOfGoldItems(BagOfGold plugin) {
		this.plugin = plugin;
		if (isBagOfGoldStyle()) {
			Bukkit.getPluginManager().registerEvents(this, plugin);
		}
	}

	public boolean isBagOfGoldStyle() {
		return Core.getConfigManager().rewardItemtype.equalsIgnoreCase("SKULL")
				|| Core.getConfigManager().rewardItemtype.equalsIgnoreCase("ITEM")
				|| Core.getConfigManager().rewardItemtype.equalsIgnoreCase("KILLED")
				|| Core.getConfigManager().rewardItemtype.equalsIgnoreCase("KILLER");
	}


	public void dropBagOfGoldMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		Item item = null;
		double moneyLeftToDrop = Tools.ceil(money);
		ItemStack is;
		UUID skinuuid = null;
		RewardType rewardType;
		double nextBag = 0;
		while (moneyLeftToDrop > 0) {
			if (moneyLeftToDrop > Core.getConfigManager().limitPerBag) {
				nextBag = Core.getConfigManager().limitPerBag;
				moneyLeftToDrop = Tools.round(moneyLeftToDrop - nextBag);
			} else {
				nextBag = Tools.round(moneyLeftToDrop);
				moneyLeftToDrop = 0;
			}

			if (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("SKULL")) {
				rewardType = RewardType.BAGOFGOLD;
				skinuuid = UUID.fromString(RewardType.BAGOFGOLD.getUUID());
				is = CoreCustomItems.getCustomtexture(
						new Reward(Core.getConfigManager().bagOfGoldName, nextBag, rewardType, skinuuid),
						Core.getConfigManager().skullTextureValue, Core.getConfigManager().skullTextureSignature);
			} else { // ITEM
				rewardType = RewardType.ITEM;
				skinuuid = null;
				is = new ItemStack(Material.valueOf(Core.getConfigManager().rewardItem), 1);
			}

			Reward reward = new Reward(
					ChatColor.valueOf(Core.getConfigManager().rewardTextColor) + Core.getConfigManager().bagOfGoldName,
					nextBag, rewardType, skinuuid);
			is = Reward.setDisplayNameAndHiddenLores(is, reward);

			item = location.getWorld().dropItem(location, is);

			if (item != null) {
				Core.getCoreRewardManager().getDroppedMoney().put(item.getEntityId(), nextBag);
				item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, new Reward(reward)));
				item.setCustomName(is.getItemMeta().getDisplayName());
				item.setCustomNameVisible(Core.getConfigManager().showCustomDisplayname);
				if (player != null)
					plugin.getMessages().debug("%s dropped %s on the ground as item %s (# of rewards=%s)(3)",
							player.getName(), Tools.format(nextBag), Core.getConfigManager().rewardItemtype,
							Core.getCoreRewardManager().getDroppedMoney().size());
				else
					plugin.getMessages().debug("A %s(%s) was dropped on the ground as item %s (# of rewards=%s)(3)",
							Core.getConfigManager().rewardItemtype, Tools.format(nextBag),
							Core.getConfigManager().rewardItemtype,
							Core.getCoreRewardManager().getDroppedMoney().size());

			}
		}
	}

	public double getAmountOfBagOfGoldMoneyInInventory(Player player) {
		double amountInInventory = 0;

		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			ItemStack is = player.getInventory().getItem(slot);

			if (Reward.isReward(is)) {
				Reward reward = Reward.getReward(is);
				int amount = is.getAmount();

				if (reward.checkHash()) {
					if (reward.isMoney())
						amountInInventory = amountInInventory + reward.getMoney() * amount;
				} else {
					// Hash is wrong
					Bukkit.getConsoleSender().sendMessage(
							ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] " + player.getName()
									+ " has tried to change the value of a BagOfGold Item. Value set to 0!(3)");
					reward.setMoney(0);
					is = Reward.setDisplayNameAndHiddenLores(is, reward);
				}
			}
		}
		return amountInInventory;
	}

	public double getSpaceForBagOfGoldMoney(Player player) {
		double space = 0;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot>35) continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				int amount = is.getAmount();
				if (rewardInSlot.checkHash()) {
					if (rewardInSlot.isMoney())
						space = space + Core.getConfigManager().limitPerBag - rewardInSlot.getMoney() * amount;

				} else {
					Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX_WARNING + player.getName()
									+ " has tried to change the value of a BagOfGold Item. Value set to 0!(4)");
					rewardInSlot.setMoney(0);
					is = Reward.setDisplayNameAndHiddenLores(is, rewardInSlot);
				}
			} else if (is == null || is.getType() == Material.AIR) {
				space = space + Core.getConfigManager().limitPerBag;
			}
		}
		plugin.getMessages().debug("%s has room for %s BagOfGold in the inventory", player.getName(), space);
		return space;
	}

	// ***********************************************************************************
	// EVENTS
	// ***********************************************************************************
	
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
				plugin.getMessages().debug("onPlayerInteractEntityEvent: %s placed a BagOfGod in an ItemFrame", player.getName());
				plugin.getRewardManager().removeMoneyFromPlayer(player, reward.getMoney());
				if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(Core.getConfigManager().rewardTextColor) + reward.getDisplayName()
									+ plugin.getMessages().getString("bagofgold.moneydrop", "money",
											Tools.round(reward.getMoney())));
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		if (event.getClickedBlock() == null)
			return;

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		if (Servers.isMC19OrNewer() && event.getHand() != EquipmentSlot.HAND)
			return;

		Player player = event.getPlayer();

		Block block = event.getClickedBlock();

		if (Reward.isReward(block)) {
			Reward reward = Reward.getReward(block);
			if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted()) {
				if (reward.getMoney() == 0)
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(Core.getConfigManager().rewardTextColor) + reward.getDisplayName());
				else
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
									+ (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("ITEM")
											? Tools.format(reward.getMoney())
											: reward.getDisplayName() + " (" + Tools.format(reward.getMoney()) + ")"));
			}
		} else if (Servers.isMC113OrNewer()) {
			if (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD) {
				Skull skullState = (Skull) block.getState();
				OfflinePlayer owner = skullState.getOwningPlayer();
				if (owner != null && owner.getName() != null
						&& !Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(Core.getConfigManager().rewardTextColor) + owner.getName());
			}
		} else {
			if (block.getType() == Material.matchMaterial("SKULL_ITEM")
					|| block.getType() == Material.matchMaterial("SKULL")) {
				Skull skullState = (Skull) block.getState();
				OfflinePlayer owner = skullState.getOwningPlayer();
				if (owner != null && owner.getName() != null
						&& !Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(Core.getConfigManager().rewardTextColor) + owner.getName());
			}
		}
	}
	
	@EventHandler
	public void onVillagerTradeEvent(InventoryClickEvent event) {
		if (event.getClickedInventory() instanceof MerchantInventory inventory) {
			Integer slotClick = event.getSlot();
			// plugin.getMessages().debug("onVillagetTradeEvent:
			// slot=%s",slotClick.toString());
			MerchantInventory villagerMerchantInventory = inventory;
			ItemStack slotItem = villagerMerchantInventory.getItem(slotClick);
			// plugin.getMessages().debug("onVillagetTradeEvent:
			// slotItem=%s",slotItem.toString());
			MerchantRecipe villagerMerchantRecipe = villagerMerchantInventory.getSelectedRecipe();
			// plugin.getMessages().debug("onVillagetTradeEvent: Ingredients=%s",
			// villagerMerchantRecipe.getIngredients().toString());
			if (slotClick != 2) {
				return;
			}
			// if (slotItem != null || slotItem.getType() != Material.AIR){
			// Merchant entity = villagerMerchantInventory.getMerchant();
			// TradeEvent villagerTradeEvent = new TradeEvent(
			// (Player) entity.getTrader(),
			// entity,
			// villagerMerchantInventory,
			// villagerMerchantRecipe,
			// slotItem,
			// slotClick,
			// villagerMerchantRecipe.getAdjustedIngredient1(),
			// villagerMerchantRecipe.getMaxUses(),
			// villagerMerchantRecipe.getVillagerExperience()
			// );
			// Bukkit.getServer().getPluginManager().callEvent(villagerTradeEvent);
			// if (villagerTradeEvent.isCancelled()){ event.setCancelled(true); }
			// }
		}
	}

}
