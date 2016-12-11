package one.lindegaard.MobHunting.rewards;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.metadata.MetadataValue;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.commands.HeadCommand;
import one.lindegaard.MobHunting.compatibility.ProtocolLibCompat;
import one.lindegaard.MobHunting.compatibility.ProtocolLibHelper;
import one.lindegaard.MobHunting.util.Misc;

public class RewardListeners implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupMoney(PlayerPickupItemEvent e) {
		// This event is NOT called when the inventory is full.
		double money = 0;
		Item item = e.getItem();
		if (item.hasMetadata(RewardManager.MH_MONEY)) {
			List<MetadataValue> metadata = item.getMetadata(RewardManager.MH_MONEY);
			for (MetadataValue mdv : metadata) {
				if (mdv.getOwningPlugin() == MobHunting.getInstance()) {
					money = (Double) mdv.value();
					Player player = e.getPlayer();
					// If not Gringotts
					if (money != 0) {
						MobHunting.getRewardManager().depositPlayer(player, money);
						Messages.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup", "money",
								MobHunting.getRewardManager().format(money)));
						item.remove();
						e.setCancelled(true);
					}
					if (RewardManager.getDroppedMoney().containsKey(item.getEntityId()))
						RewardManager.getDroppedMoney().remove(item.getEntityId());
					Messages.debug("%s picked up %s money. (# of rewards left=%s)", player.getName(),
							MobHunting.getRewardManager().format(money), RewardManager.getDroppedMoney().size());
					break;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onMobPickupMoney(EntityInteractEvent e) {
		if (e.getEntity() instanceof Item) {
			Item item = (Item) e.getEntity();
			if (item.hasMetadata(RewardManager.MH_MONEY))
				Messages.debug("RewardListeners: EventInteractEvent MH_MONEY - %s, %s, %s ", e.getEntity().getType(),
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
		if (e.getEntity().hasMetadata(RewardManager.MH_MONEY)) {
			if (RewardManager.getDroppedMoney().containsKey(e.getEntity().getEntityId()))
				RewardManager.getDroppedMoney().remove(e.getEntity().getEntityId());
			Messages.debug("The money was lost - despawned (# of rewards left=%s)",
					RewardManager.getDroppedMoney().size());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryPickupMoneyEvent(InventoryPickupItemEvent e) {
		Item item = e.getItem();
		if (RewardManager.getDroppedMoney().containsKey(item.getEntityId()))
			RewardManager.getDroppedMoney().remove(item.getEntityId());
		if (MobHunting.getConfigManager().denyHoppersToPickUpMoney && item.hasMetadata(RewardManager.MH_MONEY)
				&& e.getInventory().getType() == InventoryType.HOPPER) {
			Messages.debug("A %s tried to pick up the the reward, but this is disabled in config.yml",
					e.getInventory().getType());
			e.setCancelled(true);
		} else {
			Messages.debug("The reward was picked up by %s", e.getInventory().getType());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMoveOverMoneyEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (!player.getCanPickupItems() && !RewardManager.getDroppedMoney().isEmpty()) {
			Iterator<Entity> itemList = ((Entity) player).getNearbyEntities(1, 1, 1).iterator();
			double money = 0;
			while (itemList.hasNext()) {
				Entity ent = itemList.next();
				if (RewardManager.getDroppedMoney().containsKey(ent.getEntityId())) {
					if (ent.hasMetadata(RewardManager.MH_MONEY)) {
						List<MetadataValue> metadata = ent.getMetadata(RewardManager.MH_MONEY);
						for (MetadataValue mdv : metadata) {
							// Messages.debug("mdv=%s", mdv.toString());
							if (mdv.getOwningPlugin() == MobHunting.getInstance()) {
								money = (Double) metadata.get(0).value();
								// If not Gringotts
								if (money != 0) {
									MobHunting.getRewardManager().depositPlayer(player, money);
									if (ProtocolLibCompat.isSupported())
										ProtocolLibHelper.pickupMoney(player, ent);
									RewardManager.getDroppedMoney().remove(ent.getEntityId());
									Messages.debug("%s picked up the %s money. (# of money left=%s)", player.getName(),
											MobHunting.getRewardManager().format(money),
											RewardManager.getDroppedMoney().size());
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

	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		Entity targetEntity = null;
		Iterator<Entity> nearby = projectile.getNearbyEntities(1, 1, 1).iterator();
		while (nearby.hasNext()) {
			targetEntity = nearby.next();
			if (targetEntity.hasMetadata(RewardManager.MH_MONEY)) {
				if (RewardManager.getDroppedMoney().containsKey(targetEntity.getEntityId()))
					RewardManager.getDroppedMoney().remove(targetEntity.getEntityId());
				targetEntity.remove();
				Messages.debug("The reward was hit by %s and removed. (# of Rewards left=%s)", projectile.getType(),
						RewardManager.getDroppedMoney().size());
				break;
			}
		}
		// }
	}

}
