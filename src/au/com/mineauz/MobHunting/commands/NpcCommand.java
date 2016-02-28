package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.compatability.CitizensCompat;
import au.com.mineauz.MobHunting.compatability.CompatibilityManager;
import au.com.mineauz.MobHunting.npc.MasterMobHunterData;
import au.com.mineauz.MobHunting.npc.MasterMobHunterManager;
import au.com.mineauz.MobHunting.npc.MobHuntingTrait;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class NpcCommand implements ICommand, Listener {

	public NpcCommand() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}

	// Used case (???)
	// /mh npc create <stat type> <period> <number>
	// /mh npc remove
	// /mh npc update
	// /mh npc select

	@Override
	public String getName() {
		return "npc";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "citizens" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.npc";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				label + ChatColor.GOLD + " create" + ChatColor.RED
						+ " <stattype> <period> <number>" + ChatColor.WHITE
						+ " -:to create a MasterMobHunter NPC",
				label + ChatColor.GOLD + " remove" + ChatColor.WHITE
						+ " -:to remove the MasterMobHunter",
				label + ChatColor.GOLD + " update" + ChatColor.WHITE
						+ " -:to update the MasterMobHunter NPC" };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.npc.description");
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {

		ArrayList<String> items = new ArrayList<String>();
		if (CompatibilityManager.isPluginLoaded(CitizensCompat.class)) {
			if (args.length == 1) {
				items.add("create");
				items.add("remove");
				items.add("select");
				items.add("spawn");
				items.add("despawn");
				items.add("update");
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("create")) {
					StatType[] values = StatType.values();
					for (int i = 0; i < values.length; i++) {
						items.add(values[i].getDBColumn());
					}
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("create")) {
					TimePeriod[] values = TimePeriod.values();
					for (int i = 0; i < values.length; i++) {
						items.add(values[i].toString());
					}
				}
			}
		}
		return items;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 0)
			return false;
		Player p = (Player) sender;
		NPC npc;
		if (CompatibilityManager.isPluginLoaded(CitizensCompat.class)) {
			MasterMobHunterManager masterMobHunterManager = CitizensCompat
					.getManager();
			npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
			if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
				if (masterMobHunterManager.contains(npc.getId())) {
					masterMobHunterManager.remove(npc.getId());
					masterMobHunterManager.saveData(npc.getId());
				}
				npc.destroy();
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("select")) {
				npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
				// Location loc =p.getEyeLocation();
				sender.sendMessage("NPC is " + npc.getName());
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("spawn")) {
				npc.spawn(npc.getStoredLocation());
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("despawn")) {
				npc.despawn();
				return true;
			} else if (args.length == 1 && args[0].equalsIgnoreCase("update")) {
				sender.sendMessage("Updating all MasterMobHunter NPCs");
				masterMobHunterManager.forceUpdate();
				return true;} 
			else if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
				StatType statType = StatType.fromColumnName(args[1]);
				if (statType == null) {
					MobHunting.debug("statType=%s", statType);
					sender.sendMessage(ChatColor.RED
							+ Messages.getString(
									"mobhunting.commands.npc.unknown_stattype",
									"stattype", args[1]));
					return true;
				}
				TimePeriod period = TimePeriod.parsePeriod(args[2]);
				if (period == null) {
					sender.sendMessage(ChatColor.RED
							+ Messages
									.getString(
											"mobhunting.commands.npc.unknown_timeperiod",
											"period", args[2]));
					return true;
				}
				int rank = Integer.valueOf(args[3]);
				if (rank < 1 || rank > 25) {
					sender.sendMessage(ChatColor.RED
							+ Messages.getString(
									"mobhunting.commands.npc.unknown_rank",
									"rank", args[3]));
					return true;
				}
				NPCRegistry registry = CitizensAPI.getNPCRegistry();
				npc = registry.createNPC(EntityType.PLAYER, "MasterMobHunter");
				npc.addTrait(MobHuntingTrait.class);
				npc.spawn(p.getEyeLocation());
				MobHunting.debug("Creating NPC id=%s,stat=%s,per=%s,rank=%s",
						npc.getId(), statType.translateName(), period, rank);

				masterMobHunterManager.put(npc.getId(),
						new MasterMobHunterData(npc.getId(), statType, period,
								0, rank, false));
				masterMobHunterManager.saveData(npc.getId());
				return true;
			}

		}
		return false;
	}
}
