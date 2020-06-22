package one.lindegaard.BagOfGold.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.PlayerBalances;
import one.lindegaard.Core.storage.DataStoreException;
import one.lindegaard.Core.storage.UserNotFoundException;

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
	 * Get the players Balances from the Database
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws SQLException
	 */
	PlayerBalances loadPlayerBalances(OfflinePlayer player) throws UserNotFoundException, DataStoreException;

	/**
	 * Save the players Balances in the Database
	 * 
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	void savePlayerBalances(Set<PlayerBalance> ps, boolean cleanCache) throws DataStoreException;

	/**
	 * Insert PlayerBalance one player into the Database
	 * 
	 * @param ps
	 * @throws DataStoreException
	 */
	void insertPlayerBalance(PlayerBalance ps) throws DataStoreException;

	/**
	 * Convert all tables to use UTF-8 character set.
	 * 
	 * @param database_name
	 * @throws DataStoreException
	 */
	void databaseConvertToUtf8(String database_name) throws DataStoreException;

	void migrateDatabaseLayoutFromV2ToV3(Connection connection) throws SQLException;
	
	List<PlayerBalance> loadTop54(int i, String worldGroup, int gamemode);
	
}
