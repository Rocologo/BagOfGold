package one.lindegaard.BagOfGold.storage.asynch;

import java.util.LinkedHashSet;
import java.util.Set;

import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataStore;

public class StoreTask implements IDataStoreTask<Void> {
	private LinkedHashSet<PlayerBalance> mWaitingPlayerBalances = new LinkedHashSet<PlayerBalance>();

	public StoreTask(Set<Object> waiting) {
		synchronized (waiting) {
			mWaitingPlayerBalances.clear();

			for (Object obj : waiting) {
				if (obj instanceof PlayerBalance)
					mWaitingPlayerBalances.add((PlayerBalance) obj);
			}

			waiting.clear();
		}
	}

	@Override
	public Void run(IDataStore store) throws DataStoreException {
		if (!mWaitingPlayerBalances.isEmpty())
			store.savePlayerBalances(mWaitingPlayerBalances, true);

		return null;
	}

	@Override
	public boolean readOnly() {
		return false;
	}
}
