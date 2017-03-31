package one.lindegaard.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.grinding.Area;

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
		return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " [add|remove]" + ChatColor.WHITE
				+ " - to whitelist an area." };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.whitelistarea.description");
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
			if (MobHunting.getGrindingManager().isWhitelisted(loc))
				sender.sendMessage(
						ChatColor.GREEN + Messages.getString("mobhunting.commands.whitelistarea.iswhitelisted"));
			else
				sender.sendMessage(
						ChatColor.RED + Messages.getString("mobhunting.commands.whitelistarea.notwhitelisted"));
		} else if (args.length == 1) {
			if (args[0].equalsIgnoreCase("remove")) {
				MobHunting.getGrindingManager().unWhitelistArea(loc);
				sender.sendMessage(
						ChatColor.GREEN + Messages.getString("mobhunting.commands.whitelistarea.remove.done"));
			} else if (args[0].equalsIgnoreCase("add")) {
				Area area = new Area(loc, MobHunting.getConfigManager().grindingDetectionRange, 0);
				MobHunting.getGrindingManager().whitelistArea(area);
				sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.whitelistarea.done"));
			} else
				return false;
		} else
			return false;

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
