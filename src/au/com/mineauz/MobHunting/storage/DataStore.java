package au.com.mineauz.MobHunting.storage;

import java.util.List;
import java.util.Set;

import au.com.mineauz.MobHunting.ExtendedMobType;

public interface DataStore
{
	public void initialize() throws DataStoreException;
	
	public void shutdown() throws DataStoreException;
	
	public void saveStats(Set<StatStore> stats) throws DataStoreException;
	public void saveAchievements(Set<AchievementStore> achievements) throws DataStoreException;
	
	public Set<AchievementStore> loadAchievements(String player) throws DataStoreException;

	public List<StatStore> loadKills( ExtendedMobType type, TimePeriod period, int count ) throws DataStoreException;
	public List<StatStore> loadAssists( ExtendedMobType type, TimePeriod period, int count ) throws DataStoreException;
}
