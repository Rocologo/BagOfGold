package one.lindegaard.BagOfGold.compatibility;

import java.util.HashSet;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import one.lindegaard.BagOfGold.BagOfGold;

public class CompatibilityManager {
	
	private BagOfGold plugin;
	
	public CompatibilityManager(BagOfGold bagOfGold){
		this.plugin=bagOfGold;
	}
	
	private HashSet<Object> mCompatClasses = new HashSet<Object>();

	public void registerPlugin(@SuppressWarnings("rawtypes") Class c, CompatPlugin pluginName) {
		try {
			register(c, pluginName);
		} catch (Exception e) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(ChatColor.RED + "[MobHunting][ERROR] MobHunting could not register with [" + pluginName
							+ "] please check if [" + pluginName + "] is compatible with the server ["
							+ Bukkit.getServer().getBukkitVersion() + "]");
			if (plugin.getConfigManager().killDebug)
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
		}
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

}
