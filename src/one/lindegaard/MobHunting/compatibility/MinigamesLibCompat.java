package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.comze_instancelabs.minigamesapi.MinigamesAPI;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntEnableCheckEvent;

public class MinigamesLibCompat implements Listener {

	private static boolean supported = false;
	private static Plugin mPlugin;
	public static final String MH_MINIGAMESLIB = "MH:MINIGAMESLIB";

	// https://www.spigotmc.org/resources/minigameslib.23844/

	public MinigamesLibCompat() {

		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with MinigamesLib is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("MinigamesLib");
			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with MinigamesLib (v"
					+ mPlugin.getDescription().getVersion() + ")");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************
	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationMinigamesLib;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMinigamesLib;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isPlayingMinigame(Player player) {
		//MinigamesAPI minigamePlayer = MinigamesAPI.getAPI();//..pdata.getMinigamePlayer(player);
		//Player p = MinigamesAPI.getAPI().uuidToPlayer(player.getUniqueId());
		//return player != null && minigamePlayer.isInMinigame();
		return true;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerJoinMinigameLib(MobHuntEnableCheckEvent event) {
		//Messages.debug("onPlayerJoinMinigame was run...");
		//MinigamePlayer player = MinigamesLib.plugin.pdata.getMinigamePlayer(event.getPlayer());
		//if (player != null && player.isInMinigame())
		//	event.setEnabled(false);
	}

}
