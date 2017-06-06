package one.lindegaard.MobHunting.achievements;

import one.lindegaard.MobHunting.mobs.ExtendedMob;

public interface ProgressAchievement extends Achievement
{
	public int getNextLevel();
	
	public String inheritFrom();
	
	public String nextLevelId();
	
	public ExtendedMob getExtendedMob();
}
