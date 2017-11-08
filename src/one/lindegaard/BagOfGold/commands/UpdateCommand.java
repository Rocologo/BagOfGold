package one.lindegaard.BagOfGold.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.update.UpdateStatus;
import one.lindegaard.BagOfGold.update.Updater;

public class UpdateCommand implements ICommand {

	private BagOfGold plugin;
	private Updater updater;

	public UpdateCommand(BagOfGold plugin) {
		this.plugin = plugin;
		updater = new Updater(plugin);
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
		if (updater.getUpdateAvailable() == UpdateStatus.AVAILABLE) {
			if (Updater.downloadAndUpdateJar()) {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.update.complete"));
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.update.could-not-update"));
			}
		} else if (updater.getUpdateAvailable() == UpdateStatus.RESTART_NEEDED) {
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.update.complete"));
		} else {
			updater.pluginUpdateCheck(sender, true, false);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
