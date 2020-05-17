package one.lindegaard.BagOfGold.storage.asynch;

import java.util.LinkedHashSet;
import java.util.Set;

import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.Core.PlayerSettings;

public class StoreTask implements IDataStoreTask<Void> {
	private LinkedHashSet<PlayerSettings> mWaitingPlayerSettings = new LinkedHashSet<PlayerSettings>();
	private LinkedHashSet<PlayerBalance> mWaitingPlayerBalances = new LinkedHashSet<PlayerBalance>();

	public StoreTask(Set<Object> waiting) {
		synchronized (waiting) {
			mWaitingPlayerSettings.clear();
			mWaitingPlayerBalances.clear();

			for (Object obj : waiting) {
				if (obj instanceof PlayerSettings)
					mWaitingPlayerSettings.add((PlayerSettings) obj);
				else if (obj instanceof PlayerBalance)
					mWaitingPlayerBalances.add((PlayerBalance) obj);
			}

			waiting.clear();
		}
	}

	@Override
	public Void run(IDataStore store) throws DataStoreException {
		if (!mWaitingPlayerSettings.isEmpty())
			store.savePlayerSettings(mWaitingPlayerSettings, true);
		if (!mWaitingPlayerBalances.isEmpty())
			store.savePlayerBalances(mWaitingPlayerBalances, true);

		return null;
	}

	@Override
	public boolean readOnly() {
		return false;
	}
}
