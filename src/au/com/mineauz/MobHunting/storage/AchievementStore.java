package au.com.mineauz.MobHunting.storage;

public class AchievementStore
{
	public AchievementStore(String id, String player, int progress)
	{
		this.id = id;
		playerName = player;
		this.progress = progress;
	}
	
	public String id;
	public String playerName;
	public int progress;
}
