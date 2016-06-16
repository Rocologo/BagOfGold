package one.lindegaard.MobHunting;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Log;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import one.lindegaard.MobHunting.util.Misc;

public class Rewards implements Listener {

	public static final String MH_MONEY = "MH:Money";

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupMoney(PlayerPickupItemEvent e) {
		double money = 0;
		Item item = e.getItem();
		if (item.hasMetadata(MH_MONEY)) {
			List<MetadataValue> metadata = item.getMetadata(MH_MONEY);
			for (MetadataValue mdv : metadata) {
				if (mdv.getOwningPlugin() == MobHunting.getInstance()) {
					money = (Double) mdv.value();
					Player player = e.getPlayer();
					MobHunting.getEconomy().depositPlayer(player, money);
					MobHunting.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup", "money",
							MobHunting.getEconomy().format(money)));
					e.getItem().remove();
					e.setCancelled(true);
					break;
				}
			}

		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMobPickupMoney(EntityInteractEvent e) {
		if (e.getEntity() instanceof Item) {
			Item item = (Item) e.getEntity();
			if (item.hasMetadata(MH_MONEY))
				MobHunting.debug("Rewards: EventInteractEvent MH_MONEY - %s, %s, %s ", e.getEntity().getType(),
						e.getEntityType(), e.getBlock().getType());
		}
		if (e.getEntity() instanceof Zombie){
			MobHunting.debug("A Zombie did something");
			Zombie z = (Zombie) e.getEntity();
			if (Misc.isMC19OrNewer()){
				MobHunting.debug("Zombie hands = %s,%s",z.getEquipment().getItemInMainHand(),z.getEquipment().getItemInOffHand());
			} else {
				MobHunting.debug("Zombie hand = %s",z.getEquipment().getItemInHand());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemMergeEvent(ItemMergeEvent e) {
		Item item1 = e.getEntity();
		Item item2 = e.getTarget();
		if (item1.hasMetadata(MH_MONEY) || item2.hasMetadata(MH_MONEY)) {
			double value1 = 0;
			if (item1.hasMetadata(MH_MONEY)) {
				value1 = item1.getMetadata(MH_MONEY).get(0).asDouble();
			}
			double value2 = 0;
			if (item2.hasMetadata(MH_MONEY)) {
				value2 = item2.getMetadata(MH_MONEY).get(0).asDouble();
			}
			item2.setMetadata(MH_MONEY, new FixedMetadataValue(MobHunting.getInstance(), value1 + value2));
			item2.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
					+ MobHunting.getEconomy().format(value1 + value2));
			item2.setCustomNameVisible(true);
			MobHunting.debug("Rewards: Items merged - new value=%s", value1 + value2);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onItemDespawnEvent(ItemDespawnEvent e) {
		if (e.getEntity().hasMetadata(MH_MONEY)) {
			MobHunting.debug("The money was lost - despawned");
			// e.getEntity().setCancelled(true);
			// too many items can cause lag, dont cancel this event.
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryPickupItemEvent(InventoryPickupItemEvent e) {
		Item item = e.getItem();
		if (item.hasMetadata(MH_MONEY)) {
			Bukkit.getServer().getLogger().warning("[MobHunting] WARNING! The money was picked up by "
					+ e.getInventory().getHolder().toString() + ", event was cancelled. Please show log to Developer.");
			e.setCancelled(true);
		}
	}

	public static void dropMoneyOnGround(Entity entity, double money) {
		Location location = entity.getLocation();
		ItemStack is = new ItemStack(Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem), 1);
		Item item = location.getWorld().dropItem(location, is);
		item.setMetadata(MH_MONEY, new FixedMetadataValue(MobHunting.getInstance(), money));
		item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
				+ MobHunting.getEconomy().format(money));
		item.setCustomNameVisible(true);
	}

}
