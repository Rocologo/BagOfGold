package one.lindegaard.MobHunting.achievements;

import one.lindegaard.MobHunting.ExtendedMobType;

public interface ProgressAchievement extends Achievement
{
	public int getMaxProgress();
	
	public String inheritFrom();
	
	public String nextLevelId();
	
	public ExtendedMobType getExtendedMobType();
}
