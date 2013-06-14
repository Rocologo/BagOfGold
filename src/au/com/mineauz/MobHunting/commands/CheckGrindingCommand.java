package au.com.mineauz.MobHunting.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.Area;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class CheckGrindingCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "checkgrinding";
	}

	@Override
	public String[] getAliases()
	{
		return new String[] { "isgrinding", "grinding" };
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.checkgrinding";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] { label };
	}

	@Override
	public String getDescription()
	{
		return "Checks if the area you are in is a known grinding spot";
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

	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length != 0)
			return false;
		
		Location loc = ((Player)sender).getLocation();
		Area area = MobHunting.instance.getGrindingArea(loc);
		
		if(area != null)
			sender.sendMessage(ChatColor.RED + "This location is a sever-wide known grinding spot");
		else
		{
			ArrayList<Player> players = new ArrayList<Player>();
			for(Player player : Bukkit.getOnlinePlayers())
			{
				HuntData data = MobHunting.instance.getHuntData(player);
				area = data.getGrindingArea(loc);
				if(area != null)
					players.add(player);
			}
			
			if(players.isEmpty())
				sender.sendMessage(ChatColor.GREEN + "This location is not a grinding spot");
			else
			{
				String playerList = "";
				
				for(Player player : players)
				{
					if(!playerList.isEmpty())
						playerList += ", ";
					
					playerList += player.getName();
				}
				
				sender.sendMessage(ChatColor.RED + "This location is a known grinding spot for the following players: " + playerList);
			}
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label,
			String[] args )
	{
		// TODO Auto-generated method stub
		return null;
	}

}
