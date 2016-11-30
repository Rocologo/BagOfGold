package one.lindegaard.MobHunting.compatibility;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class ProtocolLibHelper {

	private static ProtocolManager protocolManager;

	public static void enableProtocolLib() {
		protocolManager = ProtocolLibrary.getProtocolManager();

		protocolManager.addPacketListener(
				new PacketAdapter(MobHunting.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.COLLECT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						// Item packets (id: 0x29)
						if (event.getPacketType() == PacketType.Play.Server.COLLECT) {
							Messages.debug("1Server send a PacketType=%s", event.getPacketType());
							// event.setCancelled(true);
						} else {
							Messages.debug("2Server send a PacketType=%s", event.getPacketType());
						}
					}

					@Override
					public void onPacketReceiving(PacketEvent event) {
						// Item packets (id: 0x29)
						if (event.getPacketType() == PacketType.Play.Server.COLLECT) {
							Messages.debug("3Server receive a PacketType=%s", event.getPacketType());
							// event.setCancelled(true);
						} else {
							Messages.debug("4Server receive a PacketType=%s", event.getPacketType());
						}
					}
				});

		protocolManager.addPacketListener(
				new PacketAdapter(MobHunting.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.ENTITY_ACTION) {
					@Override
					public void onPacketSending(PacketEvent event) {
						Messages.debug("5Server send a PacketType=%s", event.getPacketType());
					}

					@Override
					public void onPacketReceiving(PacketEvent event) {
						Messages.debug("6Server receive a PacketType=%s", event.getPacketType());
					}
				});
	};

	public static ProtocolManager getProtocolmanager() {
		return protocolManager;
	}
}
