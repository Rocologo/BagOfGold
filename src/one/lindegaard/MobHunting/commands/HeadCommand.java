package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
		// /mh head spawn [mobname|playername] [displayname] [amount]
		if (args.length >= 1 && args[0].equalsIgnoreCase("spawn")) {
			if (args.length >= 2) {
				OfflinePlayer offlinePlayer = null;
				String displayName;
				int amount = 1;

				// get MobType / PlayerName
				ExtendedMobType mob = ExtendedMobType.getExtendedMobType(args[1]);
				if (mob == null) {
					offlinePlayer = Bukkit.getOfflinePlayer(args[1]);
					if (offlinePlayer != null) {
						mob = ExtendedMobType.PvpPlayer;

					} else {
						sender.sendMessage(Messages.getString("command.head.unknown_name"));
						return false;
					}
				}
				// get displayname
				if (args.length >= 3) {
					displayName = args[2].replace("_", " ");
				} else {
					if (mob != null)
						displayName = mob.getDisplayName().replace("_", " ");
					else
						displayName = offlinePlayer.getName();
				}
				// get amount
				if (args.length >= 4) {
					try {
						amount = Integer.valueOf(args[3]);
					} catch (NumberFormatException e) {
						sender.sendMessage(
								Messages.getString("mobhunting.commands.base.not_a_number", "number", args[3]));
						return false;
					}
				}
				String cmdString = mob.getCommandString().replace("{player}", ((Player) sender).getName())
						.replace("{displayname}", displayName).replace("{lore}", MH_REWARD)
						.replace("{playerid}", mob.getPlayerId()).replace("{texturevalue}", mob.getTextureValue())
						.replace("{amount}", String.valueOf(amount))
						.replace("{playername}", offlinePlayer != null ? offlinePlayer.getName() : "MHF_Alex");
				Messages.debug("Spawn head cmdString=%s", cmdString);
				Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmdString);
			}

			return true;

		} else if (args.length > 1 && (args[0].equalsIgnoreCase("rename")))

		{
			// /mh head rename [displayname] - to rename the head holding in the
			// hand.
			if (sender instanceof Player) {
				Player player = (Player) sender;
				ItemStack itemInHand = player.getItemInHand();
				if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().equals(MH_REWARD)) {
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
					sender.sendMessage("You can only rename a MobHunting Reward in your hand");
				}
			} else {
				sender.sendMessage("You can only rename an item you have it in your hand ingame");
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

		} else if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
			String partial = args[1].toLowerCase();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().toLowerCase().startsWith(partial))
					items.add(player.getName());
			}
			for (ExtendedMobType mob : ExtendedMobType.values()) {
				if (mob.getName().toLowerCase().startsWith(partial)
						|| mob.getDisplayName().toLowerCase().startsWith(partial))
					items.add(mob.getName().replace(" ", "_"));
			}
		} else if (args.length == 22 && args[0].equalsIgnoreCase("spawn")) {
			// Messages.debug("arg[0,1]=(%s,%s)", args[0], args[1]);
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
		Messages.debug("onPlayerPickUpEvent hasItemMeta=%s (%s)",event.getItem().getItemStack().hasItemMeta(),
				event.getItem().getItemStack().getItemMeta().toString());
		Item item = event.getItem();
		if (event.getItem().hasMetadata(HeadCommand.MH_HEAD)) {
			String displayName = item.getMetadata(HeadCommand.MH_HEAD).get(0).asString();
			Messages.debug("You picked up a MH Head DisplayName=%s", displayName);
			ItemMeta im = item.getItemStack().getItemMeta();
			im.setDisplayName(displayName);
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(HeadCommand.MH_REWARD);
			im.setLore(lore);
			event.getItem().getItemStack().setItemMeta(im);
		}
		if (event.getItem().getItemStack().hasItemMeta() && event.getItem().getItemStack().getItemMeta().hasLore()
				&& event.getItem().getItemStack().getItemMeta().getLore().get(0).equals(HeadCommand.MH_REWARD)) {
			Messages.debug("You picked up a MH Head DisplayName(2)=%s",
					event.getItem().getItemStack().getItemMeta().getDisplayName());
			event.getItem().setMetadata(HeadCommand.MH_HEAD, new FixedMetadataValue(MobHunting.getInstance(),
					event.getItem().getItemStack().getItemMeta().getDisplayName()));
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Messages.debug("onPlayerDropItemEvent hasItemMeta=%s",event.getItemDrop().getItemStack().hasItemMeta());
		if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().hasItemMeta()
				&& event.getPlayer().getItemInHand().getItemMeta().hasLore()&&
				event.getPlayer().getItemInHand().getItemMeta().getLore().get(0).equals(MH_REWARD)) {
			Messages.debug("ItemInHand=%s", event.getPlayer().getItemInHand().getItemMeta().getDisplayName());
		}
		Item item = event.getItemDrop();
		if (event.getItemDrop().getItemStack().hasItemMeta()
				&& event.getItemDrop().getItemStack().getItemMeta().hasLore()&&
				event.getItemDrop().getItemStack().getItemMeta().getLore().equals(MH_REWARD)) {
 			String displayName = item.getItemStack().getItemMeta().getDisplayName();
			//if (event.getItemDrop().hasMetadata(HeadCommand.MH_HEAD))
			//	displayName = item.getMetadata(HeadCommand.MH_HEAD).get(0).asString();
			Messages.debug("You dropped a MH Head DisplayName=%s", displayName);
			ItemMeta im = item.getItemStack().getItemMeta();
			im.setDisplayName(displayName);
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(HeadCommand.MH_REWARD);
			im.setLore(lore);
			event.getItemDrop().setMetadata(MH_HEAD, new FixedMetadataValue(MobHunting.getInstance(),
					event.getItemDrop().getItemStack().getItemMeta().getDisplayName()));
			event.getItemDrop().getItemStack().setItemMeta(im);
		}
	}

	@EventHandler
	public void onInventoryPickUp(InventoryPickupItemEvent event) {
		if (event.getItem().hasMetadata(MH_HEAD) || event.getItem().getItemStack().hasItemMeta()
				&& event.getItem().getItemStack().getItemMeta().hasLore()&&
				event.getItem().getItemStack().getItemMeta().getLore().equals(MH_REWARD)) {
			String displayName = event.getItem().getMetadata(MH_HEAD).get(0).asString();
			Messages.debug("InventoryPickup a MH Head DisplayName=%s", displayName);
			ItemMeta im = event.getItem().getItemStack().getItemMeta();
			im.setDisplayName(displayName);
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(MH_REWARD);
			im.setLore(lore);
			event.getItem().getItemStack().setItemMeta(im);
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Messages.debug("BlockBreakEvent %s", event.getBlock());
		if (event.getBlock().hasMetadata(MH_HEAD)) {
			String displayName = event.getBlock().getMetadata(MH_HEAD).get(0).asString();
			Messages.debug("You broke a MH head Displayname=%s", displayName);
			Iterator<ItemStack> itr = event.getBlock().getDrops().iterator();
			while (itr.hasNext()){
				ItemStack is = itr.next(), is2 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				ItemMeta im = is.getItemMeta();
				ArrayList<String> lore = new ArrayList<String>();
				lore.add(MH_REWARD);
				im.setLore(lore);
				im.setDisplayName(displayName);
				is2.setItemMeta(im);
				Messages.debug("BlockBreakEvent is=%s", is.toString());
				event.getBlock().getLocation().getWorld().dropItem(event.getBlock().getLocation(), is2);
				//event.setCancelled(true);
				itr.remove();
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
				Messages.debug("ItemInHand was a MH Head DisplayName=%s", im.getDisplayName());
			}
		}
		if (event.getBlock() != null && event.getBlock().hasMetadata(HeadCommand.MH_HEAD)) {
			String displayName = event.getBlock().getMetadata(HeadCommand.MH_HEAD).get(0).asString();
			Messages.debug("You placed a MH Head DisplayName=%s", displayName);
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null)
			if (event.getMaterial() == Material.SKULL_ITEM || event.getMaterial() == Material.SKULL) {
				if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasLore()
						&& event.getItem().getItemMeta().getLore().get(0).equals(HeadCommand.MH_REWARD)) {
					String displayName = event.getItem().getItemMeta().getDisplayName();
					Messages.debug("You interacted with a MH Head DisplayName=%s", displayName);
					// debug("Add MetaData to block");
				}
			}
		if (event.getClickedBlock() != null && event.getClickedBlock().hasMetadata(HeadCommand.MH_HEAD))
			Messages.debug("You hit a MH HEAD displayName=%s",
					event.getClickedBlock().getMetadata(MH_HEAD).get(0).asString());
	}

}
