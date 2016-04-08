package au.com.mineauz.MobHunting.storage.asynch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.storage.AchievementStore;
import au.com.mineauz.MobHunting.storage.IDataStore;
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
	private HashSet<Object> mWaiting;
	
	public AchievementRetrieverTask(Mode mode, OfflinePlayer player, HashSet<Object> waiting)
	{
		mMode = mode;
		mPlayer = player;
		mWaiting = waiting;
	}
	
	private void updateUsingCache(Set<AchievementStore> achievements)
	{
		for(Object obj : mWaiting)
		{
			if(obj instanceof AchievementStore)
			{
				AchievementStore cached = (AchievementStore)obj;
				if(!cached.player.getUniqueId().equals(mPlayer.getUniqueId()))
					continue;
				
				switch(mMode)
				{
				case Completed:
					if(cached.progress == -1)
						achievements.add(cached);
					break;
				case All:
				{
					if(cached.progress == -1)
					{
						achievements.add(cached);
						break;
					}
				}
				case InProgress:
				{
					if(cached.progress != -1)
					{
						for(AchievementStore a : achievements)
						{
							if(a.id.equals(cached.id))
							{
								if(a.progress < cached.progress)
									a.progress = cached.progress;
								break;
							}
						}
					}
					break;
				}
				}
			}
		}
	}
	
	public Set<AchievementStore> run(IDataStore store) throws DataStoreException
	{
		synchronized(mWaiting)
		{
			Set<AchievementStore> achievements = store.loadAchievements(mPlayer);
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
			
			updateUsingCache(achievements);
			return achievements;
		}
	}
	
	@Override
	public boolean readOnly()
	{
		return true;
	}
}
