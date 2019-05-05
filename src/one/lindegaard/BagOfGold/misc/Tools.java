package one.lindegaard.BagOfGold.misc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import one.lindegaard.BagOfGold.BagOfGold;

public class Tools {

	public static String format(double money) {
		Locale locale = new Locale("en", "UK");
		String pattern = "0.#####";
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);
		return decimalFormat.format(money);
	}

	public static double round(double d) {
		return Math.round(d / BagOfGold.getInstance().getConfigManager().rewardRounding)
				* BagOfGold.getInstance().getConfigManager().rewardRounding;
	}

	public static double ceil(double d) {
		return Math.ceil(d / BagOfGold.getInstance().getConfigManager().rewardRounding)
				* BagOfGold.getInstance().getConfigManager().rewardRounding;
	}

	public static double floor(double d) {
		return Math.floor(d / BagOfGold.getInstance().getConfigManager().rewardRounding)
				* BagOfGold.getInstance().getConfigManager().rewardRounding;
	}

}
