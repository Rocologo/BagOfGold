package one.lindegaard.MobHunting.compatibility;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.disguise.Disguise;
import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

public class DisguiseCraftCompat implements Listener {

	// API
	// http://dev.bukkit.org/bukkit-plugins/disguisecraft/pages/developer-api/

	private static Plugin mPlugin;
	//private static DisguiseCraftAPI dcAPI;
	private static boolean supported = false;

	public DisguiseCraftCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance()
					.getLogger()
					.info("Compatibility with DisguiseCraft is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getServer().getPluginManager()
					.getPlugin("DisguiseCraft");
			if (mPlugin != null) {
				//dcAPI = DisguiseCraft.getAPI();

				Bukkit.getPluginManager().registerEvents(this,
						MobHunting.getInstance());

				MobHunting.getInstance().getLogger().info(
						"Enabling compatibility with DisguiseCraft ("
								+ getDisguiseCraft().getDescription()
										.getVersion() + ")");
				supported = true;
			}
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Plugin getDisguiseCraft() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationDisguiseCraft;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationDisguiseCraft;
	}

	public static boolean isDisguised(Player player) {
		return DisguiseCraft.getAPI().isDisguised(player);
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

	private static final DisguiseType aggresiveList[] = { DisguiseType.Blaze,
			DisguiseType.CaveSpider, DisguiseType.Creeper,
			DisguiseType.EnderDragon, DisguiseType.Enderman,
			DisguiseType.Endermite, DisguiseType.Ghast, DisguiseType.Giant,
			DisguiseType.Guardian, DisguiseType.PigZombie,
			DisguiseType.Skeleton, DisguiseType.Slime, DisguiseType.Spider,
			DisguiseType.Witch, DisguiseType.Wither, DisguiseType.Zombie };
			// TODO: Shulker not supported by Disguisecraft????
	
	public static final Set<DisguiseType> aggresiveMobs = new HashSet<DisguiseType>(
			Arrays.asList(aggresiveList));

	private static final DisguiseType passiveList[] = { DisguiseType.Bat,
			DisguiseType.Chicken, DisguiseType.Cow, DisguiseType.Horse,
			DisguiseType.IronGolem, DisguiseType.MagmaCube,
			DisguiseType.MushroomCow, DisguiseType.Ocelot, DisguiseType.Pig,
			DisguiseType.Rabbit, DisguiseType.Sheep, DisguiseType.Silverfish,
			DisguiseType.Snowman, DisguiseType.Squid };
	public static final Set<DisguiseType> passiveMobs = new HashSet<DisguiseType>(
			Arrays.asList(passiveList));

	private static final DisguiseType otherList[] = { DisguiseType.Boat,
			DisguiseType.EnderCrystal, DisguiseType.FallingBlock,
			DisguiseType.Minecart, DisguiseType.Player, DisguiseType.Villager,
			DisguiseType.Wolf };
	public static final Set<DisguiseType> otherDisguiseTypes = new HashSet<DisguiseType>(
			Arrays.asList(otherList));

	public static boolean isAggresiveDisguise(Entity entity) {
		Disguise d = getDisguise(entity);
		if (aggresiveMobs.contains(d.type))
			return true;
		else
			return false;
	}

	public static boolean isPlayerDisguise(Player player) {
		Disguise d = getDisguise(player);
		if (d.type.equals(DisguiseType.Player))
			return true;
		else
			return false;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
