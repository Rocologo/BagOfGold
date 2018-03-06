package one.lindegaard.BagOfGold;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
import one.lindegaard.BagOfGold.util.Misc;
//import one.lindegaard.CustomItemsLib.Util.Misc;
import one.lindegaard.MobHunting.MobHunting;

public class EconomyManager implements Listener {

	private BagOfGold plugin;

	public EconomyManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/**
	 * getBalance : calculate the player balance and checks if the player balance is
	 * equal with the amount of money in the inventory. If there is a difference it
	 * checks if there has been changes while the player was offline if not the
	 * balance / amount in inventory will be adjusted.
	 * 
	 * @param offlinePlayer
	 * @return
	 */
	public double getBalance(OfflinePlayer offlinePlayer) {
		// if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		if (offlinePlayer.isOnline()) {
			Player player = (Player) offlinePlayer;
			double amountInInventory = getAmountInInventory(player);
			if (player.getGameMode() != GameMode.SURVIVAL)
				return amountInInventory;

			if (Misc.round(amountInInventory) != Misc.round(ps.getBalance() + ps.getBalanceChanges())) {
				plugin.getMessages().debug("%s: inventory=%s, balance=%s, balancechanges=%s", player.getName(),
						format(amountInInventory), format(ps.getBalance()), format(ps.getBalanceChanges()));
				if (ps.getBalanceChanges() == 0) {
					plugin.getMessages().debug("Warning %s has a balance problem (%s,%s). Adjusting balance to %s",
							offlinePlayer.getName(), format(ps.getBalance()), format(amountInInventory),
							format(ps.getBalance() + ps.getBalanceChanges()));
					if (amountInInventory > ps.getBalance() + ps.getBalanceChanges()) {
						plugin.getMessages().debug("removing %s from playerinventory",
								format(amountInInventory - (ps.getBalance() + ps.getBalanceChanges())));
						plugin.getEconomyManager().removeBagOfGoldPlayer_EconomyManager(player,
								Misc.round(amountInInventory - (ps.getBalance() + ps.getBalanceChanges())));
					} else {
						plugin.getMessages().debug("adding %s to playerinventory",
								format(ps.getBalance() + ps.getBalanceChanges() - amountInInventory));
						plugin.getEconomyManager().addBagOfGoldPlayer_EconomyManager(player,
								Misc.round(ps.getBalance() + ps.getBalanceChanges() - amountInInventory));
					}
					if (Misc.round(ps.getBalance() + ps.getBalanceChanges()) >= 0)
						ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges()));
					else
						ps.setBalance(0);
					ps.setBalanceChanges(0);
				} else {
					plugin.getMessages().debug("%s's balance changed while offline (%s+%s). Adjusting balance to %s",
							offlinePlayer.getName(), format(ps.getBalance()), format(ps.getBalanceChanges()),
							format(ps.getBalance() + ps.getBalanceChanges()));
					double taken = 0;
					if (ps.getBalanceChanges() > 0) {
						plugin.getEconomyManager().addBagOfGoldPlayer_EconomyManager(player, ps.getBalanceChanges());
						ps.setBalanceChanges(0);
					} else {
						taken = plugin.getEconomyManager().removeBagOfGoldPlayer_EconomyManager(player,
								ps.getBalanceChanges());
						ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - taken));
					}
					ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges()));
				}
				plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
				plugin.getDataStoreManager().updatePlayerSettings(player, ps);
				return ps.getBalance() + ps.getBalanceChanges();
			} else {
				// player is online
				if (ps.getBalanceChanges() != 0) {
					plugin.getMessages().debug("Updating %s's balance %s with changes %s", offlinePlayer.getName(),
							format(ps.getBalance()), format(ps.getBalanceChanges()));
					ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges()));
					ps.setBalanceChanges(0);
					plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
					plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
				}
				return ps.getBalance() + ps.getBalanceChanges();
			}
		} else // player is known but not online
			return ps.getBalance() + ps.getBalanceChanges();
		// }
		// return 0;
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
	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		if (amount > 0) {
			if (offlinePlayer.isOnline()) {
				addBagOfGoldPlayer_EconomyManager((Player) offlinePlayer, ps.getBalanceChanges() + amount);
				if (((Player) offlinePlayer).getGameMode() == GameMode.SURVIVAL) {
					ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() + amount));
					ps.setBalanceChanges(0);
				} else {
					ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() + amount));
				}
			} else {
				ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() + amount));
			}
			plugin.getMessages().debug("Deposit %s to %s's account, new balance is %s", format(amount),
					offlinePlayer.getName(), format(ps.getBalance() + ps.getBalanceChanges()));
			plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
			return new EconomyResponse(amount, Misc.round(ps.getBalance() + ps.getBalanceChanges()),
					ResponseType.SUCCESS, null);
		} else {
			plugin.getMessages().debug("Could not deposit %s to %s's account, because the number is negative",
					format(amount), offlinePlayer.getName());
			return new EconomyResponse(0, Misc.round(ps.getBalance() + ps.getBalanceChanges()), ResponseType.FAILURE,
					null);
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
	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		if (amount > 0) {
			if (has(offlinePlayer, amount)) {
				if (offlinePlayer.isOnline()) {
					removeBagOfGoldPlayer_EconomyManager((Player) offlinePlayer, amount);
					if (((Player) offlinePlayer).getGameMode() == GameMode.SURVIVAL) {
						ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() - amount));
						ps.setBalanceChanges(0);
					} else {
						ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - amount));
					}
				} else
					ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - amount));
				plugin.getMessages().debug("Withdraw %s from %s's account, new balance is %s", format(amount),
						offlinePlayer.getName(), format(ps.getBalance() + ps.getBalanceChanges()));
				plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
				plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
				return new EconomyResponse(amount, Misc.round(ps.getBalance() + ps.getBalanceChanges()),
						ResponseType.SUCCESS, null);
			} else {
				double remove = ps.getBalance() + ps.getBalanceChanges();
				plugin.getMessages().debug("%s has not enough bagofgold, Withdrawing only %s , new balance is %s",
						offlinePlayer.getName(), format(remove), format(0));
				if (remove > 0) {
					removeBagOfGoldPlayer_EconomyManager((Player) offlinePlayer, remove);
					ps.setBalance(0);
					ps.setBalanceChanges(0);
					plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
					plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
				}
				return new EconomyResponse(remove, 0, ResponseType.FAILURE,
						plugin.getMessages().getString("bagofgold.commands.money.not-enough-money", "money", remove));
			}
		} else {
			return new EconomyResponse(0, ps.getBalance() + ps.getBalanceChanges(), ResponseType.SUCCESS, null);
		}
	}

	/**
	 * has : checks if the player has amount of mount on his balance.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 * @return true if the player has the amount on his money.
	 */
	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		if (offlinePlayer.isOnline() && ((Player) offlinePlayer).getGameMode() != GameMode.SURVIVAL)
			return false;
		plugin.getMessages().debug("Check if %s has %s %s on his balance=%s)", offlinePlayer.getName(), format(amount),
				plugin.getConfigManager().dropMoneyOnGroundSkullRewardName,
				format(plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer).getBalance()
						+ plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer).getBalanceChanges()));
		return plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer).getBalance()
				+ plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer).getBalanceChanges() >= amount;
	}

	/**
	 * addBagOfGoldPlayer_EconomyManager: add amount to the player inventory, but
	 * NOT on player balance.
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public void addBagOfGoldPlayer_EconomyManager(Player offlinePlayer, double amount) {
		Player player = ((Player) Bukkit.getServer().getOfflinePlayer(offlinePlayer.getUniqueId()));
		boolean found = false;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward rewardInSlot = Reward.getReward(is);
				if ((rewardInSlot.isBagOfGoldReward() || rewardInSlot.isItemReward())) {
					rewardInSlot.setMoney(rewardInSlot.getMoney() + amount);
					is = setDisplayNameAndHiddenLores(is, rewardInSlot);
					plugin.getMessages().debug(
							"Added %s to %s's item in slot %s, new value is %s (addBagOfGoldPlayer_EconomyManager)",
							format(amount), player.getName(), slot, format(rewardInSlot.getMoney()));
					found = true;
					break;
				}
			}
		}
		if (!found) {
			if (player.getInventory().firstEmpty() == -1)
				dropMoneyOnGround_EconomyManager(player, null, player.getLocation(), Misc.round(amount));
			else {
				ItemStack is;
				if (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL"))
					is = new CustomItems(plugin).getCustomtexture(UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID),
							plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
							plugin.getConfigManager().dropMoneyOnGroundSkullTextureValue,
							plugin.getConfigManager().dropMoneyOnGroundSkullTextureSignature, Misc.round(amount),
							UUID.randomUUID(), UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID));
				else {
					is = new ItemStack(Material.valueOf(plugin.getConfigManager().dropMoneyOnGroundItem), 1);
					setDisplayNameAndHiddenLores(is,
							new Reward(plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
									Misc.round(amount), UUID.fromString(Reward.MH_REWARD_ITEM_UUID), UUID.randomUUID(),
									null));
				}
				player.getInventory().addItem(is);
			}
		}
	}

	/**
	 * removeBagOfGoldPlayer_EconomyManager: remove the amount from the player
	 * inventory but NOT from the player balance.
	 * 
	 * @param player
	 * @param amount
	 * @return
	 */
	public double removeBagOfGoldPlayer_EconomyManager(Player player, double amount) {
		double taken = 0;
		if (player.getGameMode() != GameMode.SURVIVAL)
			amount = 0;
		double toBeTaken = Misc.round(amount);
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
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
				}
			}
		}

		return taken;

	}

	/**
	 * dropMoneyOnGround_EconomyManager: drop the amount of money in the location
	 * 
	 * @param player
	 *            - not used in EconomyManager
	 * @param killedEntity
	 *            - not used in EconomyManager
	 * @param location
	 * @param money
	 */
	public void dropMoneyOnGround_EconomyManager(Player player, Entity killedEntity, Location location, double money) {
		Item item = null;
		money = Misc.ceil(money);

		ItemStack is;
		UUID uuid = null, skinuuid = null;
		if (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")) {
			uuid = UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID);
			skinuuid = uuid;
			is = new CustomItems(plugin).getCustomtexture(uuid,
					plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
					plugin.getConfigManager().dropMoneyOnGroundSkullTextureValue,
					plugin.getConfigManager().dropMoneyOnGroundSkullTextureSignature, Misc.round(money),
					UUID.randomUUID(), skinuuid);
		} else { // ITEM
			uuid = UUID.fromString(Reward.MH_REWARD_ITEM_UUID);
			skinuuid = null;
			is = new ItemStack(Material.valueOf(plugin.getConfigManager().dropMoneyOnGroundItem), 1);
		}

		item = location.getWorld().dropItem(location, is);
		if (item != null) {
			MobHunting.getInstance().getRewardManager().getDroppedMoney().put(item.getEntityId(), Misc.round(money));
			item.setMetadata(Reward.MH_REWARD_DATA,
					new FixedMetadataValue(plugin,
							new Reward(
									plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM") ? ""
											: Reward.getReward(is).getDisplayname(),
									money, uuid, UUID.randomUUID(), skinuuid)));
			if (Misc.isMC18OrNewer()) {
				item.setCustomName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
						+ (plugin.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM") ? format(money)
								: Reward.getReward(is).getDisplayname() + " (" + format(Misc.round(money)) + ")"));
				item.setCustomNameVisible(true);
			}
			plugin.getMessages().debug("%s dropped %s on the ground as item %s (# of rewards=%s)", player.getName(),
					format(money), plugin.getConfigManager().dropMoneyOnGroundItemtype,
					MobHunting.getInstance().getRewardManager().getDroppedMoney().size());
		}
	}

	/**
	 * bankDeposit: deposit the amount on the account.
	 * 
	 * @param account
	 *            - This is the player UUID as a String
	 * @param amount
	 * @return EconomyResponse containing amount, balance and ResponseType
	 *         (Success/Failure).
	 */
	public EconomyResponse bankDeposit(String account, double amount) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(account));
		if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges() + amount));
				ps.setBankBalanceChanges(0);
			} else {
				ps.setBankBalanceChanges(ps.getBankBalanceChanges() + amount);
			}
			plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
			return new EconomyResponse(amount, ps.getBankBalance() + ps.getBankBalanceChanges(), ResponseType.SUCCESS,
					null);
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, "Player has no bank account");
	}

	/**
	 * bankWithdraw: withdraw the amount from the account.
	 * 
	 * @param account
	 *            - This is the player UUID as a String
	 * @param amount
	 * @return EconomyResponse containing amount, balance and ResponseType
	 *         (Success/Failure).
	 */
	public EconomyResponse bankWithdraw(String account, double amount) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(account));
		if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges() - amount));
				ps.setBankBalanceChanges(0);
			} else {
				ps.setBankBalanceChanges(ps.getBankBalanceChanges() - amount);
			}
			plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
			return new EconomyResponse(amount, ps.getBankBalance() + ps.getBankBalanceChanges(), ResponseType.SUCCESS,
					null);
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, offlinePlayer.getName() + " has no bank account");

	}

	/**
	 * bankBalance: withdraw the amount from the account.
	 * 
	 * @param account
	 *            - This is the player UUID as a String
	 * @return EconomyResponse containing amount, bank balance and ResponseType
	 *         (Success/Failure).
	 */
	public EconomyResponse bankBalance(String account) {
		OfflinePlayer offlinePlayer;
		if (Misc.isUUID(account))
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(account));
		else
			offlinePlayer = Bukkit.getOfflinePlayer(account);
		if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
			if (offlinePlayer.isOnline()) {
				ps.setBankBalance(Misc.round(ps.getBankBalance() + ps.getBankBalanceChanges()));
				ps.setBankBalanceChanges(0);
			}
			plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
			return new EconomyResponse(0, ps.getBankBalance() + ps.getBankBalanceChanges(), ResponseType.SUCCESS, null);
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, offlinePlayer.getName() + " has no bank account");

	}

	/**
	 * deleteBank: Reset the account and set the bank balance to 0.
	 * 
	 * @param account
	 *            - this is the player UUID.
	 * @return ResponseType (Success/Failure)
	 */
	public EconomyResponse deleteBank(String account) {
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(account));
		if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
			ps.setBankBalance(0);
			ps.setBankBalanceChanges(0);
			plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
			return new EconomyResponse(0, 0, ResponseType.SUCCESS, "Bank account deleted");
		}
		return new EconomyResponse(0, 0, ResponseType.FAILURE, offlinePlayer.getName() + " has no bank account");
	}

	/**
	 * Format the number
	 * 
	 * @param money
	 * @return
	 */
	public String format(double money) {
		return Misc.format(money);
	}

	/**
	 * setDisplayNameAndHiddenLores: add the Display name and the (hidden) Lores.
	 * The lores identifies the reward and contain secret information.
	 * 
	 * @param skull
	 *            - The base itemStack without the information.
	 * @param reward
	 *            - The reward information is added to the ItemStack
	 * @return the updated ItemStack.
	 */
	public ItemStack setDisplayNameAndHiddenLores(ItemStack skull, Reward reward) {
		ItemMeta skullMeta = skull.getItemMeta();
		skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + reward.getDisplayname(),
				"Hidden:" + reward.getMoney(), "Hidden:" + reward.getRewardUUID(),
				reward.getMoney() == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(), "Hidden:" + reward.getSkinUUID())));
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

	/**
	 * getAmountInInventory: calculate the total BagOfGold in the player inventory.
	 * 
	 * @param player
	 * @return
	 */
	public double getAmountInInventory(Player player) {
		double amountInInventory = 0;
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
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
	 * removeMoneyFromBalance: remove the amount from the player balance without
	 * removing amount from the player inventory
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public void removeMoneyFromBalance(OfflinePlayer offlinePlayer, double amount) {
		PlayerSettings ps = BagOfGold.getInstance().getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		plugin.getMessages().debug("Removing %s from %s's balance %s", format(amount), offlinePlayer.getName(),
				format(ps.getBalance() + ps.getBalanceChanges()));

		if (offlinePlayer.isOnline()) {
			Player player = (Player) offlinePlayer;
			if (player.getGameMode() == GameMode.SURVIVAL) {
				ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() - amount));
				ps.setBalanceChanges(0);
			} else {
				ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - amount));
			}
		} else {
			ps.setBalanceChanges(Misc.round(ps.getBalanceChanges() - amount));
		}
		plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
		plugin.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
	}

	/**
	 * addMoneyToBalance: add amount to player balance but NOT on in the player
	 * inventory
	 * 
	 * @param offlinePlayer
	 * @param amount
	 */
	public void addMoneyToBalance(OfflinePlayer offlinePlayer, double amount) {
		PlayerSettings ps = BagOfGold.getInstance().getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		plugin.getMessages().debug("Adding %s to %s's balance %s", format(amount), offlinePlayer.getName(),
				format(ps.getBalance() + ps.getBalanceChanges()));
		ps.setBalance(Misc.round(ps.getBalance() + ps.getBalanceChanges() + amount));
		if (!offlinePlayer.isOnline() || (((Player) offlinePlayer).getGameMode() == GameMode.SURVIVAL)) {
			BagOfGold.getInstance().getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			BagOfGold.getInstance().getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
		}
	}

	/**
	 * onPlayerGameModeChange: when called the player inventory will be checked if
	 * the amount on the balance is equal with the amount in inventory. If there is
	 * a difference the player inventory will be adjusted.
	 * 
	 * When the player changes to Survival GameMode rewards with 0 value will be
	 * removed.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		// Called before the gamemode change (NOT after)

		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (event.getNewGameMode() == GameMode.SURVIVAL) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(player);
			double amountInInventory = getAmountInInventory(player);
			if (Misc.round(amountInInventory) != Misc.round(ps.getBalance() + ps.getBalanceChanges())) {
				plugin.getMessages().debug("onPlayerGameModeChange: Adjusting %s's inventory balance to %s",
						player.getName(), format(amountInInventory));
				if (amountInInventory > ps.getBalance() + ps.getBalanceChanges())
					removeBagOfGoldPlayer_EconomyManager(player,
							Misc.round(amountInInventory - (ps.getBalance() + ps.getBalanceChanges())));
				else
					addBagOfGoldPlayer_EconomyManager(player,
							Misc.round(ps.getBalance() + ps.getBalanceChanges() - amountInInventory));
			}

			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				ItemStack is = player.getInventory().getItem(slot);
				if (Reward.isReward(is)) {
					Reward reward = Reward.getReward(is);
					if (reward.isBagOfGoldReward() || reward.isItemReward()) {
						if (reward.getMoney() == 0) {
							player.getInventory().clear(slot);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (player.getGameMode() != GameMode.SURVIVAL) {
			PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(player);
			ps.setBalance(0);
			ps.setBalanceChanges(0);
			plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
			plugin.getDataStoreManager().updatePlayerSettings(player, ps);
		}
	}

}
