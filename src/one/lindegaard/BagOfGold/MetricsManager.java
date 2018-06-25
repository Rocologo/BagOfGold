package one.lindegaard.BagOfGold;

import java.io.IOException;
import java.net.URL;

import org.bukkit.Bukkit;

import one.lindegaard.MobHunting.HttpTools;

public class MetricsManager {

	private BagOfGold plugin;

	private org.bstats.bukkit.Metrics bStatsMetrics;

	public MetricsManager(BagOfGold plugin) {
		this.plugin = plugin;
	}

	public void start(){
		plugin.getMessages().debug("Metrics started");
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
			public void run() {
				try {
					URL url = new URL("https://bstats.org/");
					if (HttpTools.isHomePageReachable(url)) {
						startBStatsMetrics();
					} else {
						plugin.getMessages().debug("https://bstats.org/ seems to be down");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}, 100, 72000);
	}
	
	private void startBStatsMetrics() {
		bStatsMetrics = new org.bstats.bukkit.Metrics(plugin);
		bStatsMetrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("language", () -> plugin.getConfigManager().language ));
	}

}
