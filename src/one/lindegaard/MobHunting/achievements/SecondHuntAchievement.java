package one.lindegaard.MobHunting.achievements;

import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MinecraftMob;

public class SecondHuntAchievement implements ProgressAchievement {
	private MinecraftMob mType;

	public SecondHuntAchievement(MinecraftMob entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.2.name", "mob", mType.getFriendlyName());
	}

	@Override
	public String getID() {
		return "hunting-level2-" + mType.name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.2.description", "count", getMaxProgress(), "mob",
				mType.getFriendlyName());
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialHunter2;
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
	public String nextLevelId() {
		return "hunting-level3-" + mType.name().toLowerCase();
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialHunter2Cmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialHunter2CmdDesc;
	}

	@Override
	public ItemStack getSymbol() {
		return getExtendedMobType().getCustomHead(mType.getDisplayName(), 2, 0);
	}

	@Override
	public MinecraftMob getExtendedMobType() {
		return mType;
	}
}
