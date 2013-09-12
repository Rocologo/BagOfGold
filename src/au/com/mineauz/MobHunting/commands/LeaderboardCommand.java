package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.SelectionHelper;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class LeaderboardCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "leaderboard";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.leaderboard";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] {label + ChatColor.GOLD + " create <type> <period> <isHorizonal?>" };
	}

	@Override
	public String getDescription()
	{
		return "";
	}

	@Override
	public boolean canBeConsole()
	{
		return false;
	}

	@Override
	public boolean canBeCommandBlock()
	{
		return false;
	}

	private boolean onCreate(CommandSender sender, String[] args)
	{
		if(args.length != 4)
			return false;
		
		StatType type = StatType.parseStat(args[1]);
		if(type == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-stat", "stat", ChatColor.YELLOW + args[1] + ChatColor.RED));
			return true;
		}
		
		TimePeriod period = TimePeriod.parsePeriod(args[2]);
		if(period == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-period", "period", ChatColor.YELLOW + args[2] + ChatColor.RED));
			return true;
		}
		
		boolean horizontal = Boolean.parseBoolean(args[3]);
		
		try
		{
			MobHunting.instance.getLeaderboards().createLeaderboard(type, period, SelectionHelper.getPointA((Player)sender), SelectionHelper.getPointB((Player)sender), horizontal);
		}
		catch(IllegalArgumentException e)
		{
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Leaderboard was created");
		return true;
	}
	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length == 0)
			return false;
		
		if(args[0].equalsIgnoreCase("create"))
			return onCreate(sender, args);
		
		return false;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		ArrayList<String> items = new ArrayList<String>();
		
		if(args.length == 1)
		{
			items.add("create");
		}
		else if(args.length > 1)
		{
			if(args[0].equalsIgnoreCase("create"))
			{
				if(args.length == 2)
				{
					for(StatType type : StatType.values())
						items.add(type.translateName().replaceAll(" ", "_"));
				}
				else if(args.length == 3)
				{
					for(TimePeriod period : TimePeriod.values())
						items.add(period.translateName().replaceAll(" ", "_"));
				}
				else if(args.length == 4)
				{
					items.add("true");
					items.add("false");
				}
			}
		}
		

		if(!args[args.length-1].trim().isEmpty())
		{
			String match = args[0].trim().toLowerCase();
			
			Iterator<String> it = items.iterator();
			while(it.hasNext())
			{
				String name = it.next();
				if(!name.toLowerCase().startsWith(match))
					it.remove();
			}
		}
		return items;
	}

}
