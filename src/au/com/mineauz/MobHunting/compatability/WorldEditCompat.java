package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WorldEditCompat
{
	private static WorldEditPlugin mPlugin;
	
	public WorldEditCompat()
	{
		mPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
	}
	
	public static Location getPointA(Player player) throws IllegalArgumentException
	{
		if(mPlugin == null)
			throw new IllegalArgumentException("WorldEdit is not present");
		
		Selection sel = mPlugin.getSelection(player);
		
		if(sel == null)
			throw new IllegalArgumentException("You have nothing selected");
		
		if(!(sel instanceof CuboidSelection))
			throw new IllegalArgumentException("Only cuboid selections can be used for leaderboards");
		
		return sel.getMinimumPoint();
	}
	
	public static Location getPointB(Player player) throws IllegalArgumentException
	{
		if(mPlugin == null)
			throw new IllegalArgumentException("WorldEdit is not present");
		
		Selection sel = mPlugin.getSelection(player);
		
		if(sel == null)
			throw new IllegalArgumentException("You have nothing selected");
		
		if(!(sel instanceof CuboidSelection))
			throw new IllegalArgumentException("Only cuboid selections can be used for leaderboards");
		
		return sel.getMaximumPoint();
	}
}
