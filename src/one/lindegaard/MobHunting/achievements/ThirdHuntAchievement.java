package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MinecraftMob;

public class ThirdHuntAchievement implements ProgressAchievement {
	private MinecraftMob mType;

	public ThirdHuntAchievement(MinecraftMob entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.3.name", "mob", mType.getFriendlyName()); 
	}

	@Override
	public String getID() {
		return "hunting-level3-" + mType.name().toLowerCase(); 
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.3.description", "count", getMaxProgress(), "mob",
				mType.getFriendlyName());
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialHunter3;
	}

	@Override
	public int getMaxProgress() {
		return mType.getMax() * 5;
	}

	@Override
	public String inheritFrom() {
		return "hunting-level2-" + mType.name().toLowerCase();
	}
	
	@Override
	public String nextLevelId() {
		return "hunting-level4-" + mType.name().toLowerCase();
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialHunter3Cmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialHunter3CmdDesc;
	}

	@Override
	public ItemStack getSymbol() {
		return new ItemStack(Material.GOLD_INGOT);
	}
	
	@Override
	public MinecraftMob getExtendedMobType() {
		return mType;
	}
}
