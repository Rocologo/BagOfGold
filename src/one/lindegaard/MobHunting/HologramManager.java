package one.lindegaard.MobHunting;

import java.util.HashMap;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sainttx.holograms.api.Hologram;

import one.lindegaard.MobHunting.compatibility.HologramsCompat;
import one.lindegaard.MobHunting.compatibility.HologramsHelper;
import one.lindegaard.MobHunting.compatibility.HolographicDisplaysCompat;
import one.lindegaard.MobHunting.compatibility.HolographicDisplaysHelper;
import one.lindegaard.MobHunting.leaderboard.HologramLeaderboard;

public class HologramManager {

	private MobHunting plugin;

	private HashMap<String, HologramLeaderboard> holograms = new HashMap<>();

	public HologramManager(MobHunting plugin) {
		this.plugin=plugin;
	}

	public HashMap<String, HologramLeaderboard> getHolograms() {
		return holograms;
	}

	public void createHolographicLeaderboard(HologramLeaderboard hologramLeaderboard) {
		holograms.put(hologramLeaderboard.getHologramName(), hologramLeaderboard);
		if (HologramsCompat.isSupported())
			HologramsHelper.createHologram(hologramLeaderboard);
		else if (HolographicDisplaysCompat.isSupported())
			HolographicDisplaysHelper.createHologram(hologramLeaderboard);
		hologramLeaderboard.update();
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
		if (HologramsCompat.isSupported() || HolographicDisplaysCompat.isSupported()) {
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
