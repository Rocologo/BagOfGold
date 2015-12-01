package au.com.mineauz.MobHunting.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class UpdateCommand implements ICommand {
	@Override
	public String getName() {
		return "update";
	}

	@Override
	public String[] getAliases() {
		return null;
	}

	@Override
	public String getPermission() {
		return "mobhunting.update";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { label };
	}

	@Override
	public String getDescription() {
		return Messages.getString("mobhunting.commands.update.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {
		if (MobHunting.getUpdateAvailable()) {
			if (downloadAndUpdateJar()) {
				sender.sendMessage(ChatColor.GREEN
						+ Messages
								.getString("mobhunting.commands.update.complete"));

			} else {
				sender.sendMessage(ChatColor.GREEN
						+ Messages
								.getString("mobhunting.commands.update.could-not-update"));
			}
		} else {
			sender.sendMessage(ChatColor.GREEN
					+ Messages
							.getString("mobhunting.commands.update.no-update"));
		}
		return true;
	}

	private static boolean downloadAndUpdateJar() {
		try {
			downloadFile(MobHunting.instance.getBukkitUpdate().getVersionLink(),
					"plugins/MobHunting/update/");
			File oldFile = new File("plugins/" + MobHunting.getCurrentJarFile());
			File disabledFile = new File("plugins/" + MobHunting.getCurrentJarFile()
					+ ".old");
			if (!disabledFile.exists()) {
				oldFile.renameTo(disabledFile);

				File newJarFile = new File("plugins/MobHunting/update/"
						+ MobHunting.instance.getBukkitUpdate()
								.getVersionFileName());
				File movedNewJarFile = new File("plugins/"
						+ MobHunting.instance.getBukkitUpdate()
								.getVersionFileName());
				newJarFile.renameTo(movedNewJarFile);
				return true;
			}
		} catch (MalformedInputException malformedInputException) {
			malformedInputException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return false;
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
	private static void downloadFile(String fileURL, String saveDir)
			throws IOException {
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
					fileName = disposition.substring(index + 10,
							disposition.length() - 1);
				}
			} else {
				// extracts file name from URL
				fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
						fileURL.length());
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
			System.out
					.println("No file to download. Server replied HTTP code: "
							+ responseCode);
		}
		httpConn.disconnect();

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label,
			String[] args) {
		return null;
	}

}
