package one.lindegaard.BagOfGold.compatibility;

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

import one.lindegaard.BagOfGold.BagOfGold;

public class ProtocolLibHelper {

	private static ProtocolManager protocolManager;

	public static void enableProtocolLib() {
		protocolManager = ProtocolLibrary.getProtocolManager();

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(BagOfGold.getInstance(),
				ListenerPriority.NORMAL, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
					PacketContainer packet = event.getPacket().deepClone();
					StructureModifier<ItemStack> sm = packet.getItemModifier();
					for (int i = 0; i < sm.size(); i++) {
						ItemStack is = sm.getValues().get(i);
						if (is.hasItemMeta()) {
							ItemMeta itemMeta = is.getItemMeta();
							if (itemMeta.hasLore()) {
								List<String> lore = itemMeta.getLore();
								Iterator<String> itr = lore.iterator();
								while (itr.hasNext()) {
									String str = itr.next();
									if (str.startsWith("Hidden:") || str.startsWith("Hidden("))
										itr.remove();
								}
								itemMeta.setLore(lore);
								is.setItemMeta(itemMeta);
							}
						}
					}
					event.setPacket(packet);
				}

				else if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
					PacketContainer packet = event.getPacket().deepClone();
					StructureModifier<List<ItemStack>> modifiers = packet.getItemListModifier();
					for (int j = 0; j < modifiers.size(); j++) {
						List<ItemStack> itemStackList = modifiers.getValues().get(j);
						for (int i = 0; i < itemStackList.size(); i++) {
							ItemStack is = itemStackList.get(i);
							if (is.hasItemMeta()) {
								ItemMeta itemMeta = is.getItemMeta();
								if (itemMeta.hasLore()) {
									List<String> lore = itemMeta.getLore();
									Iterator<String> itr = lore.iterator();
									while (itr.hasNext()) {
										String str = itr.next();
										if (str.startsWith("Hidden:") || str.startsWith("Hidden("))
											itr.remove();
									}
									itemMeta.setLore(lore);
									is.setItemMeta(itemMeta);
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
