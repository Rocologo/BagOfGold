package au.com.mineauz.MobHunting.storage.asynch;

import java.util.HashSet;
import java.util.Set;

import au.com.mineauz.MobHunting.storage.AchievementStore;
import au.com.mineauz.MobHunting.storage.DataStore;
import au.com.mineauz.MobHunting.storage.DataStoreException;
import au.com.mineauz.MobHunting.storage.PlayerData;
import au.com.mineauz.MobHunting.storage.StatStore;

public class StoreTask implements DataStoreTask<Void>
{
	private HashSet<StatStore> mWaitingStats = new HashSet<StatStore>();
	private HashSet<AchievementStore> mWaitingAchievements = new HashSet<AchievementStore>();
	private HashSet<PlayerData> mWaitingPlayerData = new HashSet<PlayerData>();
	
	public StoreTask(Set<Object> waiting)
	{
		synchronized(waiting)
		{
			mWaitingStats.clear();
			mWaitingAchievements.clear();
			mWaitingPlayerData.clear();
			
			for(Object obj : waiting)
			{
				if(obj instanceof StatStore)
					mWaitingStats.add((StatStore)obj);
				if(obj instanceof AchievementStore)
					mWaitingAchievements.add((AchievementStore)obj);
				if(obj instanceof PlayerData)
					mWaitingPlayerData.add((PlayerData)obj);
			}
			
			waiting.clear();
		}
	}
	@Override
	public Void run( DataStore store ) throws DataStoreException
	{
		if(!mWaitingStats.isEmpty())
			store.saveStats(mWaitingStats);

		if(!mWaitingAchievements.isEmpty())
			store.saveAchievements(mWaitingAchievements);

		if(!mWaitingPlayerData.isEmpty())
			store.updatePlayerData(mWaitingPlayerData);

		return null;
	}

	@Override
	public boolean readOnly()
	{
		return false;
	}
}
