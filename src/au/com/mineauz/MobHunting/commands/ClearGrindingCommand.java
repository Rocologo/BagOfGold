package au.com.mineauz.MobHunting.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class ClearGrindingCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "cleargrinding"; //$NON-NLS-1$
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.cleargrinding"; //$NON-NLS-1$
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] { label };
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("mobhunting.commands.cleargrinding.description"); //$NON-NLS-1$
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
		MobHunting.instance.clearGrindingArea(loc);
		
		for(Player player : Bukkit.getOnlinePlayers())
		{
			HuntData data = MobHunting.instance.getHuntData(player);
			data.clearGrindingArea(loc);
		}
		
		sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.cleargrinding.done")); //$NON-NLS-1$
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		return null;
	}

}
