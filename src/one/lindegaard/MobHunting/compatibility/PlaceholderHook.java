package one.lindegaard.MobHunting.compatibility;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.StatType;
import one.lindegaard.MobHunting.storage.IDataCallback;
import one.lindegaard.MobHunting.storage.StatStore;
import one.lindegaard.MobHunting.storage.TimePeriod;

public class PlaceholderHook extends EZPlaceholderHook implements Listener, IDataCallback<List<StatStore>> {

	private HashMap<UUID, Integer> rank = new HashMap<UUID, Integer>();

	public PlaceholderHook(Plugin plugin) {
		super(plugin, "mobhunting");
		Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		// placeholder: %mobhunting_ping%
		if (identifier.equals("ping")) {
			return "pong";
		}

		// always check if the player is null for placeholders related to the
		// player!
		if (player == null) {
			return "";
		}

		// placeholder: %mobhunting_total_kills_alltime_rank%
		if (identifier.equals("total_kills_alltime_rank")) {
			if (rank.containsKey(player.getUniqueId()))
				return String.valueOf(rank.get(player.getUniqueId()));
			else
				return "???";
		}

		// anything else someone types is invalid because we never defined
		// %customplaceholder_<what they want a value for>%
		// we can just return null so the placeholder they specified is not
		// replaced.
		return null;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		//final Player player = event.getPlayer();
		MobHunting.getDataStoreManager().requestStats(StatType.KillsTotal, TimePeriod.AllTime, 2000, this);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		if (rank.containsKey(player.getUniqueId()))
			rank.remove(player.getUniqueId());
	}

	@Override
	public void onCompleted(List<StatStore> data) {
		int n = 0;
		for (StatStore res : data) {
			if (res.getPlayer().isOnline()) {
				Messages.debug("PlacerholderHook: added %s to rank", res.getPlayer().getName());
				rank.put(res.getPlayer().getUniqueId(), n++);
			}
		}
	}

	@Override
	public void onError(Throwable error) {
		// TODO Auto-generated method stub
	}

}
