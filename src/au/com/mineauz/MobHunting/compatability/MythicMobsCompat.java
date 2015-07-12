package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import au.com.mineauz.MobHunting.MobHunting;
import de.Keyle.MyPet.api.entity.MyPetEntity;
import de.Keyle.MyPet.entity.types.MyPetType;
import net.citizensnpcs.api.CitizensPlugin;
import net.elseland.xikage.MythicMobs.API.Events.MythicMobCustomSkillEvent;
import net.elseland.xikage.MythicMobs.API.Events.MythicMobDeathEvent;
import net.elseland.xikage.MythicMobs.API.Events.MythicMobSkillEvent;
import net.elseland.xikage.MythicMobs.API.Events.MythicMobSpawnEvent;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;

public class MythicMobsCompat implements Listener {

	private static boolean supported = false;
	private static Plugin mPlugin;

	public MythicMobsCompat() {

		mPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs");

		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

		MobHunting.instance.getLogger().info(
				"Enabling Compatability with MythicMob ("
						+ getMythicMobs().getDescription().getVersion() + ")");
		// API:
		// http://xikage.elseland.net/viewgit/?a=tree&p=MythicMobs&h=dec796decd1ef71fdd49aed69aef85dc7d82b1c1&hb=ffeb51fb84e882365846a30bd2b9753716faf51e&f=MythicMobs/src/net/elseland/xikage/MythicMobs/API
		supported = true;
	}

	public static Plugin getMythicMobs() {
		return mPlugin;
	}
	
	public static boolean isMythicMobsSupported() {
		return supported;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobDeathEvent(MythicMobDeathEvent event) {
		MobHunting.debug(
				"MythicMob Death event, killer is %s, mobname=%s, Mobname=%s",
				event.getKiller(), event.MobName, event.getMobType().MobName);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobSpawnEvent(MythicMobSpawnEvent event) {
		MobHunting.debug("MythicMob spawn event: name=%s Mobtype=%s", event
				.getLivingEntity().getName(), event.getMobType().MobName);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobSkillEvent(MythicMobSkillEvent event) {
		MobHunting.debug("MythicMob Skill event");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobCustomSkillEvent(MythicMobCustomSkillEvent event) {
		MobHunting.debug("MythicMob Custom Skill event");
	}

	// @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onWolfKillMob(EntityDeathEvent event) {
		if (!MobHunting.isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) event
				.getEntity().getLastDamageCause();

		if (!(dmg.getDamager() instanceof MyPetEntity))
			return;

		MyPetEntity killer = (MyPetEntity) dmg.getDamager();

		if (killer.getPetType() != MyPetType.Wolf)
			return;

		if (killer.getOwner() != null) {
			Player owner = killer.getOwner().getPlayer();

			if (owner != null && MobHunting.isHuntEnabled(owner)) {
				MobHunting.instance.getAchievements().awardAchievementProgress(
						"fangmaster", owner, 1); //$NON-NLS-1$
			}
		}
	}
}
