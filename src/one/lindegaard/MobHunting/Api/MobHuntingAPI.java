package one.lindegaard.MobHunting.Api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.MobHuntingManager;
import one.lindegaard.MobHunting.rewards.HiddenRewardData;
import one.lindegaard.MobHunting.rewards.RewardManager;

public class MobHuntingAPI {

	MobHunting instance;

	/**
	 * Constructor for MobHuntingAPI
	 */
	public MobHuntingAPI() {
		this.instance = getMobHunting();
	}

	/**
	 * Gets the MobHunting Instance
	 * 
	 * @return Instance
	 */
	private MobHunting getMobHunting() {
		return MobHunting.getInstance();
	}

	/**
	 * Gets the MobHuntingManager
	 * 
	 * @return MobHuntingManger
	 */
	public static MobHuntingManager getMobHuntingManager() {
		return MobHunting.getMobHuntingManager();
	}

	/**
	 * Test if MobHunting is enabled for Player
	 * 
	 * @param player
	 * @return true if MobHunting is enabled for the player.
	 */
	public static boolean isMobHuntingEnabled(Player player) {
		return getMobHuntingManager().isHuntEnabled(player);
	}

	public static boolean isMobHuntingReward(ItemStack itemStack) {
		return HiddenRewardData.hasHiddenRewardData(itemStack);
	}

	public static boolean isMobHuntingReward(Block block) {
		return HiddenRewardData.hasHiddenRewardData(block);
	}

	public static boolean isBagOfGoldReward(ItemStack itemStack) {
		if (HiddenRewardData.hasHiddenRewardData(itemStack))
			return HiddenRewardData.getHiddenRewardData(itemStack).isBagOfGoldReward();
		return false;
	}

	public boolean isKilledHeadReward(ItemStack itemStack) {
		if (HiddenRewardData.hasHiddenRewardData(itemStack))
			return HiddenRewardData.getHiddenRewardData(itemStack).isKilledHeadReward();
		return false;
	}

	public boolean isKillerHeadReward(ItemStack itemStack) {
		if (HiddenRewardData.hasHiddenRewardData(itemStack))
			return HiddenRewardData.getHiddenRewardData(itemStack).isKillerHeadReward();
		return false;
	}

	public boolean isItemReward(ItemStack itemStack) {
		if (HiddenRewardData.hasHiddenRewardData(itemStack))
			return HiddenRewardData.getHiddenRewardData(itemStack).isItemReward();
		return false;
	}

}