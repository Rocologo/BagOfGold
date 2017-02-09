package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import de.Keyle.MyPet.MyPetPlugin;
import de.Keyle.MyPet.api.entity.MyPetBukkitEntity;
import one.lindegaard.MobHunting.MobHunting;

public class MyPetCompat implements Listener {
	private static boolean supported = false;
	private static MyPetPlugin mPlugin;

	public MyPetCompat() {
		if (MobHunting.getConfigManager().disableIntegrationMyPet) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with MyPet is disabled in config.yml");
		} else {
			mPlugin = (MyPetPlugin) Bukkit.getPluginManager().getPlugin("MyPet");
			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with MyPet ("
					+ getMyPetPlugin().getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************

	public static boolean isSupported() {
		return supported;
	}

	public static MyPetPlugin getMyPetPlugin() {
		return mPlugin;
	}

	public static boolean isMyPet(Entity entity) {
		if (isSupported())
			return entity instanceof MyPetBukkitEntity;
		return false;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMyPet;
	}

	public static boolean isKilledByMyPet(Entity entity) {
		if (isSupported() && (entity.getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) entity.getLastDamageCause();
			if (dmg != null && (dmg.getDamager() instanceof MyPetBukkitEntity))
				return true;
		}
		return false;
	}

	public static MyPetBukkitEntity getMyPet(Entity entity) {
		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) entity.getLastDamageCause();

		if (dmg == null || !(dmg.getDamager() instanceof MyPetBukkitEntity))
			return null;

		MyPetBukkitEntity killer = (MyPetBukkitEntity) dmg.getDamager();

		return killer;
	}

	public static Player getMyPetOwner(Entity entity) {

		if (!(entity.getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return null;

		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) entity.getLastDamageCause();

		if (dmg == null || !(dmg.getDamager() instanceof MyPetBukkitEntity))
			return null;

		MyPetBukkitEntity killer = (MyPetBukkitEntity) dmg.getDamager();

		if (killer.getOwner() == null)
			return null;

		return killer.getOwner().getPlayer();
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMyPetKillMob(EntityDeathEvent event) {
		if (!MobHunting.getMobHuntingManager().isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
		if (dmg == null || !(dmg.getDamager() instanceof MyPetBukkitEntity))
			return;

		MyPetBukkitEntity killer = (MyPetBukkitEntity) dmg.getDamager();
		if (killer.getOwner() != null) {
			Player owner = killer.getOwner().getPlayer();
			if (owner != null && MobHunting.getMobHuntingManager().isHuntEnabled(owner))
				MobHunting.getAchievementManager().awardAchievementProgress("fangmaster", owner, 1);
		}
	}
}
