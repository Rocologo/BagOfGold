package one.lindegaard.MobHunting.storage.asynch;

import java.util.HashSet;
import java.util.Set;

import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.storage.AchievementStore;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.storage.IDataStore;
import one.lindegaard.MobHunting.storage.PlayerSettings;
import one.lindegaard.MobHunting.storage.StatStore;

public class StoreTask implements DataStoreTask<Void>
{
	private HashSet<StatStore> mWaitingPlayerStats = new HashSet<StatStore>();
	private HashSet<AchievementStore> mWaitingAchievements = new HashSet<AchievementStore>();
	private HashSet<PlayerSettings> mWaitingPlayerSettings = new HashSet<PlayerSettings>();
	private HashSet<Bounty> mWaitingBounties = new HashSet<Bounty>();
	
	
	public StoreTask(Set<Object> waiting)
	{
		synchronized(waiting)
		{
			mWaitingPlayerStats.clear();
			mWaitingAchievements.clear();
			mWaitingPlayerSettings.clear();
			mWaitingBounties.clear();
			
			for(Object obj : waiting)
			{
				if(obj instanceof StatStore)
					mWaitingPlayerStats.add((StatStore)obj);
				if(obj instanceof AchievementStore)
					mWaitingAchievements.add((AchievementStore)obj);
				if(obj instanceof PlayerSettings)
					mWaitingPlayerSettings.add((PlayerSettings)obj);
				if(obj instanceof Bounty)
					mWaitingBounties.add((Bounty)obj);
			}
			
			waiting.clear();
		}
	}
	@Override
	public Void run( IDataStore store ) throws DataStoreException
	{
		if(!mWaitingPlayerStats.isEmpty())
			store.savePlayerStats(mWaitingPlayerStats);

		if(!mWaitingAchievements.isEmpty())
			store.saveAchievements(mWaitingAchievements);

		if(!mWaitingPlayerSettings.isEmpty())
			store.updatePlayerSettings(mWaitingPlayerSettings);

		if(!mWaitingBounties.isEmpty())
			store.insertBounty(mWaitingBounties);
		
		return null;
	}

	@Override
	public boolean readOnly()
	{
		return false;
	}
}
