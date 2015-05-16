package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHunting;

public class WarCompat implements Listener {
	public WarCompat() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
		MobHunting.instance.getLogger().info("Enabling War Compatability"); //$NON-NLS-1$
	}

}
