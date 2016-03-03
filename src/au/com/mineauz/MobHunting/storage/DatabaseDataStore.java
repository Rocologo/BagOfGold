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

public abstract class DatabaseDataStore implements DataStore {
	protected Connection mConnection;

	/**
	 * Args: player id
	 */
	protected PreparedStatement mAddPlayerStatsStatement;

	/**
	 * Args: player id, achievement, date, progress
	 */
	protected PreparedStatement mRecordAchievementStatement;

	/**
	 * Args: player uuid
	 */
	protected PreparedStatement myAddPlayerStatement;
	/**
	 * Args: player uuid
	 */
	protected PreparedStatement[] mGetPlayerStatement;

	/**
	 * Args: player id
	 */
	protected PreparedStatement mLoadAchievementsStatement;

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

	@Override
	public void initialize() throws DataStoreException {
		try {

			mConnection = setupConnection();
			mConnection.setAutoCommit(false);

			setupTables(mConnection);

			mGetPlayerStatement = new PreparedStatement[4];
			setupStatements(mConnection);

		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	protected abstract Connection setupConnection() throws SQLException,
			DataStoreException;

	protected abstract void setupTables(Connection connection)
			throws SQLException;

	protected abstract void setupStatements(Connection connection)
			throws SQLException;

	protected abstract void setupStatement_1(Connection connection)
			throws SQLException;

	protected void closeStatements() throws SQLException {
		mGetPlayerStatement[0].close();
		mGetPlayerStatement[1].close();
		mGetPlayerStatement[2].close();
		mGetPlayerStatement[3].close();
		mRecordAchievementStatement.close();
		mAddPlayerStatsStatement.close();
		mLoadAchievementsStatement.close();
		mGetPlayerUUID.close();
		mUpdatePlayerName.close();
		mGetPlayerData.close();
		mUpdatePlayerData.close();

	}

	protected void rollback() throws DataStoreException {

		try {
			mConnection.rollback();
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	@Override
	public void shutdown() throws DataStoreException {
		try {
			if (mConnection != null) {
				mConnection.commit();
				mConnection.close();
			}
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	protected Map<UUID, Integer> getPlayerIds(Set<OfflinePlayer> players)
			throws SQLException {

		setupStatement_1(mConnection);

		myAddPlayerStatement.clearBatch();

		for (OfflinePlayer player : players) {
			myAddPlayerStatement.setString(1, player.getPlayer().getUniqueId()
					.toString());
			myAddPlayerStatement.setString(2, player.getPlayer().getName());
			myAddPlayerStatement.addBatch();
		}
		myAddPlayerStatement.executeBatch();
		myAddPlayerStatement.close();

		int left = players.size();
		Iterator<OfflinePlayer> it = players.iterator();
		HashMap<UUID, Integer> ids = new HashMap<UUID, Integer>();

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
					updatePlayerName(player.getPlayer());
				}

				ids.put(UUID.fromString(results.getString(1)),
						results.getInt(3));
			}
			results.close();
		}

		return ids;
	}

	protected int getPlayerId(OfflinePlayer player) throws SQLException,
			DataStoreException {
		mGetPlayerStatement[0].setString(1, player.getUniqueId().toString());
		ResultSet result = mGetPlayerStatement[0].executeQuery();

		if (result.next()) {
			String name = result.getString(2);
			if (player.getUniqueId().equals(result.getString(1))
					&& !player.getName().equals(name)) {
				MobHunting.instance.getLogger().warning(
						"Name change detected(2): " + name + " -> "
								+ player.getName() + " UUID="
								+ player.getUniqueId().toString());
				updatePlayerName(player);
			}
			int res = result.getInt(3);
			result.close();
			return res;
		}

		throw new UserNotFoundException("User " + player.toString()
				+ " is not present in database");
	}

	protected void updatePlayerName(OfflinePlayer player) throws SQLException {
		try {
			mUpdatePlayerName.setString(1, player.getName());
			mUpdatePlayerName.setString(2, player.getUniqueId().toString());
			mUpdatePlayerName.executeUpdate();

			mConnection.commit();
		} finally {
			mConnection.rollback();
		}
	}

	@Override
	public OfflinePlayer getPlayerByName(String name) throws DataStoreException {
		try {
			mGetPlayerUUID.setString(1, name);
			ResultSet set = mGetPlayerUUID.executeQuery();

			if (set.next()) {
				UUID uid = UUID.fromString(set.getString(1));
				set.close();
				return Bukkit.getOfflinePlayer(uid);
			}
			throw new UserNotFoundException("User " + name
					+ " is not present in database");
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	@Override
	public Set<AchievementStore> loadAchievements(OfflinePlayer player)
			throws DataStoreException {
		try {
			int playerId = getPlayerId(player);
			mLoadAchievementsStatement.setInt(1, playerId);
			ResultSet set = mLoadAchievementsStatement.executeQuery();
			HashSet<AchievementStore> achievements = new HashSet<AchievementStore>();
			while (set.next()) {
				achievements.add(new AchievementStore(set.getString(1), player,
						set.getInt(3)));
			}
			set.close();
			return achievements;
		} catch (SQLException e) {
			throw new DataStoreException(e);
		}
	}

	@Override
	public void saveAchievements(Set<AchievementStore> achievements)
			throws DataStoreException {
		try {
			HashSet<OfflinePlayer> names = new HashSet<OfflinePlayer>();
			for (AchievementStore achievement : achievements)
				names.add(achievement.player);

			Map<UUID, Integer> ids = getPlayerIds(names);

			for (AchievementStore achievement : achievements) {
				mRecordAchievementStatement.setInt(1,
						ids.get(achievement.player.getUniqueId()));
				mRecordAchievementStatement.setString(2, achievement.id);
				mRecordAchievementStatement.setDate(3,
						new Date(System.currentTimeMillis()));
				mRecordAchievementStatement.setInt(4, achievement.progress);

				mRecordAchievementStatement.addBatch();
			}

			mRecordAchievementStatement.executeBatch();

			mConnection.commit();
		} catch (SQLException e) {

			rollback();
			throw new DataStoreException(e);
		}
	}

	@Override
	public PlayerData getPlayerData(OfflinePlayer player)
			throws DataStoreException {
		try {
			mGetPlayerStatement[0]
					.setString(1, player.getUniqueId().toString());

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

	@Override
	public void savePlayerData(Set<PlayerData> playerDataSet)
			throws DataStoreException {
		try {
			for (PlayerData playerData : playerDataSet) {
				Statement statement;
				try {
					// test if connection to MySql works properly
					statement = mConnection.createStatement();
					ResultSet rs = statement
							.executeQuery("SELECT PLAYER_ID from `mh_Players` LIMIT 0");
					rs.close();
					mUpdatePlayerData.setInt(1, playerData.isLearningMode() ? 1
							: 0);
					mUpdatePlayerData.setInt(2, playerData.isMuted() ? 1 : 0);
					mUpdatePlayerData.setString(3, playerData.getPlayer()
							.getUniqueId().toString());
					mUpdatePlayerData.addBatch();
					statement.close();
				} catch (SQLException e) {
					mInsertPlayerData.setString(1, playerData.getPlayer()
							.getUniqueId().toString());
					mInsertPlayerData.setString(2, playerData.getPlayer()
							.getName().toString());
					mInsertPlayerData.setInt(3, playerData.isLearningMode() ? 1
							: 0);
					mInsertPlayerData.setInt(4, playerData.isMuted() ? 1 : 0);
					mInsertPlayerData.addBatch();
				}
			}
			mUpdatePlayerData.executeBatch();
			// mUpdatePlayerData.close();

			mConnection.commit();
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}
}
