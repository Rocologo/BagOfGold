package au.com.mineauz.MobHunting.storage;

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

import au.com.mineauz.MobHunting.MobHunting;

public abstract class DatabaseDataStore implements IDataStore {
	/**
	 * Connection to the Database
	 */
	protected Connection mConnection;

	/**
	 * Args: player id
	 */
	protected PreparedStatement mSavePlayerStatsStatement;

	/**
	 * Args: player id
	 */
	protected PreparedStatement mLoadAchievementsStatement;

	/**
	 * Args: player id, achievement, date, progress
	 */
	protected PreparedStatement mSaveAchievementStatement;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement myAddPlayerStatement;
	/**
	 * Args: player uuid
	 */
	protected PreparedStatement[] mGetPlayerStatement;

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
	protected PreparedStatement mGetPlayerData;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mUpdatePlayerData;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mInsertPlayerData;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement mGetPlayerDATA;

	/**
	 *  Establish initial connection to Database
	 */
	protected abstract Connection setupConnection() throws SQLException,
			DataStoreException;

	/**
	 * Setup / Create database tables for MobHunting
	 */
	protected abstract void setupTables(Connection connection)
			throws SQLException;

	public enum PreparedConnectionType {
		SAVE_PLAYER_STATS, LOAD_ARCHIEVEMENTS, SAVE_ACHIEVEMENTS, UPDATE_PLAYER_NAME,
		UPDATE_PLAYER_SETTINGS, INSERT_PLAYER_DATA, GET1PLAYER, GET2PLAYERS, GET5PLAYERS, 
		GET10PLAYERS, GET_PLAYER_UUID, INSERT_PLAYER_SETTINGS, GET_PLAYER_SETTINGS
	};

	/**
	 * Open a connection to the Database and prepare a statement for executing.
	 * @param connection
	 * @param preparedConnectionType
	 * @throws SQLException
	 */
	protected abstract void openPreparedStatements(Connection connection,
			PreparedConnectionType preparedConnectionType) throws SQLException;

