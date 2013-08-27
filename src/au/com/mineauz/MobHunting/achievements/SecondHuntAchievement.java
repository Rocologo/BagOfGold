package au.com.mineauz.MobHunting.achievements;
import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.Messages;
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
		return Messages.getString("achievements.hunter.2.name", "mob", mType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String getID()
	{
		return "hunting-level2-" + mType.getName().toLowerCase(); //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.hunter.2.description", "count", getMaxProgress(), "mob", mType.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
	public String inheritFrom() { return "hunting-level1-" + mType.getName().toLowerCase(); } //$NON-NLS-1$
}
