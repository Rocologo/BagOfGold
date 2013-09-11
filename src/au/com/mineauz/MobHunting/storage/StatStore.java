package au.com.mineauz.MobHunting.storage;

public class StatStore
{
	public StatStore(String name, String player)
	{
		statName = name;
		playerName = player;
		amount = 1;
	}
	
	public StatStore(String name, String player, int amount)
	{
		statName = name;
		playerName = player;
		this.amount = amount;
	}
	
	public String statName;
	public String playerName;
	
	public int amount;
}
