package au.com.mineauz.MobHunting.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Jukebox;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PistonBaseMaterial;
import org.bukkit.material.PistonExtensionMaterial;
import org.bukkit.material.TrapDoor;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.spongepowered.api.block.tileentity.carrier.Dispenser;

import com.sk89q.worldedit.blocks.BlockType;

import au.com.mineauz.MobHunting.MobHunting;

public class MasterMobhunterSign implements Listener {

	@SuppressWarnings("unused")
	private MobHunting plugin;

	// private String owner;

	private final static String MH_POWERED = "MH:powered";
	public final static byte POWER_FROM_SIGN = 15;

	// https://regex101.com/
	// Regex string="\[(MH|mh|Mh|mH)(\d+)(\+)?\]"
	// Example: [mh001+]
	final static String MASTERMOBHUNTERSIGN = "\\[(MH|mh|Mh|mH)(\\d+)(\\+)?\\]";

	public static List<Material> supportedmats = new ArrayList<Material>();

	public MasterMobhunterSign(MobHunting plugin) {
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

	private final static BlockFace possibleBlockface[] = new BlockFace[] {
			BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
			BlockFace.UP, BlockFace.DOWN };

	// ****************************************************************************'
	// SETTERS
	// ****************************************************************************'

	@SuppressWarnings("deprecation")
	public static void setPower(Block b, byte power) {
		if (isMHSign(b)) {
			if (getPowerSetOnSign(((Sign) b.getState()).getLine(0)))
				setMHPower(b, POWER_FROM_SIGN);
			else
				removeMHPower(b);
		} else if (supportedmats.contains(b.getType())) {
			if (isRedstoneWire(b)) {
				b.setData(power, true);
				b.getState().update();
			} else
				MobHunting
						.debug("MasterMobHunterSign-setPower: The code for blockType=%s is not written yet",
								b.getType());
		}
	}

	@SuppressWarnings("unused")
	private static void setMHPower(Block b, byte power) {
		if (power >= 0 && power <= POWER_FROM_SIGN) {
			MobHunting.debug("MMHSign - setMHPower(%s) on %s ", power,
					b.getType());
			b.setMetadata(MH_POWERED,
					new FixedMetadataValue(MobHunting.getInstance(), power));
			if (!isMHSign(b))
				setPower(b, power);
		}
		if (isMHSign(b) || isMHIndirectPoweredBySign(b)) {
			power = POWER_FROM_SIGN;
		} else {
			power--;
		}
		if (power >= 0 && power <= POWER_FROM_SIGN) {
			for (BlockFace bf : possibleBlockface) {
				Block rb = b.getRelative(bf);
				if (supportedmats.contains(rb.getType())) {
					if (isMHIndirectPoweredBySign(rb)) {// b or rb?
						if (isRedstoneWire(rb)) {
							// MobHunting.debug("Set power on wire");
							setMHPowerOnRedstoneWire(rb, power);
						} else if (isRedstoneLamp(rb)) {
							// MobHunting.debug("Set power on lamp");
							setPowerOnRedstoneLamp(rb, power);
						} else if (isPistonBase(rb)) {
							// MobHunting.debug("Set power on Piston(%s)",
							// rb.getType());
							setPowerOnPiston(rb);
						} else if (rb.getType().equals(Material.FURNACE)) {
							Furnace furnace = (Furnace) rb;
							furnace.setBurnTime((short) 50);
						} else if (rb.getType().equals(Material.FENCE_GATE)) {
							// TODO:
						} else if (rb.getType().equals(Material.DISPENSER)) {
							Dispenser dispenser = (Dispenser) rb;
							// dispenser.
							// TODO:
						} else if (rb.getType().equals(Material.JUKEBOX)) {
							Jukebox jukebox = (Jukebox) rb;
							MaterialData data = jukebox.getData();
							// TODO:
						} else if (rb.getType().equals(Material.TRAP_DOOR)) {
							TrapDoor trapdoor = (TrapDoor) rb;
							trapdoor.setOpen(true);
						} else {
							MobHunting
									.debug("MasterMobHunterSign-setMHPoser - The code for placing sign next to BlockType=%s is not written yet!!!",
											b.getType());
						}
						if (!isMHPowered(rb))
							setMHPower(rb, power);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static void setMHPowerOnRedstoneWire(Block block, byte power) {
		block.setData(power, true);
		block.getState().update();
	}

	@SuppressWarnings("deprecation")
	private static void setPowerOnRedstoneLamp(Block b, byte power) {
		b.setTypeIdAndData(124, power, false);
		b.getState().update(false, false);
	}

	@SuppressWarnings("deprecation")
	private static void setPowerOnPiston(Block b) {
		MobHunting.debug("BlockPlaceEvent(3): piston on face=%s", b.getState()
				.getData());
		// Piston p = (Piston) b.getState();
		PistonBaseMaterial pistonData = (PistonBaseMaterial) b.getState()
				.getData();
		BlockFace blockFace = pistonData.getFacing();
		Block tb = b.getRelative(blockFace);
		pistonData.setPowered(true);
		b.setData(pistonData.getData());
		tb.setType(Material.PISTON_EXTENSION);
		PistonExtensionMaterial pem = (PistonExtensionMaterial) tb.getState()
				.getData();
		tb.setData(pem.getData());
		b.getState().update(true, true);
		tb.getState().update(true, true);

	}

	// ****************************************************************************'
	// GETTERS
	// ****************************************************************************'

	@SuppressWarnings("unused")
	private static int getMHpower(Block block) {
		int power = 0;
		if (block.hasMetadata(MH_POWERED)) {
			for (MetadataValue value : block.getMetadata(MH_POWERED)) {
				int p = value.asInt();
				power = power > p ? power : p;
			}
		}
		return power;
	}

	public static int getNPCIdOnSign(String str) {
		if (!str.matches(MASTERMOBHUNTERSIGN)) {
			// MobHunting.debug("This is not a MobHunting Sign");
			return 0;
		}
		Pattern pattern = Pattern.compile(MASTERMOBHUNTERSIGN);
		Matcher m = pattern.matcher(str);
		m.find();
		return Integer.valueOf(m.group(2));
	}

	public static boolean getPowerSetOnSign(String str) {
		if (!str.matches(MASTERMOBHUNTERSIGN)) {
			// MobHunting.debug("This is not a MobHunting Sign");
			return false;
		}
		Pattern pattern = Pattern.compile(MASTERMOBHUNTERSIGN);
		Matcher m = pattern.matcher(str);
		m.find();
		return (m.group(3) == null) ? false : true;
	}

	// ****************************************************************************'
	// REMOVE
	// ****************************************************************************'

	public static void removePower(Block block) {
		if (isMHPowered(block)) {
			block.removeMetadata(MH_POWERED, MobHunting.instance);
			setPower(block, (byte) 0);
			for (BlockFace bf : possibleBlockface) {
				Block rb = block.getRelative(bf);
				if (isMHPowered(rb)&&!isMHPoweredSign(rb)) {
					removeMHPower(rb);
					setPower(rb, (byte) 0);
				}
			}
		}
	}

	private static void removeMHPower(Block block) {
		block.removeMetadata(MH_POWERED, MobHunting.instance);
		for (BlockFace bf : possibleBlockface) {
			Block rb = block.getRelative(bf);
			if (isMHPowered(rb)&&!isMHPoweredSign(rb))
				removeMHPower(rb);
		}
	}

	// ****************************************************************************'
	// Events
	// ****************************************************************************'
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;
		if (event.getClickedBlock() == null)
			return;
		try {
			@SuppressWarnings({ "rawtypes", "unused" })
			Class cls = Class
					.forName("org.bukkit.event.player.PlayerInteractEvent.getHand");
			// This only works on Minecraft 1.9
			if (event.getHand().equals(EquipmentSlot.OFF_HAND))
				return;
		} catch (ClassNotFoundException e) {
			// This is not a MC 1.9
		}

		if (!event.getPlayer().getItemInHand().getType().equals(Material.STICK))
			return;

		Block clickedBlock = event.getClickedBlock();
		int power = 0;
		if (clickedBlock.hasMetadata(MH_POWERED)) {
			for (MetadataValue mdv : clickedBlock.getMetadata(MH_POWERED)) {
				int p = mdv.asInt();
				power = power > p ? power : p;
			}
		}
		int power2 = 0;
		if (clickedBlock.isBlockIndirectlyPowered())
			for (BlockFace bf : possibleBlockface) {
				int p = clickedBlock.getBlockPower(bf);
				power2 = power2 > p ? power2 : p;
			}
		int power3 = 0;
		if (clickedBlock.isBlockPowered())
			power3 = clickedBlock.getBlockPower();
		MobHunting.debug("PowerStatus: MH:pow=%s, Ipow=%s, Dpow=%s on %s",
				power, power2, power3, clickedBlock.getType());
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		Block b = e.getBlock();
		if (isSign(b) && isMHSign(b)) {
			// MobHunting.debug("BlockPlaceEvent(1): %s", b.getType());
			if (getPowerSetOnSign(((Sign) b.getState()).getLine(0)))
				setMHPower(b, POWER_FROM_SIGN);
			else
				removeMHPower(b);
		} else if (supportedmats.contains(b.getType())) {
			// MobHunting.debug("BlockPlaceEvent(2): %s", b.getType());
			if (isMHIndirectPoweredBySign(b)) {
				setMHPower(b, POWER_FROM_SIGN);
				if (isRedstoneLamp(b)) {
					// MobHunting.debug("BlockPlaceEvent(3): set lamp on");
					setPowerOnRedstoneLamp(b, POWER_FROM_SIGN);
				} else if (isPistonBase(b)) {
					setPowerOnPiston(b);
				} else {
					b.setData(POWER_FROM_SIGN, true);
					b.getState().update();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockRedstoneEvent(BlockRedstoneEvent e) {
		Block b = e.getBlock();
		if (isMHPowered(b)) {
			// MobHunting.debug("BlockRedstoneEvent: MHPowered block %s",
			// b.getType());
			for (MetadataValue mdv : b.getMetadata(MH_POWERED)) {
				if (isMHIndirectPoweredBySign(e.getBlock()))
					e.setNewCurrent(mdv.asInt());
				else {
					removeMHPower(b);
					e.setNewCurrent(0);
				}
				if (mdv.asInt() == 0) {
					removeMHPower(b);
					for (BlockFace bf : possibleBlockface) {
						Block rb = b.getRelative(bf);
						removeMHPower(rb);
					}
				}
			}
		} else if (isSign(b)) {
			// MobHunting.debug("BlockRedstoneEvent(2): Sign");
		} else if (b.getType().equals(Material.REDSTONE_LAMP_OFF)
				|| b.getType().equals(Material.REDSTONE_LAMP_ON)) {
			// MobHunting.debug("BlockRedstoneEvent(3): %s", b.getType());
		}
	}

	@EventHandler
	public void onBlockPhysicsEvent(final BlockPhysicsEvent e) {

		// This is the block which is going to be changed
		// Block b = e.getBlock();
		// getChangedType() Gets the type of block that changed, causing this
		// event
		// Material c = e.getChangedType();

		/**
		 * if (isSign(b) || isSign(c)) {
		 * MobHunting.debug("BlockPhysicsEvent(1): block=%s, rootcause=%s",
		 * b.getType(), c); if (c.equals(Material.PISTON_MOVING_PIECE)) {
		 * MobHunting.debug("BlockPhysicsEvent(3): %s", b.getType()); if
		 * (isMHPowered(b)) e.setCancelled(true); } else setMHPower(b, 15); }
		 * else if (b.getType().equals(Material.REDSTONE_LAMP_OFF) ||
		 * b.getType().equals(Material.REDSTONE_LAMP_ON)) {
		 * MobHunting.debug("BlockPhysicsEvent(2): %s", b.getType()); }
		 * 
		 * if (!supportedmats.contains(b.getType())) return;
		 * 
		 * if (b.getType().equals(e.getChangedType())) return;
		 * 
		 * MobHunting.debug("BlockPhysicsEvent(4): block=%s, rootcause=%s",
		 * b.getType(), c);
		 **/
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (isMHPowered(b)) {
			MobHunting.debug("MHpower removed from %s", b.getType());
			removeMHPower(b);
		}
	}

	// ************************************************************************************
	// TESTS
	// ************************************************************************************

	public static boolean isRedstoneWire(Block block) {
		if (block.getType().equals(Material.REDSTONE_WIRE))
			return true;
		else
			return false;
	}

	public static boolean isRedstoneLamp(Block block) {
		if (block.getType().equals(Material.REDSTONE_LAMP_OFF)
				|| block.getType().equals(Material.REDSTONE_LAMP_ON))
			return true;
		else
			return false;
	}

	public static boolean isPistonBase(Block block) {
		if (block.getType().equals(Material.PISTON_BASE)
				|| block.getType().equals(Material.PISTON_STICKY_BASE))
			return true;
		else
			return false;
	}

	public static boolean isSign(Block block) {
		if (block.getType().equals(Material.SIGN_POST)
				|| block.getType().equals(Material.WALL_SIGN))
			return true;
		else
			return false;
	}

	public static boolean isSign(BlockType blockType) {
		if (blockType.equals(Material.SIGN_POST)
				|| blockType.equals(Material.WALL_SIGN))
			return true;
		else
			return false;
	}

	public static boolean isSign(Material material) {
		if (material.equals(Material.SIGN_POST)
				|| material.equals(Material.WALL_SIGN))
			return true;
		else
			return false;
	}

	public static boolean isMHSign(Block block) {
		if (isSign(block)) {
			Sign sign = (Sign) block.getState();
			if (sign.getLine(0).matches(MASTERMOBHUNTERSIGN))
				return true;
		}
		return false;
	}

	private static boolean isMHPoweredSign(Block block) {
		if (isMHSign(block) && isMHPowered(block))
			return true;
		else
			return false;
	}

	private static boolean isMHPowered(Block block) {
		return block.hasMetadata(MH_POWERED);
	}

	@SuppressWarnings("unused")
	private static boolean isMHIndirectPowered(Block block) {
		for (BlockFace bf : possibleBlockface) {
			if (isMHPowered(block.getRelative(bf)))
				return true;
		}
		return false;
	}

	private static boolean isMHIndirectPoweredBySign(Block block) {
		for (BlockFace bf : possibleBlockface) {
			if (isMHPoweredSign(block.getRelative(bf)))
				return true;
		}
		return false;
	}

	// ********************************************************************************************
	// UNUSED
	// ********************************************************************************************

	// This method was used to format the sign.
	@SuppressWarnings("unused")
	private static void update(final Block b) {
		MobHunting.getInstance().getServer().getScheduler()
				.runTaskLater(MobHunting.getInstance(), new Runnable() {
					@Override
					public void run() {
						Sign sign;
						if (isSign(b)) {
							sign = (Sign) b.getState();
						} else {
							MobHunting.debug("Block at " + b.getX() + ","
									+ b.getY() + "," + b.getZ()
									+ ", is no longer a sign.");
							return;
						}
						String coords = b.getX() + "," + b.getY() + ","
								+ b.getZ();
						// if (!isStoredSign(sign.getBlock())) {
						// setMetadata(sign, type);
						// }
						// List<String> info =
						// plugin.signs.getStringList(SignTypetoString(type)+"."+w.getName()+"."+coords);
						// boolean dopower = false;
						// if (info.get(0).equalsIgnoreCase("true")) {
						// dopower = true;
						// }
						// String format = info.get(1);
						// List<String> lines = new ArrayList<String>();
						// if (plugin.formats.containsKey(format)) {
						// lines = new
						// ArrayList<String>(plugin.formats.get(format));
						// } else {
						// lines.add("&cError!");
						// lines.add("The format:");
						// lines.add("&c" + format);
						// lines.add("does not exist.");
						// }
						// Random rand = new Random();
						// int nextint = rand.nextInt(1000000000);
						// boolean plural = false;
						// boolean power = false;
						// if (dopower) {
						// for (int i = 0; i < 4; i++) {
						// String newline = variableize(lines.get(i), type,
						// sign,
						// info, nextint);
						// if (isMHPowered(b) && power == false) {
						// power = true;
						// }
						/*
						 * Pattern newp = Pattern.compile("\\(s\\)"); Matcher
						 * newm = newp.matcher(newline); Pattern newpp =
						 * Pattern.compile("[0-9][0-9]*"); Matcher newmm =
						 * newpp.matcher(newline); if (newm.find()) { if
						 * (newmm.find()) { if
						 * (newmm.group().equalsIgnoreCase("1")) { newline =
						 * newline.replaceAll(newp.pattern(), ""); } else {
						 * newline = newline.replaceAll(newp.pattern(), "s");
						 * plural = true; } } }
						 */
						// newline = removePowered(newline);
						// lines.set(i, newline);
						// }
						// } else {
						// for (int i = 0; i < 4; i++) {
						// String newline = variableize(lines.get(i), type,
						// sign,
						// info, nextint);
						// newline = removePowered(newline);
						// lines.set(i, newline);
						// }
						// }
						// for (int i = 0; i < 4; i++) {
						// sign.setLine(i, lines.get(i));
						// }
						// sign.update();
						if (isMHSign(b)) {
							// List<String> coordsforpoweredsigns;
							// coordsforpoweredsigns =
							// plugin.poweredsigns.get(sign
							// .getWorld());
							// if (coordsforpoweredsigns == null) {
							// coordsforpoweredsigns = new ArrayList<String>();
							// }
							// coordsforpoweredsigns.add(coords);
							// plugin.poweredsigns.put(sign.getWorld(),
							// coordsforpoweredsigns);
							setPower(b, POWER_FROM_SIGN);
						} else {
							// List<String> coordsforpoweredsigns = new
							// ArrayList<String>();
							// coordsforpoweredsigns =
							// plugin.poweredsigns.get(sign
							// .getWorld());
							// if (coordsforpoweredsigns != null) {
							// coordsforpoweredsigns.remove(coords);
							// }
							// plugin.poweredsigns.put(sign.getWorld(),
							// coordsforpoweredsigns);
							MobHunting.debug("Does this happen!!!");
							setPower(b, (byte) 0);
						}
						// return true;
					}
				}, 2L);
	}

}
