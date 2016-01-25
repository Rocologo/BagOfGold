package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class SecondHuntAchievement implements ProgressAchievement {
	private ExtendedMobType mType;

	public SecondHuntAchievement(ExtendedMobType entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.2.name", "mob",
				mType.getName());
	}

	@Override
	public String getID() {
		return "hunting-level2-" + mType.name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return Messages
				.getString(
						"achievements.hunter.2.description", "count", getMaxProgress(), "mob", mType.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public double getPrize() {
		return MobHunting.config().specialHunter2;
	}

	@Override
	public int getMaxProgress() {
		return (int) Math.round(mType.getMax() * 2.5);
	}

	@Override
	public String inheritFrom() {
		return "hunting-level1-" + mType.name().toLowerCase();
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialHunter2Cmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialHunter2CmdDesc;
	}
}
