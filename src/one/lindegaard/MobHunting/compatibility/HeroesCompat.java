package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.MobHunting;

public class HeroesCompat implements Listener {
	public HeroesCompat() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
		MobHunting.getInstance().getLogger().info("Enabling Heroes Compatibility"); //$NON-NLS-1$
	}

}
