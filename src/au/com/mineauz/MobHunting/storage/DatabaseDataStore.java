package au.com.mineauz.MobHunting.storage;

import java.sql.*;
import java.util.Map;

import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.achievements.Achievement;
import au.com.mineauz.MobHunting.achievements.ProgressAchievement;

public abstract class DatabaseDataStore implements DataStore
{
	private Connection mConnection;
	
	protected PreparedStatement mRecordKillStatement;
	protected PreparedStatement mRecordAssistStatement;
	protected PreparedStatement mRecordAchievementStatement;
	protected PreparedStatement mRecordProgressStatement;
	
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

	@Override
	public void recordKill( Player player, ExtendedMobType type ) throws DataStoreException
	{
		
	}

	@Override
	public void recordAssist( Player player, Player killer, ExtendedMobType type ) throws DataStoreException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void recordAchievement( Player player, Achievement achievement ) throws DataStoreException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void recordAchievementProgress( Player player, ProgressAchievement achievement, int progress ) throws DataStoreException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Integer> loadAchievements( Player player ) throws DataStoreException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
