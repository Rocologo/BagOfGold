package one.lindegaard.BagOfGold.rewards;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGoldCore.Tools;

public class GringottsItems implements Listener {

	BagOfGold plugin;

	public GringottsItems(BagOfGold plugin) {
		this.plugin = plugin;
		if (isGringottsStyle())
			Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public boolean isGringottsStyle() {
		return plugin.getConfigManager().dropMoneyOnGroundItemtype.equals("GRINGOTTS_STYLE");
	}
	
	public String format(double money) {
		return Tools.format(money);
	}

	public double addGringottsMoneyToPlayer(Player player, double amount) {
		double moneyLeftToGive = amount;
		double addedMoney = 0;
		Iterator<Entry<String, String>> itr = plugin.getConfigManager().gringottsDenomination.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> pair = itr.next();
			Material material;
			double value;
			try {
				material = Material.valueOf(pair.getKey());
				value = Double.valueOf(pair.getValue());
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ " Could not read denomonation (" + pair.getKey() + "," + pair.getValue() + ")");
				break;
			}
			plugin.getMessages().debug("addBagOfGoldPlayer, Material=%s value=%s", material, value);
			while (moneyLeftToGive >= value) {
				player.getInventory().addItem(new ItemStack(material));
				moneyLeftToGive = moneyLeftToGive - value;
				addedMoney = addedMoney + value;
			}
		}
		return addedMoney;
	}

	public double removeGringottsMoneyFromPlayer(Player player, double amount) {
		double taken = 0;
		double toBeTaken = Tools.round(amount);
		Iterator<Entry<String, String>> itr = plugin.getConfigManager().gringottsDenomination.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> pair = itr.next();
			Material material;
			double value;
			try {
				material = Material.valueOf(pair.getKey());
				value = Double.valueOf(pair.getValue());
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ " Could not read denomonation (" + pair.getKey() + "," + pair.getValue() + ")");
				continue;
			}
			while (toBeTaken >= value) {
				plugin.getMessages().debug("removeBagOfGoldPlayer, Material=%s, value=%s, ToBetaken=%s, taken=%s",
						material, value, toBeTaken, taken);
				int i = player.getInventory().first(material);
				ItemStack is = player.getInventory().getItem(i);
				toBeTaken = toBeTaken - value;
				taken = taken + value;
				if (is.getAmount() > 1) {
					is.setAmount(is.getAmount() - 1);
					player.getInventory().setItem(i, is);
				} else {
					player.getInventory().clear(i);
				}
			}
		}
		return taken;
	}

	public void dropGringottsMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		double moneyLeftToDrop = Tools.ceil(money);
		double droppedMoney = 0;
		Iterator<Entry<String, String>> itr = plugin.getConfigManager().gringottsDenomination.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> pair = itr.next();
			Material material;
			double value;
			try {
				material = Material.valueOf(pair.getKey());
				value = Double.valueOf(pair.getValue());
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ " Could not read denomonation (" + pair.getKey() + "," + pair.getValue() + ")");
				break;
			}
			plugin.getMessages().debug("dropGringottsMoneyOnGround, Material=%s value=%s", material, value);
			while (moneyLeftToDrop >= value) {
				ItemStack is = new ItemStack(material);
				location.getWorld().dropItem(location, is);
				moneyLeftToDrop = moneyLeftToDrop - value;
				droppedMoney = droppedMoney + value;
			}
		}
	}

	public double getAmountOfGringottsMoneyInInventory(Player player) {
		double amountInInventory = 0;
		Iterator<Entry<String, String>> itr = plugin.getConfigManager().gringottsDenomination.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> pair = itr.next();
			Material material;
			double value;
			try {
				material = Material.valueOf(pair.getKey());
				value = Double.valueOf(pair.getValue());
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ " Could not read denomonation (" + pair.getKey() + "," + pair.getValue() + ")");
				break;
			}
			plugin.getMessages().debug("getAmountInInventory, Material=%s value=%s", material, value);
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				if (slot >= 36 && slot <= 40)
					continue;
				ItemStack is = player.getInventory().getItem(slot);
				if (is != null && is.getType() == material)
					amountInInventory = amountInInventory + is.getAmount() * value;
			}
		}
		return amountInInventory;
	}

	public double getSpaceForGringottsMoney(Player player) {
		double space = 0;
		double maxValue = 0;
		Iterator<Entry<String, String>> itr = plugin.getConfigManager().gringottsDenomination.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> pair = itr.next();
			Material material;
			double value;
			try {
				material = Material.valueOf(pair.getKey());
				value = Double.valueOf(pair.getValue());
				if (value > maxValue)
					maxValue = value;
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ " Could not read denomonation (" + pair.getKey() + "," + pair.getValue() + ")");
				break;
			}
			plugin.getMessages().debug("getSpaceForMoney, Material=%s value=%s", material, value);
			for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
				if (slot >= 36 && slot <= 40)
					continue;
				ItemStack is = player.getInventory().getItem(slot);
				if (is != null && is.getType() == material)
					space = space + (64 - is.getAmount()) * value;
			}
		}
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (is == null || is.getType() == Material.AIR) {
				space = space + 64 * maxValue;
			}
		}
		plugin.getMessages().debug("%s has room for %s Gringotts money in the inventory", player.getName(), space);
		return space;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerDropReward(PlayerDropItemEvent event) {
		if (event.isCancelled())
			return;

		ItemStack is = event.getItemDrop().getItemStack();
		if (plugin.getConfigManager().gringottsDenomination.containsKey(is.getType().toString())) {
			Player player = event.getPlayer();
			double amount = Double.valueOf(plugin.getConfigManager().gringottsDenomination.get(is.getType().toString()))
					* is.getAmount();
			plugin.getMessages().debug("%s dropped a %s with a value of %s", player.getName(), is.getType().toString(),
					amount);
			plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, amount);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onRewardBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		ItemStack is = event.getItemInHand();

		if (plugin.getConfigManager().gringottsDenomination.containsKey(is.getType().toString())) {
			plugin.getMessages().debug("%s placed a %s with a value of %s", player.getName(), is.getType().toString(),
					plugin.getConfigManager().gringottsDenomination.get(is.getType().toString()));

			double amount = Double
					.valueOf(plugin.getConfigManager().gringottsDenomination.get(is.getType().toString()));
			plugin.getEconomyManager().removeMoneyFromPlayerBalance(player, amount);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		// OBS: EntityPickupItemEvent does only exist in MC1.12 and newer

		if (event.isCancelled())
			return;

		if (!(event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getEntity();
		ItemStack is = event.getItem().getItemStack();
		if (plugin.getConfigManager().gringottsDenomination.containsKey(is.getType().toString())) {
			plugin.getMessages().debug("%s picked up a %s with a value of %s", player.getName(),
					is.getType().toString(),
					plugin.getConfigManager().gringottsDenomination.get(is.getType().toString()));
			double amount = Double.valueOf(plugin.getConfigManager().gringottsDenomination.get(is.getType().toString()))
					* is.getAmount();
			plugin.getEconomyManager().addMoneyToPlayerBalance(player, amount);
		}
	}

}
