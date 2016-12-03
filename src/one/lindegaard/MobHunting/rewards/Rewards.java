package one.lindegaard.MobHunting.rewards;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.currency.Denomination;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.commands.HeadCommand;
import one.lindegaard.MobHunting.compatibility.GringottsCompat;
import one.lindegaard.MobHunting.compatibility.ProtocolLibCompat;
import one.lindegaard.MobHunting.compatibility.ProtocolLibHelper;
import one.lindegaard.MobHunting.util.Misc;

public class Rewards implements Listener {

	public static final String MH_MONEY = "MH:Money";

	public static void dropMoneyOnGround(Entity entity, double money) {
		if (GringottsCompat.isSupported()) {
			List<Denomination> denoms = Configuration.CONF.currency.denominations();
			int unit = Configuration.CONF.currency.unit;
			double rest = money;
			Location location = entity.getLocation();
			for (Denomination d : denoms) {
				ItemStack is = new ItemStack(d.key.type.getType(), 1);
				while (rest >= (d.value / unit)) {
					Item item = location.getWorld().dropItem(location, is);
					item.setMetadata(MH_MONEY, new FixedMetadataValue(MobHunting.getInstance(), (double) 0));
					rest = rest - (d.value / unit);
				}
			}
		} else {
			Location location = entity.getLocation();
			ItemStack is = new ItemStack(Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem), 1);
			Item item = location.getWorld().dropItem(location, is);
			item.setMetadata(MH_MONEY, new FixedMetadataValue(MobHunting.getInstance(), money));
			if (Misc.isMC18OrNewer()) {
				item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ MobHunting.getRewardManager().format(money));
				item.setCustomNameVisible(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPickupMoney(PlayerPickupItemEvent e) {
		// This event is NOT called when the inventory is full.
		double money = 0;
		Item item = e.getItem();
		if (item.hasMetadata(MH_MONEY)) {
			List<MetadataValue> metadata = item.getMetadata(MH_MONEY);
			for (MetadataValue mdv : metadata) {
				if (mdv.getOwningPlugin() == MobHunting.getInstance()) {
					money = (Double) mdv.value();
					Player player = e.getPlayer();
					// If not Gringotts
					if (money != 0) {
						MobHunting.getRewardManager().depositPlayer(player, money);
						Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup", "money",
								MobHunting.getRewardManager().format(money)));
						e.getItem().remove();
						e.setCancelled(true);
					}
					break;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onMobPickupMoney(EntityInteractEvent e) {
		Messages.debug("EntityInteractEvent was called on %s", e.getEntityType());
		if (e.getEntity() instanceof Item) {
			Item item = (Item) e.getEntity();
			if (item.hasMetadata(MH_MONEY))
				Messages.debug("Rewards: EventInteractEvent MH_MONEY - %s, %s, %s ", e.getEntity().getType(),
						e.getEntityType(), e.getBlock().getType());
		}
		if (e.getEntity() instanceof Zombie) {
			Zombie z = (Zombie) e.getEntity();
			if (e.getBlock().hasMetadata(HeadCommand.MH_HEAD))
				Messages.debug("A Zombie did something, with a MobHuntingHead %s", e.getBlock());
			if (Misc.isMC19OrNewer()) {
				if ((z.getEquipment().getItemInMainHand().hasItemMeta()
						&& z.getEquipment().getItemInMainHand().getItemMeta().hasLore()
						&& z.getEquipment().getItemInMainHand().getItemMeta().getLore().get(0)
								.equals(HeadCommand.MH_HEAD))
						|| (z.getEquipment().getItemInOffHand().hasItemMeta()
								&& z.getEquipment().getItemInOffHand().getItemMeta().hasLore() && z.getEquipment()
										.getItemInOffHand().getItemMeta().getLore().get(0).equals(HeadCommand.MH_HEAD)))
					Messages.debug("Zombie hands = %s,%s", z.getEquipment().getItemInMainHand(),
							z.getEquipment().getItemInOffHand());
			} else {
				if (z.getEquipment().getItemInHand().hasItemMeta()
						&& z.getEquipment().getItemInHand().getItemMeta().hasLore()
						&& z.getEquipment().getItemInHand().getItemMeta().getLore().get(0).equals(HeadCommand.MH_HEAD))
					Messages.debug("Zombie hand = %s", z.getEquipment().getItemInHand());
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onMoneyDespawnEvent(ItemDespawnEvent e) {
		if (e.getEntity().hasMetadata(MH_MONEY)) {
			Messages.debug("The money was lost - despawned");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryPickupMoneyEvent(InventoryPickupItemEvent e) {
		Item item = e.getItem();
		if (MobHunting.getConfigManager().denyHoppersToPickUpMoney && item.hasMetadata(MH_MONEY)
				&& e.getInventory().getType() == InventoryType.HOPPER) {
			Messages.debug("A %s tried to pick up the the reward, but this is disabled in config.yml",
					e.getInventory().getType());
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMoveOverMoneyEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (!player.getCanPickupItems()) {
			List<Entity> itemList = ((Entity) player).getNearbyEntities(1, 1, 1);
			double money = 0;
			for (Entity ent : itemList) {
				if (ent.hasMetadata(MH_MONEY)) {
					List<MetadataValue> metadata = ent.getMetadata(MH_MONEY);
					for (MetadataValue mdv : metadata) {
						if (mdv.getOwningPlugin() == MobHunting.getInstance()) {
							money = (Double) mdv.value();
							// If not Gringotts
							if (money != 0) {
								MobHunting.getRewardManager().depositPlayer(player, money);
								if (ProtocolLibCompat.isSupported())
									ProtocolLibHelper.pickupMoney(player, ent);
								ent.remove();
								Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup",
										"money", MobHunting.getRewardManager().format(money)));
							}
							break;
						}
					}

				}
			}
		}
	}

}
