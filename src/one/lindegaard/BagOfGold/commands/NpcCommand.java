package one.lindegaard.BagOfGold.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.bank.BagOfGoldBankerTrait;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.Tools;
import one.lindegaard.CustomItemsLib.rewards.CoreCustomItems;
import one.lindegaard.CustomItemsLib.rewards.Reward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
			npc = getSelectedBagOfGoldBanker(sender);
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
				Location target = ((Player) sender).getLocation().clone();
				target.setPitch(0F);
				npc.teleport(target, TeleportCause.PLUGIN);
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("sethome")) {
				plugin.getMessages().senderSendMessage(sender,
						plugin.getMessages().getString("bagofgold.commands.npc.home_set"));
				return true;

			} else if ((args.length == 1 || args.length == 2) && args[0].equalsIgnoreCase("select")) {
				NPC selectedNpc = null;
				if (args.length == 2) {
					try {
						int id = Integer.parseInt(args[1]);
						selectedNpc = getBagOfGoldBankerById(id);
					} catch (NumberFormatException ex) {
						selectedNpc = null;
					}
				} else {
					selectedNpc = findLookingAtBagOfGoldBanker(p, 20);
					if (selectedNpc == null) {
						selectedNpc = getSelectedBagOfGoldBanker(sender);
					}
				}

				if (selectedNpc != null) {
					CitizensAPI.getDefaultNPCSelector().select(sender, selectedNpc);
					plugin.getMessages().senderSendMessage(sender, plugin.getMessages().getString(
							"bagofgold.commands.npc.selected", "npcname", selectedNpc.getName(), "npcid", selectedNpc.getId()));
				} else {
					plugin.getMessages().senderSendMessage(sender,
							plugin.getMessages().getString("bagofgold.commands.npc.not_selected"));
				}
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
				npc = createBagOfGoldBanker(p.getLocation());
				CitizensAPI.getDefaultNPCSelector().select(sender, npc);
				plugin.getMessages().senderSendMessage(sender, ChatColor.GREEN
						+ plugin.getMessages().getString("bagofgold.commands.npc.created", "npcid", npc.getId()));
				plugin.getMessages().debug("Creating BagOfGoldBanker: id=%s", npc.getId());
				return true;
			}

		}
		return false;
	}

	public NPC createBagOfGoldBanker(Location spawnLocation) {
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.createNPC(EntityType.PLAYER, "BagOfGoldBanker");
		npc.addTrait(BagOfGoldBankerTrait.class);
		npc.getOrAddTrait(LookClose.class).setRange(6);
		npc.getOrAddTrait(LookClose.class).setRealisticLooking(true);
		npc.getOrAddTrait(LookClose.class).toggle();
		npc.getOrAddTrait(SkinTrait.class).setSkinPersistent(plugin.getConfigManager().bankerName,
				plugin.getConfigManager().bankerSignature, plugin.getConfigManager().bankerTexture);
		ItemStack is = CoreCustomItems.getCustomtexture(new Reward(),
				Core.getConfigManager().skullTextureValue, Core.getConfigManager().skullTextureSignature);
		npc.getOrAddTrait(Equipment.class).set(EquipmentSlot.OFF_HAND, is);
		npc.setName(plugin.getConfigManager().bankerName);
		npc.spawn(spawnLocation);
		npc.setProtected(true);
		return npc;
	}

	public boolean isBagOfGoldBanker(NPC npc) {
		return npc != null && npc.hasTrait(BagOfGoldBankerTrait.class);
	}

	private NPC getSelectedBagOfGoldBanker(CommandSender sender) {
		NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
		return isBagOfGoldBanker(npc) ? npc : null;
	}

	private NPC getBagOfGoldBankerById(int id) {
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		return isBagOfGoldBanker(npc) ? npc : null;
	}

	private NPC findLookingAtBagOfGoldBanker(Player player, int maxDistance) {
		Block target = Tools.getTargetBlock(player, maxDistance);
		Location targetLoc = target == null ? null : target.getLocation().add(0.5, 0.5, 0.5);
		Location eye = player.getEyeLocation();
		Vector direction = eye.getDirection().normalize();

		NPC best = null;
		double bestDistance = Double.MAX_VALUE;
		double bestDot = -1D;
		for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
			if (!isBagOfGoldBanker(npc) || !npc.isSpawned()) {
				continue;
			}
			Location npcLoc = npc.getEntity().getLocation();
			if (npcLoc.getWorld() == null || !npcLoc.getWorld().equals(player.getWorld())) {
				continue;
			}

			double distanceToPlayer = npcLoc.distance(player.getLocation());
			if (distanceToPlayer > maxDistance) {
				continue;
			}

			if (targetLoc != null) {
				double distanceToTarget = npcLoc.distance(targetLoc);
				if (distanceToTarget <= 2.5 && distanceToPlayer < bestDistance) {
					bestDistance = distanceToPlayer;
					best = npc;
				}
			}

			Vector toNpc = npcLoc.clone().add(0, 1.62, 0).toVector().subtract(eye.toVector());
			double length = toNpc.length();
			if (length <= 0.0001D) {
				continue;
			}
			double dot = direction.dot(toNpc.normalize());
			if (dot < 0.985D) {
				continue;
			}

			if (dot > bestDot || (dot == bestDot && distanceToPlayer < bestDistance)) {
				bestDot = dot;
				bestDistance = distanceToPlayer;
				best = npc;
			}
		}
		return best;
	}
}
