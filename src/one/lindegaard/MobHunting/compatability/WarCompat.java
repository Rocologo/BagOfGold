package one.lindegaard.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.MobHunting;

public class WarCompat implements Listener {
	public WarCompat() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
		MobHunting.getInstance().getLogger().info("Enabling War Compatability"); //$NON-NLS-1$
	}

}
