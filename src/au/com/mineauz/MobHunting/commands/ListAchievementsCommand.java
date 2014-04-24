package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

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
import au.com.mineauz.MobHunting.util.UUIDHelper;

public class ListAchievementsCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "achievements"; //$NON-NLS-1$
	}

	@Override
	public String[] getAliases()
	{
		return new String[] {"listachievements", "specialkills", "kills"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.listachievements"; //$NON-NLS-1$
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		if(sender instanceof ConsoleCommandSender)
			return new String[] { label + ChatColor.GOLD + " <player>" }; //$NON-NLS-1$
		else
		{
			if(sender.hasPermission("mobhunting.listachievements.other")) //$NON-NLS-1$
				return new String[] { label + ChatColor.GREEN + " [<player>]" }; //$NON-NLS-1$
			else
				return new String[] { label };
		}
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("mobhunting.commands.listachievements.description"); //$NON-NLS-1$
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
	public boolean onCommand( final CommandSender sender, String label, String[] args )
	{
		if(args.length > 1)
			return false;
		
		if(sender instanceof ConsoleCommandSender && args.length != 1)
			return false;
		
		OfflinePlayer player = null;
		
		if(sender instanceof Player)
			player = (Player)sender;
		
		if(args.length == 1)
		{
			if(!sender.hasPermission("mobhunting.listachievements.other")) //$NON-NLS-1$
				return false;
			
			String name = args[0];
			
			player = Bukkit.getPlayer(name);
			if(player == null)
			{
				UUIDHelper.initialize();
				UUID id = UUIDHelper.getKnown(name);
				if(id != null)
					player = Bukkit.getOfflinePlayer(id);
			}
		}
		
		if(player == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.listachievements.player-not-exist")); //$NON-NLS-1$
			return true;
		}
		
		final String playerName = (player instanceof Player ? ((Player)player).getDisplayName() : player.getName());
		final boolean self = (player == sender);
		
		MobHunting.instance.getAchievements().requestCompletedAchievements(player, new DataCallback<List<Entry<Achievement,Integer>>>()
		{
			@Override
			public void onError( Throwable error )
			{
				if(error instanceof UserNotFoundException)
				{
					sender.sendMessage(ChatColor.GRAY + Messages.getString("mobhunting.commands.listachievements.player-empty", "player", playerName)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "An internal error occured while getting the achievements"); //$NON-NLS-1$
					error.printStackTrace();
				}
			}
			
			@Override
			public void onCompleted( List<Entry<Achievement, Integer>> data )
			{
				int outOf = 0;
				
				for(Achievement achievement : MobHunting.instance.getAchievements().getAllAchievements())
				{
					if(achievement instanceof ProgressAchievement)
					{
						if(((ProgressAchievement)achievement).inheritFrom() == null)
							++outOf;
					}
					else
						++outOf;
				}

				int count = 0;
				for(Map.Entry<Achievement, Integer> achievement : data)
				{
					if(achievement.getValue() == -1)
						++count;
				}
				
				// Build the output
				ArrayList<String> lines = new ArrayList<String>();
				
				if(self)
					lines.add(ChatColor.GRAY + Messages.getString("mobhunting.commands.listachievements.completed.self", "num", ChatColor.YELLOW + "" + count + ChatColor.GRAY, "max", ChatColor.YELLOW + "" + outOf + ChatColor.GRAY)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				else
					lines.add(ChatColor.GRAY + Messages.getString("mobhunting.commands.listachievements.completed.other", "player", playerName, "num", ChatColor.YELLOW + "" + count + ChatColor.GRAY, "max", ChatColor.YELLOW + "" + outOf + ChatColor.GRAY)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
				
				boolean inProgress = false;
				for(Map.Entry<Achievement, Integer> achievement : data)
				{
					if(achievement.getValue() == -1)
					{
						lines.add(ChatColor.YELLOW + " " + achievement.getKey().getName()); //$NON-NLS-1$
						lines.add(ChatColor.GRAY + "    " + ChatColor.ITALIC + achievement.getKey().getDescription()); //$NON-NLS-1$
					}
					else
						inProgress = true;
				}
				
				if(inProgress)
				{
					lines.add(""); //$NON-NLS-1$
					lines.add(ChatColor.YELLOW + Messages.getString("mobhunting.commands.listachievements.progress")); //$NON-NLS-1$
					
					for(Map.Entry<Achievement, Integer> achievement : data)
					{
						if(achievement.getValue() != -1 && achievement.getKey() instanceof ProgressAchievement)
							lines.add(ChatColor.GRAY + " " + achievement.getKey().getName() + ChatColor.WHITE + "  " + achievement.getValue() + " / " + ((ProgressAchievement)achievement.getKey()).getMaxProgress()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						else
							inProgress = true;
					}
				}
				
				sender.sendMessage(lines.toArray(new String[lines.size()]));
			}
		});
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		if(!sender.hasPermission("mobhunting.listachievements.other")) //$NON-NLS-1$
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
