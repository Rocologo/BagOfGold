package one.lindegaard.BagOfGold.rewards;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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

public class BagOfGoldItems implements Listener {

	BagOfGold plugin;
	private File file;
	private YamlConfiguration config = new YamlConfiguration();

	private HashMap<Integer, Double> droppedMoney = new HashMap<Integer, Double>();
	private HashMap<UUID, Reward> placedMoney_Reward = new HashMap<UUID, Reward>();
	private HashMap<UUID, Location> placedMoney_Location = new HashMap<UUID, Location>();

	public BagOfGoldItems(BagOfGold plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), "rewards.yml");
		loadAllStoredRewardsFromMobHunting();
		loadAllStoredRewards();
		if (isBagOfGoldStyle()) {
			plugin.getMessages().debug("BagOfGoldItems: register events");
			Bukkit.getPluginManager().registerEvents(this, plugin);
		} else
			plugin.getMessages().debug("BagOfGoldItems: could not register events");
		
	}

	public boolean isBagOfGoldStyle() {
		return plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")
				|| plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
				|| plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLED")
				|| plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLER");
	}

	public HashMap<Integer, Double> getDroppedMoney() {
		return droppedMoney;
	}

	public HashMap<UUID, Reward> getReward() {
		return placedMoney_Reward;
	}

	public HashMap<UUID, Location> getLocations() {
		return placedMoney_Location;
	}

	public String format(double money) {
		return Misc.format(money);
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
				if ((rewardInSlot.isBagOfGoldReward() || rewardInSlot.isItemReward())) {
					if (rewardInSlot.getMoney() < plugin.getConfigManager().limitPerBag) {
						double space = plugin.getConfigManager().limitPerBag - rewardInSlot.getMoney();
						if (space > moneyLeftToGive) {
							addedMoney = addedMoney + moneyLeftToGive;
							rewardInSlot.setMoney(rewardInSlot.getMoney() + moneyLeftToGive);
							moneyLeftToGive = 0;
						} else {
							addedMoney = addedMoney + space;
							rewardInSlot.setMoney(plugin.getConfigManager().limitPerBag);
							moneyLeftToGive = moneyLeftToGive - space;
						}
						if (rewardInSlot.getMoney() == 0)
							player.getInventory().clear(slot);
						else
							is = setDisplayNameAndHiddenLores(is, rewardInSlot);
						plugin.getMessages().debug(
								"Added %s to %s's item in slot %s, new value is %s (addBagOfGoldPlayer_EconomyManager)",
								format(amount), player.getName(), slot, format(rewardInSlot.getMoney()));
						if (moneyLeftToGive <= 0) {
							found = true;
							break;
						}
					}
				}
			}
		}
		if (!found) {

			while (Misc.round(moneyLeftToGive) > 0 && canPickupMoney(player)) {
				double nextBag = 0;
				if (moneyLeftToGive > plugin.getConfigManager().limitPerBag) {
					nextBag = plugin.getConfigManager().limitPerBag;
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
					if (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL"))
						is = new CustomItems(plugin).getCustomtexture(
								UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID),
								plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
								plugin.getConfigManager().dropMoneyOnGroundSkullTextureValue,
								plugin.getConfigManager().dropMoneyOnGroundSkullTextureSignature, Misc.round(nextBag),
								UUID.randomUUID(), UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID));
					else {
						is = new ItemStack(Material.valueOf(plugin.getConfigManager().dropMoneyOnGroundItem), 1);
						setDisplayNameAndHiddenLores(is,
								new Reward(plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
										Misc.round(nextBag), UUID.fromString(Reward.MH_REWARD_ITEM_UUID),
										UUID.randomUUID(), null));
					}
					player.getInventory().addItem(is);
				}
			}
		}
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
				if (reward.isBagOfGoldReward() || reward.isItemReward()) {
					double saldo = Misc.round(reward.getMoney());
					if (saldo > toBeTaken) {
						reward.setMoney(Misc.round(saldo - toBeTaken));
						is = setDisplayNameAndHiddenLores(is, reward);
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
			}

		}
		return taken;
	}

	public void dropBagOfGoldMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		Item item = null;
		double moneyLeftToDrop = Misc.ceil(money);
		ItemStack is;
		UUID uuid = null, skinuuid = null;
		double nextBag = 0;
		while (moneyLeftToDrop > 0) {
			if (moneyLeftToDrop > plugin.getConfigManager().limitPerBag) {
				nextBag = plugin.getConfigManager().limitPerBag;
				moneyLeftToDrop = Misc.round(moneyLeftToDrop - nextBag);
			} else {
				nextBag = Misc.round(moneyLeftToDrop);
				moneyLeftToDrop = 0;
			}

			if (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")) {
				uuid = UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID);
				skinuuid = uuid;
				is = new CustomItems(plugin).getCustomtexture(uuid,
						plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
						plugin.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						plugin.getConfigManager().dropMoneyOnGroundSkullTextureSignature, nextBag, UUID.randomUUID(),
						skinuuid);
			} else { // ITEM
				uuid = UUID.fromString(Reward.MH_REWARD_ITEM_UUID);
				skinuuid = null;
				is = new ItemStack(Material.valueOf(plugin.getConfigManager().dropMoneyOnGroundItem), 1);
			}

			item = location.getWorld().dropItem(location, is);
			if (item != null) {
				droppedMoney.put(item.getEntityId(), nextBag);
				item.setMetadata(Reward.MH_REWARD_DATA,
						new FixedMetadataValue(plugin,
								new Reward(
										plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
												? "" : Reward.getReward(is).getDisplayname(),
										nextBag, uuid, UUID.randomUUID(), skinuuid)));
				item.setCustomName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
						+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
								? format(nextBag)
								: Reward.getReward(is).getDisplayname() + " (" + format(nextBag) + ")"));
				item.setCustomNameVisible(true);
				plugin.getMessages().debug("%s dropped %s on the ground as item %s (# of rewards=%s)", player.getName(),
						format(nextBag), plugin.getConfigManager().dropMoneyOnGroundItemtype, droppedMoney.size());
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
				if (reward.isBagOfGoldReward() || reward.isItemReward())
					amountInInventory = amountInInventory + reward.getMoney();
			}
		}
		return amountInInventory;
	}

	/**
	 * setDisplayNameAndHiddenLores: add the Display name and the (hidden)
	 * Lores. The lores identifies the reward and contain secret information.
	 * 
	 * @param skull
	 *            - The base itemStack without the information.
	 * @param reward
	 *            - The reward information is added to the ItemStack
	 * @return the updated ItemStack.
	 */
	public ItemStack setDisplayNameAndHiddenLores(ItemStack skull, Reward reward) {
		ItemMeta skullMeta = skull.getItemMeta();
		if (reward.getRewardType().equals(UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID)))
			skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + reward.getDisplayname(),
					"Hidden:" + reward.getMoney(), "Hidden:" + reward.getRewardType(),
					reward.getMoney() == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(),
					"Hidden:" + reward.getSkinUUID())));
		else
			skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + reward.getDisplayname(),
					"Hidden:" + reward.getMoney(), "Hidden:" + reward.getRewardType(),
					reward.getMoney() == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(),
					"Hidden:" + reward.getSkinUUID(), plugin.getMessages().getString("bagofgold.reward.name"))));

		if (reward.getMoney() == 0)
			skullMeta.setDisplayName(
					ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + reward.getDisplayname());
		else
			skullMeta.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
					+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
							? format(reward.getMoney())
							: reward.getDisplayname() + " (" + format(reward.getMoney()) + ")"));
		skull.setItemMeta(skullMeta);
		return skull;
	}

	public boolean canPickupMoney(Player player) {
		if (player.getInventory().firstEmpty() != -1)
			return true;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				if ((rewardInSlot.isBagOfGoldReward() || rewardInSlot.isItemReward())) {
					if (rewardInSlot.getMoney() < plugin.getConfigManager().limitPerBag)
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
				if ((rewardInSlot.isBagOfGoldReward() || rewardInSlot.isItemReward())) {
					space = space + plugin.getConfigManager().limitPerBag - rewardInSlot.getMoney();
				}
			} else if (is == null || is.getType() == Material.AIR) {
				space = space + plugin.getConfigManager().limitPerBag;
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
		if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack
				.getItemMeta().getDisplayName().contains(plugin.getConfigManager().dropMoneyOnGroundSkullRewardName)) {
			if (!itemStack.getItemMeta().hasLore()) {
				return true;
			}
		}
		return false;
	}

	public void saveReward(UUID uuid) {
		try {
			config.options().header("This is the rewards placed as blocks. Do not edit this file manually!");
			if (placedMoney_Reward.containsKey(uuid)) {
				Location location = placedMoney_Location.get(uuid);
				if (location != null && Misc.isSkull(location.getBlock().getType())) {
					Reward reward = placedMoney_Reward.get(uuid);
					ConfigurationSection section = config.createSection(uuid.toString());
					section.set("location", location);
					reward.save(section);
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadAllStoredRewards() {
		int n = 0;
		int deleted = 0;
		try {

			if (!file.exists()) {
				File file2 = new File(plugin.getDataFolder().getParentFile(), "MobHunting/rewards.yml");
				if (file2.exists()) {
					plugin.getMessages().debug("Loading rewards from MobHunting first time.");
					loadAllStoredRewardsFromMobHunting();
				}
				return;
			}

			config.load(file);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				Reward reward = new Reward();
				reward.read(section);
				Location location = (Location) section.get("location");
				if (location != null && Misc.isSkull(location.getBlock().getType())) {
					location.getBlock().setMetadata(Reward.MH_REWARD_DATA,
							new FixedMetadataValue(plugin, new Reward(reward)));
					placedMoney_Reward.put(UUID.fromString(key), reward);
					placedMoney_Location.put(UUID.fromString(key), location);
					n++;
				} else {
					deleted++;
					config.set(key, null);
				}
			}
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {

			if (deleted > 0) {
				plugin.getMessages().debug("Deleted %s rewards from the rewards.yml file", deleted);
				File file_copy = new File(plugin.getDataFolder(), "rewards.yml.old");
				Files.copy(file.toPath(), file_copy.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
						StandardCopyOption.REPLACE_EXISTING);
				config.save(file);
			}
			if (n > 0) {
				plugin.getMessages().debug("Loaded %s rewards from the rewards.yml file", n);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadAllStoredRewardsFromMobHunting() {
		int n = 0;
		int deleted = 0;
		File file = new File(plugin.getDataFolder().getParentFile(), "MobHunting/rewards.yml");
		try {

			if (!file.exists())
				return;

			config.load(file);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				Reward reward = new Reward();
				reward.read(section);
				Location location = (Location) section.get("location");
				if (location != null && Misc.isSkull(location.getBlock().getType())) {
					location.getBlock().setMetadata(Reward.MH_REWARD_DATA,
							new FixedMetadataValue(plugin, new Reward(reward)));
					placedMoney_Reward.put(UUID.fromString(key), reward);
					placedMoney_Location.put(UUID.fromString(key), location);
					saveReward(UUID.fromString(key));
					n++;
				} else {
					deleted++;
					config.set(key, null);
				}
			}
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		try {

			if (deleted > 0) {
				plugin.getMessages().debug("Deleted %s rewards from the rewards.yml file", deleted);
				File file_copy = new File(plugin.getDataFolder(), "rewards.yml.old");
				Files.copy(file.toPath(), file_copy.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
						StandardCopyOption.REPLACE_EXISTING);
				config.save(file);
			}
			if (n > 0) {
				plugin.getMessages().debug("Loaded %s rewards from the rewards.yml file", n);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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
			double money = reward.getMoney();
			if (money == 0) {
				item.setCustomName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
						+ reward.getDisplayname());
				droppedMoney.put(item.getEntityId(), money);
				plugin.getMessages().debug("%s dropped a %s (# of rewards left=%s)", player.getName(),
						reward.getDisplayname() != null ? reward.getDisplayname()
								: plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
						droppedMoney.size());
			} else {
				if (reward.isItemReward())
					item.setCustomName(
							ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + format(money));
				else
					item.setCustomName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
							+ reward.getDisplayname() + " (" + format(money) + ")");

				droppedMoney.put(item.getEntityId(), money);
				plugin.getMessages().debug("%s dropped %s money. (# of rewards left=%s)", player.getName(),
						format(money), droppedMoney.size());
				plugin.getMessages().playerActionBarMessageQueue(player,
						plugin.getMessages().getString("bagofgold.moneydrop", "money", format(money), "rewardname",
								ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
										+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
												? plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()
												: reward.getDisplayname())));
				plugin.getEconomyManager().removeMoneyFromBalance(player, money);
			}
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
			placedMoney_Reward.put(reward.getUniqueUUID(), reward);
			placedMoney_Location.put(reward.getUniqueUUID(), block.getLocation());
			plugin.getBagOfGoldItems().saveReward(reward.getUniqueUUID());
			if (reward.isBagOfGoldReward() || reward.isItemReward()) {
				plugin.getEconomyManager().removeMoneyFromBalance(player, reward.getMoney());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRewardBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		CustomItems customItems = new CustomItems(plugin);

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
						? format(reward.getMoney())
						: (reward.getMoney() == 0 ? reward.getDisplayname()
								: reward.getDisplayname() + " (" + format(reward.getMoney()) + ")");
				item.setCustomName(
						ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + displayName);
				item.setCustomNameVisible(true);
				item.setMetadata(Reward.MH_REWARD_DATA,
						new FixedMetadataValue(plugin, new Reward(reward.getHiddenLore())));
				if (placedMoney_Location.containsKey(reward.getUniqueUUID()))
					placedMoney_Location.remove(reward.getUniqueUUID());
				if (placedMoney_Reward.containsKey(reward.getUniqueUUID()))
					placedMoney_Reward.remove(reward.getUniqueUUID());
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
				plugin.getEconomyManager().removeMoneyFromBalance(player, reward.getMoney());
				plugin.getMessages().playerActionBarMessageQueue(player,
						ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ reward.getDisplayname() + plugin.getMessages().getString("bagofgold.moneydrop",
										"money", Misc.round(reward.getMoney())));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDespawnRewardEvent(ItemDespawnEvent event) {
		if (event.isCancelled())
			return;

		if (Reward.isReward(event.getEntity())) {
			if (droppedMoney.containsKey(event.getEntity().getEntityId())) {
				droppedMoney.remove(event.getEntity().getEntityId());
				if (event.getEntity().getLastDamageCause() != null)
					plugin.getMessages().debug("The reward was destroyed by %s",
							event.getEntity().getLastDamageCause().getCause());
				else
					plugin.getMessages().debug("The reward despawned (# of rewards left=%s)", droppedMoney.size());
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
			if (droppedMoney.containsKey(item.getEntityId()))
				droppedMoney.remove(item.getEntityId());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveOverRewardEvent(PlayerMoveEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();

		if (canPickupMoney(player)) {
			Iterator<Entity> entityList = ((Entity) player).getNearbyEntities(1, 1, 1).iterator();
			while (entityList.hasNext() && canPickupMoney(player)) {
				Entity entity = entityList.next();
				if (!(entity instanceof Item))
					continue;

				Item item = (Item) entity;

				if (isFakeReward(item)) {
					player.sendMessage(ChatColor.RED + "[BagOfGold] WARNING, this was a FAKE reward with no value");
					return;
				}

				if (Reward.isReward(item)) {
					if (droppedMoney.containsKey(entity.getEntityId())) {
						droppedMoney.remove(entity.getEntityId());
						Reward reward = Reward.getReward(item);
						item.remove();
						if (reward.isBagOfGoldReward() || reward.isItemReward()) {
							double addedMoney = 0;
							//plugin.getMessages().debug("AddMoney if possible: %s", reward.getMoney());
							addedMoney = addBagOfGoldMoneyToPlayer(player, reward.getMoney());
							//plugin.getMessages().debug("AddedMoney=%s", addedMoney);

							if (addedMoney > 0) {
								PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
								ps.setBalance(Misc.round(ps.getBalance() + addedMoney));
								plugin.getPlayerBalanceManager().setPlayerBalance(player, ps);
							}
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
				if (droppedMoney.containsKey(targetEntity.getEntityId()))
					droppedMoney.remove(targetEntity.getEntityId());
				targetEntity.remove();
				plugin.getMessages().debug("The reward was hit by %s and removed. (# of rewards left=%s)",
						projectile.getType(), droppedMoney.size());
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
                    if (Misc.round(reward.getMoney()) != Misc
                            .round(addBagOfGoldMoneyToPlayer(player, reward.getMoney())))
                        dropBagOfGoldMoneyOnGround(player, null, player.getLocation(), reward.getMoney());
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

		if (Misc.isMC19OrNewer() && event.getHand() != EquipmentSlot.HAND)
			return;

		Player player = event.getPlayer();

		Block block = event.getClickedBlock();

		if (Reward.hasReward(block)) {
			Reward reward = Reward.getReward(block);
			if (reward.getMoney() == 0)
				plugin.getMessages().playerActionBarMessageQueue(player,
						ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ reward.getDisplayname());
			else
				plugin.getMessages().playerActionBarMessageQueue(player,
						ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
										? format(reward.getMoney())
										: reward.getDisplayname() + " (" + format(reward.getMoney()) + ")"));
		} else if (Misc.isMC113OrNewer()
				&& (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD)) {
			Skull skullState = (Skull) block.getState();
			OfflinePlayer owner = skullState.getOwningPlayer();
			if (owner != null && owner.getName() != null)
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

		ItemStack isCurrentSlot = event.getCurrentItem();
		ItemStack isCursor = event.getCursor();

		Player player = (Player) event.getWhoClicked();

		if (!(Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor))) {
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
			return;
		}

		InventoryAction action = event.getAction();
		SlotType slotType = event.getSlotType();

		// Inventory inventory = event.getInventory();
		// if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
		// plugin.getMessages().debug(
		// "action=%s, InventoryType=%s, slottype=%s, slotno=%s, current=%s,
		// cursor=%s, view=%s", action,
		// inventory.getType(), slotType, event.getSlot(),
		// isCurrentSlot == null ? "null" : isCurrentSlot.getType(),
		// isCursor == null ? "null" : isCursor.getType(),
		// event.getView().getType());
		// }

		if (action == InventoryAction.NOTHING)
			return;

		if (!(slotType == SlotType.CONTAINER || slotType == SlotType.QUICKBAR || slotType == SlotType.OUTSIDE
				|| slotType == SlotType.RESULT || (slotType == SlotType.ARMOR && event.getSlot() == 39))) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				//Reward reward = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot)
				//		: Reward.getReward(isCursor);
				// plugin.getMessages().learn(player,
				// plugin.getMessages().getString("mobhunting.learn.rewards.no-use",
				// "rewardname", reward.getDisplayname()));
				plugin.getMessages().debug("RewardListerner: cancel 1");
				event.setCancelled(true);
				return;
			}
		}

		if (action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN) {
			if (Reward.isReward(isCurrentSlot) || Reward.isReward(isCursor)) {
				//Reward reward = Reward.isReward(isCurrentSlot) ? Reward.getReward(isCurrentSlot)
				//		: Reward.getReward(isCursor);
				// plugin.getMessages().learn(player,
				// plugin.getMessages().getString("mobhunting.learn.rewards.no-clone",
				// "rewardname", reward.getDisplayname()));
				plugin.getMessages().debug("RewardListerner: cancel 2");
				event.setCancelled(true);
				return;
			}
		}

		if (action == InventoryAction.SWAP_WITH_CURSOR) {
			if (Reward.isReward(isCurrentSlot) && Reward.isReward(isCursor)) {
				event.setCancelled(true);
				ItemMeta imCurrent = isCurrentSlot.getItemMeta();
				ItemMeta imCursor = isCursor.getItemMeta();
				Reward reward1 = new Reward(imCurrent.getLore());
				Reward reward2 = new Reward(imCursor.getLore());
				if ((reward1.isBagOfGoldReward() || reward1.isItemReward())
						&& reward1.getRewardType().equals(reward2.getRewardType())) {
					if (reward2.getMoney() + reward2.getMoney() <= plugin.getConfigManager().limitPerBag) {
						reward2.setMoney(reward1.getMoney() + reward2.getMoney());
						imCursor.setLore(reward2.getHiddenLore());
						imCursor.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
										? format(reward2.getMoney())
										: reward2.getDisplayname() + " (" + format(reward2.getMoney()) + ")"));
						isCursor.setItemMeta(imCursor);
						isCurrentSlot.setAmount(0);
						isCurrentSlot.setType(Material.AIR);
						event.setCurrentItem(isCursor);
						event.setCursor(isCurrentSlot);
						plugin.getMessages().debug("%s merged two rewards", player.getName());
					} else {
						double rest = reward1.getMoney() + reward2.getMoney() - plugin.getConfigManager().limitPerBag;
						reward2.setMoney(plugin.getConfigManager().limitPerBag);
						imCursor.setLore(reward2.getHiddenLore());
						imCursor.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
										? format(plugin.getConfigManager().limitPerBag)
										: reward2.getDisplayname() + " ("
												+ format(plugin.getConfigManager().limitPerBag) + ")"));
						isCursor.setItemMeta(imCursor);

						reward1.setMoney(rest);
						imCurrent.setLore(reward1.getHiddenLore());
						imCurrent.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
								+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
										? format(plugin.getConfigManager().limitPerBag)
										: reward1.getDisplayname() + " (" + format(reward1.getMoney()) + ")"));
						isCurrentSlot.setItemMeta(imCurrent);
						event.setCurrentItem(isCursor);
						event.setCursor(isCurrentSlot);
						plugin.getMessages().debug("%s merged two rewards", player.getName());
					}
				} else {
				}
			}

		} else if (action == InventoryAction.PICKUP_HALF) {
			if (isCursor.getType() == Material.AIR && Reward.isReward(isCurrentSlot)) {
				Reward reward = Reward.getReward(isCurrentSlot);
				if (reward.isBagOfGoldReward() || reward.isItemReward()) {
					double currentSlotMoney = Misc.round(reward.getMoney() / 2);
					double cursorMoney = Misc.round(reward.getMoney() - currentSlotMoney);
					if (cursorMoney >= plugin.getConfigManager().minimumReward) {
						event.setCancelled(true);
						reward.setMoney(currentSlotMoney);
						isCurrentSlot = setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
						event.setCurrentItem(isCurrentSlot);
						reward.setMoney(cursorMoney);
						isCursor = setDisplayNameAndHiddenLores(isCurrentSlot.clone(), reward);
						event.setCursor(isCursor);
						plugin.getMessages().debug("%s halfed a reward in two (%s,%s)", player.getName(),
								format(currentSlotMoney), format(cursorMoney));
					}
				} else if (reward.isKilledHeadReward() || reward.isKilledHeadReward()) {

				}
			}
		} else if (action == InventoryAction.COLLECT_TO_CURSOR) {
			if (Reward.isReward(isCursor)) {
				Reward cursor = Reward.getReward(isCursor);
				if (cursor.isBagOfGoldReward() || cursor.isItemReward()) {
					double saldo = Misc.floor(cursor.getMoney());
					for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
						ItemStack is = player.getInventory().getItem(slot);
						if (Reward.isReward(is)) {
							Reward reward = Reward.getReward(is);
							if ((reward.isBagOfGoldReward() || reward.isItemReward()) && reward.getMoney() > 0) {
								saldo = saldo + reward.getMoney();
								if (saldo <= plugin.getConfigManager().limitPerBag)
									player.getInventory().clear(slot);
								else {
									reward.setMoney(plugin.getConfigManager().limitPerBag);
									is = setDisplayNameAndHiddenLores(is.clone(), reward);
									is.setAmount(1);
									// event.setCurrentItem(is);
									player.getInventory().clear(slot);
									player.getInventory().addItem(is);
									saldo = saldo - plugin.getConfigManager().limitPerBag;
								}
							}
						}
					}
					cursor.setMoney(saldo);
					isCursor = setDisplayNameAndHiddenLores(isCursor.clone(), cursor);
					event.setCursor(isCursor);
				}
			}
		}
	}

}
