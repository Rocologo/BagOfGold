package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.shampaggon.crackshot.CSUtility;

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
			CSUtility crackShot = (CSUtility) mPlugin;
			return crackShot.getWeaponTitle(itemStack) != null;
		}
		return false;
	}

	public static String getCrackShotWeapon(ItemStack itemStack) {
		if (isSupported()) {
			CSUtility crackShot = (CSUtility) mPlugin;
			return crackShot.getWeaponTitle(itemStack);
		}
		return null;
	}

	public static boolean isCrackShotProjectile(Projectile Projectile) {
		if (isSupported()) {
			CSUtility crackShot = (CSUtility) mPlugin;
			return crackShot.getWeaponTitle(Projectile) != null;
		}
		return false;
	}

	public static String getCrackShotWeapon(Projectile Projectile) {
		if (isSupported()) {
			CSUtility crackShot = (CSUtility) mPlugin;
			return crackShot.getWeaponTitle(Projectile);
		}
		return null;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
