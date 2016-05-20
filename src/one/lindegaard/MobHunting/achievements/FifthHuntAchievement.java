package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.ExtendedMobType;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class FifthHuntAchievement implements ProgressAchievement {

	private ExtendedMobType mType;

	public FifthHuntAchievement(ExtendedMobType entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.5.name", "mob", mType.getName());
	}

	@Override
	public String getID() {
		return "hunting-level5-" + mType.name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.5.description", "count", getMaxProgress(), "mob",
				mType.getName());
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
		return new ItemStack(Material.DIAMOND);
	}

	@Override
	public ExtendedMobType getExtendedMobType() {
		return mType;
	}
}
