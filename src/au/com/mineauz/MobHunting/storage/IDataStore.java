package au.com.mineauz.MobHunting.storage;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.StatType;

public interface IDataStore {
	/**
	 * Initialize - opening a connection to the Database and initialize the connection.
	 * @throws DataStoreException
	 */
	public void initialize() throws DataStoreException;

	/**
	 * Closing all connections to the Database
	 * @throws DataStoreException
	 */
	public void shutdown() throws DataStoreException;

	/**
	 * loadPlayerStats - Loading <count> records of Player Stats from the Database 
	 * @param type
	 * @param period
	 * @param count
	 * @return List<StatStore>
	 * @throws DataStoreException
	 */
	public List<StatStore> loadPlayerStats(StatType type, TimePeriod period, int count)
			throws DataStoreException;

	/**
	 * Save a Set of Player Stats to the Database
	 * @param stats
	 * @throws DataStoreException
	 */
	public void savePlayerStats(Set<StatStore> stats) throws DataStoreException;

	/**
	 * Load a Players Archievements
	 * @param player
	 * @return
	 * @throws DataStoreException
	 */
	public Set<AchievementStore> loadAchievements(OfflinePlayer player)
			throws DataStoreException;

	/**
	 * Save a Set of players archievements
	 * @param achievements
	 * @throws DataStoreException
	 */
	public void saveAchievements(Set<AchievementStore> achievements)
			throws DataStoreException;

	/**
	 * Get the player by his name from the Database. 
	 *ings @param name
	 * @return
	 * @throws DataStoreException
	 */
	public OfflinePlayer getPlayerByName(String name) throws DataStoreException;

	/**
	 * Get the players Settings from the Database
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws SQLException 
	 */
	public PlayerData getPlayerSettings(OfflinePlayer player)
			throws DataStoreException, SQLException;

	/**
	 * Update the players Settings in the Database
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	public void updatePlayerSettings(Set<PlayerData> playerDataSet)
			throws DataStoreException;

	/**
	 * Insert all PlayerData for one player into the Database
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	public void insertPlayerData(Set<PlayerData> playerDataSet)
			throws DataStoreException;
	
	/**
	 * Fixes error in the database
	 * @throws SQLException
	 */
	public void databaseFixLeaderboard() throws SQLException;

}
