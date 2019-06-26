package one.lindegaard.BagOfGold.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.speech.SpeechContext;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.bank.BagOfGoldBankerTrait;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.Core.Tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class NpcCommand implements ICommand, Listener {

	private BagOfGold plugin;

	public NpcCommand(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	// Used case (???)
	// /bag npc create <stat type> <period> <number>
	// /bag npc remove
	// /bag npc spawn
	// /bag npc despawn
	// /bag npc select
	// /bag npc tphere
	// /bag npc sethome

	@Override
	public String getName() {
		return "npc";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "banker" };
	}

	@Override
	public String getPermission() {
		return "bagofgold.npc";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				ChatColor.GOLD + label + ChatColor.GREEN + " create" + ChatColor.WHITE
						+ " - to create a BagOfGoldBanker NPC",
				ChatColor.GOLD + label + ChatColor.GREEN + " remove" + ChatColor.WHITE
						+ " - to remove the selected BagOfGoldBanker",
				ChatColor.GOLD + label + ChatColor.GREEN + " select" + ChatColor.WHITE
						+ " - to select the BagOfGold NPC you are looking at.",
				ChatColor.GOLD + label + ChatColor.GREEN + " tphere" + ChatColor.WHITE
						+ " - to moved the selected BagOfGold NPC",
				ChatColor.GOLD + label + ChatColor.GREEN + " sethome" + ChatColor.WHITE
						+ " - to set home for the selected BagOfGold NPC" };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgold.commands.npc.description");
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
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {

		String[] subcmds = { "create", "remove", "select", "spawn", "despawn", "update", "tphere", "sethome" };
		ArrayList<String> items = new ArrayList<String>();
		if (plugin.getCompatibilityManager().isPluginLoaded(CitizensCompat.class)) {
			if (args.length == 1) {
				for (String cmd : subcmds)
					items.add(cmd);
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("create")) {
					items.add("BagOfGoldBanker");
				}
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("remove")) {
					// add all npc which has the BagOfGold Trait
					// for (int i = 0; i < values.length; i++)
					// items.add(ChatColor.stripColor(values[i].translateName().replace("
					// ", "_")));
				}
			}
		}

		if (!args[args.length - 1].trim().isEmpty()) {
			String match = args[args.length - 1].trim().toLowerCase();
			Iterator<String> it = items.iterator();
			while (it.hasNext()) {
				String name = it.next();
				if (!name.toLowerCase().startsWith(match))
					it.remove();
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
		if (plugin.getCompatibilityManager().isPluginLoaded(CitizensCompat.class)) {
			npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
			if (npc == null && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")
					|| args[0].equalsIgnoreCase("spawn") || args[0].equalsIgnoreCase("despawn")
					|| args[0].equalsIgnoreCase("tphere") || args[0].equalsIgnoreCase("sethome"))) {
				plugin.getMessages().senderSendMessage(sender,
						plugin.getMessages().getString("bagofgold.commands.npc.no_npc_selected"));
				return true;
			}

			if (args.length == 1 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))) {
				npc.destroy();
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("spawn")) {
				npc.spawn(npc.getStoredLocation());
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("despawn")) {
				npc.despawn();
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("tphere")) {
				npc.teleport(((Player) sender).getLocation(), TeleportCause.PLUGIN);
				Block b = Tools.getTargetBlock((Player) sender, 200);
				if (b != null)
					npc.faceLocation(b.getLocation());
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("sethome")) {
				plugin.getMessages().senderSendMessage(sender,
						plugin.getMessages().getString("bagofgold.commands.npc.home_set"));
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("select")) {
				if (npc != null)
					plugin.getMessages().senderSendMessage(sender, plugin.getMessages().getString(
							"bagofgold.commands.npc.selected", "npcname", npc.getName(), "npcid", npc.getId()));
				else
					plugin.getMessages().senderSendMessage(sender,
							plugin.getMessages().getString("bagofgold.commands.npc.not_selected"));
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
				NPCRegistry registry = CitizensAPI.getNPCRegistry();
				npc = registry.createNPC(EntityType.PLAYER, "BagOfGoldBanker");
				npc.setBukkitEntityType(EntityType.VILLAGER);
				npc.addTrait(BagOfGoldBankerTrait.class);
				npc.spawn(p.getLocation());
				plugin.getMessages().senderSendMessage(sender, ChatColor.GREEN
						+ plugin.getMessages().getString("bagofgold.commands.npc.created", "npcid", npc.getId()));
				plugin.getMessages().debug("Creating BagOfGoldBanker: id=%s", npc.getId());
				return true;
			}

		}
		return false;
	}
}
