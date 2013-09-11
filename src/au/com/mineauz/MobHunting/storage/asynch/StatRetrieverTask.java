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
	
	public StatRetrieverTask(ExtendedMobType type, boolean kills, boolean assists, TimePeriod period)
	{
		mType = type;
		mPeriod = period;
		mKills = kills;
		mAssists = assists;
	}
	
	@Override
	public List<StatStore> run( DataStore store ) throws DataStoreException
	{
		ArrayList<StatStore> results = new ArrayList<StatStore>();
		
		if(mKills)
			results.addAll(store.loadKills(mType, mPeriod, 100));
		
		if(mAssists)
			results.addAll(store.loadAssists(mType, mPeriod, 100));
		
		return results;
	}

	@Override
	public boolean readOnly()
	{
		return true;
	}

}
