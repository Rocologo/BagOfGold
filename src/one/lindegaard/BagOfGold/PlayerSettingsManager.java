package one.lindegaard.BagOfGold;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import one.lindegaard.BagOfGold.compatibility.EssentialsCompat;
import one.lindegaard.BagOfGold.compatibility.PerWorldInventoryCompat;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataCallback;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
import one.lindegaard.BagOfGold.storage.UserNotFoundException;

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
			if (offlinePlayer.isOnline())
				return new PlayerSettings(offlinePlayer, 0);
			else {
				PlayerSettings ps;
				try {
					ps = plugin.getStoreManager().loadPlayerSettings(offlinePlayer);
				} catch (UserNotFoundException e) {

					plugin.getMessages().debug("Insert new PlayerSettings for %s to database.",
							offlinePlayer.getName());

					double balance = 0;
					if (offlinePlayer.hasPlayedBefore())
						if (EssentialsCompat.isSupported()) {
							balance = EssentialsCompat.getEssentialsBalance(offlinePlayer);
						} else
							balance = plugin.getConfigManager().startingBalance;
					ps = new PlayerSettings(offlinePlayer, plugin.getConfigManager().learningMode, false, balance, 0, 0,
							0);
					try {
						plugin.getStoreManager().insertPlayerSettings(ps);
						mPlayerSettings.put(offlinePlayer.getUniqueId(), ps);
					} catch (DataStoreException e1) {
						e1.printStackTrace();
					}
					return ps;

				} catch (DataStoreException | SQLException e) {
					plugin.getMessages().debug("Error reading %s's data from the database", offlinePlayer.getName(),
							offlinePlayer.hasPlayedBefore());
					return new PlayerSettings(offlinePlayer, 0);
				}
				mPlayerSettings.put(offlinePlayer.getUniqueId(), ps);
				return ps;
			}
		}

	}

	/**
	 * Store playerSettings in memory
	 * 
	 * @param playerSettings
	 */
	public void setPlayerSettings(OfflinePlayer player, PlayerSettings playerSettings) {
		mPlayerSettings.put(player.getUniqueId(), playerSettings);
		plugin.getDataStoreManager().updatePlayerSettings(player, playerSettings);
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
		if (player.getGameMode() == GameMode.SURVIVAL) {
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
				mPlayerSettings.put(offlinePlayer.getUniqueId(), ps);
				if (ps.isMuted())
					plugin.getMessages().debug("%s isMuted()", offlinePlayer.getName());
				if (ps.isLearningMode())
					plugin.getMessages().debug("%s is in LearningMode()", offlinePlayer.getName());
				double change = ps.getBalanceChanges();
				if (!PerWorldInventoryCompat.isSupported()) {
					if (offlinePlayer.isOnline() && change != 0) {
						plugin.getMessages().debug("Balance was changed while %s was offline. New balance is %s.",
								offlinePlayer.getName(), ps.getBalance() + change);
						ps.setBalance(ps.getBalance() + change);
						ps.setBalanceChanges(0);
						if (change > 0)
							plugin.getEconomyManager().addBagOfGoldPlayer((Player) offlinePlayer, change);
						else
							plugin.getEconomyManager().removeBagOfGoldPlayer((Player) offlinePlayer, change);
						setPlayerSettings(offlinePlayer, ps);
					}
				}
				if (!offlinePlayer.hasPlayedBefore()) {
					plugin.getEconomyManager().depositPlayer(offlinePlayer, plugin.getConfigManager().startingBalance);
				}
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
	 * Test if PlayerSettings contains data for Player
	 * 
	 * @param player
	 * @return true if player exists in PlayerSettings in Memory
	 */
	public boolean containsKey(final OfflinePlayer player) {
		return mPlayerSettings.containsKey(player.getUniqueId());
	}

}
