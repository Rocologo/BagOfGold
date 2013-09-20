package au.com.mineauz.MobHunting.commands;

import java.util.HashSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.SelectionHelper;

public class SelectCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "select"; //$NON-NLS-1$
	}

	@Override
	public String[] getAliases()
	{
		return new String[] {"sel"}; //$NON-NLS-1$
	}

	@Override
	public String getPermission()
	{
		return "mobhunting.select"; //$NON-NLS-1$
	}

	@Override
	public String[] getUsageString( String label, CommandSender sender )
	{
		return new String[] {label + ChatColor.GOLD + " (1|2)" }; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("mobhunting.commands.select.description"); //$NON-NLS-1$
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

	@SuppressWarnings( "deprecation" )
	@Override
	public boolean onCommand( CommandSender sender, String label, String[] args )
	{
		if(args.length != 1)
			return false;
		
		Player player = (Player)sender;
		
		boolean pointA = false;
		if(args[0].equalsIgnoreCase("1")) //$NON-NLS-1$
			pointA = true;
		else if(!args[0].equalsIgnoreCase("2")) //$NON-NLS-1$
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.select.unknown-point", "point", args[0])); //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		
		HashSet<Byte> transparent = new HashSet<Byte>();
		transparent.add((byte)0);
		
		Block target = player.getTargetBlock(transparent, 10);
		if(target == null)
		{
			sender.sendMessage(ChatColor.RED + Messages.getString("mobhunting.commands.select.too-far")); //$NON-NLS-1$
			return true;
		}
		
		if(pointA)
			SelectionHelper.setPointA(player, target.getLocation());
		else
			SelectionHelper.setPointB(player, target.getLocation());
		
		sender.sendMessage(ChatColor.GREEN + Messages.getString("mobhunting.commands.select.done", "point", args[0], "coords", String.format("%d, %d, %d", target.getX(), target.getY(), target.getZ()))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String label, String[] args )
	{
		return null;
	}

}
