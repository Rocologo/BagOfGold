package one.lindegaard.MobHunting.storage;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import one.lindegaard.MobHunting.StatType;
import one.lindegaard.MobHunting.bounty.Bounty;
import one.lindegaard.MobHunting.mobs.ExtendedMob;

public interface IDataStore {
	/**
	 * Initialize - opening a connection to the Database and initialize the
	 * connection.
	 * 
	 * @throws DataStoreException
	 */
	public void initialize() throws DataStoreException;

	/**
	 * Closing all connections to the Database
	 * 
	 * @throws DataStoreException
	 */
	public void shutdown() throws DataStoreException;

	/**
	 * loadPlayerStats - Loading <count> records of Player Stats from the
	 * Database
	 * 
	 * @param type
	 * @param period
	 * @param count
	 * @return List<StatStore>
	 * @throws DataStoreException
	 */
	public List<StatStore> loadPlayerStats(StatType type, TimePeriod period, int count) throws DataStoreException;

	/**
	 * Save a Set of Player Stats to the Database
	 * 
	 * @param stats
	 * @throws DataStoreException
	 */
	public void savePlayerStats(Set<StatStore> stats) throws DataStoreException;

	/**
	 * Load a Players Archievements
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 */
	public Set<AchievementStore> loadAchievements(OfflinePlayer player) throws DataStoreException;

	/**
	 * Save a Set of players archievements
	 * 
	 * @param achievements
	 * @throws DataStoreException
	 */
	public void saveAchievements(Set<AchievementStore> achievements) throws DataStoreException;

	/**
	 * Get the player by his name from the Database. ings @param name
	 * 
	 * @return
	 * @throws DataStoreException
	 */
	public OfflinePlayer getPlayerByName(String name) throws DataStoreException;

	/**
	 * Get the players Settings from the Database
	 * 
	 * @param player
	 * @return
	 * @throws DataStoreException
	 * @throws SQLException
	 */
	public PlayerSettings getPlayerSettings(OfflinePlayer player) throws DataStoreException, SQLException;

	/**
	 * Update the players Settings in the Database
	 * 
	 * @param playerDataSet
	 * @throws DataStoreException
	 */
	public void updatePlayerSettings(Set<PlayerSettings> ps) throws DataStoreException;

	/**
	 * Insert all PlayerData for one player into the Database
	 * 
	 * @param ps
	 * @throws DataStoreException
	 */
	public void insertPlayerSettings(PlayerSettings ps) throws DataStoreException;

	/**
	 * Fixes error in the database
	 * 
	 * @throws SQLException
	 * @throws DataStoreException 
	 */
	public void databaseFixLeaderboard() throws DataStoreException;

	public void insertBounty(Set<Bounty> bountyDataSet) throws DataStoreException;

	public void updateBounty(Set<Bounty> bountyDataSet) throws DataStoreException;

	public OfflinePlayer getPlayerByPlayerId(int playerId) throws DataStoreException;

	public void deleteBounty(Set<Bounty> bounties) throws DataStoreException;

	public void cancelBounty(Set<Bounty> bounties) throws DataStoreException;

	public Set<Bounty> loadBounties(OfflinePlayer mPlayer) throws DataStoreException;

	public int getPlayerId(OfflinePlayer player) throws DataStoreException, UserNotFoundException;

	public Set<ExtendedMob> loadMobs() throws DataStoreException;

	public void insertMissingVanillaMobs();
	
	public void insertMissingMythicMobs();

	public void insertMissingCitizensMobs();

	public void insertTARDISWeepingAngelsMobs();

	public void insertMysteriousHalloweenMobs();

	public void insertCustomMobs();

	public void insertMobs(Set<ExtendedMob> mobs) throws DataStoreException;

	public void updateMobs(Set<ExtendedMob> mobs) throws DataStoreException;

	public void insertMissingMythicMobs(String mob);

	public void insertCitizensMobs(String mob);

	public void insertTARDISWeepingAngelsMobs(String mob);

	public void insertMysteriousHalloweenMobs(String mob);
	
	public void insertCustomMobs(String mob);

}
