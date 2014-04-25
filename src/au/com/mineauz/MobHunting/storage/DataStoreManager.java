package au.com.mineauz.MobHunting.storage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.achievements.Achievement;
import au.com.mineauz.MobHunting.achievements.ProgressAchievement;
import au.com.mineauz.MobHunting.storage.asynch.AchievementRetrieverTask;
import au.com.mineauz.MobHunting.storage.asynch.AchievementRetrieverTask.Mode;
import au.com.mineauz.MobHunting.storage.asynch.DataStoreTask;
import au.com.mineauz.MobHunting.storage.asynch.StatRetrieverTask;
import au.com.mineauz.MobHunting.storage.asynch.StoreTask;

public class DataStoreManager
{
	// Accessed on multiple threads
	private HashSet<Object> mWaiting = new HashSet<Object>();
	
	// Accessed only from these threads
	private DataStore mStore;
	private boolean mExit = false;
	
	// Accessed only from store thread
	private StoreThread mStoreThread;
	
	// Accessed only from retreive thread
	private TaskThread mTaskThread;
	
	
	public DataStoreManager(DataStore store)
	{
		mStore = store;
		
		mTaskThread = new TaskThread();
		mStoreThread = new StoreThread(MobHunting.config().savePeriod);
	}
	
	public void recordKill(OfflinePlayer player, ExtendedMobType type, boolean bonusMob)
	{
		synchronized(mWaiting)
		{
			mWaiting.add(new StatStore(StatType.fromMobType(type, true), player));
			mWaiting.add(new StatStore(StatType.KillsTotal, player));
			
			if(bonusMob)
				mWaiting.add(new StatStore(StatType.fromMobType(ExtendedMobType.BonusMob, true), player));
		}
	}
	
	public void recordAssist(OfflinePlayer player, OfflinePlayer killer, ExtendedMobType type, boolean bonusMob)
	{
		synchronized(mWaiting)
		{
			mWaiting.add(new StatStore(StatType.fromMobType(type, false), player));
			mWaiting.add(new StatStore(StatType.AssistsTotal, player));
			
			if(bonusMob)
				mWaiting.add(new StatStore(StatType.fromMobType(ExtendedMobType.BonusMob, false), player));
		}
	}
	
	public void recordAchievement(OfflinePlayer player, Achievement achievement)
	{
		synchronized(mWaiting)
		{
			mWaiting.add(new AchievementStore(achievement.getID(), player, -1));
			mWaiting.add(new StatStore(StatType.AchievementCount, player));
		}
	}
	
	public void recordAchievementProgress(OfflinePlayer player, ProgressAchievement achievement, int progress)
	{
		synchronized(mWaiting)
		{
			mWaiting.add(new AchievementStore(achievement.getID(), player, progress));
		}
	}
	
	public void requestAllAchievements(OfflinePlayer player, DataCallback<Set<AchievementStore>> callback)
	{
		mTaskThread.addTask(new AchievementRetrieverTask(Mode.All, player, mWaiting), callback);
	}
	
	public void requestCompletedAchievements(OfflinePlayer player, DataCallback<Set<AchievementStore>> callback)
	{
		mTaskThread.addTask(new AchievementRetrieverTask(Mode.Completed, player, mWaiting), callback);
	}
	
	public void requestInProgressAchievements(OfflinePlayer player, DataCallback<Set<AchievementStore>> callback)
	{
		mTaskThread.addTask(new AchievementRetrieverTask(Mode.InProgress, player, mWaiting), callback);
	}
	
	public void requestStats( StatType type, TimePeriod period, int count, DataCallback<List<StatStore>> callback )
	{
		mTaskThread.addTask(new StatRetrieverTask(type, period, count, mWaiting), callback);
	}
	
	public void flush()
	{
		mTaskThread.addTask(new StoreTask(mWaiting), null);
	}
	
