package one.lindegaard.MobHunting.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.util.Misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class LibsDisguisesCompat implements Listener {

	// API
	// https://www.spigotmc.org/wiki/lib-s-disguises/

	private static Plugin mPlugin;
	private static boolean supported = false;

	private static DisguiseType[] aggresiveList = new DisguiseType[30];
	private static DisguiseType[] passiveList = new DisguiseType[20];
	private static DisguiseType[] otherList = new DisguiseType[40];

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
		aggresiveList[n++] = DisguiseType.ZOMBIE;
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
		aggresiveList[n++] = DisguiseType.WITHER_SKULL;
		aggresiveList[n++] = DisguiseType.ZOMBIE_VILLAGER;
	}
	private static Set<DisguiseType> aggresiveMobs = new HashSet<DisguiseType>(Arrays.asList(aggresiveList));

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
		passiveList[n2++] = DisguiseType.SKELETON_HORSE;
		passiveList[n2++] = DisguiseType.SNOWMAN;
		passiveList[n2++] = DisguiseType.SQUID;
	}
	private static Set<DisguiseType> passiveMobs = new HashSet<DisguiseType>(Arrays.asList(passiveList));

	static {
		int n3 = 0;
		otherList[n3++] = DisguiseType.ARMOR_STAND;
		otherList[n3++] = DisguiseType.ARROW;
		otherList[n3++] = DisguiseType.BOAT;
		otherList[n3++] = DisguiseType.DROPPED_ITEM;
		otherList[n3++] = DisguiseType.EGG;
		otherList[n3++] = DisguiseType.ENDER_CRYSTAL;
		otherList[n3++] = DisguiseType.ENDER_PEARL;
		otherList[n3++] = DisguiseType.ENDER_SIGNAL;
		otherList[n3++] = DisguiseType.EXPERIENCE_ORB;
		otherList[n3++] = DisguiseType.FALLING_BLOCK;
		otherList[n3++] = DisguiseType.FIREBALL;
		otherList[n3++] = DisguiseType.FIREWORK;
		otherList[n3++] = DisguiseType.FISHING_HOOK;
		otherList[n3++] = DisguiseType.ITEM_FRAME;
		otherList[n3++] = DisguiseType.LEASH_HITCH;
		otherList[n3++] = DisguiseType.MINECART;
		otherList[n3++] = DisguiseType.MINECART_CHEST;
		otherList[n3++] = DisguiseType.MINECART_COMMAND;
		otherList[n3++] = DisguiseType.MINECART_FURNACE;
		otherList[n3++] = DisguiseType.MINECART_HOPPER;
		otherList[n3++] = DisguiseType.MINECART_MOB_SPAWNER;
		otherList[n3++] = DisguiseType.MINECART_TNT;
		otherList[n3++] = DisguiseType.PAINTING;
		otherList[n3++] = DisguiseType.PLAYER;
		otherList[n3++] = DisguiseType.PRIMED_TNT;
		otherList[n3++] = DisguiseType.SMALL_FIREBALL;
		otherList[n3++] = DisguiseType.SNOWBALL;
		otherList[n3++] = DisguiseType.SPLASH_POTION;
		otherList[n3++] = DisguiseType.THROWN_EXP_BOTTLE;
		otherList[n3++] = DisguiseType.VILLAGER;
		otherList[n3++] = DisguiseType.WOLF;
	}
	private static Set<DisguiseType> otherDisguiseTypes = new HashSet<DisguiseType>(Arrays.asList(otherList));

	public LibsDisguisesCompat() {
		if (MobHunting.getConfigManager().disableIntegrationLibsDisguises) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with LibsDisguises is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getServer().getPluginManager().getPlugin("LibsDisguises");

			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with LibsDisguises ("
					+ getLibsDisguises().getDescription().getVersion() + ")");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getLibsDisguises() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static me.libraryaddict.disguise.disguisetypes.Disguise getDisguise(Entity entity) {
		return DisguiseAPI.getDisguise(entity);
	}

	public static void disguisePlayer(Entity entity, me.libraryaddict.disguise.disguisetypes.Disguise disguise) {
		DisguiseAPI.disguiseToAll(entity, disguise);
	}

	public static void undisguiseEntity(Entity entity) {
		DisguiseAPI.undisguiseToAll(entity);
	}

	public static boolean isDisguised(Entity entity) {
		return DisguiseAPI.isDisguised(entity);
	}

	public static boolean isAggresiveDisguise(Entity entity) {
		if (aggresiveMobs.contains(DisguiseAPI.getDisguise(entity).getType()))
			return true;
		else
			return false;
	}

	public static boolean isPassiveDisguise(Entity entity) {
		if (passiveMobs.contains(DisguiseAPI.getDisguise(entity).getType()))
			return true;
		else
			return false;
	}

	public static boolean isOtherDisguise(Entity entity) {
		if (otherDisguiseTypes.contains(DisguiseAPI.getDisguise(entity).getType()))
			return true;
		else
			return false;
	}

	public static boolean isPlayerDisguise(Entity entity) {

		if (DisguiseAPI.getDisguise(entity).getType().equals(DisguiseType.PLAYER))
			return true;
		else
			return false;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	// DisguiseEvent

	// UndisguiseEvent

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDisguiseEvent(final DisguiseEvent event) {

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onUndisguiseEvent(final UndisguiseEvent event) {

	}
}
