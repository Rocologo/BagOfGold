package one.lindegaard.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import one.lindegaard.MobHunting.MobHunting;

public class EssentialsCompat implements Listener {

	private static Essentials mPlugin;
	private static boolean supported=false;

	public EssentialsCompat() {
		if (isDisabledInConfig()) {
			MobHunting.getInstance().getLogger().info(
					"Compatability with Essentials is disabled in config.yml");
		} else {
			mPlugin = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			MobHunting.getInstance()
					.getLogger()
					.info("Enabling compatability with Essentials ("
							+ getEssentials().getDescription().getVersion() + ")");
			supported=true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Essentials getEssentials() {
		return mPlugin;
	}
	
	public static boolean isSupported(){
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationEssentials;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationEssentials;
	}
	
	public static boolean isGodModeEnabled(Player player){
		User user = getEssentials().getUser(player);
	    return user.isGodModeEnabled();
	}
	
	public static boolean isVanishedModeEnabled(Player player){
		User user = getEssentials().getUser(player);
	    return user.isVanished();
	}
	
	// **************************************************************************
	// EVENTS
	// **************************************************************************
	
}
