package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.ExtendedMobType;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class SeventhHuntAchievement implements ProgressAchievement {

	private ExtendedMobType mType;

	public SeventhHuntAchievement(ExtendedMobType entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.7.name", "mob", mType.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public String getID() {
		return "hunting-level7-" + mType.name().toLowerCase(); //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.7.description", "count", getMaxProgress(), "mob",
				mType.getName());
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialHunter7;
	}

	@Override
	public int getMaxProgress() {
		return mType.getMax() * 100;
	}

	@Override
	public String inheritFrom() {
		return "hunting-level6-" + mType.name().toLowerCase();
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialHunter7Cmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialHunter7CmdDesc;
	}

	@Override
	public ItemStack getSymbol() {
		return new ItemStack(Material.DIAMOND_BLOCK);
	}
}
