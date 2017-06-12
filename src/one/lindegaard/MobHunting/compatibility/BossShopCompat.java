package one.lindegaard.MobHunting.compatibility;

import org.black_ixx.bossshop.BossShop;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.MobHunting;

public class BossShopCompat implements Listener {

	private static Plugin mPlugin;
	private static boolean supported = false;
	private static BossShop bs;

	// https://www.spigotmc.org/resources/bossshop-powerful-and-playerfriendly-chest-gui-shop-menu-plugin.222/

	public BossShopCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage("[MobHunting] Compatibility with BossShop is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("BossShop");

			bs = (BossShop) mPlugin;

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getConsoleSender().sendMessage(
					"[MobHunting] Enabling compatibility with BossShop (" + bs.getDescription().getVersion() + ").");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static BossShop getBossShop() {
		return bs;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationBossShop;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationBossShop;
	}

}
