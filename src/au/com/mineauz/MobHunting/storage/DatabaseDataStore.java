package au.com.mineauz.MobHunting.storage;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.achievements.Achievement;
import au.com.mineauz.MobHunting.achievements.ProgressAchievement;

public abstract class DatabaseDataStore implements DataStore
{
	protected Connection mConnection;
	
	/**
	 * Args: player id
	 */
	protected PreparedStatement mAddPlayerStatsStatement;

	/**
	 * Args: player id, achievement, date, progress
	 */
	protected PreparedStatement mRecordAchievementStatement;

	/**
	 * Args: player name
	 */
	protected PreparedStatement mAddPlayerStatement;
	/**
	 * Args: player name
	 */
	protected PreparedStatement mGetPlayerStatement;
	
	/**
	 * Args: player id
	 */
	protected PreparedStatement mLoadAchievementsStatement;
	
	@Override
	public void initialize() throws DataStoreException
	{
		try
		{
			mConnection = setupConnection();
			mConnection.setAutoCommit(false);
			
			setupTables(mConnection);
			
			setupStatements(mConnection);
		}
		catch(SQLException e)
		{
			throw new DataStoreException(e);
		}
	}
	
	protected abstract Connection setupConnection() throws SQLException, DataStoreException;
	protected abstract void setupTables(Connection connection) throws SQLException;
	protected abstract void setupStatements(Connection connection) throws SQLException;
	
	protected void rollback() throws DataStoreException
	{
		try
		{
			mConnection.rollback();
		}
		catch(SQLException e)
		{
			throw new DataStoreException(e);
		}
	}

	@Override
	public void shutdown() throws DataStoreException
	{
		try
		{
			mConnection.close();
		}
		catch ( SQLException e )
		{
			throw new DataStoreException(e);
		}
	}
	
	private int getOrAddPlayerId(Player player) throws SQLException, DataStoreException
	{
		mGetPlayerStatement.setString(1, player.getName());
		
		ResultSet set = mGetPlayerStatement.executeQuery();
		if(set.next())
			return set.getInt(1);
		
		mAddPlayerStatement.setString(1, player.getName());
		mAddPlayerStatement.executeUpdate();
		
		set = mGetPlayerStatement.executeQuery();
		if(set.next())
			return set.getInt(1);
		
		throw new DataStoreException("Somehow player add failed");
	}
	
	private int getPlayerId(Player player) throws SQLException, DataStoreException
	{
		mGetPlayerStatement.setString(1, player.getName());
		
		ResultSet set = mGetPlayerStatement.executeQuery();
		if(set.next())
			return set.getInt(1);
		
		throw new DataStoreException("No data for " + player.getName());
	}
	
	protected String[] getColumnNames()
	{
		String[] names = new String[ExtendedMobType.values().length * 2 + 2];
		for(int i = 0; i < ExtendedMobType.values().length; ++i)
			names[i] = ExtendedMobType.values()[i].name() + "_kill";
		
		for(int i = 0; i < ExtendedMobType.values().length; ++i)
			names[i + ExtendedMobType.values().length] = ExtendedMobType.values()[i].name() + "_assist";
		
		names[ExtendedMobType.values().length * 2] = "total_kill";
		names[ExtendedMobType.values().length * 2 + 1] = "total_assist";
		
		return names;
	}
	
	protected abstract void increaseStat(String statName, int playerId) throws SQLException;

	@Override
	public void recordKill( Player player, ExtendedMobType type, boolean bonusMob ) throws DataStoreException
	{
		try
		{
			int playerId = getOrAddPlayerId(player);
			
			mAddPlayerStatsStatement.setInt(1, playerId);
			mAddPlayerStatsStatement.executeUpdate();
			
			increaseStat(type.name() + "_kill", playerId);
			increaseStat("total_kill", playerId);
			
			if(bonusMob)
				increaseStat("BonusMob_kill", playerId);
			
			mConnection.commit();
		}
		catch(SQLException e)
		{
			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	public void recordAssist( Player player, Player killer, ExtendedMobType type, boolean bonusMob ) throws DataStoreException
	{
		try
		{
			int playerId = getOrAddPlayerId(player);
			
			mAddPlayerStatsStatement.setInt(1, playerId);
			mAddPlayerStatsStatement.executeUpdate();
			
			increaseStat(type.name() + "_assist", playerId);
			increaseStat("total_assist", playerId);
			
			if(bonusMob)
				increaseStat("BonusMob_assist", playerId);
			
			mConnection.commit();
		}
		catch(SQLException e)
		{
			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	public void recordAchievement( Player player, Achievement achievement ) throws DataStoreException
	{
		try
		{
			int playerId = getOrAddPlayerId(player);
			
			mRecordAchievementStatement.setInt(1, playerId);
			mRecordAchievementStatement.setString(2, achievement.getID());
			mRecordAchievementStatement.setDate(3, new Date(System.currentTimeMillis()));
			mRecordAchievementStatement.setInt(4, -1);
			
			mRecordAchievementStatement.executeUpdate();
			
			mConnection.commit();
		}
		catch(SQLException e)
		{
			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	public void recordAchievementProgress( Player player, ProgressAchievement achievement, int progress ) throws DataStoreException
	{
		try
		{
			int playerId = getOrAddPlayerId(player);
			
			mRecordAchievementStatement.setInt(1, playerId);
			mRecordAchievementStatement.setString(2, achievement.getID());
			mRecordAchievementStatement.setDate(3, new Date(System.currentTimeMillis()));
			mRecordAchievementStatement.setInt(4, progress);
			
			mRecordAchievementStatement.executeUpdate();
			
			mConnection.commit();
		}
		catch(SQLException e)
		{
			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	public Set<AchievementRecord> loadAchievements( Player player ) throws DataStoreException
	{
		try
		{
			int playerId = getPlayerId(player);
			
			mLoadAchievementsStatement.setInt(1, playerId);
			
			ResultSet set = mLoadAchievementsStatement.executeQuery();
			HashSet<AchievementRecord> achievements = new HashSet<AchievementRecord>();
			
			while(set.next())
			{
				AchievementRecord record = new AchievementRecord();
				record.id = set.getString(1);
				record.date = set.getLong(2);
				record.progress = set.getInt(3);
				achievements.add(record);
			}
			
			return achievements;
		}
		catch(SQLException e)
		{
			throw new DataStoreException(e);
		}
	}
}
