package one.lindegaard.BagOfGold.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import one.lindegaard.BagOfGold.BagOfGold;

public class MySQLDataStore extends DatabaseDataStore {

	private BagOfGold plugin;

	public MySQLDataStore(BagOfGold plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	// *******************************************************************************
	// SETUP / INITIALIZE
	// *******************************************************************************

	@Override
	protected Connection setupConnection() throws DataStoreException {
		try {
			Locale.setDefault(new Locale("us", "US"));
			Class.forName("com.mysql.jdbc.Driver");
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setUser(plugin.getConfigManager().databaseUsername);
			dataSource.setPassword(plugin.getConfigManager().databasePassword);
			if (plugin.getConfigManager().databaseHost.contains(":")) {
				dataSource.setServerName(plugin.getConfigManager().databaseHost.split(":")[0]);
				dataSource.setPort(Integer.valueOf(plugin.getConfigManager().databaseHost.split(":")[1]));
			} else {
				dataSource.setServerName(plugin.getConfigManager().databaseHost);
			}
			dataSource.setDatabaseName(plugin.getConfigManager().databaseName + "?autoReconnect=true");
			Connection c = dataSource.getConnection();
			Statement statement = c.createStatement();
			statement.executeUpdate("SET NAMES 'utf8'");
			statement.executeUpdate("SET CHARACTER SET 'utf8'");
			statement.close();
			c.setAutoCommit(false);
			return c;
		} catch (ClassNotFoundException classNotFoundEx) {
			throw new DataStoreException("MySQL not present on the classpath", classNotFoundEx);
		} catch (SQLException sqlEx) {
			throw new DataStoreException("Error creating sql connection", sqlEx);
		}
	}

	@Override
	protected void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException {
		switch (preparedConnectionType) {
		case GET_PLAYER_UUID:
			mGetPlayerUUID = connection.prepareStatement("SELECT UUID FROM mh_PlayerSettings WHERE NAME=?;");
			break;
		case GET_PLAYER_SETTINGS:
			mGetPlayerSettings = connection.prepareStatement("SELECT * FROM mh_PlayerSettings WHERE UUID=?;");
			break;
		case INSERT_PLAYER_SETTINGS:
			mInsertPlayerSettings = connection.prepareStatement(
					"REPLACE INTO mh_PlayerSettings (UUID,NAME,LEARNING_MODE,MUTE_MODE,BALANCE,BALANCE_CHANGES,BANK_BALANCE,BANK_BALANCE_CHANGES) "
							+ "VALUES(?,?,?,?,?,?,?,?);");
			break;
		case GET_PLAYER_BALANCE:
			mGetPlayerBalance = connection
					.prepareStatement("SELECT * FROM mh_Balance WHERE UUID=? AND WORLDGRP=? AND GAMEMODE=?;");
			break;
		case INSERT_PLAYER_BALANCE:
			mInsertPlayerBalance = connection.prepareStatement(
					"REPLACE INTO mh_Balance (UUID,WORLDGRP,GAMEMODE,BALANCE,BALANCE_CHANGES,BANK_BALANCE,BANK_BALANCE_CHANGES) "
							+ "VALUES(?,?,?,?,?,?,?);");
			break;
		}

	}

	@Override
	public void databaseConvertToUtf8(String database_name) throws DataStoreException {

		// reference
		// http://stackoverflow.com/questions/6115612/how-to-convert-an-entire-mysql-database-characterset-and-collation-to-utf-8

		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.GREEN + "[BagOfGold] Converting BagOfGold Database to UTF8");

		Connection connection = setupConnection();

		try {
			Statement create = connection.createStatement();

			create.executeUpdate(
					"ALTER DATABASE " + database_name + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Players CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			create.executeUpdate("ALTER TABLE mh_Balance CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			console.sendMessage(ChatColor.GREEN + "[BagOfGold] Done.");

		} catch (SQLException e) {
			console.sendMessage(ChatColor.RED + "[BagOfGold] Something went wrong when converting database tables to UTF8MB4.");
			e.printStackTrace();
		}

	}

	// *******************************************************************************
	// V1 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV1Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = plugin.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Players "//
				+ "(UUID CHAR(40) ,"//
				+ " NAME VARCHAR(20),"//
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + ","//
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0,"//
				+ " PRIMARY KEY (PLAYER_ID))");

		create.close();
		connection.commit();

	}

	@Override
	protected void setupV2Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = plugin.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_PlayerSettings "//
				+ "(UUID CHAR(40) ,"//
				+ " NAME VARCHAR(20),"//
				+ " LAST_WORLDGRP VARCHAR(20) NOT NULL DEFAULT 'default'," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + ","//
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0,"//
				+ " PRIMARY KEY (UUID))");

		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Balance "//
				+ "(UUID CHAR(40) ,"//
				+ " WORLDGRP VARCHAR(20)," //
				+ " GAMEMODE INTEGER NOT NULL DEFAULT 1," //
				+ " BALANCE REAL DEFAULT 0,"//
				+ " BALANCE_CHANGES REAL DEFAULT 0,"//
				+ " BANK_BALANCE REAL DEFAULT 0,"//
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0,"//
				+ " PRIMARY KEY (UUID,WORLDGRP,GAMEMODE),"
				+ " CONSTRAINT mh_PlayerSettings_UUID FOREIGN KEY(UUID) REFERENCES mh_PlayerSettings(UUID) ON DELETE CASCADE) ");

		create.close();
		connection.commit();

	}

}
