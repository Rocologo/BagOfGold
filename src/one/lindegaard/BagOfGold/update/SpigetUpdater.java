package one.lindegaard.BagOfGold.update;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.inventivetalent.update.spiget.SpigetUpdate;
import org.inventivetalent.update.spiget.UpdateCallback;
import org.inventivetalent.update.spiget.comparator.VersionComparator;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.CustomItemsLib.update.UpdateStatus;

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
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "[Warning] check_every in your config.yml is too low. A low number can cause server crashes. The number is raised to 900 seconds = 15 minutes.");
			seconds = 900;
		}
		if (updateCheck) {
			new BukkitRunnable() {
				@Override
				public void run() {
					checkForUpdate(sender, false, false);
				}
			}.runTaskTimer(BagOfGold.getInstance(), 20000L, seconds * 20L);
		}
	}

	public void checkForUpdate(final CommandSender sender, final boolean silent, boolean forceDownload) {
		if (!silent)
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ plugin.getMessages().getString("bagofgold.commands.update.check"));
		if (updateAvailable == UpdateStatus.RESTART_NEEDED)
			sender.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.GREEN
					+ plugin.getMessages().getString("bagofgold.commands.update.complete"));

		spigetUpdate = new SpigetUpdate(plugin, 49332);
		spigetUpdate.setVersionComparator(VersionComparator.EQUAL);
		spigetUpdate.setUserAgent("BagOfGold-" + plugin.getDescription().getVersion());

		spigetUpdate.checkForUpdate(new UpdateCallback() {

			@Override
			public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
				//// VersionComparator.EQUAL handles all updates as new, so I have to check the
				//// version number manually
				updateAvailable = isUpdateNewerVersion(newVersion);
				if (updateAvailable == UpdateStatus.AVAILABLE) {
					newDownloadVersion = newVersion;
					sender.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.GREEN + plugin.getMessages()
							.getString("bagofgold.commands.update.version-found", "newversion", newVersion));
					if (plugin.getConfigManager().autoupdate|| forceDownload) {
						if (hasDirectDownload) {
							try {
								InputStream in = new URL("https://api.spiget.org/v2/resources/49332/download")
										.openStream();
								Files.copy(in, Paths.get("plugins/BagOfGold-" + newDownloadVersion + ".jar"),
										StandardCopyOption.REPLACE_EXISTING);
							} catch (IOException e) {
								e.printStackTrace();
							}
							
							new BukkitRunnable() {
								int count = 0;

								@Override
								public void run() {
									// Wait for the response
									if (count++ > 20) {
										Bukkit.getConsoleSender()
												.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
														+ "No updates found. (No response from server after 20s)");
										plugin.getMessages().senderSendMessage(sender,
												ChatColor.GOLD + "[BagOfGold] " + ChatColor.GREEN
														+ plugin.getMessages().getString(
																"bagofgold.commands.update.could-not-update"));
										plugin.getMessages().debug("Update error: %s",
												spigetUpdate.getFailReason().toString());
										sender.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.GREEN
												+ "If download fail, you can download newest version here: "
												+ downloadUrl);
										this.cancel();
									} else {

										final String OS = System.getProperty("os.name");
										if (OS.indexOf("Win") >= 0) {
											File downloadedJar = new File("plugins/" + currentJarFile);
											File newJar = new File("plugins/BagOfGold-" + newDownloadVersion + ".jar");
											if (newJar.exists())
												newJar.delete();
											downloadedJar.renameTo(newJar);
											plugin.getMessages().senderSendMessage(sender,
													ChatColor.GOLD + "[BagOfGold]" + ChatColor.GREEN
															+ plugin.getMessages()
																	.getString("bagofgold.commands.update.complete"));
										} else {
											if (updateAvailable != UpdateStatus.RESTART_NEEDED) {
												File currentJar = new File("plugins/" + currentJarFile);
												File disabledJar = new File("plugins/" + currentJarFile + ".old");
												int count = 0;
												while (disabledJar.exists() && count++ < 100) {
													disabledJar = new File(
															"plugins/" + currentJarFile + ".old" + count);
												}
												if (!disabledJar.exists()) {
													currentJar.renameTo(disabledJar);
													File downloadedJar = new File("plugins/update/" + currentJarFile);
													File newJar = new File(
															"plugins/BagOfGold-" + newDownloadVersion + ".jar");
													downloadedJar.renameTo(newJar);
													plugin.getMessages().debug("Moved plugins/update/" + currentJarFile
															+ " to plugins/BagOfGold-" + newDownloadVersion + ".jar");
													updateAvailable = UpdateStatus.RESTART_NEEDED;
													plugin.getMessages().senderSendMessage(sender,
															ChatColor.GOLD + "[BagOfGold]" + ChatColor.GREEN
																	+ plugin.getMessages().getString(
																			"bagofgold.commands.update.complete"));
												}
											}
										}
										this.cancel();
									}
								}
							}.runTaskTimer(plugin, 20L, 20L);
							sender.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.GREEN
									+ plugin.getMessages().getString("bagofgold.commands.update.complete"));
						}
					} else
						sender.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.GREEN
								+ plugin.getMessages().getString("bagofgold.commands.update.help"));
				} else {
					sender.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
							+ plugin.getMessages().getString("bagofgold.commands.update.no-update"));
				}
			}
							

			@Override
			public void upToDate() {
				//// Plugin is up-to-date
				if (!silent)
					sender.sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
							+ plugin.getMessages().getString("bagofgold.commands.update.no-update"));
			}
		});

	}

	/**
	 * Check if "newVersion" is newer than plugin's current version
	 * 
	 * @param newVersion
	 * @return
	 */
	public UpdateStatus isUpdateNewerVersion(String newVersion) {
		// Version format on Spigot.org & Spiget.org: "n.n.n"
		// Version format in jar file: "n.n.n" | "n.n.n-SNAPSHOT-Bn"

		int updateCheck = 0, pluginCheck = 0;
		boolean snapshot = false;
		String[] updateVer = newVersion.split("\\.");

		// Check the version #'s
		String[] pluginVerSNAPSHOT = plugin.getDescription().getVersion().split("\\-");
		if (pluginVerSNAPSHOT.length > 1)
			snapshot = pluginVerSNAPSHOT[1].equals("SNAPSHOT");
		if (snapshot)
			plugin.getMessages().debug("You are using a development version (%s)",
					plugin.getDescription().getVersion());
		String[] pluginVer = pluginVerSNAPSHOT[0].split("\\.");
		// Run through major, minor, sub - version numbers
		for (int i = 0; i < Math.max(updateVer.length, pluginVer.length); i++) {
			try {
				updateCheck = 0;
				if (i < updateVer.length)
					updateCheck = Integer.valueOf(updateVer[i]);
				pluginCheck = 0;
				if (i < pluginVer.length)
					pluginCheck = Integer.valueOf(pluginVer[i]);
				if (updateCheck > pluginCheck) {
					return UpdateStatus.AVAILABLE;
				} else if (updateCheck < pluginCheck)
					return UpdateStatus.NOT_AVAILABLE;
			} catch (Exception e) {
				plugin.getLogger().warning("Could not determine update's version # ");
				plugin.getLogger().warning("Installed plugin version: " + plugin.getDescription().getVersion());
				plugin.getLogger().warning("Newest version on Spiget.org: " + newVersion);
				return UpdateStatus.UNKNOWN;
			}
		}
		if ((updateCheck == pluginCheck && snapshot))
			return UpdateStatus.AVAILABLE;
		else
			return UpdateStatus.NOT_AVAILABLE;
	}
}
