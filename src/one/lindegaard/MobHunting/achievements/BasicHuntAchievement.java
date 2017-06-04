package one.lindegaard.MobHunting.achievements;

import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.ExtendedMob;
import one.lindegaard.MobHunting.mobs.MobPlugin;

public class BasicHuntAchievement implements ProgressAchievement {
	private ExtendedMob mExtendedMob;

	public BasicHuntAchievement(ExtendedMob extendedMob) {
		mExtendedMob = extendedMob;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.1.name", "mob", mExtendedMob.getFriendlyName());
	}

	@Override
	public String getID() {
		if (mExtendedMob.getMobPlugin() == MobPlugin.Minecraft)
			return "hunting-level1-" + mExtendedMob.getName().toLowerCase();
		else
			return mExtendedMob.getMobPlugin().name() + "-hunting-level1-" + mExtendedMob.getMobtype().toLowerCase();

	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.1.description", "count", getMaxProgress(), "mob",
				mExtendedMob.getFriendlyName());
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialHunter1;
	}

	@Override
	public int getMaxProgress() {
		return mExtendedMob.getProgressAchievementLevel1();
	}

	@Override
	public String inheritFrom() {
		return null;
	}

	@Override
	public String nextLevelId() {
		if (mExtendedMob.getMobPlugin() == MobPlugin.Minecraft)
			return "hunting-level2-" + mExtendedMob.getMobtype().toLowerCase();
		else
			return mExtendedMob.getMobPlugin() + "-hunting-level2-" + mExtendedMob.getMobtype().toLowerCase();
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialHunter1Cmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialHunter1CmdDesc;
	}

	@Override
	public ItemStack getSymbol() {
		return mExtendedMob.getCustomHead(mExtendedMob.getName(), 1, 0);
	}

	@Override
	public ExtendedMob getExtendedMob() {
		return mExtendedMob;
	}
}
