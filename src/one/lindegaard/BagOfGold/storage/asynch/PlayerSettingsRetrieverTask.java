package one.lindegaard.BagOfGold.storage.asynch;

import java.sql.SQLException;
import java.util.HashSet;

import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.Messages;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
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
				Messages.debug("Saving new PlayerSettings for %s to database.", mPlayer.getName());
				PlayerSettings ps = new PlayerSettings(mPlayer, BagOfGold.getConfigManager().learningMode, false,
						BagOfGold.getConfigManager().startingBalance, 0, 0, 0);
				try {
					store.insertPlayerSettings(ps);
				} catch (DataStoreException e1) {
					e1.printStackTrace();
				}
				return ps;
			} catch (DataStoreException e) {
				e.printStackTrace();
				return null;
			} catch (SQLException e) {
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
