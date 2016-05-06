package one.lindegaard.MobHunting;

import java.util.HashMap;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import one.lindegaard.MobHunting.storage.PlayerSettings;

public class PlayerSettingsManager implements Listener {

	private static HashMap<Player, PlayerSettings> mPlayerSettings = new HashMap<Player, PlayerSettings>();

	/**
	 * Contructor for the PlayerSettingsmanager
	 */
	PlayerSettingsManager() {
		MobHunting.getInstance().getServer().getPluginManager().registerEvents(this, MobHunting.getInstance());
	}

	/**
	 * Get playerSettings from memory
	 * 
	 * @param player
	 * @return PlayerSettings
	 */
	public PlayerSettings getPlayerSettings(Player player) {
		if (mPlayerSettings.containsKey(player))
			return mPlayerSettings.get(player);
		else // its not a player
			return new PlayerSettings(player, false, true);
	}

	/**
	 * Store playerSettings in memory
	 * 
	 * @param playerSettings
	 */
	public void putPlayerSettings(Player player, PlayerSettings playerSettings) {
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
		// save(player);
		putPlayerSettings(player, new PlayerSettings(player, mPlayerSettings.get(player).isLearningMode(),
				mPlayerSettings.get(player).isMuted()));
		// MobHunting.getDataStoreManager().createPlayerSettings(player,
		// learning_mode, muted);
	}

	/**
	 * Write PlayerSettings to Database when Player Quit and remove
	 * PlayerSettings from memory
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		save(player);
		mPlayerSettings.remove(player);
	}

	/**
	 * Load PlayerSettings from Database
	 * 
	 * @param player
	 */
	public void load(Player player) {
		PlayerSettings ps = MobHunting.getDataStoreManager().getPlayerSettings(player);
		boolean learning_mode = ps.isLearningMode();
		boolean muted = ps.isMuted();
		if (muted)
			MobHunting.debug("%s isMuted()", player.getName());
		if (learning_mode)
			MobHunting.debug("%s is in LearningMode()", player.getName());
		putPlayerSettings(player, ps);
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
	 * @param killed
	 * @return true if player exists in PlayerSettings in Memory
	 */
	public boolean containsKey(LivingEntity killed) {
		if (killed instanceof Player)
			return mPlayerSettings.containsKey(killed);
		else
			return false;
	}

}
