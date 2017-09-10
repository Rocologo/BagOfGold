package one.lindegaard.MobHunting;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sainttx.holograms.api.Hologram;

import one.lindegaard.MobHunting.compatibility.HologramsCompat;
import one.lindegaard.MobHunting.compatibility.HologramsHelper;
import one.lindegaard.MobHunting.compatibility.HolographicDisplaysCompat;
import one.lindegaard.MobHunting.compatibility.HolographicDisplaysHelper;
import one.lindegaard.MobHunting.leaderboard.HologramLeaderboard;

public class HologramManager {

	MobHunting plugin;

	private HashMap<String, HologramLeaderboard> holograms = new HashMap<>();

	public HologramManager(MobHunting plugin) {
		this.plugin = plugin;
		int leaderboardUpdatePeriod = MobHunting.getConfigManager().leaderboardUpdatePeriod;
		if (leaderboardUpdatePeriod < 1200) {
			leaderboardUpdatePeriod = 1200;
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED
					+ "[MobHunting][Warning] leaderboard-update-period: in your config.yml is too low. Please raise it to 1200 or higher. Reccommended is 6000. ");
		}
	}

	public HashMap<String, HologramLeaderboard> getHolograms() {
		return holograms;
	}

	public void createHolographicLeaderboard(HologramLeaderboard hologramLeaderboard, Location location) {
		if (HologramsCompat.isSupported()) {
			HologramsHelper.createHologram(hologramLeaderboard, location);
			hologramLeaderboard.update();
		} else if (HolographicDisplaysCompat.isSupported()) {
			HolographicDisplaysHelper.createHologram(hologramLeaderboard, location);
			hologramLeaderboard.update();
		}
		holograms.put(hologramLeaderboard.getHologramName(), hologramLeaderboard);
	}

	public void deleteHolographicLeaderboard(String hologramName) {
		if (HologramsCompat.isSupported()) {
			Hologram hologram = HologramsCompat.getHologramManager().getHologram(hologramName);
			HologramsCompat.getHologramManager().deleteHologram(hologram);
		} else if (HolographicDisplaysCompat.isSupported()) {
			for (com.gmail.filoghost.holographicdisplays.api.Hologram hologram : HologramsAPI.getHolograms(plugin)) {
				if (hologram.getLocation().equals(holograms.get(hologramName).getLocation())) {
					HolographicDisplaysHelper.deleteHologram(hologram);
					break;
				}
			}
		}
		holograms.remove(hologramName);
	}

	public String listHolographicLeaderboard() {
		String str = "";
		if (HologramsCompat.isSupported()||HolographicDisplaysCompat.isSupported()) {
			if (holograms.size() == 0) {
				str = Messages.getString("mobhunting.holograms.no-holograms");
			} else {
				str = "Holograms: ";
				for (String hologramName : holograms.keySet()) {
					str = str + hologramName + ", ";
				}
				str = str.substring(0, str.length() - 2);
			}
		}
		return str;
	}

	public void updateHolographicLeaderboard(String hologramName) {
		if (HologramsCompat.isSupported() || HolographicDisplaysCompat.isSupported()) {
			holograms.get(hologramName).update();
		}
	}

}
