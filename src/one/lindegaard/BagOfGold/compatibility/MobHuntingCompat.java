package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.MobHunting.MobHunting;

public class MobHuntingCompat implements Listener {

	private static MobHunting mPlugin;
	private static boolean supported = false;

	public MobHuntingCompat() {
		mPlugin = (MobHunting) Bukkit.getPluginManager().getPlugin(CompatPlugin.MobHunting.getName());

		Bukkit.getPluginManager().registerEvents(this, BagOfGold.getInstance());

		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
				+ "Enabling compatibility with MobHunting (" + getMobHunting().getDescription().getVersion() + ")");
		supported = true;
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static MobHunting getMobHunting() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
