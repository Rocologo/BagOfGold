package one.lindegaard.BagOfGold.rewards;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.util.Misc;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.Tools;
import one.lindegaard.Core.materials.Materials;
import one.lindegaard.Core.mobs.MobType;
import one.lindegaard.Core.rewards.Reward;
import one.lindegaard.Core.rewards.RewardBlock;
import one.lindegaard.Core.server.Servers;

public class RewardManager {

	private BagOfGold plugin;

	private File file;
	private YamlConfiguration config = new YamlConfiguration();
	private PickupRewards pickupRewards;

	private HashMap<Integer, Double> droppedMoney = new HashMap<Integer, Double>();
	private HashMap<Integer, RewardBlock> rewardBlocks = new HashMap<Integer, RewardBlock>();

	public RewardManager(BagOfGold plugin) {
		this.plugin = plugin;

		pickupRewards = new PickupRewards(plugin);

		Bukkit.getPluginManager().registerEvents(new RewardListeners(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new MoneyMergeEventListener(plugin), plugin);

		if (Servers.isMC112OrNewer() && eventDoesExists())
			Bukkit.getPluginManager().registerEvents(new EntityPickupItemEventListener(pickupRewards), plugin);
		else
			Bukkit.getPluginManager().registerEvents(new PlayerPickupItemEventListener(pickupRewards), plugin);

		file = new File(plugin.getDataFolder(), "rewards.yml");

	}

	public HashMap<Integer, Double> getDroppedMoney() {
		return droppedMoney;
	}

	public HashMap<Integer, RewardBlock> getRewardBlocks() {
		return rewardBlocks;
	}

	public RewardBlock getRewardBlock(int n) {
		return rewardBlocks.get(n);
	}

	/**
	 * Check if EntityPickupItemEvent exists. EntityPickupItemEvent was introduced
	 * in after MC 1.12 was released. The result is that some MC 1.12 server know
	 * the event, others dont.
	 * 
	 * @return
	 */
	private boolean eventDoesExists() {
		try {
			@SuppressWarnings({ "rawtypes", "unused" })
			Class cls = Class.forName("org.bukkit.event.entity.EntityPickupItemEvent");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}

	}

	/**
	 * getBalance : calculate the player balance
	 * 
	 * @param offlinePlayer
	 * @return
	 */
	public double getBalance(OfflinePlayer offlinePlayer) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		return ps.getBalance() + ps.getBalanceChanges();
	}

	public boolean setbalance(OfflinePlayer offlinePlayer, double amount) {
		double bal = getBalance(offlinePlayer);
		if (offlinePlayer.isOnline() && ((Player) offlinePlayer).getGameMode() != GameMode.SPECTATOR) {
			if (amount >= bal)
				addMoneyToPlayer((Player) offlinePlayer, amount - bal);
			else
				removeMoneyFromPlayer((Player) offlinePlayer, bal - amount);
		} else {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			ps.setBalanceChanges(ps.getBalanceChanges() + (amount - bal));
			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
		}
		return true;
	}

