package au.com.mineauz.MobHunting.compatability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class DisguisesHelper {

	// ***************************************************************************
	// Integration to LibsDisguises, DisguiseCraft, IDisguise
	// ***************************************************************************

	public static boolean isDisguised(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class)
				&& entity instanceof Player)
			return LibsDisguisesCompat.isDisguised((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class)
				&& entity instanceof Player)
			return DisguiseCraftCompat.isDisguised((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class)
				&& entity instanceof Player)
			return IDisguiseCompat.isDisguised(entity);
		else {
			return false;
		}
	}

	public static boolean isDisguisedAsAgresiveMob(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class)
				&& entity instanceof Player)
			return LibsDisguisesCompat.isAggresiveDisguise(entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class)
				&& entity instanceof Player)
			return DisguiseCraftCompat.isAggresiveDisguise(entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class)
				&& entity instanceof Player)
			return IDisguiseCompat.isAggresiveDisguise(entity);
		else {
			return false;
		}
	}

	public static boolean isDisguisedAsPlayer(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class)
				&& entity instanceof Player)
			return LibsDisguisesCompat.isPlayerDisguise((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class)
				&& entity instanceof Player)
			return DisguiseCraftCompat.isPlayerDisguise((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class)
				&& entity instanceof Player)
			return IDisguiseCompat.isPlayerDisguise((Player) entity);
		else {
			return false;
		}
	}

	public static void undisguiseEntity(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class))
			LibsDisguisesCompat.undisguiseEntity(entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class))
			DisguiseCraftCompat.undisguisePlayer(entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class))
			IDisguiseCompat.undisguisePlayer(entity);
		else {

		}
	}
}
