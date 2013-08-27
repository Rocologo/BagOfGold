package au.com.mineauz.MobHunting.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class ReloadCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "reload"; //$NON-NLS-1$
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.reload"; //$NON-NLS-1$
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] {label};
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("mobhunting.commands.reload.description"); //$NON-NLS-1$
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
			sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.reload.reload-complete")); //$NON-NLS-1$
		else
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.reload.reload-error")); //$NON-NLS-1$
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		return null;
	}

}
