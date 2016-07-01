package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.ExtendedMobType;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.util.Misc;

public class HeadCommand implements ICommand, Listener {

	public static final String MH_HEAD = "MH:Head";
	public static final String MH_REWARD = "MobHunting Reward";

	public HeadCommand() {
	}

	// Used case
	// /mh head spawn [displayname] [mobname|playername] [amount] [xpos ypos
	// zpos] - to spawn a head.
	// /mh head rename [displayname] - to rename the head holding in the hand.

	@Override
	public String getName() {
		return "head";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "ph", "playerhead", "heads", "mh", "mobhead" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.head";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				ChatColor.GOLD + label + ChatColor.GREEN + " spawn" + " [playername|mobname]" + " [displayname]"
						+ " [amount] [playername|xpos ypos zpos] " + ChatColor.YELLOW + "" + ChatColor.WHITE
						+ "       - to spawn a head",
				ChatColor.GOLD + label + ChatColor.GREEN + " rename [new displayname]" + ChatColor.WHITE
						+ " - to rename the head in players name" };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.head.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		// /mh head spawn [mobname|playername] [displayname] [amount] [xpos ypos
		// zpos] - to spawn a head.
		if (args.length >= 1 && args[0].equalsIgnoreCase("spawn")) {
			if (args.length >= 2) {
				// get itemHead
				ItemStack itemHead;
				OfflinePlayer offlinePlayer = null;
				String displayName;
				int amount = 1;
				int xPos = 0, yPos = 0, zPos = 0;
				World world;
				Location location;
				ExtendedMobType mob = ExtendedMobType.getExtendedMobType(args[1]);
				if (mob != null) {
					itemHead = mob.getHead();
				} else {
					offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
					if (offlinePlayer == null) {
						sender.sendMessage(Messages.getString("command.head.unknown_name"));
						return false;
					} else {
						itemHead = Misc.getPlayerHead(offlinePlayer);
					}
				}
				// get displayname
				if (args.length >= 3) {
					displayName = args[2];
				} else {
					if (mob != null)
						displayName = mob.getName();
					else
						displayName = offlinePlayer.getName();
				}
				// get amount
				if (args.length >= 4) {
					try {
						amount = Integer.valueOf(args[3]);
					} catch (NumberFormatException e) {
						sender.sendMessage(Messages.getString("command.head.not_a_number", "number", args[3]));
						return false;
					}
				}
				// get world
				if (sender instanceof Player)
					world = ((Player) sender).getWorld();
				else if (offlinePlayer != null && offlinePlayer.isOnline()) {
					world = Misc.getOnLinePlayer(offlinePlayer).getWorld();
				} else {
					sender.sendMessage("You can only spawn heads of online players from the console");
					return false;
				}
				// get position
				if (args.length == 7) {
					try {
						xPos = Integer.valueOf(args[4]);
						yPos = Integer.valueOf(args[5]);
						zPos = Integer.valueOf(args[6]);
					} catch (NumberFormatException e) {
						sender.sendMessage(Messages.getString("command.head.not_a_number", "number", args[3]));
						return false;
					}
					location = new Location(world, xPos, yPos, zPos);
				} else {
					location = ((Player) sender).getLocation();
				}
				MobHunting.debug("Spawn head:%s at %s", itemHead.toString(), location.toString());
				for (int i = 1; i <= amount; i++) {
					ItemMeta im = itemHead.getItemMeta();
					im.setDisplayName(displayName);
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(MH_REWARD);
					im.setLore(lore);
					Item item = location.getWorld().dropItem(location, itemHead);
					item.setMetadata(MH_HEAD, new FixedMetadataValue(MobHunting.getInstance(), displayName));
					item.setCustomName(displayName);
					item.setCustomNameVisible(true);
				}
			}

			return true;

		} else if (args.length > 1 && (args[0].equalsIgnoreCase("rename"))) {
			// /mh head rename [displayname] - to rename the head holding in the
			// hand.
			if (sender instanceof Player) {
				Player player = (Player) sender;
				ItemStack itemInHand = player.getItemInHand();
				String displayname = "";
				for (int i = 1; i < args.length; i++) {
					if (i != (args.length - 1))
						displayname = displayname + args[i] + " ";
					else
						displayname = displayname + args[i];
				}
				ItemMeta im = itemInHand.getItemMeta();
				im.setDisplayName(displayname);
				itemInHand.setItemMeta(im);
			} else {
				sender.sendMessage("You can only rename an item you have inthe hand ingame");
			}
			return true;
		}
		// show help
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		ArrayList<String> items = new ArrayList<String>();
		if (args.length == 0) {
			items.add(" spawn");
			items.add(" rename");
		} else if (args.length == 1) {
			if (items.isEmpty()) {
				items.add("spawn");
				items.add("rename");
			}
			String partial = args[0].toLowerCase();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().toLowerCase().startsWith(partial))
					items.add(player.getName());
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
			// MobHunting.debug("arg[0,1]=(%s,%s)", args[0], args[1]);
			String partial = args[1].toLowerCase();
			for (OfflinePlayer wantedPlayer : MobHunting.getBountyManager().getWantedPlayers()) {
				if (wantedPlayer.getName().toLowerCase().startsWith(partial))
					items.add(wantedPlayer.getName());
			}
		}
		return items;
	}

	@EventHandler
	public void PickupItem(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		if (event.getItem().hasMetadata(HeadCommand.MH_HEAD)) {
			String displayName = item.getMetadata(HeadCommand.MH_HEAD).get(0).asString();
			MobHunting.debug("It was a MH Head DisplayName=%s", displayName);
			ItemMeta im = item.getItemStack().getItemMeta();
			im.setDisplayName(displayName);
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(HeadCommand.MH_REWARD);
			im.setLore(lore);
			event.getItem().getItemStack().setItemMeta(im);
		}
		if (event.getItem().getItemStack().hasItemMeta() && event.getItem().getItemStack().getItemMeta().hasLore()
				&& event.getItem().getItemStack().getItemMeta().getLore().get(0).equals(HeadCommand.MH_REWARD)) {
			MobHunting.debug("It was a MH Head DisplayName(2)=%s",
					event.getItem().getItemStack().getItemMeta().getDisplayName());
			event.getItem().setMetadata(HeadCommand.MH_HEAD, new FixedMetadataValue(MobHunting.getInstance(),
					event.getItem().getItemStack().getItemMeta().getDisplayName()));
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		if (event.getItemDrop().hasMetadata(HeadCommand.MH_HEAD)) {
			String displayName = item.getMetadata(HeadCommand.MH_HEAD).get(0).asString();
			MobHunting.debug("It was a MH Head DisplayName=%s", displayName);
			ItemMeta im = item.getItemStack().getItemMeta();
			im.setDisplayName(displayName);
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(HeadCommand.MH_REWARD);
			im.setLore(lore);
			event.getItemDrop().getItemStack().setItemMeta(im);
		}
		if (event.getPlayer().getItemInHand().hasItemMeta() && event.getPlayer().getItemInHand().getItemMeta().hasLore()
				&& event.getPlayer().getItemInHand().getItemMeta().getLore().get(0)
						.equalsIgnoreCase(HeadCommand.MH_REWARD)) {
			MobHunting.debug("HeadCommand: PlayerDropItem(2)=%s,%s", item.getName(),
					event.getItemDrop().getCustomName());
			MobHunting.debug("Add MetaData to block");
			event.getItemDrop().setMetadata(HeadCommand.MH_HEAD, new FixedMetadataValue(MobHunting.getInstance(),
					event.getPlayer().getItemInHand().getItemMeta().getDisplayName()));
		}

	}

	@EventHandler
	public void onInventoryPickUp(InventoryPickupItemEvent event) {
		if (event.getItem().hasMetadata(HeadCommand.MH_HEAD)) {
			String displayName = event.getItem().getMetadata(HeadCommand.MH_HEAD).get(0).asString();
			MobHunting.debug("It was a MH Head DisplayName=%s", displayName);
			ItemMeta im = event.getItem().getItemStack().getItemMeta();
			im.setDisplayName(displayName);
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(HeadCommand.MH_REWARD);
			im.setLore(lore);
			event.getItem().getItemStack().setItemMeta(im);
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(PlayerInteractEntityEvent event) {
		// MobHunting.debug("HeadCommand: PlayerInteractEntityEvent=%s",
		// event.getPlayer().getItemInHand().getType());
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (event.getBlock().hasMetadata(HeadCommand.MH_HEAD)) {
			String displayName = event.getBlock().getMetadata(HeadCommand.MH_HEAD).get(0).asString();
			Collection<ItemStack> drops = event.getBlock().getDrops();
			for (ItemStack is : drops) {
				ItemMeta im = is.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(HeadCommand.MH_REWARD);
				im.setLore(lore);
				im.setDisplayName(displayName);
				is.setItemMeta(im);
			}
		}
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (event.getItemInHand() != null && event.getItemInHand().hasItemMeta()) {
			ItemMeta im = event.getItemInHand().getItemMeta();
			if (im.hasLore() && im.getLore().get(0).equalsIgnoreCase(HeadCommand.MH_REWARD)
					&& !im.getDisplayName().isEmpty()) {
				event.getBlockPlaced().setMetadata(HeadCommand.MH_HEAD,
						new FixedMetadataValue(MobHunting.getInstance(), im.getDisplayName()));
			}
		}
		if (event.getBlock() != null && event.getBlock().hasMetadata(HeadCommand.MH_HEAD)) {
			String displayName = event.getBlock().getMetadata(HeadCommand.MH_HEAD).get(0).asString();
			MobHunting.debug("It was a MH Head DisplayName=%s", displayName);
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null)
			if (event.getMaterial() == Material.SKULL_ITEM || event.getMaterial() == Material.SKULL) {
				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasLore()
						&& event.getItem().getItemMeta().getLore().get(0).equals(HeadCommand.MH_REWARD)) {
					String displayName = event.getItem().getItemMeta().getDisplayName();
					MobHunting.debug("It was a MH Head DisplayName=%s", displayName);
					// debug("Add MetaData to block");
				}
			}
		if (event.getClickedBlock() != null && event.getClickedBlock().hasMetadata(HeadCommand.MH_HEAD))
			MobHunting.debug("The clicked blow is a MH HEAD");
	}

}
