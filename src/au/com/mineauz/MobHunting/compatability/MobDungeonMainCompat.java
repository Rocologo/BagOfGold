package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHunting;

public class MobDungeonMainCompat implements Listener {
	public MobDungeonMainCompat() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
		MobHunting.instance.getLogger().info(
				"Enabling MobDungeon Compatability"); //$NON-NLS-1$
	}

}
