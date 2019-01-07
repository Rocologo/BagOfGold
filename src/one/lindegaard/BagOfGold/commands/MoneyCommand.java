package one.lindegaard.BagOfGold.commands;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.rewards.Reward;
import one.lindegaard.BagOfGold.util.Misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.milkbowl.vault.economy.EconomyResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MoneyCommand implements ICommand {

	private BagOfGold plugin;

	public MoneyCommand(BagOfGold plugin) {
		this.plugin = plugin;
	}

	// Admin command
	// /bag money drop <amount> - to drop <amount money> where player look.
	// Permission needed mobhunt.money.drop

	// /bag money drop <playername> <amount> - to drop <amount money> where
	// player look.
	// Permission needed bagofgold.money.drop

	// /bag money give <player> <amount> - to give the player an amount of bag
	// of
	// gold.
	// Permission needed bagofgold.money.sell

	// /bag money take <player> <amount> - to take an amount of money from the
	// player.
	// have in your hand.
	// Permission needed bagofgold.money.sell

	@Override
	public String getName() {
		return "money";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "gold", "bag", plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias };
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias
				+ ChatColor.GREEN + " drop <amount>" + ChatColor.WHITE + " - to drop <amount> of "
				+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim() + ", where you look.",

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " drop <playername> " + ChatColor.YELLOW + "<amount>" + ChatColor.WHITE
						+ " - to drop <amount> of " + plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()
						+ " 3 block in front of the <player>.",

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " give <player>" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to give the player a " + plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()
						+ " in his inventory.",

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " take <player>" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to take <amount> gold from the "
						+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()
						+ " in the players inventory",

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " balance [optional playername]" + ChatColor.WHITE + " - to get your balance of "
						+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " bankbalance [optional playername]" + ChatColor.WHITE + " - to get your bankbalance of "
						+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN
						+ " pay <player>" + ChatColor.YELLOW + " <amount>" + ChatColor.WHITE
						+ " - to give the player a " + plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()
						+ " ein his inventory.",

				ChatColor.GOLD + plugin.getConfigManager().dropMoneyOnGroundMoneyCommandAlias + ChatColor.GREEN + " top"
						+ ChatColor.YELLOW + " <amount>" + ChatColor.WHITE + " - to show top 25 players." };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgold.commands.money.description", "rewardname",
				plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim());
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
			// /bag money help
			// Show help
			if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))
				return false;

			// Top 54 players 
			else if (args[0].equalsIgnoreCase("top") || args[0].equalsIgnoreCase("wealth")) {
				if (!(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.noconsole", "command", "'money deposit'"));
					return true;
				} else if (sender.hasPermission("bagofgold.money.top") || sender.hasPermission("bagofgold.money.*")) {
					Player player = (Player) sender;
					List<PlayerBalance> playerBalances = new ArrayList<PlayerBalance>();
					String worldGroup = plugin.getWorldGroupManager().getCurrentWorldGroup(player);
					int gamemode = plugin.getWorldGroupManager().getCurrentGameMode(player).getValue();
					playerBalances = plugin.getStoreManager().loadTop54(54, worldGroup, gamemode);
					plugin.getPlayerBalanceManager().showTopPlayers(sender, playerBalances);
				} else {
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
									"perm", "bagofgold.money.top", "command", "money top"));
				}
				return true;
			}
		}

		if (args.length == 0
				|| (args.length >= 1 && (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")))) {
			// mh money
			// mh money balance
			// mh money balance <player>
			// show the total amount of "bag of gold" in the players inventory.

			if (sender.hasPermission("bagofgold.money.balance") || sender.hasPermission("bagofgold.money.*")) {
				OfflinePlayer offlinePlayer = null;
				boolean other = false;
				if (args.length <= 1) {
					if (!(sender instanceof Player)) {
						plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
								.getString("bagofgold.commands.base.noconsole", "command", "'money balance'"));
						return true;
					} else
						offlinePlayer = (Player) sender;

				} else {
					if (sender.hasPermission("bagofgold.money.balance.other")
							|| sender.hasPermission("bagofgold.money.*")) {
						offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
						other = true;
					} else {
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
										"perm", "bagofgold.money.balance.other", "command", "money <playername>"));
						return true;
					}
				}

				double balance = plugin.getEconomyManager().getBalance(offlinePlayer);

				if (other)
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.money.balance.other",
									"playername", offlinePlayer.getName(), "money",
									plugin.getEconomyManager().format(balance), "rewardname",
									ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
											+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()));
				else
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.money.balance",
									"playername", "You", "money", plugin.getEconomyManager().format(balance),
									"rewardname",
									ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
											+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()));
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.money.balance", "command", "money"));
			}
			return true;

		} else if (args.length == 0 || (args.length >= 1
				&& (args[0].equalsIgnoreCase("bankbalance") || args[0].equalsIgnoreCase("bankbal")))) {
			// mh money
			// mh money bankbalance
			// mh money bankbalance <player>
			// show the total amount of "bag of gold" in the players inventory.

			if (sender.hasPermission("bagofgold.money.bankbalance") || sender.hasPermission("bagofgold.money.*")) {
				OfflinePlayer offlinePlayer = null;
				boolean other = false;
				if (args.length <= 1) {
					if (!(sender instanceof Player)) {
						plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
								.getString("bagofgold.commands.base.noconsole", "command", "'money bankbalance'"));
						return true;
					} else
						offlinePlayer = (Player) sender;

				} else {
					if (sender.hasPermission("bagofgold.money.bankbalance.other")
							|| sender.hasPermission("bagofgold.money.*")) {
						offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
						other = true;
					} else {
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
										"perm", "bagofgold.money.bankbalance.other", "command",
										"money bankbalance <playername>"));
						return true;
					}
				}

				double bankBalance = plugin.getEconomyManager()
						.bankBalance(offlinePlayer.getUniqueId().toString()).balance;

				if (other)
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.GREEN + plugin.getMessages().getString(
									"bagofgold.commands.money.bankbalance.other", "playername", offlinePlayer.getName(),
									"money", plugin.getEconomyManager().format(bankBalance), "rewardname",
									ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
											+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()));
				else
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.money.bankbalance",
									"playername", "You", "money", plugin.getEconomyManager().format(bankBalance),
									"rewardname",
									ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
											+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim()));
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.money.balance", "command", "money"));
			}
			return true;

		} else if (args.length == 1 && Bukkit.getServer().getOfflinePlayer(args[0]) == null) {
			plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
					.getString("bagofgold.commands.base.unknown_playername", "playername", args[0]));
			return true;

		} else if (args.length >= 2 && args[0].equalsIgnoreCase("drop") || args[0].equalsIgnoreCase("place"))

		{
			// /bag money drop <amount>
			// /bag money drop <player> <amount>
			if (sender.hasPermission("bagofgold.money.drop") || sender.hasPermission("bagofgold.money.*")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofgold.commands.base.playername-missing"));
				} else {
					if (args[1].matches("\\d+(\\.\\d+)?")) {
						Player player = (Player) sender;
						Location location = Misc.getTargetBlock(player, 20).getLocation();
						double money = Misc.floor(Double.valueOf(args[1]));
						if (money > plugin.getConfigManager().limitPerBag * 100) {
							money = plugin.getConfigManager().limitPerBag * 100;
							plugin.getMessages().senderSendMessage(sender,
									ChatColor.RED
											+ plugin.getMessages().getString("bagofgold.commands.money.to_big_number",
													"number", args[1], "maximum", money));
						}
						plugin.getMessages().debug("The BagOfGold was dropped at %s", location);
						plugin.getEconomyManager().dropMoneyOnGround_EconomyManager(player, null, location, money);
						plugin.getMessages().playerActionBarMessageQueue(player,
								plugin.getMessages().getString("bagofgold.moneydrop", "rewardname",
										ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
												+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName,
										"money", plugin.getEconomyManager().format(money)));
					} else if (Bukkit.getServer().getOfflinePlayer(args[1]).isOnline()) {
						if (args[2].matches("\\d+(\\.\\d+)?")) {
							Player player = ((Player) Bukkit.getServer().getOfflinePlayer(args[1]));
							Location location = Misc.getTargetBlock(player, 3).getLocation();
							double money = Misc.floor(Double.valueOf(args[2]));
							if (money > plugin.getConfigManager().limitPerBag * 100) {
								money = plugin.getConfigManager().limitPerBag * 100;
								plugin.getMessages().senderSendMessage(sender,
										ChatColor.RED + plugin.getMessages().getString(
												"bagofgold.commands.money.to_big_number", "number", args[2], "maximum",
												money));
							}
							plugin.getMessages().debug("The BagOfGold was dropped at %s", location);
							plugin.getEconomyManager().dropMoneyOnGround_EconomyManager(player, null, location, money);
							plugin.getMessages().playerActionBarMessageQueue(player,
									plugin.getMessages().getString("bagofgold.moneydrop", "rewardname",
											ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
													+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName.trim(),
											"money", plugin.getEconomyManager().format(money)));
						} else {
							plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
									.getString("bagofgold.commands.base.not_a_number", "number", args[2]));
						}
					} else {
						plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
								.getString("bagofgold.commands.base.playername-missing", "player", args[1]));
					}
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.money.drop", "command", "money drop"));
			}
			return true;

		} else if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
			// /bag money give <player> <amount>
			if (sender.hasPermission("bagofgold.money.give") || sender.hasPermission("bagofgold.money.*")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofgold.commands.base.playername-missing"));
					return true;
				}

				OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
				if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.playername-missing", "player", args[1]));
					return true;
				}

				if (args[2].matches("\\d+(\\.\\d+)?")) {
					double amount = Misc.round(Double.valueOf(args[2]));
					if (amount > plugin.getConfigManager().limitPerBag * 100) {
						amount = plugin.getConfigManager().limitPerBag * 100;
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.money.to_big_number",
										"number", args[2], "maximum", amount));
					}
					plugin.getEconomyManager().depositPlayer(offlinePlayer, amount);
				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.not_a_number", "number", args[2]));
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.money.give", "command", "money give"));
			}
			return true;
		}

		else if (args.length >= 2 && args[0].equalsIgnoreCase("pay")) {
			// /bag money pay <player> <amount>
			if (sender.hasPermission("bagofgold.money.pay") || sender.hasPermission("bagofgold.money.*")) {

				if (!(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.noconsole", "command", "'money pay'"));
					return true;
				}

				OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
				if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.playername-missing", "player", args[1]));
					return true;
				}

				Player fromPlayer = (Player) sender;
				if (args[2].matches("\\d+(\\.\\d+)?")) {
					double amount = Misc.round(Double.valueOf(args[2]));
					if (amount > plugin.getConfigManager().limitPerBag * 100) {
						amount = plugin.getConfigManager().limitPerBag * 100;
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.money.to_big_number",
										"number", args[2], "maximum", amount));
					}
					if (!plugin.getEconomyManager().has(fromPlayer, amount)) {
						plugin.getMessages().senderSendMessage(fromPlayer, plugin.getMessages()
								.getString("bagofgold.commands.money.not-enough-money", "money", args[2]));
						return true;
					}
					plugin.getEconomyManager().depositPlayer(offlinePlayer, amount);
					plugin.getEconomyManager().withdrawPlayer(fromPlayer, amount);
				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.not_a_number", "number", args[2]));
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.money.pay", "command", "money pay"));
			}
			return true;
		}

		else if (args.length >= 2 && args[0].equalsIgnoreCase("take"))

		{
			// /bag money take <player> <amount>
			if (sender.hasPermission("bagofgold.money.take") || sender.hasPermission("bagofgold.money.*")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofgold.commands.base.playername-missing"));
					return true;
				}
				OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
				if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofgold.commands.base.playername-missing"));
					return true;
				}
				if (args[2].matches("\\d+(\\.\\d+)?")) {
					double amount = Misc.round(Double.valueOf(args[2]));
					if (amount > plugin.getConfigManager().limitPerBag * 100) {
						amount = plugin.getConfigManager().limitPerBag * 100;
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.money.to_big_number",
										"number", args[2], "maximum", amount));
					}
					plugin.getEconomyManager().withdrawPlayer(offlinePlayer, amount);
				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.not_a_number", "number", args[2]));
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.money.take", "command", "money take"));
			}
			return true;

		} else if (args.length == 1 && args[0].equalsIgnoreCase("deposit")
				|| (args.length == 2 && args[0].equalsIgnoreCase("deposit")
						&& (args[1].matches("\\d+(\\.\\d+)?") || args[1].equalsIgnoreCase("all")))) {
			// /bag money deposit - deposit the bagofgold in the players hand
			// to
			// the bank
			// /bag money deposit <amount>
			if (sender.hasPermission("bagofgold.money.deposit") || sender.hasPermission("bagofgold.money.*")) {
				if (!(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.noconsole", "command", "'money deposit'"));
					return true;
				}
				Player player = (Player) sender;
				PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
				for (Iterator<NPC> npcList = CitizensAPI.getNPCRegistry().iterator(); npcList.hasNext();) {
					NPC npc = npcList.next();
					if (plugin.getBankManager().isBagOfGoldBanker(npc.getEntity())) {
						if (npc.getEntity().getLocation().distance(player.getLocation()) < 3) {
							if (args.length == 1) {
								ItemStack is = player.getItemInHand();
								if (Reward.isReward(is)) {
									Reward reward = Reward.getReward(is);
									if (reward.isBagOfGoldReward()) {
										plugin.getMessages().playerSendMessage(player,
												plugin.getMessages().getString(
														"bagofgold.money.you_cant_sell_and_buy_bagofgold", "itemname",
														reward.getDisplayname()));
										return true;
									}
									EconomyResponse res = plugin.getEconomyManager()
											.bankDeposit(player.getUniqueId().toString(), reward.getMoney());
									if (res.transactionSuccess()) {
										plugin.getEconomyManager().withdrawPlayer(player, res.amount);
									}
									plugin.getBankManager().sendBankerMessage(player);
								}
							} else {
								double to_be_removed = args[1].equalsIgnoreCase("all")
										? ps.getBalance() + ps.getBalanceChanges() : Double.valueOf(args[1]);
								EconomyResponse res = plugin.getEconomyManager().withdrawPlayer(player, to_be_removed);
								if (res.transactionSuccess()) {
									plugin.getEconomyManager().bankDeposit(player.getUniqueId().toString(), res.amount);
								}
								plugin.getBankManager().sendBankerMessage(player);
							}
							break;
						} else {
							plugin.getMessages().senderSendMessage(sender, ChatColor.RED
									+ plugin.getMessages().getString("bagofgold.commands.money.bankerdistance"));
						}

					}
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.money.deposit", "command", "money deposit"));
			}
			return true;
		}

		else if (args.length == 2 && args[0].equalsIgnoreCase("withdraw")) {
			// /bag money withdraw <amount>
			if (sender.hasPermission("bagofgold.money.withdraw") || sender.hasPermission("bagofgold.money.*")) {
				if (args.length == 2 && (args[1].matches("\\d+(\\.\\d+)?") || args[1].equalsIgnoreCase("all"))) {
					Player player = (Player) sender;
					PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
					double amount = args[1].equalsIgnoreCase("all") ? ps.getBankBalance() + ps.getBankBalanceChanges()
							: Double.valueOf(args[1]);
					double space=plugin.getEconomyManager().getSpaceForMoney(player);
					if (amount>space)
						amount=space;
					for (Iterator<NPC> npcList = CitizensAPI.getNPCRegistry().iterator(); npcList.hasNext();) {
						NPC npc = npcList.next();
						if (plugin.getBankManager().isBagOfGoldBanker(npc.getEntity())) {
							if (npc.getEntity().getLocation().distance(player.getLocation()) < 3) {
								if (ps.getBankBalance() + ps.getBankBalanceChanges() >= amount) {

									EconomyResponse res = plugin.getEconomyManager()
											.bankWithdraw(player.getUniqueId().toString(), amount);
									if (res.transactionSuccess()) {
										plugin.getEconomyManager().depositPlayer(player, amount);
									}
									plugin.getBankManager().sendBankerMessage(player);

								} else {
									plugin.getMessages().playerActionBarMessageQueue(player,
											ChatColor.RED + plugin.getMessages().getString(
													"bagofgold.commands.money.not-enough-money-in-bank", "money",
													amount, "rewardname",
													ChatColor.valueOf(
															plugin.getConfigManager().dropMoneyOnGroundTextColor)
															+ plugin.getConfigManager().dropMoneyOnGroundSkullRewardName));
								}
								break;
							} else {
								plugin.getMessages().senderSendMessage(sender, ChatColor.RED
										+ plugin.getMessages().getString("bagofgold.commands.money.bankerdistance"));
							}
						} else {
						}
					}

				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.not_a_number", "number", args[1]));
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
								"bagofgold.money.withdraw", "command", "money withdraw"));
			}
			return true;
		} else {
			plugin.getMessages().debug("no command hit...");
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
			items.add("balance");
			items.add("bankbalance");
			items.add("pay");
			items.add("Top");
		} else if (args.length == 2)
			for (Player player : Bukkit.getOnlinePlayers())
				items.add(player.getName());

		if (!args[args.length - 1].trim().isEmpty()) {
			String match = args[args.length - 1].trim().toLowerCase();

			items.removeIf(name -> !name.toLowerCase().startsWith(match));
		}
		return items;
	}
}
