package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.command.TabCompleter;

import au.com.mineauz.MobHunting.Messages;

/**
 * This allows sub commands to be handled in a clean easily expandable way. Just
 * create a new command that implements ICommand Then register it with
 * registerCommand() in the static constructor
 * 
 * Try to keep names and aliases in lowercase
 * 
 * @author Schmoller
 *
 */
public class CommandDispatcher implements CommandExecutor, TabCompleter {
	private HashMap<String, ICommand> mCommands;
	private String mRootCommandName;
	private String mRootCommandDescription;

	public CommandDispatcher(String commandName, String description) {
		mRootCommandName = commandName;
		mRootCommandDescription = description;

		mCommands = new HashMap<String, ICommand>();

		registerCommand(new InternalHelp());
	}

	/**
	 * Registers a command to be handled by this dispatcher
	 * 
	 * @param command
	 */
	public void registerCommand(ICommand command) {
		mCommands.put(command.getName().toLowerCase(), command);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length == 0) {
			displayUsage(sender, label, null);
			return true;
		}

		String subCommand = args[0].toLowerCase();
		String[] subArgs = (args.length > 1 ? Arrays.copyOfRange(args, 1,
				args.length) : new String[0]);

		ICommand com = null;
		if (mCommands.containsKey(subCommand)) {
			com = mCommands.get(subCommand);
		} else {
			// Check aliases
			AliasCheck: for (Entry<String, ICommand> ent : mCommands.entrySet()) {
				if (ent.getValue().getAliases() != null) {
					String[] aliases = ent.getValue().getAliases();
					for (String alias : aliases) {
						if (subCommand.equalsIgnoreCase(alias)) {
							com = ent.getValue();
							break AliasCheck;
						}
					}
				}
			}
		}

		// Was not found
		if (com == null) {
			displayUsage(sender, label, subCommand);
			return true;
		}