	/**
	 * Initialize the connection. Must be called after Opening of initial connection.
	 * Open Prepared statements for batch processing large selections of players. Batches
	 * will be performed in batches of 10,5,2,1 
	 */
	@Override
	public void initialize() throws DataStoreException {
		try {

			mConnection = setupConnection();
			mConnection.setAutoCommit(false);

			setupTables(mConnection);

			mGetPlayerStatement = new PreparedStatement[4];
			openPreparedStatements(mConnection,
					PreparedConnectionType.GET1PLAYER);
			openPreparedStatements(mConnection,
					PreparedConnectionType.GET2PLAYERS);
			openPreparedStatements(mConnection,
					PreparedConnectionType.GET5PLAYERS);
			openPreparedStatements(mConnection,
					PreparedConnectionType.GET10PLAYERS);

		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * Close all opened connections for batch processing. 
	 * @throws SQLException
	 */
	protected void closePreparedStatements() throws SQLException {
		mGetPlayerStatement[0].close();
		mGetPlayerStatement[1].close();
		mGetPlayerStatement[2].close();
		mGetPlayerStatement[3].close();
	}

	/**
	 * Rollback of last transaction on Database.
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
			closePreparedStatements();
			if (mConnection != null) {
				mConnection.commit();
				mConnection.close();
			}
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * getPlayerSettings
	 * @param player:OfflinePlayer
	 * @return PlayerData
	 * @throws DataStoreException
	 * 
	 */
	public PlayerData getPlayerSettings(OfflinePlayer player)
			throws DataStoreException {
		try {
			openPreparedStatements(mConnection,
					PreparedConnectionType.GET_PLAYER_SETTINGS);
			ResultSet result = mGetPlayerStatement[0].executeQuery();
			if (result.next()) {
				PlayerData ps = new PlayerData(player,
						result.getBoolean("LEARNING_MODE"),
						result.getBoolean("MUTE_MODE"));
				result.close();
				return ps;
			}
		} catch (SQLException e) {
			MobHunting.debug("ERROR in PlayerData.getPlayerData");
			e.printStackTrace();
		}
		throw new UserNotFoundException("User " + player.toString()
				+ " is not present in database");
	}

	/**
	 * getPLayerIds 
	 * @param players: A set of players: Set<OfflinePlayer>
	 * @return Map<UUID, Integer> a Map with all players UUID and player_ID in the Database. 
	 * @throws SQLException
	 */
	protected Map<UUID, Integer> getPlayerIds(Set<OfflinePlayer> players)
			throws SQLException {

		// make sure all players are in mh_Players.
		openPreparedStatements(mConnection,
				PreparedConnectionType.INSERT_PLAYER_SETTINGS);
		myAddPlayerStatement.clearBatch();
		for (OfflinePlayer player : players) {
			myAddPlayerStatement.setString(1, player.getPlayer().getUniqueId()
					.toString());
			myAddPlayerStatement.setString(2, player.getPlayer().getName());
			myAddPlayerStatement.addBatch();
		}
		myAddPlayerStatement.executeBatch();
		myAddPlayerStatement.close();
		mConnection.commit();

		// Then select all players in batches of 10,5,2,1 into the HashMap ids
		// and return ids.
		int left = players.size();
		Iterator<OfflinePlayer> it = players.iterator();
		HashMap<UUID, Integer> ids = new HashMap<UUID, Integer>();
		ArrayList<OfflinePlayer> changedNames = new ArrayList<OfflinePlayer>();

		while (left > 0) {
			PreparedStatement statement;
			int size = 0;
			if (left >= 10) {
				size = 10;
				statement = mGetPlayerStatement[3];
			} else if (left >= 5) {
				size = 5;
				statement = mGetPlayerStatement[2];
			} else if (left >= 2) {
				size = 2;
				statement = mGetPlayerStatement[1];
			} else {
				size = 1;
				statement = mGetPlayerStatement[0];
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
				if (results.getString(1)
						.equals(player.getUniqueId().toString())
						&& !results.getString(2).equals(
								player.getPlayer().getName())) {
					MobHunting.instance.getLogger().warning(
							"Name change detected(1): " + results.getString(2)
									+ " -> " + player.getPlayer().getName()
									+ " UUID="
									+ player.getUniqueId().toString());
					changedNames.add(player);
				}

				ids.put(UUID.fromString(results.getString(1)),
						results.getInt(3));
			}
			results.close();

			Iterator<OfflinePlayer> itr = changedNames.iterator();
			while (itr.hasNext()) {
				OfflinePlayer p = itr.next();
				updatePlayerName(p.getPlayer());
			}
		}
		return ids;
	}

	/**
	 * getPlayerID. get the player ID and check if the player has change name
	 * @param player
	 * @return PlayerID: int
	 * @throws SQLException
	 * @throws DataStoreException
	 */
	protected int getPlayerId(OfflinePlayer player) throws SQLException,
			DataStoreException {
		mGetPlayerStatement[0].setString(1, player.getUniqueId().toString());
		ResultSet result = mGetPlayerStatement[0].executeQuery();
		HashMap<UUID, Integer> ids = new HashMap<UUID, Integer>();
		ArrayList<OfflinePlayer> changedNames = new ArrayList<OfflinePlayer>();

		if (result.next()) {
			String name = result.getString(2);
			if (player.getUniqueId().equals(result.getString(1))
					&& !player.getName().equals(name)) {
				MobHunting.instance.getLogger().warning(
						"Name change detected(2): " + name + " -> "
								+ player.getName() + " UUID="
								+ player.getUniqueId().toString());
				ids.put(UUID.fromString(result.getString(1)), result.getInt(3));
			}
			int res = result.getInt(3);
			result.close();
			Iterator<OfflinePlayer> itr = changedNames.iterator();
			while (itr.hasNext()) {
				OfflinePlayer p = itr.next();
				updatePlayerName(p.getPlayer());
			}

			return res;
		}

		throw new UserNotFoundException("User " + player.toString()
				+ " is not present in database");
	}

	/**
	 * updatePlayerName - update the players name in the Database
	 * @param player: OfflinePlayer
	 * @throws SQLException
	 */
	protected void updatePlayerName(OfflinePlayer player) throws SQLException {
		openPreparedStatements(mConnection,
				PreparedConnectionType.UPDATE_PLAYER_NAME);
		try {
			mUpdatePlayerName.setString(1, player.getName());
			mUpdatePlayerName.setString(2, player.getUniqueId().toString());
			mUpdatePlayerName.executeUpdate();
			mUpdatePlayerName.close();
			mConnection.commit();
		} finally {
			mConnection.rollback();
		}
	}

	/**
	 * getPlayerByName - get the player
	 * @param name: String
	 * @return player
	 */
	@Override
	public OfflinePlayer getPlayerByName(String name) throws DataStoreException {
		try {
			openPreparedStatements(mConnection,
					PreparedConnectionType.GET_PLAYER_UUID);
			mGetPlayerUUID.setString(1, name);
			ResultSet set = mGetPlayerUUID.executeQuery();

			if (set.next()) {
				UUID uid = UUID.fromString(set.getString(1));
				set.close();
				mGetPlayerUUID.close();
				return Bukkit.getOfflinePlayer(uid);
			}
			throw new UserNotFoundException("User " + name
					+ " is not present in database");
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * loadAchievements - loading the achievements for one player into memory
	 * @param OfflinePlayer:
	 * @throws DataStoreException
	 */
	@Override
	public Set<AchievementStore> loadAchievements(OfflinePlayer player)
			throws DataStoreException {
		try {
			openPreparedStatements(mConnection,
					PreparedConnectionType.LOAD_ARCHIEVEMENTS);
			int playerId = getPlayerId(player);
			mLoadAchievementsStatement.setInt(1, playerId);
			ResultSet set = mLoadAchievementsStatement.executeQuery();
			HashSet<AchievementStore> achievements = new HashSet<AchievementStore>();
			while (set.next()) {
				achievements.add(new AchievementStore(set.getString(1), player,
						set.getInt(3)));
			}
			set.close();
			mLoadAchievementsStatement.close();
			return achievements;
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	/**
	 * saveAchievements - save all achievements to the Database
	 */
	@Override
	public void saveAchievements(Set<AchievementStore> achievements)
			throws DataStoreException {
		try {
			HashSet<OfflinePlayer> names = new HashSet<OfflinePlayer>();
			for (AchievementStore achievement : achievements)
				names.add(achievement.player);

			Map<UUID, Integer> ids = getPlayerIds(names);

			openPreparedStatements(mConnection,
					PreparedConnectionType.SAVE_ACHIEVEMENTS);
			for (AchievementStore achievement : achievements) {
				mSaveAchievementStatement.setInt(1,
						ids.get(achievement.player.getUniqueId()));
				mSaveAchievementStatement.setString(2, achievement.id);
				mSaveAchievementStatement.setDate(3,
						new Date(System.currentTimeMillis()));
				mSaveAchievementStatement.setInt(4, achievement.progress);

				mSaveAchievementStatement.addBatch();
			}
			mSaveAchievementStatement.executeBatch();
			mSaveAchievementStatement.close();
			mConnection.commit();
		} catch (SQLException e) {

			rollback();
			throw new DataStoreException(e);
		}
	}

	/**
	 * insertPalayerData - insert a Set of player data into the Database. 
	 */
	@Override
	public void insertPlayerData(Set<PlayerData> playerDataSet)
			throws DataStoreException {
		try {
			openPreparedStatements(mConnection,
					PreparedConnectionType.INSERT_PLAYER_DATA);
			for (PlayerData playerData : playerDataSet) {
				MobHunting.debug("insertPlayerData=%s", playerData.toString());
				mInsertPlayerData.setString(1, playerData.getPlayer()
						.getUniqueId().toString());
				mInsertPlayerData
						.setInt(2, playerData.isLearningMode() ? 1 : 0);
				mInsertPlayerData.setInt(3, playerData.isMuted() ? 1 : 0);
				mInsertPlayerData.addBatch();
			}
			mInsertPlayerData.executeBatch();
			mInsertPlayerData.close();
			mConnection.commit();
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	public void updatePlayerSettings(Set<PlayerData> playerDataSet)
			throws DataStoreException {
		try {
			openPreparedStatements(mConnection,
					PreparedConnectionType.UPDATE_PLAYER_SETTINGS);
			for (PlayerData playerData : playerDataSet) {
				mUpdatePlayerData
						.setInt(1, playerData.isLearningMode() ? 1 : 0);
				mUpdatePlayerData.setInt(2, playerData.isMuted() ? 1 : 0);
				mUpdatePlayerData.setString(3, playerData.getPlayer()
						.getUniqueId().toString());
				mUpdatePlayerData.addBatch();
			}
			mUpdatePlayerData.executeBatch();
			mUpdatePlayerData.close();
			mConnection.commit();
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

	/**
	 * databaseFixLeaderboard - tries to fix inconsistens in the database. Will later 
	 * be used for cleaning the database; deleting old data or so. This is not 
	 * implemented yet. 
	 */
	@Override
	public void databaseFixLeaderboard() throws SQLException {
		Statement statement = mConnection.createStatement();
		try {
			MobHunting.debug("Beginning cleaning of database");
			int result;
			result = statement
					.executeUpdate("DELETE FROM mh_Achievements WHERE PLAYER_ID NOT IN "
							+ "(SELECT PLAYER_ID FROM mh_Players "
							+ "where mh_Achievements.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting
					.debug("%s rows was deleted from Mh_Achievements", result);
			result = statement
					.executeUpdate("DELETE FROM mh_AllTime WHERE PLAYER_ID NOT IN "
							+ "(SELECT PLAYER_ID FROM mh_Players "
							+ "where mh_AllTime.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_AllTime", result);
			result = statement
					.executeUpdate("DELETE FROM mh_Daily WHERE PLAYER_ID NOT IN "
							+ "(SELECT PLAYER_ID FROM mh_Players "
							+ "where mh_Daily.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Daily", result);
			result = statement
					.executeUpdate("DELETE FROM mh_Monthly WHERE PLAYER_ID NOT IN "
							+ "(SELECT PLAYER_ID FROM mh_Players "
							+ "where mh_Monthly.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Monthly", result);
			result = statement
					.executeUpdate("DELETE FROM mh_Weekly WHERE PLAYER_ID NOT IN "
							+ "(SELECT PLAYER_ID FROM mh_Players "
							+ "where mh_Weekly.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Weekly", result);
			result = statement
					.executeUpdate("DELETE FROM mh_Yearly WHERE PLAYER_ID NOT IN "
							+ "(SELECT PLAYER_ID FROM mh_Players "
							+ "where mh_Yearly.PLAYER_ID=mh_Players.PLAYER_ID);");
			MobHunting.debug("%s rows was deleted from Mh_Yearly", result);
			statement.close();
			mConnection.commit();
			MobHunting.debug("MobHunting Database was cleaned");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
