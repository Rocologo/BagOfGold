package au.com.mineauz.MobHunting.compatability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class)
				&& entity instanceof Player
				&& !LibsDisguisesCompat.isDisabledInConfig())
			return LibsDisguisesCompat.isDisguised((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class)
				&& entity instanceof Player
				&& !DisguiseCraftCompat.isDisabledInConfig())
			return DisguiseCraftCompat.isDisguised((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class)
				&& entity instanceof Player
				&& !IDisguiseCompat.isDisabledInConfig())
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
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class)
				&& entity instanceof Player
				&& !LibsDisguisesCompat.isDisabledInConfig())
			return LibsDisguisesCompat.isAggresiveDisguise(entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class)
				&& entity instanceof Player
				&& !DisguiseCraftCompat.isDisabledInConfig())
			return DisguiseCraftCompat.isAggresiveDisguise(entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class)
				&& entity instanceof Player
				&& !IDisguiseCompat.isDisabledInConfig())
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
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class)
				&& entity instanceof Player
				&& !LibsDisguisesCompat.isDisabledInConfig())
			return LibsDisguisesCompat.isPlayerDisguise((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class)
				&& entity instanceof Player
				&& !DisguiseCraftCompat.isDisabledInConfig())
			return DisguiseCraftCompat.isPlayerDisguise((Player) entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class)
				&& entity instanceof Player
				&& !IDisguiseCompat.isDisabledInConfig())
			return IDisguiseCompat.isPlayerDisguise((Player) entity);
		else {
			return false;
		}
	}

	public static void undisguiseEntity(Entity entity) {
		if (CompatibilityManager.isPluginLoaded(LibsDisguisesCompat.class)
				&& !LibsDisguisesCompat.isDisabledInConfig())
			LibsDisguisesCompat.undisguiseEntity(entity);
		else if (CompatibilityManager.isPluginLoaded(DisguiseCraftCompat.class)
				&& !DisguiseCraftCompat.isDisabledInConfig())
			DisguiseCraftCompat.undisguisePlayer(entity);
		else if (CompatibilityManager.isPluginLoaded(IDisguiseCompat.class)
				&& !IDisguiseCompat.isDisabledInConfig())
			IDisguiseCompat.undisguisePlayer(entity);
		else {

		}
	}
}
