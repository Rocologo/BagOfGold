package one.lindegaard.BagOfGold.update;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import one.lindegaard.BagOfGold.BagOfGold;

public class SpigetUpdater {

	private BagOfGold plugin;

	public SpigetUpdater(BagOfGold plugin) {
		this.plugin = plugin;
	}

	private static SpigetUpdate spigetUpdate = null;
	private static UpdateStatus updateAvailable = UpdateStatus.UNKNOWN;
	private static String currentJarFile = "";
	private static String newDownloadVersion = "";

	public static SpigetUpdate getSpigetUpdate() {
		return spigetUpdate;
	}

	public UpdateStatus getUpdateAvailable() {
		return updateAvailable;
	}

	public static void setUpdateAvailable(UpdateStatus b) {
		updateAvailable = b;
	}

	public static String getCurrentJarFile() {
		return currentJarFile;
	}

	public static void setCurrentJarFile(String name) {
		currentJarFile = name;
	}

	public String getNewDownloadVersion() {
		return newDownloadVersion;
	}

	public void setNewDownloadVersion(String newDownloadVersion) {
		SpigetUpdater.newDownloadVersion = newDownloadVersion;
	}

	public void hourlyUpdateCheck(final CommandSender sender, boolean updateCheck, final boolean silent) {
		long seconds = plugin.getConfigManager().checkEvery;
		if (seconds < 900) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED
					+ "[BagOfGold][Warning] check_every in your config.yml is too low. A low number can cause server crashes. The number is raised to 900 seconds = 15 minutes.");
			seconds = 900;
		}
		if (updateCheck) {
			new BukkitRunnable() {
				@Override
				public void run() {
					// pluginUpdateCheck(sender, true, false);
					checkForUpdate(sender, true, false);
				}
			}.runTaskTimer(BagOfGold.getInstance(), 0L, seconds * 20L);
		}
	}

	public static boolean downloadAndUpdateJar() {
		boolean succes = false;
		final String OS = System.getProperty("os.name");
		if (OS.indexOf("Win") >= 0) {
			succes = spigetUpdate.downloadUpdate();
			if (succes) {
				File downloadedJar = new File("plugins/update/BagOfGold-" + newDownloadVersion + ".jar");
				File newJar = new File("plugins/update/BagOfGold.jar");
				if (newJar.exists())
					newJar.delete();
				downloadedJar.renameTo(newJar);
				return true;
			}
		} else {
			if (updateAvailable != UpdateStatus.RESTART_NEEDED) {
				succes = spigetUpdate.downloadUpdate();
				if (succes) {
					File currentJar = new File("plugins/" + getCurrentJarFile());
					File disabledJar = new File("plugins/" + getCurrentJarFile() + ".old");
					int count = 0;
					while (disabledJar.exists() && count++ < 100) {
						disabledJar = new File("plugins/" + getCurrentJarFile() + ".old" + count);
					}
					if (!disabledJar.exists()) {
						currentJar.renameTo(disabledJar);
						File downloadedJar = new File(
								"plugins/BagOfGold/update/BagOfGold-" + newDownloadVersion + ".jar");
						File newJar = new File("plugins/BagOfGold-" + newDownloadVersion + ".jar");
						downloadedJar.renameTo(newJar);
						updateAvailable = UpdateStatus.RESTART_NEEDED;
						return true;
					}
				}
			}
		}
		return false;
	}

	public void checkForUpdate(final CommandSender sender, boolean updateCheck, final boolean silent) {

		if (updateCheck) {
			if (!silent)
				BagOfGold.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] "
						+ plugin.getMessages().getString("bagofgold.commands.update.check"));
			if (updateAvailable != UpdateStatus.RESTART_NEEDED) {

				spigetUpdate = new SpigetUpdate(plugin, 49332);
				spigetUpdate.setVersionComparator(VersionComparator.SEM_VER);
				spigetUpdate.setUserAgent("BagOfGold-" + plugin.getDescription().getVersion());

				spigetUpdate.checkForUpdate(new UpdateCallback() {

					@Override
					public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
						//// A new version is available
						Bukkit.getConsoleSender()
								.sendMessage(ChatColor.RED + "A new version is available: " + newVersion);
						updateAvailable = UpdateStatus.AVAILABLE;
						newDownloadVersion = newVersion;
						sender.sendMessage(ChatColor.GREEN + "[BagOfGold] "
								+ plugin.getMessages().getString("bagofgold.commands.update.version-found"));
						if (plugin.getConfigManager().autoupdate) {
							spigetUpdate.downloadUpdate();
							sender.sendMessage(ChatColor.GREEN + "[BagOfGold] "
									+ plugin.getMessages().getString("bagofgold.commands.update.complete"));
						} else
							sender.sendMessage(ChatColor.GREEN + "[BagOfGold] "
									+ plugin.getMessages().getString("bagofgold.commands.update.help"));
					}

					@Override
					public void upToDate() {
						//// Plugin is up-to-date
						if (!silent)
							sender.sendMessage(ChatColor.GOLD + "[BagOfGold] "
									+ plugin.getMessages().getString("bagofgold.commands.update.no-update"));
					}
				});
			}
		}
	}
}
