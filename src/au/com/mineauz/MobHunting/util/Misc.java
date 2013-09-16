package au.com.mineauz.MobHunting.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class Misc
{
	public static boolean isAxe(ItemStack item)
	{
		return (item.getType() == Material.DIAMOND_AXE || item.getType() == Material.GOLD_AXE || item.getType() == Material.IRON_AXE || item.getType() == Material.STONE_AXE || item.getType() == Material.WOOD_AXE);
	}
	
	public static boolean isSword(ItemStack item)
	{
		return (item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.GOLD_SWORD || item.getType() == Material.IRON_SWORD || item.getType() == Material.STONE_SWORD || item.getType() == Material.WOOD_SWORD);
	}
	
	public static boolean isPick(ItemStack item)
	{
		return (item.getType() == Material.DIAMOND_PICKAXE || item.getType() == Material.GOLD_PICKAXE || item.getType() == Material.IRON_PICKAXE || item.getType() == Material.STONE_PICKAXE || item.getType() == Material.WOOD_PICKAXE);
	}
	
	public static double handleKillstreak(Player player)
	{
		HuntData data = MobHunting.instance.getHuntData(player);
		
		int lastKillstreakLevel = data.getKillstreakLevel();
		data.killStreak++;
		
		// Give a message notifying of killstreak increase
		if(data.getKillstreakLevel() != lastKillstreakLevel)
		{
			switch(data.getKillstreakLevel())
			{
			case 1:
				player.sendMessage(ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.1")); //$NON-NLS-1$
				break;
			case 2:
				player.sendMessage(ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.2")); //$NON-NLS-1$
				break;
			case 3:
				player.sendMessage(ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.3")); //$NON-NLS-1$
				break;
			default:
				player.sendMessage(ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.4")); //$NON-NLS-1$
				break;
			}
			
			player.sendMessage(ChatColor.GRAY + Messages.getString("mobhunting.killstreak.activated", "multiplier", String.format("%.1f",data.getKillstreakMultiplier()))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		
		return data.getKillstreakMultiplier();
	}
}
