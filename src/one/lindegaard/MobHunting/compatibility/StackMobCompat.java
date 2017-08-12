package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmob.extras.GlobalValues;

public class StackMobCompat implements Listener {

	// https://www.spigotmc.org/resources/stackmob.29999/

	private static boolean supported = false;
	private static Plugin mPlugin;
	private final static String STACKMOB_STACK_SIZE = GlobalValues.metaTag;

	public StackMobCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with StackMob is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("StackMob");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getLogger().info("[MobHunting] Enabling Compatibility with StackMob ("
					+ mPlugin.getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************

	public static boolean isSupported() {
		return supported;
	}

	private static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationStackMob;
	}

	@SuppressWarnings("unused")
	private static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationStackMob;
	}

	public static boolean isStackedMob(Entity entity) {
		if (isSupported()) {
			return entity.hasMetadata(STACKMOB_STACK_SIZE);
		}
		return false;
	}

	public static int getStackSize(Entity entity) {
		if (entity.hasMetadata(STACKMOB_STACK_SIZE)) {
			return (Integer) entity.getMetadata(STACKMOB_STACK_SIZE).get(0).value();
		}
		return 1;
	}

	public static boolean killHoleStackOnDeath(Entity entity) {
		return mPlugin.getConfig().getBoolean("creature.kill-all.enabled");
	}

	public static boolean isGrindingStackedMobsAllowed() {
		return MobHunting.getConfigManager().isGrindingStackedMobsAllowed;
	}

}
