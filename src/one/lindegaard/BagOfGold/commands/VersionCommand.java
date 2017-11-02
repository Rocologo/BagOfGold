package one.lindegaard.BagOfGold.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.Messages;
import one.lindegaard.BagOfGold.update.UpdateStatus;
import one.lindegaard.BagOfGold.update.Updater;
import one.lindegaard.MobHunting.MobHunting;

public class VersionCommand implements ICommand {

	private BagOfGold plugin;
	
	public VersionCommand(BagOfGold plugin) {
		this.plugin=plugin;
	}

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
		return "bagofgold.version";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " version"
				+ ChatColor.WHITE + " - to get the version number" };
	}

	@Override
	public String getDescription() {
		return Messages.getString("bagofgold.commands.version.description");
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

		MobHunting.getInstance().getMessages().senderSendMessage(sender,ChatColor.GREEN
				+ Messages.getString(
						"bagofgold.commands.version.currentversion","currentversion",
						BagOfGold.getInstance().getDescription().getVersion()));
		if (Updater.getUpdateAvailable() == UpdateStatus.AVAILABLE)
			MobHunting.getInstance().getMessages().senderSendMessage(sender,ChatColor.GREEN
					+ Messages.getString(
							"bagofgold.commands.version.newversion","newversion",
							Updater.getBukkitUpdate().getVersionName()));
		if (sender.hasPermission("bagofgold.update")) {
			Updater.pluginUpdateCheck(sender, true, true);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
		return null;
	}

}
