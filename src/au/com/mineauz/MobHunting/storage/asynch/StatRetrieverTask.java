package au.com.mineauz.MobHunting.storage.asynch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.DataStore;
import au.com.mineauz.MobHunting.storage.DataStoreException;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class StatRetrieverTask implements DataStoreTask<List<StatStore>> {
	private StatType mType;
	private TimePeriod mPeriod;

	private int mCount;
	private HashSet<Object> mWaiting;

	public StatRetrieverTask(StatType type, TimePeriod period, int count,
			HashSet<Object> waiting) {
		mType = type;
		mPeriod = period;
		mCount = count;

		mWaiting = waiting;
	}

	private void updateUsingCache(List<StatStore> stats) {
		for (Object obj : mWaiting) {
			if (obj instanceof StatStore) {
				StatStore cached = (StatStore) obj;

				Iterator<StatStore> it = stats.iterator();
				boolean found = false;
				if (!it.hasNext())
					while (it.hasNext()) {
						StatStore stat = it.next();
						if (cached.getPlayer().getUniqueId()
								.equals(stat.getPlayer().getUniqueId())
								&& cached.getType().equals(stat.getType())) {
							stat.setAmount(stat.getAmount()
									+ cached.getAmount());
							// stat.amount += cached.amount;

							found = true;
							break;
						}
					}

				if (!found && cached.getType().equals(mType))
					stats.add(cached);
			}
		}
	}

	@Override
	public List<StatStore> run(DataStore store) throws DataStoreException {
		synchronized (mWaiting) {
			List<StatStore> stats = store.loadStats(mType, mPeriod, mCount);
			updateUsingCache(stats);
			return stats;
		}
	}

	@Override
	public boolean readOnly() {
		return true;
	}

}
