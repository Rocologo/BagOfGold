package au.com.mineauz.MobHunting.storage;

import java.util.List;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.StatType;

public interface DataStore {
	public void initialize() throws DataStoreException;

	public void shutdown() throws DataStoreException;

	public void saveStats(Set<StatStore> stats) throws DataStoreException;

	public void saveAchievements(Set<AchievementStore> achievements)
			throws DataStoreException;

	public Set<AchievementStore> loadAchievements(OfflinePlayer player)
			throws DataStoreException;

	public List<StatStore> loadStats(StatType type, TimePeriod period, int count)
			throws DataStoreException;

	public OfflinePlayer getPlayerByName(String name) throws DataStoreException;

	public PlayerData getPlayerData(OfflinePlayer player)
			throws DataStoreException;

	public void updatePlayerData(Set<PlayerData> playerDataSet)
			throws DataStoreException;

	public void createPlayerData(Set<PlayerData> playerDataSet)
			throws DataStoreException;

}
