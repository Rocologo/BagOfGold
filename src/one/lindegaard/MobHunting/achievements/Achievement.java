package one.lindegaard.MobHunting.achievements;

import org.bukkit.inventory.ItemStack;

public interface Achievement
{
	public String getName();
	public String getID();
	
	public String getDescription();
	
	public double getPrize();
	
	public String getPrizeCmd();
	public String getPrizeCmdDescription();
	
	public ItemStack getSymbol();
}
