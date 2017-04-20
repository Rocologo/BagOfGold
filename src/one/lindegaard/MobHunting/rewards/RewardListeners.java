package one.lindegaard.MobHunting.rewards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
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
import one.lindegaard.MobHunting.mobs.MinecraftMob;
import one.lindegaard.MobHunting.util.Misc;

public class RewardListeners implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupReward(PlayerPickupItemEvent event) {
		// This event is NOT called when the inventory is full.
		if (event.isCancelled())
			return;
		Item item = event.getItem();
		if (HiddenRewardData.hasHiddenRewardData(item)) {
			HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(item);
			Messages.debug("HiddenData=%s", hiddenRewardData.getHiddenLore());
			Player player = event.getPlayer();
			// If not Gringotts
			if (hiddenRewardData.getMoney() != 0)
				if (!MobHunting.getConfigManager().dropMoneyOnGroundUseAsCurrency) {
					MobHunting.getRewardManager().depositPlayer(player, hiddenRewardData.getMoney());
					if (ProtocolLibCompat.isSupported())
						ProtocolLibHelper.pickupMoney(player, item);
					item.remove();
					event.setCancelled(true);
					Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup", "money",
							MobHunting.getRewardManager().format(hiddenRewardData.getMoney())));
				} else {
					boolean found = false;
					HashMap<Integer, ? extends ItemStack> slots = player.getInventory()
							.all(item.getItemStack().getType());
					for (int slot : slots.keySet()) {
						ItemStack is = player.getInventory().getItem(slot);
						if (HiddenRewardData.hasHiddenRewardData(is)) {
							HiddenRewardData hrd = HiddenRewardData.getHiddenRewardData(is);
							if ((hiddenRewardData.isBagOfGoldReward() || hiddenRewardData.isItemReward())
									&& hrd.getRewardUUID().equals(hiddenRewardData.getRewardUUID())) {
								ItemMeta im = is.getItemMeta();
								HiddenRewardData newHiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
								newHiddenRewardData
										.setMoney(newHiddenRewardData.getMoney() + hiddenRewardData.getMoney());
								im.setLore(newHiddenRewardData.getHiddenLore());
								String displayName = MobHunting.getConfigManager().dropMoneyOnGroundItemtype
										.equalsIgnoreCase("ITEM")
												? MobHunting.getRewardManager().format(newHiddenRewardData.getMoney())
												: newHiddenRewardData.getDisplayname() + "(" + MobHunting
														.getRewardManager().format(newHiddenRewardData.getMoney())
														+ ")";
								im.setDisplayName(
										ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
												+ displayName);
								is.setItemMeta(im);
								is.setAmount(1);
								event.setCancelled(true);
								if (ProtocolLibCompat.isSupported())
									ProtocolLibHelper.pickupMoney(player, item);
								item.remove();
								Messages.debug("ItemStack in slot %s added value %s, new value %s", slot,
										MobHunting.getRewardManager().format(hiddenRewardData.getMoney()),
										MobHunting.getRewardManager().format(newHiddenRewardData.getMoney()));
								found = true;
								break;
							}
						}
					}

					if (!found) {
						ItemStack is = item.getItemStack();
						ItemMeta im = is.getItemMeta();
						String displayName = MobHunting.getConfigManager().dropMoneyOnGroundItemtype
								.equalsIgnoreCase("ITEM")
										? MobHunting.getRewardManager().format(hiddenRewardData.getMoney())
										: hiddenRewardData.getDisplayname() + " ("
												+ MobHunting.getRewardManager().format(hiddenRewardData.getMoney())
												+ ")";
						im.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
								+ displayName);
						im.setLore(hiddenRewardData.getHiddenLore());
						is.setItemMeta(im);
						item.setItemStack(is);
						item.setMetadata(RewardManager.MH_HIDDEN_REWARD_DATA, new FixedMetadataValue(
								MobHunting.getInstance(), new HiddenRewardData(hiddenRewardData)));
					}
				}
			if (RewardManager.getDroppedMoney().containsKey(item.getEntityId()))
				RewardManager.getDroppedMoney().remove(item.getEntityId());
			Messages.debug("%s picked up %s money. (# of rewards left=%s)", player.getName(),
					MobHunting.getRewardManager().format(hiddenRewardData.getMoney()),
					RewardManager.getDroppedMoney().size());
		} 
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropReward(PlayerDropItemEvent event) {
		if (event.isCancelled())
			return;
		Item item = event.getItemDrop();
		// ItemStack is = item.getItemStack();
		Player player = event.getPlayer();
		if (HiddenRewardData.hasHiddenRewardData(item)) {
			HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(item);
			double money = hiddenRewardData.getMoney();
			if (money == 0) {
				item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ hiddenRewardData.getDisplayname());
				Messages.debug("%s dropped a %s (# of rewards left=%s)", player.getName(),
						hiddenRewardData.getDisplayname(), RewardManager.getDroppedMoney().size());
			} else {
				String displayName = MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
						? MobHunting.getRewardManager().format(money)
						: hiddenRewardData.getDisplayname() + "(" + MobHunting.getRewardManager().format(money) + ")";
				item.setCustomName(
						ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor) + displayName);
				RewardManager.getDroppedMoney().put(item.getEntityId(), money);
				if (!MobHunting.getConfigManager().dropMoneyOnGroundUseAsCurrency)
					RewardManager.getEconomy().withdrawPlayer(player, money);
				Messages.debug("%s dropped %s money. (# of rewards left=%s)", player.getName(),
						MobHunting.getRewardManager().format(money), RewardManager.getDroppedMoney().size());
				Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneydrop", "money",
						MobHunting.getRewardManager().format(money)));
			}
			item.setCustomNameVisible(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onMobPickupReward(EntityInteractEvent event) {
		Entity entity = event.getEntity();
		Block block = event.getBlock();

		if (entity instanceof Item) {
			Item item = (Item) event.getEntity();
			if (item.hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA))
				Messages.debug("RewardListeners: EventInteractEvent MH_MONEY - %s, %s, %s ", entity.getType(),
						event.getEntityType(), block.getType());
		}
		if (entity instanceof Zombie) {
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
		} else if (entity instanceof Skeleton) {
			Skeleton z = (Skeleton) event.getEntity();
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
	public void onDespawnRewardEvent(ItemDespawnEvent event) {
		if (event.isCancelled())
			return;

		if (HiddenRewardData.hasHiddenRewardData(event.getEntity())) {
			if (RewardManager.getDroppedMoney().containsKey(event.getEntity().getEntityId())) {
				RewardManager.getDroppedMoney().remove(event.getEntity().getEntityId());
				Messages.debug("The reward was lost - despawned (# of rewards left=%s)",
						RewardManager.getDroppedMoney().size());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryPickupRewardEvent(InventoryPickupItemEvent event) {
		if (event.isCancelled())
			return;

		Item item = event.getItem();
		if (!item.hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA))
			return;

		if (MobHunting.getConfigManager().denyHoppersToPickUpMoney
				&& event.getInventory().getType() == InventoryType.HOPPER) {
			// Messages.debug("A %s tried to pick up the the reward, but this is
			// disabled in config.yml",
			// event.getInventory().getType());
			event.setCancelled(true);
		} else {
			// Messages.debug("The reward was picked up by %s",
			// event.getInventory().getType());
			if (RewardManager.getDroppedMoney().containsKey(item.getEntityId()))
				RewardManager.getDroppedMoney().remove(item.getEntityId());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMoveOverRewardEvent(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (player.getInventory().firstEmpty() == -1 && !player.getCanPickupItems()
				&& !RewardManager.getDroppedMoney().isEmpty()) {
			Iterator<Entity> entityList = ((Entity) player).getNearbyEntities(1, 1, 1).iterator();
			while (entityList.hasNext()) {
				Entity entity = entityList.next();
				if (!(entity instanceof Item))
					continue;
				Item item = (Item) entity;
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
								Messages.debug("%s picked up the %s money. (# of rewards left=%s)", player.getName(),
										MobHunting.getRewardManager().format(hiddenRewardDataOnGround.getMoney()),
										RewardManager.getDroppedMoney().size());
								Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup",
										"money",
										MobHunting.getRewardManager().format(hiddenRewardDataOnGround.getMoney())));
							} else {

								boolean found = false;
								HashMap<Integer, ? extends ItemStack> slots = player.getInventory()
										.all(item.getItemStack().getType());
								for (int slot : slots.keySet()) {
									ItemStack is = player.getInventory().getItem(slot);
									if (HiddenRewardData.hasHiddenRewardData(is)) {
										HiddenRewardData hrd = HiddenRewardData.getHiddenRewardData(is);
										if ((hiddenRewardDataOnGround.isBagOfGoldReward() || hiddenRewardDataOnGround.isItemReward())
												&& hrd.getRewardUUID().equals(hiddenRewardDataOnGround.getRewardUUID())) {
											ItemMeta im = is.getItemMeta();
											HiddenRewardData newHiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
											newHiddenRewardData
													.setMoney(newHiddenRewardData.getMoney() + hiddenRewardDataOnGround.getMoney());
											im.setLore(newHiddenRewardData.getHiddenLore());
											String displayName = MobHunting.getConfigManager().dropMoneyOnGroundItemtype
													.equalsIgnoreCase("ITEM")
															? MobHunting.getRewardManager().format(newHiddenRewardData.getMoney())
															: newHiddenRewardData.getDisplayname() + "(" + MobHunting
																	.getRewardManager().format(newHiddenRewardData.getMoney())
																	+ ")";
											im.setDisplayName(
													ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
															+ displayName);
											is.setItemMeta(im);
											is.setAmount(1);
											event.setCancelled(true);
											if (ProtocolLibCompat.isSupported())
												ProtocolLibHelper.pickupMoney(player, item);
											item.remove();
											Messages.debug("ItemStack in slot %s added value %s, new value %s", slot,
													MobHunting.getRewardManager().format(hiddenRewardDataOnGround.getMoney()),
													MobHunting.getRewardManager().format(newHiddenRewardData.getMoney()));
											found = true;
											break;
										}
									}
								}

								if (!found) {
									ItemStack is = item.getItemStack();
									ItemMeta im = is.getItemMeta();
									String displayName = MobHunting.getConfigManager().dropMoneyOnGroundItemtype
											.equalsIgnoreCase("ITEM")
													? MobHunting.getRewardManager().format(hiddenRewardDataOnGround.getMoney())
													: hiddenRewardDataOnGround.getDisplayname() + " ("
															+ MobHunting.getRewardManager().format(hiddenRewardDataOnGround.getMoney())
															+ ")";
									im.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
											+ displayName);
									im.setLore(hiddenRewardDataOnGround.getHiddenLore());
									is.setItemMeta(im);
									item.setItemStack(is);
									item.setMetadata(RewardManager.MH_HIDDEN_REWARD_DATA, new FixedMetadataValue(
											MobHunting.getInstance(), new HiddenRewardData(hiddenRewardDataOnGround)));
								}

							}
						}
					}
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHitRewardEvent(ProjectileHitEvent event) {
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRewardBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		ItemStack is = event.getItemInHand();
		Block block = event.getBlockPlaced();
		if (HiddenRewardData.hasHiddenRewardData(is)) {
			HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
			if (hiddenRewardData.getMoney() == 0)
				hiddenRewardData.setUniqueId(UUID.randomUUID());
			Messages.debug("Placed block-reward:%s", hiddenRewardData.toString());
			block.setMetadata(RewardManager.MH_HIDDEN_REWARD_DATA,
					new FixedMetadataValue(MobHunting.getInstance(), hiddenRewardData));
			RewardManager.getLocations().put(hiddenRewardData.getUniqueUUID(), hiddenRewardData);
			RewardManager.getHiddenRewardData().put(hiddenRewardData.getUniqueUUID(), block.getLocation());
			RewardManager.saveReward(hiddenRewardData.getUniqueUUID());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRewardBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (HiddenRewardData.hasHiddenRewardData(block)) {
			HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(block);
			block.getDrops().clear();
			block.setType(Material.AIR);
			block.removeMetadata(RewardManager.MH_HIDDEN_REWARD_DATA, MobHunting.getInstance());
			ItemStack is;
			if (hiddenRewardData.isBagOfGoldReward()) {
				is = CustomItems.getCustomtexture(hiddenRewardData.getRewardUUID(), hiddenRewardData.getDisplayname(),
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature,
						hiddenRewardData.getMoney(), hiddenRewardData.getUniqueUUID());
			} else { // (hiddenRewardData.getUuid()toString().equals(RewardManager.MH_REWARD_HEAD_UUID)){
				// Is it an EnderDragon
				if (hiddenRewardData.getDisplayname().equalsIgnoreCase(MinecraftMob.Skeleton.getDisplayName()))
					is = new ItemStack(Material.SKULL_ITEM, 1, (short) 0);
				else if (hiddenRewardData.getDisplayname()
						.equalsIgnoreCase(MinecraftMob.WitherSkeleton.getDisplayName()))
					is = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
				else if (hiddenRewardData.getDisplayname().equalsIgnoreCase(MinecraftMob.Zombie.getDisplayName()))
					is = new ItemStack(Material.SKULL_ITEM, 1, (short) 2);
				else if (hiddenRewardData.getDisplayname().equalsIgnoreCase(MinecraftMob.Creeper.getDisplayName()))
					is = new ItemStack(Material.SKULL_ITEM, 1, (short) 4);
				else if (hiddenRewardData.getDisplayname().equalsIgnoreCase(MinecraftMob.EnderDragon.getDisplayName()))
					is = new ItemStack(Material.SKULL_ITEM, 1, (short) 5);
				else
					is = CustomItems.getCustomtexture(hiddenRewardData.getRewardUUID(),
							hiddenRewardData.getDisplayname(),
							MinecraftMob.getTexture(hiddenRewardData.getDisplayname()),
							MinecraftMob.getSignature(hiddenRewardData.getDisplayname()), hiddenRewardData.getMoney(),
							hiddenRewardData.getUniqueUUID());
				is = RewardManager.setDisplayNameAndHiddenLores(is, hiddenRewardData.getDisplayname(),
						hiddenRewardData.getMoney(), hiddenRewardData.getRewardUUID());
			}
			Item item = block.getWorld().dropItemNaturally(block.getLocation(), is);
			if (hiddenRewardData.getMoney() == 0)
				item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ hiddenRewardData.getDisplayname());
			else
				item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ MobHunting.getRewardManager().format(hiddenRewardData.getMoney()));
			item.setCustomNameVisible(true);
			item.setMetadata(RewardManager.MH_HIDDEN_REWARD_DATA, new FixedMetadataValue(MobHunting.getInstance(),
					new HiddenRewardData(hiddenRewardData.getHiddenLore())));
			if (RewardManager.getLocations().containsKey(hiddenRewardData.getUniqueUUID()))
				RewardManager.getLocations().remove(hiddenRewardData.getUniqueUUID());
			if (RewardManager.getHiddenRewardData().containsKey(hiddenRewardData.getUniqueUUID()))
				RewardManager.getHiddenRewardData().remove(hiddenRewardData.getUniqueUUID());
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClickReward(InventoryClickEvent event) {
		if (event.isCancelled())
			return;

		// Inventory inv = event.getClickedInventory();
		InventoryAction action = event.getAction();
		// ClickType clickType = event.getClick();
		ItemStack isCurrentSlot = event.getCurrentItem();
		if (isCurrentSlot == null)
			return;
		ItemStack isCursor = event.getCursor();
		Player player = (Player) event.getWhoClicked();
		if ((isCurrentSlot.getType() == Material.SKULL_ITEM
				|| isCurrentSlot.getType() == Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem))
				&& isCurrentSlot.getType() == isCursor.getType() && action == InventoryAction.SWAP_WITH_CURSOR) {
			if (HiddenRewardData.hasHiddenRewardData(isCurrentSlot) && HiddenRewardData.hasHiddenRewardData(isCursor)) {
				ItemMeta imCurrent = isCurrentSlot.getItemMeta();
				ItemMeta imCursor = isCursor.getItemMeta();
				HiddenRewardData hiddenRewardData1 = new HiddenRewardData(imCurrent.getLore());
				HiddenRewardData hiddenRewardData2 = new HiddenRewardData(imCursor.getLore());
				if ((hiddenRewardData1.isBagOfGoldReward() || hiddenRewardData1.isItemReward())
						&& hiddenRewardData1.getRewardUUID().equals(hiddenRewardData2.getRewardUUID())) {
					hiddenRewardData2.setMoney(hiddenRewardData1.getMoney() + hiddenRewardData2.getMoney());
					imCursor.setLore(hiddenRewardData2.getHiddenLore());
					imCursor.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
							+ (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
									? MobHunting.getRewardManager().format(hiddenRewardData2.getMoney())
									: hiddenRewardData2.getDisplayname() + " ("
											+ MobHunting.getRewardManager().format(hiddenRewardData2.getMoney())
											+ ")"));
					isCursor.setItemMeta(imCursor);
					isCurrentSlot.setAmount(0);
					isCurrentSlot.setType(Material.AIR);
					Messages.debug("%s merged two rewards", player.getName());
				} else {
					Messages.debug("You can only merge Bag of gold and Money Items, and both items must be same type");
				}
			} else {
				Messages.debug("No Hiddendata");
			}

		} else if (isCursor.getType() == Material.AIR && (isCurrentSlot.getType() == Material.SKULL_ITEM
				|| isCurrentSlot.getType() == Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem))
				&& action == InventoryAction.PICKUP_HALF) {
			if (HiddenRewardData.hasHiddenRewardData(isCurrentSlot)) {
				HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(isCurrentSlot);
				if (hiddenRewardData.isBagOfGoldReward() || hiddenRewardData.isItemReward()) {
					double money = hiddenRewardData.getMoney() / 2;
					if (Misc.floor(money) >= MobHunting.getConfigManager().minimumReward) {
						event.setCancelled(true);
						if (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")) {
							isCurrentSlot = RewardManager.setDisplayNameAndHiddenLores(isCurrentSlot.clone(),
									hiddenRewardData.getDisplayname(), Misc.ceil(money),
									hiddenRewardData.getRewardUUID());
						} else {
							isCurrentSlot = CustomItems.getCustomtexture(hiddenRewardData.getRewardUUID(),
									hiddenRewardData.getDisplayname(),
									MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
									MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature,
									Misc.ceil(money), UUID.randomUUID());
						}

						event.setCurrentItem(isCurrentSlot);

						if (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")) {
							isCursor = RewardManager.setDisplayNameAndHiddenLores(isCurrentSlot.clone(),
									hiddenRewardData.getDisplayname(), Misc.floor(money),
									hiddenRewardData.getRewardUUID());
						} else {
							isCursor = CustomItems.getCustomtexture(hiddenRewardData.getRewardUUID(),
									hiddenRewardData.getDisplayname(),
									MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
									MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature,
									Misc.floor(money), UUID.randomUUID());
						}
						event.setCursor(isCursor);

						Messages.debug("%s halfed a reward in two (%s,%s)", player.getName(),
								MobHunting.getRewardManager().format(Misc.floor(money)),
								MobHunting.getRewardManager().format(Misc.ceil(money)));

						if (HiddenRewardData.hasHiddenRewardData(isCurrentSlot)
								|| HiddenRewardData.hasHiddenRewardData(isCursor)) {
							Messages.debug("HiddenData = (%s,%s)", isCurrentSlot.getItemMeta().getLore(),
									isCursor.getItemMeta().getLore());
						} else {
							Messages.debug("No hiddendata");
						}
					}
				}
			}
		}
	}

}
