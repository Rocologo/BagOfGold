package one.lindegaard.MobHunting.rewards;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.commands.HeadCommand;
import one.lindegaard.MobHunting.compatibility.ProtocolLibCompat;
import one.lindegaard.MobHunting.compatibility.ProtocolLibHelper;
import one.lindegaard.MobHunting.util.Misc;

public class RewardListeners implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupMoney(PlayerPickupItemEvent event) {
		// This event is NOT called when the inventory is full.
		Item item = event.getItem();
		if (item.hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA)) {
			HiddenRewardData hiddenRewardData = (HiddenRewardData) item.getMetadata(RewardManager.MH_HIDDEN_REWARD_DATA)
					.get(0).value();
			Player player = event.getPlayer();
			// If not Gringotts
			if (hiddenRewardData.getMoney() != 0)
				if (!MobHunting.getConfigManager().dropMoneyOnGroundUseAsCurrency) {
					MobHunting.getRewardManager().depositPlayer(player, hiddenRewardData.getMoney());
					item.remove();
					event.setCancelled(true);
					Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup", "money",
							MobHunting.getRewardManager().format(hiddenRewardData.getMoney())));

				} else {
					ItemStack is = item.getItemStack();
					ItemMeta im = is.getItemMeta();
					im.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
							+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " ("
							+ MobHunting.getRewardManager().format(hiddenRewardData.getMoney()) + ")");
					is.setItemMeta(im);
					item.setItemStack(is);
				}
			if (RewardManager.getDroppedMoney().containsKey(item.getEntityId()))
				RewardManager.getDroppedMoney().remove(item.getEntityId());
			Messages.debug("%s picked up %s money. (# of rewards left=%s)", player.getName(),
					MobHunting.getRewardManager().format(hiddenRewardData.getMoney()),
					RewardManager.getDroppedMoney().size());
		} 
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropMoney(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		ItemStack is = item.getItemStack();
		Player player = event.getPlayer();
		//Messages.debug("%s dropped %s on ground", player.getName(), is.getType());
		if (HiddenRewardData.hasHiddenRewardData(is)) {
			HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
			double money = hiddenRewardData.getMoney();
			item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
					+ MobHunting.getRewardManager().format(money));
			item.setCustomNameVisible(true);
			RewardManager.getDroppedMoney().put(item.getEntityId(), money);
			if (!MobHunting.getConfigManager().dropMoneyOnGroundUseAsCurrency)
				RewardManager.getEconomy().withdrawPlayer(player, money);
			Messages.debug("%s dropped %s money. (# of rewards left=%s)", player.getName(),
					MobHunting.getRewardManager().format(money), RewardManager.getDroppedMoney().size());
			Messages.playerActionBarMessage(player,
					Messages.getString("mobhunting.moneydrop", "money", MobHunting.getRewardManager().format(money)));
			item.setMetadata(RewardManager.MH_HIDDEN_REWARD_DATA,
					new FixedMetadataValue(MobHunting.getInstance(),
							new HiddenRewardData(MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, money,
									UUID.fromString(RewardManager.MH_REWARD_UUID), UUID.randomUUID())));
			//Messages.debug("Item has hidden MetaData=%s", item.hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onMobPickupMoney(EntityInteractEvent event) {
		if (event.getEntity() instanceof Item) {
			Item item = (Item) event.getEntity();
			if (//item.hasMetadata(RewardManager.MH_MONEY) || 
					item.hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA))
				Messages.debug("RewardListeners: EventInteractEvent MH_MONEY - %s, %s, %s ",
						event.getEntity().getType(), event.getEntityType(), event.getBlock().getType());
		}
		if (event.getEntity() instanceof Zombie) {
			Zombie z = (Zombie) event.getEntity();
			if (event.getBlock().hasMetadata(HeadCommand.MH_HEAD))
				Messages.debug("A Zombie did something, with a MobHuntingHead %s", event.getBlock());
			if (Misc.isMC19OrNewer()) {
				if ((z.getEquipment().getItemInMainHand().hasItemMeta()
						&& z.getEquipment().getItemInMainHand().getItemMeta().hasLore()
						&& z.getEquipment().getItemInMainHand().getItemMeta().getLore().get(0)
								.equals(HeadCommand.MH_HEAD))
						|| (z.getEquipment().getItemInOffHand().hasItemMeta()
								&& z.getEquipment().getItemInOffHand().getItemMeta().hasLore() && z.getEquipment()
										.getItemInOffHand().getItemMeta().getLore().get(0).equals(HeadCommand.MH_HEAD)))
					Messages.debug("Zombie hands = %s,%s", z.getEquipment().getItemInMainHand(),
							z.getEquipment().getItemInOffHand());
			} else {
				if (z.getEquipment().getItemInHand().hasItemMeta()
						&& z.getEquipment().getItemInHand().getItemMeta().hasLore()
						&& z.getEquipment().getItemInHand().getItemMeta().getLore().get(0).equals(HeadCommand.MH_HEAD))
					Messages.debug("Zombie hand = %s", z.getEquipment().getItemInHand());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMoneyDespawnEvent(ItemDespawnEvent event) {
		if (HiddenRewardData.hasHiddenRewardData(event.getEntity())) {
			if (RewardManager.getDroppedMoney().containsKey(event.getEntity().getEntityId()))
				RewardManager.getDroppedMoney().remove(event.getEntity().getEntityId());
			Messages.debug("The reward was lost - despawned (# of rewards left=%s)",
					RewardManager.getDroppedMoney().size());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryPickupMoneyEvent(InventoryPickupItemEvent event) {
		Item item = event.getItem();
		if (RewardManager.getDroppedMoney().containsKey(item.getEntityId()))
			RewardManager.getDroppedMoney().remove(item.getEntityId());
		if (MobHunting.getConfigManager().denyHoppersToPickUpMoney
				&& (item.hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA))
				&& event.getInventory().getType() == InventoryType.HOPPER) {
			Messages.debug("A %s tried to pick up the the reward, but this is disabled in config.yml",
					event.getInventory().getType());
			event.setCancelled(true);
		} else {
			Messages.debug("The reward was picked up by %s", event.getInventory().getType());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMoveOverMoneyEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getInventory().firstEmpty() == -1 && !player.getCanPickupItems()
				&& !RewardManager.getDroppedMoney().isEmpty()) {
			Iterator<Entity> entityList = ((Entity) player).getNearbyEntities(1, 1, 1).iterator();
			while (entityList.hasNext()) {
				Entity entity = entityList.next();
				if (!(entity instanceof Item))
					continue;
				if (RewardManager.getDroppedMoney().containsKey(entity.getEntityId())) {
					if (HiddenRewardData.hasHiddenRewardData(entity)) {
						HiddenRewardData hiddenRewardDataOnGround = HiddenRewardData.getHiddenRewardData(entity);
						double moneyOnGround = hiddenRewardDataOnGround.getMoney();
						// If not Gringotts
						if (hiddenRewardDataOnGround.getMoney() != 0) {
							if (ProtocolLibCompat.isSupported())
								ProtocolLibHelper.pickupMoney(player, entity);
							RewardManager.getDroppedMoney().remove(entity.getEntityId());
							if (!MobHunting.getConfigManager().dropMoneyOnGroundUseAsCurrency) {
								MobHunting.getRewardManager().depositPlayer(player, moneyOnGround);
								entity.remove();
								Messages.debug("%s picked up the %s money. (# of rewards left=%s)",
										player.getName(),
										MobHunting.getRewardManager().format(hiddenRewardDataOnGround.getMoney()),
										RewardManager.getDroppedMoney().size());
								Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup",
										"money",
										MobHunting.getRewardManager().format(hiddenRewardDataOnGround.getMoney())));
							} else {
								int slot = player.getInventory().first(Material.SKULL_ITEM);
								//Messages.debug("%s has a Bag of Gold in slot %s", player.getName(), slot);
								if (slot != -1) {
									ItemStack is = player.getInventory().getItem(slot);
									if (HiddenRewardData.hasHiddenRewardData(is)) {
										ItemMeta im = is.getItemMeta();
										HiddenRewardData newHiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
										newHiddenRewardData.setMoney(
												newHiddenRewardData.getMoney() + hiddenRewardDataOnGround.getMoney());
										im.setLore(newHiddenRewardData.getLore());
										im.setDisplayName(ChatColor
												.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
												+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " ("
												+ MobHunting.getRewardManager().format(newHiddenRewardData.getMoney())
												+ " )");
										is.setItemMeta(im);
										is.setAmount(1);
										entity.remove();
										Messages.debug("ItemStack in slot %s added value %s, new value %s", slot,
												hiddenRewardDataOnGround.getMoney(), newHiddenRewardData.getMoney());
									}
								}
							}
						}
					}
				} 
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		Entity targetEntity = null;
		Iterator<Entity> nearby = projectile.getNearbyEntities(1, 1, 1).iterator();
		while (nearby.hasNext()) {
			targetEntity = nearby.next();
			if (HiddenRewardData.hasHiddenRewardData(targetEntity)) {
				if (RewardManager.getDroppedMoney().containsKey(targetEntity.getEntityId()))
					RewardManager.getDroppedMoney().remove(targetEntity.getEntityId());
				targetEntity.remove();
				Messages.debug("The reward was hit by %s and removed. (# of rewards left=%s)", projectile.getType(),
						RewardManager.getDroppedMoney().size());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack is = event.getItemInHand();
		Block block = event.getBlockPlaced();
		//Messages.debug("%s placed a %s on a %s while holding %s", event.getPlayer().getName(), block.getType(),
		//		event.getBlockAgainst().getType(), is.getType());
		if (HiddenRewardData.hasHiddenRewardData(is)) {
			HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
			block.setMetadata(RewardManager.MH_HIDDEN_REWARD_DATA,
					new FixedMetadataValue(MobHunting.getInstance(), hiddenRewardData));
			RewardManager.getLocations().put(hiddenRewardData.getUniqueId(), hiddenRewardData);
			RewardManager.getHiddenRewardData().put(hiddenRewardData.getUniqueId(), block.getLocation());
			Messages.debug("HiddenRewardData added=%s",
					((HiddenRewardData) block.getMetadata(RewardManager.MH_HIDDEN_REWARD_DATA).get(0).value())
							.getLore());
			RewardManager.saveReward(hiddenRewardData.getUniqueId());
		} 
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		//Messages.debug("%s broke a %s", event.getPlayer().getName(), event.getBlock().getType());
		//Messages.debug("Block has Metadata=%s", event.getBlock().hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA));
		Block block = event.getBlock();
		if (HiddenRewardData.hasHiddenRewardData(block)) {
			event.setCancelled(true);
			block.setType(Material.AIR);
			HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData( block);
			ItemStack is = CustomItems.getCustomtexture(RewardManager.MH_REWARD_UUID,
					ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
							+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
					MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
					MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature, hiddenRewardData.getMoney(),
					hiddenRewardData.getUniqueId());

			Item item = block.getWorld().dropItemNaturally(block.getLocation(), is);
			item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
					+ MobHunting.getRewardManager().format(hiddenRewardData.getMoney()));
			item.setCustomNameVisible(true);
			item.setMetadata(RewardManager.MH_HIDDEN_REWARD_DATA,
					new FixedMetadataValue(MobHunting.getInstance(), new HiddenRewardData(hiddenRewardData.getLore())));
			if (RewardManager.getLocations().containsKey(hiddenRewardData.getUniqueId()))
				RewardManager.getLocations().remove(hiddenRewardData.getUniqueId());
			if (RewardManager.getHiddenRewardData().containsKey(hiddenRewardData.getUniqueId()))
				RewardManager.getHiddenRewardData().remove(hiddenRewardData.getUniqueId());

		} 
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		// Inventory inv = event.getClickedInventory();
		InventoryAction action = event.getAction();
		//ClickType clickType = event.getClick();
		ItemStack isCurrentSlot = event.getCurrentItem();
		if (isCurrentSlot == null)
			return;
		ItemStack isCursor = event.getCursor();
		Player player = (Player) event.getWhoClicked();
		if (isCurrentSlot.getType() == Material.SKULL_ITEM && isCurrentSlot.getType() == isCursor.getType()
				&& action == InventoryAction.SWAP_WITH_CURSOR) {
			if (HiddenRewardData.hasHiddenRewardData(isCurrentSlot) && HiddenRewardData.hasHiddenRewardData(isCursor)) {
				ItemMeta imCurrent = isCurrentSlot.getItemMeta();
				ItemMeta imCursor = isCursor.getItemMeta();
				HiddenRewardData hiddenRewardData1 = new HiddenRewardData(imCurrent.getLore());
				HiddenRewardData hiddenRewardData2 = new HiddenRewardData(imCursor.getLore());
				hiddenRewardData2.setMoney(hiddenRewardData1.getMoney() + hiddenRewardData2.getMoney());
				imCursor.setLore(hiddenRewardData2.getLore());
				imCursor.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " ("
						+ MobHunting.getRewardManager().format(hiddenRewardData2.getMoney()) + ")");
				isCursor.setItemMeta(imCursor);
				isCurrentSlot.setAmount(0);
				isCurrentSlot.setType(Material.AIR);
				Messages.debug("%s merged two rewards",player.getName());
			}
		} else if (isCursor.getType() == Material.AIR && isCurrentSlot.getType() == Material.SKULL_ITEM
				&& action == InventoryAction.PICKUP_HALF) {
			if (HiddenRewardData.hasHiddenRewardData(isCurrentSlot)) {
				event.setCancelled(true);
				ItemMeta imCurrentSlot = isCurrentSlot.getItemMeta();
				double money = new HiddenRewardData(imCurrentSlot.getLore()).getMoney()/2;
				isCurrentSlot=CustomItems.getCustomtexture(RewardManager.MH_REWARD_UUID,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature,
						Double.valueOf(money), UUID.randomUUID());
				isCursor=CustomItems.getCustomtexture(RewardManager.MH_REWARD_UUID,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature,
						Double.valueOf(money), UUID.randomUUID());
				event.setCurrentItem(isCurrentSlot);
				event.setCursor(isCursor);
				Messages.debug("%s halfed a reward in two",player.getName());
			}
		}
	}

}
