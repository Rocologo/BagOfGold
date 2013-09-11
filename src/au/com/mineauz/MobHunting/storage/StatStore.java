package au.com.mineauz.MobHunting.storage;

import au.com.mineauz.MobHunting.Messages;

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
	
	public String translateName()
	{
		String[] parts = statName.split("_");
		
		if(parts[0].equals("total"))
			parts[0] = Messages.getString("stats.total");
		else
			parts[0] = Messages.getString("mobs." + parts[0] + ".name");
		
		if(parts[1].equals("assist"))
			parts[1] = Messages.getString("stats.assists");
		else
			parts[1] = Messages.getString("stats.kills");
		
		return Messages.getString("stats.name-format", "mob", parts[0], "stattype", parts[1]);
	}
}
