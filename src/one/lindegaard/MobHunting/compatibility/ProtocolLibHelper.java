package one.lindegaard.MobHunting.compatibility;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.comphenix.packetwrapper.WrapperPlayServerCollect;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class ProtocolLibHelper {

	private static ProtocolManager protocolManager;

	public static void enableProtocolLib() {
		protocolManager = ProtocolLibrary.getProtocolManager();

		//protocolManager.addPacketListener(
		//		new PacketAdapter(MobHunting.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.COLLECT) {
		//			@Override
		//			public void onPacketSending(PacketEvent event) {
		//				if (event.getPacketType() == PacketType.Play.Server.COLLECT) {
							//WrapperPlayServerCollect wpsc = new WrapperPlayServerCollect();
							//int entityID = wpsc.getCollectorEntityId();
							//Messages.debug("Server send a COLLECT packet type=%s, EntityID=%s", event.getPacketType(),
							//		entityID);
							// /summon Zombie ~ ~1 ~ {CanPickUpLoot:1}
							// if ( instanceof Player)
							// Messages.debug("A player picked up the item");
							// else if (wpsc.getCollectorEntityId() instanceof
							// Zombie)
							// Messages.debug("A player picked up the item");
							// event.setCancelled(true);
		//				}
		//			}
		//		});
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
