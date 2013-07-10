package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.ExtendedMobType;

public class BasicHuntAchievement implements ProgressAchievement
{
	private ExtendedMobType mType;
	
	public BasicHuntAchievement(ExtendedMobType entity)
	{
		mType = entity;
	}
	@Override
	public String getName()
	{
		return "Beginner " + mType.getName() + " Hunter";
	}

	@Override
	public String getID()
	{
		return "hunting-level1-" + mType.getName().toLowerCase();
	}

	@Override
	public String getDescription()
	{
		return "Hunt " + getMaxProgress() + " " + mType.getName() + "s";
	}

	@Override
	public double getPrize()
	{
		return 1000;
	}

	@Override
	public int getMaxProgress()
	{
		return mType.getMax() / 10;
	}

	@Override
	public String inheritFrom() { return null; }
}
