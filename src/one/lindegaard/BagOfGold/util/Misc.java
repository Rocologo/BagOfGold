package one.lindegaard.BagOfGold.util;

import one.lindegaard.MobHunting.MobHunting;

public class Misc {

	public static String trimSignText(String string) {
		return string.length() > 15 ? string.substring(0, 14).trim() : string;
	}

	public static double round(double d) {
		return Math.round(d / MobHunting.getInstance().getConfigManager().rewardRounding)
				* MobHunting.getInstance().getConfigManager().rewardRounding;
	}

	public static double ceil(double d) {
		return Math.ceil(d / MobHunting.getInstance().getConfigManager().rewardRounding)
				* MobHunting.getInstance().getConfigManager().rewardRounding;
	}

	public static double floor(double d) {
		return Math.floor(d / MobHunting.getInstance().getConfigManager().rewardRounding)
				* MobHunting.getInstance().getConfigManager().rewardRounding;
	}

}
