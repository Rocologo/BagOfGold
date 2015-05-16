package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHunting;

public class HeroesCompat implements Listener {
	public HeroesCompat() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
		MobHunting.instance.getLogger().info("Enabling Heroes Compatability"); //$NON-NLS-1$
	}

}
