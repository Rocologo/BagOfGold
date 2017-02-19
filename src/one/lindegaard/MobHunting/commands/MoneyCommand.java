package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockIterator;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.rewards.RewardManager;
import one.lindegaard.MobHunting.util.Misc;

public class MoneyCommand implements ICommand {

	public static int minutesToRun = 0;
	public static int minutesLeft = 0;
	public static double multiplier = 1;
	long starttime;

	BukkitTask happyhourevent = null;
	BukkitTask happyhoureventStop = null;

	public MoneyCommand() {

	}

	// Admin commnand
	// /mh money drop <amount> - to drop <amount money> where player look.
	// Permission needed mobhunt.money.drop

	@Override
	public String getName() {
		return "money";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "gold", "bag" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.money";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				ChatColor.GOLD + label + ChatColor.GREEN + " drop" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to drop <amount> of Bag of gold, where you look.",
				ChatColor.RED + "TODO: " + ChatColor.GOLD + label + ChatColor.GREEN + " give <player>"
						+ ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to give the player a bag of gold in his inventory.",
				ChatColor.RED + "TODO: " + ChatColor.GOLD + label + ChatColor.GREEN + " take <player>"
						+ ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to remove <amount> gold from the bag of gold in the players inventory",
				ChatColor.RED + "TODO: " + ChatColor.GOLD + label + ChatColor.GREEN + " sell" + ChatColor.YELLOW
						+ " <amount>" + ChatColor.WHITE
						+ " - to sell some of the gold in your bag of gold and get the money.",
				ChatColor.RED + "TODO: " + ChatColor.GOLD + label + ChatColor.GREEN + " buy" + ChatColor.YELLOW
						+ " <amount>" + ChatColor.WHITE
						+ " - to buy some more gold with your money and put it into your bag of gold." };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.money.description");
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
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		if (args.length == 0) {

			// mh money
			// show the total amount of "bag of gold" in the players inventory.
			sender.sendMessage(ChatColor.RED + "Not implemented yet.");
			return true;

		} else if (args.length == 1) {

			// /mh money help
			// Show help
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
				return false;

		} else if (args.length >= 2) {
			// /mh money drop <amount>
			if (args[0].equalsIgnoreCase("drop") || args[0].equalsIgnoreCase("place")) {
				if (sender instanceof Player) {
					if (sender.hasPermission("mobhunting.money.drop")) {
						if (args[1].matches("\\d+(\\.\\d+)?")) {
							Player player = (Player) sender;
							Block b = Misc.getTargetBlock(player, 100);
							Location location = b.getLocation();
							if (location != null) {
								RewardManager.dropMoneyOnGround(player, null, location, Double.valueOf(args[1]));
								Messages.playerActionBarMessage(player,
										Messages.getString("mobhunting.moneydrop", "money", Double.valueOf(args[1])));
							} else {
								sender.sendMessage(ChatColor.RED
										+ Messages.getString("mobhunting.commands.money.look-at-location"));
							}
						} else {
							sender.sendMessage(ChatColor.RED
									+ Messages.getString("mobhunting.commands.base.not_a_number", "number", args[1]));
						}
					} else {
						sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.money.no-permission",
								"permission", "mobhunting.money.drop"));
					}
				} else {
					sender.sendMessage(
							Messages.getString(ChatColor.RED + "mobhunting.commands.money.not-from-console"));
				}

				return true;
			}

			if (args[0].equalsIgnoreCase("give")) {
				sender.sendMessage(ChatColor.RED + "Not implemented yet.");
				return true;
			}

			if (args[0].equalsIgnoreCase("take")) {
				sender.sendMessage(ChatColor.RED + "Not implemented yet.");
				return true;
			}

			if (args[0].equalsIgnoreCase("sell")) {
				sender.sendMessage(ChatColor.RED + "Not implemented yet.");
				return true;
			}

			if (args[0].equalsIgnoreCase("buy")) {
				sender.sendMessage(ChatColor.RED + "Not implemented yet.");
				return true;
			}

		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		ArrayList<String> items = new ArrayList<String>();
		if (args.length == 1) {
			items.add("drop");
			items.add("give");
			items.add("take");
			items.add("sell");
			items.add("buy");
		}
		return items;
	}
}
