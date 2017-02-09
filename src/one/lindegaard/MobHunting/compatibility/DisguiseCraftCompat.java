package one.lindegaard.MobHunting.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.util.Misc;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class DisguiseCraftCompat implements Listener {

	// API
	// http://dev.bukkit.org/bukkit-plugins/disguisecraft/pages/developer-api/

	private static Plugin mPlugin;
	// private static DisguiseCraftAPI dcAPI;
	private static boolean supported = false;

	public DisguiseCraftCompat() {
		if (MobHunting.getConfigManager().disableIntegrationDisguiseCraft) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with DisguiseCraft is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getServer().getPluginManager().getPlugin("DisguiseCraft");
			if (mPlugin.getDescription().getVersion().compareTo("5.0") >= 0) {
				Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

				Bukkit.getLogger().info("[MobHunting] Enabling compatibility with DisguiseCraft ("
						+ getDisguiseCraft().getDescription().getVersion() + ")");
				supported = true;

			} else {
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				console.sendMessage(
						ChatColor.RED + "[MobHunting] Your version (" + mPlugin.getDescription().getVersion()
								+ ") of DisguisCraft is too old and not supported by MobHunting.");
			}
		}
	}

	private static DisguiseType[] aggresiveList = new DisguiseType[20];
	private static DisguiseType[] passiveList = new DisguiseType[20];
	private static DisguiseType[] otherList = new DisguiseType[10];

	static {
		int n = 0;
		if (Misc.isMC111OrNewer()) {
			// Not supported by Disguisecraft
			// aggresiveList[n++] = DisguiseType.VEX;
			// aggresiveList[n++] = DisguiseType.EVOKER;
			// aggresiveList[n++] = DisguiseType.VINDICATOR;
		}
		if (Misc.isMC111OrNewer()) {
			// Not supported by Disguisecraft
			// aggresiveList[n++] = DisguiseType.HUSK;
			// aggresiveList[n++] = DisguiseType.STRAY;
		}
		if (Misc.isMC19OrNewer()) {
			// Not supported by Disguisecraft
			// aggresiveList[n++] = DisguiseType.SHULKER;
		}
		if (Misc.isMC18OrNewer()) {
			aggresiveList[n++] = DisguiseType.Guardian;
			aggresiveList[n++] = DisguiseType.Endermite;
			// aggresiveList[n++] = DisguiseType.ELDER_GUARDIAN;
		}
		aggresiveList[n++] = DisguiseType.Zombie;
		aggresiveList[n++] = DisguiseType.Blaze;
		aggresiveList[n++] = DisguiseType.CaveSpider;
		aggresiveList[n++] = DisguiseType.Creeper;
		aggresiveList[n++] = DisguiseType.EnderDragon;
		aggresiveList[n++] = DisguiseType.Enderman;
		aggresiveList[n++] = DisguiseType.Ghast;
		aggresiveList[n++] = DisguiseType.Giant;
		aggresiveList[n++] = DisguiseType.PigZombie;
		aggresiveList[n++] = DisguiseType.Skeleton;
		aggresiveList[n++] = DisguiseType.Slime;
		aggresiveList[n++] = DisguiseType.Spider;
		aggresiveList[n++] = DisguiseType.Witch;
		aggresiveList[n++] = DisguiseType.Wither;
		// aggresiveList[n++] = DisguiseType.WITHER_SKELETON;
		// aggresiveList[n++] = DisguiseType.WITHER_SKULL;
		// aggresiveList[n++] = DisguiseType.ZOMBIE_VILLAGER;
	}
	private static Set<DisguiseType> aggresiveMobs = new HashSet<DisguiseType>(Arrays.asList(aggresiveList));

	static {
		int n2 = 0;
		if (Misc.isMC111OrNewer()) {
			// passiveList[n2++] = DisguiseType.LLAMA;
		}
		if (Misc.isMC110OrNewer()) {
			// passiveList[n2++] = DisguiseType.POLAR_BEAR;
		}
		passiveList[n2++] = DisguiseType.Bat;
		passiveList[n2++] = DisguiseType.Chicken;
		passiveList[n2++] = DisguiseType.Cow;
		// passiveList[n2++] = DisguiseType.DONKEY;
		passiveList[n2++] = DisguiseType.Horse;
		passiveList[n2++] = DisguiseType.IronGolem;
		passiveList[n2++] = DisguiseType.MagmaCube;
		// passiveList[n2++] = DisguiseType.MULE;
		passiveList[n2++] = DisguiseType.MushroomCow;
		passiveList[n2++] = DisguiseType.Ocelot;
		passiveList[n2++] = DisguiseType.Pig;
		passiveList[n2++] = DisguiseType.Rabbit;
		passiveList[n2++] = DisguiseType.Sheep;
		passiveList[n2++] = DisguiseType.Silverfish;
		// passiveList[n2++] = DisguiseType.SKELETON_HORSE;
		passiveList[n2++] = DisguiseType.Snowman;
		passiveList[n2++] = DisguiseType.Squid;
	}
	private static Set<DisguiseType> passiveMobs = new HashSet<DisguiseType>(Arrays.asList(passiveList));

	static {
		int n3 = 0;
		otherList[n3++] = DisguiseType.TNTPrimed;
		otherList[n3++] = DisguiseType.Boat;
		otherList[n3++] = DisguiseType.EnderCrystal;
		otherList[n3++] = DisguiseType.FallingBlock;
		otherList[n3++] = DisguiseType.Minecart;
		otherList[n3++] = DisguiseType.Player;
		otherList[n3++] = DisguiseType.Villager;
		otherList[n3++] = DisguiseType.Wolf;
	}
	public static final Set<DisguiseType> otherDisguiseTypes = new HashSet<DisguiseType>(Arrays.asList(otherList));

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getDisguiseCraft() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisguised(Player player) {
		if (isSupported())
			return DisguiseCraft.getAPI().isDisguised(player);
		return false;
	}

	public static Disguise getDisguise(Entity entiry) {
		return DisguiseCraft.getAPI().getDisguise((Player) entiry);
	}

	public static void disguisePlayer(Player player, Disguise disguise) {
		DisguiseCraft.getAPI().disguisePlayer(player, disguise);
	}

	// public static void changePlayerDisguise(Player player, Disguise
	// newDisguise) {
	// dcAPI.changePlayerDisguise(player, newDisguise);
	// }

	public static void undisguisePlayer(Entity entity) {
		if (entity instanceof Player)
			DisguiseCraft.getAPI().undisguisePlayer((Player) entity);
	}

	public static boolean isAggresiveDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (aggresiveMobs.contains(d.type))
			return true;
		return false;
	}

	public static boolean isPassiveDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (passiveMobs.contains(d.type))
			return true;
		return false;
	}

	public static boolean isOtherDisguiseTypes(Entity entity) {
		Disguise d = getDisguise(entity);
		if (otherDisguiseTypes.contains(d.type))
			return true;
		return false;
	}

	public static boolean isPlayerDisguise(Player player) {
		Disguise d = getDisguise(player);
		if (d.type.equals(DisguiseType.Player))
			return true;
		return false;
	}

}
