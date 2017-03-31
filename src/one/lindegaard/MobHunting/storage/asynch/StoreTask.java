package one.lindegaard.MobHunting.storage.asynch;

import java.util.HashSet;
import java.util.Set;

import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.storage.AchievementStore;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.storage.IDataStore;
import one.lindegaard.MobHunting.storage.PlayerSettings;
import one.lindegaard.MobHunting.storage.StatStore;

public class StoreTask implements IDataStoreTask<Void> {
	private HashSet<StatStore> mWaitingPlayerStats = new HashSet<StatStore>();
	private HashSet<AchievementStore> mWaitingAchievements = new HashSet<AchievementStore>();
	private HashSet<PlayerSettings> mWaitingPlayerSettings = new HashSet<PlayerSettings>();
	private HashSet<Bounty> mWaitingBounties = new HashSet<Bounty>();

	public StoreTask(Set<Object> waiting) {
		synchronized (waiting) {
			mWaitingPlayerStats.clear();
			mWaitingAchievements.clear();
			mWaitingPlayerSettings.clear();
			mWaitingBounties.clear();

			for (Object obj : waiting) {
				if (obj instanceof PlayerSettings)
					mWaitingPlayerSettings.add((PlayerSettings) obj);
				else if (obj instanceof AchievementStore)
					mWaitingAchievements.add((AchievementStore) obj);
				else if (obj instanceof StatStore)
					mWaitingPlayerStats.add((StatStore) obj);
				else if (obj instanceof Bounty)
					mWaitingBounties.add((Bounty) obj);
			}

			waiting.clear();
		}
	}

	@Override
	public Void run(IDataStore store) throws DataStoreException {
		if (!mWaitingPlayerSettings.isEmpty())
			store.savePlayerSettings(mWaitingPlayerSettings);

		if (!mWaitingPlayerStats.isEmpty())
			store.savePlayerStats(mWaitingPlayerStats);

		if (!mWaitingAchievements.isEmpty())
			store.saveAchievements(mWaitingAchievements);

		if (!mWaitingBounties.isEmpty())
			store.saveBounties(mWaitingBounties);

		return null;
	}

	@Override
	public boolean readOnly() {
		return false;
	}
}
