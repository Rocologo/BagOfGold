package one.lindegaard.BagOfGold.storage.asynch;

import java.util.HashSet;

import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerSettings;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.BagOfGold.storage.UserNotFoundException;

public class PlayerSettingsRetrieverTask implements IDataStoreTask<PlayerSettings> {

	private OfflinePlayer mPlayer;
	private HashSet<Object> mWaiting;

	public PlayerSettingsRetrieverTask(OfflinePlayer player, HashSet<Object> waiting) {
		mPlayer = player;
		mWaiting = waiting;
	}

	public PlayerSettings run(IDataStore store) throws DataStoreException {
		synchronized (mWaiting) {
			try {
				return store.loadPlayerSettings(mPlayer);
			} catch (UserNotFoundException e) {
				BagOfGold.getInstance().getMessages().debug("Insert new PlayerSettings for %s to database.",
						mPlayer.getName());
				String worldgroup = mPlayer.isOnline()
						? BagOfGold.getInstance().getWorldGroupManager().getCurrentWorldGroup(mPlayer)
						: BagOfGold.getInstance().getWorldGroupManager().getDefaultWorldgroup();
				PlayerSettings ps = new PlayerSettings(mPlayer, worldgroup,
						BagOfGold.getInstance().getConfigManager().learningMode, false, null, null,
						System.currentTimeMillis(), System.currentTimeMillis());
				try {
					store.insertPlayerSettings(ps);
				} catch (DataStoreException e1) {
					e1.printStackTrace();
				}
				return ps;
			} catch (DataStoreException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public boolean readOnly() {
		return true;
	}
}
