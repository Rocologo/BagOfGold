package one.lindegaard.BagOfGold;

import java.io.IOException;
import java.net.URL;

import org.bukkit.Bukkit;
import org.mcstats_mh.Metrics;
import org.mcstats_mh.Metrics.Graph;

public class MetricsManager {

	// Metrics
	private Metrics metrics;
	private Graph automaticUpdatesGraph;
	private BagOfGold plugin;

	private org.bstats.bukkit.Metrics bStatsMetrics;

	public MetricsManager(BagOfGold plugin) {
		this.plugin = plugin;
	}

	public void startBStatsMetrics() {
		bStatsMetrics = new org.bstats.bukkit.Metrics(plugin);
		
		bStatsMetrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("language", () -> BagOfGold.getConfigManager().language ));

	}

	public void startMetrics() {
		try {
			metrics = new Metrics(plugin);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		automaticUpdatesGraph = metrics.createGraph("# of installations with automatic update");
		automaticUpdatesGraph.addPlotter(new Metrics.Plotter("Amount") {
			@Override
			public int getValue() {
				return BagOfGold.getConfigManager().autoupdate ? 1 : 0;
			}
		});
		metrics.addGraph(automaticUpdatesGraph);

		metrics.start();
		Messages.debug("Metrics started");
		Bukkit.getScheduler().runTaskTimerAsynchronously(BagOfGold.getInstance(), new Runnable() {
			public void run() {
				try {
					// make a URL to MCStats.org
					URL url = new URL("http://mcstats.org");
					if (HttpTools.isHomePageReachable(url)) {
						metrics.enable();
					} else {
						metrics.disable();
						Messages.debug("Http://mcstats.org seems to be down");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}, 100, 72000);

	}
}
