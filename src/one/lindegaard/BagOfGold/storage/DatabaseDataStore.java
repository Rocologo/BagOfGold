package one.lindegaard.BagOfGold.storage;

import java.sql.*;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.PlayerBalances;
import one.lindegaard.BagOfGold.PlayerSettings;
import one.lindegaard.BagOfGold.util.Misc;

public abstract class DatabaseDataStore implements IDataStore {

	private BagOfGold plugin;

	public DatabaseDataStore(BagOfGold plugin) {
		this.plugin = plugin;
	}

	/**
	 * Connection to the Database
	 */
	// protected Connection mConnection;

	/**
	 * Args: player name
	 */
	protected PreparedStatement mGetPlayerUUID;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mGetPlayerSettings;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mInsertPlayerSettings;

	/**
	 * Args: player uuid,worldgrp,gamemode
	 */
	protected PreparedStatement mGetPlayerBalance;

	/**
	 * Args: player uuid,worldgrp,gamemode
	 */
	protected PreparedStatement mInsertPlayerBalance;

	/**
	 * Establish initial connection to Database
	 */
	protected abstract Connection setupConnection() throws SQLException, DataStoreException;

	/**
	 * Setup / Create database version 1 tables for BagOfGold
	 */
	protected abstract void setupV1Tables(Connection connection) throws SQLException;

	/**
	 * Setup / Create database version 2 tables for BagOfGold
	 */
	protected abstract void setupV2Tables(Connection connection) throws SQLException;

	/**
	 * Open a connection to the Database and prepare a statement for executing.
	 * 
	 * @param connection
	 * @param preparedConnectionType
	 * @throws SQLException
	 */
	protected abstract void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException;

	public enum PreparedConnectionType {
		GET_PLAYER_UUID, GET_PLAYER_SETTINGS, INSERT_PLAYER_SETTINGS, GET_PLAYER_BALANCE, INSERT_PLAYER_BALANCE
	};

