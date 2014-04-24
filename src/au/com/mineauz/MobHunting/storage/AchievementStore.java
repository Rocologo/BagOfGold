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
}
