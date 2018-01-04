package one.lindegaard.BagOfGold.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.update.SpigetUpdater;
import one.lindegaard.BagOfGold.update.UpdateStatus;
//import one.lindegaard.BagOfGold.update.BukkitUpdater;

public class UpdateCommand implements ICommand {

	private BagOfGold plugin;
	private SpigetUpdater updater;

	public UpdateCommand(BagOfGold plugin) {
		this.plugin = plugin;
		updater = new SpigetUpdater(plugin);
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
			if (SpigetUpdater.downloadAndUpdateJar()) {
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
			updater.checkForUpdate(sender, true, false);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
