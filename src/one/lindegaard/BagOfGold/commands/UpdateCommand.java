package one.lindegaard.BagOfGold.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.update.UpdateStatus;

public class UpdateCommand implements ICommand {

	private BagOfGold plugin;

	public UpdateCommand(BagOfGold plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "update";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "bagofgold.update";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.WHITE + " - to download and update the plugin." };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgold.commands.update.description");
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
		if (plugin.getSpigetUpdater().getUpdateAvailable() == UpdateStatus.AVAILABLE)
			plugin.getSpigetUpdater().checkForUpdate(sender, false, true);
		else if (plugin.getSpigetUpdater().getUpdateAvailable() == UpdateStatus.RESTART_NEEDED)
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.update.complete"));
		else
			plugin.getSpigetUpdater().checkForUpdate(sender, false, true);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
