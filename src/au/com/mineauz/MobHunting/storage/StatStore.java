package au.com.mineauz.MobHunting.storage;

import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.StatType;

public class StatStore
{
	public StatStore(StatType type, OfflinePlayer player)
	{
		this.type = type;
		this.player = player;
		amount = 1;
	}
	
	public StatStore(StatType type, OfflinePlayer player, int amount)
	{
		this.type = type;
		this.player = player;
		this.amount = amount;
	}
	
	public StatType type;
	public OfflinePlayer player;
	
	public int amount;
	
	@Override
	public String toString()
	{
		return String.format("StatStore: {player: %s type: %s amount: %d}", player.getName(), type.getDBColumn(), amount);  
	}
}
