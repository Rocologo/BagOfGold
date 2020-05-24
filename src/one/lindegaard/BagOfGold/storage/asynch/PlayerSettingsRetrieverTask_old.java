package one.lindegaard.BagOfGold.storage.asynch;

import java.util.HashSet;

import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.PlayerSettings;

public class PlayerSettingsRetrieverTask_old implements IDataStoreTask<PlayerSettings> {

	private OfflinePlayer mPlayer;
	private HashSet<Object> mWaiting;

	public PlayerSettingsRetrieverTask_old(OfflinePlayer player, HashSet<Object> waiting) {
		mPlayer = player;
		mWaiting = waiting;
	}

	@Override
	public PlayerSettings run(IDataStore store) throws DataStoreException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**public PlayerSettings run(IDataStore store) throws DataStoreException {
		synchronized (mWaiting) {
			try {
				return store.loadPlayerSettings(mPlayer);
			} catch (UserNotFoundException e) {
				BagOfGold.getInstance().getMessages().debug("Insert new PlayerSettings for %s to database.",
						mPlayer.getName());
				String worldgroup = mPlayer.isOnline() ? Core.getWorldGroupManager().getCurrentWorldGroup(mPlayer)
						: Core.getWorldGroupManager().getDefaultWorldgroup();
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
	}**/

	@Override
	public boolean readOnly() {
		return true;
	}

	
}
