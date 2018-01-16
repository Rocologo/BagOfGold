package one.lindegaard.BagOfGold;

import java.sql.SQLException;
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

import one.lindegaard.BagOfGold.compatibility.EssentialsCompat;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataCallback;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
import one.lindegaard.BagOfGold.util.Misc;

public class PlayerSettingsManager implements Listener {

	private HashMap<UUID, PlayerSettings> mPlayerSettings = new HashMap<UUID, PlayerSettings>();

	private BagOfGold plugin;

	/**
	 * Constructor for the PlayerSettingsmanager
	 */
	PlayerSettingsManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
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
		else {
			PlayerSettings ps;
			try {
				ps = plugin.getStoreManager().loadPlayerSettings(offlinePlayer);
			} catch (DataStoreException | SQLException e) {
				plugin.getMessages().debug("%s is not in the database (has played before=%s)", offlinePlayer.getName(),
						offlinePlayer.hasPlayedBefore());
				if (offlinePlayer.hasPlayedBefore() && EssentialsCompat.isSupported()) {
					double bal = EssentialsCompat.getEssentialsBalance(offlinePlayer);
					return new PlayerSettings(offlinePlayer, bal);
				} else
					return new PlayerSettings(offlinePlayer, plugin.getConfigManager().startingBalance);

			}
			plugin.getMessages().debug("%s is offline, fetching PlayerData from database", offlinePlayer.getName());
			return ps;
		}

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
		plugin.getMessages().debug("Removing %s from player settings cache", player.getName());
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
		if (!containsKey(player))
			load(player);
		else {

			if (getPlayerSettings(player).getBalanceChanges() != 0) {
				plugin.getMessages().debug("Balance was changed while %s was offline. New balance is %s.",
						player.getName(), Misc.format(getPlayerSettings(player).getBalance()
								+ getPlayerSettings(player).getBalanceChanges()));
				double change = getPlayerSettings(player).getBalanceChanges();
				getPlayerSettings(player).setBalance(
						getPlayerSettings(player).getBalance() + getPlayerSettings(player).getBalanceChanges());
				getPlayerSettings(player).setBalanceChanges(0);
				if (change > 0)
					plugin.getEconomyManager().addBagOfGoldPlayer_EconomyManager(player, change);
				else
					plugin.getEconomyManager().removeBagOfGoldPlayer_EconomyManager(player, change);
			}
		}
	}

	/**
	 * Write PlayerSettings to Database when Player Quit and remove PlayerSettings
	 * from memory
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final double balance = getPlayerSettings(player).getBalance();
		if (EssentialsCompat.isSupported()) {
			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

				@Override
				public void run() {

					EssentialsCompat.setEssentialsBalance(player, balance);

				}
			}, 100L);

		}
	}

	/**
	 * Load PlayerSettings asynchronously from Database
	 * 
	 * @param offlinePlayer
	 */
	public void load(final OfflinePlayer offlinePlayer) {
		plugin.getDataStoreManager().requestPlayerSettings(offlinePlayer, new IDataCallback<PlayerSettings>() {

			@Override
			public void onCompleted(PlayerSettings ps) {
				if (ps.isMuted())
					plugin.getMessages().debug("%s isMuted()", offlinePlayer.getName());
				if (ps.isLearningMode())
					plugin.getMessages().debug("%s is in LearningMode()", offlinePlayer.getName());
				if (offlinePlayer.isOnline() && ps.getBalanceChanges() != 0) {
					plugin.getMessages().debug("Balance was changed while %s was offline. New balance is %s.",
							offlinePlayer.getName(), ps.getBalance() + ps.getBalanceChanges());
					double change = ps.getBalanceChanges();
					ps.setBalance(ps.getBalance() + ps.getBalanceChanges());
					ps.setBalanceChanges(0);
					if (change > 0)
						plugin.getEconomyManager().addBagOfGoldPlayer_EconomyManager((Player) offlinePlayer, change);
					else
						plugin.getEconomyManager().removeBagOfGoldPlayer_EconomyManager((Player) offlinePlayer, change);
				}
				mPlayerSettings.put(offlinePlayer.getUniqueId(), ps);
			}

			@Override
			public void onError(Throwable error) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BagOfGold][ERROR] " + offlinePlayer.getName()
						+ " is new, creating user in database.");
				mPlayerSettings.put(offlinePlayer.getUniqueId(),
						new PlayerSettings(offlinePlayer, plugin.getConfigManager().startingBalance));
			}
		});
	}

	/**
	 * Write PlayerSettings to Database
	 * 
	 * @param player
	 */
	public void save(final OfflinePlayer player) {
		plugin.getDataStoreManager().updatePlayerSettings(player, getPlayerSettings(player));
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
