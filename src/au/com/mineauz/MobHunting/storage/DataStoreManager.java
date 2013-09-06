package au.com.mineauz.MobHunting.storage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.achievements.Achievement;
import au.com.mineauz.MobHunting.achievements.ProgressAchievement;

public class DataStoreManager
{
	// Accessed on multiple threads
	private HashSet<Object> mWaiting = new HashSet<Object>();
	
	// Accessed only from these threads
	private DataStore mStore;
	private boolean mExit = false;
	
	// Accessed only from store thread
	private boolean mFlush = false;
	
	private StoreThread mStoreThread;
	
	// Accessed only from retreive thread
	private RetrieveThread mRetrieveThread;
	
	
	public DataStoreManager(DataStore store)
	{
		mStore = store;
		
		mStoreThread = new StoreThread(MobHunting.config().savePeriod);
		mRetrieveThread = new RetrieveThread();
	}
	
	public void recordKill(Player player, ExtendedMobType type, boolean bonusMob)
	{
		synchronized(mWaiting)
		{
			mWaiting.add(new StatStore(type.getName() + "_kill", player.getName()));
			mWaiting.add(new StatStore("total_kill", player.getName()));
			
			if(bonusMob)
				mWaiting.add(new StatStore("BonusMob_kill", player.getName()));
		}
	}
	
	public void recordAssist(Player player, Player killer, ExtendedMobType type, boolean bonusMob)
	{
		synchronized(mWaiting)
		{
			mWaiting.add(new StatStore(type.getName() + "_assist", player.getName()));
			mWaiting.add(new StatStore("total_assist", player.getName()));
			
			if(bonusMob)
				mWaiting.add(new StatStore("BonusMob_assist", player.getName()));
		}
	}
	
	public void recordAchievement(Player player, Achievement achievement)
	{
		synchronized(mWaiting)
		{
			mWaiting.add(new AchievementStore(achievement.getID(), player.getName(), -1));
		}
	}
	
	public void recordAchievementProgress(Player player, ProgressAchievement achievement, int progress)
	{
		synchronized(mWaiting)
		{
			mWaiting.add(new AchievementStore(achievement.getID(), player.getName(), progress));
		}
	}
	
	public void requestAllAchievements(OfflinePlayer player, DataCallback<Set<AchievementStore>> callback)
	{
		mRetrieveThread.addTask(new RetrieveTask(RetrieveTask.ACHIEVEMENT_ALL, player.getName(), callback));
	}
	
	public void requestCompletedAchievements(OfflinePlayer player, DataCallback<Set<AchievementStore>> callback)
	{
		mRetrieveThread.addTask(new RetrieveTask(RetrieveTask.ACHIEVEMENT_COMPLETED, player.getName(), callback));
	}
	
	public void requestInProgressAchievements(OfflinePlayer player, DataCallback<Set<AchievementStore>> callback)
	{
		mRetrieveThread.addTask(new RetrieveTask(RetrieveTask.ACHIEVEMENT_PROGRESS, player.getName(), callback));
	}
	
	public void flush()
	{
		synchronized(this)
		{
			mFlush = true;
			mStoreThread.interrupt();
		}
	}
	
	public void shutdown()
	{
		synchronized(this)
		{
			flush();
			mExit = true;
		}
		
		try
		{
			mStoreThread.join();
			mRetrieveThread.interrupt();
		}
		catch ( InterruptedException e )
		{
			e.printStackTrace();
		}
	}
	
	private class StoreThread extends Thread
	{
		private HashSet<StatStore> mWaitingStats = new HashSet<StatStore>();
		private HashSet<AchievementStore> mWaitingAchievements = new HashSet<AchievementStore>();
		private int mSaveInterval;
		
		public StoreThread(int interval)
		{
			super("MH Data Storer");
			start();
			mSaveInterval = interval;
		}
		
		private void queueWaiting()
		{
			synchronized(mWaiting)
			{
				mWaitingStats.clear();
				mWaitingAchievements.clear();
				
				for(Object obj : mWaiting)
				{
					if(obj instanceof StatStore)
						mWaitingStats.add((StatStore)obj);
					if(obj instanceof AchievementStore)
						mWaitingAchievements.add((AchievementStore)obj);
				}
				
				mWaiting.clear();
			}
		}
		
