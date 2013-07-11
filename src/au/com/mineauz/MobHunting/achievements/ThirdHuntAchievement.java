package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.MobHunting;

public class ThirdHuntAchievement implements ProgressAchievement
{
	private ExtendedMobType mType;
	
	public ThirdHuntAchievement(ExtendedMobType entity)
	{
		mType = entity;
	}
	@Override
	public String getName()
	{
		return "Apprentice " + mType.getName() + " Hunter";
	}

	@Override
	public String getID()
	{
		return "hunting-level3-" + mType.getName().toLowerCase();
	}

	@Override
	public String getDescription()
	{
		return "Hunt " + getMaxProgress() + " " + mType.getName() + "s";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialHunter3;
	}

	@Override
	public int getMaxProgress()
	{
		return mType.getMax() / 2;
	}

	@Override
	public String inheritFrom() { return "hunting-level2-" + mType.getName().toLowerCase(); }
}
