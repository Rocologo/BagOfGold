package one.lindegaard.BagOfGold.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import one.lindegaard.BagOfGold.BagOfGold;

public class SQLiteDataStore extends DatabaseDataStore {

	private BagOfGold plugin;

	public SQLiteDataStore(BagOfGold plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	// *******************************************************************************
	// SETUP / INITIALIZE
	// *******************************************************************************

	@Override
	protected Connection setupConnection() throws DataStoreException {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder().getPath() + "/"
					+ plugin.getConfigManager().databaseName + ".db");
			connection.setAutoCommit(false);
			return connection;
		} catch (ClassNotFoundException classNotFoundEx) {
			throw new DataStoreException("SQLite not present on the classpath", classNotFoundEx);
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
					"INSERT OR REPLACE INTO mh_PlayerSettings (UUID,NAME,LAST_WORLDGRP,LEARNING_MODE,MUTE_MODE) "
							+ "VALUES(?,?,?,?,?);");
			break;
		case GET_PLAYER_BALANCE:
			mGetPlayerBalance = connection.prepareStatement("SELECT * FROM mh_Balance WHERE UUID=?;");
			break;
		case INSERT_PLAYER_BALANCE:
			mInsertPlayerBalance = connection.prepareStatement(
					"INSERT OR REPLACE INTO mh_Balance (UUID,WORLDGRP,GAMEMODE,BALANCE,BALANCE_CHANGES,BANK_BALANCE,BANK_BALANCE_CHANGES) "
							+ "VALUES(?,?,?,?,?,?,?);");
			break;
		}
	}

	@Override
	public void databaseConvertToUtf8(String database_name) throws DataStoreException {
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.RED + "[BagOfGold] this command is only for MySQL");
	}

	// *******************************************************************************
	// V1 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV1Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = plugin.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_PlayerSettings" //
				+ "(UUID TEXT," //
				+ " NAME TEXT, " //
				+ " PLAYER_ID INTEGER NOT NULL DEFAULT 1," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + "," //
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0," //
				+ " BALANCE REAL DEFAULT 0," //
				+ " BALANCE_CHANGES REAL DEFAULT 0," //
				+ " BANK_BALANCE REAL DEFAULT 0," //
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0," //
				+ " PRIMARY KEY(PLAYER_ID))");

		create.close();
		connection.commit();

	}

	@Override
	protected void setupV2Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = plugin.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_PlayerSettings" //
				+ "(UUID TEXT," //
				+ " NAME TEXT, " //
				+ " LAST_WORLDGRP NOT NULL DEFAULT 'default'," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + "," //
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0," //
				+ " PRIMARY KEY(UUID))");

		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Balance" //
				+ "(UUID TEXT," //
				+ " WORLDGRP TEXT DEFAULT 'default'," //
				+ " GAMEMODE INT DEFAULT 0," //
				+ " BALANCE REAL DEFAULT 0," //
				+ " BALANCE_CHANGES REAL DEFAULT 0," //
				+ " BANK_BALANCE REAL DEFAULT 0," //
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0," //
				+ " PRIMARY KEY(UUID, WORLDGRP, GAMEMODE)),"
				+ " FOREIGN KEY(UUID) REFERENCES mh_PlayerSettings(UUID) ON DELETE CASCADE");

		create.close();
		connection.commit();

	}

}