		@Override
		public void run()
		{
			while (true)
			{
				synchronized(this)
				{
					if(mExit)
						break;
				}

				
				queueWaiting();
				
				try
				{
					if(!mWaitingStats.isEmpty())
						mStore.saveStats(mWaitingStats);
					
					if(!mWaitingAchievements.isEmpty())
						mStore.saveAchievements(mWaitingAchievements);
					
					Thread.sleep(mSaveInterval * 50);
				}
				catch(DataStoreException e)
				{
					e.printStackTrace();
				}
				catch(InterruptedException e)
				{
					synchronized(this)
					{
						if(mFlush)
						{
							mFlush = false;
							continue;
						}
						else
							break;
					}
				}
			}
		}
	}
	
	private static class RetrieveTask
	{
		public static final int ACHIEVEMENT_ALL = 0;
		public static final int ACHIEVEMENT_COMPLETED = 1;
		public static final int ACHIEVEMENT_PROGRESS = 2;
		
		@SuppressWarnings( "unused" )
		public RetrieveTask(int type, DataCallback<?> callback)
		{
			this.type = type;
			this.callback = callback;
		}
		
		public RetrieveTask(int type, String player, DataCallback<?> callback)
		{
			this.type = type;
			this.callback = callback;
			playerName = player;
		}
		
		public int type;
		
		public DataCallback<?> callback;
		
		public String playerName;
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
	
	private class RetrieveThread extends Thread
	{
		private BlockingQueue<RetrieveTask> mQueue;
		
		RetrieveThread()
		{
			super("MH Data Retriever");
			
			mQueue = new LinkedBlockingQueue<RetrieveTask>();
			
			start();
		}
		
		public void addTask(RetrieveTask task)
		{
			try
			{
				mQueue.put(task);
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
					RetrieveTask task = mQueue.take();
					
					switch(task.type)
					{
					case RetrieveTask.ACHIEVEMENT_ALL:
					{
						try
						{
							Set<AchievementStore> achievements = mStore.loadAchievements(task.playerName);
							Bukkit.getScheduler().runTask(MobHunting.instance, new CallbackCaller((DataCallback<Object>) task.callback, achievements, true));
						}
						catch(DataStoreException e)
						{
							Bukkit.getScheduler().runTask(MobHunting.instance, new CallbackCaller((DataCallback<Object>) task.callback, e, false));
						}
						
						break;
					}
					case RetrieveTask.ACHIEVEMENT_COMPLETED:
					{
						try
						{
							Set<AchievementStore> achievements = mStore.loadAchievements(task.playerName);
							
							Iterator<AchievementStore> it = achievements.iterator();
							while(it.hasNext())
							{
								AchievementStore achievement = it.next();
								if(achievement.progress != -1)
									it.remove();
							}
							
							Bukkit.getScheduler().runTask(MobHunting.instance, new CallbackCaller((DataCallback<Object>) task.callback, achievements, true));
						}
						catch(DataStoreException e)
						{
							Bukkit.getScheduler().runTask(MobHunting.instance, new CallbackCaller((DataCallback<Object>) task.callback, e, false));
						}
						
						break;
					}
					case RetrieveTask.ACHIEVEMENT_PROGRESS:
					{
						try
						{
							Set<AchievementStore> achievements = mStore.loadAchievements(task.playerName);
							
							Iterator<AchievementStore> it = achievements.iterator();
							while(it.hasNext())
							{
								AchievementStore achievement = it.next();
								if(achievement.progress == -1)
									it.remove();
							}
							
							Bukkit.getScheduler().runTask(MobHunting.instance, new CallbackCaller((DataCallback<Object>) task.callback, achievements, true));
						}
						catch(DataStoreException e)
						{
							Bukkit.getScheduler().runTask(MobHunting.instance, new CallbackCaller((DataCallback<Object>) task.callback, e, false));
						}
						
						break;
					}
					}
					
				}
			}
			catch(InterruptedException e)
			{
				System.out.println("MH Data Retriever thread was interrupted");
			}
		}
	}
}	
