package au.com.mineauz.MobHunting.npc;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.metadata.FixedMetadataValue;
import au.com.mineauz.MobHunting.MobHunting;

public class NpcSigns {

	private static MobHunting plugin;

	public static List<Material> supportedmats = new ArrayList<Material>();

	public NpcSigns(MobHunting plugin) {
		NpcSigns.plugin = plugin;

		supportedmats.add(Material.REDSTONE_COMPARATOR_OFF);
		supportedmats.add(Material.REDSTONE_COMPARATOR_ON);
		supportedmats.add(Material.REDSTONE_LAMP_OFF);
		supportedmats.add(Material.REDSTONE_LAMP_ON);
		supportedmats.add(Material.REDSTONE_TORCH_OFF);
		supportedmats.add(Material.REDSTONE_TORCH_ON);

		supportedmats.add(Material.REDSTONE_WIRE);

		supportedmats.add(Material.DISPENSER);
		supportedmats.add(Material.FURNACE);
		supportedmats.add(Material.POWERED_RAIL);
		supportedmats.add(Material.ACTIVATOR_RAIL);
		supportedmats.add(Material.DIODE_BLOCK_OFF);
		supportedmats.add(Material.DIODE_BLOCK_ON);
		supportedmats.add(Material.COMMAND);
		supportedmats.add(Material.FENCE_GATE);
		supportedmats.add(Material.IRON_DOOR);
		supportedmats.add(Material.WOODEN_DOOR);
		supportedmats.add(Material.JUKEBOX);
		supportedmats.add(Material.PISTON_BASE);
		supportedmats.add(Material.PISTON_STICKY_BASE);
		supportedmats.add(Material.TNT);
		supportedmats.add(Material.TRAP_DOOR);

	}

	@SuppressWarnings("deprecation")
	public static void setPower(Location signLocation, boolean poweron) {
		byte power;
		if (poweron) {
			power = 0xF;
		} else {
			power = 0x0;
		}
		Block b = signLocation.getBlock();
		if (supportedmats.contains(b.getRelative(BlockFace.UP)
				.getRelative(BlockFace.UP).getType())) {
			b.getRelative(BlockFace.UP).getRelative(BlockFace.UP)
					.setData(power);
			if (poweron) {
				b.getRelative(BlockFace.UP)
						.getRelative(BlockFace.UP)
						.setMetadata("MH_Redstone",
								new FixedMetadataValue(plugin, 0));
			}
		}
		for (BlockFace bf : new BlockFace[] { BlockFace.UP, BlockFace.DOWN,
				BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST,
				BlockFace.WEST, BlockFace.SOUTH }) {
			if (supportedmats.contains(b.getRelative(bf).getType())) {
				b.getRelative(bf).setData(power);
				if (poweron) {
					b.getRelative(bf).setMetadata("MH_Redstone",
							new FixedMetadataValue(plugin, 0));
				}
			}
		}
	}

}
