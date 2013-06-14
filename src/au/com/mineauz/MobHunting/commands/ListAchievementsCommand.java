package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.achievements.Achievement;

public class ListAchievementsCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "achievements";
	}

	@Override
	public String[] getAliases()
	{
		return new String[] {"listachievements", "specialkills", "kills"};
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.listachievements";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		if(sender instanceof ConsoleCommandSender)
			return new String[] { label + ChatColor.GOLD + " <player>" };
		else
		{
			if(sender.hasPermission("mobhunting.listachievements.other"))
				return new String[] { label + ChatColor.GREEN + " [<player>]" };
			else
				return new String[] { label };
		}
	}

	@Override
	public String getDescription()
	{
		return "Lists all your special kills.";
	}

	@Override
	public boolean canBeConsole()
	{
		return true;
	}

	@Override
	public boolean canBeCommandBlock()
	{
		return false;
	}

	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length > 1)
			return false;
		
		if(sender instanceof ConsoleCommandSender && args.length != 1)
			return false;
		
		String playerName = null;
		
		if(sender instanceof Player)
			playerName = ((Player)sender).getName();
		
		if(args.length == 1)
		{
			if(!sender.hasPermission("mobhunting.listachievements.other"))
				return false;
			
			playerName = args[0];
			
			Player player = Bukkit.getPlayer(playerName);
			if(player != null)
				playerName = player.getName();
		}
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		
		if(!player.hasPlayedBefore())
		{
			sender.sendMessage(ChatColor.RED + "That player doesnt exist");
			return true;
		}
		
		List<Achievement> achievements = MobHunting.instance.getAchievements().getCompletedAchievements(player);
		int outOf = MobHunting.instance.getAchievements().getAllAchievements().size();

		
		// Build the output
		ArrayList<String> lines = new ArrayList<String>();
		
		if(sender instanceof Player && ((Player)sender).getName().equals(playerName))
			lines.add(String.format(ChatColor.GRAY + "You have completed " + ChatColor.YELLOW + "%d" + ChatColor.GRAY + " out of " + ChatColor.YELLOW + "%d" + ChatColor.GRAY + " special kills:", achievements.size(), outOf));
		else
			lines.add(String.format(ChatColor.GRAY + "%s has completed " + ChatColor.YELLOW + "%d" + ChatColor.GRAY + " out of " + ChatColor.YELLOW + "%d" + ChatColor.GRAY + " special kills:", playerName, achievements.size(), outOf));
		
		for(Achievement achievement : achievements)
			lines.add(ChatColor.YELLOW + " " + achievement.getName());
		
		sender.sendMessage(lines.toArray(new String[lines.size()]));
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		if(!sender.hasPermission("mobhunting.listachievements.other"))
			return null;
		
		if(args.length == 0)
			return null;
		
		String partial = args[0].toLowerCase();
		
		ArrayList<String> names = new ArrayList<String>();
		for(Player player : Bukkit.getOnlinePlayers())
		{
			if(player.getName().toLowerCase().startsWith(partial))
				names.add(player.getName());
		}
		
		return names;
	}

}
