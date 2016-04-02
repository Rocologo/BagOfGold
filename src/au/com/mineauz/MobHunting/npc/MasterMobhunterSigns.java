package au.com.mineauz.MobHunting.npc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.metadata.FixedMetadataValue;

import au.com.mineauz.MobHunting.MobHunting;

public class MasterMobhunterSigns implements Listener {

	private static MobHunting plugin;

	private String owner;
	private int x;
	private int y;
	private int z;
	private String world;
	private static int direction = 0;
	private static boolean isWallSign = false;

	public static List<Material> supportedmats = new ArrayList<Material>();

	public MasterMobhunterSigns(MobHunting plugin) {
		MasterMobhunterSigns.plugin = plugin;

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

	private final BlockFace possibleBlockface[] = new BlockFace[] {
			BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
			BlockFace.UP, BlockFace.DOWN };
	
	public static void setPower(Block b, boolean poweron) {
		byte power = (byte) (poweron ? 0xF : 0x0);
		if (b.getType().equals(Material.SIGN)
				|| b.getType().equals(Material.SIGN_POST)) {
			Sign sign = (Sign) b.getState();
			if (sign.getLine(0).matches("\\[(MH|mh|Mh|mH)(\\d+)(\\+)?\\]")) {
				b.setMetadata("MH:powered",
						new FixedMetadataValue(MobHunting.getInstance(), power));
				turnOn(b);
				//BlockRedstoneEvent e = new BlockRedstoneEvent(b, 0, power);
				//MobHunting.debug(
				//		"create RedstoneEvent on MMHSign power=%s (%s)", power,
				//		b.getType());
				//Bukkit.getServer().getPluginManager().callEvent(e);
				
			}
		}
	}

	@EventHandler
	public void onBlockPhysics(final BlockPhysicsEvent event) {
		if (!supportedmats.contains(event.getBlock().getType()))
			return;

		if (event.getBlock().getType().equals(event.getChangedType()))
			return;

		// This is the block which is going to be changed
		Block b = event.getBlock();

		// getChangedType()
		// Gets the type of block that changed, causing this event
		Material c = event.getChangedType();

		MobHunting.debug("BlockPhysicsEvent: block=%s, rootcause=%s",
				b.getType(), c);
		if (b.getType().equals(Material.REDSTONE_LAMP_OFF) && c.equals(Material.SIGN_POST) ){
			MobHunting.debug("BlockPhysicsEvent: set %s to LAMP ON",b.getType());
			b.setType(Material.REDSTONE_LAMP_ON, false);
		}
		
	}

	@EventHandler
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		if (event.getOldCurrent() == event.getNewCurrent())
			return;
		Block b = event.getBlock();
		MobHunting.debug("BlockRedStoneEvent: block=%s (%s->%s)",
				b.getType(), event.getOldCurrent(), event.getNewCurrent());
		
		if(event.getBlock().getState() instanceof Sign){
			//Sign sign = (Sign) event.getBlock().getState();
			return;
		}
		
		for (BlockFace blockFace : possibleBlockface) {
			Block rb = b.getRelative(blockFace);
			if (rb.hasMetadata("MH:powered")) {
				if (rb.getType().equals(Material.SIGN)
						|| rb.getType().equals(Material.SIGN_POST)) {
					Sign sign = (Sign) rb.getState();
					if (sign.getLine(0).matches(
							"\\[(MH|mh|Mh|mH)(\\d+)(\\+)?\\]")) {
						 MobHunting
						 .debug("relative block %s has MH:powered hardcoded=15 change to torch",
						 rb.getType());

						 BlockRedstoneEvent e = new BlockRedstoneEvent(
								 sign.getBlock(), event.getOldCurrent(),
								 event.getNewCurrent());
						 callLater(e);
						 return;
					}
				} else if (rb.getType().equals(Material.REDSTONE_TORCH_ON)){
					
				} else {
					MobHunting.debug("Removing Metadata from block: %s",
							rb);
					rb.removeMetadata("MH:powered",
							MobHunting.getInstance());
				}
			}
		}
				
		
	}
	/**
	public void temp(){
		if (b.getType().equals(Material.REDSTONE_LAMP_ON)) {
			if (c.equals(Material.SIGN_POST) || c.equals(Material.SIGN)) {
				MobHunting.debug(
						"cancel BlockPhysicsEvent Block=%s - ChangedTyep=%s",
						b.getType(), c);
				event.setCancelled(true);
				return;
			} else if (c.equals(Material.REDSTONE_TORCH_ON)) {
				MobHunting.debug(
						"cancel BlockPhysicsEvent Block=%s - ChangedTyep=%s",
						b.getType(), c);
				event.setCancelled(true);
				return;
			}
		}

		if (b.getType().equals(Material.REDSTONE_LAMP_OFF)) {
			if (c.equals(Material.SIGN_POST) || c.equals(Material.SIGN)) {
				b.setType(Material.REDSTONE_LAMP_ON, false);
				return;
			} else if (c.equals(Material.REDSTONE_TORCH_ON)) {
				MobHunting.debug("Virtual Restone Torch changed");
				b.setType(Material.REDSTONE_LAMP_ON, true);
				return;
			}
		}

		if (event.getBlock().getType().equals(Material.REDSTONE_TORCH_ON)) {
			if (event.getBlock().hasMetadata("MH:powered")) {
				// MobHunting.debug(
				// "cancel BlockPhysicsEvent Block=%s - ChangedTyep=%s",
				// event.getBlock().getType(), event.getChangedType());
				// event.setCancelled(true);
			}
		}

		// if relative is powered by MH then cancel event.
		for (BlockFace blockFace : possibleBlockface) {
			Block rb = event.getBlock().getRelative(blockFace);
			//
			if (rb.hasMetadata("MH:powered")) {
				if (supportedmats.contains(rb.getType())) {
					MobHunting
							.debug("BlockphysicsEvent: Relative block %s has MH:powered",
									rb.getType());
					if (rb.getType().equals(Material.SIGN)
							|| rb.getType().equals(Material.SIGN_POST)) {
						Sign sign = (Sign) rb.getState();
						if (sign.getLine(0).matches(
								"\\[(MH|mh|Mh|mH)(\\d+)(\\+)?\\]")) {

							if (event.getBlock().getType()
									.equals(Material.REDSTONE_LAMP_ON)
									|| event.getBlock().getType()
											.equals(Material.REDSTONE_WIRE)) {
								// MobHunting
								// .debug("cancel BlockPhysicsEvent Block=%s - ChangedTyep=%s",
								// event.getBlock().getType(),
								// event.getChangedType());
								// event.setCancelled(true);
								// return;
							}

						}
					} else if (rb.getType().equals(Material.REDSTONE_TORCH_ON)) {

						// MobHunting.debug("cancel BlockPhysicsEvent changed=%s block=%s",
						// event.getChangedType(),event.getBlock().getType());
						// event.setCancelled(true);
						// return;
						// rb.setType(Material.SIGN_POST);
						// Sign sign = (Sign) rb.getState();
						// sign.setLine(0, "[mh56+]");
						// sign.update();
					} else if (rb.getType().equals(Material.AIR)) {
						MobHunting.debug("Removing Metadata(1) from block: %s",
								rb);
						rb.removeMetadata("MH:powered",
								MobHunting.getInstance());
					}
				}
			}
		}
	}
	
	
		
		  if (b.getType().equals(Material.REDSTONE_LAMP_OFF)) { for (BlockFace
		  blockFace : possibleBlockface) { Block rb = b.getRelative(blockFace);
		  if (rb.hasMetadata("MH:powered") &&
		  (rb.getType().equals(Material.REDSTONE_TORCH_ON))) {
		  MobHunting.debug("change to sign"); rb.setType(Material.SIGN_POST);
		  Sign sign = (Sign) rb.getState(); sign.setLine(0, "[mh56+]");
		  sign.update(); return; } } }
		 

		// if MH powered block create a redstone event on supported materials
		// next to this block
		if (b.hasMetadata("MH:powered")) {
			if (b.getType().equals(Material.SIGN)
					|| b.getType().equals(Material.SIGN_POST)) {
				Sign sign = (Sign) b.getState();
				if (sign.getLine(0).matches("\\[(MH|mh|Mh|mH)(\\d+)(\\+)?\\]")) {
					for (BlockFace blockFace : possibleBlockface) {
						Block rb = b.getRelative(blockFace);
						if (supportedmats.contains(rb.getType())) {
							MobHunting
									.debug("create new BlockRedstoneEvent on %s with power %s",
											rb.getType(), event.getNewCurrent());
							BlockRedstoneEvent e = new BlockRedstoneEvent(rb,
									event.getNewCurrent(),
									event.getNewCurrent() - 1);
							Bukkit.getServer().getPluginManager().callEvent(e);
							MobHunting
									.debug("create new BlockPhycisEvent on %s with type=%s",
											rb.getType(),
											Material.REDSTONE_TORCH_ON);
							rb.setType(Material.REDSTONE_LAMP_ON);
							BlockPhysicsEvent bpe = new BlockPhysicsEvent(rb,
									76);
							Bukkit.getServer().getPluginManager()
									.callEvent(bpe);
							// callLaterBPE(bpe);

						}
					}
				}
			} else if (b.getType().equals(Material.REDSTONE_TORCH_ON)) {
				MobHunting.debug("MH:powered is a torch(1)");
			}
		} else {

			// if not a MH powered block then check if relative is a powered
			// MH
			// block.
			for (BlockFace blockFace : possibleBlockface) {
				Block rb = b.getRelative(blockFace);
				if (rb.hasMetadata("MH:powered")) {
					if (rb.getType().equals(Material.SIGN)
							|| rb.getType().equals(Material.SIGN_POST)) {
						Sign sign = (Sign) rb.getState();
						if (sign.getLine(0).matches(
								"\\[(MH|mh|Mh|mH)(\\d+)(\\+)?\\]")) {
							// MobHunting
							// .debug("relative block %s has MH:powered hardcoded=15 change to torch",
							// rb.getType());

							// rb.setType(Material.REDSTONE_TORCH_ON);

							if (b.getType().equals(Material.REDSTONE_LAMP_OFF)) {
								// MobHunting.debug("Turn Redstone Lamp On");
								// event.getBlock().setType(Material.REDSTONE_LAMP_ON);
								// event.getBlock().setTypeIdAndData(124, (byte)
								// 0xF, true);
								// event.getBlock().getState().update();
								// MobHunting.debug("Turn Redstone Lamp On (%s)",
								// event.getBlock());
								// event.setNewCurrent(14);
								// BlockPhysicsEvent bpe = new
								// BlockPhysicsEvent(b,
								// 76);
								// Bukkit.getServer().getPluginManager().callEvent(bpe);
								// BlockRedstoneEvent e = new
								// BlockRedstoneEvent(b,
								// event.getOldCurrent(),
								// event.getNewCurrent());
								// Bukkit.getServer().getPluginManager().callEvent(e);
								// callLater(e);
								// MobHunting.debug(
								// "setNewCurrent(%s) on this block %s ",
								// event.getNewCurrent(), event.getBlock()
								// .getType());
								// event.setNewCurrent(14);

							}
							return;
							// MobHunting.debug(
							// "create new event on block %s with power=%s",
							// b.getType(), event.getNewCurrent() - 1);
							// BlockRedstoneEvent e = new BlockRedstoneEvent(b,
							// event.getOldCurrent(),
							// event.getNewCurrent() - 1);
							// Bukkit.getServer().getPluginManager().callEvent(e);
							// callLater(e);}
						}
					} else if (rb.getType().equals(Material.REDSTONE_TORCH_ON)) {
						MobHunting.debug("MH:powered is a torch(2)");

					} else {
						MobHunting.debug("Removing Metadata(2) from block: %s",
								event.getBlock().getType());
						event.getBlock()
								.getRelative(blockFace)
								.removeMetadata("MH:powered",
										MobHunting.getInstance());

					}

				}
			}
		}

	
	
	**/

