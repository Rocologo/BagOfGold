package one.lindegaard.MobHunting.storage.asynch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import one.lindegaard.MobHunting.StatType;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.storage.IDataStore;
import one.lindegaard.MobHunting.storage.StatStore;
import one.lindegaard.MobHunting.storage.TimePeriod;

public class StatRetrieverTask implements DataStoreTask<List<StatStore>> {
	private StatType mType;
	private TimePeriod mPeriod;

	private int mCount;
	private HashSet<Object> mWaiting;

	public StatRetrieverTask(StatType type, TimePeriod period, int count, HashSet<Object> waiting) {
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

				while (it.hasNext()) {
					StatStore stat = it.next();
					if (cached.getPlayer().getUniqueId().equals(stat.getPlayer().getUniqueId())
							&& cached.getType().equals(stat.getType())) {
						stat.setAmount(stat.getAmount() + cached.getAmount());
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
	public List<StatStore> run(IDataStore store) throws DataStoreException {
		synchronized (mWaiting) {
			List<StatStore> stats = store.loadPlayerStats(mType, mPeriod, mCount);
			updateUsingCache(stats);
			return stats;
		}
	}

	@Override
	public boolean readOnly() {
		return true;
	}

}
