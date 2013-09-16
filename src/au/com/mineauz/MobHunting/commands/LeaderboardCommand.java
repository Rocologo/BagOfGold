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
import au.com.mineauz.MobHunting.leaderboard.Leaderboard;
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
		return new String[] {
			label + ChatColor.GOLD + " create <id> <type> <period> <isHorizonal?>",
			label + ChatColor.GOLD + " delete <id>",
			label + ChatColor.GOLD + " edit <id> (type|period|horizontal) <value>",
			label + ChatColor.GOLD + " list"
		};
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("mobhunting.commands.leaderboard.description");
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

	private boolean onDelete(CommandSender sender, String[] args)
	{
		if(args.length != 2)
			return false;
		
		String id = args[1];
		
		try
		{
			MobHunting.instance.getLeaderboards().deleteLeaderboard(id);
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.delete", "id", id));
		}
		catch(IllegalArgumentException e)
		{
			sender.sendMessage(ChatColor.RED + e.getMessage());
		}

		return true;
	}
	private boolean onEdit(CommandSender sender, String[] args)
	{
		if(args.length != 4)
			return false;
		
		String id = args[1];
		
		Leaderboard leaderboard = MobHunting.instance.getLeaderboards().getLeaderboard(id);
		if(leaderboard == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.leaderboard.edit.noboard", "id", id));
			return true;
		}
		
		
		if(args[2].equalsIgnoreCase("type"))
		{
			StatType type = StatType.parseStat(args[3]);
			if(type == null)
			{
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-stat", "stat", ChatColor.YELLOW + args[3] + ChatColor.RED));
				return true;
			}
			
			leaderboard.setType(type);
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.edit.set-type", "id", id, "type", type.translateName()));
		}
		else if(args[2].equalsIgnoreCase("period"))
		{
			TimePeriod period = TimePeriod.parsePeriod(args[3]);
			if(period == null)
			{
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-period", "period", ChatColor.YELLOW + args[3] + ChatColor.RED));
				return true;
			}
			
			leaderboard.setPeriod(period);
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.edit.set-period", "id", id, "period", period.translateNameFriendly()));
		}
		else if(args[2].equalsIgnoreCase("horizontal"))
		{
			leaderboard.setHorizontal(Boolean.parseBoolean(args[3]));
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.edit.set-horizontal", "id", id, "horizontal", leaderboard.getHorizontal()));
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Unknown setting " + args[2] + ". Valid values: type, period, horizontal");
			return true;
		}
		
		leaderboard.updateBoard();
		MobHunting.instance.getLeaderboards().save();
		
		return true;
	}
	private boolean onList(CommandSender sender, String[] args)
	{
		if(args.length != 1)
			return false;
		
		int count = MobHunting.instance.getLeaderboards().getAllBoards().size();
		ArrayList<String> lines = new ArrayList<String>();
		lines.add(Messages.getString("mobhunting.commands.leaderboard.list.header", "count", count));
		
		for(Leaderboard board : MobHunting.instance.getLeaderboards().getAllBoards())
			lines.add(Messages.getString("mobhunting.commands.leaderboard.list.format", "id", ChatColor.YELLOW + board.getId(), "type", ChatColor.GREEN + board.getType().translateName(), "period", ChatColor.GREEN + board.getPeriod().translateNameFriendly()));
		
		sender.sendMessage(lines.toArray(new String[lines.size()]));
		
		return true;
	}
	private boolean onCreate(CommandSender sender, String[] args)
	{
		if(args.length != 5)
			return false;
		
		String id = args[1];
		
		StatType type = StatType.parseStat(args[2]);
		if(type == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-stat", "stat", ChatColor.YELLOW + args[2] + ChatColor.RED));
			return true;
		}
		
		TimePeriod period = TimePeriod.parsePeriod(args[3]);
		if(period == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-period", "period", ChatColor.YELLOW + args[3] + ChatColor.RED));
			return true;
		}
		
		boolean horizontal = Boolean.parseBoolean(args[4]);
		
		try
		{
			MobHunting.instance.getLeaderboards().createLeaderboard(id, type, period, SelectionHelper.getPointA((Player)sender), SelectionHelper.getPointB((Player)sender), horizontal);
		}
		catch(IllegalArgumentException e)
		{
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
		
		sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.create", "id", id));
		return true;
	}
	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length == 0)
			return false;
		
		if(args[0].equalsIgnoreCase("create"))
			return onCreate(sender, args);
		else if(args[0].equalsIgnoreCase("delete"))
			return onDelete(sender, args);
		else if(args[0].equalsIgnoreCase("edit"))
			return onEdit(sender, args);
		else if(args[0].equalsIgnoreCase("list"))
			return onList(sender, args);
		
		return false;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		ArrayList<String> items = new ArrayList<String>();
		
		if(args.length == 1)
		{
			items.add("create");
			items.add("delete");
			items.add("edit");
			items.add("list");
		}
		else if(args.length > 1)
		{
			if(args[0].equalsIgnoreCase("create"))
			{
				if(args.length == 3)
				{
					for(StatType type : StatType.values())
						items.add(type.translateName().replaceAll(" ", "_"));
				}
				else if(args.length == 4)
				{
					for(TimePeriod period : TimePeriod.values())
						items.add(period.translateName().replaceAll(" ", "_"));
				}
				else if(args.length == 5)
				{
					items.add("true");
					items.add("false");
				}
			}
			else if(args[0].equalsIgnoreCase("edit"))
			{
				if(args.length == 3)
				{
					items.add("type");
					items.add("period");
					items.add("horizontal");
				}
				else if(args.length == 4)
				{
					if(args[2].equalsIgnoreCase("type"))
					{
						for(StatType type : StatType.values())
							items.add(type.translateName().replaceAll(" ", "_"));
					}
					else if(args[2].equalsIgnoreCase("period"))
					{
						for(TimePeriod period : TimePeriod.values())
							items.add(period.translateName().replaceAll(" ", "_"));
					}
					else if(args[2].equalsIgnoreCase("horizontal"))
					{
						items.add("true");
						items.add("false");
					}
				}
			}
		}
		

		if(!args[args.length-1].trim().isEmpty())
		{
			String match = args[args.length-1].trim().toLowerCase();
			
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
