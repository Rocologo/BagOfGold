package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.bounty.BountyStatus;
import one.lindegaard.MobHunting.storage.UserNotFoundException;

public class BountyCommand implements ICommand {

	public BountyCommand() {
	}

	// Used case
	// /mh bounty - to show a list of player with a bounty
	// /mh bounty <player> - to check if there is a bounty on <player>
	// /mh bounty <player> <prize> - to put a prize on the player
	// /mh bounty remove <player> - to remove a bounty from player
	// /mh bounty list <#page> - to make list of the most wanted players = /mh
	// bounty
	// /mh bounty help - to get help to see this list
	// /mh bounty locate <player> - to get chunk(?)/last known place
	// /mh bounty top <#page>- show the most active bounty hunters

	@Override
	public String getName() {
		return "bounty";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "headhunt", "prize" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.bounty";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { label + ChatColor.RED + " " + ChatColor.GREEN + "       - to show a list",
				label + ChatColor.RED + " <player>" + ChatColor.GREEN + " - to check if there is a bounty on <player>",
				label + ChatColor.RED + " <player> <prize> <message>" + ChatColor.GREEN
						+ " - put a bounty on <player> deliver the message when killed.",
				label + ChatColor.RED + " <player>" + ChatColor.WHITE + " drop" + ChatColor.GREEN
						+ " - to remove bounty on <player> with a 50% reduction" };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.bounty.description");
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
		@SuppressWarnings("deprecation")
		OfflinePlayer bountyOwner = Bukkit.getOfflinePlayer(sender.getName());
		String worldGroupName = MobHunting.getWorldGroupManager().getCurrentWorldGroup((Player) bountyOwner);
		if (args.length == 0) {

			// /mh bounty
			// Show list of Bounties on all wantedPlayers
			if (!MobHunting.getBountyManager().getBounties().isEmpty()) {
				sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties-header"));
				sender.sendMessage("-----------------------------------");
				Set<Bounty> bounties = MobHunting.getBountyManager().getBounties();
				for (Bounty bounty : bounties) {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties", "bountyowner",
							bounty.getBountyOwner().getName(), "prize", String.format("%.2f", bounty.getPrize()),
							"wantedplayer", bounty.getWantedPlayer().getName(), "daysleft",
							(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)));
					// sender.sendMessage(bounty.getBountyOwner().getName() + "
					// has put "
					// + String.format("%.2f", bounty.getPrize()) + " on " +
					// bounty.getWantedPlayer().getName()
					// + " (" + (bounty.getEndDate() -
					// System.currentTimeMillis()) / (86400000L) + " days
					// left)");
				}
			} else {
				sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-bounties"));
			}
			return true;

		} else if (args.length == 1) {

			// /mh bounty help
			// Show help
			if (args[0].equalsIgnoreCase("help"))
				return false;

			// /mh bounty <player>
			// Show list of Bounties on for player <player>
			// check if args[0] is a known playername
			@SuppressWarnings("deprecation")
			OfflinePlayer wantedPlayer = Bukkit.getOfflinePlayer(args[0]);
			if (wantedPlayer != null) {
				if (MobHunting.getBountyManager().hasBounties(worldGroupName, wantedPlayer)) {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties-header"));
					sender.sendMessage("-----------------------------------");
					Set<Bounty> bounties = MobHunting.getBountyManager().getBounties(worldGroupName, wantedPlayer);
					for (Bounty bounty : bounties) {
						sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounties", "bountyowner",
								bounty.getBountyOwner().getName(), "prize", String.format("%.2f", bounty.getPrize()),
								"wantedplayer", bounty.getWantedPlayer().getName(), "daysleft",
								(bounty.getEndDate() - System.currentTimeMillis()) / (86400000L)));
						// sender.sendMessage(bounty.getBountyOwner().getName()
						// + " has put "
						// + String.format("%.2f", bounty.getPrize())
						// + MobHunting.getEconomy().currencyNamePlural() + " on
						// " + wantedPlayer.getName() + " ("
						// + (bounty.getEndDate() - System.currentTimeMillis())
						// / (86400000L) + " days left)");
					}
				} else {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-bounties-player",
							"wantedplayer", wantedPlayer.getName()));
				}
				return true;
			} else {
				sender.sendMessage(
						Messages.getString("mobhunting.commands.bounty.unknown-player", "wantedplayer", args[0]));
				return true;
			}

		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			// /mh bounty drop <player> - to drop the bounty on the player
			// Remove a bounty on player <player>
			@SuppressWarnings("deprecation")
			OfflinePlayer wantedPlayer = Bukkit.getOfflinePlayer(args[1]);
			if (wantedPlayer != null) {
				//MobHunting.debug("BountyCommand: remove bounty on %s", wantedPlayer.getName());
				if (MobHunting.getBountyManager().hasBounty(worldGroupName, wantedPlayer, bountyOwner)) {

					Bounty bounty = MobHunting.getBountyManager().getBounty(worldGroupName, wantedPlayer, bountyOwner);
					// MobHunting.getBountyManager().removeBounty(bounty);
					bounty.setStatus(BountyStatus.canceled);
					//MobHunting.debug("BountyCommand: Remove Bounty:%s", bounty.toString());
					MobHunting.getBountyManager().cancelBounty(bounty);

					int pct = MobHunting.getConfigManager().bountyReturnPct;
					MobHunting.getEconomy().depositPlayer(bountyOwner, bounty.getPrize() * pct / 100);
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounty-removed", "wantedplayer",
							wantedPlayer.getName(), "money", String.format("%.2f", bounty.getPrize() * pct / 100)));
					// sender.sendMessage("The bounty on " +
					// wantedPlayer.getName()
					// + " was removed. You got "
					// + String.format("%.2f", bounty.getPrize() * pct / 100) +
					// "
					// back.");
					return true;
				} else {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-bounties-player",
							"wantedplayer", wantedPlayer.getName()));
					return true;
				}
			} else {
				sender.sendMessage(
						Messages.getString("mobhunting.commands.bounty.unknown-player", "wantedplayer", args[1]));
				// sender.sendMessage(args[1] + " is unknown!");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("locate")) {

			// /mh bounty locate <player> - to locate the area on the player
			// TODO: locate not implemented yet.
			return true;

		} else if (args.length == 2 && args[0].equalsIgnoreCase("top")) {

			// /mh bounty drop <player> - to show the most active headhuners
			// TODO: locate not implemented yet.
			return true;

		} else if (args[1].matches("\\d+(\\.\\d+)?")) {

			// /mh bounty <player> <prize> <message>
			// put or add a bounty on player
			@SuppressWarnings("deprecation")
			OfflinePlayer wantedPlayer = Bukkit.getOfflinePlayer(args[0]);
			int playerId;
			try {
				playerId = MobHunting.getDataStoreManager().getPlayerId(wantedPlayer);
			} catch (UserNotFoundException e) {
				sender.sendMessage(Messages.getString("mobhunting.commands.bounty.unknown-player", "wantedplayer",
						wantedPlayer.getName()));
				// sender.sendMessage(wantedPlayer.getName() + " is unknown on
				// this server.");
				return true;
			}
			if (wantedPlayer != null && playerId != 0) {
				if (bountyOwner.equals(wantedPlayer)) {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-bounty-on-yourself"));
					return true;
				}
				double prize = Double.valueOf(args[1]);
				if (!MobHunting.getEconomy().has(bountyOwner, prize)) {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.no-money", "money", prize));
					// sender.sendMessage("You dont have " + prize + " on your
					// account.");
					return true;
				}
				if (prize <= 0)
					return false;
				String message = "";
				for (int i = 2; i < args.length; i++) {
					message = message + args[i] + " ";
				}
				// MobHunting.debug("args[0]=%s,args[1]=%s,args[2-]=%s",
				// args[0], args[1], message);
				Bounty bounty;
				bounty = new Bounty(worldGroupName, bountyOwner, wantedPlayer, prize, message);
				if (MobHunting.getBountyManager().hasBounty(worldGroupName, wantedPlayer, bountyOwner)) {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounty-added", "wantedplayer",
							wantedPlayer.getName()));
					// sender.sendMessage("You have already put a prize on " +
					// wantedPlayer.getName()
					// + "'s head. Prize will be added.");
				} else {
					sender.sendMessage(Messages.getString("mobhunting.commands.bounty.bounty", "money", prize,
							"wantedplayer", wantedPlayer.getName()));
					// sender.sendMessage("You have put a prize on " +
					// wantedPlayer.getName() + "'s head.");
				}

				MobHunting.getBountyManager().addBounty(bounty);
				MobHunting.getEconomy().withdrawPlayer(bountyOwner, prize);
				sender.sendMessage(Messages.getString("mobhunting.commands.bounty.money-withdrawn", "money", prize));
				// sender.sendMessage(prize + " was withdrawed your accont");

				MobHunting.debug("%s has put %s on %s with the message %s", bountyOwner.getName(), prize,
						wantedPlayer.getName(), message);
				return true;
			} else {
				sender.sendMessage(
						Messages.getString("mobhunting.commands.bounty.unknown-player", "wantedplayer", args[0]));
				// sender.sendMessage(args[0] + " is unknown!");
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		ArrayList<String> items = new ArrayList<String>();
		if (args.length == 0) {
			items.add(" remove");
			items.add(" top");
			items.add(" gui");
		} else if (args.length == 1) {
			//MobHunting.debug("arg[0]=(%s)", args[0]);
			if (items.isEmpty()) {
				items.add("remove");
				items.add("top");
				items.add("gui");
			}
			String partial = args[0].toLowerCase();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().toLowerCase().startsWith(partial))
					items.add(player.getName());
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			//MobHunting.debug("arg[0,1]=(%s,%s)", args[0], args[1]);
			String partial = args[1].toLowerCase();
			for (OfflinePlayer wantedPlayer : MobHunting.getBountyManager().getWantedPlayers()) {
				if (wantedPlayer.getName().toLowerCase().startsWith(partial))
					items.add(wantedPlayer.getName());
			}
		}
		return items;
	}
}
