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
import org.bukkit.event.inventory.InventoryCloseEvent;
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

		}

	}

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTradeSelectEvent(TradeSelectEvent event) {
		if (event.isCancelled())
			return;

		MerchantInventory inv = event.getInventory();
		Player buyer = (Player) event.getWhoClicked();
		MerchantRecipe recipe = inv.getMerchant().getRecipe(event.getIndex());
		ItemStack is0 = recipe.getIngredients().get(0);
		ItemStack is1 = recipe.getIngredients().get(1);
		ItemStack isResult = recipe.getResult();

		Core.getMessages().debug("TradeSelectEvent: Player=%s, index=%s (%sx%s,%sx%s,%sx%s) - inv=(%s,%s,%s), uses=%s, maxuses=%s, SpecialPrice=%s, PriceMultiplier=%s, demand=%s ",
				buyer.getName(), event.getIndex(), is0.getAmount(), is0.getType(), is1.getAmount(), is1.getType(),
				isResult.getAmount(), isResult.getType(),
				inv.getItem(0) != null ? inv.getItem(0).getType() : "null",
				inv.getItem(1) != null ? inv.getItem(1).getType() : "null",
				inv.getItem(2) != null ? inv.getItem(2).getType() : "null", recipe.getUses(), recipe.getMaxUses(),
						recipe.getSpecialPrice(),recipe.getPriceMultiplier(),recipe.getDemand());

		// Ingrediens0
		boolean found0=false;
		if (Reward.isReward(is0)) {
			double isMoney0 = (Reward.isReward(is0) ? Reward.getReward(is0).getMoney() * is0.getAmount() : 0);
			if (inv.getItem(0)==null) {
				Core.getMessages().debug("TradeSelectEvent: setItem0");
				inv.setItem(0,is0);
				BagOfGold.getInstance().getEconomyManager().withdrawPlayer(buyer, isMoney0*is0.getAmount());
				found0=true;
			}
		}
		
		// Ingrediens1		
		boolean found1=false;
		if (Reward.isReward(is1)) {
			double isMoney1 = (Reward.isReward(is1) ? Reward.getReward(is1).getMoney() * is1.getAmount() : 0);
			if (inv.getItem(1)==null) {
				Core.getMessages().debug("TradeSelectEvent: setItem1");
				inv.setItem(1,is1);
				BagOfGold.getInstance().getEconomyManager().withdrawPlayer(buyer, isMoney1*is1.getAmount());
				found1=true;
			}
		}
		
		// RESULT
		if (Reward.isReward(isResult)) {
			double isResultMoney = (Reward.isReward(isResult) ? Reward.getReward(isResult).getMoney() * isResult.getAmount() : 0);
			if (inv.getItem(2)==null) {
				
				
			}
		} else {
			if ((found0||found1) && inv.getItem(2)==null) {
				inv.setItem(2, isResult);
				//event.getWhoClicked().getInventory().first(isResult);
			}
		}
		//buyer.updateInventory();
			
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		
		if (event.getInventory().getType() == InventoryType.MERCHANT) {
			MerchantInventory inventory = (MerchantInventory) event.getInventory();
			ItemStack is0 = inventory.getItem(0);
			if (Reward.isReward(is0)) {
				Reward reward0 =Reward.getReward(is0);
				if (reward0.isMoney()) {
					BagOfGold.getInstance().getRewardManager().addMoneyToPlayerBalance(player, reward0.getMoney()*is0.getAmount());
				}
			}
		}
	}
	
/**

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTradeSelectEvent(TradeSelectEvent event) {
		if (event.isCancelled())
			return;

		MerchantInventory inv = event.getInventory();
		Player buyer = (Player) event.getWhoClicked();
		MerchantRecipe recipe = inv.getMerchant().getRecipe(event.getIndex());
		ItemStack is0 = recipe.getIngredients().get(0);
		ItemStack is1 = recipe.getIngredients().get(1);
		ItemStack isResult = recipe.getResult();

		if (Reward.isReward(is0) || Reward.isReward(is1) || Reward.isReward(isResult)) {
			
			event.setCancelled(true);
			
			Core.getMessages().debug("TradeSelectEvent: Player=%s, index=%s (%sx%s,%sx%s,%sx%s) - inv=(%s,%s,%s), uses=%s, maxuses=%s ",
					buyer.getName(), event.getIndex(), is0.getAmount(), is0.getType(), is1.getAmount(), is1.getType(),
					isResult.getAmount(), isResult.getType(),
					inv.getItem(0) != null ? inv.getItem(0).getType() : "null",
					inv.getItem(1) != null ? inv.getItem(1).getType() : "null",
					inv.getItem(2) != null ? inv.getItem(2).getType() : "null", recipe.getUses(), recipe.getMaxUses());
			
			double isMoney01 = (Reward.isReward(is0) ? Reward.getReward(is0).getMoney() * is0.getAmount() : 0)
					+ (Reward.isReward(is1) ? Reward.getReward(is1).getMoney() * is1.getAmount() : 0);
			
			if (BagOfGold.getInstance().getEconomyManager().hasMoney(buyer, isMoney01)) {
				if (is0 != null) {
					if (inv.getItem(0) == null)
						inv.setItem(0, is0);
					else if (Reward.isReward(is0) && Reward.getReward(is0).isMoney()) {
						BagOfGold.getInstance().getEconomyManager().depositPlayer(buyer,
								Reward.getReward(is0).getMoney());
					} else {
						buyer.getInventory().addItem(is0);
						
					}
				}
				if (is1 != null) {
					if (inv.getItem(1) == null)
						inv.setItem(1, is1);
					else if (Reward.isReward(is1) && Reward.getReward(is1).isMoney()) {
						BagOfGold.getInstance().getEconomyManager().depositPlayer(buyer,
								Reward.getReward(is1).getMoney());
					} else {
						buyer.getInventory().addItem(is1);
					}
				}
				BagOfGold.getInstance().getEconomyManager().withdrawPlayer(buyer, isMoney01);
			}
			if (isResult != null)
				inv.setItem(2, isResult);
		}
	}
**/
}
