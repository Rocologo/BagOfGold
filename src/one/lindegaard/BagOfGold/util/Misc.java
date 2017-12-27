package one.lindegaard.BagOfGold.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import one.lindegaard.BagOfGold.BagOfGold;

public class Misc {

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

	// *******************************************************************
	// Version detection
	// *******************************************************************
	public static boolean isMC112() {
		return Bukkit.getBukkitVersion().contains("1.12");
	}

	public static boolean isMC111() {
		return Bukkit.getBukkitVersion().contains("1.11");
	}

	public static boolean isMC110() {
		return Bukkit.getBukkitVersion().contains("1.10");
	}

	public static boolean isMC19() {
		return Bukkit.getBukkitVersion().contains("1.9");
	}

	public static boolean isMC18() {
		return Bukkit.getBukkitVersion().contains("1.8");
	}

	public static boolean isMC17() {
		return Bukkit.getBukkitVersion().contains("1.7");
	}

	public static boolean isMC112OrNewer() {
		if (isMC112())
			return true;
		else if (isMC111() || isMC110() || isMC19() || isMC18() || isMC17())
			return false;
		return true;
	}

	public static boolean isMC111OrNewer() {
		if (isMC111())
			return true;
		else if (isMC110() || isMC19() || isMC18() || isMC17())
			return false;
		return true;
	}

	public static boolean isMC110OrNewer() {
		if (isMC110())
			return true;
		else if (isMC19() || isMC18() || isMC17())
			return false;
		return true;
	}

	public static boolean isMC19OrNewer() {
		if (isMC19())
			return true;
		else if (isMC18() || isMC17())
			return false;
		return true;
	}

	public static boolean isMC18OrNewer() {
		if (isMC18())
			return true;
		else if (isMC17())
			return false;
		return true;
	}

	// *******************************************************************
	// Version detection
	// *******************************************************************
	public static boolean isGlowstoneServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("Glowstone");
	}

	public static boolean isSpigotServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("CraftBukkit")
				&& Bukkit.getServer().getVersion().toLowerCase().contains("spigot");
	}

	public static boolean isCraftBukkitServer() {
		return Bukkit.getServer().getName().equalsIgnoreCase("CraftBukkit")
				&& Bukkit.getServer().getVersion().toLowerCase().contains("bukkit");
	}

	public static String format(double money) {
		Locale locale = new Locale("en", "UK");
		String pattern = "#.#####";
		DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
		decimalFormat.applyPattern(pattern);
		return decimalFormat.format(money);
	}

	public static boolean isSign(Block block) {
		if (block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN))
			return true;
		else
			return false;
	}

	public static boolean isSign(Material material) {
		if (material.equals(Material.SIGN_POST) || material.equals(Material.WALL_SIGN))
			return true;
		else
			return false;
	}

	public static final Block getTargetBlock(Player player, int range) {
		BlockIterator iter = new BlockIterator(player, range);
		Block lastBlock = iter.next();
		while (iter.hasNext()) {
			lastBlock = iter.next();
			if (lastBlock.getType() == Material.AIR) {
				continue;
			}
			break;
		}
		return lastBlock;
	}

}
