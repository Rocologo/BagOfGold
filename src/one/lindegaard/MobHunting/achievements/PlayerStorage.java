package one.lindegaard.MobHunting.achievements;

import java.util.HashMap;
import java.util.HashSet;

public class PlayerStorage
{
	public boolean enableAchievements = false;
	public HashSet<String> gainedAchievements = new HashSet<String>();
	public HashMap<String, Integer> progressAchievements = new HashMap<String, Integer>();
}
