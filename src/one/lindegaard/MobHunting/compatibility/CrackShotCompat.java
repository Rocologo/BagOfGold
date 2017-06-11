package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.shampaggon.crackshot.CSUtility;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import com.shampaggon.crackshot.events.WeaponExplodeEvent;
import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class CrackShotCompat implements Listener {

	private static Plugin mPlugin;
	private static boolean supported = false;
	// https://dev.bukkit.org/projects/crackshot
	// API: https://github.com/Shampaggon/CrackShot/wiki/Hooking-into-CrackShot

	public CrackShotCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with CrackShot is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("CrackShot");

			if (mPlugin.getDescription().getVersion().compareTo("0.98.5") >= 0) {

				Bukkit.getLogger().info("[MobHunting] Enabling compatibility with CrackShot ("
						+ mPlugin.getDescription().getVersion() + ")");

				supported = true;

				Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			} else {
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				console.sendMessage(ChatColor.RED + "[MobHunting] Your current version of CrackShot ("
						+ mPlugin.getDescription().getVersion()
						+ ") has no API implemented. Please update to V0.98.5 or newer.");
			}
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public Plugin getPlugin() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationCrackShot;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationCrackShot;
	}

	public static boolean isCrackShotWeapon(ItemStack itemStack) {
		if (isSupported()) {
			CSUtility cs = new CSUtility();
			return cs.getWeaponTitle(itemStack) != null;
		}
		return false;
	}

	public static String getCrackShotWeapon(ItemStack itemStack) {
		if (isSupported()) {
			CSUtility cs = new CSUtility();
			return cs.getWeaponTitle(itemStack);
		}
		return null;
	}

	public static boolean isCrackShotProjectile(Projectile Projectile) {
		if (isSupported()) {
			CSUtility cs = new CSUtility();
			return cs.getWeaponTitle(Projectile) != null;
		}
		return false;
	}

	public static String getCrackShotWeapon(Projectile Projectile) {
		if (isSupported()) {
			CSUtility cs = new CSUtility();
			return cs.getWeaponTitle(Projectile);
		}
		return null;
	}

	public static boolean isCrackShotUsed(Entity entity) {
		if (MobHunting.getMobHuntingManager().getDamageHistory().containsKey(entity))
			return !MobHunting.getMobHuntingManager().getDamageHistory().get(entity).getCrackShotWeaponUsed().isEmpty();
		return false;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	@EventHandler(priority = EventPriority.LOW)
	public void onWeaponDamageEntityEvent(WeaponDamageEntityEvent event) {
		if (event.getVictim() instanceof LivingEntity) {
			DamageInformation info = new DamageInformation();
			Messages.debug("onWeaponDamageEntityEvent: Victim=%s damaged with a %s", event.getVictim().getType(),
					getCrackShotWeapon(event.getPlayer().getItemInHand()));
			info.setTime(System.currentTimeMillis());
			info.setLastAttackTime(info.getTime());
			info.setAttacker(event.getPlayer());
			info.setAttackerPosition(event.getPlayer().getLocation().clone());
			info.setCrackShotWeapon(getCrackShotWeapon(event.getPlayer().getItemInHand()));
			info.setCrackShotPlayer(event.getPlayer());
			MobHunting.getMobHuntingManager().getDamageHistory().put((LivingEntity) event.getVictim(), info);
		}
	}

	// @EventHandler(priority = EventPriority.NORMAL)
	// public void onWeaponDamageEntityEvent(WeaponExplodeEvent event) {
	// Messages.debug("WeaponExplodeEvent: Weapon=%s", event.getWeaponTitle());
	// }

}
