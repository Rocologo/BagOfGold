package one.lindegaard.BagOfGold.rewards;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import org.bukkit.event.block.BlockPlaceEvent;
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
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.BagOfGold.util.Misc;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.Tools;
import one.lindegaard.Core.rewards.CoreCustomItems;
import one.lindegaard.Core.rewards.Reward;
import one.lindegaard.Core.rewards.RewardBlock;
import one.lindegaard.Core.rewards.RewardType;
import one.lindegaard.Core.server.Servers;

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

	public String format(double money) {
		return Tools.format(money);
	}

	public double addBagOfGoldMoneyToPlayer(Player player, double amount) {

		boolean found = false;
		double moneyLeftToGive = amount;
		double addedMoney = 0;

		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);

				if (rewardInSlot.isMoney()) {

					// plugin.getMessages().debug("BagOfGoldItems: addBagOfGoldMoneyToPlayer, there
					// is money in slot=%s",
					// slot);

					if (rewardInSlot.checkHash()) {
						if (rewardInSlot.getMoney() < Core.getConfigManager().limitPerBag) {
							double space = Core.getConfigManager().limitPerBag - rewardInSlot.getMoney();
							if (space > moneyLeftToGive) {
								addedMoney = addedMoney + moneyLeftToGive;
								rewardInSlot.setMoney(rewardInSlot.getMoney() + moneyLeftToGive);
								moneyLeftToGive = 0;
							} else {
								addedMoney = addedMoney + space;
								rewardInSlot.setMoney(Core.getConfigManager().limitPerBag);
								moneyLeftToGive = moneyLeftToGive - space;
							}
							if (rewardInSlot.getMoney() == 0)
								player.getInventory().clear(slot);
							else {
								// plugin.getMessages().debug(
								// "BagOfGoldItems: addBagOfGoldMoneyToPlayer change lores and displayname");
								is = Reward.setDisplayNameAndHiddenLores(is, rewardInSlot);
							}
							plugin.getMessages().debug(
									"Added %s to %s's item in slot %s, new value is %s (addBagOfGoldPlayer_EconomyManager)",
									format(amount), player.getName(), slot, format(rewardInSlot.getMoney()));
							if (moneyLeftToGive <= 0) {
								found = true;
								break;
							}
						}
					} else {
						// Hash is wrong
						Bukkit.getConsoleSender()
								.sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] "
										+ player.getName()
										+ " has tried to change the value of a BagOfGold Item. Value set to 0!(1)");
						rewardInSlot.setMoney(0);
						is = Reward.setDisplayNameAndHiddenLores(is, rewardInSlot);
					}
				}
			}
		}
		if (!found) {
			while (Misc.round(moneyLeftToGive) > 0 && canPickupMoney(player)) {
				double nextBag = 0;
				if (moneyLeftToGive > Core.getConfigManager().limitPerBag) {
					nextBag = Core.getConfigManager().limitPerBag;
					moneyLeftToGive = moneyLeftToGive - nextBag;
				} else {
					nextBag = moneyLeftToGive;
					moneyLeftToGive = 0;
				}
				if (player.getInventory().firstEmpty() == -1)
					dropBagOfGoldMoneyOnGround(player, null, player.getLocation(), Misc.round(nextBag));
				else {
					addedMoney = addedMoney + nextBag;
					ItemStack is;
					if (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("SKULL"))
						is = new CoreCustomItems().getCustomtexture(
								new Reward(Core.getConfigManager().bagOfGoldName,
										Misc.round(nextBag), RewardType.BAGOFGOLD,
										UUID.fromString(RewardType.BAGOFGOLD.getUUID())),
								Core.getConfigManager().skullTextureValue,
								Core.getConfigManager().skullTextureSignature);
					else {
						is = new ItemStack(Material.valueOf(Core.getConfigManager().rewardItem), 1);
						is = Reward.setDisplayNameAndHiddenLores(is,
								new Reward(Core.getConfigManager().bagOfGoldName,
										Misc.round(nextBag), RewardType.ITEM, null));
					}
					player.getInventory().addItem(is);
				}
			}
		}
		if (moneyLeftToGive > 0)
			dropBagOfGoldMoneyOnGround(player, null, player.getLocation(), moneyLeftToGive);
		return addedMoney;
	}

	public double removeBagOfGoldFromPlayer(Player player, double amount) {
		double taken = 0;
		double toBeTaken = Misc.round(amount);
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward reward = Reward.getReward(is);
				if (reward.checkHash()) {
					if (reward.isMoney()) {
						double saldo = Misc.round(reward.getMoney());
						if (saldo > toBeTaken) {
							reward.setMoney(Misc.round(saldo - toBeTaken));
							is = Reward.setDisplayNameAndHiddenLores(is, reward);
							player.getInventory().setItem(slot, is);
							taken = taken + toBeTaken;
							toBeTaken = 0;
							return Misc.round(taken);
						} else {
							player.getInventory().clear(slot);
							taken = taken + saldo;
							toBeTaken = toBeTaken - saldo;
						}
						if (reward.getMoney() == 0)
							player.getInventory().clear(slot);
					}
				} else {
					// Hash is wrong
					Bukkit.getConsoleSender().sendMessage(
							ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] " + player.getName()
									+ " has tried to change the value of a BagOfGold Item. Value set to 0!(2)");
					reward.setMoney(0);
					is = Reward.setDisplayNameAndHiddenLores(is, reward);
				}
			}

		}
		return taken;
	}

	public void dropBagOfGoldMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		Item item = null;
		double moneyLeftToDrop = Misc.ceil(money);
		ItemStack is;
		// UUID uuid = null, skinuuid = null;
		UUID skinuuid = null;
		RewardType rewardType;
		double nextBag = 0;
		while (moneyLeftToDrop > 0) {
			if (moneyLeftToDrop > Core.getConfigManager().limitPerBag) {
				nextBag = Core.getConfigManager().limitPerBag;
				moneyLeftToDrop = Misc.round(moneyLeftToDrop - nextBag);
			} else {
				nextBag = Misc.round(moneyLeftToDrop);
				moneyLeftToDrop = 0;
			}

			if (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("SKULL")) {
				rewardType = RewardType.BAGOFGOLD;
				skinuuid = UUID.fromString(RewardType.BAGOFGOLD.getUUID());
				is = new CoreCustomItems().getCustomtexture(
						new Reward(Core.getConfigManager().bagOfGoldName, nextBag,
								rewardType, skinuuid),
						Core.getConfigManager().skullTextureValue,
						Core.getConfigManager().skullTextureSignature);
			} else { // ITEM
				rewardType = RewardType.ITEM;
				skinuuid = null;
				is = new ItemStack(Material.valueOf(Core.getConfigManager().rewardItem), 1);
			}

			Reward reward = new Reward(
					ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
							+ Core.getConfigManager().bagOfGoldName,
					nextBag, rewardType, skinuuid);
			is = Reward.setDisplayNameAndHiddenLores(is, reward);

			item = location.getWorld().dropItemNaturally(location, is);

			if (item != null) {
				plugin.getRewardManager().getDroppedMoney().put(item.getEntityId(), nextBag);
				item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, new Reward(reward)));
				item.setCustomName(is.getItemMeta().getDisplayName());
				item.setCustomNameVisible(true);
				if (player != null)
					plugin.getMessages().debug("%s dropped %s on the ground as item %s (# of rewards=%s)(3)",
							player.getName(), format(nextBag), Core.getConfigManager().rewardItemtype,
							plugin.getRewardManager().getDroppedMoney().size());
				else
					plugin.getMessages().debug("A %s(%s) was dropped on the ground as item %s (# of rewards=%s)(3)",
							Core.getConfigManager().rewardItemtype, format(nextBag),
							Core.getConfigManager().rewardItemtype,
							plugin.getRewardManager().getDroppedMoney().size());

			}
		}
	}

	public double getAmountOfBagOfGoldMoneyInInventory(Player player) {

		double amountInInventory = 0;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);

			if (Reward.isReward(is)) {
				Reward reward = Reward.getReward(is);
				if (reward.checkHash()) {
					if (reward.isMoney())
						amountInInventory = amountInInventory + reward.getMoney();
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

	public boolean canPickupMoney(Player player) {
		if (player.getGameMode() == GameMode.SPECTATOR)
			return false;
		else if (player.getInventory().firstEmpty() != -1)
			return true;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				if (rewardInSlot.isMoney()) {
					if (rewardInSlot.getMoney() < Core.getConfigManager().limitPerBag)
						return true;
				}
			}
		}
		return false;
	}

	public double getSpaceForBagOfGoldMoney(Player player) {
		double space = 0;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				if (rewardInSlot.checkHash()) {
					if (rewardInSlot.isMoney())
						space = space + Core.getConfigManager().limitPerBag - rewardInSlot.getMoney();

				} else {
					Bukkit.getConsoleSender().sendMessage(
							ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] " + player.getName()
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

	private boolean isFakeReward(Item item) {
		ItemStack itemStack = item.getItemStack();
		return isFakeReward(itemStack);
	}

	private boolean isFakeReward(ItemStack itemStack) {
		// if (!Materials.isPlayerHead(itemStack.getType()))
		// return false;

		if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()
				&& itemStack.getItemMeta().getDisplayName()
						.contains(Core.getConfigManager().bagOfGoldName)) {
			if (!itemStack.getItemMeta().hasLore()) {
				return true;
			}
		}
		return false;
	}

	// ***********************************************************************************
	// EVENTS
	// ***********************************************************************************

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerDropReward(PlayerDropItemEvent event) {
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
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] "
						+ player.getName() + " has tried to change the value of a BagOfGold Item. Value set to 0!(5)");
				reward.setMoney(0);
				ItemStack is = Reward.setDisplayNameAndHiddenLores(item.getItemStack(), reward);
				item.setItemStack(is);
			}
			if (reward.isMoney()) {
				double money = reward.getMoney();
				plugin.getRewardManager().getDroppedMoney().put(item.getEntityId(), money);
				if (money == 0) {
					plugin.getMessages().debug("%s dropped a %s (# of rewards left=%s)(1)", player.getName(),
							reward.getDisplayName() != null ? reward.getDisplayName()
									: Core.getConfigManager().bagOfGoldName,
							plugin.getRewardManager().getDroppedMoney().size());
				} else {
					plugin.getMessages().debug("%s dropped %s %s. (# of rewards left=%s)(2)", player.getName(),
							format(money), reward.getDisplayName(), plugin.getRewardManager().getDroppedMoney().size());
					if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
						plugin.getMessages().playerActionBarMessageQueue(player,
								plugin.getMessages().getString("bagofgold.moneydrop", "money", format(money),
										"rewardname",
										ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
												+ reward.getDisplayName()));
					if (Reward.isReward(player.getItemOnCursor())) {
						plugin.getMessages().debug("%s dropped %s %s from the PlayerInventory", player.getName(), money,
								reward.getDisplayName());
					} else {
						// when dropping from the quickbar using Q key
						plugin.getMessages().debug("%s dropped %s %s using Q key", player.getName(), money,
								reward.getDisplayName());
						plugin.getRewardManager().removeMoneyFromPlayerBalance(player, money);
					}
				}
				item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, reward));
				ItemStack is = Reward.setDisplayNameAndHiddenLores(item.getItemStack(), reward);
				item.setItemStack(is);
				item.setCustomName(is.getItemMeta().getDisplayName());
				item.setCustomNameVisible(true);
			}
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
			if (reward.checkHash()) {
				if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
					// Duplication not allowed
					reward.setMoney(0);
				}
				plugin.getMessages().debug("%s placed a reward block: %s", player.getName(),
						ChatColor.stripColor(reward.toString()));
				reward.setUniqueID(plugin.getRewardManager().getNextID());
				block.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, reward));
				plugin.getRewardManager().getRewardBlocks().put(reward.getUniqueID(),
						new RewardBlock(block.getLocation(), reward));
				if (reward.isMoney()) {
					plugin.getRewardManager().removeMoneyFromPlayerBalance(player, reward.getMoney());
				}
			} else {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] "
						+ player.getName() + " has tried to change the value of a BagOfGold Item. Value set to 0!(6)");
				reward.setMoney(0);
				is = Reward.setDisplayNameAndHiddenLores(is, reward);
			}
		}
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
				plugin.getRewardManager().removeMoneyFromPlayer(player, reward.getMoney());
				if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
									+ reward.getDisplayName() + plugin.getMessages().getString("bagofgold.moneydrop",
											"money", Misc.round(reward.getMoney())));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDespawnRewardEvent(ItemDespawnEvent event) {
		if (event.isCancelled())
			return;

		if (Reward.isReward(event.getEntity())) {
			if (plugin.getRewardManager().getDroppedMoney().containsKey(event.getEntity().getEntityId())) {
				plugin.getRewardManager().getDroppedMoney().remove(event.getEntity().getEntityId());
				if (event.getEntity().getLastDamageCause() != null)
					plugin.getMessages().debug("The reward was destroyed by %s",
							event.getEntity().getLastDamageCause().getCause());
				else
					plugin.getMessages().debug("The reward despawned (# of rewards left=%s)",
							plugin.getRewardManager().getDroppedMoney().size());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryPickupRewardEvent(InventoryPickupItemEvent event) {
		if (event.isCancelled())
			return;

		Item item = event.getItem();
		if (!item.hasMetadata(Reward.MH_REWARD_DATA_NEW))
			return;

		if (Core.getConfigManager().denyHoppersToPickUpRewards
				&& event.getInventory().getType() == InventoryType.HOPPER) {
			// plugin.getMessages().debug("A %s tried to pick up the the reward,
			// but this is
			// disabled in config.yml",
			// event.getInventory().getType());
			event.setCancelled(true);
		} else {
			// plugin.getMessages().debug("The reward was picked up by %s",
			// event.getInventory().getType());
			if (plugin.getRewardManager().getDroppedMoney().containsKey(item.getEntityId()))
				plugin.getRewardManager().getDroppedMoney().remove(item.getEntityId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveOverRewardEvent(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		if (player.getInventory().firstEmpty() != -1)
			return;

		Iterator<Entity> entityList = ((Entity) player).getNearbyEntities(1, 1, 1).iterator();
		while (entityList.hasNext()) {
			Entity entity = entityList.next();
			if (!(entity instanceof Item))
				continue;

			Item item = (Item) entity;

			if (isFakeReward(item)) {
				player.sendMessage(ChatColor.RED + "[BagOfGold] WARNING, this was a FAKE reward and it was removed");
				item.remove();
				return;
			}

			if (Reward.isReward(item) && canPickupMoney(player)) {
				if (plugin.getRewardManager().getDroppedMoney().containsKey(entity.getEntityId())) {
					plugin.getRewardManager().getDroppedMoney().remove(entity.getEntityId());
					Reward reward = Reward.getReward(item);
					if (reward.checkHash()) {
						if (reward.isMoney()) {
							double addedMoney = addBagOfGoldMoneyToPlayer(player, reward.getMoney());
							if (addedMoney > 0) {
								PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
								ps.setBalance(Misc.round(ps.getBalance() + addedMoney));
								plugin.getPlayerBalanceManager().setPlayerBalance(player, ps);
							}

						}
					} else {
						Bukkit.getConsoleSender()
								.sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] "
										+ player.getName()
										+ " has tried to change the value of a BagOfGold Item. Value set to 0!(7)");
						reward.setMoney(0);
						ItemStack is = Reward.setDisplayNameAndHiddenLores(item.getItemStack(), reward);
						item.setItemStack(is);
					}
					item.remove();
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
				if (plugin.getRewardManager().getDroppedMoney().containsKey(targetEntity.getEntityId()))
					plugin.getRewardManager().getDroppedMoney().remove(targetEntity.getEntityId());
				targetEntity.remove();
				plugin.getMessages().debug("The reward was hit by %s and removed. (# of rewards left=%s)",
						projectile.getType(), plugin.getRewardManager().getDroppedMoney().size());
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
				event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
				return;
			}

			if (Reward.isReward(helmet)) {
				Reward reward = Reward.getReward(helmet);
				if (reward.checkHash()) {
					if (reward.isBagOfGoldReward()) {
						plugin.getMessages().playerActionBarMessageQueue(player,
								plugin.getMessages().getString("bagofgold.learn.rewards.no-helmet"));
						event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
						if (Misc.round(reward.getMoney()) != Misc
								.round(addBagOfGoldMoneyToPlayer(player, reward.getMoney())))
							dropBagOfGoldMoneyOnGround(player, null, player.getLocation(), reward.getMoney());
					} else {
						event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
						player.getWorld().dropItem(player.getLocation(), helmet);
					}
				} else {
					Bukkit.getConsoleSender().sendMessage(
							ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] " + player.getName()
									+ " has tried to change the value of a BagOfGold Item. Value set to 0!(8)");
					reward.setMoney(0);
					helmet = Reward.setDisplayNameAndHiddenLores(helmet, reward);
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

		if (Reward.isReward(block)) {
			Reward reward = Reward.getReward(block);
			if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted()) {
				if (reward.getMoney() == 0)
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
									+ reward.getDisplayName());
				else
					plugin.getMessages().playerActionBarMessageQueue(player,
							ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
									+ (Core.getConfigManager().rewardItemtype.equalsIgnoreCase("ITEM")
											? format(reward.getMoney())
											: reward.getDisplayName() + " (" + format(reward.getMoney()) + ")"));
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

	private boolean isInventoryAllowed(Inventory inv) {
		List<InventoryType> allowedInventories;
		if (Servers.isMC114OrNewer())
			allowedInventories = Arrays.asList(InventoryType.PLAYER, InventoryType.BARREL, InventoryType.ANVIL,
					InventoryType.CHEST, InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.ENDER_CHEST,
					InventoryType.HOPPER, InventoryType.SHULKER_BOX, InventoryType.CRAFTING);
		else if (Servers.isMC19OrNewer())
			allowedInventories = Arrays.asList(InventoryType.PLAYER, InventoryType.ANVIL, InventoryType.CHEST,
					InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.ENDER_CHEST, InventoryType.HOPPER,
					InventoryType.SHULKER_BOX, InventoryType.CRAFTING);
		else // MC 1.8
			allowedInventories = Arrays.asList(InventoryType.PLAYER, InventoryType.ANVIL, InventoryType.CHEST,
					InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.ENDER_CHEST, InventoryType.HOPPER,
					InventoryType.CRAFTING);
		if (inv != null)
			return allowedInventories.contains(inv.getType());
		else
			return true;
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClickReward(InventoryClickEvent event) {

		if (event.isCancelled() || event.getInventory() == null)
			return;

		InventoryAction action = event.getAction();
		if (action == InventoryAction.NOTHING)
			return;

		if (CitizensCompat.isNPC(event.getWhoClicked()))
			return;

		Player player = (Player) event.getWhoClicked();

		ItemStack isCurrentSlot = event.getCurrentItem();
		ItemStack isCursor = event.getCursor();
		ItemStack isKey = event.getHotbarButton() != -1 ? player.getInventory().getItem(event.getHotbarButton()) : null;

		if ((event.getAction() == InventoryAction.HOTBAR_SWAP
				|| event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) && event.getClick().isKeyboardClick()) {
			// plugin.getMessages().debug("Keyboard click reward=%s",
			// Reward.isReward(player.getInventory().getItem(event.getHotbarButton())));
			if (player.getGameMode() != GameMode.SURVIVAL) {
				event.setCancelled(true);
			}
			return;
		}

		if (isFakeReward(isCurrentSlot)) {
			isCurrentSlot.setType(Material.AIR);
			isCurrentSlot.setAmount(0);
			player.getInventory().clear(event.getSlot());
			return;
		}
		if (isFakeReward(isCursor)) {
			isCursor.setType(Material.AIR);
			isCursor.setAmount(0);
			return;
		}
		if (isFakeReward(isKey)) {
			isKey.setType(Material.AIR);
			isKey.setAmount(0);
			return;
		}
		if (Reward.isReward(isCurrentSlot)) {
			Reward reward = Reward.getReward(isCurrentSlot);
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] "
						+ player.getName() + " has tried to change the value of a BagOfGold Item. Value set to 0!(9)");
				reward.setMoney(0);
				isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot, reward);
			}
		}
		if (Reward.isReward(isCursor)) {
			Reward reward = Reward.getReward(isCursor);
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] "
						+ player.getName() + " has tried to change the value of a BagOfGold Item. Value set to 0!(10)");
				reward.setMoney(0);
				isCursor = Reward.setDisplayNameAndHiddenLores(isCursor, reward);
			}
		}
		if (Reward.isReward(isKey)) {
			Reward reward = Reward.getReward(isKey);
			if (!reward.checkHash()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + "[Warning] "
						+ player.getName() + " has tried to change the value of a BagOfGold Item. Value set to 0!(11)");
				reward.setMoney(0);
				isKey = Reward.setDisplayNameAndHiddenLores(isKey, reward);
			}
		}

		SlotType slotType = event.getSlotType();

		Inventory inventory = event.getInventory();
		Inventory clickedInventory;
		if (Servers.isMC113OrNewer())
			clickedInventory = event.getClickedInventory();
		else
			clickedInventory = inventory;

		if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor) || Reward.isReward(isKey)) {
			// plugin.getMessages().debug(
			// "action=%s, InvType=%s, clickedInvType=%s, slottype=%s, slotno=%s,
			// current=%s, cursor=%s, view=%s, key=%s",
			// action, inventory.getType(), clickedInventory == null ? "null" :
			// clickedInventory.getType(),
			// slotType, event.getSlot(), isCurrentSlot == null ? "null" :
			// isCurrentSlot.getType(),
			// isCursor == null ? "null" : isCursor.getType(), event.getView().getType(),
			// isKey == null ? "null" : isKey.getType());

			List<SlotType> allowedSlots = Arrays.asList(SlotType.CONTAINER, SlotType.QUICKBAR, SlotType.OUTSIDE);

			if (allowedSlots.contains(slotType)) {
				if (isInventoryAllowed(clickedInventory)) {
					switch (action) {
					case CLONE_STACK:
					case UNKNOWN:
						plugin.getMessages().debug("%s tried to clone BagOfGold", player.getName());
						event.setCancelled(true);
						break;
					case COLLECT_TO_CURSOR:
						if (Reward.isReward(isCursor)) {
							Reward cursor = Reward.getReward(isCursor);
							double money_in_hand = cursor.getMoney();
							if (cursor.isMoney()) {
								double saldo = Misc.floor(cursor.getMoney());
								for (int slot = 0; slot < clickedInventory.getSize(); slot++) {
									ItemStack is = clickedInventory.getItem(slot);
									if (Reward.isReward(is)) {
										Reward reward = Reward.getReward(is);
										if ((reward.isMoney()) && reward.getMoney() > 0) {
											saldo = saldo + reward.getMoney();
											if (saldo <= Core.getConfigManager().limitPerBag)
												clickedInventory.clear(slot);
											else {
												reward.setMoney(Core.getConfigManager().limitPerBag);
												is = Reward.setDisplayNameAndHiddenLores(is.clone(), reward);
												is.setAmount(1);
												clickedInventory.clear(slot);
												clickedInventory.addItem(is);
												saldo = saldo - Core.getConfigManager().limitPerBag;
											}
										}
									}
								}
								cursor.setMoney(saldo);
								isCursor = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), cursor);
								event.setCursor(isCursor);
								plugin.getMessages().debug("%s collected %s to the cursor", player.getName(), saldo);
								if (clickedInventory.getType() == InventoryType.PLAYER) {
									if (cursor.isMoney())
										plugin.getRewardManager().removeMoneyFromPlayerBalance(player,
												saldo - money_in_hand);
								}
							} else if (cursor.isKilledHeadReward() || cursor.isKillerHeadReward()) {
								plugin.getMessages()
										.debug("Collect to cursor on MobHunting heads is still not implemented");
							}
						}
						break;
					case DROP_ALL_CURSOR:
					case DROP_ONE_CURSOR:
						if (Reward.isReward(isCursor)) {
							plugin.getMessages().debug("%s tried to do a drop BagOfGold.", player.getName());
							if (slotType == SlotType.OUTSIDE && Reward.isReward(isCursor)) {
								// if (inventory.getType() == InventoryType.PLAYER) {
								Reward reward = Reward.getReward(isCursor);
								plugin.getMessages().debug("%s dropped %s BagOfGold outside the inventory",
										player.getName(), reward.getMoney());
								// must be addMoneyToBalance because PlayerDropItem is called too.
								if (reward.isMoney())
									plugin.getRewardManager().addMoneyToPlayerBalance(player, reward.getMoney());
								// }
							}
						}
						break;
					case DROP_ALL_SLOT:
					case DROP_ONE_SLOT:
						if (Reward.isReward(isCurrentSlot)) {
							plugin.getMessages().debug("%s tried to do a drop BagOfGold.", player.getName());
							if (slotType == SlotType.OUTSIDE && Reward.isReward(isCurrentSlot)) {
								// if (inventory.getType() == InventoryType.PLAYER) {
								Reward reward = Reward.getReward(isCursor);
								plugin.getMessages().debug("%s dropped %s BagOfGold from slot", player.getName(),
										reward.getMoney());
								// must be addMoneyToBalance because PlayerDropItem is called too.
								if (reward.isMoney())
									plugin.getRewardManager().addMoneyToPlayerBalance(player, reward.getMoney());
								// }
							}
						}
						break;
					case HOTBAR_MOVE_AND_READD:
					case HOTBAR_SWAP:
						if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor) || Reward.isReward(isKey)) {
							plugin.getMessages().debug(
									"%s tried to do a HOTBAR_SWAP/HOTBAR_MOVE_AND_READD with a BagOfGold.",
									player.getName());
							Reward playerInv = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot)
									: new Reward();
							Reward chestInv = Reward.isReward(isCursor) ? Reward.getReward(isCursor) : new Reward();
							Reward key = Reward.isReward(isKey) ? Reward.getReward(isKey) : new Reward();

							// plugin.getMessages().debug("slot=%s cursor=%s, key=%s", playerInv.getMoney(),
							// chestInv.getMoney(), key.getMoney());
							if (playerInv.isMoney() || chestInv.isMoney() || key.isMoney())
								if (clickedInventory.getType() == InventoryType.PLAYER) {
									plugin.getRewardManager().removeMoneyFromPlayer(player,
											playerInv.getMoney() + key.getMoney() - chestInv.getMoney());
								} else {
									plugin.getRewardManager().addMoneyToPlayer(player,
											playerInv.getMoney() + key.getMoney() - chestInv.getMoney());
								}
						}
						break;
					case MOVE_TO_OTHER_INVENTORY:
						if ((Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor))
								&& inventory.getType() != InventoryType.ANVIL
								&& inventory.getType() != InventoryType.ENCHANTING) {
							Reward reward = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot)
									: Reward.getReward(isCursor);
							if (reward.isMoney()) {
								if (slotType != SlotType.CONTAINER && slotType != SlotType.QUICKBAR)
									if (clickedInventory.getType() == InventoryType.PLAYER) {
										plugin.getMessages().debug("%s moved %s %s out of the Player Inventory",
												player.getName(), reward.getMoney(), reward.getDisplayName());
										plugin.getRewardManager().removeMoneyFromPlayerBalance(player,
												reward.getMoney());
									} else { // CHEST, DISPENSER, DROPPER, ......
										plugin.getMessages().debug("%s moved %s %s into the Player Inventory",
												player.getName(), reward.getMoney(), reward.getDisplayName());
										plugin.getRewardManager().addMoneyToPlayerBalance(player, reward.getMoney());
									}
							}
						} else {
							plugin.getMessages().debug("%s: this reward can't be moved into %s", player.getName(),
									inventory.getType());
							event.setCancelled(true);
							return;
						}
						break;
					case NOTHING:
						break;
					case PICKUP_ALL:
					case PICKUP_ONE:
					case PICKUP_SOME:
						if (Reward.isReward(isCurrentSlot)) {
							if (clickedInventory.getType() == InventoryType.PLAYER) {
								Reward reward = Reward.getReward(isCurrentSlot);
								plugin.getMessages().debug("%s moved BagOfGold (%s) out of Inventory", player.getName(),
										reward.getMoney());
								if (reward.isMoney())
									plugin.getRewardManager().removeMoneyFromPlayerBalance(player, reward.getMoney());
							}
						}
						break;
					case PICKUP_HALF:
						if (isCursor.getType() == Material.AIR && Reward.isReward(isCurrentSlot)) {
							Reward reward = Reward.getReward(isCurrentSlot);
							if (reward.isMoney()) {
								double currentSlotMoney = Misc.round(reward.getMoney() / 2);
								double cursorMoney = Misc.round(reward.getMoney()) - currentSlotMoney;
								if (cursorMoney >= plugin.getConfigManager().minimumReward) {
									event.setCancelled(true);
									reward.setMoney(currentSlotMoney);
									isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
									event.setCurrentItem(isCurrentSlot);
									reward.setMoney(cursorMoney);
									isCursor = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
									event.setCursor(isCursor);
									plugin.getMessages().debug("%s halfed a reward in two (%s,%s)", player.getName(),
											format(currentSlotMoney), format(cursorMoney));
									if (clickedInventory.getType() == InventoryType.PLAYER) {
										if (reward.isMoney())
											plugin.getRewardManager().removeMoneyFromPlayerBalance(player, cursorMoney);
									}
								}
							}
						}
						break;
					case PLACE_ALL:
						if (Reward.isReward(isCurrentSlot) && isCursor.getType() == Material.AIR) {
							if (clickedInventory.getType() == InventoryType.PLAYER) {
								Reward reward = Reward.getReward(isCurrentSlot);
								plugin.getMessages().debug("(2) %s moved BagOfGold (%s) out of Inventory",
										player.getName(), reward.getMoney());
								if (reward.isMoney())
									plugin.getRewardManager().removeMoneyFromPlayerBalance(player, reward.getMoney());
							}
						}
						if (Reward.isReward(isCursor)) {
							if (clickedInventory.getType() == InventoryType.PLAYER) {
								Reward reward = Reward.getReward(isCursor);
								plugin.getMessages().debug("%s moved BagOfGold (%s) into Inventory", player.getName(),
										reward.getMoney());
								if (reward.isMoney())
									plugin.getRewardManager().addMoneyToPlayerBalance(player, reward.getMoney());
							}
						}
						break;
					case PLACE_ONE:
					case PLACE_SOME:
					case SWAP_WITH_CURSOR:
						if (Reward.isReward(isCurrentSlot) && Reward.isReward(isCursor)) {
							ItemMeta imCurrent = isCurrentSlot.getItemMeta();
							ItemMeta imCursor = isCursor.getItemMeta();
							Reward reward1 = new Reward(imCurrent.getLore());
							Reward reward2 = new Reward(imCursor.getLore());
							if ((reward1.isMoney()) && reward1.getRewardType().equals(reward2.getRewardType())) {
								event.setCancelled(true);
								if (reward1.getMoney() + reward2.getMoney() <= Core.getConfigManager().limitPerBag) {
									double added_money = reward2.getMoney();
									reward2.setMoney(reward1.getMoney() + reward2.getMoney());
									isCursor = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward2);
									isCurrentSlot.setAmount(0);
									isCurrentSlot.setType(Material.AIR);
									event.setCurrentItem(isCursor);
									event.setCursor(isCurrentSlot);
									plugin.getMessages().debug("%s merged two rewards(1)", player.getName());
									if (clickedInventory.getType() == InventoryType.PLAYER) {
										plugin.getRewardManager().addMoneyToPlayerBalance(player, added_money);
									}
								} else {
									double rest = reward1.getMoney() + reward2.getMoney()
											- Core.getConfigManager().limitPerBag;
									double added_money = Core.getConfigManager().limitPerBag - reward1.getMoney();
									reward2.setMoney(Core.getConfigManager().limitPerBag);
									isCursor = Reward.setDisplayNameAndHiddenLores(isCursor.clone(), reward2);
									reward1.setMoney(rest);
									isCurrentSlot = Reward.setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward1);
									event.setCurrentItem(isCursor);
									event.setCursor(isCurrentSlot);
									plugin.getMessages().debug("%s merged two rewards(2)", player.getName());
									if (clickedInventory.getType() == InventoryType.PLAYER) {
										plugin.getRewardManager().addMoneyToPlayerBalance(player, added_money);
									}
								}
							} else if ((reward1.isKilledHeadReward() || reward1.isKillerHeadReward())
									&& reward1.getRewardType().equals(reward2.getRewardType())
									&& reward1.getSkinUUID().equals(reward2.getSkinUUID())
									&& Misc.round(reward1.getMoney()) == Misc.round(reward2.getMoney())) {
								event.setCancelled(true);
								if (isCursor.getAmount() + isCurrentSlot.getAmount() <= 64) {
									isCurrentSlot.setAmount(isCursor.getAmount() + isCurrentSlot.getAmount());
									isCursor.setAmount(0);
									isCursor.setType(Material.AIR);
									plugin.getMessages().debug("%s merged two rewards(3)", player.getName());
								} else {
									isCursor.setAmount(isCursor.getAmount() + isCurrentSlot.getAmount() - 64);
									isCurrentSlot.setAmount(64);
									plugin.getMessages().debug("%s merged two rewards(4)", player.getName());
								}
							}
						} else if (clickedInventory.getType() == InventoryType.PLAYER) {
							double playerInv = Reward.isReward(isCurrentSlot)
									? Reward.getReward(isCurrentSlot).getMoney()
									: 0;
							double chestInv = Reward.isReward(isCursor) ? Reward.getReward(isCursor).getMoney() : 0;
							// plugin.getMessages().debug("(1)slot=%s cursor=%s", playerInv, chestInv);
							// plugin.getRewardManager().removeMoneyFromPlayer(player, playerInv -
							// chestInv);
						} else {
							double playerInv = Reward.isReward(isCurrentSlot)
									? Reward.getReward(isCurrentSlot).getMoney()
									: 0;
							double chestInv = Reward.isReward(isCursor) ? Reward.getReward(isCursor).getMoney() : 0;
							// plugin.getMessages().debug("(2)slot=%s cursor=%s", playerInv, chestInv);
							// plugin.getRewardManager().addMoneyToPlayer(player, playerInv - chestInv);

						}

						break;
					default:
						// plugin.getMessages().debug("BagOfGoldItems: action=%s", action);
						if (player.getGameMode() == GameMode.SURVIVAL)
							plugin.getRewardManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
						else if (player.getGameMode() == GameMode.SPECTATOR)
							break;
						else
							plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
						break;
					}
				} else {
					plugin.getMessages().debug("%s its not allowed to use BagOfGold in a %s inventory",
							player.getName(), inventory.getType());
					event.setCancelled(true);
					return;
				}
			} else {
				plugin.getMessages().debug("%s its not allowed to use BagOfGold a %s slot", player.getName(), slotType);
				event.setCancelled(true);
				return;
			}
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
