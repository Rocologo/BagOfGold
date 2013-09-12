package au.com.mineauz.MobHunting.leaderboard;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class LeaderboardManager
{
	private Set<Leaderboard> mLeaderboards = new HashSet<Leaderboard>();
	private BukkitTask mUpdater = null;
	private int mUpdaterInterval = 200; // TODO: Make Configurable
	
	public void initialize()
	{
		mUpdater = Bukkit.getScheduler().runTaskTimer(MobHunting.instance, new Updater(), 1L, mUpdaterInterval);
	}
	
	public void shutdown()
	{
		mUpdater.cancel();
	}
	
	public void createLeaderboard(StatType type, TimePeriod period, Location pointA, Location pointB, boolean horizontal) throws IllegalArgumentException
	{
		Leaderboard board = new Leaderboard(type, period, pointA, pointB, horizontal);
		mLeaderboards.add(board);
		board.updateBoard();
	}
	
	private class Updater implements Runnable
	{
		@Override
		public void run()
		{
			for(Leaderboard board : mLeaderboards)
				board.updateBoard();
		}
	}
}
