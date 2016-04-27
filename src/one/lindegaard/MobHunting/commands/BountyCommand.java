package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.bounty.Bounties;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.bounty.BountyManager;
import one.lindegaard.MobHunting.bounty.BountyOwner;

public class BountyCommand implements ICommand {

	private MobHunting instance;

	public BountyCommand(MobHunting plugin) {
		this.instance = plugin;
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
		if (args.length == 0) {

			// /mh bounty
			// Show list of Bounties on all wantedPlayers
			if (!BountyManager.getBountyManager().getAllWantedPlayers().isEmpty()) {
				sender.sendMessage("|Bounty Owner   | Prize | Wanted Player |");
				sender.sendMessage("|---------------|-------|---------------|");
				for (Entry<OfflinePlayer, Bounties> bounties : BountyManager.getBountyManager().getBounties()
						.entrySet()) {
					for (Entry<OfflinePlayer, Bounty> bounty : bounties.getValue().getBounties().entrySet()) {
						sender.sendMessage("|" + String.format("%1$15s", bounty.getKey().getName()) + "|"
								+ String.format("%.2f", bounty.getValue().getPrize()) + "|"
								+ String.format("%1$20s", bounties.getKey().getName()) + "|");
					}
				}
			} else {
				sender.sendMessage("There is currently no bounties at all.");
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
				if (BountyManager.getBountyManager().hasBounties(wantedPlayer)) {
					sender.sendMessage("|Bounty Owner   | Prize | Wanted Player |");
					sender.sendMessage("|---------------|-------|---------------|");
					Bounties bounties = BountyManager.getBountyManager().getBounties().get(wantedPlayer);
					for (Bounty bounty : bounties.getBounties().values()) {
						sender.sendMessage(
								String.format("| %1$15s | %.2f | %1$20s |", bounty.getBountyOwner().getName(),
										Double.valueOf(bounty.getPrize()), wantedPlayer.getName()));
					}
				} else {
					sender.sendMessage("There is no bounty on " + wantedPlayer.getName());
				}
				return true;
			} else {
				sender.sendMessage(args[0] + " is unknown!");
				return true;
			}

		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			// /mh bounty drop <player> - to drop the bounty on the player
			// Remove a bounty on player <player>
			@SuppressWarnings("deprecation")
			OfflinePlayer wantedPlayer = Bukkit.getOfflinePlayer(args[1]);
			if (wantedPlayer != null) {
				if (!BountyManager.getBountyManager().hasBounty(wantedPlayer, bountyOwner)) {
					sender.sendMessage("You have not put a prize on " + wantedPlayer.getName());
					return true;
				}
				Bounty bounty = BountyManager.getBountyManager().getBounties().get(wantedPlayer).getBounty(bountyOwner);
				BountyManager.getBountyManager().removeBounty(wantedPlayer, bountyOwner);

				int pct = MobHunting.getConfigManager().bountyReturnPct;
				MobHunting.getEconomy().depositPlayer(bountyOwner, bounty.getPrize() * pct / 100);

				return true;
			} else {
				sender.sendMessage(args[1] + " is unknown!");
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

		} else if (Integer.valueOf(args[1]) > 0) {

			// /mh bounty <player> <prize> <message>
			// put or add a bounty on player
			@SuppressWarnings("deprecation")
			OfflinePlayer wantedPlayer = Bukkit.getOfflinePlayer(args[0]);
			if (wantedPlayer != null) {
				if (bountyOwner.equals(wantedPlayer)) {
					sender.sendMessage("You are not allowed to put a prize on yourself!");
					return true;
				}
				double prize = Double.valueOf(args[1]);
				if (!MobHunting.getEconomy().has(bountyOwner, prize)) {
					sender.sendMessage("You dont have " + prize + " on your account.");
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
				Bounty bountyOld, bountyNew;
				double oldPrize = 0, newPrize = 0;
				if (BountyManager.getBountyManager().hasBounty(wantedPlayer, bountyOwner)) {
					bountyOld = BountyManager.getBountyManager().getBounties().get(wantedPlayer).getBounty(bountyOwner);
					oldPrize = bountyOld.getPrize();
					sender.sendMessage("You have already put a prize on the players head. Prize wil be added.");
				} else
					sender.sendMessage("You have put a prize on the players head.");
				MobHunting.getEconomy().withdrawPlayer(bountyOwner, prize);
				sender.sendMessage(prize + "was withdrawed your accont");

				newPrize = oldPrize + prize;

				bountyNew = new Bounty(bountyOwner, newPrize, message);
				BountyManager.getBountyManager().putBountyOnWantedPlayer(wantedPlayer, bountyNew);
				MobHunting.debug("%s has put %s on %s with the message %s", bountyOwner.getName(), newPrize,
						wantedPlayer.getName(), message);
				return true;
			} else {
				sender.sendMessage(args[0] + " is unknown!");
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		ArrayList<String> items = new ArrayList<String>();
		if (args.length == 0) {
			String partial = args[0].toLowerCase();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().toLowerCase().startsWith(partial))
					items.add(player.getName());
			}
		} else if (args.length == 1) {
			String partial = args[0].toLowerCase();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getName().toLowerCase().startsWith(partial))
					items.add(player.getName());
			}
			if (items.isEmpty()) {
				items.add("remove");
				items.add("<prize>");
				items.add(" ");
			}
		}
		return items;
	}
}
