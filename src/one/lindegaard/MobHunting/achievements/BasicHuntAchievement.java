package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.ExtendedMobType;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class BasicHuntAchievement implements ProgressAchievement {
	private ExtendedMobType mType;

	public BasicHuntAchievement(ExtendedMobType entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.1.name", "mob",
				mType.getName());
	}

	@Override
	public String getID() {
		return "hunting-level1-" + mType.name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.1.description", "count",
				getMaxProgress(), "mob", mType.getName());
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialHunter1;
	}

	@Override
	public int getMaxProgress() {
		return mType.getMax();
	}

	@Override
	public String inheritFrom() {
		return null;
	}
	
	@Override
	public String nextLevelId() {
		return "hunting-level2-" + mType.name().toLowerCase();
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
		return new ItemStack(Material.COAL);
	}
}
