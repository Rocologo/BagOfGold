package one.lindegaard.MobHunting;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import one.lindegaard.MobHunting.storage.IDataCallback;
import one.lindegaard.MobHunting.storage.PlayerSettings;

public class PlayerSettingsManager implements Listener {

	private static HashMap<UUID, PlayerSettings> mPlayerSettings = new HashMap<UUID, PlayerSettings>();

	/**
	 * Constructor for the PlayerSettingsmanager
	 */
	PlayerSettingsManager() {
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
	}

	/**
	 * Get playerSettings from memory
	 * 
	 * @param offlinePlayer
	 * @return PlayerSettings
	 */
	public PlayerSettings getPlayerSettings(OfflinePlayer offlinePlayer) {
		if (mPlayerSettings.containsKey(offlinePlayer.getUniqueId()))
			return mPlayerSettings.get(offlinePlayer.getUniqueId());
		else
			return new PlayerSettings(offlinePlayer);
	}

	/**
	 * Store playerSettings in memory
	 * 
	 * @param playerSettings
	 */
	public void setPlayerSettings(OfflinePlayer player, PlayerSettings playerSettings) {
		mPlayerSettings.put(player.getUniqueId(), playerSettings);
	}

	/**
	 * Remove PlayerSettings from Memory
	 * 
	 * @param player
	 */
	public void removePlayerSettings(OfflinePlayer player) {
		Messages.debug("Removing %s from player settings cache", player.getName());
		mPlayerSettings.remove(player.getUniqueId());
	}

	/**
	 * Read PlayerSettings From database into Memory when player joins
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (containsKey(player))
			Messages.debug("Using cached player settings");
		else
			load(player);
	}

	/**
	 * Write PlayerSettings to Database when Player Quit and remove
	 * PlayerSettings from memory
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		save(player);
	}

	/**
	 * Load PlayerSettings asynchronously from Database
	 * 
	 * @param player
	 */
	public void load(final OfflinePlayer player) {
		MobHunting.getDataStoreManager().requestPlayerSettings(player, new IDataCallback<PlayerSettings>() {

			@Override
			public void onCompleted(PlayerSettings ps) {
				if (ps.isMuted())
					Messages.debug("%s isMuted()", player.getName());
				if (ps.isLearningMode())
					Messages.debug("%s is in LearningMode()", player.getName());
				mPlayerSettings.put(player.getUniqueId(), ps);
			}

			@Override
			public void onError(Throwable error) {
				Bukkit.getConsoleSender().sendMessage(
						ChatColor.RED + "[MobHunting][ERROR] Could not load playerSettings for " + player.getName());
				mPlayerSettings.put(player.getUniqueId(),new PlayerSettings(player));
			}
		});
	}

	/**
	 * Write PlayerSettings to Database
	 * 
	 * @param player
	 */
	public void save(final OfflinePlayer player) {
		MobHunting.getDataStoreManager().updatePlayerSettings(player, getPlayerSettings(player).isLearningMode(),
				getPlayerSettings(player).isMuted());
	}

	/**
	 * Test if PlayerSettings contains data for Player
	 * 
	 * @param player
	 * @return true if player exists in PlayerSettings in Memory
	 */
	public boolean containsKey(final OfflinePlayer player) {
		return mPlayerSettings.containsKey(player.getUniqueId());
	}

}
