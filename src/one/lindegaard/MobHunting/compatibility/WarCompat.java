package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.MobHunting;

public class WarCompat implements Listener {
	public WarCompat() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
		MobHunting.getInstance().getLogger().info("Enabling War Compatibility"); //$NON-NLS-1$
	}

}
