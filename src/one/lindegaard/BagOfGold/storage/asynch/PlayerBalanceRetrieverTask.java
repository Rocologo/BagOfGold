package one.lindegaard.BagOfGold.storage.asynch;

import java.util.HashSet;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalances;
import one.lindegaard.CustomItemsLib.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.PlayerSettings;
import one.lindegaard.CustomItemsLib.storage.UserNotFoundException;

public class PlayerBalanceRetrieverTask implements IDataStoreTask<PlayerBalances> {

	private OfflinePlayer mPlayer;
	private HashSet<Object> mWaiting;

	public PlayerBalanceRetrieverTask(OfflinePlayer player, HashSet<Object> waiting) {
		mPlayer = player;
		mWaiting = waiting;
	}

	public PlayerBalances run(IDataStore store) throws DataStoreException {
		synchronized (mWaiting) {
			PlayerBalances ps = new PlayerBalances();
			try {
				ps = store.loadPlayerBalances(mPlayer);
			} catch (UserNotFoundException e) {
				String worldGroup;
				if (mPlayer.isOnline()) {
					Player player = (Player) mPlayer;
					worldGroup = Core.getWorldGroupManager().getCurrentWorldGroup(player);
				} else {
					worldGroup = Core.getWorldGroupManager().getDefaultWorldgroup();
				}
				BagOfGold.getInstance().getMessages().debug("PlayerBalanceRetriever: UserNotFound player=%s worldGrp=%s",
						mPlayer.getName(), worldGroup);
				if (mPlayer.isOnline()) {
					PlayerSettings playersettings = Core.getPlayerSettingsManager()
							.getPlayerSettings(mPlayer);
					if (!playersettings.getLastKnownWorldGrp().equals(worldGroup)) {
						playersettings.setLastKnownWorldGrp(worldGroup);
						Core.getDataStoreManager().insertPlayerSettings(playersettings);
					}
				}
			}
			return ps;
		}
	}

	@Override
	public boolean readOnly() {
		return true;
	}
}
