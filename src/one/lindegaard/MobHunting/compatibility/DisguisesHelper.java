package one.lindegaard.MobHunting.compatibility;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.MobHunting;

public class DisguisesHelper {

	// ***************************************************************************
	// Integration to LibsDisguises, DisguiseCraft, IDisguise
	// ***************************************************************************

	/**
	 * isDisguised - checks if the player is disguised.
	 * 
	 * @param entity
	 * @return true when the player is disguised and false when the is not.
	 */
	public static boolean isDisguised(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationLibsDisguises)
			return LibsDisguisesCompat.isDisguised((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationDisguiseCraft)
			return DisguiseCraftCompat.isDisguised((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationIDisguise)
			return IDisguiseCompat.isDisguised(entity);
		else {
			return false;
		}
	}

	/**
	 * isDisguisedAsAsresiveMob - checks if the player is disguised as a mob who
	 * attacks players.
	 * 
	 * @param entity
	 * @return true when the player is disguised as a mob who attacks players
	 *         and false when not.
	 */
	public static boolean isDisguisedAsAgresiveMob(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationLibsDisguises)
			return LibsDisguisesCompat.isAggresiveDisguise(entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationDisguiseCraft)
			return DisguiseCraftCompat.isAggresiveDisguise(entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationIDisguise)
			return IDisguiseCompat.isAggresiveDisguise(entity);
		else {
			return false;
		}
	}

	/**
	 * isDisguisedAsPlayer - checks if the player is disguised as another
	 * player.
	 * 
	 * @param entity
	 * @return true when the player is disguised as another player, and false
	 *         when not.
	 */
	public static boolean isDisguisedAsPlayer(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationLibsDisguises)
			return LibsDisguisesCompat.isPlayerDisguise((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationDisguiseCraft)
			return DisguiseCraftCompat.isPlayerDisguise((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class) && entity instanceof Player
				&& !MobHunting.getConfigManager().disableIntegrationIDisguise)
			return IDisguiseCompat.isPlayerDisguise((Player) entity);
		else {
			return false;
		}
	}

	public static void undisguiseEntity(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class)
				&& !MobHunting.getConfigManager().disableIntegrationLibsDisguises)
			LibsDisguisesCompat.undisguiseEntity(entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class)
				&& !MobHunting.getConfigManager().disableIntegrationDisguiseCraft)
			DisguiseCraftCompat.undisguisePlayer(entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class)
				&& !MobHunting.getConfigManager().disableIntegrationIDisguise)
			IDisguiseCompat.undisguisePlayer(entity);
		else {

		}
	}
}
