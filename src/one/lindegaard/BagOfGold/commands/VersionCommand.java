package one.lindegaard.BagOfGold.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.update.UpdateStatus;

public class VersionCommand implements ICommand {

	private BagOfGold plugin;

	public VersionCommand(BagOfGold plugin) {
		this.plugin = plugin;
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
		return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " version" + ChatColor.WHITE
				+ " - to get the version number" };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgold.commands.version.description");
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
		plugin.getMessages().senderSendMessage(sender,
				ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.version.currentversion",
						"currentversion", plugin.getDescription().getVersion()));
		if (plugin.getSpigetUpdater().getUpdateAvailable() == UpdateStatus.AVAILABLE)
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.version.newversion",
							"newversion", plugin.getSpigetUpdater().getNewDownloadVersion()));
		else if (sender.hasPermission("bagofgold.update"))
			plugin.getSpigetUpdater().checkForUpdate(sender, true, true);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
