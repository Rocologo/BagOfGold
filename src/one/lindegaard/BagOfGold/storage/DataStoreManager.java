package one.lindegaard.BagOfGold.storage;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.storage.asynch.IDataStoreTask;
import one.lindegaard.BagOfGold.storage.asynch.PlayerSettingsRetrieverTask;
import one.lindegaard.BagOfGold.storage.asynch.StoreTask;

public class DataStoreManager {

	private BagOfGold plugin;

	// Accessed on multiple threads
	private final HashSet<Object> mWaiting = new HashSet<Object>();

	// Accessed only from these threads
	private IDataStore mStore;
	private boolean mExit = false;

	// Accessed only from store thread
	private StoreThread mStoreThread;

	// Accessed only from retrieve thread
	private TaskThread mTaskThread;

	public DataStoreManager(BagOfGold plugin, IDataStore store) {
		this.plugin = plugin;
		mStore = store;
		mTaskThread = new TaskThread();
		int savePeriod = plugin.getConfigManager().savePeriod;
		if (savePeriod < 1200) {
			savePeriod = 1200;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED
					+ "[BagOfGold][Warning] save-period in your config.yml is too low. Please raise it to 1200 or higher");
		}
		mStoreThread = new StoreThread(savePeriod);
	}

	public boolean isRunning() {
		return mTaskThread.getState() != Thread.State.WAITING && mTaskThread.getState() != Thread.State.TERMINATED
				&& mStoreThread.getState() != Thread.State.WAITING
				&& mStoreThread.getState() != Thread.State.TERMINATED;
	}


	// *****************************************************************************
	// PlayerSettings
	// *****************************************************************************
	public void requestPlayerSettings(OfflinePlayer player, IDataCallback<PlayerSettings> callback) {
		mTaskThread.addTask(new PlayerSettingsRetrieverTask(player, mWaiting), callback);
	}

	/**
	 * Update the playerSettings in the Database
	 * 
	 * @param offlinePlayer
	 * @param learning_mode
	 * @param muted
	 * @param balance
	 * @param balanceChanges
	 * @param bankBalance
	 * @param bankBalanceChanges
	 */
	public void updatePlayerSettings(OfflinePlayer offlinePlayer, boolean learning_mode, boolean muted, double balance,
			double balanceChanges, double bankBalance, double bankBalanceChanges) {
		synchronized (mWaiting) {
			mWaiting.add(new PlayerSettings(offlinePlayer, learning_mode, muted, balance, balanceChanges, bankBalance,
					bankBalanceChanges));
		}
	}

	/**
	 * Update the playerSettings in the Database
	 * 
	 * @param offlinePlayer
	 * @param playerSetting
	 */
	public void updatePlayerSettings(OfflinePlayer offlinePlayer, PlayerSettings ps) {
		synchronized (mWaiting) {
			mWaiting.add(new PlayerSettings(offlinePlayer, ps));
		}
	}

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
	 * Get the playerId from the database
	 * 
	 * @param offlinePlayer
	 * @return
	 * @throws UserNotFoundException
	 */
	public int getPlayerId(OfflinePlayer offlinePlayer) throws UserNotFoundException {
		try {
			return mStore.getPlayerId(offlinePlayer);
		} catch (DataStoreException e) {
			if (plugin.getConfigManager().debug)
				e.printStackTrace();
		}
		throw new UserNotFoundException(
				"[BagOfGold] User " + offlinePlayer.getName() + " is not present in BagOfGold database");
	}

	// *****************************************************************************
	// Common
	// *****************************************************************************
	/**
	 * Flush all waiting data to the database
	 */
	public void flush() {
		if (mWaiting.size() != 0) {
			plugin.getMessages().debug("Force saving waiting %s data to database...", mWaiting.size());
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
				plugin.getMessages().debug("Waiting %s",n);
				n++;
			}
			plugin.getMessages().debug("mTaskThread.state=%s", mTaskThread.getState());
			if (mTaskThread.getState() == Thread.State.RUNNABLE) {
				plugin.getMessages().debug("Interupting mTaskThread");
				mTaskThread.interrupt();
			}
			plugin.getMessages().debug("mStoreThread.state=%s", mStoreThread.getState());
			//plugin.getMessages().debug("Interupting mStoreThread");
			//mStoreThread.interrupt();
			plugin.getMessages().debug("mTaskThread.state=%s", mTaskThread.getState());
			if (mTaskThread.getState() != Thread.State.WAITING) {
				mTaskThread.waitForEmptyQueue();
			} else {
				//plugin.getMessages().debug("Interupting mTaskThread");
				//mTaskThread.interrupt();
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
				plugin.getMessages().debug("StoreThread was interrupted");
			}
		}
	}

	private class Task {
		public Task(IDataStoreTask<?> task, IDataCallback<?> callback) {
			this.task = task;
			this.callback = callback;
		}

		public IDataStoreTask<?> task;

		public IDataCallback<?> callback;
	}

	private class CallbackCaller implements Runnable {
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
				plugin.getMessages().debug("waitForEmptyQueue: Waiting for %s+%s tasks to finish before closing connections.",
						mQueue.size(), mWaiting.size());
				while (!mQueue.isEmpty())
					mSignal.wait();
			}
		}

		public void setWriteOnlyMode(boolean writes) {
			mWritesOnly = writes;
		}

		public <T> void addTask(IDataStoreTask<T> storeTask, IDataCallback<T> callback) {
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
							Bukkit.getScheduler().runTask(plugin,
									new CallbackCaller((IDataCallback<Object>) task.callback, result, true));

					} catch (DataStoreException e) {
						plugin.getMessages().debug("DataStoreManager: TaskThread.run() failed!!!!!!!");
						if (task.callback != null)
							Bukkit.getScheduler().runTask(plugin,
									new CallbackCaller((IDataCallback<Object>) task.callback, e, false));
						else
							e.printStackTrace();
					}
				}

			} catch (InterruptedException e) {
				plugin.getMessages().debug(" TaskThread was interrupted");
			}
		}
	}

}
