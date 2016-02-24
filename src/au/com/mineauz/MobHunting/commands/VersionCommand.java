package au.com.mineauz.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.update.UpdateHelper;
import au.com.mineauz.MobHunting.update.UpdateStatus;

public class VersionCommand implements ICommand {
	@Override
	public String getName() {
		return "version";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "ver", "-v" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.version";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { label + ChatColor.RED + " version"
				+ ChatColor.GOLD + " to get the version number" };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.version.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		sender.sendMessage(ChatColor.GREEN
				+ Messages.getString(
						"mobhunting.commands.version.version-number","version",
						MobHunting.instance.pluginVersion));
		if (UpdateHelper.getUpdateAvailable() == UpdateStatus.AVAILABLE)
			sender.sendMessage(ChatColor.GREEN
					+ Messages.getString(
							"mobhunting.commands.version.newversion-number","version",
							UpdateHelper.getBukkitUpdate().getVersionName()));
		if (sender.hasPermission("mobhunting.update")) {
			UpdateHelper.pluginUpdateCheck(sender, true, true);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
		return null;
	}

}
