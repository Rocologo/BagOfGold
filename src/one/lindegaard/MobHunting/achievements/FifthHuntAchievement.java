package one.lindegaard.MobHunting.achievements;

import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MinecraftMob;

public class FifthHuntAchievement implements ProgressAchievement {

	private MinecraftMob mType;

	public FifthHuntAchievement(MinecraftMob entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.5.name", "mob", mType.getFriendlyName());
	}

	@Override
	public String getID() {
		return "hunting-level5-" + mType.name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.5.description", "count", getMaxProgress(), "mob",
				mType.getFriendlyName());
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialHunter5;
	}

	@Override
	public int getMaxProgress() {
		return mType.getMax() * 25;
	}

	@Override
	public String inheritFrom() {
		return "hunting-level4-" + mType.name().toLowerCase();
	}

	@Override
	public String nextLevelId() {
		return "hunting-level6-" + mType.name().toLowerCase();
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
		return getExtendedMobType().getCustomHead(5, 0);
	}

	@Override
	public MinecraftMob getExtendedMobType() {
		return mType;
	}
}
