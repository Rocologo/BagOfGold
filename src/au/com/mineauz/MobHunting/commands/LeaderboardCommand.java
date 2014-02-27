package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Sign;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.leaderboard.Leaderboard;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class LeaderboardCommand implements ICommand, Listener
{
	private WeakHashMap<Player, BoardState> mWaitingStates = new WeakHashMap<Player, BoardState>();
	
	public LeaderboardCommand()
	{
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}
	
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
			label + ChatColor.GOLD + " create <type> <period> [isHorizonal?] [<width> <height>]", //$NON-NLS-1$
			label + ChatColor.GOLD + " delete <id>", //$NON-NLS-1$
			label + ChatColor.GOLD + " edit (type|period|horizontal) <value>" //$NON-NLS-1$
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
		if(args.length != 3)
			return false;
		
		BoardState state = mWaitingStates.get(sender);
		if(state == null)
			state = new BoardState();
		
		if(args[1].equalsIgnoreCase("type")) //$NON-NLS-1$
		{
			StatType type = StatType.parseStat(args[2]);
			if(type == null)
			{
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-stat", "stat", ChatColor.YELLOW + args[2] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
			
			state.type = type;
		}
		else if(args[1].equalsIgnoreCase("period")) //$NON-NLS-1$
		{
			TimePeriod period = TimePeriod.parsePeriod(args[2]);
			if(period == null)
			{
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-period", "period", ChatColor.YELLOW + args[2] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
			
			state.period = period;
		}
		else if(args[1].equalsIgnoreCase("horizontal")) //$NON-NLS-1$
		{
			state.horizontal = Boolean.parseBoolean(args[2]);
		}
		else
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.leaderboard.edit.unknown", "setting", args[1])); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		mWaitingStates.put((Player)sender, state);
		if(state.create)
			sender.sendMessage(ChatColor.GOLD + "Changes saved, right click a wall sign to create the board.");
		else
			sender.sendMessage(ChatColor.GOLD + "Changes saved, right click a leaderboard to apply changes.");
		
		return true;
	}

	private boolean onCreate(CommandSender sender, String[] args)
	{
		if(args.length < 3 || args.length > 6)
			return false;
		
		StatType type = StatType.parseStat(args[1]);
		if(type == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-stat", "stat", ChatColor.YELLOW + args[1] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		TimePeriod period = TimePeriod.parsePeriod(args[2]);
		if(period == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.top.unknown-period", "period", ChatColor.YELLOW + args[2] + ChatColor.RED)); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		boolean horizontal = true;
		int width = 3;
		int height = 3;
		
		if(args.length == 4)
			horizontal = Boolean.parseBoolean(args[3]);
		else if(args.length > 4)
		{
			try
			{
				width = Integer.parseInt(args[args.length-2]);
				if(width < 1)
				{
					sender.sendMessage(ChatColor.RED + "Width is too small. Must be at least 1");
					return true;
				}
			}
			catch(NumberFormatException e)
			{
				sender.sendMessage(ChatColor.RED + "Width must be a whole number of at least 1");
				return true;
			}
			
			try
			{
				height = Integer.parseInt(args[args.length-1]);
				if(height < 1)
				{
					sender.sendMessage(ChatColor.RED + "Height is too small. Must be at least 1");
					return true;
				}
			}
			catch(NumberFormatException e)
			{
				sender.sendMessage(ChatColor.RED + "Height must be a whole number of at least 1");
				return true;
			}
			
			if(args.length == 6)
				horizontal = Boolean.parseBoolean(args[3]);
		}
		
		BoardState state = new BoardState();
		state.create = true;
		state.height = height;
		state.width = width;
		state.horizontal = horizontal;
		state.period = period;
		state.type = type;
		
		mWaitingStates.put((Player)sender, state);
		
		sender.sendMessage(ChatColor.GOLD + "Click a wall sign to create the leaderboard");

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
		}
		else if(args.length > 1)
		{
			if(args[0].equalsIgnoreCase("create")) //$NON-NLS-1$
			{
				if(args.length == 2)
				{
					for(StatType type : StatType.values())
						items.add(type.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else if(args.length == 3)
				{
					for(TimePeriod period : TimePeriod.values())
						items.add(period.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if(args[0].equalsIgnoreCase("edit")) //$NON-NLS-1$
			{
				if(args.length == 2)
				{
					items.add("type"); //$NON-NLS-1$
					items.add("period"); //$NON-NLS-1$
					items.add("horizontal"); //$NON-NLS-1$
				}
				else if(args.length == 3)
				{
					if(args[1].equalsIgnoreCase("type")) //$NON-NLS-1$
					{
						for(StatType type : StatType.values())
							items.add(type.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else if(args[1].equalsIgnoreCase("period")) //$NON-NLS-1$
					{
						for(TimePeriod period : TimePeriod.values())
							items.add(period.translateName().replaceAll(" ", "_")); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else if(args[1].equalsIgnoreCase("horizontal")) //$NON-NLS-1$
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
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=false)
	private void onClickSign(PlayerInteractEvent event)
	{
		if(!event.hasBlock())
			return;
		
		BoardState state = mWaitingStates.remove(event.getPlayer());
		if(state == null)
			return;
		
		if(event.getClickedBlock().getType() != Material.WALL_SIGN)
		{
			if(state.create)
				event.getPlayer().sendMessage(ChatColor.RED + "Leaderboard creation cancelled.");
			else
				event.getPlayer().sendMessage(ChatColor.RED + "Leaderboard edit cancelled.");
			return;
		}
		
		if(state.create)
		{
			BlockFace face = ((Sign)event.getClickedBlock().getState().getData()).getFacing();
			
			try
			{
				MobHunting.instance.getLeaderboards().createLeaderboard(event.getClickedBlock().getLocation(), face, state.type, state.period, state.horizontal, state.width, state.height);
				event.getPlayer().sendMessage(ChatColor.GREEN + "Leaderboard created");
			}
			catch(IllegalArgumentException e)
			{
				event.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
			}
			
		}
		else
		{
			Leaderboard board = MobHunting.instance.getLeaderboards().getLeaderboardAt(event.getClickedBlock().getLocation());
			if(board != null)
			{
				if(state.type != null)
					board.setType(state.type);
				
				if(state.period != null)
					board.setPeriod(state.period);
				
				if(state.horizontal != null)
					board.setHorizontal(state.horizontal);
				
				board.update();
				event.getPlayer().sendMessage(ChatColor.GREEN + "Leaderboard edited");

				MobHunting.instance.getLeaderboards().saveWorld(board.getWorld());
			}
			else
				event.getPlayer().sendMessage(ChatColor.RED + "That is not a leaderboard. Edit cancelled.");
		}
		
	}
	
	private static class BoardState
	{
		public Integer width;
		public Integer height;
		public Boolean horizontal;
		public StatType type;
		public TimePeriod period;
		public boolean create;
	}
}
