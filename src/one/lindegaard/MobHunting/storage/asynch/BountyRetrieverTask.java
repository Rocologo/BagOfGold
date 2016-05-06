package one.lindegaard.MobHunting.storage.asynch;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.storage.IDataStore;

public class BountyRetrieverTask implements DataStoreTask<Set<Bounty>> {
	public enum BountyMode {
		All, Completed, Open
	}

	private BountyMode mMode;
	private OfflinePlayer mPlayer;
	private HashSet<Object> mWaiting;

	public BountyRetrieverTask(BountyMode mode, OfflinePlayer player, HashSet<Object> waiting) {
		mMode = mode;
		mPlayer = player;
		mWaiting = waiting;
	}

	private void updateUsingCache(Set<Bounty> bounties) {
		for (Object obj : mWaiting) {
			if (obj instanceof Bounty) {
				Bounty cached = (Bounty) obj;
				if (MobHunting.getBountyManager().hasBounty(cached)) {
					continue;
				}

				switch (mMode) {
				case Completed:
					if (cached.isCompleted())
						bounties.add(cached);
					break;
				case All: {
					bounties.add(cached);
					break;
				}
				case Open:
					if (!cached.isCompleted())
						bounties.add(cached);
					break;
				}
			}
		}
	}

	public Set<Bounty> run(IDataStore store) throws DataStoreException {
		synchronized (mWaiting) {
			Set<Bounty> bounties = store.loadBounties(mPlayer);
			switch (mMode) {
			case All:
				break;
			case Completed: {
				Iterator<Bounty> it = bounties.iterator();
				while (it.hasNext()) {
					Bounty bounty = it.next();
					if (!bounty.isCompleted())
						it.remove();
				}
				break;
			}
			case Open: {
				Iterator<Bounty> it = bounties.iterator();
				while (it.hasNext()) {
					Bounty bounty = it.next();
					if (bounty.isCompleted())
						it.remove();
				}
				break;
			}
			}
			updateUsingCache(bounties);
			return bounties;
		}
	}

	@Override
	public boolean readOnly() {
		return true;
	}
}
