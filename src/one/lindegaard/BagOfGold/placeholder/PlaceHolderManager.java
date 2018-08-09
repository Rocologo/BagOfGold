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

import one.lindegaard.BagOfGold.BagOfGold;

public class PlaceHolderManager implements Listener { 

	private BagOfGold plugin;
	private static HashMap<UUID, PlaceHolderData> placeHolders = new HashMap<UUID, PlaceHolderData>();

	public PlaceHolderManager(BagOfGold plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
		plugin.getMessages().debug("PlaceHolderManager started");
	}

	public HashMap<UUID, PlaceHolderData> getPlaceHolders() {
		return placeHolders;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		placeHolders.put(event.getPlayer().getUniqueId(), new PlaceHolderData());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerQuit(PlayerQuitEvent event) {
		placeHolders.remove(event.getPlayer().getUniqueId());
	}
}
