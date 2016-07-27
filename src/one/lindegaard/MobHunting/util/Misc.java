package one.lindegaard.MobHunting.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class Misc {
	public static boolean isAxe(ItemStack item) {
		return (item.getType() == Material.DIAMOND_AXE || item.getType() == Material.GOLD_AXE
				|| item.getType() == Material.IRON_AXE || item.getType() == Material.STONE_AXE
				|| item.getType() == Material.WOOD_AXE);
	}

	public static boolean isSword(ItemStack item) {
		return (item.getType() == Material.DIAMOND_SWORD || item.getType() == Material.GOLD_SWORD
				|| item.getType() == Material.IRON_SWORD || item.getType() == Material.STONE_SWORD
				|| item.getType() == Material.WOOD_SWORD);
	}

	public static boolean isPick(ItemStack item) {
		return (item.getType() == Material.DIAMOND_PICKAXE || item.getType() == Material.GOLD_PICKAXE
				|| item.getType() == Material.IRON_PICKAXE || item.getType() == Material.STONE_PICKAXE
				|| item.getType() == Material.WOOD_PICKAXE);
	}

	public static double handleKillstreak(Player player) {
		HuntData data = MobHunting.getMobHuntingManager().getHuntData(player);

		// Killstreak can be disabled by setting the multiplier to 1
		double multiplier = data.getKillstreakMultiplier();
		if (multiplier != 1) {
			
			int lastKillstreakLevel = data.getKillstreakLevel();

			data.setKillStreak(data.getKillStreak() + 1);

			// Give a message notifying of killstreak increase
			if (data.getKillstreakLevel() != lastKillstreakLevel) {
				switch (data.getKillstreakLevel()) {
				case 1:
					Messages.playerActionBarMessage(player,
							ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.1"));
					break;
				case 2:
					Messages.playerActionBarMessage(player,
							ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.2"));
					break;
				case 3:
					Messages.playerActionBarMessage(player,
							ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.3"));
					break;
				default:
					Messages.playerActionBarMessage(player,
							ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.4"));
					break;
				}

				Messages.playerActionBarMessage(player, ChatColor.GRAY + Messages
						.getString("mobhunting.killstreak.activated", "multiplier", String.format("%.1f", multiplier)));
			}
		}

		return multiplier;
	}

	public static Map<String, Object> toMap(Location loc) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("X", loc.getX());
		map.put("Y", loc.getY());
		map.put("Z", loc.getZ());

		map.put("Yaw", (double) loc.getYaw());
		map.put("Pitch", (double) loc.getPitch());

		if (loc.getWorld() != null)
			map.put("W", loc.getWorld().getUID().toString());

		return map;
	}

	public static Location fromMap(Map<String, Object> map) {
		double x, y, z;
		float yaw, pitch;
		UUID world;

		x = (Double) map.get("X");
		y = (Double) map.get("Y");
		z = (Double) map.get("Z");

		yaw = (float) (double) (Double) map.get("Yaw");
		pitch = (float) (double) (Double) map.get("Pitch");

		if (map.containsKey("W")) {
			world = UUID.fromString((String) map.get("W"));
			return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
		} else
			return new Location(null, x, y, z, yaw, pitch);
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

	public static ItemStack getPlayerHead(OfflinePlayer offlinePlayer) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
		skull.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(offlinePlayer.getName());
		skull.setItemMeta(skullMeta);
		return skull;
	}

	public static Player getOnlinePlayer(OfflinePlayer offlinePlayer) {
		for (Player player : MobHunting.getMobHuntingManager().getOnlinePlayers()) {
			if (player.getName().equals(offlinePlayer.getName()))
				return player;
		}
		return null;
	}

	public static String trimSignText(String string) {
		return string.length() > 15 ? string.substring(0, 14).trim() : string;
	}

	public static double round(double d) {
		return Math.round(d / MobHunting.getConfigManager().rewardRounding)
				* MobHunting.getConfigManager().rewardRounding;
	}
}
