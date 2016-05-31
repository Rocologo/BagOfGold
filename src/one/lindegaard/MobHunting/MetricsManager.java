package one.lindegaard.MobHunting;

import java.io.IOException;

import org.mcstats.Metrics;
import org.mcstats.Metrics.Graph;

import one.lindegaard.MobHunting.compatability.ActionBarCompat;
import one.lindegaard.MobHunting.compatability.BarAPICompat;
import one.lindegaard.MobHunting.compatability.BattleArenaCompat;
import one.lindegaard.MobHunting.compatability.BossBarAPICompat;
import one.lindegaard.MobHunting.compatability.CitizensCompat;
import one.lindegaard.MobHunting.compatability.DisguiseCraftCompat;
import one.lindegaard.MobHunting.compatability.EssentialsCompat;
import one.lindegaard.MobHunting.compatability.IDisguiseCompat;
import one.lindegaard.MobHunting.compatability.LibsDisguisesCompat;
import one.lindegaard.MobHunting.compatability.MobArenaCompat;
import one.lindegaard.MobHunting.compatability.MyPetCompat;
import one.lindegaard.MobHunting.compatability.MythicMobsCompat;
import one.lindegaard.MobHunting.compatability.PVPArenaCompat;
import one.lindegaard.MobHunting.compatability.TitleAPICompat;
import one.lindegaard.MobHunting.compatability.TitleManagerCompat;
import one.lindegaard.MobHunting.compatability.VanishNoPacketCompat;
import one.lindegaard.MobHunting.compatability.WorldEditCompat;
import one.lindegaard.MobHunting.compatability.WorldGuardCompat;

public class MetricsManager {

	// Metrics
	private Metrics metrics;
	private Graph automaticUpdatesGraph, databaseGraph, integrationsGraph, titleManagerGraph;

	public MetricsManager() {
	}

	public void startMetrics() {
		try {
			metrics = new Metrics(MobHunting.getInstance());

			databaseGraph = metrics.createGraph("Database used for MobHunting");
			if (MobHunting.getConfigManager().databaseType.equalsIgnoreCase("MySQL"))
				databaseGraph.addPlotter(new Metrics.Plotter("MySQL") {
					@Override
					public int getValue() {
						return 1;
					}
				});
			else if (MobHunting.getConfigManager().databaseType.equalsIgnoreCase("SQLite"))
				databaseGraph.addPlotter(new Metrics.Plotter("SQLite") {
					@Override
					public int getValue() {
						return 1;
					}
				});
			else {
				databaseGraph.addPlotter(new Metrics.Plotter(MobHunting.getConfigManager().databaseType) {
					@Override
					public int getValue() {
						return 1;
					}
				});
			}
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
						Class cls = Class.forName("pgDev.bukkit.DisguiseCraft.disguise.DisguiseType");
						return DisguiseCraftCompat.isSupported() ? 1 : 0;
					} catch (ClassNotFoundException e) {
						// MobHunting.debug("DisguiseCraft is not installed -
						// reported 0");
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
						// de.robingrether.idisguise.disguise.DisguiseType
						Class cls = Class.forName("de.robingrether.idisguise.disguise.DisguiseType");
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
						Class cls = Class.forName("me.libraryaddict.disguise.disguisetypes.DisguiseType");
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
			integrationsGraph.addPlotter(new Metrics.Plotter("BattleArena") {
				@Override
				public int getValue() {
					return BattleArenaCompat.isSupported() ? 1 : 0;
				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("WorldGuard") {
				@Override
				public int getValue() {
					try {
						@SuppressWarnings({ "rawtypes", "unused" })
						Class cls = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
						return WorldGuardCompat.isSupported() ? 1 : 0;
					} catch (ClassNotFoundException e) {
						return 0;
					}

				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("WorldEdit") {
				@Override
				public int getValue() {
					try {
						@SuppressWarnings({ "rawtypes", "unused" })
						Class cls = Class.forName("com.sk89q.worldedit.bukkit.WorldEditPlugin");
						return WorldEditCompat.isSupported() ? 1 : 0;
					} catch (ClassNotFoundException e) {
						return 0;
					}

				}
			});
			integrationsGraph.addPlotter(new Metrics.Plotter("VanishNoPacket") {
				@Override
				public int getValue() {
					return VanishNoPacketCompat.isSupported() ? 1 : 0;
				}
			});
			metrics.addGraph(integrationsGraph);

			titleManagerGraph = metrics.createGraph("TitleManagers");
			titleManagerGraph.addPlotter(new Metrics.Plotter("BossBarAPI") {
				@Override
				public int getValue() {
					return BossBarAPICompat.isSupported() ? 1 : 0;
				}
			});
			titleManagerGraph.addPlotter(new Metrics.Plotter("TitleAPI") {
				@Override
				public int getValue() {
					return TitleAPICompat.isSupported() ? 1 : 0;
				}
			});
			titleManagerGraph.addPlotter(new Metrics.Plotter("BarAPI") {
				@Override
				public int getValue() {
					return BarAPICompat.isSupported() ? 1 : 0;
				}
			});
			titleManagerGraph.addPlotter(new Metrics.Plotter("TitleManager") {
				@Override
				public int getValue() {
					return TitleManagerCompat.isSupported() ? 1 : 0;
				}
			});
			titleManagerGraph.addPlotter(new Metrics.Plotter("ActionBar") {
				@Override
				public int getValue() {
					return ActionBarCompat.isSupported() ? 1 : 0;
				}
			});
			metrics.addGraph(titleManagerGraph);

			automaticUpdatesGraph = metrics.createGraph("Installations with automatic update");
			automaticUpdatesGraph.addPlotter(new Metrics.Plotter("Amount") {
				@Override
				public int getValue() {
					return MobHunting.getConfigManager().autoupdate ? 1 : 0;
				}
			});
			metrics.addGraph(automaticUpdatesGraph);

			metrics.enable();
			metrics.start();
			MobHunting.debug("Metrics started");
		} catch (IOException e) {
			MobHunting.debug("Failed to start Metrics!");
		}

	}

}
