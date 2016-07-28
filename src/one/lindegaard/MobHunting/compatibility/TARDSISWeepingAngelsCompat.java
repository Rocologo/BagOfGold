package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;

import me.eccentric_nz.tardisweepingangels.TARDISWeepingAngelsAPI;
import me.eccentric_nz.tardisweepingangels.utils.Monster;

public class TARDSISWeepingAngelsCompat {

	private static Plugin mPlugin;
	private static boolean supported = false;

	// http://dev.bukkit.org/bukkit-plugins/tardisweepingangels/

	public TARDSISWeepingAngelsCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info("Compatibility with TARDISWeepingAngels is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("TARDISWeepingAngels");

			MobHunting.getInstance().getLogger().info("Enabling compatibility with TARDISWeepingAngelsAPI ("
					+ mPlugin.getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public TARDISWeepingAngelsAPI getTARDISWeepingAngelsAPI() {
		return (TARDISWeepingAngelsAPI) mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationTARDISWeepingAngels;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationTARDISWeepingAngels;
	}

	/**
	 * Returns whether an entity is a TARDISWeepingAngels entity.
	 *
	 * @param entity
	 *            the entity to check
	 * @return true if the entity is a TARDISWeepingAngels entity
	 */
	public boolean isWeepingAngelMonster(Entity entity) {
		return getTARDISWeepingAngelsAPI().isWeepingAngelMonster(entity);
	}

	/**
	 * Returns the Monster type for a TARDISWeepingAngels entity.
	 *
	 * @param entity
	 *            the entity to get the Monster type for
	 * @return the Monster type or null if it is not TARDISWeepingAngels entity
	 */
	public Monster getWeepingAngelMonsterType(Entity entity) {
		return getTARDISWeepingAngelsAPI().getWeepingAngelMonsterType(entity);
	}

}
