package au.com.mineauz.MobHunting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.compatability.WorldEditCompat;

public class SelectionHelper
{
	public static Location getPointA(Player player) throws IllegalArgumentException
	{
		if(needsCommands())
			throw new IllegalArgumentException("Point 1 is not set");
		else
			return WorldEditCompat.getPointA(player);
	}
	
	public static Location getPointB(Player player) throws IllegalArgumentException
	{
		if(needsCommands())
			throw new IllegalArgumentException("Point 2 is not set");
		else
			return WorldEditCompat.getPointB(player);
	}
	
	public static boolean needsCommands()
	{
		return !Bukkit.getPluginManager().isPluginEnabled("WorldEdit");
	}
}
