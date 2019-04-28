package one.lindegaard.BagOfGold.rewards;

import java.util.Iterator;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.Tools;
import one.lindegaard.Core.Server.Servers;
import one.lindegaard.Core.rewards.CustomItems;
import one.lindegaard.Core.rewards.Reward;

public class BagOfGoldListeners implements Listener {

	BagOfGold plugin;

	public BagOfGoldListeners(BagOfGold plugin) {
		this.plugin = plugin;
	}

	private boolean isFakeReward(Item item) {
		ItemStack itemStack = item.getItemStack();
		return isFakeReward(itemStack);
	}

	private boolean isFakeReward(ItemStack itemStack) {
		if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack
				.getItemMeta().getDisplayName().contains(plugin.getConfigManager().dropMoneyOnGroundSkullRewardName)) {
			if (!itemStack.getItemMeta().hasLore()) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerDropReward(PlayerDropItemEvent event) {

		plugin.getMessages().debug("An item was dropped");

		if (event.isCancelled())
			return;

		Item item = event.getItemDrop();
		Player player = event.getPlayer();

		if (isFakeReward(item)) {
			player.sendMessage(ChatColor.RED + "[BagOfGold] WARNING, this was a FAKE reward with no value");
			return;
		}

		if (Reward.isReward(item)) {
			Reward reward = Reward.getReward(item);
			double money = reward.getMoney();
			if (money == 0) {
				item.setCustomName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
						+ reward.getDisplayname());
				Core.getAPI().getBagOfGoldItems().getDroppedMoney().put(item.getEntityId(), money);
				plugin.getMessages().debug("%s dropped a %s (# of rewards left=%s)(1)", player.getName(),
						reward.getDisplayname() != null ? reward.getDisplayname()
								: plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
						Core.getAPI().getRewardManager().getDroppedMoney().size());
			} else {
				if (reward.isItemReward())
					item.setCustomName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
							+ Tools.format(money));
				else
					item.setCustomName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
							+ reward.getDisplayname() + " (" + Tools.format(money) + ")");

				Core.getAPI().getRewardManager().getDroppedMoney().put(item.getEntityId(), money);
				plugin.getMessages().debug("%s dropped %s money. (# of rewards left=%s)(2)", player.getName(),
						Tools.format(money), Core.getAPI().getRewardManager().getDroppedMoney().size());
				if (!plugin.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
					plugin.getMessages().playerActionBarMessageQueue(player, plugin.getMessages().getString(
							"bagofgold.moneydrop", "money", Tools.format(money), "rewardname",
							ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
									+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
											? plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()
											: reward.getDisplayname())));
				plugin.getMessages().debug("BagOfGoldItems: OpenInv.type=%s, cursor=%s",
						player.getOpenInventory().getType(), player.getItemOnCursor().getType());
				if (Reward.isReward(player.getItemOnCursor())) {// player.getOpenInventory() instanceof PlayerInventory)
																// {
					plugin.getMessages().debug("BagOfGoldItems: dropped BagOfGold from the PlayerInventory");
					// when dropping directly from an inventory
					// plugin.getEconomyManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
					// plugin.getEconomyManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
					// plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, money);
					// removeBagOfGoldFromPlayer(player, money);
				} else {
					// when dropping from the quickbar using Q key
					plugin.getMessages().debug("BagOfGoldItems: dropped BagOfGold using Q key");
					plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, money);
				}
			}
		} else {
			plugin.getMessages().debug("This was not a reward");
			item.setCustomNameVisible(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRewardBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		ItemStack is = event.getItemInHand();
		Block block = event.getBlockPlaced();

		if (isFakeReward(is)) {
			player.sendMessage(ChatColor.RED + "[BagOfGold] WARNING, this was a FAKE reward with no value");
			return;
		}

		if (Reward.isReward(is)) {
			Reward reward = Reward.getReward(is);
			if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
				reward.setMoney(0);
				// plugin.getMessages().learn(event.getPlayer(),
				// plugin.getMessages().getString("mobhunting.learn.no-duplication"));
			}
			if (reward.getMoney() == 0)
				reward.setUniqueId(UUID.randomUUID());
			plugin.getMessages().debug("%s placed a reward block: %s", player.getName(),
					ChatColor.stripColor(reward.toString()));
			block.setMetadata(Reward.MH_REWARD_DATA, new FixedMetadataValue(plugin, reward));
			Core.getAPI().getRewardManager().getReward().put(reward.getUniqueUUID(), reward);
			Core.getAPI().getRewardManager().getLocations().put(reward.getUniqueUUID(), block.getLocation());
			plugin.getBagOfGoldItems().saveReward(reward.getUniqueUUID());
			if (reward.isBagOfGoldReward() || reward.isItemReward()) {
				plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, reward.getMoney());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRewardBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		CustomItems customItems = new CustomItems();

		Block block = event.getBlock();
		if (Reward.hasReward(block)) {
			plugin.getMessages().debug("A BagOfGold block was broken.");
			Reward reward = Reward.getReward(block);
			block.getDrops().clear();
			block.setType(Material.AIR);
			block.removeMetadata(Reward.MH_REWARD_DATA, plugin);
			ItemStack is;
			if (reward.isBagOfGoldReward()) {
				is = customItems.getCustomtexture(reward.getRewardType(), reward.getDisplayname(),
						plugin.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						plugin.getConfigManager().dropMoneyOnGroundSkullTextureSignature, reward.getMoney(),
						reward.getUniqueUUID(), reward.getSkinUUID());

				Item item = block.getWorld().dropItemNaturally(block.getLocation(), is);

				String displayName = plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
						? Tools.format(reward.getMoney())
						: (reward.getMoney() == 0 ? reward.getDisplayname()
								: reward.getDisplayname() + " (" + Tools.format(reward.getMoney()) + ")");
				item.setCustomName(
						ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + displayName);
				item.setCustomNameVisible(true);
				item.setMetadata(Reward.MH_REWARD_DATA,
						new FixedMetadataValue(plugin, new Reward(reward.getHiddenLore())));
				if (Core.getAPI().getRewardManager().getLocations().containsKey(reward.getUniqueUUID()))
					Core.getAPI().getRewardManager().getLocations().remove(reward.getUniqueUUID());
				if (Core.getAPI().getRewardManager().getReward().containsKey(reward.getUniqueUUID()))
					Core.getAPI().getRewardManager().getReward().remove(reward.getUniqueUUID());
			}

		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		// OBS: EntityPickupItemEvent does only exist in MC1.12 and newer

		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Player))
			return;

	}

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
				plugin.getMessages().debug("%s placed a BagOfGod in an ItemFrame", player.getName());
				plugin.getEconomyManager().removeMoneyFromPlayer(player, reward.getMoney());
				if (!plugin.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
									+ reward.getDisplayname() + plugin.getMessages().getString("bagofgold.moneydrop",
											"money", Tools.round(reward.getMoney())));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDespawnRewardEvent(ItemDespawnEvent event) {
		if (event.isCancelled())
			return;

		if (Reward.isReward(event.getEntity())) {
			if (Core.getAPI().getRewardManager().getDroppedMoney().containsKey(event.getEntity().getEntityId())) {
				Core.getAPI().getRewardManager().getDroppedMoney().remove(event.getEntity().getEntityId());
				if (event.getEntity().getLastDamageCause() != null)
					plugin.getMessages().debug("The reward was destroyed by %s",
							event.getEntity().getLastDamageCause().getCause());
				else
					plugin.getMessages().debug("The reward despawned (# of rewards left=%s)",
							Core.getAPI().getRewardManager().getDroppedMoney().size());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryPickupRewardEvent(InventoryPickupItemEvent event) {
		if (event.isCancelled())
			return;

		Item item = event.getItem();
		if (!item.hasMetadata(Reward.MH_REWARD_DATA))
			return;

		if (plugin.getConfigManager().denyHoppersToPickUpMoney
				&& event.getInventory().getType() == InventoryType.HOPPER) {
			// plugin.getMessages().debug("A %s tried to pick up the the reward,
			// but this is
			// disabled in config.yml",
			// event.getInventory().getType());
			event.setCancelled(true);
		} else {
			// plugin.getMessages().debug("The reward was picked up by %s",
			// event.getInventory().getType());
			if (Core.getAPI().getRewardManager().getDroppedMoney().containsKey(item.getEntityId()))
				Core.getAPI().getRewardManager().getDroppedMoney().remove(item.getEntityId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveOverRewardEvent(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		if (Core.getAPI().getRewardManager().canPickupMoney(player)) {
			Iterator<Entity> entityList = ((Entity) player).getNearbyEntities(1, 1, 1).iterator();
			while (entityList.hasNext() && Core.getAPI().getRewardManager().canPickupMoney(player)) {
				Entity entity = entityList.next();
				if (!(entity instanceof Item))
					continue;

				Item item = (Item) entity;

				if (isFakeReward(item)) {
					player.sendMessage(ChatColor.RED + "[BagOfGold] WARNING, this was a FAKE reward with no value");
					return;
				}

				if (Reward.isReward(item)) {
					if (Core.getAPI().getRewardManager().getDroppedMoney().containsKey(entity.getEntityId())) {
						Core.getAPI().getRewardManager().getDroppedMoney().remove(entity.getEntityId());
						Reward reward = Reward.getReward(item);
						if (reward.isBagOfGoldReward() || reward.isItemReward()) {
							double addedMoney = Core.getAPI().getRewardManager().addBagOfGoldMoneyToPlayer(player,
									reward.getMoney());
							if (addedMoney > 0) {
								PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
								ps.setBalance(Tools.round(ps.getBalance() + addedMoney));
								plugin.getPlayerBalanceManager().setPlayerBalance(player, ps);
							}
							item.remove();
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHitRewardEvent(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		Entity targetEntity = null;
		Iterator<Entity> nearby = projectile.getNearbyEntities(1, 1, 1).iterator();
		while (nearby.hasNext()) {
			targetEntity = nearby.next();

			if (Reward.isReward(targetEntity)) {
				if (Core.getAPI().getRewardManager().getDroppedMoney().containsKey(targetEntity.getEntityId()))
					Core.getAPI().getRewardManager().getDroppedMoney().remove(targetEntity.getEntityId());
				targetEntity.remove();
				plugin.getMessages().debug("The reward was hit by %s and removed. (# of rewards left=%s)",
						projectile.getType(), Core.getAPI().getRewardManager().getDroppedMoney().size());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		Inventory inventory = event.getInventory();
		if (inventory.getType() == InventoryType.CRAFTING) {
			ItemStack helmet = player.getEquipment().getHelmet();

			if (isFakeReward(helmet)) {
				player.sendMessage(
						ChatColor.RED + "[BagOfGold] WARNING, you can't wear a reward on your head. It was removed.");
				event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
				return;
			}

			if (Reward.isReward(helmet)) {
				Reward reward = Reward.getReward(helmet);
				if (reward.isBagOfGoldReward()) {
					// plugin.getMessages().learn(player,
					// plugin.getMessages().getString("mobhunting.learn.rewards.no-helmet"));
					event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
					if (Tools.round(reward.getMoney()) != Tools.round(Core.getInstance().getBagOfGoldItems()
							.addBagOfGoldMoneyToPlayer(player, reward.getMoney())))
						Core.getAPI().getRewardManager().dropBagOfGoldMoneyOnGround(player, null, player.getLocation(),
								reward.getMoney());
				}
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

		if (Reward.hasReward(block)) {
			Reward reward = Reward.getReward(block);
			if (!plugin.getPlayerSettingsManager().getPlayerSettings(player).isMuted()) {
				if (reward.getMoney() == 0)
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
									+ reward.getDisplayname());
				else
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
									+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
											? Tools.format(reward.getMoney())
											: reward.getDisplayname() + " (" + Tools.format(reward.getMoney()) + ")"));
			}
		} else if (Servers.isMC113OrNewer()
				&& (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD)) {
			Skull skullState = (Skull) block.getState();
			OfflinePlayer owner = skullState.getOwningPlayer();
			if (owner != null && owner.getName() != null
					&& !plugin.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
				plugin.getMessages().playerActionBarMessageQueue(player,
						ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + owner.getName());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClickReward(InventoryClickEvent event) {
		if (event.isCancelled() || event.getInventory() == null) {
			plugin.getMessages().debug("RewardListeners: Something cancelled the InventoryClickEvent");
			return;
		}

		if (CitizensCompat.isNPC(event.getWhoClicked()))
			return;

		Player player = (Player) event.getWhoClicked();

		ItemStack isCurrentSlot = event.getCurrentItem();
		ItemStack isCursor = event.getCursor();
		ItemStack isKey = event.getHotbarButton() != -1 ? player.getInventory().getItem(event.getHotbarButton()) : null;

		if ((event.getAction() == InventoryAction.HOTBAR_SWAP
				|| event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) && event.getClick().isKeyboardClick()) {
			plugin.getMessages().debug("Keyboard click reward=%s",
					Reward.isReward(player.getInventory().getItem(event.getHotbarButton())));
			if (player.getGameMode() != GameMode.SURVIVAL)
				event.setCancelled(true);
			return;
		}

		if (!(Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) || Reward.isReward(isKey)) {
			if (isFakeReward(isCurrentSlot)) {
				player.sendMessage(ChatColor.RED + "[BagOfGold] WARNING, this is a FAKE reward. It was removed.");
				isCurrentSlot.setType(Material.AIR);
				return;
			}
			if (isFakeReward(isCursor)) {
				player.sendMessage(ChatColor.RED + "[BagOfGold] WARNING, this is a FAKE reward. It was removed.");
				isCursor.setType(Material.AIR);
				return;
			}
			if (isFakeReward(isKey)) {
				player.sendMessage(ChatColor.RED + "[BagOfGold] WARNING, this is a FAKE reward. It was removed.");
				isKey.setType(Material.AIR);
				return;
			}
			plugin.getMessages().debug("This is not a BagOfGold reward. key=%s isKey=%s", event.getHotbarButton(),
					isKey == null ? "null" : isKey.getType());
			return;
		}

		InventoryAction action = event.getAction();
		SlotType slotType = event.getSlotType();

		Inventory inventory = event.getInventory();
		Inventory clickedInventory = event.getClickedInventory();

		if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor) || Reward.isReward(isKey)) {
			plugin.getMessages().debug(
					"action=%s, InventoryType=%s, slottype=%s, slotno=%s, current=%s, cursor=%s, view=%s, clickedInv=%s, key=%s",
					action, inventory.getType(), slotType, event.getSlot(),
					isCurrentSlot == null ? "null" : isCurrentSlot.getType(),
					isCursor == null ? "null" : isCursor.getType(), event.getView().getType(),
					clickedInventory == null ? "null" : event.getClickedInventory().getType(),
					isKey == null ? "null" : isKey.getType());
		} else {
			plugin.getMessages().debug("No BagOfGold reward");
		}

		if (action == InventoryAction.NOTHING) {
			plugin.getMessages().debug("%s did NOTHING in the inventory", player.getName());
			return;
		}

		if (slotType == SlotType.OUTSIDE && Reward.isReward(isCursor)) {
			Reward reward = Reward.getReward(isCursor);
			plugin.getMessages().debug("RewardListerner: %s dropped %s BagOfGold outside the inventory",
					player.getName(), reward.getMoney());
			// if (player.getGameMode() == GameMode.CREATIVE)
			// dropBagOfGoldMoneyOnGround(player, null, player.getLocation(),
			// reward.getMoney());
			// plugin.getEconomyManager().removeMoneyFromPlayerBalance(player,
			// reward.getMoney());
			plugin.getEconomyManager().addMoneyToPlayerBalance(player, reward.getMoney());
			return;
		}

		else if (!(slotType == SlotType.CONTAINER || slotType == SlotType.QUICKBAR || slotType == SlotType.RESULT
				|| (slotType == SlotType.ARMOR && event.getSlot() == 39))) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				// Reward reward = Reward.isReward(isCurrentSlot) ?
				// Reward.getReward(isCurrentSlot)
				// : Reward.getReward(isCursor);
				// plugin.getMessages().learn(player,
				// plugin.getMessages().getString("mobhunting.learn.rewards.no-use",
				// "rewardname", reward.getDisplayname()));
				plugin.getMessages().debug("RewardListerner: %s its not allowed to use BagOfGold here",
						player.getName());
				event.setCancelled(true);
				return;
			}
		}

		if (action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				// Reward reward = Reward.isReward(isCurrentSlot) ?
				// Reward.getReward(isCurrentSlot)
				// : Reward.getReward(isCursor);
				// plugin.getMessages().learn(player,
				// plugin.getMessages().getString("mobhunting.learn.rewards.no-clone",
				// "rewardname", reward.getDisplayname()));
				plugin.getMessages().debug("RewardListerner: %s its not allowed to clone BagOfGold", player.getName());
				event.setCancelled(true);
				return;
			}
		}

		if (action == InventoryAction.PLACE_ALL && Reward.isReward(isCurrentSlot)
				&& isCursor.getType() == Material.AIR) {
			if (event.getView().getTitle().equalsIgnoreCase("Inventory")) {

				Reward reward = Reward.getReward(isCurrentSlot);
				plugin.getMessages().debug("(2) %s moved BagOfGold (%s) out of Inventory", player.getName(),
						reward.getMoney());
				plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, reward.getMoney());
			}
		} else if ((action == InventoryAction.PICKUP_ALL || action == InventoryAction.PICKUP_ONE
				|| action == InventoryAction.PICKUP_SOME) && Reward.isReward(isCurrentSlot)) {
			if (event.getView().getTitle().equalsIgnoreCase("Inventory")) {
				Reward reward = Reward.getReward(isCurrentSlot);
				plugin.getMessages().debug("%s moved BagOfGold (%s) out of Inventory", player.getName(),
						reward.getMoney());
				plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, reward.getMoney());
			}
		} else if ((action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE
				|| action == InventoryAction.PLACE_SOME) && Reward.isReward(isCursor)) {
			if (event.getView().getTitle().equalsIgnoreCase("Inventory")) {
				Reward reward = Reward.getReward(isCursor);
				plugin.getMessages().debug("%s moved BagOfGold (%s) into Inventory", player.getName(),
						reward.getMoney());
				plugin.getEconomyManager().addMoneyToPlayerBalance(player, reward.getMoney());
			}
		}

		else if (action == InventoryAction.SWAP_WITH_CURSOR) {
			if (Reward.isReward(isCurrentSlot) && Reward.isReward(isCursor)) {
				event.setCancelled(true);
				ItemMeta imCurrent = isCurrentSlot.getItemMeta();
				ItemMeta imCursor = isCursor.getItemMeta();
				Reward reward1 = new Reward(imCurrent.getLore());
				Reward reward2 = new Reward(imCursor.getLore());
				if ((reward1.isBagOfGoldReward() || reward1.isItemReward())
						&& reward1.getRewardType().equals(reward2.getRewardType())) {
					if (reward1.getMoney() + reward2.getMoney() <= plugin.getConfigManager().limitPerBag) {
						double added_money = reward2.getMoney();
						reward2.setMoney(reward1.getMoney() + reward2.getMoney());
						imCursor.setLore(reward2.getHiddenLore());
						imCursor.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
										? Tools.format(reward2.getMoney())
										: reward2.getDisplayname() + " (" + Tools.format(reward2.getMoney()) + ")"));
						isCursor.setItemMeta(imCursor);
						isCurrentSlot.setAmount(0);
						isCurrentSlot.setType(Material.AIR);
						event.setCurrentItem(isCursor);
						event.setCursor(isCurrentSlot);
						plugin.getMessages().debug("%s merged two rewards(1)", player.getName());
						plugin.getEconomyManager().addMoneyToPlayerBalance(player, added_money);
					} else {
						double rest = reward1.getMoney() + reward2.getMoney() - plugin.getConfigManager().limitPerBag;
						double added_money = plugin.getConfigManager().limitPerBag - reward1.getMoney();
						reward2.setMoney(plugin.getConfigManager().limitPerBag);
						imCursor.setLore(reward2.getHiddenLore());
						imCursor.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
										? Tools.format(plugin.getConfigManager().limitPerBag)
										: reward2.getDisplayname() + " ("
												+ Tools.format(plugin.getConfigManager().limitPerBag) + ")"));
						isCursor.setItemMeta(imCursor);

						reward1.setMoney(rest);
						imCurrent.setLore(reward1.getHiddenLore());
						imCurrent.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
										? Tools.format(plugin.getConfigManager().limitPerBag)
										: reward1.getDisplayname() + " (" + Tools.format(reward1.getMoney()) + ")"));
						isCurrentSlot.setItemMeta(imCurrent);
						event.setCurrentItem(isCursor);
						event.setCursor(isCurrentSlot);
						plugin.getMessages().debug("%s merged two rewards(2)", player.getName());
						if (event.getView().getTitle().equalsIgnoreCase("Inventory")) {
							plugin.getEconomyManager().addMoneyToPlayerBalance(player, added_money);
						}
					}
				} else {
					if (clickedInventory.getType() == InventoryType.PLAYER) {
						double playerInv = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot).getMoney()
								: 0;
						double chestInv = Reward.isReward(isCursor) ? Reward.getReward(isCursor).getMoney() : 0;
						plugin.getMessages().debug("slot=%s cursor=%s", playerInv, chestInv);
						plugin.getEconomyManager().removeMoneyFromPlayer(player, playerInv - chestInv);
					} else {
						double playerInv = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot).getMoney()
								: 0;
						double chestInv = Reward.isReward(isCursor) ? Reward.getReward(isCursor).getMoney() : 0;
						plugin.getMessages().debug("slot=%s cursor=%s", playerInv, chestInv);
						plugin.getEconomyManager().addMoneyToPlayer(player, playerInv - chestInv);
					}
				}
			} else {

			}

		} else if (action == InventoryAction.PICKUP_HALF) {
			if (isCursor.getType() == Material.AIR && Reward.isReward(isCurrentSlot)) {
				Reward reward = Reward.getReward(isCurrentSlot);
				if (reward.isBagOfGoldReward() || reward.isItemReward()) {
					double currentSlotMoney = Tools.round(reward.getMoney() / 2);
					double cursorMoney = Tools.round(reward.getMoney()) - currentSlotMoney;
					if (cursorMoney >= plugin.getConfigManager().minimumReward) {
						event.setCancelled(true);
						reward.setMoney(currentSlotMoney);
						isCurrentSlot = Core.getInstance().getBagOfGoldItems()
								.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
						event.setCurrentItem(isCurrentSlot);
						reward.setMoney(cursorMoney);
						isCursor = Core.getInstance().getBagOfGoldItems()
								.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
						event.setCursor(isCursor);
						plugin.getMessages().debug("%s halfed a reward in two (%s,%s)", player.getName(),
								Tools.format(currentSlotMoney), Tools.format(cursorMoney));
						if (event.getView().getTitle().equalsIgnoreCase("Inventory")) {
							plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, cursorMoney);
						}
					}
				} else if (reward.isKilledHeadReward() || reward.isKilledHeadReward()) {

				}
			}
		} else if (action == InventoryAction.COLLECT_TO_CURSOR) {
			if (Reward.isReward(isCursor)) {
				Reward cursor = Reward.getReward(isCursor);
				double money_in_hand = cursor.getMoney();
				if (cursor.isBagOfGoldReward() || cursor.isItemReward()) {
					double saldo = Tools.floor(cursor.getMoney());
					for (int slot = 0; slot < event.getClickedInventory().getSize(); slot++) {
						ItemStack is = event.getClickedInventory().getItem(slot);
						if (Reward.isReward(is)) {
							Reward reward = Reward.getReward(is);
							if ((reward.isBagOfGoldReward() || reward.isItemReward()) && reward.getMoney() > 0) {
								saldo = saldo + reward.getMoney();
								if (saldo <= plugin.getConfigManager().limitPerBag)
									event.getClickedInventory().clear(slot);
								else {
									reward.setMoney(plugin.getConfigManager().limitPerBag);
									is = Core.getAPI().getRewardManager().setDisplayNameAndHiddenLores(is.clone(),
											reward);
									is.setAmount(1);
									// event.setCurrentItem(is);
									event.getClickedInventory().clear(slot);
									event.getClickedInventory().addItem(is);
									saldo = saldo - plugin.getConfigManager().limitPerBag;
								}
							}
						}
					}
					cursor.setMoney(saldo);
					isCursor = Core.getAPI().getRewardManager().setDisplayNameAndHiddenLores(isCursor.clone(), cursor);
					event.setCursor(isCursor);
					plugin.getMessages().debug("%s collected %s to the cursor", player.getName(), saldo);
					if (event.getView().getTitle().equalsIgnoreCase("Inventory")) {
						plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, saldo - money_in_hand);
					}
				}
			}
		}

		else if (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor) || Reward.isReward(isKey)) {
				plugin.getMessages().debug("%s tried to do a HOTBAR_SWAP/HOTBAR_MOVE_AND_READD with a BagOfGold.",
						player.getName());

				// event.setCancelled(true);
				// if (player.getGameMode() != GameMode.SURVIVAL) {
				if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
					double playerInv = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot).getMoney() : 0;
					double chestInv = Reward.isReward(isCursor) ? Reward.getReward(isCursor).getMoney() : 0;
					double keyMoney = Reward.isReward(isKey) ? Reward.getReward(isKey).getMoney() : 0;
					plugin.getMessages().debug("slot=%s cursor=%s, key=%s", playerInv, chestInv, keyMoney);
					plugin.getEconomyManager().removeMoneyFromPlayer(player, playerInv - chestInv);
				} else {
					double playerInv = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot).getMoney() : 0;
					double chestInv = Reward.isReward(isCursor) ? Reward.getReward(isCursor).getMoney() : 0;
					double keyMoney = Reward.isReward(isKey) ? Reward.getReward(isKey).getMoney() : 0;
					plugin.getMessages().debug("slot=%s cursor=%s, key=%s", playerInv, chestInv, keyMoney);
					plugin.getEconomyManager().addMoneyToPlayer(player, playerInv - chestInv);
				}
				// } else {
				// event.setCancelled(true);
				// }
			}

			return;
		}

		else if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY)

		{
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				Reward reward = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot)
						: Reward.getReward(isCursor);
				if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
					plugin.getMessages().debug("%s Moved %s BagOfGold out of the Player Inventory", player.getName(),
							reward.getMoney());
					plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, reward.getMoney());
				} else {
					plugin.getMessages().debug("%s Moved %s BagOfGold into the Player Inventory", player.getName(),
							reward.getMoney());
					plugin.getEconomyManager().addMoneyToPlayerBalance(player, reward.getMoney());

				}
			}
		}

		else if (action == InventoryAction.DROP_ALL_CURSOR) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				plugin.getMessages().debug("%s tried to do a DROP_ALL_CURSOR with a BagOfGold.", player.getName());
			}
		}

		else if (action == InventoryAction.DROP_ALL_SLOT) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				plugin.getMessages().debug("%s tried to do a DROP_ALL_SLOT with a BagOfGold.", player.getName());
			}
		}

		else if (action == InventoryAction.DROP_ONE_CURSOR) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				plugin.getMessages().debug("%s tried to do a DROP_ONE_CURSOR with a BagOfGold.", player.getName());
			}
		}

		else if (action == InventoryAction.DROP_ONE_SLOT) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				plugin.getMessages().debug("%s tried to do a DROP_ONE_SLOT with a BagOfGold.", player.getName());
			}
		} else {
			plugin.getMessages().debug("BagOfGoldItems: action=%s", action);
			if (player.getGameMode() == GameMode.SURVIVAL)
				plugin.getEconomyManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
			else
				plugin.getEconomyManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
		}

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
		// plugin.getMessages().debug("BagOfGoldItems: onInventoryMoveItemEvent
		// called");
		// plugin.getMessages().debug("BagOfGoldItems: Moved Item=%s",
		// event.getItem().getType());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryInteractEvent(InventoryInteractEvent event) {
		// plugin.getMessages().debug("BagOfGoldItems: onInventoryInteractEvent
		// called");
		// plugin.getMessages().debug("BagOfGoldItems: %s clicked an inventory %s",
		// event.getWhoClicked().getName(),
		// event.getInventory().getType());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryDragEvent(InventoryDragEvent event) {
		// plugin.getMessages().debug("BagOfGoldItems: onInventoryDragEvent called");
		// plugin.getMessages().debug("BagOfGoldItems: %s draged an %s in inventory %s",
		// event.getWhoClicked().getName() == null ? "null" :
		// event.getWhoClicked().getName(),
		// event.getCursor() == null ? "null" : event.getCursor().getType(),
		// event.getInventory() == null ? "null" : event.getInventory().getType());
	}
}
