package au.com.mineauz.MobHunting.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.com.mineauz.MobHunting.MobHunting;

public class SQLiteDataStore extends DatabaseDataStore
{
	@Override
	protected Connection setupConnection() throws SQLException, DataStoreException
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			return DriverManager.getConnection("jdbc:sqlite:" + MobHunting.instance.getDataFolder().getPath() + "/" + MobHunting.config().databaseName + ".db");
		}
		catch(ClassNotFoundException e)
		{
			throw new DataStoreException("SQLite not present on the classpath");
		}
	}

	@Override
	protected void setupTables(Connection connection) throws SQLException
	{
		Statement create = connection.createStatement();
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Players (NAME TEXT PRIMARY KEY, PLAYER_ID INTEGER NOT NULL)");
		
		String[] names = getColumnNames();
		String dataString = "";
		for(String name : names)
			dataString += ", " + name + " INTEGER NOT NULL DEFAULT 0";
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Daily (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Weekly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Monthly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Yearly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS AllTime (PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID)" + dataString + ", PRIMARY KEY(PLAYER_ID))");
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Achievements (PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID) NOT NULL, ACHIEVEMENT TEXT NOT NULL, DATE INTEGER NOT NULL, PROGRESS INTEGER NOT NULL, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT), FOREIGN KEY(PLAYER_ID) REFERENCES Players(PLAYER_ID))");
		
		create.executeUpdate("create trigger if not exists DailyInsert after insert on Daily begin insert or ignore into Weekly(ID, PLAYER_ID) values(strftime(\"%Y%W\",\"now\"), NEW.PLAYER_ID); insert or ignore into Monthly(ID, PLAYER_ID) values(strftime(\"%Y%m\",\"now\"), NEW.PLAYER_ID); insert or ignore into Yearly(ID, PLAYER_ID) values(strftime(\"%Y\",\"now\"), NEW.PLAYER_ID); insert or ignore into AllTime(PLAYER_ID) values(NEW.PLAYER_ID); end");
		
		// Create the cascade update trigger. It will allow us to only modify the Daily table, and the rest will happen automatically
		StringBuilder updateStringBuilder = new StringBuilder();
		
		for(String name : names)
		{
			if(updateStringBuilder.length() != 0)
				updateStringBuilder.append(", ");
			
			updateStringBuilder.append(String.format("%s = (%1$s + (NEW.%1$s - OLD.%1$s)) ", name));
		}
		
		String updateString = updateStringBuilder.toString();
		
		StringBuilder updateTrigger = new StringBuilder();
		updateTrigger.append("create trigger if not exists DailyUpdate after update on Daily begin ");
		
		// Weekly
		updateTrigger.append("update Weekly set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y%W','now') AND PLAYER_ID=New.PLAYER_ID;");
		
		// Monthly
		updateTrigger.append(" update Monthly set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y%m','now') AND PLAYER_ID=New.PLAYER_ID;");
		
		// Yearly
		updateTrigger.append(" update Yearly set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where ID=strftime('%Y','now') AND PLAYER_ID=New.PLAYER_ID;");
		
		// AllTime
		updateTrigger.append("update AllTime set ");
		updateTrigger.append(updateString);
		updateTrigger.append(" where PLAYER_ID=New.PLAYER_ID;");
		
		updateTrigger.append("END");
		
		create.executeUpdate(updateTrigger.toString());
		create.close();
		
		connection.commit();
	}

	@Override
	protected void setupStatements(Connection connection) throws SQLException
	{
		mAddPlayerStatement = connection.prepareStatement("INSERT OR IGNORE INTO Players VALUES(?, (SELECT IFNULL(MAX(PLAYER_ID),0)+1 FROM Players));");
		mGetPlayerStatement[0] = connection.prepareStatement("SELECT * FROM Players WHERE NAME=?;");
		mGetPlayerStatement[1] = connection.prepareStatement("SELECT * FROM Players WHERE NAME IN (?,?);");
		mGetPlayerStatement[2] = connection.prepareStatement("SELECT * FROM Players WHERE NAME IN (?,?,?,?,?);");
		mGetPlayerStatement[3] = connection.prepareStatement("SELECT * FROM Players WHERE NAME IN (?,?,?,?,?,?,?,?,?,?);");
		
		mRecordAchievementStatement = connection.prepareStatement("INSERT OR REPLACE INTO Achievements VALUES(?,?,?,?);");
		
		mAddPlayerStatsStatement = connection.prepareStatement("INSERT OR IGNORE INTO Daily(ID, PLAYER_ID) VALUES(strftime(\"%Y%j\",\"now\"),?);");
		
		mLoadAchievementsStatement = connection.prepareStatement("SELECT ACHIEVEMENT, DATE, PROGRESS FROM Achievements WHERE PLAYER_ID = ?;");
	}

	@Override
	protected void increaseStat( String statName, int playerId ) throws SQLException
	{
		Statement statement = mConnection.createStatement();
		statement.executeUpdate(String.format("UPDATE Daily SET %1$s = %1$s + 1 WHERE ID = strftime(\"%%Y%%j\",\"now\") AND PLAYER_ID = %2$d;", statName, playerId));
		statement.close();
	}
	
	@Override
	public void saveStats( Set<StatStore> stats ) throws DataStoreException
	{
		try
		{
			Statement statement = mConnection.createStatement();
			
			HashSet<String> names = new HashSet<String>();
			for(StatStore stat : stats)
				names.add(stat.playerName);
			
			Map<String, Integer> ids = getPlayerIds(names);
			
			// Make sure the stats are available for each player
			mAddPlayerStatsStatement.clearBatch();
			for(String name : names)
			{
				mAddPlayerStatsStatement.setInt(1, ids.get(name));
				mAddPlayerStatsStatement.addBatch();
			}

			mAddPlayerStatsStatement.executeBatch();
			
			// Now add each of the stats
			for(StatStore stat : stats)
				statement.addBatch(String.format("UPDATE Daily SET %1$s = %1$s + 1 WHERE ID = strftime(\"%%Y%%j\",\"now\") AND PLAYER_ID = %2$d;", stat.statName, ids.get(stat.playerName)));

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
}