	/**
	 * Initialize the connection. Must be called after Opening of initial
	 * connection. Open Prepared statements for batch processing large
	 * selections of players. Batches will be performed in batches of 10,5,2,1
	 */
	@Override
	public void initialize() throws DataStoreException {
		plugin.getMessages().debug("Initialize database");
		try {

			Connection mConnection = setupConnection();

			// Find current database version
			if (plugin.getConfigManager().databaseVersion == 0) {
				Statement statement = mConnection.createStatement();
				try {
					// Check if Database exists at all?
					ResultSet rs = statement.executeQuery("SELECT UUID FROM mh_Players LIMIT 0");
					rs.close();
					plugin.getConfigManager().databaseVersion = 1;
					plugin.getConfigManager().saveConfig();
				} catch (SQLException e2) {
					// Database v1 does not exist. Create V2
					setupV2Tables(mConnection);
					plugin.getConfigManager().databaseVersion = 2;
					plugin.getConfigManager().saveConfig();
				}
				statement.close();
			}

			switch (plugin.getConfigManager().databaseVersion) {
			case 1:
				Bukkit.getLogger().info(
						"[BagOfGold] Database version " + plugin.getConfigManager().databaseVersion + " detected.");
				setupV2Tables(mConnection);
				migrateDatabaseLayoutFromV1ToV2(mConnection);
			case 2:
				setupV2Tables(mConnection);
			}

			plugin.getConfigManager().databaseVersion = 2;
			plugin.getConfigManager().saveConfig();

			// Enable FOREIGN KEY for Sqlite database
			if (!plugin.getConfigManager().databaseType.equalsIgnoreCase("MySQL")) {
				Statement statement = mConnection.createStatement();
				statement.execute("PRAGMA foreign_keys = ON");
				statement.close();
			}
			mConnection.close();

		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * Rollback of last transaction on Database.
	 * 
	 * @throws DataStoreException
	 */
	protected void rollback(Connection mConnection) throws DataStoreException {

		try {
			mConnection.rollback();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * Shutdown: Commit and close database connection completely.
	 */
	@Override
	public void shutdown() throws DataStoreException {
		int n = 0;
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			n++;
		} while (plugin.getDataStoreManager().isRunning() && n < 40);
		System.out.println("[BagOfGold] Closing database connection.");
	}

	// ******************************************************************
	// Player Settings
	// ******************************************************************

	/**
	 * getPlayerSettings
	 * 
	 * @param offlinePlayer
	 *            :OfflinePlayer
	 * @return PlayerData
	 * @throws DataStoreException
	 * @throws SQLException
	 * 
	 */
	@Override
	public PlayerSettings loadPlayerSettings(OfflinePlayer offlinePlayer) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_SETTINGS);
			mGetPlayerSettings.setString(1, offlinePlayer.getUniqueId().toString());
			ResultSet result;
			result = mGetPlayerSettings.executeQuery();
			if (result.next()) {
				PlayerSettings ps = new PlayerSettings(offlinePlayer, result.getString("LAST_WORLDGRP"),
						result.getBoolean("LEARNING_MODE"), result.getBoolean("MUTE_MODE"));
				result.close();
				plugin.getMessages().debug("Reading from Database: %s", ps.toString());
				mGetPlayerSettings.close();
				mConnection.close();
				return ps;
			}
			mGetPlayerSettings.close();
			mConnection.close();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
		throw new UserNotFoundException("User " + offlinePlayer.toString() + " is not present in database");
	}

	/**
	 * insertPlayerSettings to database
	 */
	@Override
	public void insertPlayerSettings(PlayerSettings playerSettings) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_SETTINGS);
				mInsertPlayerSettings.setString(1, playerSettings.getPlayer().getUniqueId().toString());
				mInsertPlayerSettings.setString(2, playerSettings.getPlayer().getName());
				mInsertPlayerSettings.setString(3, playerSettings.getLastKnownWorldGrp());
				mInsertPlayerSettings.setInt(4, playerSettings.isLearningMode() ? 1 : 0);
				mInsertPlayerSettings.setInt(5, playerSettings.isMuted() ? 1 : 0);
				mInsertPlayerSettings.addBatch();
				mInsertPlayerSettings.executeBatch();
				mInsertPlayerSettings.close();
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
	public void savePlayerSettings(Set<PlayerSettings> playerDataSet, boolean cleanCache) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_SETTINGS);
				for (PlayerSettings playerSettings : playerDataSet) {
					mInsertPlayerSettings.setString(1, playerSettings.getPlayer().getUniqueId().toString());
					mInsertPlayerSettings.setString(2, playerSettings.getPlayer().getName());
					mInsertPlayerSettings.setString(3, playerSettings.getLastKnownWorldGrp());
					mInsertPlayerSettings.setInt(4, playerSettings.isLearningMode() ? 1 : 0);
					mInsertPlayerSettings.setInt(5, playerSettings.isMuted() ? 1 : 0);
					mInsertPlayerSettings.addBatch();
				}
				mInsertPlayerSettings.executeBatch();
				mInsertPlayerSettings.close();
				mConnection.commit();
				mConnection.close();

				plugin.getMessages().debug("PlayerSettings saved.");

				if (cleanCache)
					for (PlayerSettings playerData : playerDataSet) {
						if (plugin.getPlayerSettingsManager().containsKey(playerData.getPlayer())
								&& !playerData.getPlayer().isOnline() && playerData.getPlayer().hasPlayedBefore())
							plugin.getPlayerSettingsManager().removePlayerSettings(playerData.getPlayer());
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

	// ******************************************************************
	// PlayerBalances
	// ******************************************************************

	/**
	 * getPlayerBalances
	 * 
	 * @param offlinePlayer
	 *            :OfflinePlayer
	 * @return PlayerBalances
	 * @throws DataStoreException
	 * @throws SQLException
	 * 
	 */
	@Override
	public PlayerBalances loadPlayerBalances(OfflinePlayer offlinePlayer) throws DataStoreException {
		Connection mConnection;
		PlayerBalances playerBalances = new PlayerBalances();
		try {
			mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_BALANCE);
			mGetPlayerBalance.setString(1, offlinePlayer.getUniqueId().toString());
			ResultSet result = mGetPlayerBalance.executeQuery();
			while (result.next()) {
				PlayerBalance ps = new PlayerBalance(offlinePlayer, result.getString("WORLDGRP"),
						GameMode.valueOf(result.getString("GAMEMODE")), result.getDouble("BALANCE"),
						result.getDouble("BALANCE_CHANGES"), result.getDouble("BANK_BALANCE"),
						result.getDouble("BANK_BALANCE_CHANGES"));
				playerBalances.putPlayerBalance(ps);
			}
			result.close();
			mGetPlayerBalance.close();
			mConnection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (!playerBalances.getPlayerBalances().isEmpty()) {
			plugin.getMessages().debug("DatabaseDataStore - %s found in database:%s", offlinePlayer.getName(),
					playerBalances.toString());
			return playerBalances;
		} else {
			plugin.getMessages().debug("DatabaseDataStore: player not found in DB");
			throw new UserNotFoundException("User " + offlinePlayer.toString() + " is not present in database");
		}

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
					mInsertPlayerBalance.setString(3, playerBalance.getGamemode().toString());
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