	/**
	 * depositPlayer : deposit the amount to the players balance and add the mount
	 * to the players inventory. Do not deposit negative amounts!
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return EconomyResponce containing amount, balance and ResponseType
	 *         (Success/failure)
	 */
	public boolean depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		double drop = 0, give = amount;
		if (amount == 0) {
			return true;
		} else if (amount > 0) {
			if (offlinePlayer.isOnline()) {
				Player player = (Player) offlinePlayer;
				double space = getSpaceForMoney(player);
				if (amount > space) {
					give = space;
					drop = amount - give;
				}
				addMoneyToPlayer(player, Misc.round(ps.getBalanceChanges()) + Misc.round(give));
				dropMoneyOnGround(player, null, player.getLocation(), drop);
				ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() + give));
				ps.setBalanceChanges(0);
			} else {
				ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() + give));
			}
			plugin.getMessages().debug("Deposit %s to %s's account, new balance is %s", format(give),
					offlinePlayer.getName(), format(ps.getBalance() + ps.getBalanceChanges()));
			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			return true;
		} else {
			plugin.getMessages().debug("Could not deposit %s to %s's account, because the number is negative",
					format(amount), offlinePlayer.getName());
			return false;
		}
	}

	/**
	 * withdrawPlayer : withdraw the amount from the players balance and remove the
	 * mount to the players inventory. Do not withdraw negative amounts.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return EconomyResponse containing amount withdraw, balance and ResponseType
	 *         (Success/Failure).
	 */
	public boolean withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		if (amount >= 0) {
			if (hasMoney(offlinePlayer, amount)) {
				if (offlinePlayer.isOnline()) {
					removeMoneyFromPlayer((Player) offlinePlayer, amount + Misc.round(ps.getBalanceChanges()));
					ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() - amount));
					ps.setBalanceChanges(0);
				} else
					ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - amount));
				plugin.getMessages().debug("Withdraw %s from %s's account, new balance is %s", format(amount),
						offlinePlayer.getName(), format(ps.getBalance() + ps.getBalanceChanges()));
				plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
				if (offlinePlayer.isOnline() && ((Player) offlinePlayer).isValid()) {
					Player player = (Player) offlinePlayer;
					if (player.getGameMode() == GameMode.SURVIVAL) {
						plugin.getMessages().debug(
								"EconomyManager: withdrawPlayer adjusting Player Balance to Amount of BagOfGold in Inventory",
								player.getName());
						plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
					} else {
						plugin.getMessages().debug(
								"EconomyManager: withdrawPlayer %s adjusting Amount of BagOfGold in Inventory To Balance",
								player.getName());
						plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
					}
				}
				return true;
			} else {
				double remove = Misc.round(ps.getBalance() + ps.getBalanceChanges());
				plugin.getMessages().debug("%s has not enough bagofgold, Withdrawing only %s , new balance is %s",
						offlinePlayer.getName(), format(remove), format(0));
				if (remove > 0) {
					removeMoneyFromPlayer((Player) offlinePlayer, remove);
					ps.setBalance(0);
					ps.setBalanceChanges(0);
					plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
					return true;
				}
				return false;
			}
		} else
			return true;
	}

	/**
	 * MobHunting has : checks if the player has amount of mount on his balance.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return true if the player has the amount on his money.
	 */
	public boolean hasMoney(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance pb = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		plugin.getMessages().debug("Check if %s has %s %s on the balance=%s)", offlinePlayer.getName(), format(amount),
				Core.getConfigManager().bagOfGoldName, format(pb.getBalance() + pb.getBalanceChanges()));
		return Misc.round(pb.getBalance()) + Misc.round(pb.getBalanceChanges()) >= Misc.round(amount);
	}

	/**
	 * dropMoneyOnGround_EconomyManager: drop the amount of money in the location
	 * 
	 * @param player       - not used in EconomyManager
	 * @param killedEntity -Misc not used in EconomyManager
	 * @param location
	 * @param money
	 */
	public void dropMoneyOnGround(Player player, Entity killedEntity, Location location, double amount) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			plugin.getBagOfGoldItems().dropBagOfGoldMoneyOnGround(player, killedEntity, location, amount);
		else if (plugin.getGringottsItems().isGringottsStyle())
			plugin.getGringottsItems().dropGringottsMoneyOnGround(player, killedEntity, location, amount);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ Core.getConfigManager().rewardItemtype + "'");
		}
	}

	/**
	 * Dropes an Reward Item at the specified location
	 * 
	 * @param location - where the Item is dropped.
	 * @param reward   - the reward to be dropped
	 */
	public void dropRewardOnGround(Location location, Reward reward) {
		if (reward.isBagOfGoldReward()) {
			dropMoneyOnGround(null, null, location, reward.getMoney());
		} else if (reward.isItemReward()) {
			ItemStack is = new ItemStack(Material.valueOf(Core.getConfigManager().rewardItem), 1);
			Item item = location.getWorld().dropItemNaturally(location, is);
			plugin.getRewardManager().getDroppedMoney().put(item.getEntityId(), reward.getMoney());
		} else if (reward.isKilledHeadReward()) {
			MobType mob = MobType.getMobType(reward.getSkinUUID());
			if (mob != null) {
				ItemStack is = new CustomItems().getCustomHead(mob, reward.getDisplayName(), 1, reward.getMoney(),
						reward.getSkinUUID());
				Item item = location.getWorld().dropItemNaturally(location, is);
				item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, new Reward(reward)));
				getDroppedMoney().put(item.getEntityId(), reward.getMoney());
			} else {
				plugin.getMessages().debug("Reward Manager could not find a mob from reward=%s", reward.toString());
			}
		} else if (reward.isKillerHeadReward()) {
			ItemStack is = new CustomItems().getPlayerHead(reward.getSkinUUID(), reward.getDisplayName(), 1,
					reward.getMoney());
			Item item = location.getWorld().dropItemNaturally(location, is);
			item.setMetadata(Reward.MH_REWARD_DATA_NEW, new FixedMetadataValue(plugin, new Reward(reward)));
			plugin.getRewardManager().getDroppedMoney().put(item.getEntityId(), reward.getMoney());
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "Unhandled reward type in RewardManager (DropRewardOnGround).");
		}
	}

	/**
	 * bankDeposit: deposit the amount on the account.
	 * 
	 * @param account - This is the player UUID as a String
	 * @param amount
	 * @return EconomyResponse containing amount, balance and ResponseType
	 *         (Success/Failure).
	 */
	public boolean bankDeposit(String account, double amount) {
		OfflinePlayer offlinePlayer = Tools.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges() + amount));
				ps.setBankBalanceChanges(0);
			} else {
				ps.setBankBalanceChanges(ps.getBankBalanceChanges() + amount);
			}
			plugin.getMessages().debug("bankDeposit: %s", ps.toString());

			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			return true;
		} else
			return false;
	}

	/**
	 * bankWithdraw: withdraw the amount from the account.
	 * 
	 * @param account - This is the player UUID as a String
	 * @param amount
	 * @return EconomyResponse containing amount, balance and ResponseType
	 *         (Success/Failure).
	 */
	public boolean bankWithdraw(String account, double amount) {
		OfflinePlayer offlinePlayer = Tools.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges() - amount));
				ps.setBankBalanceChanges(0);
			} else {
				ps.setBankBalanceChanges(ps.getBankBalanceChanges() - amount);
			}
			plugin.getMessages().debug("bankWithdraw: %s -  ", ps.toString());

			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			return true;
		} else
			return false;
	}

	/**
	 * bankBalance: withdraw the amount from the account.
	 * 
	 * @param account - This is the player UUID as a String
	 * @return EconomyResponse containing amount, bank balance and ResponseType
	 *         (Success/Failure).
	 */
	public double bankBalance(String account) {
		OfflinePlayer offlinePlayer = Tools.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges()));
				ps.setBankBalanceChanges(0);
				plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			}
			return ps.getBankBalance() + ps.getBankBalanceChanges();
		} else
			return 0;
	}

	public boolean setBankBalance(String account, double amount) {
		OfflinePlayer offlinePlayer = Tools.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			ps.setBankBalance(Misc.round(amount));
			ps.setBankBalanceChanges(0);
			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
		}
		return true;
	}

	/**
	 * deleteBank: Reset the account and set the bank balance to 0.
	 * 
	 * @param account - this is the player UUID.
	 * @return ResponseType (Success/Failure)
	 */
	public boolean deleteBank(String account) {
		OfflinePlayer offlinePlayer = Tools.isUUID(account) ? Bukkit.getOfflinePlayer(UUID.fromString(account))
				: Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null) {
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
			ps.setBankBalance(0);
			ps.setBankBalanceChanges(0);
			plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
			return true;
		} else
			return false;
	}

	public boolean isBankOwner(String account, OfflinePlayer offlinePlayer) {
		return account.equalsIgnoreCase(offlinePlayer.getUniqueId().toString());
	}

	/**
	 * Check if the player is a member of the bank account
	 * 
	 * @param name          of the account
	 * @param offlinePlayer to check membership
	 * @return EconomyResponse Object
	 */
	public boolean isBankMember(String account, OfflinePlayer offlinePlayer) {
		return account.equalsIgnoreCase(offlinePlayer.getUniqueId().toString());
	}

	/**
	 * Format the number
	 * 
	 * @param money
	 * @return
	 */
	public String format(double money) {
		return Tools.format(money);
	}

	/**
	 * Calculate the total amount of money in the player inventory.
	 * 
	 * @param player
	 * @return
	 */
	public double getAmountInInventory(Player player) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().getAmountOfBagOfGoldMoneyInInventory(player);
		else if (plugin.getGringottsItems().isGringottsStyle())
			return plugin.getGringottsItems().getAmountOfGringottsMoneyInInventory(player);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ Core.getConfigManager().rewardItemtype + "'");
			return 0;
		}
	}

	/**
	 * Add amount to the player inventory, but NOT on player balance in
	 * memory/database.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public double addMoneyToPlayer(Player player, double amount) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().addBagOfGoldMoneyToPlayer(player, amount);
		else if (plugin.getGringottsItems().isGringottsStyle())
			return plugin.getGringottsItems().addGringottsMoneyToPlayer(player, amount);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ Core.getConfigManager().rewardItemtype + "'");
			return 0;
		}
	}

	/**
	 * Remove the amount from the player inventory but NOT from the player balance
	 * in memory/database.
	 * 
	 * @param player
	 * @param amount
	 * @return
	 */
	public double removeMoneyFromPlayer(Player player, double amount) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().removeBagOfGoldFromPlayer(player, amount);
		else if (plugin.getGringottsItems().isGringottsStyle())
			return plugin.getGringottsItems().removeGringottsMoneyFromPlayer(player, amount);
		else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ Core.getConfigManager().rewardItemtype + "'");
			return 0;
		}
	}

	/**
	 * Remove the amount from the player balance in memory/databse without removing
	 * amount from the player inventory
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public void removeMoneyFromPlayerBalance(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		plugin.getMessages().debug("Removing %s from %s's balance %s", format(amount), offlinePlayer.getName(),
				format(ps.getBalance() + ps.getBalanceChanges()));
		if (offlinePlayer.isOnline()) {
			ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() - amount));
			ps.setBalanceChanges(0);
		} else {
			ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - amount));
		}
		plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
	}

	/**
	 * Add amount to player balance in memory/database but NOT on in the players
	 * inventory
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public void addMoneyToPlayerBalance(OfflinePlayer offlinePlayer, double amount) {
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer);
		plugin.getMessages().debug("Adding %s to %s's balance %s", format(amount), offlinePlayer.getName(),
				format(ps.getBalance() + ps.getBalanceChanges()));
		if (offlinePlayer.isOnline()) {
			ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() + amount));
			ps.setBalanceChanges(0);
		} else {
			ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() + amount));
		}
		plugin.getPlayerBalanceManager().setPlayerBalance(offlinePlayer, ps);
	}

	/**
	 * Change the players amount of money in his inventory to the players balance in
	 * memory/database
	 * 
	 * @param player
	 */
	public void adjustAmountOfMoneyInInventoryToPlayerBalance(Player player) {

		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		double amountInInventory = getAmountInInventory(player);
		if (ps != null) {
			double diff = (Misc.round(ps.getBalance()) + Misc.round(ps.getBalanceChanges()))
					- Misc.round(amountInInventory);
			double space = getSpaceForMoney(player);
			if (diff > space) {
				plugin.getMessages().debug("Not enough space for the money. Space=%s", space);
				diff = space;
				ps.setBalance(ps.getBalance() + space);
			}
			if (Misc.round(diff) != 0)
				plugin.getMessages().debug("Adjusting amt to Balance: amt=%s, bal=%s(+%s)", amountInInventory,
						ps.getBalance(), ps.getBalanceChanges());
			if (Misc.round(diff) > 0) {
				plugin.getMessages().debug("Add %s money to balance", diff);
				addMoneyToPlayer(player, Misc.round(diff));
			} else if (Misc.round(diff) < 0) {
				plugin.getMessages().debug("remove %s money from balance", -diff);
				removeMoneyFromPlayer(player, -diff);
			} else
				plugin.getMessages().debug("There was no difference");
		}
	}

	/**
	 * Change the players balance in memory/database to the amount of money in his
	 * inventory
	 * 
	 * @param player
	 */
	public void adjustPlayerBalanceToAmounOfMoneyInInventory(Player player) {
		double amountInInventory = getAmountInInventory(player);
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		ItemStack is = player.getItemOnCursor();
		double inHand = 0;
		if (Reward.isReward(is)) {
			Reward reward = Reward.getReward(is);
			if (reward.isBagOfGoldReward() || reward.isItemReward())
				inHand = reward.getMoney();
		}
		if (ps != null) {
			double diff = Misc.round(amountInInventory + inHand)
					- (Misc.round(ps.getBalance()) + Misc.round(ps.getBalanceChanges()));
			// plugin.getMessages().debug("Adjusting Balance to amt: diff=%s", diff);
			if (Misc.round(diff) != 0)
				plugin.getMessages().debug("Adjusting Balance to amt: amt=%s, inHand=%s, bal=%s(+%s)",
						amountInInventory, inHand, ps.getBalance(), ps.getBalanceChanges());
			if (Misc.round(diff) > 0)
				addMoneyToPlayerBalance(player, Misc.round(diff));
			else if (Misc.round(diff) < 0)
				removeMoneyFromPlayerBalance(player, -diff);
			else
				plugin.getMessages().debug("there was no difference");

		}
	}

	/**
	 * Get the amount of money which the player has room for in his inventory.
	 * 
	 * @param player
	 * @return
	 */
	public double getSpaceForMoney(Player player) {
		if (plugin.getBagOfGoldItems().isBagOfGoldStyle())
			return plugin.getBagOfGoldItems().getSpaceForBagOfGoldMoney(player);
		else if (plugin.getGringottsItems().isGringottsStyle()) {
			return plugin.getGringottsItems().getSpaceForGringottsMoney(player);
		} else {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGOld] " + ChatColor.RED
							+ "Error in config.sys: unknown 'drop-money-on-ground-itemtype: "
							+ Core.getConfigManager().rewardItemtype + "'");
			return 0;
		}
	}

	/**
	 * Remove the Reward block from the world and clean up in saved rewards.
	 * 
	 * @param block
	 */
	public void removeReward(Block block) {
		if (Reward.isReward(block)) {
			Reward reward = Reward.getReward(block);
			block.getDrops().clear();
			block.setType(Material.AIR);
			block.removeMetadata(Reward.MH_REWARD_DATA_NEW, plugin);
			if (rewardBlocks.containsKey(reward.getUniqueID()))
				rewardBlocks.remove(reward.getUniqueID());
		}
	}

	public int getNextID() {
		int max = 0;
		for (int n : rewardBlocks.keySet())
			max = Math.max(max, n);
		return max + 1;
	}

	public void saveAllRewards() {
		plugin.getMessages().debug("Shutdown RewardManager: Saving all MobHunting Reward Blocks to disk");
		try {
			config.options().header(
					"This is the rewards placed as blocks. Do not edit this file manually! If you remove a section the reward will loose its value on next server restart.");
			for (Integer id : rewardBlocks.keySet()) {
				Location location = rewardBlocks.get(id).getLocation();
				if (location != null && Materials.isSkull(location.getBlock().getType())) {
					Reward reward = rewardBlocks.get(id).getReward();
					ConfigurationSection section = config.createSection(id.toString());
					section.set("location", location.clone());
					reward.save(section);
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveReward(Integer id) {
		try {
			config.options().header("This is the rewards placed as blocks. Do not edit this file manually!");
			if (rewardBlocks.containsKey(id)) {
				Location location = rewardBlocks.get(id).getLocation();
				if (location != null && Materials.isSkull(location.getBlock().getType())) {
					Reward reward = rewardBlocks.get(id).getReward();
					ConfigurationSection section = config.createSection(id.toString());
					section.set("location", rewardBlocks.get(id).getLocation());
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
				for (RewardBlock rb : rewardBlocks.values()) {
					if (rb.equals(new RewardBlock(location, reward)))
						continue;
				}
				if (location != null && Materials.isSkull(location.getBlock().getType())) {
					n++;
					reward.setUniqueID(n);
					location.getBlock().setMetadata(Reward.MH_REWARD_DATA_NEW,
							new FixedMetadataValue(plugin, new Reward(reward)));
					rewardBlocks.put(n, new RewardBlock(location, reward));
					if (Tools.isUUID(key)) {
						deleted++;
						config.set(key, null);
					}
				} else {
					// This block is not a PLAYER_HEAD, delete the section.
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
				plugin.getMessages().debug("Loaded %s rewards from the BagOfGold/rewards.yml file", n);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// This is needed when server owner is disabling/enabling MobHunting/BagOfGold
		// from time to time.
		loadAllStoredRewardsFromMobHunting();
	}

	private void loadAllStoredRewardsFromMobHunting() {
		YamlConfiguration mobhunting_config = new YamlConfiguration();
		int n = 0;
		File file = new File(plugin.getDataFolder().getParentFile(), "MobHunting/rewards.yml");

		if (!file.exists())
			return;

		try {
			mobhunting_config.load(file);
		} catch (IOException | InvalidConfigurationException e1) {
			e1.printStackTrace();
		}

		try {
			for (String key : mobhunting_config.getKeys(false)) {
				ConfigurationSection section = mobhunting_config.getConfigurationSection(key);
				Reward reward = new Reward();
				reward.read(section);
				Location location = (Location) section.get("location");
				for (RewardBlock rb : rewardBlocks.values()) {
					if (rb.equals(new RewardBlock(location, reward)))
						continue;
				}
				if (location != null && Materials.isSkull(location.getBlock().getType())) {
					n++;
					reward.setUniqueID(n);
					location.getBlock().setMetadata(Reward.MH_REWARD_DATA_NEW,
							new FixedMetadataValue(plugin, new Reward(reward)));
					rewardBlocks.put(n, new RewardBlock(location, reward));
				} else {
					// BagOfGold should not delete keys in the MobHunting file.
				}
			}
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		if (n > 0) {
			plugin.getMessages().debug("Loaded %s rewards from the MobHunting/rewards.yml file", n);
		}

	}

}
