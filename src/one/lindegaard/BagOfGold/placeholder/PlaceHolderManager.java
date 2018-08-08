package one.lindegaard.BagOfGold.placeholder;

import java.util.HashMap;
//import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import one.lindegaard.BagOfGold.BagOfGold;
//import one.lindegaard.BagOfGold.storage.IDataCallback;

public class PlaceHolderManager implements Listener { //, IDataCallback<List<StatStore>> {

	private BagOfGold plugin;
	private static HashMap<UUID, PlaceHolderData> placeHolders = new HashMap<UUID, PlaceHolderData>();
	private BukkitTask mUpdater = null;

	public PlaceHolderManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getMessages().debug("PlaceHolderManager started");
	}

	public HashMap<UUID, PlaceHolderData> getPlaceHolders() {
		return placeHolders;
	}

	public void updateRanks() {
		//plugin.getDataStoreManager().requestStats(StatType.KillsTotal, TimePeriod.AllTime, 2000, this);
	}

	private class Updater implements Runnable {
		@Override
		public void run() {
			updateRanks();
		}
	}

	public void shutdown() {
		mUpdater.cancel();
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		placeHolders.put(event.getPlayer().getUniqueId(), new PlaceHolderData());
		//plugin.getDataStoreManager().requestStats(StatType.KillsTotal, TimePeriod.AllTime, 2000, this);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerQuit(PlayerQuitEvent event) {
		placeHolders.remove(event.getPlayer().getUniqueId());
	}

	/**
	@Override
	public void onCompleted(List<StatStore> data) {
		int n = 0;
		for (StatStore statStore : data) {
			n++;
			if (statStore.getPlayer().isOnline()) {
				PlaceHolderData placeholder;
				if (placeHolders.containsKey(statStore.getPlayer().getUniqueId()))
					placeholder = placeHolders.get(statStore.getPlayer().getUniqueId());
				else
					placeholder = new PlaceHolderData();
				placeholder.setTotal_kills(statStore.getAmount());
				placeholder.setTotal_cash(statStore.getCash());
				placeholder.setRank(n);
				placeHolders.put(statStore.getPlayer().getUniqueId(), placeholder);
			}
		}
		if (!placeHolders.isEmpty())
			plugin.getMessages().debug("Refreshed %s ranks.", placeHolders.size());
	}

	@Override
	public void onError(Throwable error) {
		// TODO Auto-generated method stub
	}**/

}
