package one.lindegaard.MobHunting.compatibility;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.packetwrapper.WrapperPlayServerCollect;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;

import one.lindegaard.MobHunting.MobHunting;

public class ProtocolLibHelper {

	private static ProtocolManager protocolManager;

	public static void enableProtocolLib() {
		protocolManager = ProtocolLibrary.getProtocolManager();

		// protocolManager.addPacketListener(
		// new PacketAdapter(MobHunting.getInstance(), ListenerPriority.NORMAL,
		// PacketType.Play.Server.COLLECT) {
		// @Override
		// public void onPacketSending(PacketEvent event) {
		// if (event.getPacketType() == PacketType.Play.Server.COLLECT) {
		// WrapperPlayServerCollect wpsc = new WrapperPlayServerCollect();
		// int entityID = wpsc.getCollectorEntityId();
		// Messages.debug("Server send a COLLECT packet type=%s, EntityID=%s",
		// event.getPacketType(),
		// entityID);
		// /summon Zombie ~ ~1 ~ {CanPickUpLoot:1}
		// if ( instanceof Player)
		// Messages.debug("A player picked up the item");
		// else if (wpsc.getCollectorEntityId() instanceof
		// Zombie)
		// Messages.debug("A player picked up the item");
		// event.setCancelled(true);
		// }
		// }
		// });

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(MobHunting.getInstance(),
				ListenerPriority.NORMAL, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
					PacketContainer packet = event.getPacket().deepClone();
					StructureModifier<ItemStack> sm = packet.getItemModifier();
					for (int j = 0; j < sm.size(); j++) {
						if (sm.getValues().get(j) != null) {
							ItemStack item = sm.getValues().get(j);
							if (item.hasItemMeta()) {
								ItemMeta itemMeta = item.getItemMeta();
								if (itemMeta.hasLore()) {
									List<String> lore = itemMeta.getLore();
									Iterator<String> itr = lore.iterator();
									while (itr.hasNext()) {
										String str = itr.next();
										if (str.startsWith("Hidden:"))
											itr.remove();
									}
									itemMeta.setLore(lore);
									item.setItemMeta(itemMeta);
								}
							}
						}
					}
					event.setPacket(packet);
				}
				if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
					PacketContainer packet = event.getPacket().deepClone();
					StructureModifier<ItemStack[]> sm = packet.getItemArrayModifier();
					for (int j = 0; j < sm.size(); j++) {
						for (int i = 0; i < sm.getValues().size(); i++) {
							if (sm.getValues().get(j)[i] != null) {
								final ItemStack item = sm.getValues().get(j)[i];
								if (item.hasItemMeta()) {
									final ItemMeta itemMeta = item.getItemMeta();
									if (itemMeta.hasLore()) {
										List<String> lore = itemMeta.getLore();
										Iterator<String> itr = lore.iterator();
										while (itr.hasNext()) {
											String str = itr.next();
											if (str.startsWith("Hidden:"))
												itr.remove();
										}
									}
								}
							}
						}
					}
					event.setPacket(packet);
				}
			}
		});
	}

	public static ProtocolManager getProtocolmanager() {
		return protocolManager;
	}

	public static void pickupMoney(Player player, Entity ent) {
		WrapperPlayServerCollect wpsc = new WrapperPlayServerCollect();
		wpsc.setCollectedEntityId(ent.getEntityId());
		wpsc.setCollectorEntityId(player.getEntityId());
		wpsc.sendPacket(player);
	}
}
