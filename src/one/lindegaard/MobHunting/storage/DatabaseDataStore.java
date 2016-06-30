package one.lindegaard.MobHunting.storage;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.bounty.BountyStatus;

public abstract class DatabaseDataStore implements IDataStore {
	/**
	 * Connection to the Database
	 */
	protected Connection mConnection;

	/**
	 * Args: player id
	 */
	protected PreparedStatement mSavePlayerStats;

	/**
	 * Args: player id
	 */
	protected PreparedStatement mLoadAchievements;

	/**
	 * Args: player id, achievement, date, progress
	 */
	protected PreparedStatement mSaveAchievement;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement[] mGetPlayerData;

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
	 * Setup / Create database tables for MobHunting
	 */
	protected abstract void setupTables(Connection connection) throws SQLException;

	public static int connections = 0;
	public static final int MAX_CONNECTIONS = 2;

	public enum PreparedConnectionType {
		SAVE_PLAYER_STATS, LOAD_ARCHIEVEMENTS, SAVE_ACHIEVEMENTS, UPDATE_PLAYER_NAME, GET1PLAYER, GET2PLAYERS, GET5PLAYERS, GET10PLAYERS, GET_PLAYER_UUID, INSERT_PLAYER_DATA, UPDATE_PLAYER_SETTINGS, GET_BOUNTIES, INSERT_BOUNTY, UPDATE_BOUNTY, DELETE_BOUNTY, GET_PLAYER_BY_PLAYER_ID
	};

	/**
	 * Open a connection to the Database and prepare a statement for executing.
	 * 
	 * @param connection
	 * @param preparedConnectionType
	 * @throws SQLException
	 */
	protected abstract void openPreparedStatements(Connection connection, PreparedConnectionType preparedConnectionType)
			throws SQLException;

