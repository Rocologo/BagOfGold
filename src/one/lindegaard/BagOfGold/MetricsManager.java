package one.lindegaard.BagOfGold;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;

import one.lindegaard.BagOfGold.compatibility.ActionAnnouncerCompat;
import one.lindegaard.BagOfGold.compatibility.ActionBarAPICompat;
import one.lindegaard.BagOfGold.compatibility.ActionbarCompat;
import one.lindegaard.BagOfGold.compatibility.BarAPICompat;
import one.lindegaard.BagOfGold.compatibility.BossBarAPICompat;
import one.lindegaard.BagOfGold.compatibility.CMICompat;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.BagOfGold.compatibility.EssentialsCompat;
import one.lindegaard.BagOfGold.compatibility.PerWorldInventoryCompat;
import one.lindegaard.BagOfGold.compatibility.PlaceholderAPICompat;
import one.lindegaard.BagOfGold.compatibility.ProtocolLibCompat;
import one.lindegaard.BagOfGold.compatibility.TitleAPICompat;
import one.lindegaard.BagOfGold.compatibility.TitleManagerCompat;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.HttpTools;
import one.lindegaard.Core.HttpTools.httpCallback;

import org.bstats.bukkit.Metrics;

public class MetricsManager {

	private BagOfGold plugin;
	private boolean started = false;

	private Metrics bStatsMetrics;

	public MetricsManager(BagOfGold plugin) {
		this.plugin = plugin;
	}

	public void start() {
		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			public void run() {
				try {
					URL url = new URL("https://bstats.org/");
					if (!started) {
						HttpTools.isHomePageReachable(url, new httpCallback() {

							@Override
							public void onSuccess() {
								startBStatsMetrics();
								plugin.getMessages().debug("Metrics reporting to Https://bstats.org has started.");
								started = true;
							}

							@Override
							public void onError() {
								started = false;
								plugin.getMessages().debug("https://bstats.org/ seems to be down");
							}
						});
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 100L, 72000L);
	}

	public void startBStatsMetrics() {
		// https://bstats.org/what-is-my-plugin-id
		bStatsMetrics = new Metrics(plugin, 1783);
		bStatsMetrics.addCustomChart(
				new Metrics.SimplePie("database_used_for_bagofgold", () -> plugin.getConfigManager().databaseType));
		bStatsMetrics
				.addCustomChart(new Metrics.AdvancedPie("other_integrations", new Callable<Map<String, Integer>>() {
					@Override
					public Map<String, Integer> call() throws Exception {
						Map<String, Integer> valueMap = new HashMap<>();
						valueMap.put("Citizens", CitizensCompat.isSupported() ? 1 : 0);
						valueMap.put("Essentials", EssentialsCompat.isSupported() ? 1 : 0);
						valueMap.put("PerWorldInventory", PerWorldInventoryCompat.isSupported() ? 1 : 0);
						valueMap.put("ProtocolLib", ProtocolLibCompat.isSupported() ? 1 : 0);
						return valueMap;
					}

				}));
		bStatsMetrics.addCustomChart(new Metrics.SimplePie("language", () -> plugin.getConfigManager().language));
		
		bStatsMetrics.addCustomChart(new Metrics.SimplePie("item_type", () -> Core.getConfigManager().rewardItemtype));
		
		bStatsMetrics.addCustomChart(new Metrics.SimplePie("economy_base", () -> plugin.getEconomyManager().getEconomyAPI()));
	
		bStatsMetrics.addCustomChart(new Metrics.AdvancedPie("titlemanagers", new Callable<Map<String, Integer>>() {
			@Override
			public Map<String, Integer> call() throws Exception {
				Map<String, Integer> valueMap = new HashMap<>();
				//actionbar
				valueMap.put("TitleAPI", TitleAPICompat.isSupported() ? 1 : 0);
				valueMap.put("TitleManager", TitleManagerCompat.isSupported() ? 1 : 0);
				valueMap.put("ActionBar", ActionbarCompat.isSupported() ? 1 : 0);
				valueMap.put("ActionBarAPI", ActionBarAPICompat.isSupported() ? 1 : 0);
				valueMap.put("ActionAnnouncer", ActionAnnouncerCompat.isSupported() ? 1 : 0);
				valueMap.put("CMI", CMICompat.isSupported() ? 1 : 0);
				//bossbar
				valueMap.put("BarAPI", BarAPICompat.isSupported() ? 1 : 0);
				valueMap.put("BossBarAPI", BossBarAPICompat.isSupported() ? 1 : 0);
				//Placeholder
				valueMap.put("PlaceholderAPI", PlaceholderAPICompat.isSupported() ? 1 : 0);
				return valueMap;
			}
		}));

	}
}
