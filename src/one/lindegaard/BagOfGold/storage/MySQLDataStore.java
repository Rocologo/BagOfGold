package one.lindegaard.BagOfGold.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.storage.DatabaseDataStore.PreparedConnectionType;
import one.lindegaard.BagOfGold.util.Misc;

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
					"REPLACE INTO mh_PlayerSettings (UUID,NAME,LAST_WORLDGRP,LEARNING_MODE,MUTE_MODE) "
							+ "VALUES(?,?,?,?,?);");
			break;
		case GET_PLAYER_BALANCE:
			mGetPlayerBalance = connection
					.prepareStatement("SELECT * FROM mh_Balance WHERE UUID=?;");
			break;
		case INSERT_PLAYER_BALANCE:
			mInsertPlayerBalance = connection.prepareStatement(
					"INSERT INTO mh_Balance (UUID,WORLDGRP,GAMEMODE,BALANCE,BALANCE_CHANGES,BANK_BALANCE,BANK_BALANCE_CHANGES) "
							+ "VALUES(?,?,?,?,?,?,?) "
							+ "ON DUPLICATE KEY UPDATE BALANCE=?, BALANCE_CHANGES=?, BANK_BALANCE=?, BANK_BALANCE_CHANGES=?;");
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
				+ "(UUID CHAR(40) PRIMARY KEY,"//
				+ " NAME VARCHAR(20),"//
				+ " LAST_WORLDGRP VARCHAR(20) NOT NULL DEFAULT 'default'," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + ","//
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0,"//
				+ " UNIQUE KEY (UUID))");

		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Balance "//
				+ "(UUID CHAR(40),"//
				+ " WORLDGRP VARCHAR(20)," //
				+ " GAMEMODE INTEGER NOT NULL DEFAULT 0," //
				+ " BALANCE REAL DEFAULT 0,"//
				+ " BALANCE_CHANGES REAL DEFAULT 0,"//
				+ " BANK_BALANCE REAL DEFAULT 0,"//
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0,"//
				+ " UNIQUE KEY (UUID,WORLDGRP,GAMEMODE),"
				+ " CONSTRAINT mh_PlayerSettings_UUID FOREIGN KEY(UUID) REFERENCES mh_PlayerSettings(UUID) ON DELETE CASCADE) ");

		create.close();
	}
	
	public void migrateDatabaseLayoutFromV1ToV2(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		statement.executeUpdate("INSERT INTO mh_PlayerSettings (UUID,NAME,LAST_WORLDGRP,LEARNING_MODE,MUTE_MODE)"
				+ " SELECT DISTINCT UUID,NAME,'default',LEARNING_MODE,MUTE_MODE from mh_Players");
		statement.executeUpdate(
				"INSERT INTO mh_Balance (UUID,WORLDGRP,GAMEMODE,BALANCE,BALANCE_CHANGES,BANK_BALANCE,BANK_BALANCE_CHANGES)"
						+ " SELECT DISTINCT UUID,'default' A,0 B,MAX(BALANCE) D,MAX(BALANCE_CHANGES) E,MAX(BANK_BALANCE) F,MAX(BANK_BALANCE_CHANGES) G"
						+ "from mh_Players GROUP BY UUID,A,B ON DUPLICATE KEY UPDATE BALANCE=D, BALANCE_CHANGES=E, BANK_BALANCE=F, BANK_BALANCE_CHANGES=G");
		statement.executeUpdate("DROP TABLE mh_Players;");
		statement.close();
		connection.commit();
	}

	/**
	 * insertPlayerBalance to database
	 */
	@Override
	public void insertPlayerBalance(PlayerBalance playerBalance) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				BagOfGold.getInstance().getMessages().debug("DatabaseDataStore: insert to db=%s",
						playerBalance.toString());
				openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_BALANCE);
				mInsertPlayerBalance.setString(1, playerBalance.getPlayer().getUniqueId().toString());
				mInsertPlayerBalance.setString(2, playerBalance.getWorldGroup());
				mInsertPlayerBalance.setInt(3, 	playerBalance.getGamemode().getValue());
				mInsertPlayerBalance.setDouble(4, Misc.round(playerBalance.getBalance()));
				mInsertPlayerBalance.setDouble(5, Misc.round(playerBalance.getBalanceChanges()));
				mInsertPlayerBalance.setDouble(6, Misc.round(playerBalance.getBankBalance()));
				mInsertPlayerBalance.setDouble(7, Misc.round(playerBalance.getBankBalanceChanges()));
				// ON DUBLICATE KEY
				mInsertPlayerBalance.setDouble(8, Misc.round(playerBalance.getBalance()));
				mInsertPlayerBalance.setDouble(9, Misc.round(playerBalance.getBalanceChanges()));
				mInsertPlayerBalance.setDouble(10, Misc.round(playerBalance.getBankBalance()));
				mInsertPlayerBalance.setDouble(11, Misc.round(playerBalance.getBankBalanceChanges()));
				mInsertPlayerBalance.addBatch();
				mInsertPlayerBalance.executeBatch();
				mInsertPlayerBalance.close();
				mConnection.commit();
				mConnection.close();
			} catch (SQLException e) {
				rollback(mConnection);
				mConnection.close();
				throw new DataStoreException(e);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void savePlayerBalances(Set<PlayerBalance> playerBalanceSet, boolean cleanCache) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_BALANCE);
				for (PlayerBalance playerBalance : playerBalanceSet) {
					BagOfGold.getInstance().getMessages().debug("DatabaseDataStore: savedata: %s",
							playerBalance.toString());
					mInsertPlayerBalance.setString(1, playerBalance.getPlayer().getUniqueId().toString());
					mInsertPlayerBalance.setString(2, playerBalance.getWorldGroup());
					mInsertPlayerBalance.setInt(3, playerBalance.getGamemode().getValue());
					mInsertPlayerBalance.setDouble(4, Misc.round(playerBalance.getBalance()));
					mInsertPlayerBalance.setDouble(5, Misc.round(playerBalance.getBalanceChanges()));
					mInsertPlayerBalance.setDouble(6, Misc.round(playerBalance.getBankBalance()));
					mInsertPlayerBalance.setDouble(7, Misc.round(playerBalance.getBankBalanceChanges()));
					//ON DUBLICATE KEY
					mInsertPlayerBalance.setDouble(8, Misc.round(playerBalance.getBalance()));
					mInsertPlayerBalance.setDouble(9, Misc.round(playerBalance.getBalanceChanges()));
					mInsertPlayerBalance.setDouble(10, Misc.round(playerBalance.getBankBalance()));
					mInsertPlayerBalance.setDouble(11, Misc.round(playerBalance.getBankBalanceChanges()));

					mInsertPlayerBalance.addBatch();
				}
				mInsertPlayerBalance.executeBatch();
				mInsertPlayerBalance.close();
				mConnection.commit();
				mConnection.close();

				plugin.getMessages().debug("PlayerBalances saved.");

				if (cleanCache)
					for (PlayerBalance playerData : playerBalanceSet) {
						if (plugin.getPlayerBalanceManager().containsKey(playerData.getPlayer())
								&& !playerData.getPlayer().isOnline())
							plugin.getPlayerBalanceManager().removePlayerBalance(playerData.getPlayer());
					}

			} catch (SQLException e) {
				rollback(mConnection);
				mConnection.close();
				throw new DataStoreException(e);
			}
		} catch (SQLException e1) {
			throw new DataStoreException(e1);
		}
	}

}
