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
import au.com.mineauz.MobHunting.leaderboard.LegacyLeaderboard;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class LeaderboardCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "leaderboard"; //$NON-NLS-1$
	}

	@Override
	public String[] getAliases()
	{
		return new String[] { "lb", "board" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.leaderboard"; //$NON-NLS-1$
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] {
			label + ChatColor.GOLD + " create <id> <type> <period> <isHorizonal?>", //$NON-NLS-1$
			label + ChatColor.GOLD + " delete <id>", //$NON-NLS-1$
			label + ChatColor.GOLD + " edit <id> (type|period|horizontal) <value>", //$NON-NLS-1$
			label + ChatColor.GOLD + " list" //$NON-NLS-1$
		};
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("mobhunting.commands.leaderboard.description"); //$NON-NLS-1$
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
			MobHunting.instance.getLeaderboards().deleteLegacyLeaderboard(id);
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.delete", "id", id)); //$NON-NLS-1$ //$NON-NLS-2$
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
		
		LegacyLeaderboard leaderboard = MobHunting.instance.getLeaderboards().getLeaderboard(id);
		if(leaderboard == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.leaderboard.edit.noboard", "id", id)); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		
		if(args[2].equalsIgnoreCase("type")) //$NON-NLS-1$
		{
			StatType type = StatType.parseStat(args[3]);
			if(type == null)
			{
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-stat", "stat", ChatColor.YELLOW + args[3] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
			
			leaderboard.setType(type);
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.edit.set-type", "id", id, "type", type.translateName())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else if(args[2].equalsIgnoreCase("period")) //$NON-NLS-1$
		{
			TimePeriod period = TimePeriod.parsePeriod(args[3]);
			if(period == null)
			{
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-period", "period", ChatColor.YELLOW + args[3] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
			
			leaderboard.setPeriod(period);
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.edit.set-period", "id", id, "period", period.translateNameFriendly())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else if(args[2].equalsIgnoreCase("horizontal")) //$NON-NLS-1$
		{
			leaderboard.setHorizontal(Boolean.parseBoolean(args[3]));
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.edit.set-horizontal", "id", id, "horizontal", leaderboard.getHorizontal())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		else
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.leaderboard.edit.unknown", "setting", args[2])); //$NON-NLS-1$ //$NON-NLS-2$
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
		
		int count = MobHunting.instance.getLeaderboards().getAllLegacyBoards().size();
		ArrayList<String> lines = new ArrayList<String>();
		lines.add(Messages.getString("mobhunting.commands.leaderboard.list.header", "count", count)); //$NON-NLS-1$ //$NON-NLS-2$
		
		for(LegacyLeaderboard board : MobHunting.instance.getLeaderboards().getAllLegacyBoards())
			lines.add(Messages.getString("mobhunting.commands.leaderboard.list.format", "id", ChatColor.YELLOW + board.getId(), "type", ChatColor.GREEN + board.getType().translateName(), "period", ChatColor.GREEN + board.getPeriod().translateNameFriendly())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		
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
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-stat", "stat", ChatColor.YELLOW + args[2] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		TimePeriod period = TimePeriod.parsePeriod(args[3]);
		if(period == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-period", "period", ChatColor.YELLOW + args[3] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		boolean horizontal = Boolean.parseBoolean(args[4]);
		
		try
		{
			MobHunting.instance.getLeaderboards().createLegacyLeaderboard(id, type, period, SelectionHelper.getPointA((Player)sender), SelectionHelper.getPointB((Player)sender), horizontal);
		}
		catch(IllegalArgumentException e)
		{
			sender.sendMessage(ChatColor.RED + e.getMessage());
			return true;
		}
		
		sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.leaderboard.create", "id", id)); //$NON-NLS-1$ //$NON-NLS-2$
		return true;
	}
	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length == 0)
			return false;
		
		if(args[0].equalsIgnoreCase("create")) //$NON-NLS-1$
			return onCreate(sender, args);
		else if(args[0].equalsIgnoreCase("delete")) //$NON-NLS-1$
			return onDelete(sender, args);
		else if(args[0].equalsIgnoreCase("edit")) //$NON-NLS-1$
			return onEdit(sender, args);
		else if(args[0].equalsIgnoreCase("list")) //$NON-NLS-1$
			return onList(sender, args);
		
		return false;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		ArrayList<String> items = new ArrayList<String>();
		
		if(args.length == 1)
		{
			items.add("create"); //$NON-NLS-1$
			items.add("delete"); //$NON-NLS-1$
			items.add("edit"); //$NON-NLS-1$
			items.add("list"); //$NON-NLS-1$
		}
		else if(args.length > 1)
		{
			if(args[0].equalsIgnoreCase("create")) //$NON-NLS-1$
			{
				if(args.length == 3)
				{
					for(StatType type : StatType.values())
						items.add(type.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else if(args.length == 4)
				{
					for(TimePeriod period : TimePeriod.values())
						items.add(period.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else if(args.length == 5)
				{
					items.add("true"); //$NON-NLS-1$
					items.add("false"); //$NON-NLS-1$
				}
			}
			else if(args[0].equalsIgnoreCase("edit")) //$NON-NLS-1$
			{
				if(args.length == 3)
				{
					items.add("type"); //$NON-NLS-1$
					items.add("period"); //$NON-NLS-1$
					items.add("horizontal"); //$NON-NLS-1$
				}
				else if(args.length == 4)
				{
					if(args[2].equalsIgnoreCase("type")) //$NON-NLS-1$
					{
						for(StatType type : StatType.values())
							items.add(type.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else if(args[2].equalsIgnoreCase("period")) //$NON-NLS-1$
					{
						for(TimePeriod period : TimePeriod.values())
							items.add(period.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else if(args[2].equalsIgnoreCase("horizontal")) //$NON-NLS-1$
					{
						items.add("true"); //$NON-NLS-1$
						items.add("false"); //$NON-NLS-1$
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
