package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.DataCallback;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class TopCommand implements ICommand
{
	public TopCommand()
	{
	}
	
	@Override
	public String getName()
	{
		return "top"; //$NON-NLS-1$
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.top"; 
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] { label + ChatColor.GOLD + " <type> (day|week|month|year|alltime)" + ChatColor.GREEN + " [count]" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("mobhunting.commands.top.description"); //$NON-NLS-1$
	}

	@Override
	public boolean canBeConsole()
	{
		return true;
	}

	@Override
	public boolean canBeCommandBlock()
	{
		return true;
	}
	
	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length != 2 && args.length != 3)
			return false;
		
		StatType selectedType = StatType.parseStat(args[0]);
		if(selectedType == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-stat", "stat", ChatColor.YELLOW + args[0] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		// Check the time period
		TimePeriod selectedPeriod = TimePeriod.parsePeriod(args[1]);
		if(selectedPeriod == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-period", "period", ChatColor.YELLOW + args[1] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		int count = 10;
		if(args.length >= 3)
		{
			try
			{
				count = Integer.parseInt(args[2]);
				if(count <= 0 || count > 100)
				{
					sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.invalid-range")); //$NON-NLS-1$
					return true;
				}
			}
			catch(NumberFormatException e)
			{
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.invalid-number")); //$NON-NLS-1$
				return true;
			}
		}
		
		MobHunting.instance.getDataStore().requestStats(selectedType, selectedPeriod, count, new LeaderboardDisplay(sender, count, selectedPeriod));
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		ArrayList<String> items = new ArrayList<String>();
		
		if(args.length == 1)
		{
			for(StatType type : StatType.values())
				items.add(type.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else if(args.length == 2)
		{
			for(TimePeriod period : TimePeriod.values())
				items.add(period.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		else
			return null;

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

	private static class LeaderboardDisplay implements DataCallback<List<StatStore>>
	{
		private CommandSender mSender;
		private int mCount;
		private TimePeriod mPeriod;
		
		public LeaderboardDisplay(CommandSender sender, int count, TimePeriod period)
		{
			mSender = sender;
			mCount = count;
			mPeriod = period;
		}
		
		@Override
		public void onCompleted( List<StatStore> data )
		{
			ArrayList<String> lines = new ArrayList<String>();
			String name = ""; //$NON-NLS-1$
			if(!data.isEmpty())
				name = data.get(0).type.translateName();
			else
			{
				mSender.sendMessage(Messages.getString("mobhunting.commands.top.results.empty", "period", mPeriod.translateNameFriendly())); //$NON-NLS-1$ //$NON-NLS-2$
				return;
			}
			
			lines.add(ChatColor.GRAY + Messages.getString("mobhunting.commands.top.results.header", "count", ChatColor.YELLOW + "" + mCount + ChatColor.GRAY, "period", ChatColor.YELLOW + mPeriod.translateNameFriendly() + ChatColor.GRAY, "statname", ChatColor.YELLOW + name + ChatColor.GRAY)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			
			int index = 1;
			for(StatStore stat : data)
			{
				if(stat.amount == 0)
					continue;
				
				lines.add(ChatColor.GRAY + "" + index + ": " + ChatColor.GOLD + stat.player.getName() + ChatColor.GRAY + " - " + ChatColor.GOLD + stat.amount); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				++index;
			}

			mSender.sendMessage(lines.toArray(new String[lines.size()]));
		}

		@Override
		public void onError( Throwable error )
		{
			mSender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.base.error")); //$NON-NLS-1$
			error.printStackTrace();
		}
		
	}
}
