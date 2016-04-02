package au.com.mineauz.MobHunting.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.storage.DataStoreManager;
import au.com.mineauz.MobHunting.storage.PlayerData;

public class MuteCommand implements ICommand, Listener {
	private MobHunting instance;

	public MuteCommand(Plugin plugin) {
		this.instance = (MobHunting) plugin;
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}

	// Used case
	// /mh mute - No args, args.length = 0 || arg[0]=""

	@Override
	public String getName() {
		return "mute";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "silent", "notify" };
	}

	@Override
	public String getPermission() {
		return "mobhunting.mute";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				label + " mute" + ChatColor.GREEN + " - to mute/unmute.",
				label
						+ " mute playername"
						+ ChatColor.GREEN
						+ " - to mute/unmute a the notifications for a specific player." };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.mute.description");
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
			togglePlayerMuteMode((Player) sender);
			return true;
		} else if (args.length == 1) {
			DataStoreManager ds = MobHunting.instance.getDataStore();
			Player player = (Player) ds.getPlayerByName(args[0]);
			if (player != null) {
				if (sender.hasPermission("mobhunting.mute.other")
						|| sender instanceof ConsoleCommandSender) {
					togglePlayerMuteMode(player);
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You dont have permission " + ChatColor.AQUA
							+ "'mobhunting.mute.other'");
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

	private void togglePlayerMuteMode(Player player) {
		DataStoreManager ds = instance.getDataStore();
		if (instance.getPlayerData().containsKey(player.getUniqueId())) {
			boolean lm = instance.getPlayerData(player.getUniqueId())
					.isLearningMode();
			if (instance.getPlayerData(player.getUniqueId()).isMuted()) {
				ds.updatePlayerData(player, lm, false);
				instance.addPlayerData(new PlayerData(player, lm, false));
				player.sendMessage(Messages.getString(
						"mobhunting.commands.mute.unmuted", "player",
						player.getName()));
			} else {
				ds.updatePlayerData(player, lm, true);
				instance.addPlayerData(new PlayerData(player, lm, true));
				player.sendMessage(Messages.getString(
						"mobhunting.commands.mute.muted", "player",
						player.getName()));
			}
		}
	}

}
