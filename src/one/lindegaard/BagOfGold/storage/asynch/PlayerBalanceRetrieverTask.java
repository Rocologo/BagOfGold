package one.lindegaard.BagOfGold.storage.asynch;

import java.util.HashSet;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.PlayerBalances;
import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataStore;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.PlayerSettings;
import one.lindegaard.Core.storage.UserNotFoundException;

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
				GameMode gamemode;
				if (mPlayer.isOnline()) {
					Player player = (Player) mPlayer;
					worldGroup = Core.getWorldGroupManager().getCurrentWorldGroup(player);
					gamemode = player.getGameMode();
				} else {
					worldGroup = Core.getWorldGroupManager().getDefaultWorldgroup();
					gamemode = Core.getWorldGroupManager().getDefaultGameMode();
				}
				if (!ps.has(worldGroup, gamemode)) {
					BagOfGold.getInstance().getMessages().debug("PlayerBalanceRetriver - %s%s does not exist -creating",
							worldGroup, gamemode);
					PlayerBalance pb = new PlayerBalance(mPlayer, worldGroup, gamemode);
					ps.putPlayerBalance(pb);
					BagOfGold.getInstance().getPlayerBalanceManager().setPlayerBalance(mPlayer, pb);
					BagOfGold.getInstance().getDataStoreManager().updatePlayerBalance(mPlayer, pb);
				}
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
