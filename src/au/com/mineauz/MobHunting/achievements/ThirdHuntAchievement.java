package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.Messages;
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
		return Messages.getString("achievements.hunter.3.name", "mob", mType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String getID()
	{
		return "hunting-level3-" + mType.name().toLowerCase(); //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.hunter.3.description", "count", getMaxProgress(), "mob", mType.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialHunter3;
	}

	@Override
	public int getMaxProgress()
	{
		return mType.getMax() * 5;
	}

	@Override
	public String inheritFrom() { return "hunting-level2-" + mType.name().toLowerCase(); } //$NON-NLS-1$
	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialHunter3Cmd;
	}
	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialHunter3CmdDesc;
	}
}
