package au.com.mineauz.MobHunting.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
		
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Players (PLAYER_ID INTEGER PRIMARY KEY ASC AUTOINCREMENT, NAME TEXT)");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Achievements (PLAYER_ID INTEGER, ACHIEVEMENT TEXT, DATE INTEGER, PROGRESS INTEGER, PRIMARY KEY(PLAYER_ID, ACHIEVEMENT), FOREIGN KEY(PLAYER_ID) REFERENCES Players(PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Kills (ID INTEGER PRIMARY KEY ASC AUTOINCREMENT, DATE INTEGER, PLAYER_ID INTEGER, MOB_TYPE TEXT, BONUS INTEGER, FOREIGN KEY(PLAYER_ID) REFERENCES Players(PLAYER_ID))");
		create.executeUpdate("CREATE TABLE IF NOT EXISTS Assists (ID INTEGER PRIMARY KEY ASC AUTOINCREMENT, DATE INTEGER, PLAYER_ID INTEGER, MOB_TYPE TEXT, BONUS INTEGER, ASSISTED_ID INTEGER, FOREIGN KEY(PLAYER_ID) REFERENCES Players(PLAYER_ID), FOREIGN KEY(ASSISTED_ID) REFERENCES Players(PLAYER_ID))");
		
		create.close();
		
		connection.commit();
	}

	@Override
	protected void setupStatements(Connection connection) throws SQLException
	{
		mAddPlayerStatement = connection.prepareStatement("INSERT INTO Players(NAME) VALUES(?);");
		mGetPlayerStatement = connection.prepareStatement("SELECT PLAYER_ID FROM Players WHERE NAME=?;");
		
		mRecordAchievementStatement = connection.prepareStatement("INSERT OR REPLACE INTO Achievements VALUES(?,?,?,?);");
		mRecordKillStatement = connection.prepareStatement("INSERT INTO Kills(DATE, PLAYER_ID, MOB_TYPE, BONUS) VALUES(?,?,?,?);");
		mRecordAssistStatement = connection.prepareStatement("INSERT INTO Assists(DATE, PLAYER_ID, MOB_TYPE, BONUS, ASSISTED_ID) VALUES(?,?,?,?,?);");
		
		mLoadAchievementsStatement = connection.prepareStatement("SELECT ACHIEVEMENT, DATE, PROGRESS FROM Achievements WHERE PLAYER_ID = ?;");
	}

}
