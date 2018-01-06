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

	private SpigetUpdate spigetUpdate = null;
	private UpdateStatus updateAvailable = UpdateStatus.UNKNOWN;
	private String currentJarFile = "";
	private String newDownloadVersion = "";

	public SpigetUpdate getSpigetUpdate() {
		return spigetUpdate;
	}

	public UpdateStatus getUpdateAvailable() {
		return updateAvailable;
	}

	public void setUpdateAvailable(UpdateStatus b) {
		updateAvailable = b;
	}

	public String getCurrentJarFile() {
		return currentJarFile;
	}

	public void setCurrentJarFile(String name) {
		currentJarFile = name;
	}

	public String getNewDownloadVersion() {
		return newDownloadVersion;
	}

	public void setNewDownloadVersion(String newDownloadVersion) {
		this.newDownloadVersion = newDownloadVersion;
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
					checkForUpdate(sender, true, false);
				}
			}.runTaskTimer(BagOfGold.getInstance(), 0L, seconds * 20L);
		}
	}

	public boolean downloadAndUpdateJar() {
		new BukkitRunnable() {
			int count = 0;
			boolean succes = spigetUpdate.downloadUpdate();
			final String OS = System.getProperty("os.name");

			@Override
			public void run() {
				if (count++ > 10) {
					Bukkit.getConsoleSender().sendMessage(
							ChatColor.RED + "[BagOfGold] No updates found. (No response from server after 10s)");
					this.cancel();
				} else {
					// Wait for the response
					if (succes) {
						if (OS.indexOf("Win") >= 0) {
							File downloadedJar = new File("plugins/update/" + currentJarFile);
							File newJar = new File("plugins/update/BagOfGold-" + newDownloadVersion + ".jar");
							if (newJar.exists())
								newJar.delete();
							downloadedJar.renameTo(newJar);
						}
					} else {
						if (updateAvailable != UpdateStatus.RESTART_NEEDED) {
							File currentJar = new File("plugins/" + currentJarFile);
							File disabledJar = new File("plugins/" + currentJarFile + ".old");
							int count = 0;
							while (disabledJar.exists() && count++ < 100) {
								disabledJar = new File("plugins/" + currentJarFile + ".old" + count);
							}
							if (!disabledJar.exists()) {
								currentJar.renameTo(disabledJar);
								File downloadedJar = new File("plugins/update/" + currentJarFile);
								File newJar = new File("plugins/BagOfGold-" + newDownloadVersion + ".jar");
								downloadedJar.renameTo(newJar);
								plugin.getMessages().debug("Moved plugins/update/" + currentJarFile + " to plugins/BagOfGold-"
										+ newDownloadVersion + ".jar");
								updateAvailable = UpdateStatus.RESTART_NEEDED;
							}
						}
					}
					this.cancel();
				}
			}

		}.runTaskTimer(plugin, 20L, 20L);
		return true;
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
						updateAvailable = UpdateStatus.AVAILABLE;
						newDownloadVersion = newVersion;
						sender.sendMessage(ChatColor.GREEN + "[BagOfGold] " + plugin.getMessages()
								.getString("bagofgold.commands.update.version-found", "newversion", newVersion));
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