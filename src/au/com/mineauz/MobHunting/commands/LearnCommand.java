package au.com.mineauz.MobHunting.commands;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.storage.DataStoreManager;
import au.com.mineauz.MobHunting.storage.PlayerData;

public class LearnCommand implements ICommand, Listener {

	public LearnCommand() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}

	// Used case
	// /mh learn - args.length = 0 || arg[0]=""

	@Override
	public String getName() {
		return "learn";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "learning", "-l", "learnmode" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.learn";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				label + " learn" + ChatColor.GREEN
						+ " - to enable/disable learningmode.",
				label
						+ " learn playername"
						+ ChatColor.GREEN
						+ " - to enable/disable learningmode for a specific player." };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.learn.description");
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		if (args.length == 0) {
			togglePlayerLearningMode((Player) sender);
			return true;
		} else if (args.length == 1) {
			DataStoreManager ds = MobHunting.instance.getDataStore();
			Player player = (Player) ds.getPlayerByName(args[0]);
			if (player != null) {
				if (sender.hasPermission("mobhunting.learn.other")
						|| sender instanceof ConsoleCommandSender) {
					togglePlayerLearningMode(player);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You dont have permission " + ChatColor.AQUA
							+ "'mobhunting.learn.other'");
				}
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Player " + args[0]
						+ " is not online.");
				return false;
			}
		}
		return false;
	}

	private void togglePlayerLearningMode(Player player) {
		DataStoreManager ds = MobHunting.instance.getDataStore();
		if (MobHunting.instance.playerData.containsKey(player.getUniqueId())) {
			boolean mm = MobHunting.instance.playerData.get(
					player.getUniqueId()).isMuted();
			if (MobHunting.instance.playerData.get(player.getUniqueId())
					.isLearningMode()) {
				ds.savePlayerData(player, false, mm);
				MobHunting.instance.playerData.put(player.getUniqueId(),
						new PlayerData(player, false, mm));
				player.sendMessage(Messages.getString(
						"mobhunting.commands.learn.disabled", "player",
						player.getName()));
			} else {
				ds.savePlayerData(player, true, mm);
				MobHunting.instance.playerData.put(player.getUniqueId(),
						new PlayerData(player, true, mm));
				player.sendMessage(Messages.getString(
						"mobhunting.commands.learn.enabled", "player",
						player.getName()));
			}
		}
	}

}
