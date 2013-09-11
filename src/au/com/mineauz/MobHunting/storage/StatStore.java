package au.com.mineauz.MobHunting.storage;

import au.com.mineauz.MobHunting.StatType;

public class StatStore
{
	public StatStore(StatType type, String player)
	{
		this.type = type;
		playerName = player;
		amount = 1;
	}
	
	public StatStore(StatType type, String player, int amount)
	{
		this.type = type;
		playerName = player;
		this.amount = amount;
	}
	
	public StatType type;
	public String playerName;
	
	public int amount;
}
