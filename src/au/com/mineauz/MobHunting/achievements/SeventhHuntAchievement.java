package au.com.mineauz.MobHunting.achievements;
import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class SeventhHuntAchievement implements ProgressAchievement {

	private ExtendedMobType mType;
	
	public SeventhHuntAchievement(ExtendedMobType entity)
	{
		mType = entity;
	}
	@Override
	public String getName()
	{
		return Messages.getString("achievements.hunter.7.name", "mob", mType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String getID()
	{
		return "hunting-level7-" + mType.name().toLowerCase(); //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.hunter.7.description", "count", getMaxProgress(), "mob", mType.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialHunter7;
	}

	@Override
	public int getMaxProgress()
	{
		return mType.getMax() * 100;
	}

	@Override
	public String inheritFrom() { return "hunting-level6-" + mType.name().toLowerCase(); } //$NON-NLS-1$
	
	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialHunter7Cmd;
	}
	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialHunter7CmdDesc;
	}
}
