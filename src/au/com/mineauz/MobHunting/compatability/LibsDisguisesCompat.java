package au.com.mineauz.MobHunting.compatability;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.events.DisguiseEvent;
import me.libraryaddict.disguise.events.UndisguiseEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import au.com.mineauz.MobHunting.MobHunting;

public class LibsDisguisesCompat implements Listener {

	// API
	// https://www.spigotmc.org/wiki/lib-s-disguises/

	private static Plugin mPlugin;

	public LibsDisguisesCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance
					.getLogger()
					.info("Compatability with LibsDisguises is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getServer().getPluginManager()
					.getPlugin("LibsDisguises");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

			MobHunting.instance.getLogger().info(
					"Enabling compatability with LibsDisguises ("
							+ getLibsDisguises().getDescription().getVersion()
							+ ")");
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getLibsDisguises() {
		return mPlugin;
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

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationLibsDisguises;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationLibsDisguises;
	}

	public static boolean isDisguised(Entity entity) {
		return DisguiseAPI.isDisguised(entity);
	}

	private static final DisguiseType aggresiveList[] = { DisguiseType.ZOMBIE,
			DisguiseType.BLAZE, DisguiseType.CAVE_SPIDER, DisguiseType.CREEPER,
			DisguiseType.ELDER_GUARDIAN, DisguiseType.ENDER_DRAGON,
			DisguiseType.ENDERMAN, DisguiseType.ENDERMITE, DisguiseType.GHAST,
			DisguiseType.GIANT, DisguiseType.GUARDIAN, DisguiseType.PIG_ZOMBIE,
			DisguiseType.SKELETON, DisguiseType.SLIME, DisguiseType.SPIDER,
			DisguiseType.WITCH, DisguiseType.WITHER,
			DisguiseType.WITHER_SKELETON, DisguiseType.WITHER_SKULL,
			DisguiseType.ZOMBIE, DisguiseType.ZOMBIE_VILLAGER };
	public static final Set<DisguiseType> aggresiveMobs = new HashSet<DisguiseType>(
			Arrays.asList(aggresiveList));

	private static final DisguiseType passiveList[] = { DisguiseType.BAT,
			DisguiseType.CHICKEN, DisguiseType.COW, DisguiseType.DONKEY,
			DisguiseType.HORSE, DisguiseType.IRON_GOLEM,
			DisguiseType.MAGMA_CUBE, DisguiseType.MULE,
			DisguiseType.MUSHROOM_COW, DisguiseType.OCELOT, DisguiseType.PIG,
			DisguiseType.RABBIT, DisguiseType.SHEEP, DisguiseType.SILVERFISH,
			DisguiseType.SKELETON_HORSE, DisguiseType.SNOWMAN,
			DisguiseType.SQUID, DisguiseType.UNDEAD_HORSE };
	public static final Set<DisguiseType> passiveMobs = new HashSet<DisguiseType>(
			Arrays.asList(passiveList));

	private static final DisguiseType otherList[] = { DisguiseType.ARMOR_STAND,
			DisguiseType.ARROW, DisguiseType.BOAT, DisguiseType.DROPPED_ITEM,
			DisguiseType.EGG, DisguiseType.ENDER_CRYSTAL,
			DisguiseType.ENDER_PEARL, DisguiseType.ENDER_SIGNAL,
			DisguiseType.EXPERIENCE_ORB, DisguiseType.FALLING_BLOCK,
			DisguiseType.FIREBALL, DisguiseType.FIREWORK,
			DisguiseType.FISHING_HOOK, DisguiseType.ITEM_FRAME,
			DisguiseType.LEASH_HITCH, DisguiseType.MINECART,
			DisguiseType.MINECART_CHEST, DisguiseType.MINECART_COMMAND,
			DisguiseType.MINECART_FURNACE, DisguiseType.MINECART_HOPPER,
			DisguiseType.MINECART_MOB_SPAWNER, DisguiseType.MINECART_TNT,
			DisguiseType.PAINTING, DisguiseType.PLAYER,
			DisguiseType.PRIMED_TNT, DisguiseType.SMALL_FIREBALL,
			DisguiseType.SNOWBALL, DisguiseType.SPLASH_POTION,
			DisguiseType.THROWN_EXP_BOTTLE, DisguiseType.VILLAGER,
			DisguiseType.WOLF };
	public static final Set<DisguiseType> otherDisguiseTypes = new HashSet<DisguiseType>(
			Arrays.asList(otherList));

	public static boolean isAggresiveDisguise(Entity entity) {
		if (aggresiveMobs.contains(DisguiseAPI.getDisguise(entity).getType()))
			return true;
		else
			return false;
	}

	public static boolean isPlayerDisguise(Entity entity) {
		
		if (DisguiseAPI.getDisguise(entity).equals(DisguiseType.PLAYER))
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
