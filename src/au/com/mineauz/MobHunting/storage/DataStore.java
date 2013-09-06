package au.com.mineauz.MobHunting.storage;

import java.util.Set;

public interface DataStore
{
	public void initialize() throws DataStoreException;
	
	public void shutdown() throws DataStoreException;
	
	public void saveStats(Set<StatStore> stats) throws DataStoreException;
	public void saveAchievements(Set<AchievementStore> achievements) throws DataStoreException;
	
	public Set<AchievementStore> loadAchievements(String player) throws DataStoreException;
}
