package one.lindegaard.BagOfGold.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.ConsoleCommandSender;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.util.Misc;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.PlayerSettings;
import one.lindegaard.Core.storage.DataStoreException;

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
		case GET_PLAYER_BALANCE:
			mGetPlayerBalance = connection.prepareStatement("SELECT * FROM mh_Balance WHERE UUID=?;");
			break;
		case INSERT_PLAYER_BALANCE:
			mInsertPlayerBalance = connection.prepareStatement(
					"INSERT OR REPLACE INTO mh_Balance (UUID,WORLDGRP,GAMEMODE,BALANCE,BALANCE_CHANGES,BANK_BALANCE,BANK_BALANCE_CHANGES) "
							+ "VALUES(?,?,?,?,?,?,?);");
			break;
		case GET_TOP25_BALANCE:
			mTop25Balances = connection.prepareStatement(
					"select UUID,WORLDGRP,GAMEMODE, BALANCE, BALANCE_CHANGES, BANK_BALANCE,BANK_BALANCE_CHANGES, "
							+ "sum(BALANCE + BALANCE_CHANGES + BANK_BALANCE + BANK_BALANCE_CHANGES) AS 'TOTAL'"
							+ "FROM mh_Balance "//
							+ "WHERE (WORLDGRP=? OR ?='') AND (GAMEMODE=? OR ?=-1) "//
							+ "GROUP BY UUID "//
							+ "ORDER BY TOTAL DESC "//
							+ "LIMIT ?");//
			break;

		}
	}

	@Override
	public void databaseConvertToUtf8(String database_name) throws DataStoreException {
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.RED + "[BagOfGold] this command is only for MySQL");
	}

	// *******************************************************************************
	// V2 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV2Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = plugin.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_PlayerSettings" //
				+ "(UUID TEXT PRIMARY KEY," //
				+ " NAME TEXT, " //
				+ " LAST_WORLDGRP NOT NULL DEFAULT 'default'," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + "," //
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0," //
				+ " UNIQUE(UUID))");

		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Balance" //
				+ "(UUID TEXT," //
				+ " WORLDGRP TEXT DEFAULT 'default'," //
				+ " GAMEMODE INT DEFAULT 0," //
				+ " BALANCE REAL DEFAULT 0," //
				+ " BALANCE_CHANGES REAL DEFAULT 0," //
				+ " BANK_BALANCE REAL DEFAULT 0," //
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0," //
				+ " UNIQUE(UUID, WORLDGRP, GAMEMODE),"
				+ " FOREIGN KEY(UUID) REFERENCES mh_PlayerSettings(UUID) ON DELETE CASCADE)");

		create.close();
		connection.commit();

	}

	// *******************************************************************************
	// V3 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV3Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		String lm = plugin.getConfigManager().learningMode ? "1" : "0";
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_PlayerSettings" //
				+ "(UUID TEXT PRIMARY KEY," //
				+ " NAME TEXT, " //
				+ " LAST_WORLDGRP NOT NULL DEFAULT 'default'," //
				+ " LEARNING_MODE INTEGER NOT NULL DEFAULT " + lm + "," //
				+ " MUTE_MODE INTEGER NOT NULL DEFAULT 0," //
				+ " TEXTURE TEXT, " //
				+ " SIGNATURE TEXT, " //
				+ " LAST_LOGON INTEGER, " //
				+ " LAST_INTEREST INTEGER, " //
				+ " UNIQUE(UUID))");

		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Balance" //
				+ "(UUID TEXT," //
				+ " WORLDGRP TEXT DEFAULT 'default'," //
				+ " GAMEMODE INT DEFAULT 0," //
				+ " BALANCE REAL DEFAULT 0," //
				+ " BALANCE_CHANGES REAL DEFAULT 0," //
				+ " BANK_BALANCE REAL DEFAULT 0," //
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0," //
				+ " UNIQUE(UUID, WORLDGRP, GAMEMODE),"
				+ " FOREIGN KEY(UUID) REFERENCES mh_PlayerSettings(UUID) ON DELETE CASCADE)");

		create.close();
		connection.commit();

	}

	// *******************************************************************************
	// V4 DATABASE SETUP
	// *******************************************************************************

	@Override
	protected void setupV4Tables(Connection connection) throws SQLException {
		Statement create = connection.createStatement();

		// Create new empty tables if they do not exist
		create.executeUpdate("CREATE TABLE IF NOT EXISTS mh_Balance" //
				+ "(UUID TEXT," //
				+ " WORLDGRP TEXT DEFAULT 'default'," //
				+ " GAMEMODE INT DEFAULT 0," //
				+ " BALANCE REAL DEFAULT 0," //
				+ " BALANCE_CHANGES REAL DEFAULT 0," //
				+ " BANK_BALANCE REAL DEFAULT 0," //
				+ " BANK_BALANCE_CHANGES REAL DEFAULT 0," //
				+ " UNIQUE(UUID, WORLDGRP, GAMEMODE))");

		create.close();
		connection.commit();

	}

	public void migrateDatabaseLayoutFromV2ToV3(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			ResultSet rs = statement.executeQuery("SELECT TEXTURE from mh_PlayerSettings LIMIT 0");
			rs.close();
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.GOLD + "[BagOfGold] " + ChatColor.GREEN + "Adding new coloumns to BagOfGold Database.");
			statement.executeUpdate("alter table `mh_PlayerSettings` add column `TEXTURE` TEXT");
			statement.executeUpdate("alter table `mh_PlayerSettings` add column `SIGNATURE` TEXT");
			statement.executeUpdate("alter table `mh_PlayerSettings` add column `LAST_LOGON` INTEGER DEFAULT 0");
			statement.executeUpdate("alter table `mh_PlayerSettings` add column `LAST_INTEREST` INTEGER DEFAULT 0");
			statement.close();
			connection.commit();
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.GOLD + "[BagOfGold] " + ChatColor.GREEN + "Database was converted to version 3");
		}
	}

	protected void migrateDatabaseLayoutFromV3ToV4(Connection mConnection) throws DataStoreException {
		Statement statement;
		try {
			statement = mConnection.createStatement();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.GREEN
					+ "Copying players from BagOfGold til BagOfGoldCore database");
			ResultSet result = statement.executeQuery("select * from mh_PlayerSettings");
			while (result.next()) {
				String uuid = result.getString("UUID");
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
				PlayerSettings ps;
				if (offlinePlayer.hasPlayedBefore()) {
					ps = Core.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
					ps.setLastKnownWorldGrp(result.getString("LAST_WORLDGRP"));
					ps.setLearningMode(result.getBoolean("LEARNING_MODE"));
					ps.setMuteMode(result.getBoolean("MUTE_MODE"));
					ps.setTexture(result.getString("TEXTURE"));
					ps.setSignature(result.getString("SIGNATURE"));
					ps.setLast_logon(result.getLong("LAST_LOGON"));
					ps.setLast_interest(result.getLong("LAST_INTEREST"));
					Core.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
				}
			}
			Core.getDataStoreManager().flush();
			statement.close();
			mConnection.commit();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	// *******************************************************************************
	// Other functions
	// *******************************************************************************

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
				mInsertPlayerBalance.setInt(3, playerBalance.getGamemode().getValue());
				mInsertPlayerBalance.setDouble(4, Misc.round(playerBalance.getBalance()));
				mInsertPlayerBalance.setDouble(5, Misc.round(playerBalance.getBalanceChanges()));
				mInsertPlayerBalance.setDouble(6, Misc.round(playerBalance.getBankBalance()));
				mInsertPlayerBalance.setDouble(7, Misc.round(playerBalance.getBankBalanceChanges()));
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
