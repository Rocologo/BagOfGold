package one.lindegaard.MobHunting.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.bounty.BountyManager;
import one.lindegaard.MobHunting.bounty.WantedPlayer;

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
		return new String[] {
				label + ChatColor.RED + " " + ChatColor.GREEN
						+ "       - to show a list",
				label + ChatColor.RED + " <player>" + ChatColor.GREEN
						+ " - to check if there is a bounty on <player>",
				label
						+ ChatColor.RED
						+ " <player> <prize> <message>"
						+ ChatColor.GREEN
						+ " - put a bounty on <player> deliver the message when killed.",
				label
						+ ChatColor.RED
						+ " <player>"
						+ ChatColor.WHITE
						+ " drop"
						+ ChatColor.GREEN
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
			// TODO: Show list of Bountys
			return true;
		} else if (args.length == 1) {
			// /mh bounty help
			if (args[0].equalsIgnoreCase("help"))
				return false;
			// /mh bounty <player>
			// check if args[0] is a known playername
			@SuppressWarnings("deprecation")
			OfflinePlayer wantedPlayer = Bukkit.getOfflinePlayer(args[0]);
			if (wantedPlayer != null) {
				if (BountyManager.getHandler().hasBounties(wantedPlayer)) {
					WantedPlayer wp = BountyManager.getHandler()
							.getWantedPlayer(wantedPlayer);
					for (int i = 0; i < wp.getAllBounties().size(); i++) {
						Bounty bounty = wp.getAllBounties().get(i);
						sender.sendMessage(bounty.getBountyOwner() + "has put "
								+ bounty.getPrize() + " on " + wp.getName());
					}
				} else {
					sender.sendMessage("There is no bounty on "+wantedPlayer.getName());
				}
				return true;
			} else {
				sender.sendMessage(args[0] + " is unknown!");
				return true;
			}
		} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
			// /mh bounty drop <player> - to drop the bounty on the player

			@SuppressWarnings("deprecation")
			OfflinePlayer wantedPlayer = Bukkit.getOfflinePlayer(args[1]);
			if (wantedPlayer != null) {
				BountyManager.getHandler().removeBounty(wantedPlayer,
						bountyOwner);

				// TODO: Pay some money back
				int pct = instance.config().bountyReturnPct;
				// not implemented yet.

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
			@SuppressWarnings("deprecation")
			OfflinePlayer wantedPlayer = Bukkit.getOfflinePlayer(args[0]);
			if (wantedPlayer != null) {
				if (bountyOwner.equals(wantedPlayer)) {
					sender.sendMessage("You are not allowed to put a prize on yourself!");
					return true;
				}
				double prize = Double.valueOf(args[1]);
				if (prize <= 0)
					return false;
				MobHunting.debug("args[0]=%s,args[1]=%s,args[2-]=%s", args[0],
						args[1], StringUtils.concatenate(Arrays.copyOfRange(args, 2,
								args.length - 1)));
				Bounty bountyOld, bountyNew;
				double oldPrize = 0, newPrize = 0;
				if (BountyManager.getHandler().hasBounty(wantedPlayer,
						bountyOwner)) {
					bountyOld = BountyManager.getHandler().getBounty(
							wantedPlayer, bountyOwner);
					oldPrize = bountyOld.getPrize();
					sender.sendMessage("You have already put a prize on the players head. Prize wil be added.");

				} else
					sender.sendMessage("You have put a prize on the players head.");

				newPrize = oldPrize + prize;

				String message = StringUtils.concatenate(Arrays.copyOfRange(args, 2,
						args.length - 1));

				bountyNew = new Bounty(bountyOwner, newPrize, message);
				BountyManager.getHandler().addBountyOnWantedPlayer(
						wantedPlayer, bountyNew);
				MobHunting.debug("%s has put %s on %s with the message %s",
						bountyOwner.getName(), newPrize,
						wantedPlayer.getName(), message);
				return true;
			} else {
				sender.sendMessage(args[0] + " is unknown!");
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
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
