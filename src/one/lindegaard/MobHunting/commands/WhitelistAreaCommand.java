package one.lindegaard.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Area;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class WhitelistAreaCommand implements ICommand {

	@Override
	public String getName() {
		return "whitelistarea";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mobhunting.whitelist";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { label + ChatColor.GREEN + " [add|remove]" };
	}

	@Override
	public String getDescription() {
		return Messages
				.getString("mobhunting.commands.whitelistarea.description");
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
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		Location loc = ((Player) sender).getLocation();

		if (args.length == 0) {
			if (MobHunting.getAreaManager().isWhitelisted(loc))
				sender.sendMessage(ChatColor.GREEN
						+ Messages
								.getString("mobhunting.commands.whitelistarea.iswhitelisted"));
			else
				sender.sendMessage(ChatColor.RED
						+ Messages
								.getString("mobhunting.commands.whitelistarea.notwhitelisted"));
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("remove")) {
				MobHunting.getAreaManager().unWhitelistArea(loc);
				sender.sendMessage(ChatColor.GREEN
						+ Messages
								.getString("mobhunting.commands.whitelistarea.remove.done"));
			} else if (args[0].equalsIgnoreCase("add")) {
				Area area = new Area();
				area.center = loc;
				area.range = 15;
				MobHunting.getAreaManager().whitelistArea(area);

				sender.sendMessage(ChatColor.GREEN
						+ Messages
								.getString("mobhunting.commands.whitelistarea.done"));
			} else
				return false;
		} else
			return false;

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
		return null;
	}

}
