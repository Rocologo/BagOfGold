package one.lindegaard.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

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
		return new String[] { ChatColor.GOLD + label + ChatColor.WHITE + " - to reload MobHunting configuration." };
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
		/**MobHunting.getDataStoreManager().shutdown();
		try {
			MobHunting.getStoreManager().initialize();
		} catch (DataStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
		
		MobHunting.getGrindingManager().saveData();
		
		long starttime = System.currentTimeMillis();
		int i = 1;
		while (MobHunting.getDataStoreManager().isRunning() && (starttime + 10000 > System.currentTimeMillis())) {
			if (((int) (System.currentTimeMillis() - starttime)) / 1000 == i) {
				Messages.debug("saving data (%s)");
				i++;
			}
		}

		if (MobHunting.getConfigManager().loadConfig()) {
			int n = MobHunting.getMobHuntingManager().getOnlinePlayersAmount();
			if (n > 0) {
				Messages.debug("Reloading %s online playerSettings from the database", n);
				// reload player settings
				for (Player player : MobHunting.getMobHuntingManager().getOnlinePlayers())
					MobHunting.getPlayerSettingsmanager().load(player);
				// reload bounties
				if (!MobHunting.getConfigManager().disablePlayerBounties)
					for (Player player : MobHunting.getMobHuntingManager().getOnlinePlayers())
						MobHunting.getBountyManager().loadOpenBounties(player);
				// reload achievements
				for (Player player : MobHunting.getMobHuntingManager().getOnlinePlayers())
					MobHunting.getAchievementManager().load(player);
			}
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
