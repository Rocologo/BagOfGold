package au.com.mineauz.MobHunting.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Misc
{
	public static boolean isAxe(ItemStack item)
	{
		return (item.getType() == Material.DIAMOND_AXE || item.getType() == Material.GOLD_AXE || item.getType() == Material.IRON_AXE || item.getType() == Material.STONE_AXE || item.getType() == Material.WOOD_AXE);
	}
}
