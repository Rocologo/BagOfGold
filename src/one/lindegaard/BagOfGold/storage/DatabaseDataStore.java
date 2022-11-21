package one.lindegaard.BagOfGold.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.PlayerBalances;
import one.lindegaard.CustomItemsLib.storage.DataStoreException;
import one.lindegaard.CustomItemsLib.storage.UserNotFoundException;

public abstract class DatabaseDataStore implements IDataStore {

	private BagOfGold plugin;

	public DatabaseDataStore(BagOfGold plugin) {
		this.plugin = plugin;
	}

	/**
	 * Args: player name
	 */
	protected PreparedStatement mGetPlayerUUID;

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
	 * Setup / Create database version 2 tables for BagOfGold
	 */
	protected abstract void setupV2Tables(Connection connection) throws SQLException;

	/**
	 * Setup / Create database version 3 tables for BagOfGold
	 */
	protected abstract void setupV3Tables(Connection connection) throws SQLException;

	/**
	 * Setup / Create database version 4 tables for BagOfGold
	 */
	protected abstract void setupV4Tables(Connection connection) throws SQLException;

	/**
	 * Setup / Migrate from database version 6 to version 7 tables for MobHunting
	 * 
	 * @throws DataStoreException
	 */
	protected abstract void migrateDatabaseLayoutFromV3ToV4(Connection connection) throws DataStoreException;

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
		GET_PLAYER_BALANCE, INSERT_PLAYER_BALANCE, GET_TOP25_BALANCE, // GET_OLD_PLAYERSETTINGS,
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
			if (plugin.getConfigManager().databaseVersion < 4) {
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
			case 2:
				setupV2Tables(mConnection);
				migrateDatabaseLayoutFromV2ToV3(mConnection);
				plugin.getConfigManager().databaseVersion = 3;
				plugin.getConfigManager().saveConfig();

			case 3:
				setupV3Tables(mConnection);
				migrateDatabaseLayoutFromV3ToV4(mConnection);
				plugin.getConfigManager().databaseVersion = 4;
				plugin.getConfigManager().saveConfig();

			case 4:
				setupV4Tables(mConnection);

			}

			plugin.getConfigManager().databaseVersion = 4;
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
		Bukkit.getConsoleSender()
				.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET + "Closing database connection.");
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
			mTop25Balances.setInt(2, gamemode);
			mTop25Balances.setInt(3, n);

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

	@Override
	public void databaseDeleteOldPlayers() {
		plugin.getMessages().debug("Deleting players not known on this server.");
		int n = 0;
		try {
			Connection mConnection = setupConnection();
			Statement statement = mConnection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT UUID FROM mh_Balance");
			while (rs.next()) {
				String uuid = rs.getString("UUID");
				plugin.getMessages().debug("Player:%s - hasplayedbefore:%s", uuid,
						Bukkit.getOfflinePlayer(UUID.fromString(uuid)).hasPlayedBefore());
				if (!Bukkit.getOfflinePlayer(UUID.fromString(uuid)).hasPlayedBefore()) {
					plugin.getMessages().debug("Deleting player:%s from mh_Balance mh_Balance.", uuid);
					statement.executeUpdate("DELETE FROM mh_Balance WHERE UUID='" + uuid + "'");
					n++;
				}
			}
			rs.close();
			statement.close();
			mConnection.close();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.WHITE + n
					+ " players was deleted from the BagOfGold database.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