	public void callLater(final BlockRedstoneEvent event) {
		Bukkit.getScheduler().runTaskLater(MobHunting.instance, new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}, 1L);
	}

	public void callLaterBPE(final BlockPhysicsEvent bpe) {
		Bukkit.getScheduler().runTaskLater(MobHunting.instance, new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getPluginManager().callEvent(bpe);
			}
		}, 1L);
	}

	@SuppressWarnings("deprecation")
	public static void turnOn(Block block) {
		if (MasterMobhunterSigns.getLocation(block) == null) // If the world is
																// not loaded or
			// doesn't exist
			return;

		// Block block = getLocation().getBlock();

		if (!MasterMobhunterSigns.getIsWallSign()) {
			// if (!WirelessRedstone.WireBox.isValidLocation(block)) {
			// WirelessRedstone.WireBox.signWarning(block, 1);
			// } else {
			// block.setType(Material.REDSTONE_TORCH_ON);
			// block.getState().update(); // This doesn't work for 1.7 when
			// facing west, please don't ask me why :P
			if (plugin.getServer().getBukkitVersion().contains("v1_8")
					|| plugin.getServer().getBukkitVersion().contains("v1_9")) {
				MobHunting.debug("set sign to redstone torch - byte 0");
				block.setTypeIdAndData(76, (byte) 0, true);
			} else {
				MobHunting.debug("set sign to redstone torch - byte 5");
				block.setTypeIdAndData(76, (byte) 5, true);
			}
			// }
		} else {
			if (block.getType() == Material.WALL_SIGN) {
				if (!MasterMobhunterSigns.isValidWallLocation(block)) {
					// WirelessRedstone.WireBox.signWarning(block, 1);
					MobHunting.debug("Invalid sign location");
				} else {
					byte directionByte;

					if (plugin.getServer().getBukkitVersion().contains("v1_8")
							|| plugin.getServer().getBukkitVersion()
									.contains("v1_9")) {
						switch (MasterMobhunterSigns.getDirection()) {
						case EAST:
							directionByte = 1;
							break;

						case WEST:
							directionByte = 2;
							break;

						case SOUTH:
							directionByte = 3;
							break;

						case NORTH:
							directionByte = 4;
							break;

						default:
							directionByte = 0;

						}
					} else {
						switch (MasterMobhunterSigns.getDirection()) {
						case EAST:
							directionByte = 0;
							break;

						case WEST:
							directionByte = 2;
							break;

						case SOUTH:
							directionByte = 3;
							break;

						case NORTH:
							directionByte = 4;
							break;

						default:
							directionByte = 5;

						}
					}

					block.setTypeIdAndData(76, directionByte, true);
					MobHunting.debug("set wallsign to redstone torch - byte 0");
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void turnOff(Block block) {
		if (getLocation(block) == null)
			return;

		byte directionByte;
		// Block block = getLocation().getBlock();
		int blockID = getIsWallSign() ? 68 : 63;

		if (getIsWallSign()) {
			switch (getDirection()) {
			case NORTH:
				directionByte = 2;
				break;

			case SOUTH:
				directionByte = 3;
				break;

			case WEST:
				directionByte = 4;
				break;

			case EAST:
				directionByte = 5;
				break;

			default:
				directionByte = 2;
			}
		} else {
			directionByte = (byte) signFaceToInt(getDirection());
		}

		if (plugin.getServer().getBukkitVersion().contains("v1_8")
				|| plugin.getServer().getBukkitVersion().contains("v1_9")) {
			block.setTypeIdAndData(blockID, directionByte, true);
			MobHunting.debug("set torch to sign -1");
		} else {
			block.setType(Material.AIR);
			block.setTypeIdAndData(blockID, directionByte, true);
			block.getState().update();
			MobHunting.debug("set torch to sign -2");
		}

		if (block.getState() instanceof Sign) {
			changeSignContent(block);
		}
	}

	public String getOwner() {
		return this.owner;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public String getWorld() {
		return this.world;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public static Location getLocation(Block block) {
		return block.getLocation();// new Location(Bukkit.getWorld(world), x, y,
									// z);
	}

	public static boolean getIsWallSign() {
		return isWallSign;
	}

	public void setIsWallSign(boolean iswallsign) {
		this.isWallSign = iswallsign;
	}

	/**
	 * This method gets the block the sign is attached to, and checks that a
	 * redstone torch can be put on the side of this block.
	 *
	 * @param block
	 *            - The sign block
	 * @return true if a torch can be put there.
	 */
	public static boolean isValidWallLocation(final Block block) {
		org.bukkit.material.Sign sign = (org.bukkit.material.Sign) block
				.getState().getData();
		BlockFace face = sign.getAttachedFace();
		Block tempBlock = block.getRelative(face);

		Boolean check = !(tempBlock.getType() == Material.AIR
				|| tempBlock.getType() == Material.PISTON_BASE
				|| tempBlock.getType() == Material.PISTON_EXTENSION
				|| tempBlock.getType() == Material.PISTON_MOVING_PIECE
				|| tempBlock.getType() == Material.PISTON_STICKY_BASE
				|| tempBlock.getType() == Material.GLOWSTONE
				|| tempBlock.getType() == Material.REDSTONE_LAMP_ON
				|| tempBlock.getType() == Material.REDSTONE_LAMP_OFF
				|| tempBlock.getType() == Material.LEAVES
				|| tempBlock.getType() == Material.WOOD_STAIRS
				|| tempBlock.getType() == Material.COBBLESTONE_STAIRS
				|| tempBlock.getType() == Material.RED_SANDSTONE_STAIRS
				|| tempBlock.getType() == Material.SANDSTONE_STAIRS
				|| tempBlock.getType() == Material.FENCE
				|| tempBlock.getType() == Material.ACACIA_FENCE
				|| tempBlock.getType() == Material.DARK_OAK_FENCE
				|| tempBlock.getType() == Material.JUNGLE_FENCE
				|| tempBlock.getType() == Material.BIRCH_FENCE
				|| tempBlock.getType() == Material.WOOD_DOOR
				|| tempBlock.getType() == Material.WOODEN_DOOR
				|| tempBlock.getType() == Material.IRON_DOOR_BLOCK
				|| tempBlock.getType() == Material.IRON_DOOR
				|| tempBlock.getType() == Material.GLASS
				|| tempBlock.getType() == Material.THIN_GLASS
				|| tempBlock.getType() == Material.STAINED_GLASS
				|| tempBlock.getType() == Material.STAINED_GLASS_PANE
				|| tempBlock.getType() == Material.COBBLE_WALL
				|| tempBlock.getType() == Material.ICE
				|| tempBlock.getType() == Material.WOOD_STEP
				|| tempBlock.getType() == Material.STEP || tempBlock.getType() == Material.TNT);
		if (plugin.getServer().getBukkitVersion().contains("v1_8")
				|| plugin.getServer().getBukkitVersion().contains("v1_9")) {
			if (check)
				check = !(tempBlock.getType() == Material.SEA_LANTERN);
		}
		return check;
	}

	public static BlockFace getDirection() {
		return intToBlockFaceSign(direction);
	}

	public static BlockFace intToBlockFaceSign(final int dir) {
		switch (dir) {
		case 8:
			return BlockFace.NORTH;
		case 12:
			return BlockFace.EAST;
		case 0:
			return BlockFace.SOUTH;
		case 4:
			return BlockFace.WEST;
		default:
			return BlockFace.NORTH;
		}
	}

	public static int signFaceToInt(final BlockFace face) {
		switch (face) {
		case NORTH:
			return 8;
		case EAST:
			return 12;
		case SOUTH:
			return 0;
		case WEST:
			return 4;
		default:
			return 0;
		}
	}

	public static void changeSignContent(Block block) {
		Sign signtemp = (Sign) block.getState();
		signtemp.setLine(0, "[mh56+]");
		// signtemp.setLine(1, channelName);
		// signtemp.setLine(2,
		// WirelessRedstone.strings.tagsReceiverDefaultType.get(0));
		signtemp.update(true);
	}

}
