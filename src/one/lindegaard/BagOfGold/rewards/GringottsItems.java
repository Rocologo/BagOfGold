package one.lindegaard.BagOfGold.rewards;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import one.lindegaard.BagOfGold.util.Misc;
import one.lindegaard.Core.Tools;

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

	public double getMoneyInHand(Player player) {
		double money = 0;
		ItemStack moneyInHand = player.getItemInHand();
		Iterator<Entry<String, String>> itr = plugin.getConfigManager().gringottsDenomination.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> pair = itr.next();
			try {
				if (Material.valueOf(pair.getKey()) == moneyInHand.getType()) {
					money = Double.valueOf(pair.getValue()) * moneyInHand.getAmount();
					plugin.getMessages().debug("Money in hans is %s", money);
					break;
				} else {
					plugin.getMessages().debug("This is not Gringotts money");
				}
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ " Could not read denomonation (" + pair.getKey() + "," + pair.getValue() + ")");
				break;
			}
		}
		return money;
	}

	public boolean hasMoney(Player player, double amount) {
		return getAmountOfGringottsMoneyInInventory(player) >= amount;
	}

	public boolean hasMoney(Player player, Material material, double amount) {
		return getAmountOfGringottsMoneyInInventory(player, material) >= amount;
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
			while (moneyLeftToGive >= value) {
				player.getInventory().addItem(new ItemStack(material));
				moneyLeftToGive = moneyLeftToGive - value;
				addedMoney = addedMoney + value;
			}
		}
		return addedMoney;
	}

	public double removeGringottsMoneyFromPlayer(Player player, double amount) {
		LinkedHashMap<String, String> denominations = new LinkedHashMap<>();
		denominations = plugin.getConfigManager().gringottsDenomination;
		Map<String, String> result = denominations.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));
		Iterator<Entry<String, String>> itr = result.entrySet().iterator();
		double taken = 0;
		double toBeTaken = Misc.round(amount);
		denomination: while (itr.hasNext()) {
			Entry<String, String> pair = itr.next();
			Material material;
			double value;
			try {
				material = Material.valueOf(pair.getKey());
				value = Double.valueOf(pair.getValue());
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ " Could not read denomonation (" + pair.getKey() + "," + pair.getValue() + ")");
				continue denomination;
			}
			while (taken >= 0) {
				int i = player.getInventory().first(material);
				if (i != -1) {
					ItemStack is = player.getInventory().getItem(i);
					toBeTaken = toBeTaken - value;
					taken = taken + value;
					if (is.getAmount() > 1) {
						is.setAmount(is.getAmount() - 1);
						player.getInventory().setItem(i, is);
					} else {
						player.getInventory().clear(i);
					}
				} else
					continue denomination;
			}
			if (taken < 0)
				addGringottsMoneyToPlayer(player, -taken);
		}
		return amount;
	}

	public void dropGringottsMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		double moneyLeftToDrop = Misc.ceil(money);
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

	public double getAmountOfGringottsMoneyInInventory(Player player, Material material) {
		double amountInInventory = 0;
		double value = 0;
		try {
			value = Double.valueOf(plugin.getConfigManager().gringottsDenomination.get(material.toString()));
		} catch (Exception e) {
			Bukkit.getConsoleSender()
					.sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + " Could not read denomonation ("
							+ material.name() + ","
							+ plugin.getConfigManager().gringottsDenomination.get(material.name()) + ")");
		}
		for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
			if (slot >= 36 && slot <= 40)
				continue;
			ItemStack is = player.getInventory().getItem(slot);
			if (is != null && is.getType() == material)
				amountInInventory = amountInInventory + is.getAmount() * value;
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

	public boolean isGringottsReward(Material material) {
		Iterator<Entry<String, String>> itr = plugin.getConfigManager().gringottsDenomination.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, String> pair = itr.next();
			Material mat;
			try {
				mat = Material.valueOf(pair.getKey());
				if (mat == material)
					return true;
			} catch (Exception e) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ " Could not read denomonation (" + pair.getKey() + "," + pair.getValue() + ")");
				break;
			}
		}
		return false;
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
			plugin.getRewardManager().removeMoneyFromPlayerBalance(player, amount);
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
			plugin.getRewardManager().removeMoneyFromPlayerBalance(player, amount);
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
			plugin.getRewardManager().addMoneyToPlayerBalance(player, amount);
		}
	}

}
