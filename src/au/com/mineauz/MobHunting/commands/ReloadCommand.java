package au.com.mineauz.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.MobHunting.MobHunting;

public class ReloadCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "reload";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.reload";
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] {label};
	}

	@Override
	public String getDescription()
	{
		return "Reloads the configuration";
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
		if(MobHunting.config().load())
			sender.sendMessage(ChatColor.GREEN + "Configuration Reloaded");
		else
			sender.sendMessage(ChatColor.RED + "There is a problem with the config. Please check any changes you made");
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		return null;
	}

}
