package one.lindegaard.MobHunting.achievements;

import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.ExtendedMob;
import one.lindegaard.MobHunting.mobs.MobPlugin;

public class FifthHuntAchievement implements ProgressAchievement {

	private ExtendedMob mExtendedMob;

	public FifthHuntAchievement(ExtendedMob entity) {
		mExtendedMob = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.5.name", "mob", mExtendedMob.getFriendlyName());
	}

	@Override
	public String getID() {
		if (mExtendedMob.getMobPlugin() == MobPlugin.Minecraft)
			return "hunting-level5-" + mExtendedMob.getName().toLowerCase();
		else
			return mExtendedMob.getMobPlugin().name() + "-hunting-level5-" + mExtendedMob.getMobtype().toLowerCase();

	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.5.description", "count", getMaxProgress(), "mob",
				mExtendedMob.getFriendlyName());
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialHunter5;
	}

	@Override
	public int getMaxProgress() {
		return mExtendedMob.getProgressAchievementLevel1() * 25;
	}

	@Override
	public String inheritFrom() {
		if (mExtendedMob.getMobPlugin() == MobPlugin.Minecraft)
			return "hunting-level4-" + mExtendedMob.getMobtype().toLowerCase();
		else
			return mExtendedMob.getMobPlugin().name() + "-hunting-level4-" + mExtendedMob.getMobtype().toLowerCase();
	}

	@Override
	public String nextLevelId() {
		if (mExtendedMob.getMobPlugin() == MobPlugin.Minecraft)
			return "hunting-level6-" + mExtendedMob.getMobtype().toLowerCase();
		else
			return mExtendedMob.getMobPlugin().name() + "-hunting-level6-" + mExtendedMob.getMobtype().toLowerCase();
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialHunter5Cmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialHunter5CmdDesc;
	}

	@Override
	public ItemStack getSymbol() {
		return mExtendedMob.getCustomHead(mExtendedMob.getName(), 5, 0);
	}

	@Override
	public ExtendedMob getExtendedMob() {
		return mExtendedMob;
	}
}