		// Check that the sender is correct
		if (!com.canBeConsole()
				&& (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender)) {
			sender.sendMessage(ChatColor.RED
					+ Messages
							.getString(
									"mobhunting.commands.base.noconsole", "command", "/" + label + " " + subCommand)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return true;
		}
		if (!com.canBeCommandBlock() && sender instanceof BlockCommandSender) {
			sender.sendMessage(ChatColor.RED
					+ Messages
							.getString(
									"mobhunting.commands.base.nocommandblock", "command", "/" + label + " " + subCommand)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return true;
		}

		// Check that they have permission
		if (com.getPermission() != null
				&& !sender.hasPermission(com.getPermission())) {
			sender.sendMessage(ChatColor.RED
					+ Messages
							.getString(
									"mobhunting.commands.base.nopermission", "command", "/" + label + " " + subCommand)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			return true;
		}

		if (!com.onCommand(sender, subCommand, subArgs)) {
			String[] lines = com.getUsageString(subCommand, sender);
			String usageString = ""; //$NON-NLS-1$

			for (String line : lines) {
				if (lines.length > 1)
					usageString += "\n    "; //$NON-NLS-1$

				usageString += ChatColor.GRAY + "/" + label + " " + line; //$NON-NLS-1$ //$NON-NLS-2$
			}

			usageString = ChatColor.RED
					+ Messages
							.getString(
									"mobhunting.commands.base.usage", "usage", usageString); //$NON-NLS-1$ //$NON-NLS-2$
			sender.sendMessage(usageString);
		}

		return true;
	}

	private void displayUsage(CommandSender sender, String label,
			String subcommand) {
		String usage = ""; //$NON-NLS-1$

		if (subcommand != null) {
			sender.sendMessage(ChatColor.RED
					+ Messages
							.getString(
									"mobhunting.commands.base.unknowncommand", "command", ChatColor.RESET + "/" + label + " " + ChatColor.GOLD + subcommand)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			sender.sendMessage(Messages
					.getString("mobhunting.commands.base.validcommands")); //$NON-NLS-1$
		} else {
			sender.sendMessage(ChatColor.RED
					+ Messages
							.getString(
									"mobhunting.commands.base.nocommand", "command", ChatColor.RESET + "/" + label + ChatColor.GOLD + " <command>")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			sender.sendMessage(Messages
					.getString("mobhunting.commands.base.validcommands")); //$NON-NLS-1$
		}

		boolean first = true;
		boolean odd = true;
		// Build the list
		for (Entry<String, ICommand> ent : mCommands.entrySet()) {
			if (odd)
				usage += ChatColor.WHITE;
			else
				usage += ChatColor.GRAY;
			odd = !odd;

			if (first)
				usage += ent.getKey();
			else
				usage += ", " + ent.getKey(); //$NON-NLS-1$

			first = false;
		}

		sender.sendMessage(usage);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String label, String[] args) {
		List<String> results = new ArrayList<String>();
		if (args.length == 1) // Tab completing the sub command
		{
			for (ICommand registeredCommand : mCommands.values()) {
				if (registeredCommand.getName().toLowerCase()
						.startsWith(args[0].toLowerCase()))
					results.add(registeredCommand.getName()+" ");
			}
		} else {
			// Find the command to use
			String subCommand = args[0].toLowerCase();

			ICommand com = null;
			if (mCommands.containsKey(subCommand)) {
				com = mCommands.get(subCommand);
			} else {
				// Check aliases
				AliasCheck: for (Entry<String, ICommand> ent : mCommands
						.entrySet()) {
					if (ent.getValue().getAliases() != null) {
						String[] aliases = ent.getValue().getAliases();
						for (String alias : aliases) {
							if (subCommand.equalsIgnoreCase(alias)) {
								com = ent.getValue();
								break AliasCheck;
							}
						}
					}
				}
			}

			// Was not found
			if (com == null) {
				return results;
			}

			// Check that the sender is correct
			if (!com.canBeConsole() && sender instanceof ConsoleCommandSender) {
				return results;
			}

			// Check that they have permission
			if (com.getPermission() != null
					&& !sender.hasPermission(com.getPermission())) {
				return results;
			}

			String[] subArgs = (args.length > 1 ? Arrays.copyOfRange(args, 1,
					args.length) : new String[0]);
			results = com.onTabComplete(sender, subCommand, subArgs);
			if (results == null)
				return new ArrayList<String>();
		}
		return results;
	}

	private class InternalHelp implements ICommand {

		@Override
		public String getName() {
			return "help"; //$NON-NLS-1$
		}

		@Override
		public String[] getAliases() {
			return null;
		}

		@Override
		public String getPermission() {
			return null;
		}

		@Override
		public String[] getUsageString(String label, CommandSender sender) {
			return new String[] { label };
		}

		@Override
		public String getDescription() {
			return Messages
					.getString("mobhunting.commands.base.help.description"); //$NON-NLS-1$
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
		public boolean onCommand(CommandSender sender, String label,
				String[] args) {
			if (args.length != 0)
				return false;

			sender.sendMessage(ChatColor.GOLD + mRootCommandDescription);
			sender.sendMessage(ChatColor.GOLD
					+ Messages
							.getString("mobhunting.commands.base.help.commands")); //$NON-NLS-1$

			for (ICommand command : mCommands.values()) {
				// Dont show commands that are irrelevant
				if (!command.canBeCommandBlock()
						&& sender instanceof BlockCommandSender)
					continue;
				if (!command.canBeConsole()
						&& (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender))
					continue;

				if (command.getPermission() != null
						&& !sender.hasPermission(command.getPermission()))
					continue;

				String usageString = ""; //$NON-NLS-1$
				boolean first = true;
				for (String line : command.getUsageString(command.getName(),
						sender)) {
					if (!first)
						usageString += "\n"; //$NON-NLS-1$

					first = false;

					usageString += ChatColor.GOLD
							+ "/" + mRootCommandName + " " + line; //$NON-NLS-1$ //$NON-NLS-2$
				}

				sender.sendMessage(usageString
						+ "\n  " + ChatColor.WHITE + command.getDescription()); //$NON-NLS-1$
			}
			return true;
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, String label,
				String[] args) {
			return null;
		}

	}
}
