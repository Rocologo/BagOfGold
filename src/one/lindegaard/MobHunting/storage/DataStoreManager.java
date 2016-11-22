package one.lindegaard.MobHunting.storage;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.StatType;
import one.lindegaard.MobHunting.achievements.Achievement;
import one.lindegaard.MobHunting.achievements.ProgressAchievement;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.bounty.BountyStatus;
import one.lindegaard.MobHunting.mobs.ExtendedMob;
import one.lindegaard.MobHunting.mobs.ExtendedMobManager;
import one.lindegaard.MobHunting.mobs.MinecraftMob;
import one.lindegaard.MobHunting.storage.asynch.AchievementRetrieverTask;
import one.lindegaard.MobHunting.storage.asynch.DataStoreTask;
import one.lindegaard.MobHunting.storage.asynch.StatRetrieverTask;
import one.lindegaard.MobHunting.storage.asynch.StoreTask;
import one.lindegaard.MobHunting.storage.asynch.AchievementRetrieverTask.Mode;
import one.lindegaard.MobHunting.storage.asynch.BountyRetrieverTask;

public class DataStoreManager {
	// Accessed on multiple threads
	private HashSet<Object> mWaiting = new HashSet<Object>();

	// Accessed only from these threads
	private IDataStore mStore;
	private boolean mExit = false;

	// Accessed only from store thread
	private StoreThread mStoreThread;

	// Accessed only from retrieve thread
	private TaskThread mTaskThread;

	public DataStoreManager(IDataStore store) {
		mStore = store;
		mTaskThread = new TaskThread();
		mStoreThread = new StoreThread(MobHunting.getConfigManager().savePeriod);
	}

	public boolean isRunning() {
		return mTaskThread.getState() != Thread.State.WAITING && mTaskThread.getState() != Thread.State.TERMINATED
				&& mStoreThread.getState() != Thread.State.WAITING
				&& mStoreThread.getState() != Thread.State.TERMINATED;

		// mTaskThread.isAlive() && mStoreThread.isAlive();
	}

	// **************************************************************************************
	// PlayerStats
	// **************************************************************************************
	public void recordKill(OfflinePlayer player, MinecraftMob type, ExtendedMob mob, boolean bonusMob) {
		synchronized (mWaiting) {
			mWaiting.add(new StatStore(StatType.fromMobType(type, true), mob, player));

			if (bonusMob)
				mWaiting.add(new StatStore(StatType.fromMobType(MinecraftMob.BonusMob, true), mob, player));
		}
	}

	public void recordAssist(OfflinePlayer player, OfflinePlayer killer, MinecraftMob type, ExtendedMob mob,
			boolean bonusMob) {
		synchronized (mWaiting) {
			mWaiting.add(new StatStore(StatType.fromMobType(type, false), mob, player));

			if (bonusMob)
				mWaiting.add(new StatStore(StatType.fromMobType(MinecraftMob.BonusMob, false), mob, player));
		}
	}

	public void requestStats(StatType type, TimePeriod period, int count, IDataCallback<List<StatStore>> callback) {
		mTaskThread.addTask(new StatRetrieverTask(type, period, count, mWaiting), callback);
	}

	// **************************************************************************************
	// Achievements
	// **************************************************************************************
	public void recordAchievement(OfflinePlayer player, Achievement achievement) {
		synchronized (mWaiting) {
			mWaiting.add(new AchievementStore(achievement.getID(), player, -1));
			mWaiting.add(new StatStore(StatType.AchievementCount, ExtendedMobManager.getFirstMob(), player));
		}
	}

	public void recordAchievementProgress(OfflinePlayer player, ProgressAchievement achievement, int progress) {
		synchronized (mWaiting) {
			mWaiting.add(new AchievementStore(achievement.getID(), player, progress));
		}
	}

	public void requestAllAchievements(OfflinePlayer player, IDataCallback<Set<AchievementStore>> callback) {
		mTaskThread.addTask(new AchievementRetrieverTask(Mode.All, player, mWaiting), callback);
	}

	public void requestCompletedAchievements(OfflinePlayer player, IDataCallback<Set<AchievementStore>> callback) {
		mTaskThread.addTask(new AchievementRetrieverTask(Mode.Completed, player, mWaiting), callback);
	}

