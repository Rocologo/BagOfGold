package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.rewards.CustomItems;
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
		return new String[] { "gold", "bag", MobHunting.getConfigManager().dropMoneyOnGroundMoneyCommandAlias };
	}

	@Override
	public String getPermission() {
		return "mobhunting.money";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				ChatColor.GOLD + MobHunting.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " drop <amount>" + ChatColor.WHITE + " - to drop <amount> of "
						+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + ", where you look.",
				ChatColor.GOLD + MobHunting.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " drop <playername> " + ChatColor.YELLOW + "<amount>" + ChatColor.WHITE
						+ " - to drop <amount> of " + MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName
						+ " 3 block in front of the <player>.",
				ChatColor.GOLD + MobHunting.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " give <player>" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to give the player a " + MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName
						+ " in his inventory.",
				ChatColor.GOLD + MobHunting.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " take <player>" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to take <amount> gold from the "
						+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " in the players inventory",
				ChatColor.GOLD + MobHunting.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " sell" + ChatColor.WHITE + " - to sell the "
						+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " in your hand.",
				ChatColor.GOLD + MobHunting.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " sell" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to sell some of the gold in your "
						+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " and get the money.",
				ChatColor.GOLD + MobHunting.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " buy" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to buy some more gold with your money and put it into your "
						+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + "." };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.money.description", "rewardname",
				MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName);
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		if (args.length == 1) {
			// /mh money help
			// Show help
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
				return false;
		}

		if (args.length == 0 || (args.length == 1 && Bukkit.getServer().getOfflinePlayer(args[1]).isOnline())) {
			// mh money
			// mh money <player>
			// show the total amount of "bag of gold" in the players inventory.

			if (sender.hasPermission("mobhunting.money.balance")) {
				Player player = null;
				if (args.length == 0) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED
								+ Messages.getString("mobhunting.commands.base.noconsole", "command", "'money sell'"));
						return true;
					} else
						player = (Player) sender;
				} else {
					if (sender.hasPermission("mobhunting.money.balance.other"))
						player = ((Player) Bukkit.getServer().getOfflinePlayer(args[1]));
					else {
						sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission",
								"perm", "mobhunting.money.balance.other", "command", "money <playername>"));
						return true;
					}
				}

				double sum = 0;
				for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
					ItemStack is = player.getInventory().getItem(slot);
					if (HiddenRewardData.hasHiddenRewardData(is)) {
						HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
						sum = sum + hiddenRewardData.getMoney();
					}
				}
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.money.balance", "money",
						RewardManager.getEconomy().format(sum), "rewardname",
						MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName));
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.balance", "command", "money"));
			}
			return true;

		}

		else if (args.length >= 2 && args[0].equalsIgnoreCase("drop") || args[0].equalsIgnoreCase("place")) {
			// /mh money drop <amount>
			// /mh money drop <player> <amount>
			if (sender.hasPermission("mobhunting.money.drop")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					sender.sendMessage(
							ChatColor.RED + Messages.getString("mobhunting.commands.base.playername-missing"));
				} else {
					if (args[1].matches("\\d+(\\.\\d+)?")) {
						Player player = (Player) sender;
						Block b = Misc.getTargetBlock(player, 20);
						Location location = b.getLocation();
						if (location != null) {
							RewardManager.dropMoneyOnGround(player, null, location, Double.valueOf(args[1]));
							Messages.playerActionBarMessage(player,
									Messages.getString("mobhunting.moneydrop", "rewardname",
											MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
											Double.valueOf(args[1])));
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
									Messages.getString("mobhunting.moneydrop", "rewardname",
											MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
											Double.valueOf(args[2])));
						} else {
							sender.sendMessage(ChatColor.RED
									+ Messages.getString("mobhunting.commands.base.not_a_number", "number", args[2]));
						}
					} else {
						if (args.length >= 2)
							sender.sendMessage(ChatColor.RED + Messages
									.getString("mobhunting.commands.base.playername-missing", "player", args[1]));
						else
							sender.sendMessage(ChatColor.RED
									+ Messages.getString("mobhunting.commands.base.playername-missing", "player", ""));
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
							ChatColor.RED + Messages.getString("mobhunting.commands.base.playername-missing"));
				} else {
					if (Bukkit.getServer().getOfflinePlayer(args[1]).isOnline()) {
						if (args[2].matches("\\d+(\\.\\d+)?")) {
							Player player = ((Player) Bukkit.getServer().getOfflinePlayer(args[1]));
							RewardManager.dropMoneyOnGround(player, null, player.getLocation(),
									Double.valueOf(args[2]));
							Messages.playerActionBarMessage(player,
									Messages.getString("mobhunting.commands.money.give", "rewardname",
											MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
											Double.valueOf(args[2])));
							sender.sendMessage(Messages.getString("mobhunting.commands.money.give-sender", "rewardname",
									MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
									Double.valueOf(args[2]), "player", player.getName()));
						} else {
							sender.sendMessage(ChatColor.RED
									+ Messages.getString("mobhunting.commands.base.not_a_number", "number", args[2]));
						}
					} else {
						sender.sendMessage(ChatColor.RED
								+ Messages.getString("mobhunting.commands.base.playername-missing", "player", args[1]));
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.give", "command", "money give"));
			}
			return true;
		}

		else if (args.length >= 2 && args[0].equalsIgnoreCase("take")) {
			// /mh money take <player> <amount>
			if (sender.hasPermission("mobhunting.money.take")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					sender.sendMessage(
							ChatColor.RED + Messages.getString("mobhunting.commands.base.playername-missing"));
				} else {
					if (Bukkit.getServer().getOfflinePlayer(args[1]).isOnline()) {
						if (args[2].matches("\\d+(\\.\\d+)?")) {
							Player player = ((Player) Bukkit.getServer().getOfflinePlayer(args[1]));

							double taken = 0;
							double rest = Double.valueOf(args[2]);
							for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
								ItemStack is = player.getInventory().getItem(slot);
								if (HiddenRewardData.hasHiddenRewardData(is)) {
									HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
									double saldo = hiddenRewardData.getMoney();
									if (saldo >= rest) {
										hiddenRewardData.setMoney(saldo - rest);
										is = CustomItems.getCustomtexture(RewardManager.MH_REWARD_UUID,
												MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
												MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
												MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature,
												saldo - rest, UUID.randomUUID());
										ItemMeta im = is.getItemMeta();
										im.setLore(hiddenRewardData.getLore());
										im.setDisplayName(ChatColor
												.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
												+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " ("
												+ MobHunting.getRewardManager().format(saldo - rest) + " )");
										is.setItemMeta(im);
										player.getInventory().setItem(slot, is);
										taken = taken + rest;
										rest = 0;
										break;
									} else {
										is.setItemMeta(null);
										is.setType(Material.AIR);
										is.setAmount(0);
										player.getInventory().setItem(slot, is);
										taken = taken + saldo;
										rest = rest - saldo;
									}
								}
							}

							Messages.playerActionBarMessage(player,
									Messages.getString("mobhunting.commands.money.take", "rewardname",
											MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
											RewardManager.getEconomy().format(taken)));
							sender.sendMessage(Messages.getString("mobhunting.commands.money.take-sender", "rewardname",
									MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
									RewardManager.getEconomy().format(taken), "player", player.getName()));
						} else {
							sender.sendMessage(ChatColor.RED
									+ Messages.getString("mobhunting.commands.base.not_a_number", "number", args[2]));
						}
					} else {
						sender.sendMessage(ChatColor.RED
								+ Messages.getString("mobhunting.commands.base.playername-missing", "player", args[1]));
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.take", "command", "money take"));
			}
			return true;

		} else if ((args.length == 1 && args[0].equalsIgnoreCase("sell"))
				|| (args.length == 2 && args[0].equalsIgnoreCase("sell") && (args[1].matches("\\d+(\\.\\d+)?")))) {
			// /mh money sell
			// /mh money sell <amount>
			if (sender.hasPermission("mobhunting.money.sell")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED
							+ Messages.getString("mobhunting.commands.base.noconsole", "command", "'money sell'"));
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
						is.setItemMeta(null);
						player.setItemInHand(is);
						Messages.playerActionBarMessage(player,
								Messages.getString("mobhunting.commands.money.sell", "rewardname",
										MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
										RewardManager.getEconomy().format(hiddenRewardData.getMoney())));
					}
				} else if ((args[0].equalsIgnoreCase("sell") && (args[1].matches("\\d+(\\.\\d+)?")))) {
					double sold = 0;
					double toBeSold = Double.valueOf(args[1]);
					for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
						ItemStack is = player.getInventory().getItem(slot);
						if (HiddenRewardData.hasHiddenRewardData(is)) {
							HiddenRewardData hiddenRewardData = HiddenRewardData.getHiddenRewardData(is);
							double saldo = hiddenRewardData.getMoney();
							if (saldo >= toBeSold) {
								hiddenRewardData.setMoney(saldo - toBeSold);
								is = CustomItems.getCustomtexture(RewardManager.MH_REWARD_UUID,
										MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
										MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
										MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature,
										saldo - toBeSold, UUID.randomUUID());
								ItemMeta im = is.getItemMeta();
								im.setLore(hiddenRewardData.getLore());
								im.setDisplayName(
										ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
												+ MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName + " ("
												+ MobHunting.getRewardManager().format(saldo - toBeSold) + " )");
								is.setItemMeta(im);
								player.getInventory().setItem(slot, is);
								sold = sold + toBeSold;
								toBeSold = 0;
								break;
							} else {
								is.setItemMeta(null);
								is.setType(Material.AIR);
								is.setAmount(0);
								player.getInventory().setItem(slot, is);
								sold = sold + saldo;
								toBeSold = toBeSold - saldo;
							}
						}
					}
					RewardManager.getEconomy().depositPlayer(player, sold);
					Messages.playerActionBarMessage(player,
							Messages.getString("mobhunting.commands.money.sell", "rewardname",
									MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
									RewardManager.getEconomy().format(sold)));
				}
			} else {
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.nopermission", "perm",
						"mobhunting.money.sell", "command", "money sell"));
			}
			return true;
		}

		else if (args.length >= 2 && args[0].equalsIgnoreCase("buy")) {
			// /mh money buy <amount>
			if (sender.hasPermission("mobhunting.money.buy")) {
				if (args.length == 2 && args[1].matches("\\d+(\\.\\d+)?")) {
					Player player = (Player) sender;
					RewardManager.dropMoneyOnGround(player, null, player.getLocation(), Double.valueOf(args[1]));
					RewardManager.getEconomy().withdrawPlayer(player, Double.valueOf(args[1]));
					Messages.playerActionBarMessage(player,
							Messages.getString("mobhunting.commands.money.buy", "rewardname",
									MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, "money",
									Double.valueOf(args[1])));
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
		} else if (args.length == 2) {
			String partial = args[1].toLowerCase();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().toLowerCase().startsWith(partial))
					items.add(player.getName());
			}
		}
		return items;
	}
}
