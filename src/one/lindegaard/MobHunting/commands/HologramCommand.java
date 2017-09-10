package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.StatType;
import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.CompatibilityManager;
import one.lindegaard.MobHunting.leaderboard.HologramLeaderboard;
import one.lindegaard.MobHunting.storage.TimePeriod;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class HologramCommand implements ICommand, Listener {

	private MobHunting plugin;

	public HologramCommand(MobHunting plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	// Used case (???)
	// /mh hologram create hologramid <stat type> <period> <number>
	// /mh hologram remove hologramid 
	// /mh hologram update hologramid 
	// /mh hologram list hologramid 

	@Override
	public String getName() {
		return "hologram";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "hg", "holographicdisplay", "holograms" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.hologram";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				ChatColor.GOLD + label + ChatColor.GREEN + " create <stattype> <period> <number>" + ChatColor.WHITE
						+ " - to create a Holographic Leadaderboard",
				ChatColor.GOLD + label + ChatColor.GREEN + " remove" + ChatColor.WHITE
						+ " - to remove a Holographic Leadaderboard",
				ChatColor.GOLD + label + ChatColor.GREEN + " update" + ChatColor.WHITE
						+ " - to update a Holographic Leadaderboard" };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.hologram.description");
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

		String[] subcmds = { "create", "delete", "list", "update" };
		ArrayList<String> items = new ArrayList<String>();
		if (CompatibilityManager.isPluginLoaded(CitizensCompat.class)) {
			if (args.length == 1) {
				for (String cmd : subcmds)
					items.add(cmd);
			} else if (args.length == 3) {
				if (args[0].equalsIgnoreCase("create")) {
					StatType[] values = StatType.values();
					for (int i = 0; i < values.length; i++)
						if (values[i] != null)
							items.add(ChatColor.stripColor(values[i].translateName().replace(" ", "_")));
				}
			} else if (args.length == 4) {
				if (args[0].equalsIgnoreCase("create")) {
					TimePeriod[] values = TimePeriod.values();
					for (int i = 0; i < values.length; i++)
						items.add(ChatColor.stripColor(values[i].translateName().replace(" ", "_")));
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
		if (CompatibilityManager.isPluginLoaded(CitizensCompat.class)) {

			if (args.length == 2 && (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))) {

				String hologramName = args[1];
				if (plugin.getLeaderboardManager().getHologramManager().getHolograms().containsKey(hologramName)) {
					plugin.getLeaderboardManager().deleteHologramLeaderboard(hologramName);
					sender.sendMessage(Messages.getString("mobhunting.commands.hologram.deleted","hologramid",hologramName));
				} else
					sender.sendMessage(ChatColor.RED
							+ Messages.getString("mobhunting.commands.hologram.unknown", "hologramid", args[1]));
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("update")) {
				String hologramName = args[1];
				if (plugin.getLeaderboardManager().getHologramManager().getHolograms().containsKey(hologramName)) {
					sender.sendMessage(Messages.getString("mobhunting.commands.hologram.updating"));
					plugin.getLeaderboardManager().getHologramManager().updateHolographicLeaderboard(hologramName);
				} else
					sender.sendMessage(ChatColor.RED
							+ Messages.getString("mobhunting.commands.hologram.unknown", "hologramid", args[1]));
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("select")) {
				sender.sendMessage(Messages.getString("mobhunting.commands.hologram.selected", "xxx", "yyy"));
				return true;

			} else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
				String res = plugin.getLeaderboardManager().getHologramManager().listHolographicLeaderboard();
				sender.sendMessage(res);
				return true;
			} else if (args.length == 5 && args[0].equalsIgnoreCase("create")) {
				String hologramName = args[1];
				if (!plugin.getLeaderboardManager().getHologramManager().getHolograms().containsKey(hologramName)) {
					StatType statType = StatType.parseStat(args[2]);
					if (statType == null) {
						sender.sendMessage(ChatColor.RED
								+ Messages.getString("mobhunting.commands.base.unknown_stattype", "stattype", args[2]));
						return true;
					}
					TimePeriod period = TimePeriod.parsePeriod(args[3]);
					if (period == null) {
						sender.sendMessage(ChatColor.RED
								+ Messages.getString("mobhunting.commands.base.unknown_timeperiod", "period", args[3]));
						return true;
					}
					int no_of_lines = Integer.valueOf(args[4]);
					if (no_of_lines < 1 || no_of_lines > 25) {
						sender.sendMessage(ChatColor.RED + Messages
								.getString("mobhunting.commands.hologram.too_many_lines", "no_of_lines", args[4]));
						return true;
					}

					// create hologram
					Location location = ((Player) sender).getLocation();
					plugin.getLeaderboardManager().getHologramManager()
							.createHolographicLeaderboard(new HologramLeaderboard(plugin, hologramName, statType,
									period, no_of_lines, location.add(0, 2, 0)), location);
					plugin.getLeaderboardManager().saveHologramLeaderboard(hologramName);
					sender.sendMessage(ChatColor.GREEN
							+ Messages.getString("mobhunting.commands.hologram.created", "hologramid", hologramName));
					Messages.debug("Creating Hologram Leaderbard: id=%s,stat=%s,per=%s,rank=%s", hologramName,
							statType.translateName(), period, no_of_lines);
					return true;
				} else {
					sender.sendMessage(Messages.getString("mobhunting.commands.hologram.hologram_exists", "hologramid",
							hologramName));
				}
			}

		}
		return false;
	}
}
