package au.com.mineauz.MobHunting.storage.asynch;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.storage.AchievementStore;
import au.com.mineauz.MobHunting.storage.DataStore;
import au.com.mineauz.MobHunting.storage.DataStoreException;

public class AchievementRetrieverTask implements DataStoreTask<Set<AchievementStore>>
{
	public enum Mode
	{
		All,
		Completed,
		InProgress
	}
	
	private Mode mMode;
	private OfflinePlayer mPlayer;
	
	public AchievementRetrieverTask(Mode mode, OfflinePlayer player)
	{
		mMode = mode;
		mPlayer = player;
	}
	
	public Set<AchievementStore> run(DataStore store) throws DataStoreException
	{
		Set<AchievementStore> achievements = store.loadAchievements(mPlayer.getName());
		switch(mMode)
		{
		case All:
			break;
		case Completed:
		{
			Iterator<AchievementStore> it = achievements.iterator();
			while(it.hasNext())
			{
				AchievementStore achievement = it.next();
				if(achievement.progress != -1)
					it.remove();
			}
			break;
		}
		case InProgress:
		{
			Iterator<AchievementStore> it = achievements.iterator();
			while(it.hasNext())
			{
				AchievementStore achievement = it.next();
				if(achievement.progress == -1)
					it.remove();
			}
			break;
		}
		}
		
		return achievements;
	}
	
	@Override
	public boolean readOnly()
	{
		return true;
	}
}
