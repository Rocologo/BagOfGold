package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.ExtendedMobType;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class SixthHuntAchievement implements ProgressAchievement {

	private ExtendedMobType mType;

	public SixthHuntAchievement(ExtendedMobType entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.6.name", "mob", mType.getFriendlyName());
	}

	@Override
	public String getID() {
		return "hunting-level6-" + mType.name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.6.description", "count", getMaxProgress(), "mob",
				mType.getFriendlyName());
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialHunter6;
	}

	@Override
	public int getMaxProgress() {
		return mType.getMax() * 50;
	}

	@Override
	public String inheritFrom() {
		return "hunting-level5-" + mType.name().toLowerCase(); 
	}
	
	@Override
	public String nextLevelId() {
		return "hunting-level7-" + mType.name().toLowerCase();
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialHunter6Cmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialHunter6CmdDesc;
	}

	@Override
	public ItemStack getSymbol() {
		return new ItemStack(Material.ENDER_PEARL);
	}
	
	@Override
	public ExtendedMobType getExtendedMobType() {
		return mType;
	}
}
