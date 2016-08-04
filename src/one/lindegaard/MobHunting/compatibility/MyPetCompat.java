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
import de.Keyle.MyPet.api.entity.MyPetType;
import one.lindegaard.MobHunting.Messages;
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
			Bukkit.getLogger()
					.info("[MobHunting] Enabling compatibility with MyPet (" + getMyPetPlugin().getDescription().getVersion() + ")");
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

	public static boolean isMyPet(Object obj) {
		return (supported && obj instanceof MyPetBukkitEntity && MobHunting.getConfigManager().disableIntegrationMyPet);
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMyPet;
	}

	public static boolean isKilledByMyPet(Entity entity) {
		if (!(entity instanceof EntityDamageByEntityEvent))
			return false;

		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) entity.getLastDamageCause();

		if (!(dmg.getDamager() instanceof MyPetBukkitEntity))
			return false;

		MyPetBukkitEntity killer = (MyPetBukkitEntity) dmg.getDamager();

		if (killer.getPetType() != MyPetType.Wolf)
			return false;

		Messages.debug("MyPetCompat: A Wolf Killed a mob");

		return true;
	}

	public static Player getMyPetOwner(Entity entity) {
		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) entity.getLastDamageCause();

		if (!(dmg.getDamager() instanceof MyPetBukkitEntity))
			return null;

		MyPetBukkitEntity killer = (MyPetBukkitEntity) dmg.getDamager();

		// TODO: Handle other PetTypes
		if (killer.getPetType() != MyPetType.Wolf)
			return null;

		if (killer.getOwner() == null)
			return null;

		return killer.getOwner().getPlayer();
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onWolfKillMob(EntityDeathEvent event) {
		if (!MobHunting.getMobHuntingManager().isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

		if (!(dmg.getDamager() instanceof MyPetBukkitEntity))
			return;

		MyPetBukkitEntity killer = (MyPetBukkitEntity) dmg.getDamager();

		if (killer.getPetType() != MyPetType.Wolf)
			return;

		Messages.debug("MyPetCompat: A Wolf Killed a mob");

		if (killer.getOwner() != null) {
			Player owner = killer.getOwner().getPlayer();

			if (owner != null && MobHunting.getMobHuntingManager().isHuntEnabled(owner)) {
				MobHunting.getAchievementManager().awardAchievementProgress("fangmaster", owner, 1);
			}
		}
	}
}
