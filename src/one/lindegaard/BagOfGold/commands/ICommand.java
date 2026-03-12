package one.lindegaard.BagOfGold.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface ICommand {

	/**
	 * Gets the name of the command
	 */
	String getName();

	/**
	 * Gets any aliases this command has
	 * @return an array of the aliases or null if there are none
	 */
	String[] getAliases();

	/**
	 * Gets the permission that this command needs to be used, or null if there isn't one
	 */
	String getPermission();

	/**
	 * Gets the usage string for this command.
	 * @param label This is either the name of the command or an alias if they used one.
	 * @param sender The sender of the command
	 * @return The usage string
	 */
	String[] getUsageString(String label, CommandSender sender);

	/**
	 * Gets the description of the command for the help system
	 */
	String getDescription();

	/**
	 * Can the sender of this command be a console?
	 */
	boolean canBeConsole();

	/**
	 * Can the sender of this command be a command block?
	 */
	boolean canBeCommandBlock();

	/**
	 * Called when this command is executed.
	 * @param sender The sender of this command
	 * @param label The command name or alias used
	 * @param args The arguments for this command
	 * @return True if this command was executed, false otherwise
	 */
	boolean onCommand(CommandSender sender, String label, String[] args);

	/**
	 * Called when tab complete is used on this command.
	 * @param sender The sender of the tab complete
	 * @param label The command name or alias used
	 * @param args The current arguments entered
	 * @return A list of all results or null
	 */
	List<String> onTabComplete(CommandSender sender, String label, String[] args);
}
