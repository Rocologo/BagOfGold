package one.lindegaard.BagOfGold;

public class MetricsManager {

	// Metrics
	private BagOfGold plugin;

	private org.bstats.bukkit.Metrics bStatsMetrics;

	public MetricsManager(BagOfGold plugin) {
		this.plugin = plugin;
	}

	public void startBStatsMetrics() {
		bStatsMetrics = new org.bstats.bukkit.Metrics(plugin);
		bStatsMetrics.addCustomChart(new org.bstats.bukkit.Metrics.SimplePie("language", () -> plugin.getConfigManager().language ));
	}

}
