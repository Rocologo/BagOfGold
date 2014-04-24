package au.com.mineauz.MobHunting.storage;

import org.bukkit.OfflinePlayer;

public class AchievementStore
{
	public AchievementStore(String id, OfflinePlayer player, int progress)
	{
		this.id = id;
		this.player = player;
		this.progress = progress;
	}
	
	public String id;
	public OfflinePlayer player;
	public int progress;
	
	@Override
	public int hashCode()
	{
		return id.hashCode() | player.getUniqueId().hashCode();
	}
	
	@Override
	public boolean equals( Object obj )
	{
		if(!(obj instanceof AchievementStore))
			return false;
		
		AchievementStore other = (AchievementStore)obj;
		
		return id.equals(other.id) && player.getUniqueId().equals(other.player.getUniqueId()) && progress == other.progress;
	}
}
