package one.lindegaard.MobHunting.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.robingrether.idisguise.api.*;
import de.robingrether.idisguise.disguise.*;
import de.robingrether.idisguise.disguise.DisguiseType;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.util.Misc;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class IDisguiseCompat implements Listener {

	// API
	// http://dev.bukkit.org/bukkit-plugins/idisguise/pages/api/

	private static Plugin mPlugin;
	private static DisguiseAPI api;
	private static boolean supported = false;

	private static DisguiseType[] aggresiveList = new DisguiseType[30];
	private static DisguiseType[] passiveList = new DisguiseType[20];
	private static DisguiseType[] otherList = new DisguiseType[10];

	static {
		int n = 0;
		if (Misc.isMC111OrNewer()) {
			aggresiveList[n++] = DisguiseType.VEX;
			aggresiveList[n++] = DisguiseType.EVOKER;
			aggresiveList[n++] = DisguiseType.VINDICATOR;
		}
		if (Misc.isMC111OrNewer()) {
			aggresiveList[n++] = DisguiseType.HUSK;
			aggresiveList[n++] = DisguiseType.STRAY;
		}
		if (Misc.isMC19OrNewer()) {
			aggresiveList[n++] = DisguiseType.SHULKER;
		}
		if (Misc.isMC18OrNewer()){
			aggresiveList[n++] = DisguiseType.GUARDIAN;
			aggresiveList[n++] = DisguiseType.ENDERMITE;
			aggresiveList[n++] = DisguiseType.ELDER_GUARDIAN;
		}
		aggresiveList[n++] = DisguiseType.BLAZE;
		aggresiveList[n++] = DisguiseType.CAVE_SPIDER;
		aggresiveList[n++] = DisguiseType.CREEPER;
		aggresiveList[n++] = DisguiseType.ENDER_DRAGON;
		aggresiveList[n++] = DisguiseType.ENDERMAN;
		aggresiveList[n++] = DisguiseType.GHAST;
		aggresiveList[n++] = DisguiseType.GIANT;
		aggresiveList[n++] = DisguiseType.PIG_ZOMBIE;
		aggresiveList[n++] = DisguiseType.SKELETON;
		aggresiveList[n++] = DisguiseType.SLIME;
		aggresiveList[n++] = DisguiseType.SPIDER;
		aggresiveList[n++] = DisguiseType.WITCH;
		aggresiveList[n++] = DisguiseType.WITHER;
		aggresiveList[n++] = DisguiseType.WITHER_SKELETON;
		aggresiveList[n++] = DisguiseType.ZOMBIE;
		aggresiveList[n++] = DisguiseType.ZOMBIE_VILLAGER;
		aggresiveList[n++] = DisguiseType.SHULKER;
	}
	private static final Set<DisguiseType> aggresiveMobs = new HashSet<DisguiseType>(Arrays.asList(aggresiveList));

	static {
		int n2 = 0;
		if (Misc.isMC111OrNewer()) {
			passiveList[n2++] = DisguiseType.LLAMA;
		}
		if (Misc.isMC110OrNewer()) {
			passiveList[n2++] = DisguiseType.POLAR_BEAR;
		}
		passiveList[n2++] = DisguiseType.BAT;
		passiveList[n2++] = DisguiseType.CHICKEN;
		passiveList[n2++] = DisguiseType.COW;
		passiveList[n2++] = DisguiseType.DONKEY;
		passiveList[n2++] = DisguiseType.HORSE;
		passiveList[n2++] = DisguiseType.IRON_GOLEM;
		passiveList[n2++] = DisguiseType.MAGMA_CUBE;
		passiveList[n2++] = DisguiseType.MULE;
		passiveList[n2++] = DisguiseType.MUSHROOM_COW;
		passiveList[n2++] = DisguiseType.OCELOT;
		passiveList[n2++] = DisguiseType.PIG;
		passiveList[n2++] = DisguiseType.RABBIT;
		passiveList[n2++] = DisguiseType.SHEEP;
		passiveList[n2++] = DisguiseType.SILVERFISH;
		passiveList[n2++] = DisguiseType.SNOWMAN;
		passiveList[n2++] = DisguiseType.SQUID;
	}
	private static final Set<DisguiseType> passiveMobs = new HashSet<DisguiseType>(Arrays.asList(passiveList));

	static {
		int n3 = 0;
		otherList[n3++] = DisguiseType.ARMOR_STAND;
		otherList[n3++] = DisguiseType.BOAT;
		otherList[n3++] = DisguiseType.ENDER_CRYSTAL;
		otherList[n3++] = DisguiseType.FALLING_BLOCK;
		otherList[n3++] = DisguiseType.MINECART;
		otherList[n3++] = DisguiseType.PLAYER;
		otherList[n3++] = DisguiseType.VILLAGER;
		otherList[n3++] = DisguiseType.WOLF;
	}
	private static final Set<DisguiseType> otherDisguiseTypes = new HashSet<DisguiseType>(Arrays.asList(otherList));

	public IDisguiseCompat() {
		if (MobHunting.getConfigManager().disableIntegrationIDisguise) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with iDisguise is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getServer().getPluginManager().getPlugin(CompatPlugin.iDisguise.getName());
			api = Bukkit.getServicesManager().getRegistration(DisguiseAPI.class).getProvider();

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with iDisguise ("
					+ getiDisguise().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getiDisguise() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisguised(Entity entity) {
		if (entity instanceof Player)
			return api.isDisguised((OfflinePlayer) entity);
		else
			return false;
	}

	public static Disguise getDisguise(Entity entity) {
		if (entity instanceof Player)
			return api.getDisguise((OfflinePlayer) entity);
		else
			return null;
	}

	public static void disguisePlayer(Player player, Disguise disguise) {
		api.disguise(player, disguise);
	}

	public static void undisguisePlayer(Entity entity) {
		if (entity instanceof Player)
			api.undisguise((Player) entity);
	}

	public static boolean isAggresiveDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (aggresiveMobs.contains(d.getType()))
			return true;
		else
			return false;
	}

	public static boolean isPassiveDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (passiveMobs.contains(d.getType()))
			return true;
		else
			return false;
	}

	public static boolean isOtherDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (otherDisguiseTypes.contains(d.getType()))
			return true;
		else
			return false;
	}

	public static boolean isPlayerDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (d.getType().equals(DisguiseType.PLAYER))
			return true;
		else
			return false;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
