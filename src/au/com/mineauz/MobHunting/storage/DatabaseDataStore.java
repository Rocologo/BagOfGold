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
		// mUpdatePlayerData.close();

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
				//mConnection.close();
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
		mConnection.commit();
		
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
			while(itr.hasNext()) {
				OfflinePlayer p = itr.next(); 
				updatePlayerName(p.getPlayer());
			}
		}
		return ids;
	}

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
				ids.put(UUID.fromString(result.getString(1)),result.getInt(3));
			}
			int res = result.getInt(3);
			result.close();
			
			Iterator<OfflinePlayer> itr = changedNames.iterator(); 
			while(itr.hasNext()) {
				OfflinePlayer p = itr.next(); 
				updatePlayerName(p.getPlayer());
			}
			
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
	public void createPlayerData(Set<PlayerData> playerDataSet)
			throws DataStoreException {
		try {
			for (PlayerData playerData : playerDataSet) {
				MobHunting.debug("playerdata=%s", playerData.toString());
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
	public void updatePlayerData(Set<PlayerData> playerDataSet)
			throws DataStoreException {
		try {
			for (PlayerData playerData : playerDataSet) {
				mUpdatePlayerData
						.setInt(1, playerData.isLearningMode() ? 1 : 0);
				mUpdatePlayerData.setInt(2, playerData.isMuted() ? 1 : 0);
				mUpdatePlayerData.setString(3, playerData.getPlayer()
						.getUniqueId().toString());
				mUpdatePlayerData.addBatch();
				mUpdatePlayerData.executeBatch();
				mUpdatePlayerName.close();
				mConnection.commit();
			}
		} catch (SQLException e) {
			rollback();
			throw new DataStoreException(e);
		}
	}

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
