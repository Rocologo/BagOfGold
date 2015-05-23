package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHunting;

public class WorldGuardCompat implements Listener {
	
	public WorldGuardCompat() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
		MobHunting.instance.getLogger().info("Enabling WorldGuard Compatability");
	}

}
