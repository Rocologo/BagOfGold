package au.com.mineauz.MobHunting.compatability;

import java.util.HashSet;

import org.bukkit.Bukkit;

public class CompatibilityManager
{
	private static HashSet<Object> mCompatClasses = new HashSet<Object>();
	/**
	 * Registers the compatability handler if the plugin specified is loaded
	 * @param compatabilityHandler The class that will be created
	 * @param pluginName The name of the plugin to check
	 */
	public static void register(Class<?> compatibilityHandler, String pluginName)
	{
		if(Bukkit.getPluginManager().isPluginEnabled(pluginName))
		{
			try
			{
				mCompatClasses.add(compatibilityHandler.newInstance());
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
