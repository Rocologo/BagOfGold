package one.lindegaard.MobHunting.Api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.MobHuntingManager;
import one.lindegaard.MobHunting.rewards.Reward;
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
		return Reward.isReward(itemStack);
	}

	public static boolean isMobHuntingReward(Block block) {
		return Reward.hasReward(block);
	}

	public static boolean isBagOfGoldReward(ItemStack itemStack) {
		if (Reward.isReward(itemStack))
			return Reward.getReward(itemStack).isBagOfGoldReward();
		return false;
	}

	public static boolean isKilledHeadReward(ItemStack itemStack) {
		if (Reward.isReward(itemStack))
			return Reward.getReward(itemStack).isKilledHeadReward();
		return false;
	}

	public static boolean isKillerHeadReward(ItemStack itemStack) {
		if (Reward.isReward(itemStack))
			return Reward.getReward(itemStack).isKillerHeadReward();
		return false;
	}

	public static boolean isItemReward(ItemStack itemStack) {
		if (Reward.isReward(itemStack))
			return Reward.getReward(itemStack).isItemReward();
		return false;
	}

}