	public void shutdown()
	{
		mExit = true;
		flush();
		mTaskThread.setWriteOnlyMode(true);
		
		try
		{
			mStoreThread.interrupt();
			mTaskThread.waitForEmptyQueue();
			mStoreThread.interrupt();
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}
	}
	
	public void waitForUpdates()
	{
		flush();
		
		try
		{
			mTaskThread.waitForEmptyQueue();
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}
	}
	
	private class StoreThread extends Thread
	{
		private int mSaveInterval;
		
		public StoreThread(int interval)
		{
			super("MH Data Storer"); //$NON-NLS-1$
			start();
			mSaveInterval = interval;
		}
		
		@Override
		public void run()
		{
			try
			{
				while (true)
				{
					synchronized(this)
					{
						if(mExit)
							break;
					}
	
					mTaskThread.addTask(new StoreTask(mWaiting), null);
					
					Thread.sleep(mSaveInterval * 50);
				}
			}
			catch(InterruptedException e)
			{
				
			}
		}
	}
	
	private static class Task
	{
		public Task(DataStoreTask<?> task, DataCallback<?> callback)
		{
			this.task = task;
			this.callback = callback;
		}
		
		public DataStoreTask<?> task;
		
		public DataCallback<?> callback;
	}
	
	private static class CallbackCaller implements Runnable
	{
		private DataCallback<Object> mCallback;
		private Object mObj;
		private boolean mSuccess;
		
		public CallbackCaller(DataCallback<Object> callback, Object obj, boolean success)
		{
			mCallback = callback;
			mObj = obj;
			mSuccess = success;
		}
		
		@Override
		public void run()
		{
			if(mSuccess)
				mCallback.onCompleted(mObj);
			else
				mCallback.onError((Throwable)mObj);
		}
		
	}
	
	private class TaskThread extends Thread
	{
		private BlockingQueue<Task> mQueue;
		private boolean mWritesOnly = false;
		
		private Object mSignal = new Object();
		
		public TaskThread()
		{
			super("MH Data Retriever"); //$NON-NLS-1$
			
			mQueue = new LinkedBlockingQueue<Task>();
			
			start();
		}
		
		public void waitForEmptyQueue() throws InterruptedException
		{
			if(mQueue.isEmpty())
				return;
			
			synchronized ( mSignal )
			{
				mSignal.wait();
			}
		}

		public void setWriteOnlyMode(boolean writes)
		{
			mWritesOnly = writes;
		}
		
		public <T> void addTask(DataStoreTask<T> task, DataCallback<T> callback)
		{
			try
			{
				mQueue.put(new Task(task, callback));
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		@SuppressWarnings( "unchecked" )
		@Override
		public void run()
		{
			try
			{
				while(true)
				{
					if(mQueue.isEmpty())
					{
						synchronized(mSignal)
						{
							mSignal.notifyAll();
						}
					}
					
					Task task = mQueue.take();
					
					if(mWritesOnly && task.task.readOnly())
						continue;
					
					try
					{
						Object result = task.task.run(mStore);
						
						if(task.callback != null)
							Bukkit.getScheduler().runTask(MobHunting.instance, new CallbackCaller((DataCallback<Object>) task.callback, result, true));
					}
					catch(DataStoreException e)
					{
						if(task.callback != null)
							Bukkit.getScheduler().runTask(MobHunting.instance, new CallbackCaller((DataCallback<Object>) task.callback, e, false));
						else
							e.printStackTrace();
					}
				}
			}
			catch(InterruptedException e)
			{
				System.out.println("MH Data Retriever thread was interrupted"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Gets an offline player using the last known name.
	 * WARNING: This does a database lookup directly. This will block waiting for a reply
	 */
	public OfflinePlayer getPlayerByName( String name )
	{
		try
		{
			return mStore.getPlayerByName(name);
		}
		catch (UserNotFoundException e)
		{
			return null;
		}
		catch ( DataStoreException e )
		{
			e.printStackTrace();
			return null;
		}
	}

}	
