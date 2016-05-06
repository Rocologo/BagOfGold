package one.lindegaard.MobHunting;

import java.io.IOException;

import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

import one.lindegaard.MobHunting.compatability.CitizensCompat;
import one.lindegaard.MobHunting.compatability.DisguiseCraftCompat;
import one.lindegaard.MobHunting.compatability.EssentialsCompat;
import one.lindegaard.MobHunting.compatability.IDisguiseCompat;
import one.lindegaard.MobHunting.compatability.LibsDisguisesCompat;
import one.lindegaard.MobHunting.compatability.MobArenaCompat;
import one.lindegaard.MobHunting.compatability.MyPetCompat;
import one.lindegaard.MobHunting.compatability.MythicMobsCompat;
import one.lindegaard.MobHunting.compatability.PVPArenaCompat;
import one.lindegaard.MobHunting.compatability.WorldGuardCompat;

public class MetricsManager {

	// Metrics
	private Metrics metrics;
	private Graph automaticUpdatesGraph, databaseGraph, integrationsGraph;

	public MetricsManager() {
	}

	public void startMetrics() {
		try {
			metrics = new Metrics(MobHunting.getInstance());
			automaticUpdatesGraph = metrics.createGraph("Installations with automatic update");
			automaticUpdatesGraph.addPlotter(new Metrics.Plotter("Amount") {
				@Override
				public int getValue() {
					return MobHunting.getConfigManager().autoupdate ? 1 : 0;
				}
			});
			metrics.addGraph(automaticUpdatesGraph);

			databaseGraph = metrics.createGraph("Database used for MobHunting");
			databaseGraph.addPlotter(new Metrics.Plotter("MySQL") {
				@Override
				public int getValue() {
					return MobHunting.getConfigManager().databaseType.equalsIgnoreCase("MySQL") ? 1 : 0;
				}
			});
			databaseGraph.addPlotter(new Metrics.Plotter("SQLite") {
				@Override
				public int getValue() {
					return MobHunting.getConfigManager().databaseType.equalsIgnoreCase("SQLite") ? 1 : 0;
				}
			});
			metrics.addGraph(databaseGraph);

			integrationsGraph = metrics.createGraph("MobHunting integrations");
			integrationsGraph.addPlotter(new Metrics.Plotter("Citizens") {
				@Override
				public int getValue() {
					return CitizensCompat.isCitizensSupported() ? 1 : 0;
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("Essentials") {
				@Override
				public int getValue() {
					return EssentialsCompat.isSupported() ? 1 : 0;
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("MyPet") {
				@Override
				public int getValue() {
					return MyPetCompat.isSupported() ? 1 : 0;
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("MythicMobs") {
				@Override
				public int getValue() {
					return MythicMobsCompat.isSupported() ? 1 : 0;
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("DisguisesCraft") {
				@Override
				public int getValue() {
					try {
						@SuppressWarnings({ "rawtypes", "unused" })
						// Class cls = Class
						// .forName("pgDev.bukkit.DisguiseCraft.disguise.DisguiseType");
						Class cls = Class.forName("pgDev.bukkit.DisguiseCraft");
						return DisguiseCraftCompat.isSupported() ? 1 : 0;
					} catch (ClassNotFoundException e) {
						// DisguiseCraft is not present.
						return 0;
					}
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("iDisguises") {
				@Override
				public int getValue() {
					try {
						@SuppressWarnings({ "rawtypes", "unused" })
						Class cls = Class.forName("de.robingrether.idisguise");
						return IDisguiseCompat.isSupported() ? 1 : 0;
					} catch (ClassNotFoundException e) {
						return 0;
					}
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("LibsDisguises") {
				@Override
				public int getValue() {
					try {
						@SuppressWarnings({ "rawtypes", "unused" })
						Class cls = Class.forName("me.libraryaddict.disguise.DisguiseAPI");
						return LibsDisguisesCompat.isSupported() ? 1 : 0;
					} catch (ClassNotFoundException e) {
						return 0;
					}
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("MobArena") {
				@Override
				public int getValue() {
					return MobArenaCompat.isSupported() ? 1 : 0;
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("PvpArena") {
				@Override
				public int getValue() {
					return PVPArenaCompat.isSupported() ? 1 : 0;
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("WorldGuard") {
				@Override
				public int getValue() {
					try {
						@SuppressWarnings({ "rawtypes", "unused" })
						Class cls = Class.forName("com.sk89q.worldguard");
						return WorldGuardCompat.isSupported() ? 1 : 0;
					} catch (ClassNotFoundException e) {
						return 0;
					}

				}
			});
			metrics.addGraph(integrationsGraph);

			metrics.enable();
			metrics.start();
			MobHunting.debug("Metrics started");
		} catch (IOException e) {
			MobHunting.debug("Failed to start Metrics!");
		}

	}

}
