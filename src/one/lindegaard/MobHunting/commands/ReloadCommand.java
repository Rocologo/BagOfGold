package one.lindegaard.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.bounty.BountyManager;

public class ReloadCommand implements ICommand {
	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mobhunting.reload";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { label };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.reload.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (MobHunting.getConfigManager().loadConfig()) {
			if (MobHunting.getMobHuntingManager().getOnlinePlayersAmount() > 0) {
				Messages.debug("Reloading %s online player settings from the database",
						MobHunting.getMobHuntingManager().getOnlinePlayersAmount());
				for (Player player : MobHunting.getMobHuntingManager().getOnlinePlayers())
					MobHunting.getPlayerSettingsmanager().load(player);
			}
			if (MobHunting.getMobHuntingManager().getOnlinePlayersAmount() > 0)
				for (Player player : MobHunting.getMobHuntingManager().getOnlinePlayers())
					MobHunting.getBountyManager().loadOpenBounties(player);
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.reload.reload-complete"));
		} else
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.reload.reload-error"));

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		return null;
	}

}
