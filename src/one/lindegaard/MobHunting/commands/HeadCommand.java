package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.bounty.BountyManager;
import one.lindegaard.MobHunting.bounty.BountyStatus;
import one.lindegaard.MobHunting.storage.UserNotFoundException;

public class HeadCommand implements ICommand {

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
				ChatColor.GOLD + label + ChatColor.GREEN + " spawn" + " [displayname]"
						+ " [playername|mobname] [amount] [playername|xpos ypos zpos] " + ChatColor.YELLOW + ""
						+ ChatColor.WHITE + "       - to spawn a head",
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

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		@SuppressWarnings("deprecation")
		OfflinePlayer bountyOwner = Bukkit.getOfflinePlayer(sender.getName());
		if (args.length >= 1 && args[0].equalsIgnoreCase("spawn")) {

			return true;

		} else if (args.length >= 1 && (args[0].equalsIgnoreCase("rename"))) {

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
}