	/**
	 * Initialize the connection. Must be called after Opening of initial
	 * connection. Open Prepared statements for batch processing large
	 * selections of players. Batches will be performed in batches of 10,5,2,1
	 */
	@Override
	public void initialize() throws DataStoreException {
		try {

			mConnection = setupConnection();
			mConnection.setAutoCommit(false);
			setupTables(mConnection);
			mGetPlayerData = new PreparedStatement[4];
			// openPreparedGetPlayerStatements();

		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * Open connections for batch processing.
	 * 
	 * @throws SQLException
	 */
	protected void openPreparedGetPlayerStatements() throws SQLException {
		openPreparedStatements(mConnection, PreparedConnectionType.GET1PLAYER);
		openPreparedStatements(mConnection, PreparedConnectionType.GET2PLAYERS);
		openPreparedStatements(mConnection, PreparedConnectionType.GET5PLAYERS);
		openPreparedStatements(mConnection, PreparedConnectionType.GET10PLAYERS);
	}

	/**
	 * Close all opened connections for batch processing.
	 * 
	 * @throws SQLException
	 */
	protected void closePreparedGetPlayerStatements() throws SQLException {
		mGetPlayerData[0].close();
		mGetPlayerData[1].close();
		mGetPlayerData[2].close();
		mGetPlayerData[3].close();
		if (MobHunting.getConfigManager().debugSQL) {
			connections = connections - 4;
			if (connections >= MAX_CONNECTIONS)
				MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
		}
	}

	/**
	 * Rollback of last transaction on Database.
	 * 
	 * @throws DataStoreException
	 */
	protected void rollback() throws DataStoreException {

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
		try {
			if (mConnection != null) {
				mConnection.commit();
				int n = 0;
				do {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					n++;
				} while (MobHunting.getDataStoreManager().isRunning() && n < 40);
				System.out.println("[MobHunting] Closing database connection.");
				mConnection.close();
			}
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	// ******************************************************************
	// Player Settings
	// ******************************************************************

	/**
	 * getPlayerSettings
	 * 
	 * @param player
	 *            :OfflinePlayer
	 * @return PlayerData
	 * @throws DataStoreException
	 * @throws SQLException
	 * 
	 */
	@Override
	public PlayerSettings getPlayerSettings(OfflinePlayer player) throws DataStoreException, SQLException {
		openPreparedGetPlayerStatements();
		mGetPlayerData[0].setString(1, player.getUniqueId().toString());
		ResultSet result = mGetPlayerData[0].executeQuery();
		if (result.next()) {
			PlayerSettings ps = new PlayerSettings(player, result.getBoolean("LEARNING_MODE"),
					result.getBoolean("MUTE_MODE"));
			int id = result.getInt("PLAYER_ID");
			if (id != 0)
				ps.setPlayerId(id);
			result.close();
			MobHunting.debug("Read Playersettings from Database: %s", ps.toString());
			closePreparedGetPlayerStatements();
			return ps;
		}
		closePreparedGetPlayerStatements();
		throw new UserNotFoundException("User " + player.toString() + " is not present in database");
	}

	/**
	 * insertPalayerData - insert a Set of player data into the Database.
	 */
	@Override
	public void insertPlayerSettings(PlayerSettings playerData) throws DataStoreException {
		try {
			openPreparedStatements(mConnection, PreparedConnectionType.INSERT_PLAYER_DATA);
			mInsertPlayerData.setString(1, playerData.getPlayer().getUniqueId().toString());
			mInsertPlayerData.setString(2, playerData.getPlayer().getName());
			mInsertPlayerData.setInt(3, playerData.isLearningMode() ? 1 : 0);
			mInsertPlayerData.setInt(4, playerData.isMuted() ? 1 : 0);
			mInsertPlayerData.addBatch();
			mInsertPlayerData.executeBatch();
			mInsertPlayerData.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			mConnection.commit();
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	public void updatePlayerSettings(Set<PlayerSettings> playerDataSet) throws DataStoreException {
		try {
			openPreparedStatements(mConnection, PreparedConnectionType.UPDATE_PLAYER_SETTINGS);
			for (PlayerSettings playerData : playerDataSet) {
				mUpdatePlayerSettings.setInt(1, playerData.isLearningMode() ? 1 : 0);
				mUpdatePlayerSettings.setInt(2, playerData.isMuted() ? 1 : 0);
				mUpdatePlayerSettings.setString(3, playerData.getPlayer().getUniqueId().toString());
				mUpdatePlayerSettings.addBatch();
			}
			mUpdatePlayerSettings.executeBatch();
			mUpdatePlayerSettings.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			mConnection.commit();
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

	/**
	 * getPlayerIds
	 * 
	 * @param players
	 *            : A set of players: Set<OfflinePlayer>
	 * @return Map<UUID, Integer> a Map with all players UUID and player_ID in
	 *         the Database.
	 * @throws SQLException
	 */
	protected Map<UUID, Integer> getPlayerIds(Set<OfflinePlayer> players) throws SQLException {

		// Then select all players in batches of 10,5,2,1 into the HashMap ids
		// and return ids.
		int left = players.size();
		Iterator<OfflinePlayer> it = players.iterator();
		HashMap<UUID, Integer> ids = new HashMap<UUID, Integer>();
		ArrayList<OfflinePlayer> changedNames = new ArrayList<OfflinePlayer>();

		while (left > 0) {
			PreparedStatement statement;
			openPreparedGetPlayerStatements();
			int size = 0;
			if (left >= 10) {
				size = 10;
				statement = mGetPlayerData[3];
			} else if (left >= 5) {
				size = 5;
				statement = mGetPlayerData[2];
			} else if (left >= 2) {
				size = 2;
				statement = mGetPlayerData[1];
			} else {
				size = 1;
				statement = mGetPlayerData[0];
			}

			left -= size;

			ArrayList<OfflinePlayer> temp = new ArrayList<OfflinePlayer>(size);
			for (int i = 0; i < size; ++i) {
				OfflinePlayer player = it.next();
				temp.add(player);
				statement.setString(i + 1, player.getUniqueId().toString());
			}

			ResultSet results = statement.executeQuery();

			int index = 0;
			while (results.next()) {
				OfflinePlayer player = temp.get(index++);
				if (results.getString(1).equals(player.getUniqueId().toString())
						&& !results.getString(2).equals(player.getPlayer().getName())) {
					MobHunting.getInstance().getLogger()
							.warning("[MobHunting] Name change detected(1): " + results.getString(2) + " -> "
									+ player.getPlayer().getName() + " UUID=" + player.getUniqueId().toString());
					changedNames.add(player);
				}

				ids.put(UUID.fromString(results.getString(1)), results.getInt(3));
			}
			results.close();

			Iterator<OfflinePlayer> itr = changedNames.iterator();
			while (itr.hasNext()) {
				OfflinePlayer p = itr.next();
				updatePlayerName(p.getPlayer());
			}
		}
		closePreparedGetPlayerStatements();
		mConnection.commit();
		return ids;
	}

	/**
	 * getPlayerID. get the player ID and check if the player has change name
	 * 
	 * @param offlinePlayer
	 * @return PlayerID: int
	 * @throws SQLException
	 * @throws DataStoreException
	 */
	public int getPlayerId(OfflinePlayer offlinePlayer) throws SQLException, DataStoreException {
		if (offlinePlayer == null)
			return 0;
		int res = 0;
		if (offlinePlayer.isOnline()) {
			PlayerSettings ps = MobHunting.getPlayerSettingsmanager().getPlayerSettings(offlinePlayer);
			if (ps != null && ps.getPlayerId() != 0)
				res = ps.getPlayerId();
		}
		if (res == 0) {
			openPreparedGetPlayerStatements();
			mGetPlayerData[0].setString(1, offlinePlayer.getUniqueId().toString());
			ResultSet result = mGetPlayerData[0].executeQuery();
			HashMap<UUID, Integer> ids = new HashMap<UUID, Integer>();
			ArrayList<OfflinePlayer> changedNames = new ArrayList<OfflinePlayer>();

			if (result.next()) {
				String name = result.getString(2);
				UUID uuid = UUID.fromString(result.getString(1));
				if (name != null && uuid != null)
					if (offlinePlayer.getUniqueId().equals(uuid) && !offlinePlayer.getName().equals(name)) {
						MobHunting.getInstance().getLogger().warning("[MobHunting] Name change detected(2): " + name
								+ " -> " + offlinePlayer.getName() + " UUID=" + offlinePlayer.getUniqueId().toString());
						ids.put(UUID.fromString(result.getString(1)), result.getInt(3));
					}
				res = result.getInt(3);
				result.close();
				Iterator<OfflinePlayer> itr = changedNames.iterator();
				while (itr.hasNext()) {
					OfflinePlayer p = itr.next();
					updatePlayerName(p.getPlayer());
				}
			}
			result.close();
			closePreparedGetPlayerStatements();
		} else {
			MobHunting.debug("Using PlayerId %s from memory.", res);
		}
		return res;
	}

	/**
	 * updatePlayerName - update the players name in the Database
	 * 
	 * @param player
	 *            : OfflinePlayer
	 * @throws SQLException
	 */
	protected void updatePlayerName(OfflinePlayer player) throws SQLException {
		openPreparedStatements(mConnection, PreparedConnectionType.UPDATE_PLAYER_NAME);
		try {
			mUpdatePlayerName.setString(1, player.getName());
			mUpdatePlayerName.setString(2, player.getUniqueId().toString());
			mUpdatePlayerName.executeUpdate();
			mUpdatePlayerName.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			mConnection.commit();
		} finally {
			mConnection.rollback();
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
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_UUID);
			mGetPlayerUUID.setString(1, name);
			ResultSet set = mGetPlayerUUID.executeQuery();

			if (set.next()) {
				UUID uid = UUID.fromString(set.getString(1));
				set.close();
				mGetPlayerUUID.close();
				if (MobHunting.getConfigManager().debugSQL) {
					connections--;
					if (connections >= MAX_CONNECTIONS)
						MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
				}
				return Bukkit.getOfflinePlayer(uid);
			}
			mGetPlayerUUID.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			throw new UserNotFoundException("[MobHunting] User " + name + " is not present in database");
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
			openPreparedStatements(mConnection, PreparedConnectionType.GET_PLAYER_BY_PLAYER_ID);
			mGetPlayerByPlayerId.setInt(1, playerId);
			ResultSet set = mGetPlayerByPlayerId.executeQuery();

			if (set.next()) {
				UUID uid = UUID.fromString(set.getString(1));
				set.close();
				mGetPlayerByPlayerId.close();
				if (MobHunting.getConfigManager().debugSQL) {
					connections--;
					if (connections >= MAX_CONNECTIONS)
						MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
				}
				return Bukkit.getOfflinePlayer(uid);
			}
			mGetPlayerByPlayerId.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			throw new UserNotFoundException("[MobHunting] PlayerId " + playerId + " is not present in database");
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * loadAchievements - loading the achievements for one player into memory
	 * 
	 * @param OfflinePlayer
	 *            :
	 * @throws DataStoreException
	 */
	@Override
	public Set<AchievementStore> loadAchievements(OfflinePlayer player) throws DataStoreException {
		HashSet<AchievementStore> achievements = new HashSet<AchievementStore>();
		try {
			openPreparedStatements(mConnection, PreparedConnectionType.LOAD_ARCHIEVEMENTS);
			int playerId = getPlayerId(player);
			if (playerId != 0) {
				mLoadAchievements.setInt(1, playerId);
				ResultSet set = mLoadAchievements.executeQuery();
				while (set.next()) {
					achievements.add(new AchievementStore(set.getString(1), player, set.getInt(3)));
				}
				set.close();
			}
			mLoadAchievements.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			return achievements;
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * saveAchievements - save all achievements to the Database
	 */
	@Override
	public void saveAchievements(Set<AchievementStore> achievements) throws DataStoreException {
		try {
			openPreparedStatements(mConnection, PreparedConnectionType.SAVE_ACHIEVEMENTS);
			for (AchievementStore achievement : achievements) {
				mSaveAchievement.setInt(1, getPlayerId(achievement.player));
				mSaveAchievement.setString(2, achievement.id);
				mSaveAchievement.setDate(3, new Date(System.currentTimeMillis()));
				mSaveAchievement.setInt(4, achievement.progress);

				mSaveAchievement.addBatch();
			}
			mSaveAchievement.executeBatch();
			mSaveAchievement.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}

			mConnection.commit();
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

	/**
	 * databaseFixLeaderboard - tries to fix inconsistens in the database. Will
	 * later be used for cleaning the database; deleting old data or so. This is
	 * not implemented yet.
	 */
	@Override
	public void databaseFixLeaderboard() throws SQLException {
		Statement statement = mConnection.createStatement();
		if (MobHunting.getConfigManager().debugSQL) {
			connections++;
			if (connections >= MAX_CONNECTIONS)
				MobHunting.debug("DatabaseDatastore: Open - connections=%s", connections);
		}
		try {
			MobHunting.debug("Beginning cleaning of database");
			int result;
			result = statement.executeUpdate("DELETE FROM mh_Achievements WHERE PLAYER_ID NOT IN "
					+ "(SELECT PLAYER_ID FROM mh_Players " + "where mh_Achievements.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Achievements", result);
			result = statement.executeUpdate("DELETE FROM mh_AllTime WHERE PLAYER_ID NOT IN "
					+ "(SELECT PLAYER_ID FROM mh_Players " + "where mh_AllTime.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_AllTime", result);
			result = statement.executeUpdate("DELETE FROM mh_Daily WHERE PLAYER_ID NOT IN "
					+ "(SELECT PLAYER_ID FROM mh_Players " + "where mh_Daily.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Daily", result);
			result = statement.executeUpdate("DELETE FROM mh_Monthly WHERE PLAYER_ID NOT IN "
					+ "(SELECT PLAYER_ID FROM mh_Players " + "where mh_Monthly.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Monthly", result);
			result = statement.executeUpdate("DELETE FROM mh_Weekly WHERE PLAYER_ID NOT IN "
					+ "(SELECT PLAYER_ID FROM mh_Players " + "where mh_Weekly.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Weekly", result);
			result = statement.executeUpdate("DELETE FROM mh_Yearly WHERE PLAYER_ID NOT IN "
					+ "(SELECT PLAYER_ID FROM mh_Players " + "where mh_Yearly.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Yearly", result);
			statement.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			mConnection.commit();
			MobHunting.debug("MobHunting Database was cleaned");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// ******************************************************************
	// Bounties
	// ******************************************************************
	@Override
	public Set<Bounty> loadBounties(OfflinePlayer offlinePlayer) throws DataStoreException {
		Set<Bounty> bounties = new HashSet<Bounty>();
		try {
			int playerId = getPlayerId(offlinePlayer);
			openPreparedStatements(mConnection, PreparedConnectionType.GET_BOUNTIES);
			mGetBounties.setInt(1, playerId);
			mGetBounties.setInt(2, playerId);

			ResultSet set = mGetBounties.executeQuery();

			while (set.next()) {
				Bounty b = new Bounty();
				b.setBountyOwnerId(set.getInt(1));
				b.setBountyOwner(getPlayerByPlayerId(set.getInt(1)));
				b.setMobtype(set.getString(2));
				b.setWantedPlayerId(set.getInt(3));
				b.setWantedPlayer(getPlayerByPlayerId(set.getInt(3)));
				b.setNpcId(set.getInt(4));
				b.setMobId(set.getString(5));
				b.setWorldGroup(set.getString(6));
				b.setCreatedDate(set.getLong(7));
				b.setEndDate(set.getLong(8));
				b.setPrize(set.getDouble(9));
				b.setMessage(set.getString(10));
				b.setStatus(BountyStatus.valueOf(set.getInt(11)));
				bounties.add(b);
			}
			set.close();
			mGetBounties.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			return (Set<Bounty>) bounties;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataStoreException(e);
		}
	};

	@Override
	public void insertBounty(Set<Bounty> bountyDataSet) throws DataStoreException {
		try {
			openPreparedStatements(mConnection, PreparedConnectionType.INSERT_BOUNTY);
			for (Bounty bounty : bountyDataSet) {
				int bountyOwnerId = getPlayerId(bounty.getBountyOwner());
				int wantedPlayerId = getPlayerId(bounty.getWantedPlayer());
				mInsertBounty.setString(1, bounty.getMobtype());
				mInsertBounty.setInt(2, bountyOwnerId);
				mInsertBounty.setInt(3, wantedPlayerId);
				mInsertBounty.setInt(4, bounty.getNpcId());
				mInsertBounty.setString(5, bounty.getMobId());
				mInsertBounty.setString(6, bounty.getWorldGroup());
				mInsertBounty.setLong(7, bounty.getCreatedDate());
				mInsertBounty.setLong(8, bounty.getEndDate());
				mInsertBounty.setDouble(9, bounty.getPrize());
				mInsertBounty.setString(10, bounty.getMessage());
				mInsertBounty.setInt(11, bounty.getStatus().getValue());
				mInsertBounty.addBatch();
			}
			mInsertBounty.executeBatch();
			mInsertBounty.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			mConnection.commit();
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}

	};

	@Override
	public void updateBounty(Set<Bounty> bountyDataSet) throws DataStoreException {
		try {
			openPreparedStatements(mConnection, PreparedConnectionType.UPDATE_BOUNTY);
			for (Bounty bounty : bountyDataSet) {
				mUpdateBounty.setDouble(1, bounty.getPrize());
				mUpdateBounty.setString(2, bounty.getMessage());
				mUpdateBounty.setLong(3, bounty.getEndDate());
				mUpdateBounty.setInt(4, bounty.getStatus().getValue());
				mUpdateBounty.setInt(5, getPlayerId(bounty.getWantedPlayer()));
				mUpdateBounty.setInt(6, getPlayerId(bounty.getBountyOwner()));
				mUpdateBounty.setString(7, bounty.getWorldGroup());
			}
			mUpdateBounty.executeBatch();
			mUpdateBounty.close();
			if (MobHunting.getConfigManager().debugSQL) {
				connections--;
				if (connections >= MAX_CONNECTIONS)
					MobHunting.debug("DatabaseDatastore: Close - connections=%s", connections);
			}
			mConnection.commit();
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	};

	@Override
	public void deleteBounty(Set<Bounty> bounties) throws DataStoreException {
		Iterator<Bounty> itr = bounties.iterator();
		while (itr.hasNext()) {
			Bounty b = itr.next();
			b.setStatus(BountyStatus.deleted);
		}
		insertBounty(bounties);
	}

	@Override
	public void cancelBounty(Set<Bounty> bounties) throws DataStoreException {
		Iterator<Bounty> itr = bounties.iterator();
		while (itr.hasNext()) {
			Bounty b = itr.next();
			b.setStatus(BountyStatus.canceled);
		}
		insertBounty(bounties);
	}

}