	public void requestInProgressAchievements(OfflinePlayer player, IDataCallback<Set<AchievementStore>> callback) {
		mTaskThread.addTask(new AchievementRetrieverTask(Mode.InProgress, player, mWaiting), callback);
	}

	// *****************************************************************************
	// Bounties
	// *****************************************************************************
	public void insertBounty(Bounty bounty) {
		HashSet<Bounty> bounties = new HashSet<Bounty>();
		bounties.add(bounty);
		try {
			mStore.insertBounty(bounties);
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
	}

	public void deleteBounty(Bounty bounty) {
		HashSet<Bounty> bounties = new HashSet<Bounty>();
		bounties.add(bounty);
		try {
			mStore.deleteBounty(bounties);
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
	}

	public void cancelBounty(Bounty bounty) {
		bounty.setStatus(BountyStatus.canceled);
		// synchronized (mWaiting) {
		// mWaiting.add(new Bounty(bounty));
		// }
		HashSet<Bounty> bounties = new HashSet<Bounty>();
		bounties.add(bounty);
		try {
			mStore.cancelBounty(bounties);
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
	}

	public void updateBounty(Bounty bounty) {
		// synchronized (mWaiting) {
		// mWaiting.add(new Bounty(bounty));
		// }
		HashSet<Bounty> bounties = new HashSet<Bounty>();
		bounties.add(bounty);
		try {
			mStore.updateBounty(bounties);
		} catch (DataStoreException e) {
			e.printStackTrace();
		}

	}

	public void requestBounties(BountyStatus mode, OfflinePlayer player, IDataCallback<Set<Bounty>> callback) {
		mTaskThread.addTask(new BountyRetrieverTask(mode, player, mWaiting), callback);
	}

	// *****************************************************************************
	// PlayerSettings
	// *****************************************************************************
	/**
	 * Gets an offline player using the last known name. WARNING: This does a
	 * database lookup directly. This will block waiting for a reply
	 */
	public OfflinePlayer getPlayerByName(String name) {
		try {
			return mStore.getPlayerByName(name);
		} catch (UserNotFoundException e) {
			return null;
		} catch (DataStoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param player
	 * @return Get PlayerSettings for player. If player does not exist in
	 *         Database, a new record will be created.
	 * @throws SQLException
	 */
	public PlayerSettings getPlayerSettings(Player player) {
		try {
			return mStore.getPlayerSettings(player);
		} catch (UserNotFoundException e) {
			Messages.debug("Saving new PlayerSettings for %s to database.", player.getName());
			PlayerSettings ps = new PlayerSettings(player, MobHunting.getConfigManager().learningMode, false);
			try {
				mStore.insertPlayerSettings(ps);
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

	/**
	 * Update the playerSettings in the Database
	 * 
	 * @param player
	 * @param learning_mode
	 * @param muted
	 */
	public void updatePlayerSettings(Player player, boolean learning_mode, boolean muted) {
		Set<PlayerSettings> ps = new HashSet<PlayerSettings>();
		ps.add(new PlayerSettings(player, learning_mode, muted));
		try {
			mStore.updatePlayerSettings(ps);
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the playerId from the database
	 * 
	 * @param offlinePlayer
	 * @return
	 * @throws UserNotFoundException
	 */
	public int getPlayerId(OfflinePlayer offlinePlayer) throws UserNotFoundException {
		try {
			return mStore.getPlayerId(offlinePlayer);
		} catch (SQLException | DataStoreException e) {
			if (MobHunting.getConfigManager().killDebug)
				e.printStackTrace();
		}
		throw new UserNotFoundException(
				"[MobHunting] User " + offlinePlayer.getName() + " is not present in MobHunting database");
	}

	// *****************************************************************************
	// Common
	// *****************************************************************************
	/**
	 * Flush all waiting data to the database
	 */
	public void flush() {
		if (mWaiting.size() != 0) {
			Messages.debug("Flushing waiting %s data to database...", mWaiting.size());
			mTaskThread.addTask(new StoreTask(mWaiting), null);
		}
	}

	/**
	 * Shutdown the DataStoreManager
	 */
	public void shutdown() {
		mExit = true;
		flush();
		mTaskThread.setWriteOnlyMode(true);
		int n = 0;
		try {
			while (mTaskThread.getState() != Thread.State.WAITING && mTaskThread.getState() != Thread.State.TERMINATED
					&& n < 40) {
				Thread.sleep(500);
				n++;
			}
			Messages.debug("mTaskThread.state=%s", mTaskThread.getState());
			if (mTaskThread.getState() == Thread.State.RUNNABLE) {
				Messages.debug("Interupting mTaskThread");
				mTaskThread.interrupt();
			}
			Messages.debug("mStoreThread.state=%s", mStoreThread.getState());
			Messages.debug("Interupting mStoreThread");
			mStoreThread.interrupt();
			Messages.debug("mTaskThread.state=%s", mTaskThread.getState());
			if (mTaskThread.getState() != Thread.State.WAITING) {
				mTaskThread.waitForEmptyQueue();
			} else {
				Messages.debug("Interupting mTaskThread");
				mTaskThread.interrupt();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Wait until all data has been updated
	 */
	public void waitForUpdates() {
		flush();
		try {
			mTaskThread.waitForEmptyQueue();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor for the StoreThread
	 * 
	 * @author Rocologo
	 *
	 */
	private class StoreThread extends Thread {
		private int mSaveInterval;

		public StoreThread(int interval) {
			super("MH StoreThread");
			start();
			mSaveInterval = interval;
		}

		@Override
		public void run() {
			try {
				while (true) {
					synchronized (this) {
						if (mExit && mWaiting.size() == 0) {
							break;
						}
					}
					mTaskThread.addTask(new StoreTask(mWaiting), null);
					Thread.sleep(mSaveInterval * 50);
				}
			} catch (InterruptedException e) {
				System.out.println("[MobHunting] StoreThread was interrupted");
			}
		}
	}

	private static class Task {
		public Task(DataStoreTask<?> task, IDataCallback<?> callback) {
			this.task = task;
			this.callback = callback;
		}

		public DataStoreTask<?> task;

		public IDataCallback<?> callback;
	}

	private static class CallbackCaller implements Runnable {
		private IDataCallback<Object> mCallback;
		private Object mObj;
		private boolean mSuccess;

		public CallbackCaller(IDataCallback<Object> callback, Object obj, boolean success) {
			mCallback = callback;
			mObj = obj;
			mSuccess = success;
		}

		@Override
		public void run() {
			if (mSuccess)
				mCallback.onCompleted(mObj);
			else
				mCallback.onError((Throwable) mObj);
		}

	}

	private class TaskThread extends Thread {
		private BlockingQueue<Task> mQueue;
		private boolean mWritesOnly = false;

		private Object mSignal = new Object();

		public TaskThread() {
			super("MH TaskThread");

			mQueue = new LinkedBlockingQueue<Task>();

			start();
		}

		public void waitForEmptyQueue() throws InterruptedException {
			if (mQueue.isEmpty())
				return;

			synchronized (mSignal) {
				Messages.debug("waitForEmptyQueue: Waiting for %s+%s tasks to finish before closing connections.",
						mQueue.size(), mWaiting.size());
				while (!mQueue.isEmpty())
					mSignal.wait();
			}
		}

		public void setWriteOnlyMode(boolean writes) {
			mWritesOnly = writes;
		}

		public <T> void addTask(DataStoreTask<T> storeTask, IDataCallback<T> callback) {
			try {
				mQueue.put(new Task(storeTask, callback));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			try {
				while (true) {
					if (MobHunting.getConfigManager().debugSQL && mQueue.size() > 20) {
						Messages.debug("TaskThread: mQueue.size()=%s", mQueue.size());
					}
					if (mQueue.isEmpty())
						synchronized (mSignal) {
							mSignal.notifyAll();
						}
					// } else { //DONT ENABLE THIS CAUSES 100 CPU USAGE

					Task task = mQueue.take();

					if (mWritesOnly && task.task.readOnly())
						continue;

					try {

						Object result = task.task.run(mStore);

						if (task.callback != null && !mExit)
							Bukkit.getScheduler().runTask(MobHunting.getInstance(),
									new CallbackCaller((IDataCallback<Object>) task.callback, result, true));

					} catch (DataStoreException e) {
						Messages.debug("DataStoreManager: TaskThread.run() failed!!!!!!!");
						if (task.callback != null)
							Bukkit.getScheduler().runTask(MobHunting.getInstance(),
									new CallbackCaller((IDataCallback<Object>) task.callback, e, false));
						else
							e.printStackTrace();
					}
				}

			} catch (InterruptedException e) {
				System.out.println("[MobHunting] TaskThread was interrupted");
			}
		}
	}

}
