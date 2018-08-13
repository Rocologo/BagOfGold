package one.lindegaard.BagOfGold;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import one.lindegaard.BagOfGold.compatibility.EssentialsCompat;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataCallback;
import one.lindegaard.BagOfGold.storage.UserNotFoundException;
import one.lindegaard.MobHunting.util.Misc;

public class PlayerBalanceManager implements Listener {

	private BagOfGold plugin;
	private HashMap<UUID, PlayerBalances> mBalances = new HashMap<UUID, PlayerBalances>();

	/**
	 * Constructor for the PlayerBalanceManager
	 */
	PlayerBalanceManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public HashMap<UUID, PlayerBalances> getBalances() {
		return mBalances;
	}

	public PlayerBalance getPlayerBalance(OfflinePlayer offlinePlayer) {
		if (offlinePlayer.isOnline()) {
			String worldGroup = plugin.getWorldGroupManager().getCurrentWorldGroup(offlinePlayer);
			GameMode gamemode = plugin.getWorldGroupManager().getCurrentGameMode(offlinePlayer);
			return getPlayerBalance(offlinePlayer, worldGroup, gamemode);
		} else {
			String worldGroup = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer)
					.getLastKnownWorldGrp();
			GameMode gamemode = plugin.getWorldGroupManager().getDefaultGameMode();
			return getPlayerBalance(offlinePlayer, worldGroup, gamemode);
		}
	}

	public PlayerBalance getPlayerBalance(OfflinePlayer offlinePlayer, String worldGroup, GameMode gamemode) {
		if (mBalances.containsKey(offlinePlayer.getUniqueId()))
			// offlinePlayer is in the Database
			if (mBalances.get(offlinePlayer.getUniqueId()).has(worldGroup, gamemode)) {
				return mBalances.get(offlinePlayer.getUniqueId()).getPlayerBalance(worldGroup, gamemode);
			} else {
				// offlinePlayer does have a balance for this
				// worldgroup-gamemode. Create it with default values
				// PlayerBalances ps = new PlayerBalances();
				plugin.getMessages().debug("PlayerBlananceManager: creating new %s and %s", worldGroup, gamemode);
				PlayerBalances ps = mBalances.get(offlinePlayer.getUniqueId());
				PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
				ps.putPlayerBalance(pb);
				setPlayerBalance(offlinePlayer, pb);
				return pb;
			}
		else {
			// offlinePlayer is NOT in memory, try loading from DB
			PlayerBalances ps = new PlayerBalances();
			PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
			try {
				ps = plugin.getStoreManager().loadPlayerBalances(offlinePlayer);
			} catch (UserNotFoundException e) {
				//
			} catch (DataStoreException e) {
				e.printStackTrace();
			}

			if (!ps.has(worldGroup, gamemode)) {
				setPlayerBalance(offlinePlayer, pb);
			}
			mBalances.put(offlinePlayer.getUniqueId(), ps);
			pb = ps.getPlayerBalance(worldGroup, gamemode);
			return pb;
		}
	}

	// TODO: remove parameter offlinePlayer
	public void setPlayerBalance(OfflinePlayer offlinePlayer, PlayerBalance playerBalance) {
		if (!mBalances.containsKey(offlinePlayer.getUniqueId())) {
			PlayerBalances ps = new PlayerBalances();
			ps.putPlayerBalance(playerBalance);
			mBalances.put(offlinePlayer.getUniqueId(), ps);
		} else {
			mBalances.get(offlinePlayer.getUniqueId()).putPlayerBalance(playerBalance);
		}
		plugin.getDataStoreManager().updatePlayerBalance(offlinePlayer, playerBalance);
	}

	/**
	 * Remove PlayerSettings from Memory
	 * 
	 * @param offlinePlayer
	 */
	public void removePlayerBalance(OfflinePlayer offlinePlayer) {
		plugin.getMessages().debug("Removing %s from player settings cache", offlinePlayer.getName());
		mBalances.remove(offlinePlayer.getUniqueId());
	}

	/**
	 * Read PlayerSettings From database into Memory when player joins
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!containsKey(player)) {
			PlayerBalances playerBalances = new PlayerBalances();
			PlayerBalance playerBalance = new PlayerBalance(player);
			playerBalances.putPlayerBalance(playerBalance);
			mBalances.put(player.getUniqueId(), playerBalances);
			load(player);
		}
		plugin.getEconomyManager().adjustAmountInInventoryToBalance(player);
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

		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(player);
		ps.setLastKnownWorldGrp(plugin.getWorldGroupManager().getCurrentWorldGroup(player));
		plugin.getPlayerSettingsManager().setPlayerSettings(player, ps);

		// update Essentials balance
		if (EssentialsCompat.isSupported()) {
			final double balance = getPlayerBalance(player).getBalance();
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
		plugin.getDataStoreManager().requestPlayerBalances(offlinePlayer, new IDataCallback<PlayerBalances>() {

			@Override
			public void onCompleted(PlayerBalances ps) {
				String worldGroup;
				GameMode gamemode;
				if (offlinePlayer.isOnline()) {
					Player player = (Player) offlinePlayer;
					worldGroup = plugin.getWorldGroupManager().getCurrentWorldGroup(player);
					gamemode = player.getGameMode();
				} else {
					worldGroup = plugin.getWorldGroupManager().getDefaultWorldgroup();
					gamemode = plugin.getWorldGroupManager().getDefaultGameMode();
				}
				if (!ps.has(worldGroup, gamemode)) {
					PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
					ps.putPlayerBalance(pb);
					setPlayerBalance(offlinePlayer, pb);
				}
				mBalances.put(offlinePlayer.getUniqueId(), ps);

				if (offlinePlayer.isOnline()) {
					double amountInInventory = plugin.getEconomyManager().getAmountInInventory((Player) offlinePlayer);
					PlayerBalance pb = getPlayerBalance(offlinePlayer);
					if (Misc.round(amountInInventory) != Misc.round(pb.getBalance())
							+ Misc.round(pb.getBalanceChanges())) {
						double change = pb.getBalanceChanges();
						plugin.getMessages().debug("Balance was changed while %s was offline. New balance is %s.",
								offlinePlayer.getName(), pb.getBalance() + change);
						pb.setBalance(pb.getBalance() + change);
						pb.setBalanceChanges(0);
						setPlayerBalance(offlinePlayer, pb);
						if (change > 0)
							plugin.getEconomyManager().addBagOfGoldPlayer((Player) offlinePlayer, change);
						else
							plugin.getEconomyManager().removeBagOfGoldPlayer((Player) offlinePlayer, change);
					}
				}

				if (!offlinePlayer.hasPlayedBefore()) {
					plugin.getEconomyManager().depositPlayer(offlinePlayer,
							plugin.getWorldGroupManager().getCurrentStartingBalance(offlinePlayer));
				}
				if (offlinePlayer.isOnline())
					plugin.getEconomyManager().adjustAmountInInventoryToBalance((Player) offlinePlayer);
			}

			@Override
			public void onError(Throwable error) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BagOfGold][ERROR] " + offlinePlayer.getName()
						+ " is new, creating user in database.");
				mBalances.put(offlinePlayer.getUniqueId(), new PlayerBalances());
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
		return mBalances.containsKey(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		ps.setBalance(0);
		ps.setBalanceChanges(0);
		setPlayerBalance(player, ps);
		plugin.getMessages().debug("PlayerBalancManager: player died balance=0");
	}

}
