package one.lindegaard.BagOfGold.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.PlayerBalances;
import one.lindegaard.BagOfGold.PlayerSettings;

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
	 * Args n Top of records.
	 */
	protected PreparedStatement mTop25Balances;

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
	 * Setup / Create database version 3 tables for BagOfGold
	 */
	protected abstract void setupV3Tables(Connection connection) throws SQLException;

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
		GET_PLAYER_UUID, GET_PLAYER_SETTINGS, INSERT_PLAYER_SETTINGS, GET_PLAYER_BALANCE, INSERT_PLAYER_BALANCE,
		GET_TOP25_BALANCE
	};

	/**
	 * Initialize the connection. Must be called after Opening of initial
	 * connection. Open Prepared statements for batch processing large selections of
	 * players. Batches will be performed in batches of 10,5,2,1
	 */
	@Override
	public void initialize() throws DataStoreException {
		plugin.getMessages().debug("Initialize database");
		try {

			Connection mConnection = setupConnection();

			// Find current database version
			if (plugin.getConfigManager().databaseVersion < 3) {
				Statement statement = mConnection.createStatement();
				try {
					ResultSet rs = statement.executeQuery("SELECT TEXTURE FROM mh_PlayerSettings LIMIT 0");
					rs.close();
					plugin.getConfigManager().databaseVersion = 3;
				} catch (SQLException e1) {
					try {
						ResultSet rs = statement.executeQuery("SELECT UUID FROM mh_PlayerSettings LIMIT 0");
						rs.close();
						plugin.getConfigManager().databaseVersion = 2;
					} catch (SQLException e2) {
						try {
							// Check if Database exists at all?
							ResultSet rs = statement.executeQuery("SELECT UUID FROM mh_Balance LIMIT 0");
							rs.close();
							plugin.getConfigManager().databaseVersion = 1;
						} catch (SQLException e3) {
							// Database v1,v2 does not exist. Create V3
							plugin.getConfigManager().databaseVersion = 3;
						}

					}

				}
				statement.close();
				plugin.getConfigManager().saveConfig();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.WHITE
						+ " Database version " + plugin.getConfigManager().databaseVersion + " detected.");
			}

			switch (plugin.getConfigManager().databaseVersion) {
			case 1:
				setupV2Tables(mConnection);
				migrateDatabaseLayoutFromV1ToV2(mConnection);
				plugin.getConfigManager().databaseVersion = 2;
				plugin.getConfigManager().saveConfig();
				migrateDatabaseLayoutFromV2ToV3(mConnection);
				plugin.getConfigManager().databaseVersion = 3;
				plugin.getConfigManager().saveConfig();

			case 2:
				setupV2Tables(mConnection);
				migrateDatabaseLayoutFromV2ToV3(mConnection);
				plugin.getConfigManager().databaseVersion = 3;
				plugin.getConfigManager().saveConfig();

			default:
				setupV3Tables(mConnection);

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
	 * @param offlinePlayer :OfflinePlayer
	 * @return PlayerData
	 * @throws DataStoreException
	 * @throws SQLException
	 * 
	 */
	@Override
	public PlayerSettings loadPlayerSettings(OfflinePlayer offlinePlayer)
			throws UserNotFoundException, DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_SETTINGS);
			mGetPlayerSettings.setString(1, offlinePlayer.getUniqueId().toString());
			ResultSet result;
			result = mGetPlayerSettings.executeQuery();
			if (result.next()) {
				PlayerSettings ps = new PlayerSettings(offlinePlayer, result.getString("LAST_WORLDGRP"),
						result.getBoolean("LEARNING_MODE"), result.getBoolean("MUTE_MODE"), result.getString("TEXTURE"),
						result.getString("SIGNATURE"), result.getLong("LAST_LOGON"), result.getLong("LAST_INTEREST"));
				result.close();
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
				mInsertPlayerSettings.setString(6, playerSettings.getTexture());
				mInsertPlayerSettings.setString(7, playerSettings.getSignature());
				mInsertPlayerSettings.setLong(8, playerSettings.getLast_logon());
				mInsertPlayerSettings.setLong(9, playerSettings.getLast_interest());
				
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
	public void savePlayerSettings(Set<PlayerSettings> playerDataSet, boolean removeFromCache) throws DataStoreException {
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
					mInsertPlayerSettings.setString(6, playerSettings.getTexture());
					mInsertPlayerSettings.setString(7, playerSettings.getSignature());
					mInsertPlayerSettings.setLong(8, playerSettings.getLast_logon());
					mInsertPlayerSettings.setLong(9, playerSettings.getLast_interest());
					
					mInsertPlayerSettings.addBatch();
				}
				mInsertPlayerSettings.executeBatch();
				mInsertPlayerSettings.close();
				mConnection.commit();
				mConnection.close();

				plugin.getMessages().debug("PlayerSettings saved.");

				if (removeFromCache)
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

	/**
	 * getPlayerByName - get the player
	 * 
	 * @param name : String
	 * @return player
	 */
	@Override
	public OfflinePlayer getPlayerByName(String name) throws DataStoreException {
		if (name.equals("Random Bounty"))
			return null; // used for Random Bounties
		try {
			Connection mConnection = setupConnection();

			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_UUID);
			mGetPlayerUUID.setString(1, name);
			ResultSet set = mGetPlayerUUID.executeQuery();

			if (set.next()) {
				UUID uid = UUID.fromString(set.getString(1));
				set.close();
				mGetPlayerUUID.close();
				mConnection.close();
				return Bukkit.getOfflinePlayer(uid);
			}
			mGetPlayerUUID.close();
			mConnection.close();
			throw new UserNotFoundException("[MobHunting] User " + name + " is not present in database");
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	// ******************************************************************
	// PlayerBalances
	// ******************************************************************

	/**
	 * getPlayerBalances
	 * 
	 * @param offlinePlayer :OfflinePlayer
	 * @return PlayerBalances
	 * @throws DataStoreException
	 * @throws SQLException
	 * 
	 */
	@Override
	public PlayerBalances loadPlayerBalances(OfflinePlayer offlinePlayer)
			throws UserNotFoundException, DataStoreException {
		Connection mConnection;
		PlayerBalances playerBalances = new PlayerBalances();
		try {
			mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_BALANCE);
			mGetPlayerBalance.setString(1, offlinePlayer.getUniqueId().toString());
			ResultSet result = mGetPlayerBalance.executeQuery();
			while (result.next()) {
				PlayerBalance ps = new PlayerBalance(offlinePlayer, result.getString("WORLDGRP"),
						GameMode.getByValue(result.getInt("GAMEMODE")), result.getDouble("BALANCE"),
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
		if (!playerBalances.getPlayerBalances().isEmpty())
			return playerBalances;
		else
			throw new UserNotFoundException("User " + offlinePlayer.toString() + " is not present in database");
	}

	@Override
	public List<PlayerBalance> loadTop54(int n, String worldgroup, int gamemode) {
		Connection mConnection;
		List<PlayerBalance> playerBalances = new ArrayList<PlayerBalance>();
		try {
			mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_TOP25_BALANCE);
			mTop25Balances.setString(1, worldgroup);
			mTop25Balances.setString(2, worldgroup);
			mTop25Balances.setInt(3, gamemode);
			mTop25Balances.setInt(4, gamemode);
			mTop25Balances.setInt(5, n);

			ResultSet result = mTop25Balances.executeQuery();
			while (result.next()) {
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(result.getString("UUID")));
				if (offlinePlayer.getName() != null) {
					PlayerBalance ps = null;
					if (plugin.getPlayerBalanceManager().containsKey(offlinePlayer)) {
						ps = plugin.getPlayerBalanceManager().getPlayerBalance(offlinePlayer, worldgroup,
								GameMode.getByValue(gamemode));
					} else {
						ps = new PlayerBalance(offlinePlayer, result.getString("WORLDGRP"),
								GameMode.getByValue(result.getInt("GAMEMODE")), result.getDouble("BALANCE"),
								result.getDouble("BALANCE_CHANGES"), result.getDouble("BANK_BALANCE"),
								result.getDouble("BANK_BALANCE_CHANGES"));
					}
					playerBalances.add(ps);
				}
			}
			result.close();
			mTop25Balances.close();
			mConnection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		playerBalances.sort(Comparator.comparing(PlayerBalance::getTotalWealth).reversed());
		return playerBalances;
	}

}
