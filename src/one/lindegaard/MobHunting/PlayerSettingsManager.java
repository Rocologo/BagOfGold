package one.lindegaard.MobHunting;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import one.lindegaard.MobHunting.storage.PlayerSettings;

public class PlayerSettingsManager implements Listener {

	private static HashMap<OfflinePlayer, PlayerSettings> mPlayerSettings = new HashMap<OfflinePlayer, PlayerSettings>();

	/**
	 * Constructor for the PlayerSettingsmanager
	 */
	PlayerSettingsManager() {
		Bukkit.getServer().getPluginManager().registerEvents(this, MobHunting.getInstance());
	}

	/**
	 * Get playerSettings from memory
	 * 
	 * @param offlinePlayer
	 * @return PlayerSettings
	 */
	public PlayerSettings getPlayerSettings(OfflinePlayer offlinePlayer) {
		return mPlayerSettings.get(offlinePlayer);
	}

	/**
	 * Store playerSettings in memory
	 * 
	 * @param playerSettings
	 */
	public void setPlayerSettings(Player player, PlayerSettings playerSettings) {
		mPlayerSettings.put(player, playerSettings);
	}

	/**
	 * Remove PlayerSettings from Memory
	 * 
	 * @param player
	 */
	public void removePlayerSettings(Player player) {
		mPlayerSettings.remove(player);
	}

	/**
	 * Read PlayerSettings From database into Memory when player joins
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		load(player);
	}

	/**
	 * Write PlayerSettings to Database when Player Quit and remove
	 * PlayerSettings from memory
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		save(player);
	}

	/**
	 * Load PlayerSettings from Database
	 * 
	 * @param player
	 */
	public void load(Player player) {
		PlayerSettings ps = MobHunting.getDataStoreManager().getPlayerSettings(player);
		if (ps.isMuted())
			MobHunting.debug("%s isMuted()", player.getName());
		if (ps.isLearningMode())
			MobHunting.debug("%s is in LearningMode()", player.getName());
		mPlayerSettings.put(player, ps);
	}

	/**
	 * Write PlayerSettings to Database
	 * 
	 * @param player
	 */
	public void save(Player player) {
		MobHunting.getDataStoreManager().updatePlayerSettings(player, getPlayerSettings(player).isLearningMode(),
				getPlayerSettings(player).isMuted());
	}

	/**
	 * Test if PlayerSettings contains data for Player
	 * 
	 * @param entity
	 * @return true if player exists in PlayerSettings in Memory
	 */
	public boolean containsKey(LivingEntity entity) {
		if (entity instanceof Player)
			return mPlayerSettings.containsKey((Player) entity);
		else
			return false;
	}

}
