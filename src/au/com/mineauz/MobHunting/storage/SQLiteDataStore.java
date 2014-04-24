package au.com.mineauz.MobHunting.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;

public class SQLiteDataStore extends DatabaseDataStore
{
	@Override
	protected Connection setupConnection() throws SQLException, DataStoreException
	{
		try
		{
			Class.forName("org.sqlite.JDBC"); //$NON-NLS-1$
			return DriverManager.getConnection("jdbc:sqlite:" + MobHunting.instance.getDataFolder().getPath() + "/" + MobHunting.config().databaseName + ".db"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		catch(ClassNotFoundException e)
		{
			throw new DataStoreException("SQLite not present on the classpath"); //$NON-NLS-1$
		}
	}

	@Override
	protected void setupTables(Connection connection) throws SQLException
	{
		Statement create = connection.createStatement();
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Players (UUID TEXT PRIMARY KEY, NAME TEXT, PLAYER_ID INTEGER NOT NULL)"); //$NON-NLS-1$
		
		String dataString = ""; //$NON-NLS-1$
		for(StatType type : StatType.values())
			dataString += ", " + type.getDBColumn() + " INTEGER NOT NULL DEFAULT 0"; //$NON-NLS-1$ //$NON-NLS-2$
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Daily (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Weekly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Monthly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Yearly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		create.executeUpdate("CREATE TABLE IF NOT EXISTS AllTime (PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(PLAYER_ID))"); //$NON-NLS-1$ //$NON-NLS-2$
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Achievements (PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID) NOT NULL, ACHIEVEMENT TEXT NOT NULL, DATE INTEGER NOT NULL, PROGRESS INTEGER NOT NULL, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT), FOREIGN KEY(PLAYER_ID) REFERENCES Players(PLAYER_ID))"); //$NON-NLS-1$
		
		create.executeUpdate("create trigger if not exists DailyInsert after insert on Daily begin insert or ignore into Weekly(ID, PLAYER_ID) values(strftime(\"%Y%W\",\"now\"), NEW.PLAYER_ID); insert or ignore into Monthly(ID, PLAYER_ID) values(strftime(\"%Y%m\",\"now\"), NEW.PLAYER_ID); insert or ignore into Yearly(ID, PLAYER_ID) values(strftime(\"%Y\",\"now\"), NEW.PLAYER_ID); insert or ignore into AllTime(PLAYER_ID) values(NEW.PLAYER_ID); end"); //$NON-NLS-1$
		
		// Create the cascade update trigger. It will allow us to only modify the Daily table, and the rest will happen automatically
		StringBuilder updateStringBuilder = new StringBuilder();
		
		for(StatType type : StatType.values())
		{
			if(updateStringBuilder.length() != 0)
				updateStringBuilder.append(", "); //$NON-NLS-1$
			
			updateStringBuilder.append(String.format("%s = (%1$s + (NEW.%1$s - OLD.%1$s)) ", type.getDBColumn())); //$NON-NLS-1$
		}
		
		String updateString = updateStringBuilder.toString();
		
		StringBuilder updateTrigger = new StringBuilder();
		updateTrigger.append("create trigger if not exists DailyUpdate after update on Daily begin "); //$NON-NLS-1$
		
		// Weekly
		updateTrigger.append("update Weekly set "); //$NON-NLS-1$
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y%W','now') AND PLAYER_ID=New.PLAYER_ID;"); //$NON-NLS-1$
		
		// Monthly
		updateTrigger.append(" update Monthly set "); //$NON-NLS-1$
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y%m','now') AND PLAYER_ID=New.PLAYER_ID;"); //$NON-NLS-1$
		
		// Yearly
		updateTrigger.append(" update Yearly set "); //$NON-NLS-1$
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y','now') AND PLAYER_ID=New.PLAYER_ID;"); //$NON-NLS-1$
		
		// AllTime
		updateTrigger.append("update AllTime set "); //$NON-NLS-1$
		updateTrigger.append(updateString);
		updateTrigger.append(" where PLAYER_ID=New.PLAYER_ID;"); //$NON-NLS-1$
		
		updateTrigger.append("END"); //$NON-NLS-1$
		
		create.executeUpdate(updateTrigger.toString());
		create.close();
		
		connection.commit();
	}

	@Override
	protected void setupStatements(Connection connection) throws SQLException
	{
		mAddPlayerStatement = connection.prepareStatement("INSERT OR IGNORE INTO Players VALUES(?, ?, (SELECT IFNULL(MAX(PLAYER_ID),0)+1 FROM Players));"); //$NON-NLS-1$
		mGetPlayerStatement[0] = connection.prepareStatement("SELECT * FROM Players WHERE UUID=?;"); //$NON-NLS-1$
		mGetPlayerStatement[1] = connection.prepareStatement("SELECT * FROM Players WHERE UUID IN (?,?);"); //$NON-NLS-1$
		mGetPlayerStatement[2] = connection.prepareStatement("SELECT * FROM Players WHERE UUID IN (?,?,?,?,?);"); //$NON-NLS-1$
		mGetPlayerStatement[3] = connection.prepareStatement("SELECT * FROM Players WHERE UUID IN (?,?,?,?,?,?,?,?,?,?);"); //$NON-NLS-1$
		
		mRecordAchievementStatement = connection.prepareStatement("INSERT OR REPLACE INTO Achievements VALUES(?,?,?,?);"); //$NON-NLS-1$
		
		mAddPlayerStatsStatement = connection.prepareStatement("INSERT OR IGNORE INTO Daily(ID, PLAYER_ID) VALUES(strftime(\"%Y%j\",\"now\"),?);"); //$NON-NLS-1$
		
		mLoadAchievementsStatement = connection.prepareStatement("SELECT ACHIEVEMENT, DATE, PROGRESS FROM Achievements WHERE PLAYER_ID = ?;"); //$NON-NLS-1$
	}

	@Override
	public void saveStats( Set<StatStore> stats ) throws DataStoreException
	{
		try
		{
			Statement statement = mConnection.createStatement();
			
			HashSet<OfflinePlayer> names = new HashSet<OfflinePlayer>();
			for(StatStore stat : stats)
				names.add(stat.player);
			
			Map<UUID, Integer> ids = getPlayerIds(names);
			
			// Make sure the stats are available for each player
			mAddPlayerStatsStatement.clearBatch();
			for(OfflinePlayer player : names)
			{
				mAddPlayerStatsStatement.setInt(1, ids.get(player.getUniqueId()));
				mAddPlayerStatsStatement.addBatch();
			}

			mAddPlayerStatsStatement.executeBatch();
			
			// Now add each of the stats
			for(StatStore stat : stats)
				statement.addBatch(String.format("UPDATE Daily SET %1$s = %1$s + 1 WHERE ID = strftime(\"%%Y%%j\",\"now\") AND PLAYER_ID = %2$d;", stat.type.getDBColumn(), ids.get(stat.player.getUniqueId()))); //$NON-NLS-1$

			statement.executeBatch();
			
			statement.close();
			
			mConnection.commit();
		}
		catch(SQLException e)
		{
			rollback();
			throw new DataStoreException(e);
		}
	}
	
	@Override
	public List<StatStore> loadStats( StatType type, TimePeriod period, int count ) throws DataStoreException
	{
		try
		{
			String id;
			switch(period)
			{
			case Day:
				id = "strftime('%Y%j','now')"; //$NON-NLS-1$
				break;
			case Week:
				id = "strftime('%Y%W','now')"; //$NON-NLS-1$
				break;
			case Month:
				id = "strftime('%Y%m','now')"; //$NON-NLS-1$
				break;
			case Year:
				id = "strftime('%Y','now')"; //$NON-NLS-1$
				break;
			default:
				id = null;
				break;
			}
			
			Statement statement = mConnection.createStatement();
			ResultSet results = statement.executeQuery("SELECT " + type.getDBColumn() + ", Players.UUID from " + period.getTable() + " inner join Players using (PLAYER_ID)" + (id != null ? " where ID=" + id : "") + " order by " + type.getDBColumn() + " desc limit " + count); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$
			ArrayList<StatStore> list = new ArrayList<StatStore>();
			
			while(results.next())
				list.add(new StatStore(type, Bukkit.getOfflinePlayer(UUID.fromString(results.getString(2))), results.getInt(1)));
			
			return list;
		}
		catch(SQLException e)
		{
			throw new DataStoreException(e);
		}
	}
}
