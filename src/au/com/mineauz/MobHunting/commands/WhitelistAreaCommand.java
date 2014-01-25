package au.com.mineauz.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.Area;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class WhitelistAreaCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "whitelistarea"; //$NON-NLS-1$
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.whitelist"; //$NON-NLS-1$
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] { label + ChatColor.GREEN + " [add|remove]"};
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("mobhunting.commands.whitelistarea.description"); //$NON-NLS-1$
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
		Location loc = ((Player)sender).getLocation();
		
		if(args.length == 0)
		{
			if(MobHunting.isWhitelisted(loc))
				sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.whitelistarea.iswhitelisted")); //$NON-NLS-1$
			else
				sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.whitelistarea.notwhitelisted")); //$NON-NLS-1$
		}
		else if(args.length == 1)
		{
			if(args[0].equalsIgnoreCase("remove"))
			{
				MobHunting.instance.unWhitelistArea(loc);
				sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.whitelistarea.remove.done")); //$NON-NLS-1$
			}
			else if(args[0].equalsIgnoreCase("add"))
			{
				Area area = new Area();
				area.center = loc;
				area.range = 15;
				MobHunting.instance.whitelistArea(area);
				
				sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.whitelistarea.done")); //$NON-NLS-1$
			}
			else
				return false;
		}
		else
			return false;
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		return null;
	}

}
