package one.lindegaard.BagOfGold.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.storage.DataStoreException;

public class DatabaseCommand implements ICommand, Listener {

	private BagOfGold plugin;

	public DatabaseCommand(BagOfGold plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "database";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "db" };
	}

	@Override
	public String getPermission() {
		return "bagofgold.database";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " deleteoldplayers" };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgold.commands.database.description");
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
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {

		ArrayList<String> items = new ArrayList<String>();
		if (args.length == 1) {
			items.add("deleteoldplayers");
			// items.add("backup");
			// items.add("restore");
			// items.add("deletebackup");
		}
		if (!args[args.length - 1].trim().isEmpty()) {
			String match = args[args.length - 1].trim().toLowerCase();
			items.removeIf(name -> !name.toLowerCase().startsWith(match));
		}
		return items;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (args.length == 0)
			return false;
		if (args.length == 1 && (args[0].equalsIgnoreCase("deleteoldplayers"))) {
			try {
				plugin.getStoreManager().databaseDeleteOldPlayers();
				Core.getStoreManager().databaseDeleteOldPlayers();
			} catch (DataStoreException e) {
				e.printStackTrace();
			}
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("backup")) {
			// TODO: create a backup
			plugin.getMessages().senderSendMessage(sender, "Backup feature is not implemented yet.");
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("restore")) {
			// TODO: restore a backup
			plugin.getMessages().senderSendMessage(sender, "Restore feature is not implemented yet.");
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("deletebackup")) {
			// TODO: restore a backup
			plugin.getMessages().senderSendMessage(sender, "Deletebackup feature is not implemented yet.");
			return true;
		}
		return false;
	}
}
