package one.lindegaard.BagOfGold.storage;

import java.sql.SQLException;
import java.util.Set;

import org.bukkit.OfflinePlayer;

public interface IDataStore {
	/**
	 * Initialize - opening a connection to the Database and initialize the
	 * connection.
	 * 
	 * @throws DataStoreException
	 */
	void initialize() throws DataStoreException;

	/**
	 * Closing all connections to the Database
	 * 
	 * @throws DataStoreException
	 */
	void shutdown() throws DataStoreException;

	/**
	 * Get the player by his name from the Database. ings @param name
	 * 
	 * @return
	 * @throws DataStoreException
	 */
	OfflinePlayer getPlayerByName(String name) throws DataStoreException;

	/**
	 * Get the players Settings from the Database
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws SQLException
	 */
	PlayerSettings loadPlayerSettings(OfflinePlayer player) throws DataStoreException, SQLException;

	/**
	 * Update the players Settings in the Database
	 * 
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	void savePlayerSettings(Set<PlayerSettings> ps) throws DataStoreException;

	/**
	 * Insert all PlayerData for one player into the Database
	 * 
	 * @param ps
	 * @throws DataStoreException
	 */
	void insertPlayerSettings(PlayerSettings ps) throws DataStoreException;

	/**
	 * Convert all tables to use UTF-8 character set.
	 * @param database_name
	 * @throws DataStoreException
	 */
	void databaseConvertToUtf8(String database_name) throws DataStoreException;

	OfflinePlayer getPlayerByPlayerId(int playerId) throws DataStoreException;

	/**
	 * Get the player ID directly from the database
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws UserNotFoundException
	 */
	int getPlayerId(OfflinePlayer player) throws DataStoreException;

}
