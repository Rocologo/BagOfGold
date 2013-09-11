package au.com.mineauz.MobHunting.storage.asynch;

import java.util.ArrayList;
import java.util.List;
import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.storage.DataStore;
import au.com.mineauz.MobHunting.storage.DataStoreException;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class StatRetrieverTask implements DataStoreTask<List<StatStore>>
{
	private ExtendedMobType mType;
	private TimePeriod mPeriod;
	
	private boolean mKills;
	private boolean mAssists;
	
	private int mCount;
	
	public StatRetrieverTask(ExtendedMobType type, boolean kills, boolean assists, TimePeriod period, int count)
	{
		mType = type;
		mPeriod = period;
		mKills = kills;
		mAssists = assists;
		mCount = count;
	}
	
	@Override
	public List<StatStore> run( DataStore store ) throws DataStoreException
	{
		ArrayList<StatStore> results = new ArrayList<StatStore>();
		
		if(mKills)
			results.addAll(store.loadKills(mType, mPeriod, mCount));
		
		if(mAssists)
			results.addAll(store.loadAssists(mType, mPeriod, mCount));
		
		return results;
	}

	@Override
	public boolean readOnly()
	{
		return true;
	}

}
