package one.lindegaard.BagOfGold.util;

import one.lindegaard.BagOfGold.BagOfGold;

public class Misc {
	
	public static double round(double d) {
		return Math.round(d / BagOfGold.getConfigManager().rewardRounding)
				* BagOfGold.getConfigManager().rewardRounding;
	}

	public static double ceil(double d) {
		return Math.ceil(d / BagOfGold.getConfigManager().rewardRounding)
				* BagOfGold.getConfigManager().rewardRounding;
	}

	public static double floor(double d) {
		return Math.floor(d / BagOfGold.getConfigManager().rewardRounding)
				* BagOfGold.getConfigManager().rewardRounding;
	}

}
