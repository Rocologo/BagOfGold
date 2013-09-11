package au.com.mineauz.MobHunting.storage.asynch;

import java.util.List;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.DataStore;
import au.com.mineauz.MobHunting.storage.DataStoreException;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class StatRetrieverTask implements DataStoreTask<List<StatStore>>
{
	private StatType mType;
	private TimePeriod mPeriod;
	
	private int mCount;
	
	public StatRetrieverTask(StatType type, TimePeriod period, int count)
	{
		mType = type;
		mPeriod = period;
		mCount = count;
	}
	
	@Override
	public List<StatStore> run( DataStore store ) throws DataStoreException
	{
		return store.loadStats(mType, mPeriod, mCount);
	}

	@Override
	public boolean readOnly()
	{
		return true;
	}

}
