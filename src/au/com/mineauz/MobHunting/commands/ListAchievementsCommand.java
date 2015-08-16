package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.achievements.Achievement;
import au.com.mineauz.MobHunting.achievements.ProgressAchievement;
import au.com.mineauz.MobHunting.storage.DataCallback;
import au.com.mineauz.MobHunting.storage.UserNotFoundException;

public class ListAchievementsCommand implements ICommand {

	@Override
	public String getName() {
		return "achievements";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "listachievements", "specialkills", "kills" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.listachievements";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		if (sender instanceof ConsoleCommandSender)
			return new String[] { label + ChatColor.GOLD + " <player>" };
		else {
			if (sender.hasPermission("mobhunting.listachievements.other"))
				return new String[] { label + ChatColor.GREEN + " [<player>]" };
			else
				return new String[] { label };
		}
	}

	@Override
	public String getDescription() {
		return Messages
				.getString("mobhunting.commands.listachievements.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, String label,
			String[] args) {
		if (args.length > 1)
			return false;

		if (sender instanceof ConsoleCommandSender && args.length != 1)
			return false;

		OfflinePlayer player = null;

		if (sender instanceof Player)
			player = (Player) sender;

		if (args.length == 1) {
			if (!sender.hasPermission("mobhunting.listachievements.other"))
				return false;

			String name = args[0];

			player = Bukkit.getPlayer(name);
			if (player == null)
				player = MobHunting.instance.getDataStore().getPlayerByName(
						name);

		}

		if (player == null) {
			sender.sendMessage(ChatColor.RED
					+ Messages
							.getString("mobhunting.commands.listachievements.player-not-exist"));
			return true;
		}

		final String playerName = (player instanceof Player ? ((Player) player)
				.getDisplayName() : player.getName());
		final boolean self = (player == sender);

		MobHunting.instance.getAchievements().requestCompletedAchievements(
				player, new DataCallback<List<Entry<Achievement, Integer>>>() {
					@Override
					public void onError(Throwable error) {
						if (error instanceof UserNotFoundException) {
							sender.sendMessage(ChatColor.GRAY
									+ Messages
											.getString(
													"mobhunting.commands.listachievements.player-empty",
													"player", playerName));
						} else {
							sender.sendMessage(ChatColor.RED
									+ "An internal error occured while getting the achievements");
							error.printStackTrace();
						}
					}

					@Override
					public void onCompleted(
							List<Entry<Achievement, Integer>> data) {
						int outOf = 0;

						for (Achievement achievement : MobHunting.instance
								.getAchievements().getAllAchievements()) {
							if (achievement instanceof ProgressAchievement) {
								if (((ProgressAchievement) achievement)
										.inheritFrom() == null)
									++outOf;
							} else
								++outOf;
						}

						int count = 0;
						for (Map.Entry<Achievement, Integer> achievement : data) {
							if (achievement.getValue() == -1)
								++count;
						}

						// Build the output
						ArrayList<String> lines = new ArrayList<String>();

						if (self)
							lines.add(ChatColor.GRAY
									+ Messages
											.getString(
													"mobhunting.commands.listachievements.completed.self",
													"num", ChatColor.YELLOW
															+ "" + count
															+ ChatColor.GRAY,
													"max", ChatColor.YELLOW
															+ "" + outOf
															+ ChatColor.GRAY));
						else
							lines.add(ChatColor.GRAY
									+ Messages
											.getString(
													"mobhunting.commands.listachievements.completed.other",
													"player", playerName,
													"num", ChatColor.YELLOW
															+ "" + count
															+ ChatColor.GRAY,
													"max", ChatColor.YELLOW
															+ "" + outOf
															+ ChatColor.GRAY));

						boolean inProgress = false;
						for (Map.Entry<Achievement, Integer> achievement : data) {
							if (achievement.getValue() == -1) {
								lines.add(ChatColor.YELLOW + " "
										+ achievement.getKey().getName());
								lines.add(ChatColor.GRAY + "    "
										+ ChatColor.ITALIC
										+ achievement.getKey().getDescription());
							} else
								inProgress = true;
						}

						if (inProgress) {
							lines.add(""); //$NON-NLS-1$
							lines.add(ChatColor.YELLOW
									+ Messages
											.getString("mobhunting.commands.listachievements.progress"));

							for (Map.Entry<Achievement, Integer> achievement : data) {
								if (achievement.getValue() != -1
										&& achievement.getKey() instanceof ProgressAchievement)
									lines.add(ChatColor.GRAY
											+ " "
											+ achievement.getKey().getName()
											+ ChatColor.WHITE
											+ "  "
											+ achievement.getValue()
											+ " / "
											+ ((ProgressAchievement) achievement
													.getKey()).getMaxProgress());
								else
									inProgress = true;
							}
						}

						sender.sendMessage(lines.toArray(new String[lines
								.size()]));
					}
				});

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
		if (!sender.hasPermission("mobhunting.listachievements.other"))
			return null;

		if (args.length == 0)
			return null;

		String partial = args[0].toLowerCase();

		ArrayList<String> names = new ArrayList<String>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getName().toLowerCase().startsWith(partial))
				names.add(player.getName());
		}

		return names;
	}

}
