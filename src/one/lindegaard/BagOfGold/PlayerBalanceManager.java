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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import one.lindegaard.BagOfGold.compatibility.EssentialsCompat;
import one.lindegaard.BagOfGold.storage.DataStoreException;
import one.lindegaard.BagOfGold.storage.IDataCallback;
import one.lindegaard.BagOfGold.storage.UserNotFoundException;
import one.lindegaard.MobHunting.util.Misc;

public class PlayerBalanceManager implements Listener {

	private HashMap<UUID, PlayerBalances> mBalances = new HashMap<UUID, PlayerBalances>();

	private BagOfGold plugin;

	/**
	 * Constructor for the PlayerBalanceManager
	 */
	PlayerBalanceManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public HashMap<UUID, PlayerBalances> getBalances(){
		return mBalances;
	}
	
	public PlayerBalance getPlayerBalances(OfflinePlayer offlinePlayer) {
		String worldGroup = plugin.getWorldGroupManager().getCurrentWorldGroup(offlinePlayer);
		GameMode gamemode = plugin.getWorldGroupManager().getCurrentGameMode(offlinePlayer);
		return getPlayerBalances(offlinePlayer, worldGroup, gamemode);
	}

	public PlayerBalance getPlayerBalances(OfflinePlayer offlinePlayer, String worldGroup, GameMode gamemode) {
		if (mBalances.containsKey(offlinePlayer.getUniqueId()))
			// offlinePlayer is in the Database
			if (mBalances.get(offlinePlayer.getUniqueId()).has(worldGroup, gamemode))
				return mBalances.get(offlinePlayer.getUniqueId()).getPlayerBalance(worldGroup, gamemode);
			else {
				// offlinePlayer does have a balance for this
				// worldgroup-gamemode. Create it with default values
				PlayerBalances ps = new PlayerBalances();
				PlayerBalance pb = new PlayerBalance(offlinePlayer, worldGroup, gamemode);
				ps.putPlayerBalance(pb);
				setPlayerBalance(offlinePlayer, pb);
				checkPlayerBalanceAgainstInventory(pb);
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
				plugin.getMessages().debug("Insert new PlayerBalances for %s to database.", offlinePlayer.getName());
				setPlayerBalance(offlinePlayer, pb);
			}
			mBalances.put(offlinePlayer.getUniqueId(), ps);
			checkPlayerBalanceAgainstInventory(pb);
			return pb;
		}
	}

	public void checkPlayerBalanceAgainstInventory(PlayerBalance pb) {
		if (pb.getPlayer().isOnline()) {
			Player player = (Player) pb.getPlayer();
			double amountInInventory = plugin.getEconomyManager().getAmountInInventory(player);
			double diff = Misc.round(amountInInventory) - Misc.round(pb.getBalance())
					- Misc.round(pb.getBalanceChanges());
			plugin.getMessages().debug("PlayerBalanceManager: checking amountInInventory and balance amt=%s,pb=%s,diff=%s",
					amountInInventory,pb.toString(),diff);
			
			if (diff > 0)
				plugin.getEconomyManager().removeBagOfGoldPlayer(player, diff);
			else if (diff < 0)
				plugin.getEconomyManager().addBagOfGoldPlayer2(player, -diff);
		}
	}

	// TODO: remove parameter offlinePlayer
	public void setPlayerBalance(OfflinePlayer offlinePlayer, PlayerBalance playerBalance) {
		if (!mBalances.containsKey(offlinePlayer.getUniqueId())) {
			plugin.getMessages().debug("PlayerBalanceManager: mBalances does not contain player %s",
					offlinePlayer.getName());
			PlayerBalances ps = new PlayerBalances();
			ps.putPlayerBalance(playerBalance);
			mBalances.put(offlinePlayer.getUniqueId(), ps);
		} else {
			plugin.getMessages().debug("PlayerBalanceManager: insert playerBalance into mBalances for player %s",
					offlinePlayer.getName());
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
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		if (!containsKey(player)) {
			PlayerBalances playerBalances = new PlayerBalances();
			PlayerBalance playerBalance = new PlayerBalance(player);
			playerBalances.putPlayerBalance(playerBalance);
			mBalances.put(player.getUniqueId(), playerBalances);
			load(player);
		}
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
			final double balance = getPlayerBalances(player).getBalance();
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
				BagOfGold.getInstance().getMessages().debug("PlayerBalanceManager load1=%s", ps.toString());
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

				BagOfGold.getInstance().getMessages().debug("PlayerBalanceManager load2=%s", ps.toString());
				
				/**
				 * if (offlinePlayer.isOnline()) { PlayerBalance pb =
				 * getPlayerBalances(offlinePlayer); double change =
				 * pb.getBalanceChanges(); plugin.getMessages().debug("Balance
				 * was changed while %s was offline. New balance is %s.",
				 * offlinePlayer.getName(), pb.getBalance() + change);
				 * pb.setBalance(pb.getBalance() + change);
				 * pb.setBalanceChanges(0); if (change > 0)
				 * plugin.getEconomyManager().addBagOfGoldPlayer((Player)
				 * offlinePlayer, change); else
				 * plugin.getEconomyManager().removeBagOfGoldPlayer((Player)
				 * offlinePlayer, change); setPlayerBalance(offlinePlayer, pb);
				 * }
				 **/

				if (!offlinePlayer.hasPlayedBefore()) {
					plugin.getEconomyManager().depositPlayer(offlinePlayer,
							plugin.getWorldGroupManager().getCurrentStartingBalance(offlinePlayer));
				}
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

}
