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

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;

public class MySQLDataStore extends DatabaseDataStore
{

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
				statement.addBatch(String.format("UPDATE Daily SET %1$s = %1$s + 1 WHERE ID = DATE_FORMAT(NOW(), '%%Y%%j') AND PLAYER_ID = %2$d;", stat.type.getDBColumn(), ids.get(stat.playerName)));

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
	protected Connection setupConnection() throws SQLException, DataStoreException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://" + MobHunting.config().databaseHost + "/" + MobHunting.config().databaseName, MobHunting.config().databaseUsername, MobHunting.config().databasePassword);
		}
		catch(ClassNotFoundException e)
		{
			throw new DataStoreException("MySQL not present on the classpath");
		}
	}

	@Override
	protected void setupTables( Connection connection ) throws SQLException
	{
		Statement create = connection.createStatement();
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Players (NAME CHAR(20) PRIMARY KEY, PLAYER_ID INTEGER NOT NULL AUTO_INCREMENT, KEY PLAYER_ID (PLAYER_ID))");
		
		String dataString = "";
		for(StatType type : StatType.values())
			dataString += ", " + type.getDBColumn() + " INTEGER NOT NULL DEFAULT 0";
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Daily (ID CHAR(7) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID) ON DELETE CASCADE" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Weekly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID) ON DELETE CASCADE" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Monthly (ID CHAR(6) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID) ON DELETE CASCADE" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Yearly (ID CHAR(4) NOT NULL, PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID) ON DELETE CASCADE" + dataString + ", PRIMARY KEY(ID, PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS AllTime (PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID) ON DELETE CASCADE" + dataString + ", PRIMARY KEY(PLAYER_ID))");
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Achievements (PLAYER_ID INTEGER REFERENCES Players(PLAYER_ID) ON DELETE CASCADE, ACHIEVEMENT VARCHAR(64) NOT NULL, DATE DATETIME NOT NULL, PROGRESS INTEGER NOT NULL, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT))");
		
		
		// Workaround for no create trigger if not exists
		ResultSet set = create.executeQuery(String.format("select TRIGGER_NAME from information_schema.triggers where TRIGGER_SCHEMA = '%s' and TRIGGER_NAME = '%s'", MobHunting.config().databaseName, "DailyInsert"));
		if(!set.next())
			create.executeUpdate("create trigger DailyInsert after insert on Daily for each row begin insert ignore into Weekly(ID, PLAYER_ID) values(DATE_FORMAT(NOW(), '%Y%U'), NEW.PLAYER_ID); insert ignore into Monthly(ID, PLAYER_ID) values(DATE_FORMAT(NOW(), '%Y%c'), NEW.PLAYER_ID); insert ignore into Yearly(ID, PLAYER_ID) values(DATE_FORMAT(NOW(), '%Y'), NEW.PLAYER_ID); insert ignore into AllTime(PLAYER_ID) values(NEW.PLAYER_ID); end");
		
		
		set = create.executeQuery(String.format("select TRIGGER_NAME from information_schema.triggers where TRIGGER_SCHEMA = '%s' and TRIGGER_NAME = '%s'", MobHunting.config().databaseName, "DailyUpdate"));
		if(!set.next())
		{
			// Create the cascade update trigger. It will allow us to only modify the Daily table, and the rest will happen automatically
			StringBuilder updateStringBuilder = new StringBuilder();
			
			for(StatType type : StatType.values())
			{
				if(updateStringBuilder.length() != 0)
					updateStringBuilder.append(", ");
				
				updateStringBuilder.append(String.format("%s = (%1$s + (NEW.%1$s - OLD.%1$s)) ", type.getDBColumn()));
			}
			
			String updateString = updateStringBuilder.toString();
			
			StringBuilder updateTrigger = new StringBuilder();
			updateTrigger.append("create trigger DailyUpdate after update on Daily for each row begin ");
			
			// Weekly
			updateTrigger.append("update Weekly set ");
			updateTrigger.append(updateString);
			updateTrigger.append(" where ID=DATE_FORMAT(NOW(), '%Y%U') AND PLAYER_ID=New.PLAYER_ID;");
			
			// Monthly
			updateTrigger.append(" update Monthly set ");
			updateTrigger.append(updateString);
			updateTrigger.append(" where ID=DATE_FORMAT(NOW(), '%Y%c') AND PLAYER_ID=New.PLAYER_ID;");
			
			// Yearly
			updateTrigger.append(" update Yearly set ");
			updateTrigger.append(updateString);
			updateTrigger.append(" where ID=DATE_FORMAT(NOW(), '%Y') AND PLAYER_ID=New.PLAYER_ID;");
			
			// AllTime
			updateTrigger.append("update AllTime set ");
			updateTrigger.append(updateString);
			updateTrigger.append(" where PLAYER_ID=New.PLAYER_ID;");
			
			updateTrigger.append("END");
			
			create.executeUpdate(updateTrigger.toString());
		}
		
		create.close();
		
		connection.commit();
	}

	@Override
	protected void setupStatements( Connection connection ) throws SQLException
	{
		mAddPlayerStatement = connection.prepareStatement("INSERT IGNORE INTO Players(NAME) VALUES(?);");
		mGetPlayerStatement[0] = connection.prepareStatement("SELECT * FROM Players WHERE NAME=?;");
		mGetPlayerStatement[1] = connection.prepareStatement("SELECT * FROM Players WHERE NAME IN (?,?);");
		mGetPlayerStatement[2] = connection.prepareStatement("SELECT * FROM Players WHERE NAME IN (?,?,?,?,?);");
		mGetPlayerStatement[3] = connection.prepareStatement("SELECT * FROM Players WHERE NAME IN (?,?,?,?,?,?,?,?,?,?);");
		
		mRecordAchievementStatement = connection.prepareStatement("REPLACE INTO Achievements VALUES(?,?,?,?);");
		
		mAddPlayerStatsStatement = connection.prepareStatement("INSERT IGNORE INTO Daily(ID, PLAYER_ID) VALUES(DATE_FORMAT(NOW(), '%Y%j'),?);");
		
		mLoadAchievementsStatement = connection.prepareStatement("SELECT ACHIEVEMENT, DATE, PROGRESS FROM Achievements WHERE PLAYER_ID = ?;");
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
				id = "DATE_FORMAT(NOW(), '%Y%j')";
				break;
			case Week:
				id = "DATE_FORMAT(NOW(), '%Y%U')";
				break;
			case Month:
				id = "DATE_FORMAT(NOW(), '%Y%c')";
				break;
			case Year:
				id = "DATE_FORMAT(NOW(), '%Y')";
				break;
			default:
				id = null;
				break;
			}
			
			Statement statement = mConnection.createStatement();
			ResultSet results = statement.executeQuery("SELECT " + type.getDBColumn() + ", Players.NAME from " + period.getTable() + " inner join Players on Players.PLAYER_ID=" + period.getTable() + ".PLAYER_ID" + (id != null ? " where ID=" + id : "") + " order by " + type.getDBColumn() + " asc limit " + count);
			ArrayList<StatStore> list = new ArrayList<StatStore>();
			
			while(results.next())
				list.add(new StatStore(type, results.getString(2), results.getInt(1)));
			
			return list;
		}
		catch(SQLException e)
		{
			throw new DataStoreException(e);
		}
	}
}
