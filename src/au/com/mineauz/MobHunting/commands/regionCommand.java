package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.Keyle.MyPet.api.entity.MyPetEntity;
import au.com.mineauz.MobHunting.Area;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.compatability.MyPetCompat;
import au.com.mineauz.MobHunting.compatability.WorldGuardCompat;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class regionCommand implements ICommand, Listener {

	public regionCommand() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}

	// Used case (???)
	// /mh region <id> MobHunting allow - args.length = 3 || arg[1]="mobhunting"
	// /mh region <id> MobHunting deny - args.length = 3 || arg[1]="mobhunting"
	// /mh region <id> MobHunting - args.length = 2 || arg[1]="mobhunting"
	// /mh region MobHunting allow - args.length = 2 || arg[0]="mobhunting"
	// /mh region MobHunting deny - args.length = 2 || arg[0]="mobhunting"
	// /mh region MobHunting - args.length = 1 || arg[0]="mobhunting"

	@Override
	public String getName() {
		return "region";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "rg", "worldguard" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.region";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				label + ChatColor.GOLD + " <id> mobhunting allow",
				label + ChatColor.GOLD + " <id> mobhunting deny",
				label + ChatColor.GOLD + " <id> mobhunting",
				label + ChatColor.GOLD + " mobhunting allow",
				label + ChatColor.GOLD + " mobhunting deny",
				label + ChatColor.GOLD + " mobhunting" };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.region.description");
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	private StatType[] parseTypes(String typeString)
			throws IllegalArgumentException {
		String[] parts = typeString.split(",");
		StatType[] types = new StatType[parts.length];
		for (int i = 0; i < parts.length; ++i) {
			types[i] = StatType.parseStat(parts[i]);
			if (types[i] == null)
				throw new IllegalArgumentException(parts[i]);
		}

		return types;
	}

	private TimePeriod[] parsePeriods(String periodString)
			throws IllegalArgumentException {
		String[] parts = periodString.split(",");
		TimePeriod[] periods = new TimePeriod[parts.length];
		for (int i = 0; i < parts.length; ++i) {
			periods[i] = TimePeriod.parsePeriod(parts[i]);
			if (periods[i] == null)
				throw new IllegalArgumentException(parts[i]);
		}

		return periods;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 0)
			return false;

		if (WorldGuardCompat.isWorldGuardSupported()) {
			if ((sender instanceof Player)
					|| (MyPetCompat.isMyPetSupported() && sender instanceof MyPetEntity)) {
				RegionManager regionManager = WorldGuardCompat
						.getWorldGuardPlugin().getRegionManager(
								((Player) sender).getWorld());
				ApplicableRegionSet set = regionManager
						.getApplicableRegions(((Player) sender).getLocation());
				if (set == null)
					sender.sendMessage(ChatColor.RED
							+ Messages
									.getString("Unexpected error!!! Please report error to developer (cegionCommand.java - Set is null) "));
				else if (set.size() == 1) {
					// player is standing on a location with single region
					ProtectedRegion region = set.getRegions().iterator().next();
					if ((args.length == 1)
							&& args[0].equalsIgnoreCase("mobhunting"))
						return removeCurrentRegionFlag(set, "mobhunting");
					else if ((args.length >= 2)
							&& args[0].equalsIgnoreCase("mobhunting"))
						return setCurrentRegionFlag(sender, region, "mobhunting",
								args);
				} else {
					// player is standing on a location with more than one
					// region
					if ((args.length == 2)
							&& args[1].equalsIgnoreCase("mobhunting"))
						return removeFlag("mobhunting");

					else if ((args.length >= 3)
							&& args[1].equalsIgnoreCase("mobhunting"))
						return setFlag("mobhunting", args);
					else
						sender.sendMessage(ChatColor.RED
								+ Messages
										.getString("mobhunting.commands.region.specifyRegionId"));
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED
					+ Messages
							.getString("mobhunting.commands.region.noWorldguardSupport"));
		}

		return false;
	}

	private boolean setFlag(String regionid, String[] args) {
		// TODO Auto-generated method stub
		final StateFlag MOBHUNTING = new StateFlag("mobhunting", true);
		// region.setFlag(MOBHUNTING, MOBHUNTING.parseInput(plugin, sender,
		// value))
		return true;
	}

	private boolean setCurrentRegionFlag(CommandSender sender,
			ProtectedRegion region, String flag, String[] args) {
		// TODO Auto-generated method stub
		final StateFlag MOBHUNTING = new StateFlag(flag, true);
			try {
				
				region.setFlag(MOBHUNTING, MOBHUNTING.parseInput(
						WorldGuardCompat.getWorldGuardPlugin(), sender, "Allow"));
			} catch (InvalidFlagFormat e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return true;
	}

	private boolean removeFlag(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean removeCurrentRegionFlag(ApplicableRegionSet set, String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
		ArrayList<String> items = new ArrayList<String>();

		if (args.length == 1) {
			if (WorldGuardCompat.isWorldGuardSupported()) {
				if ((sender instanceof Player)
						|| (MyPetCompat.isMyPetSupported() && sender instanceof MyPetEntity)) {
					RegionManager regionManager = WorldGuardCompat
							.getWorldGuardPlugin().getRegionManager(
									((Player) sender).getWorld());
					ApplicableRegionSet set = regionManager
							.getApplicableRegions(((Player) sender)
									.getLocation());
					if (set != null) {
						Iterator<ProtectedRegion> it = set.iterator();
						while (it.hasNext()) {
							ProtectedRegion area = it.next();
							items.add(area.getId());
						}
					}
				}
			}

		} else if (args.length == 2) {
			if (args[1].equalsIgnoreCase("mobhunting")) {
				items.add("allow");
				items.add("deny");
				items.add("");
			}
		} else if (args.length == 3) {
			if (args[1].equalsIgnoreCase("mobhunting")) {
				items.add("allow");
				items.add("deny");
				items.add("");
			}
		}

		/**
		 * if (!args[args.length - 1].trim().isEmpty()) { String match =
		 * args[args.length - 1].trim().toLowerCase();
		 * 
		 * Iterator<String> it = items.iterator(); while (it.hasNext()) { String
		 * name = it.next(); if (!name.toLowerCase().startsWith(match))
		 * it.remove(); } }
		 **/
		return items;
	}

}
