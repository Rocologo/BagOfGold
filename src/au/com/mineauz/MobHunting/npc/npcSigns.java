package au.com.mineauz.MobHunting.npc;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import au.com.mineauz.MobHunting.MobHunting;

public class npcSigns {

	private MobHunting plugin;

	public List<Material> supportedmats = new ArrayList<Material>();

	public npcSigns(MobHunting plugin) {
		this.plugin = plugin;

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
	public void setPower(Location signLocation, boolean poweron) {
		byte power;
		if (poweron) {
			power = 0xF;
		} else {
			power = 0x0;
		}
		Block b = signLocation.getBlock();
		if (supportedmats.contains(b.getRelative(BlockFace.UP).getType())) {
			b.getRelative(BlockFace.UP).setData(power);
			if (poweron) {
				b.getRelative(BlockFace.UP).setMetadata("MH_RedStone",
						new FixedMetadataValue(plugin, 0));
			}
		}
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
		if (supportedmats.contains(b.getRelative(BlockFace.DOWN).getType())) {
			b.getRelative(BlockFace.DOWN).setData(power);
			if (poweron) {
				b.getRelative(BlockFace.DOWN).setMetadata("MH_Redstone",
						new FixedMetadataValue(plugin, 0));
			}
		}
		if (supportedmats.contains(b.getRelative(BlockFace.NORTH).getType())) {
			b.getRelative(BlockFace.NORTH).setData(power);
			if (poweron) {
				b.getRelative(BlockFace.NORTH).setMetadata("MH_Redstone",
						new FixedMetadataValue(plugin, 0));
			}
		}
		if (supportedmats.contains(b.getRelative(BlockFace.SOUTH).getType())) {
			b.getRelative(BlockFace.SOUTH).setData(power);
			if (poweron) {
				b.getRelative(BlockFace.SOUTH).setMetadata("MH_Redstone",
						new FixedMetadataValue(plugin, 0));
			}
		}
		if (supportedmats.contains(b.getRelative(BlockFace.EAST).getType())) {
			b.getRelative(BlockFace.EAST).setData(power);
			if (poweron) {
				b.getRelative(BlockFace.EAST).setMetadata("MH_Redstone",
						new FixedMetadataValue(plugin, 0));
			}
		}
		if (supportedmats.contains(b.getRelative(BlockFace.WEST).getType())) {
			b.getRelative(BlockFace.WEST).setData(power);
			if (poweron) {
				b.getRelative(BlockFace.WEST).setMetadata("MH_Redstone",
						new FixedMetadataValue(plugin, 0));
			}
		}
	}

	public boolean insideBox(Player p, Location xyz1, Location xyz2) {
		int pX = p.getLocation().getBlockX();
		int pY = p.getLocation().getBlockY();
		int pZ = p.getLocation().getBlockZ();
		if (!p.getWorld().equals(xyz1.getWorld())
				|| !p.getWorld().equals(xyz2.getWorld())) {
			return false;
		}
		int sX1 = xyz1.getBlockX();
		int sY1 = xyz1.getBlockY();
		int sZ1 = xyz1.getBlockZ();

		int sX2 = xyz2.getBlockX();
		int sY2 = xyz2.getBlockY();
		int sZ2 = xyz2.getBlockZ();

		int lowX = 0, highX = 0, lowY = 0, highY = 0, lowZ = 0, highZ = 0;

		if (sX1 < sX2) {
			lowX = sX1;
			highX = sX2;
		} else if (sX1 > sX2) {
			lowX = sX2;
			highX = sX1;
		}

		if (sY1 < sY2) {
			lowY = sY1;
			highY = sY2;
		} else if (sY1 > sY2) {
			lowY = sY2;
			highY = sY1;
		}

		if (sZ1 < sZ2) {
			lowZ = sZ1;
			highZ = sZ2;
		} else if (sZ1 > sZ2) {
			lowZ = sZ2;
			highZ = sZ1;
		}

		if (pX >= lowX && pY >= lowY && pZ >= lowZ && pX < highX + 1
				&& pY < highY + 1 && pZ < highZ + 1) {
			return true;
		} else {
			return false;
		}
	}
}
