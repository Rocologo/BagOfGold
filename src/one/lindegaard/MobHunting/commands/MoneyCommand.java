package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.rewards.HiddenRewardData;
import one.lindegaard.MobHunting.rewards.RewardManager;
import one.lindegaard.MobHunting.util.Misc;

public class MoneyCommand implements ICommand {

	public MoneyCommand() {

	}

	// Admin commnand
	// /mh money drop <amount> - to drop <amount money> where player look.
	// Permission needed mobhunt.money.drop

	// /mh money drop <playername> <amount> - to drop <amount money> where
	// player look.
	// Permission needed mobhunt.money.drop

	// /mh money sell - to sell all bag of gold you are holding in your hand.
	// Permission needed mobhunt.money.sell

	// /mh money sell <amount> - to sell <amount money> of the bag of gold you
	// have in your hand.
	// Permission needed mobhunt.money.sell

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
				ChatColor.GOLD + label + ChatColor.GREEN + " drop <amount>" + ChatColor.WHITE
						+ " - to drop <amount> of Bag of gold, where you look.",
				ChatColor.GOLD + label + ChatColor.GREEN + " drop <playername> " + ChatColor.YELLOW + "<amount>"
						+ ChatColor.WHITE + " - to drop <amount> of Bag of gold 3 block in front of the <player>.",
				ChatColor.GOLD + label + ChatColor.GREEN + " give <player>" + ChatColor.YELLOW + " <amount>"
						+ ChatColor.WHITE + " - to give the player a bag of gold in his inventory.",
				ChatColor.GOLD + label + ChatColor.GREEN + " take <player>" + ChatColor.YELLOW + " <amount>"
						+ ChatColor.WHITE + " - to take <amount> gold from the bag of gold in the players inventory",
				ChatColor.GOLD + label + ChatColor.GREEN + " sell" + ChatColor.WHITE
						+ " - to sell the bag of gold in your hand.",
				ChatColor.GOLD + label + ChatColor.GREEN + " sell" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to sell some of the gold in your bag of gold and get the money.",
				ChatColor.GOLD + label + ChatColor.GREEN + " buy" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
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

		}

		if (args.length >= 2 && args[0].equalsIgnoreCase("drop") || args[0].equalsIgnoreCase("place")) {
			// /mh money drop <amount>
			// /mh money drop <player> <amount>
			if (sender.hasPermission("mobhunting.money.drop")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					sender.sendMessage(
							Messages.getString(ChatColor.RED + "mobhunting.commands.base.playername-missing"));
				} else {
					if (args[1].matches("\\d+(\\.\\d+)?")) {
						Player player = (Player) sender;
						Block b = Misc.getTargetBlock(player, 20);
						Location location = b.getLocation();
						if (location != null) {
							RewardManager.dropMoneyOnGround(player, null, location, Double.valueOf(args[1]));
							Messages.playerActionBarMessage(player,
									Messages.getString("mobhunting.moneydrop", "money", Double.valueOf(args[1])));
						}
					} else if (Bukkit.getServer().getOfflinePlayer(args[1]).isOnline()) {
						if (args[2].matches("\\d+(\\.\\d+)?")) {
							Player player = ((Player) Bukkit.getServer().getOfflinePlayer(args[1]));
							Block block = Misc.getTargetBlock(player, 3);
							if (block != null) {
								RewardManager.dropMoneyOnGround(player, null, block.getLocation(),
										Double.valueOf(args[2]));
							}
							Messages.playerActionBarMessage(player,
									Messages.getString("mobhunting.moneydrop", "money", Double.valueOf(args[2])));
						} else {
							sender.sendMessage(ChatColor.RED
									+ Messages.getString("mobhunting.commands.base.not_a_number", "number", args[2]));
						}
					} else {
						sender.sendMessage(Messages.getString(
								ChatColor.RED + "mobhunting.commands.base.playername-missing", "player", args[1]));
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.drop", "command", "money drop"));
			}
			return true;

		} else if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
			// /mh money give <player> <amount>
			if (sender.hasPermission("mobhunting.money.give")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					sender.sendMessage(
							Messages.getString(ChatColor.RED + "mobhunting.commands.base.playername-missing"));
				} else {
					if (Bukkit.getServer().getOfflinePlayer(args[1]).isOnline()) {
						if (args[2].matches("\\d+(\\.\\d+)?")) {
							Player player = ((Player) Bukkit.getServer().getOfflinePlayer(args[1]));
							RewardManager.dropMoneyOnGround(player, null, player.getLocation(),
									Double.valueOf(args[2]));
							Messages.playerActionBarMessage(player,
									Messages.getString("mobhunting.money.give", "money", Double.valueOf(args[2])));
							sender.sendMessage(Messages.getString("mobhunting.commands.money.give-sender", "money",
									Double.valueOf(args[2]), "player", player.getName()));
						} else {
							sender.sendMessage(ChatColor.RED
									+ Messages.getString("mobhunting.commands.base.not_a_number", "number", args[2]));
						}
					} else {
						sender.sendMessage(Messages.getString(
								ChatColor.RED + "mobhunting.commands.base.playername-missing", "player", args[1]));
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.give", "command", "money give"));
			}
			return true;

		} else

		if (args.length >= 2 && args[0].equalsIgnoreCase("take")) {
			// /mh money take <player> <amount>
			if (sender.hasPermission("mobhunting.money.take")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					sender.sendMessage(
							Messages.getString(ChatColor.RED + "mobhunting.commands.base.playername-missing"));
				} else {
					if (Bukkit.getServer().getOfflinePlayer(args[1]).isOnline()) {
						if (args[2].matches("\\d+(\\.\\d+)?")) {
							Player player = ((Player) Bukkit.getServer().getOfflinePlayer(args[1]));

							double taken = 0;
							double rest = Double.valueOf(args[2]);
							ListIterator<ItemStack> itr = player.getInventory().iterator();
							while (itr.hasNext()) {
								ItemStack is = (ItemStack) itr.next();
								if (HiddenRewardData.hasHiddenRewardData(is)) {
									HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
									double saldo = hiddenRewardData.getMoney();
									if (saldo >= rest) {
										hiddenRewardData.setMoney(saldo - rest);
										taken = taken + rest;
										rest = 0;
										break;
									} else {
										hiddenRewardData.setMoney(0);
										is.setType(Material.AIR);
										is.setAmount(0);
										taken = taken + saldo;
										rest = rest - saldo;
									}
								}
							}

							Messages.playerActionBarMessage(player, Messages.getString("mobhunting.commands.money.take",
									"money", RewardManager.getEconomy().format(taken)));
							sender.sendMessage(Messages.getString("mobhunting.commands.money.take-sender", "money",
									RewardManager.getEconomy().format(taken), "player", player.getName()));
						} else {
							sender.sendMessage(ChatColor.RED
									+ Messages.getString("mobhunting.commands.base.not_a_number", "number", args[2]));
						}
					} else {
						sender.sendMessage(Messages.getString(
								ChatColor.RED + "mobhunting.commands.base.playername-missing", "player", args[1]));
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.take", "command", "money take"));
			}
			return true;

		} else if (args[0].equalsIgnoreCase("sell")
				|| (args[0].equalsIgnoreCase("sell") && (args[1].matches("\\d+(\\.\\d+)?")))) {
			// /mh money sell
			// /mh money sell <amount>
			if (sender.hasPermission("mobhunting.money.sell")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Messages.getString(ChatColor.RED + "mobhunting.commands.base.noconsole",
							"command", "'money sell'"));
					return true;
				}

				Player player = (Player) sender;
				if (args.length == 1) {
					ItemStack is = player.getItemInHand();
					if (HiddenRewardData.hasHiddenRewardData(is)) {
						HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
						RewardManager.getEconomy().depositPlayer(player, hiddenRewardData.getMoney());
						is.setType(Material.AIR);
						is.setAmount(0);
						Messages.playerActionBarMessage(player, Messages.getString("mobhunting.commands.money.sell",
								"money", RewardManager.getEconomy().format(hiddenRewardData.getMoney())));
						return true;
					}
				} else if ((args[0].equalsIgnoreCase("sell") && (args[1].matches("\\d+(\\.\\d+)?")))) {
					double sold = 0;
					double toBeSold = Double.valueOf(args[1]);
					ListIterator<ItemStack> itr = player.getInventory().iterator();
					while (itr.hasNext()) {
						ItemStack is = (ItemStack) itr.next();
						if (HiddenRewardData.hasHiddenRewardData(is)) {
							HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
							double saldo = hiddenRewardData.getMoney();
							if (saldo >= toBeSold) {
								hiddenRewardData.setMoney(saldo - toBeSold);
								sold = sold + toBeSold;
								toBeSold = 0;
								break;
							} else {
								hiddenRewardData.setMoney(0);
								is.setType(Material.AIR);
								is.setAmount(0);
								sold = sold + saldo;
								toBeSold = toBeSold - saldo;
							}
						}
					}
					RewardManager.getEconomy().depositPlayer(player, sold);
					Messages.playerActionBarMessage(player, Messages.getString("mobhunting.commands.money.sell",
							"money", RewardManager.getEconomy().format(sold)));
				}
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.sell", "command", "money sell"));
			}
		}

		else if (args.length >= 2 && args[0].equalsIgnoreCase("buy")) {
			// /mh money buy <amount>
			if (sender.hasPermission("mobhunting.money.buy")) {
				if (args.length == 2 && args[1].matches("\\d+(\\.\\d+)?")) {
					Player player = (Player) sender;
					RewardManager.dropMoneyOnGround(player, null, player.getLocation(), Double.valueOf(args[1]));
					RewardManager.getEconomy().withdrawPlayer(player, Double.valueOf(args[1]));
					Messages.playerActionBarMessage(player,
							Messages.getString("mobhunting.commands.money.buy", "money", Double.valueOf(args[1])));
				} else {
					sender.sendMessage(ChatColor.RED
							+ Messages.getString("mobhunting.commands.base.not_a_number", "number", args[1]));
				}
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.buy", "command", "money buy"));
			}
			return true;

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
