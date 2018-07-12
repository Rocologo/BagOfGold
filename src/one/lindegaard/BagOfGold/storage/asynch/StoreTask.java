package one.lindegaard.BagOfGold.storage.asynch;

import java.util.LinkedHashSet;
import java.util.Set;

import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.BagOfGold.storage.PlayerSettings;

public class StoreTask implements IDataStoreTask<Void> {
	private LinkedHashSet<PlayerSettings> mWaitingPlayerSettings = new LinkedHashSet<PlayerSettings>();

	public StoreTask(Set<Object> waiting) {
		synchronized (waiting) {
			mWaitingPlayerSettings.clear();

			for (Object obj : waiting) {
				if (obj instanceof PlayerSettings)
					mWaitingPlayerSettings.add((PlayerSettings) obj);
			}

			waiting.clear();
		}
	}

	@Override
	public Void run(IDataStore store) throws DataStoreException {
		if (!mWaitingPlayerSettings.isEmpty())
			store.savePlayerSettings(mWaitingPlayerSettings);

		return null;
	}

	@Override
	public boolean readOnly() {
		return false;
	}
}
