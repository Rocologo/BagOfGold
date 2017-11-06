package one.lindegaard.BagOfGold;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
import one.lindegaard.BagOfGold.util.Misc;
import one.lindegaard.MobHunting.MobHunting;

public class EconomyManager {

	private BagOfGold plugin;

	public EconomyManager(BagOfGold plugin) {
		this.plugin = plugin;
	}

	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		if (offlinePlayer.isOnline()) {
			depositBagOfGoldPlayer((Player) offlinePlayer, ps.getBalanceChanges() + amount);
			ps.setBalanceChanges(0);
		} else {
			ps.setBalanceChanges(ps.getBalanceChanges() + amount);
		}
		ps.setBalance(ps.getBalance() + amount);
		plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
		BagOfGold.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
		return new EconomyResponse(amount, ps.getBalance(), ResponseType.SUCCESS, null);
	}

	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		if (has(offlinePlayer, amount)) {
			if (offlinePlayer.isOnline()) {
				withdrawBagOfGoldPlayer((Player) offlinePlayer, ps.getBalanceChanges() - amount);
			} else {

				ps.setBalanceChanges(ps.getBalanceChanges() - amount);
			}
			ps.setBalance(ps.getBalance() - amount);
			plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			BagOfGold.getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
			return new EconomyResponse(amount, ps.getBalance(), ResponseType.SUCCESS, null);
		} else
			return new EconomyResponse(0, ps.getBalance(), ResponseType.FAILURE,
					Messages.getString("mobhunting.commands.money.not-enough-money", "money", ps.getBalance()));
	}

	public String format(double amount) {
		Locale locale = new Locale("en", "UK");
		String pattern = "#.#####";
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);
		return decimalFormat.format(amount);
	}

	public double getBalance(OfflinePlayer offlinePlayer) {

		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);

		if (offlinePlayer.isOnline()) {
			Player player = (Player) offlinePlayer;
			double sum = 0;
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				ItemStack is = player.getInventory().getItem(slot);
				if (Reward.isReward(is)) {
					Reward reward = Reward.getReward(is);
					if (reward.isBagOfGoldReward())
						sum = sum + reward.getMoney();
				}
			}
			if (ps.getBalance() + ps.getBalanceChanges() != sum) {
				if (ps.getBalanceChanges() == 0) {
					Messages.debug("Warning %s has a player balance problem (%s,%s). Adjusting balance to %s",
							offlinePlayer.getName(), ps.getBalance(), sum, sum);
					ps.setBalance(sum);
					ps.setBankBalanceChanges(0);
					plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
					BagOfGold.getDataStoreManager().updatePlayerSettings(player, ps.isLearningMode(), ps.isMuted(),
							ps.getBalance(), ps.getBalanceChanges(), ps.getBankBalance(), ps.getBankBalanceChanges());
				} else {
					Messages.debug(
							"Warning %s has a player balance changes while offline (%s+%s). Adjusting balance to %s",
							offlinePlayer.getName(), ps.getBalance(), ps.getBalanceChanges(),
							ps.getBalance() + ps.getBalanceChanges());
					double taken = 0;
					if (ps.getBalanceChanges() > 0)
						depositBagOfGoldPlayer(player, ps.getBalanceChanges());
					else
						withdrawBagOfGoldPlayer(player, ps.getBalanceChanges());
					ps.setBalanceChanges(ps.getBalanceChanges() + taken);
					ps.setBalance(ps.getBalance() + ps.getBalanceChanges());
					plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);
					BagOfGold.getDataStoreManager().updatePlayerSettings(player, ps.isLearningMode(), ps.isMuted(),
							ps.getBalance(), ps.getBalanceChanges(), ps.getBankBalance(), ps.getBankBalanceChanges());
				}
			}
		}
		return ps.getBalance() + ps.getBalanceChanges();
	}

	public void depositBagOfGoldPlayer(OfflinePlayer offlinePlayer, double amount) {
		CustomItems customItems = new CustomItems(plugin);
		Player player = ((Player) Bukkit.getServer().getOfflinePlayer(offlinePlayer.getUniqueId()));
		boolean found = false;
		if (player.getInventory().firstEmpty() == -1)
			dropMoneyOnGround(player, null, player.getLocation(), Misc.floor(amount));
		else {
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				ItemStack is = player.getInventory().getItem(slot);
				if (Reward.isReward(is)) {
					Reward rewardInSlot = Reward.getReward(is);
					if ((rewardInSlot.isBagOfGoldReward() || rewardInSlot.isItemReward())) {
						rewardInSlot.setMoney(rewardInSlot.getMoney() + amount);
						ItemMeta im = is.getItemMeta();
						im.setLore(rewardInSlot.getHiddenLore());
						String displayName = BagOfGold.getConfigManager().dropMoneyOnGroundItemtype
								.equalsIgnoreCase("ITEM") ? plugin.getEconomyManager().format(rewardInSlot.getMoney())
										: rewardInSlot.getDisplayname() + " ("
												+ plugin.getEconomyManager().format(rewardInSlot.getMoney()) + ")";
						im.setDisplayName(ChatColor.valueOf(BagOfGold.getConfigManager().dropMoneyOnGroundTextColor)
								+ displayName);
						is.setItemMeta(im);
						is.setAmount(1);
						Messages.debug("Added %s to item in slot %s, new value is %s",
								plugin.getEconomyManager().format(amount), slot,
								plugin.getEconomyManager().format(rewardInSlot.getMoney()));
						found = true;
						break;
					}
				}
			}
			if (!found) {
				ItemStack is = customItems.getCustomtexture(UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID),
						BagOfGold.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
						BagOfGold.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						BagOfGold.getConfigManager().dropMoneyOnGroundSkullTextureSignature, Misc.floor(amount),
						UUID.randomUUID(), UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID));
				player.getInventory().addItem(is);
			}
		}
		plugin.getMessages().playerSendMessage(player,
				Messages.getString("mobhunting.commands.money.give", "rewardname",
						BagOfGold.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(), "money",
						plugin.getEconomyManager().format(Misc.floor(amount))));
	}

	public double withdrawBagOfGoldPlayer(Player player, double amount) {
		double taken = 0;
		double toBeTaken = Misc.floor(amount);
		CustomItems customItems = new CustomItems(plugin);
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			ItemStack is = player.getInventory().getItem(slot);
			if (Reward.isReward(is)) {
				Reward reward = Reward.getReward(is);
				if (reward.isBagOfGoldReward()) {
					double saldo = Misc.floor(reward.getMoney());
					if (saldo > toBeTaken) {
						reward.setMoney(saldo - toBeTaken);
						is = customItems.getCustomtexture(reward.getRewardUUID(),
								BagOfGold.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
								BagOfGold.getConfigManager().dropMoneyOnGroundSkullTextureValue,
								BagOfGold.getConfigManager().dropMoneyOnGroundSkullTextureSignature, saldo - toBeTaken,
								UUID.randomUUID(), reward.getSkinUUID());
						player.getInventory().setItem(slot, is);
						taken = taken + toBeTaken;
						toBeTaken = 0;
						return taken;
					} else {
						is.setItemMeta(null);
						is.setType(Material.AIR);
						is.setAmount(0);
						player.getInventory().setItem(slot, is);
						taken = taken + saldo;
						toBeTaken = toBeTaken - saldo;
						return taken;
					}
				}
			}
		}

		return amount;

	}

	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		return plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer).getBalance() >= amount;
	}

	public void dropMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		Item item = null;
		money = Misc.ceil(money);

		ItemStack is;
		UUID uuid = null, skinuuid = null;
		// if
		// (BagOfGold.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLED"))
		// {
		// MinecraftMob mob = MinecraftMob.getMinecraftMobType(killedEntity);
		// uuid = UUID.fromString(Reward.MH_REWARD_KILLED_UUID);
		// skinuuid = mob.getPlayerUUID();
		// is = new CustomItems(plugin).getCustomHead(mob,
		// mob.getFriendlyName(), 1, money, skinuuid);
		// } else
		if (BagOfGold.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")) {
			uuid = UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID);
			skinuuid = uuid;
			is = new CustomItems(plugin).getCustomtexture(uuid,
					BagOfGold.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
					BagOfGold.getConfigManager().dropMoneyOnGroundSkullTextureValue,
					BagOfGold.getConfigManager().dropMoneyOnGroundSkullTextureSignature, money, UUID.randomUUID(),
					skinuuid);

		} else if (BagOfGold.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLER")) {
			uuid = UUID.fromString(Reward.MH_REWARD_KILLER_UUID);
			skinuuid = player.getUniqueId();
			is = new CustomItems(plugin).getPlayerHead(player.getUniqueId(), 1, money);

		} else { // ITEM
			uuid = UUID.fromString(Reward.MH_REWARD_ITEM_UUID);
			skinuuid = null;
			is = new ItemStack(Material.valueOf(BagOfGold.getConfigManager().dropMoneyOnGroundItem), 1);
		}

		item = location.getWorld().dropItem(location, is);
		MobHunting.getInstance().getRewardManager().getDroppedMoney().put(item.getEntityId(), money);
		item.setMetadata(Reward.MH_REWARD_DATA,
				new FixedMetadataValue(plugin,
						new Reward(
								BagOfGold.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM") ? ""
										: Reward.getReward(is).getDisplayname(),
								money, uuid, UUID.randomUUID(), skinuuid)));
		if (Misc.isMC18OrNewer()) {
			item.setCustomName(ChatColor.valueOf(BagOfGold.getConfigManager().dropMoneyOnGroundTextColor)
					+ (BagOfGold.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
							? plugin.getEconomyManager().format(money)
							: Reward.getReward(is).getDisplayname() + " (" + plugin.getEconomyManager().format(money)
									+ ")"));
			item.setCustomNameVisible(true);
		}
		if (item != null)
			Messages.debug("%s was dropped on the ground as item %s (# of rewards=%s)", format(money),
					BagOfGold.getConfigManager().dropMoneyOnGroundItemtype,
					MobHunting.getInstance().getRewardManager().getDroppedMoney().size());
	}

}
