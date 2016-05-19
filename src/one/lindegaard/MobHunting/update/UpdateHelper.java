package one.lindegaard.MobHunting.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class UpdateHelper {

	// ***************************************************************************
	// UPDATECHECK - Check if there is a new version available at
	// https://api.curseforge.com/servermods/files?projectIds=63718
	// ***************************************************************************

	// Update object
	private static BukkitUpdate bukkitUpdate = null;
	private static UpdateStatus updateAvailable = UpdateStatus.UNKNOWN;
	private static String currentJarFile = "";

	public static BukkitUpdate getBukkitUpdate() {
		return bukkitUpdate;
	}

	public static UpdateStatus getUpdateAvailable() {
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

	public static void hourlyUpdateCheck(final CommandSender sender, boolean updateCheck, final boolean silent) {
		if (updateCheck) {
			new BukkitRunnable() {
				@Override
				public void run() {
					pluginUpdateCheck(sender, true, false);
				}
			}.runTaskTimer(MobHunting.getInstance(), 0L, MobHunting.getConfigManager().checkEvery * 20L);
			// Check for update timer
		}
	}

	public static void pluginUpdateCheck(final CommandSender sender, boolean updateCheck, final boolean silent) {
		if (updateCheck) {
			if (!silent) {
				MobHunting.getInstance().getServer().getConsoleSender().sendMessage(
						ChatColor.GOLD + "[MobHunting] " + Messages.getString("mobhunting.commands.update.check"));
			}
			if (updateAvailable != UpdateStatus.RESTART_NEEDED) {
				// Check for updates asynchronously in background
				MobHunting.getInstance().getServer().getScheduler().runTaskAsynchronously(MobHunting.getInstance(),
						new Runnable() {
							@Override
							public void run() {
								bukkitUpdate = new BukkitUpdate(63718); // MobHunting
								if (!bukkitUpdate.isSuccess()) {
									bukkitUpdate = null;
								}
							}
						});
				// Check if bukkitUpdate is found in background
				new BukkitRunnable() {
					int count = 0;

					@Override
					public void run() {
						if (count++ > 10) {
							if (!silent)
								sender.sendMessage(ChatColor.RED
										+ "[MobHunting] No updates found. (No response from server after 10s)");
							this.cancel();
						} else {
							// Wait for the response
							if (bukkitUpdate != null) {
								if (bukkitUpdate.isSuccess()) {
									updateAvailable = isUpdateNewerVersion();
									if (updateAvailable == UpdateStatus.AVAILABLE) {
										sender.sendMessage(ChatColor.GREEN + "[MobHunting] "
												+ Messages.getString("mobhunting.commands.update.version-found"));
										if (MobHunting.getConfigManager().autoupdate) {
											downloadAndUpdateJar();
											sender.sendMessage(ChatColor.GREEN + "[MobHunting] "
													+ Messages.getString("mobhunting.commands.update.complete"));
										} else {
											sender.sendMessage(ChatColor.GREEN + "[MobHunting] "
													+ Messages.getString("mobhunting.commands.update.help"));
										}

									} else {
										if (!silent) {
											sender.sendMessage(ChatColor.GOLD + "[MobHunting] "
													+ Messages.getString("mobhunting.commands.update.no-update"));
										}
									}
								}
								this.cancel();
							}

						}
					}
				}.runTaskTimer(MobHunting.getInstance(), 20L, 20L); // Check
																	// status
				// every second
			} else {
				sender.sendMessage(
						ChatColor.GREEN + "[MobHunting] " + Messages.getString("mobhunting.commands.update.complete"));
			}
		}
	}

	public static boolean downloadAndUpdateJar() {
		final String OS = System.getProperty("os.name");
		if (OS.indexOf("Win") >= 0) {
			try {
				downloadFile(getBukkitUpdate().getVersionLink(), "plugins/update/");
				File downloadedJar = new File(
						"plugins/update/" + UpdateHelper.getBukkitUpdate().getVersionFileName());
				File newJar = new File("plugins/updater/MobHunting.jar");
				downloadedJar.renameTo(newJar);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			try {
				if (updateAvailable != UpdateStatus.RESTART_NEEDED)
					downloadFile(getBukkitUpdate().getVersionLink(), "plugins/MobHunting/update/");
				File currentJar = new File("plugins/" + getCurrentJarFile());
				File disabledJar = new File("plugins/" + getCurrentJarFile() + ".old");
				int count = 0;
				while (disabledJar.exists() && count++ < 100) {
					disabledJar = new File("plugins/" + getCurrentJarFile() + ".old" + count);
				}
				if (!disabledJar.exists()) {
					currentJar.renameTo(disabledJar);

					File downloadedJar = new File(
							"plugins/MobHunting/update/" + UpdateHelper.getBukkitUpdate().getVersionFileName());
					File newJar = new File("plugins/" + UpdateHelper.getBukkitUpdate().getVersionFileName());
					downloadedJar.renameTo(newJar);
					updateAvailable = UpdateStatus.RESTART_NEEDED;
					return true;
				}
			} catch (MalformedInputException malformedInputException) {
				malformedInputException.printStackTrace();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		return false;
	}

	public static UpdateStatus isUpdateNewerVersion() {
		// Check to see if the latest file is newer that this one
		String[] split = UpdateHelper.getBukkitUpdate().getVersionName().split(" V");
		// Only do this if the format is what we expect
		if (split.length == 2) {
			// Need to escape the period in the regex expression
			String[] updateVer = split[1].split("\\.");
			// CHeck the version #'s
			String[] pluginVer = MobHunting.getInstance().getDescription().getVersion().split("\\.");
			// Run through major, minor, sub
			for (int i = 0; i < Math.max(updateVer.length, pluginVer.length); i++) {
				try {
					int updateCheck = 0;
					if (i < updateVer.length) {
						updateCheck = Integer.valueOf(updateVer[i]);
					}
					int pluginCheck = 0;
					if (i < pluginVer.length) {
						pluginCheck = Integer.valueOf(pluginVer[i]);
					}
					if (updateCheck > pluginCheck) {
						return UpdateStatus.AVAILABLE;
					} else if (updateCheck < pluginCheck)
						return UpdateStatus.NOT_AVAILABLE;
				} catch (Exception e) {
					MobHunting.getInstance().getLogger().warning("Could not determine update's version # ");
					MobHunting.getInstance().getLogger()
							.warning("Plugin version: " + MobHunting.getInstance().getDescription().getVersion());
					MobHunting.getInstance().getLogger()
							.warning("Update version: " + UpdateHelper.getBukkitUpdate().getVersionName());
					return UpdateStatus.UNKNOWN;
				}
			}
		}
		return UpdateStatus.NOT_AVAILABLE;
	}

	private static final int BUFFER_SIZE = 4096;

	/**
	 * Downloads a file from a URL
	 * 
	 * @param fileURL
	 *            HTTP URL of the file to be downloaded
	 * @param saveDir
	 *            path of the directory to save the file
	 * @throws IOException
	 */
	private static void downloadFile(String fileURL, String saveDir) throws IOException {
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// Create savedir if needed
		if (!new File(saveDir).exists())
			new File(saveDir).mkdirs();

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String fileName = "";
			String disposition = httpConn.getHeaderField("Content-Disposition");
			String contentType = httpConn.getContentType();
			int contentLength = httpConn.getContentLength();

			if (disposition != null) {
				// extracts file name from header field
				int index = disposition.indexOf("filename=");
				if (index > 0) {
					fileName = disposition.substring(index + 10, disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
			}

			System.out.println("Content-Type = " + contentType);
			System.out.println("Content-Disposition = " + disposition);
			System.out.println("Content-Length = " + contentLength);
			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();

			String saveFilePath = saveDir + File.separator + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded");
		} else {
			System.out.println("No file to download. Server replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();
	}

}
