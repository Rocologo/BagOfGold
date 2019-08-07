package one.lindegaard.BagOfGold.compatibility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.compatibility.CompatPlugin;

public class CompatibilityManager implements Listener {

	private BagOfGold plugin;
	private static HashSet<Object> mCompatClasses = new HashSet<Object>();
	private static HashMap<CompatPlugin, Class<?>> mWaitingCompatClasses = new HashMap<CompatPlugin, Class<?>>();

	public CompatibilityManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, BagOfGold.getInstance());
	}

	public void registerPlugin(@SuppressWarnings("rawtypes") Class c, CompatPlugin pluginName) {
		try {
			register(c, pluginName);
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED + "[ERROR] MobHunting could not register with [" + pluginName
							+ "] please check if [" + pluginName + "] is compatible with the server ["
							+ Bukkit.getServer().getBukkitVersion() + "]");
			if (plugin.getConfigManager().debug)
				e.printStackTrace();
		}
	}

	/**
	 * Registers the compatability handler if the plugin specified is loaded
	 * 
	 * @param compatibilityHandler
	 *            The class that will be created
	 * @param pluginName
	 *            The name of the plugin to check
	 */
	private void register(Class<?> compatibilityHandler, CompatPlugin pluginName) {
		if (Bukkit.getPluginManager().isPluginEnabled(pluginName.getName())) {
			try {
				mCompatClasses.add(compatibilityHandler.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else
			mWaitingCompatClasses.put(pluginName, compatibilityHandler);
	}

	/**
	 * detect if the compatibility class is loaded.
	 * 
	 * @param class1
	 *            - The Compatibility class ex. "WorldGuardCompat.class"
	 * @return true if loaded.
	 */
	public boolean isPluginLoaded(Class<?> class1) {
		Iterator<Object> i = mCompatClasses.iterator();
		while (i.hasNext()) {
			Class<?> c = i.next().getClass();
			if (c.getName().equalsIgnoreCase(class1.getName()))
				return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	private void onPluginEnabled(PluginEnableEvent event) {
		CompatPlugin compatPlugin = CompatPlugin.getCompatPlugin(event.getPlugin().getName());
		if (mWaitingCompatClasses.containsKey(compatPlugin)) {
			registerPlugin(mWaitingCompatClasses.get(compatPlugin), compatPlugin);
			mWaitingCompatClasses.remove(compatPlugin);
		}
	}

}
