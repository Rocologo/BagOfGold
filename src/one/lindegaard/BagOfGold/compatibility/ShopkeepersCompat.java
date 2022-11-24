package one.lindegaard.BagOfGold.compatibility;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import net.sf.antcontrib.logic.ForEach;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.Tools;
import one.lindegaard.CustomItemsLib.compatibility.BagOfGoldCompat;
import one.lindegaard.CustomItemsLib.compatibility.CompatPlugin;
import one.lindegaard.CustomItemsLib.rewards.Reward;
import one.lindegaard.CustomItemsLib.server.Servers;

public class ShopkeepersCompat implements Listener {

	BagOfGold plugin;
	private static Plugin mPlugin;
	private static boolean supported = false;

	// https://www.spigotmc.org/resources/shopkeepers.80756/

	public ShopkeepersCompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ "Compatibility with Shopkeepers is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.Shopkeepers.getName());
			if (getShopkeepers().getDescription().getVersion().compareTo("2.16.1") < 0) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
						+ "Your current version of Shopkeepers (" + mPlugin.getDescription().getVersion()
						+ ") is not supported by BagOfGold. BagOfGold does only support version 2.16.1 or newer.");
			} else {
				Bukkit.getConsoleSender()
						.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
								+ "Enabling compatibility with Shopkeepers ("
								+ getShopkeepers().getDescription().getVersion() + ")");
				supported = true;
			}
		}
		Bukkit.getPluginManager().registerEvents(this, BagOfGold.getInstance());
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getShopkeepers() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationShopkeepersBETA;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClickReward(InventoryClickEvent event) {

		if (event.isCancelled() || event.getInventory() == null)
			return;

		InventoryAction action = event.getAction();
		if (action == InventoryAction.NOTHING)
			return;

		ClickType clickType = event.getClick();

		Player player = (Player) event.getWhoClicked();

		ItemStack isCurrentSlot = event.getCurrentItem() != null ? event.getCurrentItem().clone() : null;
		ItemStack isCursor = event.getCursor() != null ? event.getCursor().clone() : null;
		ItemStack isNumberKey = clickType == ClickType.NUMBER_KEY
				? event.getWhoClicked().getInventory().getItem(event.getHotbarButton())
				: event.getCurrentItem();
		ItemStack isSwapOffhand = clickType == ClickType.SWAP_OFFHAND
				? event.getWhoClicked().getInventory().getItem(EquipmentSlot.OFF_HAND)
				: event.getCurrentItem();

		SlotType slotType = event.getSlotType();
		Inventory inventory = event.getInventory();
		Inventory clickedInventory = Servers.isMC113OrNewer() ? event.getClickedInventory() : inventory;

		if (inventory.getType() == InventoryType.MERCHANT && BagOfGoldCompat.isSupported()
				&& ShopkeepersCompat.isSupported()) {
			Core.getMessages().debug(
					"action=%s, InvType=%s, clickedInvType=%s, slottype=%s, slotno=%s, current=%s, cursor=%s, view=%s, keyboardClick=%s, numberKey=%s, swap_hand=%s",
					action, inventory.getType(), clickedInventory == null ? "null" : clickedInventory.getType(),
					slotType, event.getSlot(), isCurrentSlot == null ? "null" : isCurrentSlot.getType(),
					isCursor == null ? "null" : isCursor.getType(), event.getView().getType(),
					event.getClick().isKeyboardClick(), isNumberKey == null ? "null" : isNumberKey.getType(),
					isSwapOffhand == null ? "null" : isSwapOffhand.getType());

			MerchantInventory inv = (MerchantInventory) event.getInventory();

			// for (ItemStack itemStack : inv.getSelectedRecipe().getIngredients()) {

			// if (Reward.isReward(itemStack)) {
			// Reward reward = Reward.getReward(itemStack);
			// Core.getMessages().debug("A bagofgold was traded to something");

			// }

			// }
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTradeSelectEvent(TradeSelectEvent event) {
		if (event.isCancelled())
			return;
		HumanEntity trader = event.getMerchant().getTrader();
		MerchantInventory inv = event.getInventory();
		Player buyer = (Player) event.getWhoClicked();
		MerchantRecipe recipe = inv.getMerchant().getRecipe(event.getIndex());
		List<ItemStack> ingredients = recipe.getIngredients();

		Core.getMessages().debug("TradeSelectEvent: Trader=%s, Player=%s, index=%s, result=%s", trader.getName(),
				buyer.getName(), event.getIndex(), event.getResult().toString());
		Core.getMessages().debug("TradeSelectEvent: RecipeInventorySize=%s, TraderInventorySize=%s", inv.getSize(),
				event.getMerchant().getTrader().getInventory().getSize());

		double cost = 0;
		int antal = ingredients.size();
		for (int n = 0; n < antal; n++) {
			ItemStack is = ingredients.get(n);
			if (Reward.isReward(is)) {
				Reward reward = Reward.getReward(is);
				Core.getMessages().debug(
						"TradeSelectEvent: %s recipe contains a reward with value %s (amount=%s) in slot=%s",
						buyer.getName(), reward.getMoney(), is.getAmount(), n);

				boolean found=false;
				for (int n2 = 0; n2 < event.getWhoClicked().getInventory().getSize(); n2++) {
					if (Reward.isReward(event.getWhoClicked().getInventory().getItem(n2))) {
						Reward bag = Reward.getReward(event.getWhoClicked().getInventory().getItem(n2));
						if (bag.getMoney() >= reward.getMoney()) {
							Core.getMessages().debug("Found reward in slot=%s (value=%s)", n2, bag.getMoney());
							
							bag.setMoney(bag.getMoney() - reward.getMoney());
							if (bag.getMoney() > 0) {
								ItemStack isBag = Reward.setDisplayNameAndHiddenLores(is, bag);
								event.getWhoClicked().getInventory().setItem(n2, isBag);
							} else {
								event.getWhoClicked().getInventory().clear(n2);
							}

							Core.getMessages().debug("New value = %s (recipe.result=%s)", bag.getMoney(),recipe.getResult().getType());
							found=true;
							inv.setItem(0, recipe.getIngredients().get(0));
							//inv.setItem(1, recipe.getIngredients().get(1));
							inv.setItem(2, recipe.getResult());
							//BagOfGold.getInstance().getRewardManager().removeMoneyFromPlayer(buyer, bag.getMoney());
							break;
						}

					}
				}
				if (!found) {
					Core.getMessages().debug("No money found");
					event.setCancelled(true);
				}
				//double taken = Core.getCoreRewardManager().removeBagOfGoldFromPlayer((Player) event.getWhoClicked(),
				//		cost);
				//Core.getMessages().debug("taken=%s", taken);
				cost = cost + reward.getMoney();
			}
		}

		if (Reward.isReward(recipe.getResult())) {
			Reward reward = Reward.getReward(recipe.getResult());
			recipe.setPriceMultiplier((float) reward.getMoney());
			Core.getMessages().debug(
					"TradeSelectEvent: the trade is resulting in BagOfGold with value: %s, uses=%s, PriceMultiplier=%s",
					reward.getMoney(), recipe.getUses(), recipe.getPriceMultiplier());
		} else if (recipe.getResult() != null && cost != 0) {
			Core.getMessages().debug("%s choose a recipe containing bagofgold (value=%s)a to get  %s", buyer.getName(),
					cost, recipe.getResult().getType());
		}

	}

}
