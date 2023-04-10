package one.lindegaard.BagOfGold.commands;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.bank.Bank;
import one.lindegaard.BagOfGold.bank.Vault;
import one.lindegaard.BagOfGold.bank.VaultType;
import one.lindegaard.BagOfGold.compatibility.WorldGuardCompat;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.Tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BankCommand implements ICommand {

	private BagOfGold plugin;

	public BankCommand(BagOfGold plugin) {
		this.plugin = plugin;
		if (Core.getConfigManager().bagOfGoldName == null || Core.getConfigManager().bagOfGoldName.isEmpty()) {
			Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX_WARNING
					+ "The reward_name in config.yml can't be empty. Changed to 'Bag Of Gold'");
			Core.getConfigManager().bagOfGoldName = "Bag of gold";
			Core.getConfigManager().saveConfig();
		}
	}

	// Admin commands
	// /bag bank give <player> <amount> - to give the player an amount of bag of
	// gold on his bank account.
	// Permission needed bagofgold.bank.give

	// /bag bank take <player> <amount> - to take an amount of money from the
	// players bank account.
	// have in your hand.
	// Permission needed bagofgold.bank.take

	// Player commands
	// /bag bank balance - to get your own bank balance
	// Permission needed bagofgold.bank.balance

	// /bag bank balance <player> - to get the players bank balance
	// Permission needed bagofgold.bank.balance.other

	// New Bankvault commands
	// /bag bank register - register the Bank with current region
	// /bag bank register regionid - register the Bank with regionId
	// /bag bank info - to get information about the bank
	// /bag bank info
	// /bag bank addVault - Adding the vault the player is looking at, in current
	// region.
	// /bag bank addVault regionId - Adding the vault the player is looking at, in
	// regionId.
	// /bag bank removeVault - removing the vault the player is looking at.

	@Override
	public String getName() {
		return "bank";
	}

	@Override
	public String[] getAliases() {
		return new String[] {};
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				ChatColor.GOLD + label + ChatColor.GREEN + " give <player>" + ChatColor.YELLOW + " <amount>"
						+ ChatColor.WHITE + " - to give the player a " + Core.getConfigManager().bagOfGoldName.trim()
						+ " in his inventory.",

				ChatColor.GOLD + label + ChatColor.GREEN + " take <player>" + ChatColor.YELLOW + " <amount>"
						+ ChatColor.WHITE + " - to take <amount> gold from the "
						+ Core.getConfigManager().bagOfGoldName.trim() + " in the players inventory",

				ChatColor.GOLD + label + ChatColor.GREEN + " balance [optional playername]" + ChatColor.WHITE
						+ " - to get your bankbalance of " + Core.getConfigManager().bagOfGoldName.trim() };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgold.commands.bank.description", "rewardname",
				Core.getConfigManager().bagOfGoldName.trim());
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

		// **** HELP **********************
		// /bag bank help - Show help
		if (args.length == 1 && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?"))) {
			return false;
		}

		// **** CREATE **********************
		// /bag bank register
		// /bag bank register regionid
		else if (args.length >= 1 && args[0].equalsIgnoreCase("register")) {
			if (sender.hasPermission("bagofgold.bank.owner") || sender.hasPermission("bagofgold.bank.*")) {
				if (plugin.getCompatibilityManager().isPluginLoaded(WorldGuardCompat.class)) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						String regionId = args.length >= 2 ? WorldGuardCompat.returnRegion(player, args[1])
								: WorldGuardCompat.returnRegion(player, null);
						if (regionId != null) {
							if (!plugin.getBankManager().hasBank(regionId)) {
								Bank bank = new Bank(plugin);
								bank.setRegionId(regionId);
								bank.setOwner(player);
								bank.setDisplayName(plugin.getConfigManager().bankname);
								plugin.getBankManager().addBank(bank);
								plugin.getMessages().debug("%s registered a bank in region=%s", player.getName(),
										regionId);
								plugin.getMessages().senderSendMessage(sender,
										plugin.getMessages().getString("bagofgold.commands.bank.registered", "region",
												regionId != null ? regionId : "null"));
								return true;
							} else {
								plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
										.getString("bagofgold.commands.bank.region_has_bank", "region", regionId));
								return true;
							}
						} else {
							plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
									.getString("bagofgold.commands.bank.no_region", "region", regionId));
							return true;
						}
					} else {
						Bukkit.getConsoleSender().sendMessage(
								BagOfGold.PREFIX + plugin.getMessages().getString("bagofgold.commands.base.noconsole"));
						return true;
					}
				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofGold.commands.bank.noWorldguardSupport"));
					return true;
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
								Core.PH_PERMISSION, "bagofgold.bank.owner", Core.PH_COMMAND,
								"bank register <regionid>"));
				return true;
			}
		}

		// **** INFO **********************
		else if (args.length >= 1 && args[0].equalsIgnoreCase("info")) {
			if (sender.hasPermission("bagofgold.bank.owner") || sender.hasPermission("bagofgold.bank.*")) {
				if (plugin.getCompatibilityManager().isPluginLoaded(WorldGuardCompat.class)) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						String regionId = args.length >= 2 ? WorldGuardCompat.returnRegion(player, args[1])
								: WorldGuardCompat.returnRegion(player, null);
						if (regionId != null) {
							Bank bank = plugin.getBankManager().getBank(regionId);
							if (bank != null) {
								plugin.getMessages().senderSendMessage(sender,
										ChatColor.GREEN
												+ plugin.getMessages().getString("bagofgold.commands.bank.info.name",
														"bankname", bank.getDisplayName()));
								plugin.getMessages().senderSendMessage(sender,
										ChatColor.GREEN
												+ plugin.getMessages().getString("bagofgold.commands.bank.info.region",
														"region", bank.getRegionId()));
								plugin.getMessages().senderSendMessage(sender,
										ChatColor.GREEN
												+ plugin.getMessages().getString("bagofgold.commands.bank.info.owner",
														"owner", bank.getOwner()!=null?bank.getOwner().getName():"-"));
								plugin.getMessages().senderSendMessage(sender,
										ChatColor.GREEN
												+ plugin.getMessages().getString("bagofgold.commands.bank.info.balance",
														"balance", bank.getBalance()));
								plugin.getMessages().senderSendMessage(sender,
										ChatColor.GREEN
												+ plugin.getMessages().getString("bagofgold.commands.bank.info.number_vaults",
														"number", bank.getVaults().size()));


								return true;
							} else {
								plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
										.getString("bagofgold.commands.bank.no_bank", "region", regionId));
								return true;
							}
						} else {
							plugin.getMessages().senderSendMessage(sender, ChatColor.RED
									+ plugin.getMessages().getString("bagofgold.commands.base.no_region"));
							return true;
						}
					} else {
						Bukkit.getConsoleSender().sendMessage(
								BagOfGold.PREFIX + plugin.getMessages().getString("bagofgold.commands.base.noconsole"));
						return true;
					}

				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("mobhunting.commands.region.noWorldguardSupport"));
					return true;
				}

			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
								Core.PH_PERMISSION, "bagofgold.bank.owner", Core.PH_COMMAND, "bank addVault"));
				return true;
			}
		}

		// **** ADDVAULT **********************
		// /bag bank addVault - Adding the vault the player is looking at, in current
		// region.
		// /bag bank addVault regionId - Adding the vault the player is looking at, in
		// regionId.
		else if (args.length == 1 && args[0].equalsIgnoreCase("addvault")) {
			if (sender.hasPermission("bagofgold.bank.owner") || sender.hasPermission("bagofgold.bank.*")) {
				Player player = (sender instanceof Player) ? (Player) sender : null;
				if (player != null) {
					Block block = player.getTargetBlock(null, 5);
					if (block != null && VaultType.isValidMaterial(block)) {
						if (!Bank.isVault(block)) {
							Vault vault = new Vault(block);
							if (plugin.getCompatibilityManager().isPluginLoaded(WorldGuardCompat.class)) {
								String regionId = args.length >= 2 ? WorldGuardCompat.returnRegion(player, args[1])
										: WorldGuardCompat.returnRegion(player, null);
								if (regionId != null) {
									Bank bank = plugin.getBankManager().getBank(regionId);
									if (bank != null) {
										bank.addVault(vault);
										plugin.getMessages().playerSendMessage(player,
												ChatColor.GREEN + plugin.getMessages().getString(
														"bagofgold.commands.bank.vault_added", "number",
														vault.getNumber(), "bankname", bank.getDisplayName()));
										return true;

									} else {
										plugin.getMessages().senderSendMessage(sender,
												ChatColor.RED + plugin.getMessages().getString(
														"bagofgold.commands.bank.no_bank", "region", regionId));
										return true;
									}
								} else {
									plugin.getMessages().senderSendMessage(sender, ChatColor.RED
											+ plugin.getMessages().getString("bagofgold.commands.base.no_region"));
									return true;
								}
							} else {
								plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
										.getString("mobhunting.commands.region.noWorldguardSupport"));
								return true;
							}
						} else {
							Vault v = Bank.getVault(block);
							plugin.getMessages().playerSendMessage(player,
									ChatColor.RED + plugin.getMessages().getString(
											"bagofgold.commands.bank.vault_already_registered", "number",
											v.getNumber()));
							return true;
						}
					} else {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED
								+ plugin.getMessages().getString("bagofgold.commands.bank.no_bankvault_block"));
						return true;
					}
				} else {
					Bukkit.getConsoleSender().sendMessage(
							ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.noconsole"));
					return true;
				}

			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
								Core.PH_PERMISSION, "bagofgold.bank.owner", Core.PH_COMMAND, "bank addVault"));
				return true;
			}

			// **** removeVAULT **********************
			// /bag bank removeVault - removing the vault the player is looking at, in
			// current
			// region.
			// /bag bank removeVault regionId - removing the vault the player is looking at,
			// in
			// regionId.
		} else if (args.length == 1 && args[0].equalsIgnoreCase("removevault")) {
			if (sender.hasPermission("bagofgold.bank.owner") || sender.hasPermission("bagofgold.bank.*")) {
				Player player = (sender instanceof Player) ? (Player) sender : null;
				if (player != null) {
					Block block = player.getTargetBlock(null, 5);
					if (block != null && Bank.isVault(block)) {
						Vault vault = Bank.getVault(block);

						if (plugin.getCompatibilityManager().isPluginLoaded(WorldGuardCompat.class)) {
							String regionId = args.length >= 2 ? WorldGuardCompat.returnRegion(player, args[1])
									: WorldGuardCompat.returnRegion(player, null);
							if (regionId != null) {
								Bank bank = plugin.getBankManager().getBank(regionId);
								if (bank != null) {
									bank.removeVault(block);
									plugin.getMessages().playerSendMessage(player,
											ChatColor.GREEN + plugin.getMessages().getString(
													"bagofgold.commands.bank.vault_deregistered", "number",
													vault.getNumber(), "bankname", bank.getDisplayName()));
									return true;

								} else {
									plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
											.getString("bagofgold.commands.bank.no_bank", "region", regionId));
									return true;
								}
							} else {
								plugin.getMessages().senderSendMessage(sender, ChatColor.RED
										+ plugin.getMessages().getString("bagofgold.commands.base.no_region"));
								return true;
							}
						} else {
							plugin.getMessages().senderSendMessage(sender, ChatColor.RED
									+ plugin.getMessages().getString("mobhunting.commands.region.noWorldguardSupport"));
							return true;
						}

					} else {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED
								+ plugin.getMessages().getString("bagofgold.commands.bank.no_bankvault_block"));
						return true;
					}
				} else {
					Bukkit.getConsoleSender().sendMessage(
							ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.noconsole"));
					return true;
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
								Core.PH_PERMISSION, "bagofgold.bank.owner", Core.PH_COMMAND, "bank addVault"));
				return true;
			}
		}

		// **** BALANCE **********************
		// bag bank
		// bag bank balance
		// bag bank balance <player> to show the total amount of "bag of
		// gold" in the players bank account.
		else if (args.length == 0
				|| (args.length >= 1 && (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")
						|| args[0].equalsIgnoreCase("bankbalance") || args[0].equalsIgnoreCase("bankbal")))) {
			if (sender.hasPermission("bagofgold.bank.balance") || sender.hasPermission("bagofgold.bank.*")) {
				OfflinePlayer offlinePlayer = null;
				boolean other = false;
				if (args.length <= 1) {
					if (!(sender instanceof Player)) {
						plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
								.getString("bagofgold.commands.base.noconsole", Core.PH_COMMAND, "'bank balance'"));
						return true;
					} else
						offlinePlayer = (Player) sender;

				} else {
					if (sender.hasPermission("bagofgold.bank.balance.other")
							|| sender.hasPermission("bagofgold.bank.*")) {
						offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
						other = true;
					} else {
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
										Core.PH_PERMISSION, "bagofgold.bank.balance.other", Core.PH_COMMAND,
										"bank <playername>"));
						return true;
					}
				}

				double balance = plugin.getEconomyManager().bankBalance(offlinePlayer.getUniqueId().toString());

				if (other)
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.GREEN + plugin.getMessages().getString(
									"bagofgold.commands.money.bankbalance.other", "playername", offlinePlayer.getName(),
									"money", plugin.getEconomyManager().format(balance), "rewardname",
									ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
											+ Core.getConfigManager().bagOfGoldName.trim()));
				else
					plugin.getMessages().senderSendMessage(sender,
							ChatColor.GREEN + plugin.getMessages().getString("bagofgold.commands.money.bankbalance",
									"playername", "You", "money", plugin.getEconomyManager().format(balance),
									"rewardname", ChatColor.valueOf(Core.getConfigManager().rewardTextColor)
											+ Core.getConfigManager().bagOfGoldName.trim()));
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
								Core.PH_PERMISSION, "bagofgold.bank.balance", Core.PH_COMMAND, "bank"));
			}
			return true;

		} else if (args.length == 1 && Bukkit.getServer().getOfflinePlayer(args[0]) == null
				&& (args[0].equalsIgnoreCase("balance") || args[0].equalsIgnoreCase("bal")
						|| args[0].equalsIgnoreCase("bankbalance") || args[0].equalsIgnoreCase("bankbal"))) {
			plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
					.getString("bagofgold.commands.base.unknown_playername", "playername", args[0]));
			return true;

		}

		// **** GIVE **********************
		// /bag bank give <player> <amount>
		else if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {

			if (sender.hasPermission("bagofgold.bank.give") || sender.hasPermission("bagofgold.bank.*")) {
				if (args.length == 2 && !(sender instanceof Player)) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED
							+ plugin.getMessages().getString("bagofgold.commands.base.playername-missing"));
					return true;
				}

				OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(args[1]);
				if (offlinePlayer == null || !offlinePlayer.hasPlayedBefore()) {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.playername-missing", Core.PH_PLAYERNAME, args[1]));
					return true;
				}

				if (args[2].matches("\\d+(\\.\\d+)?")) {
					double amount = Tools.round(Double.valueOf(args[2]));
					if (amount > Core.getConfigManager().limitPerBag * 100) {
						amount = Core.getConfigManager().limitPerBag * 100;
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.money.to_big_number",
										"number", args[2], "maximum", amount));
					}
					plugin.getEconomyManager().bankAccountDeposit(offlinePlayer.getUniqueId().toString(), amount);
				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.not_a_number", "number", args[2]));
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
								Core.PH_PERMISSION, "bagofgold.bank.give", Core.PH_COMMAND, "bank give"));
			}
			return true;

		}

		// **** TAKE **********************
		// /bag bank take <player> <amount>
		else if (args.length >= 2 && args[0].equalsIgnoreCase("take")) {
			if (sender.hasPermission("bagofgold.bank.take") || sender.hasPermission("bagofgold.bank.*")) {
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
					double amount = Tools.round(Double.valueOf(args[2]));
					if (amount > Core.getConfigManager().limitPerBag * 100) {
						amount = Core.getConfigManager().limitPerBag * 100;
						plugin.getMessages().senderSendMessage(sender,
								ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.money.to_big_number",
										"number", args[2], "maximum", amount));
					}
					plugin.getEconomyManager().bankAccountWithdraw(offlinePlayer.getUniqueId().toString(), amount);
				} else {
					plugin.getMessages().senderSendMessage(sender, ChatColor.RED + plugin.getMessages()
							.getString("bagofgold.commands.base.not_a_number", "number", args[2]));
				}
			} else {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission",
								Core.PH_PERMISSION, "bagofgold.bank.take", Core.PH_COMMAND, "bank take"));
			}
			return true;

		}

		return false;

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		ArrayList<String> items = new ArrayList<String>();
		if (args.length == 1) {
			items.add("balance");
			// admin commands
			items.add("give");
			items.add("take");
			items.add("register");
			items.add("info");
			items.add("addVault");
			items.add("removeVault");
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
