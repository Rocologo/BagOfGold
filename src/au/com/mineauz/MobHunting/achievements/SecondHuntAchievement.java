package au.com.mineauz.MobHunting.achievements;
import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.MobHunting;

public class SecondHuntAchievement implements ProgressAchievement
{
	private ExtendedMobType mType;
	
	public SecondHuntAchievement(ExtendedMobType entity)
	{
		mType = entity;
	}
	@Override
	public String getName()
	{
		return "Amature " + mType.getName() + " Hunter";
	}

	@Override
	public String getID()
	{
		return "hunting-level2-" + mType.getName().toLowerCase();
	}

	@Override
	public String getDescription()
	{
		return "Hunt " + getMaxProgress() + " " + mType.getName() + "s";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialHunter2;
	}

	@Override
	public int getMaxProgress()
	{
		return mType.getMax() / 4;
	}

	@Override
	public String inheritFrom() { return "hunting-level1-" + mType.getName().toLowerCase(); }
}
