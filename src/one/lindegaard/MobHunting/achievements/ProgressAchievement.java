package one.lindegaard.MobHunting.achievements;

public interface ProgressAchievement extends Achievement
{
	public int getMaxProgress();
	
	public String inheritFrom();
}
