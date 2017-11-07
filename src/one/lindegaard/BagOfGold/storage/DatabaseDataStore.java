package one.lindegaard.BagOfGold.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.BagOfGold;
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
	 * Args: player id
	 */
	protected PreparedStatement mSavePlayerStats;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mGetPlayerData;

	/**
	 * Args: player name
	 */
	protected PreparedStatement mGetPlayerUUID;

	/**
	 * Args: player name, player uuid
	 */
	protected PreparedStatement mUpdatePlayerName;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mUpdatePlayerSettings;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mInsertPlayerData;

	/**
	 * Args: Player OfflinePLayer
	 */
	protected PreparedStatement mGetBounties;

	/**
	 * Args: Bounty
	 */
	protected PreparedStatement mInsertBounty;

	/**
	 * Args: Bounty
	 */
	protected PreparedStatement mUpdateBounty;

	/**
	 * Args: Bounty ID
	 */
	protected PreparedStatement mDeleteBounty;

	/**
	 * Args: player player_id
	 */
	protected PreparedStatement mGetPlayerByPlayerId;

	/**
	 * Establish initial connection to Database
	 */
	protected abstract Connection setupConnection() throws SQLException, DataStoreException;

	/**
	 * Setup / Create database version 1 tables for BagOfGold
	 */
	protected abstract void setupV1Tables(Connection connection) throws SQLException;

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
		UPDATE_PLAYER_NAME, GET_PLAYER_DATA, GET_PLAYER_UUID, INSERT_PLAYER_DATA, UPDATE_PLAYER_SETTINGS, GET_PLAYER_BY_PLAYER_ID
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
				plugin.getConfigManager().databaseVersion = 1;
				plugin.getConfigManager().saveConfig();
			}

			switch (plugin.getConfigManager().databaseVersion) {
			case 1:
				Bukkit.getLogger().info(
						"[BagOfGold] Database version " + plugin.getConfigManager().databaseVersion + " detected.");
				setupV1Tables(mConnection);
			}

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
	public PlayerSettings loadPlayerSettings(OfflinePlayer offlinePlayer) throws DataStoreException, SQLException {
		Connection mConnection = setupConnection();
		openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_DATA);
		mGetPlayerData.setString(1, offlinePlayer.getUniqueId().toString());
		ResultSet result = mGetPlayerData.executeQuery();
		if (result.next()) {
			PlayerSettings ps = new PlayerSettings(offlinePlayer, result.getBoolean("LEARNING_MODE"),
					result.getBoolean("MUTE_MODE"), result.getDouble("BALANCE"), result.getDouble("BALANCE_CHANGES"),
					result.getDouble("BANK_BALANCE"), result.getDouble("BANK_BALANCE_CHANGES"));
			int id = result.getInt("PLAYER_ID");
			if (id != 0)
				ps.setPlayerId(id);
			result.close();
			plugin.getMessages().debug("Reading Playersettings from Database: %s", ps.toString());
			mGetPlayerData.close();
			mConnection.close();
			return ps;
		}
		mGetPlayerData.close();
		mConnection.close();
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
				openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_DATA);
				mInsertPlayerData.setString(1, playerSettings.getPlayer().getUniqueId().toString());
				mInsertPlayerData.setString(2, playerSettings.getPlayer().getName());
				mInsertPlayerData.setInt(3, playerSettings.isLearningMode() ? 1 : 0);
				mInsertPlayerData.setInt(4, playerSettings.isMuted() ? 1 : 0);
				mInsertPlayerData.setDouble(5, playerSettings.getBalance());
				mInsertPlayerData.setDouble(6, playerSettings.getBalanceChanges());
				mInsertPlayerData.setDouble(7, playerSettings.getBankBalance());
				mInsertPlayerData.setDouble(8, playerSettings.getBankBalanceChanges());
				mInsertPlayerData.addBatch();
				mInsertPlayerData.executeBatch();
				mInsertPlayerData.close();
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
	public void savePlayerSettings(Set<PlayerSettings> playerDataSet) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.UPDATE_PLAYER_SETTINGS);
				for (PlayerSettings playerData : playerDataSet) {
					mUpdatePlayerSettings.setInt(1, playerData.isLearningMode() ? 1 : 0);
					mUpdatePlayerSettings.setInt(2, playerData.isMuted() ? 1 : 0);
					mUpdatePlayerSettings.setDouble(3, Misc.floor(playerData.getBalance()));
					mUpdatePlayerSettings.setDouble(4, Misc.floor(playerData.getBalanceChanges()));
					mUpdatePlayerSettings.setDouble(5, Misc.floor(playerData.getBankBalance()));
					mUpdatePlayerSettings.setDouble(6, Misc.floor(playerData.getBankBalanceChanges()));
					mUpdatePlayerSettings.setString(7, playerData.getPlayer().getUniqueId().toString());
					mUpdatePlayerSettings.addBatch();
				}
				mUpdatePlayerSettings.executeBatch();
				mUpdatePlayerSettings.close();
				mConnection.commit();
				mConnection.close();

				for (PlayerSettings playerData : playerDataSet) {
					if (plugin.getPlayerSettingsManager().containsKey(playerData.getPlayer())
							&& !playerData.getPlayer().isOnline())
						plugin.getPlayerSettingsManager().removePlayerSettings(playerData.getPlayer());
				}

				plugin.getMessages().debug("PlayerSettings saved.");

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
	 * getPlayerID. get the player ID and check if the player has change name
	 * 
	 * @param offlinePlayer
	 * @return PlayerID: int
	 * @throws SQLException
	 * @throws DataStoreException
	 */
	public int getPlayerId(OfflinePlayer offlinePlayer) throws DataStoreException {
		if (offlinePlayer == null)
			return 0;
		int playerId = 0;
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		if (ps != null)
			playerId = ps.getPlayerId();
		if (playerId == 0) {
			Connection mConnection;
			try {
				ArrayList<OfflinePlayer> changedNames = new ArrayList<OfflinePlayer>();

				mConnection = setupConnection();
				openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_DATA);
				mGetPlayerData.setString(1, offlinePlayer.getUniqueId().toString());
				ResultSet result = mGetPlayerData.executeQuery();
				if (result.next()) {
					String name = result.getString(2);
					UUID uuid = UUID.fromString(result.getString(1));
					if (name != null && uuid != null)
						if (offlinePlayer.getUniqueId().equals(uuid) && !offlinePlayer.getName().equals(name)) {
							plugin.getLogger()
									.warning("[BagOfGold] Name change detected(2): " + name + " -> "
											+ offlinePlayer.getName() + " UUID="
											+ offlinePlayer.getUniqueId().toString());
							changedNames.add(offlinePlayer);
						}
					playerId = result.getInt(3);
					result.close();

				}
				result.close();
				mGetPlayerData.close();
				mConnection.close();

				Iterator<OfflinePlayer> itr = changedNames.iterator();
				while (itr.hasNext()) {
					OfflinePlayer p = itr.next();
					plugin.getMessages().debug("Updating playername in database and in memory (%s)", p.getName());
					updatePlayerName(p.getPlayer());
				}
			} catch (SQLException e) {
				throw new DataStoreException(e);
			}
		}
		return playerId;
	}

	/**
	 * updatePlayerName - update the players name in the Database
	 * 
	 * @param offlinePlayer
	 *            : OfflinePlayer
	 * @throws SQLException
	 * @throws DataStoreException
	 */
	protected void updatePlayerName(OfflinePlayer offlinePlayer) throws DataStoreException {
		Connection mConnection;
		try {
			mConnection = setupConnection();
			try {
				openPreparedStatements(mConnection, PreparedConnectionType.UPDATE_PLAYER_NAME);
				mUpdatePlayerName.setString(1, offlinePlayer.getName());
				mUpdatePlayerName.setString(2, offlinePlayer.getUniqueId().toString());
				mUpdatePlayerName.executeUpdate();
				mUpdatePlayerName.close();
				mConnection.commit();
				mConnection.close();
			} catch (SQLException e) {
				mConnection.close();
				throw new DataStoreException(e);
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * getPlayerByName - get the player
	 * 
	 * @param name
	 *            : String
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
			throw new UserNotFoundException("[BagOfGold] User " + name + " is not present in database");
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * getPlayerByName - get the player
	 * 
	 * @param name
	 *            : String
	 * @return player
	 */
	@Override
	public OfflinePlayer getPlayerByPlayerId(int playerId) throws DataStoreException {
		if (playerId == 0)
			return null; // Used for Random Bounty
		try {
			Connection mConnection = setupConnection();
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_BY_PLAYER_ID);
			mGetPlayerByPlayerId.setInt(1, playerId);
			ResultSet set = mGetPlayerByPlayerId.executeQuery();

			if (set.next()) {
				UUID uid = UUID.fromString(set.getString(1));
				set.close();
				mGetPlayerByPlayerId.close();
				mConnection.close();
				return Bukkit.getOfflinePlayer(uid);
			}
			mGetPlayerByPlayerId.close();
			mConnection.close();
			throw new UserNotFoundException("[BagOfGold] PlayerId " + playerId + " is not present in database");
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

}
