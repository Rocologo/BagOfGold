package one.lindegaard.BagOfGold;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import one.lindegaard.BagOfGold.compatibility.ActionAnnouncerCompat;
import one.lindegaard.BagOfGold.compatibility.ActionBarAPICompat;
import one.lindegaard.BagOfGold.compatibility.ActionbarCompat;
import one.lindegaard.BagOfGold.compatibility.BarAPICompat;
import one.lindegaard.BagOfGold.compatibility.BossBarAPICompat;
import one.lindegaard.BagOfGold.compatibility.CMICompat;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.BagOfGold.compatibility.PlaceholderAPICompat;
import one.lindegaard.BagOfGold.compatibility.TitleManagerCompat;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.Strings;

public class Messages {

	private BagOfGold plugin;

	public Messages(BagOfGold plugin) {
		this.plugin = plugin;
		exportDefaultLanguages(plugin);
	}

	private static Map<String, String> mTranslationTable;
	private static String[] mValidEncodings = new String[] { "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-8", "ISO646-US" };
	private static String[] sources = new String[] { "en_US.lang", "hu_HU.lang", "pt_BR.lang", "zh_CN.lang",
			"fr_FR.lang", "ru_RU.lang" };

	public void exportDefaultLanguages(Plugin plugin) {
		File folder = new File(plugin.getDataFolder(), "lang");
		if (!folder.exists())
			folder.mkdirs();

		for (String source : sources) {
			File dest = new File(folder, source);
			if (!dest.exists()) {
				Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "Creating language file " + source + " from JAR.");
				plugin.saveResource("lang/" + source, false);
			} else {
				if (!injectChanges(plugin.getResource("lang/" + source),
						new File(plugin.getDataFolder(), "lang/" + source))) {
					plugin.saveResource("lang/" + source, true);
				}
			}
			//mTranslationTable = loadLang(dest);
		}
	}

	private static boolean injectChanges(InputStream inJar, File onDisk) {
		try {
			Map<String, String> source = loadLang(inJar, "UTF-8");
			Map<String, String> dest = loadLang(onDisk);

			if (dest == null)
				return false;

			HashMap<String, String> newEntries = new HashMap<String, String>();
			for (String key : source.keySet()) {
				if (!dest.containsKey(key)) {
					newEntries.put(key, source.get(key));
				}
			}

			if (!newEntries.isEmpty()) {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(onDisk, true), StandardCharsets.UTF_8));
				for (Entry<String, String> entry : newEntries.entrySet())
					writer.append("\n" + entry.getKey() + "=" + entry.getValue());
				writer.close();
				sortFileOnDisk(onDisk);
				Bukkit.getConsoleSender()
						.sendMessage(BagOfGold.PREFIX + "Updated " + onDisk.getName() + " language file with missing keys");
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean sortFileOnDisk(File onDisk) {
		try {
			Map<String, String> source = loadLang(onDisk);
			source = sortByKeys(source);
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(onDisk, false), StandardCharsets.UTF_8));
			for (Entry<String, String> entry : source.entrySet()) {
				writer.append("\n" + entry.getKey() + "=" + entry.getValue());
			}
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Map<String, String> loadLang(InputStream stream, String encoding) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));

			while (reader.ready()) {
				String line = reader.readLine();
				if (line == null)
					continue;
				int index = line.indexOf('=');
				if (index == -1)
					continue;

				String key = line.substring(0, index).trim();
				String value = line.substring(index + 1).trim();

				map.put(key, value);
			}
			reader.close();
		} catch (Exception e) {
			Bukkit.getServer().getConsoleSender()
					.sendMessage(BagOfGold.PREFIX + "Error reading the language file. Please check the format.");
		}

		return map;
	}

	private static Pattern mDetectEncodingPattern = Pattern.compile("^[a-zA-Z\\.\\-0-9_]+=.+$");

	private static String detectEncoding(File file) throws IOException {
		for (String charset : mValidEncodings) {
			FileInputStream input = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input, charset));
			String line = null;
			boolean ok = true;

			while (reader.ready()) {
				line = reader.readLine();
				if (line == null || line.trim().isEmpty())
					continue;

				if (!mDetectEncodingPattern.matcher(line.trim()).matches())
					ok = false;
			}

			reader.close();

			if (ok)
				return charset;
		}

		return "UTF-8";
	}

	private static Map<String, String> loadLang(File file) {
		Map<String, String> map;

		try {
			String encoding = detectEncoding(file);
			if (encoding == null) {
				FileInputStream input = new FileInputStream(file);
				Bukkit.getConsoleSender()
						.sendMessage(BagOfGold.PREFIX + "Could not detect encoding of lang file. Defaulting to UTF-8");
				map = loadLang(input, "UTF-8");
				input.close();
			}

			FileInputStream input = new FileInputStream(file);
			map = loadLang(input, encoding);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return map;
	}

	public void setLanguage(String lang) {
		File file = new File(plugin.getDataFolder(), "lang/" + lang);
		if (!file.exists()) {
			Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX
					+ "Language file does not exist. Creating a new file based on en_US. You need to translate the file yourself.");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		if (file.exists()) {
			InputStream resource = plugin.getResource("lang/en_US.lang");
			injectChanges(resource, file);
			mTranslationTable = loadLang(file);
		} else {
			Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "Could not read the language file:" + file.getName());
		}

		if (mTranslationTable == null) {
			mTranslationTable = new HashMap<String, String>();
			Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "Creating new translation table.");
		}
	}

	private static String getStringInternal(String key) {
		String value = mTranslationTable.get(key);

		if (value == null) {
			Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "mTranslationTable has not key: " + key.toString());
			throw new MissingResourceException("", "", key);
		}

		return value.trim();
	}

	private static Pattern mPattern;

	/**
	 * Gets the message and replaces specified values
	 * 
	 * @param key    The message key to find
	 * @param values these are key-value pairs, they should be like: {key1, value1,
	 *               key2, value2,..., keyN,valueN}. keys must be strings
	 */
	public String getString(String key, Object... values) {
		try {
			if (mPattern == null)
				mPattern = Pattern.compile("\\$\\{([\\w\\.\\-]+)\\}");

			HashMap<String, Object> map = new HashMap<String, Object>();

			String name = null;
			for (Object value : values) {
				if (name == null)
					name = (String) value; // This must be a string
				else {
					map.put(name, value);
					name = null;
				}
			}

			String str = getStringInternal(key);
			Matcher m = mPattern.matcher(str);

			String output = str;

			while (m.find()) {
				name = m.group(1);
				Object replace = map.get(name);
				if (replace != null)
					output = output.replaceAll("\\$\\{" + name + "\\}", Matcher.quoteReplacement(replace.toString()));
			}

			return Strings.convertColors(ChatColor.translateAlternateColorCodes('&', output));
		} catch (MissingResourceException e) {
			Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "BagOfGold could not find key: " + key.toString());
			return key;
		}
	}

	public String getString(String key) {
		try {
			return Strings.convertColors(ChatColor.translateAlternateColorCodes('&', getStringInternal(key)));
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Show debug information in the Server console log
	 * 
	 * @param message
	 * @param args
	 */
	public void debug(String message, Object... args) {
		if (plugin.getConfigManager().debug) {
			Bukkit.getServer().getConsoleSender().sendMessage(BagOfGold.PREFIX_DEBUG + String.format(message, args));
		}
	}

	public void playerSendMessage(final Player player, String message) {
		if (isEmpty(message))
			return;
		player.sendMessage(PlaceholderAPICompat.setPlaceholders(player, message));
	}

	public void senderSendMessage(final CommandSender sender, String message) {
		if (isEmpty(message))
			return;
		if (sender instanceof Player) {
			Player player = ((Player) sender);
			if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
				player.sendMessage(PlaceholderAPICompat.setPlaceholders((Player) sender, message));
		} else
			sender.sendMessage(message);
	}

	private static Map<String, String> sortByKeys(Map<String, String> map) {
		SortedSet<String> keys = new TreeSet<String>(map.keySet());
		Map<String, String> sortedHashMap = new LinkedHashMap<String, String>();
		for (String it : keys) {
			sortedHashMap.put(it, map.get(it));
		}
		return sortedHashMap;
	}

	private static boolean isEmpty(String message) {
		message = ChatColor.stripColor(message);
		return message.isEmpty();
	}

	HashMap<Player, Long> lastMessage = new HashMap<Player, Long>();

	public void playerActionBarMessageQueue(Player player, String message) {
		if (isEmpty(message))
			return;

		final String final_message = PlaceholderAPICompat.setPlaceholders(player, message);

		if (TitleManagerCompat.isSupported() || ActionbarCompat.isSupported() || ActionAnnouncerCompat.isSupported()
				|| ActionBarAPICompat.isSupported() || CMICompat.isSupported()) {
			long last = 0L;
			long time_between_messages = 80L;
			long delay = 1L, now = System.currentTimeMillis();
			if (lastMessage.containsKey(player)) {
				last = lastMessage.get(player);
				if (now > last + time_between_messages) {
					delay = 1L;
				} else if (now > last)
					delay = time_between_messages - (now - last);
				else
					delay = (last - now) + time_between_messages;
			}
			lastMessage.put(player, now + delay);

			Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {

				@Override
				public void run() {
					playerActionBarMessageNow(player, final_message);
				}
			}, delay);
		} else {
			player.sendMessage(final_message);
		}
	}

	/**
	 * Show message to the player using the ActionBar
	 * 
	 * @param player
	 * @param message
	 */
	public void playerActionBarMessageNow(Player player, String message) {
		if (isEmpty(message))
			return;

		message = Strings.convertColors(PlaceholderAPICompat.setPlaceholders(player, message));
		if (TitleManagerCompat.isSupported()) {
			TitleManagerCompat.setActionBar(player, message);
		} else if (ActionbarCompat.isSupported()) {
			ActionbarCompat.setMessage(player, message);
		} else if (ActionAnnouncerCompat.isSupported()) {
			ActionAnnouncerCompat.setMessage(player, message);
		} else if (ActionBarAPICompat.isSupported()) {
			ActionBarAPICompat.setMessage(player, message);
		} else if (CMICompat.isSupported()) {
			CMICompat.sendActionBarMessage(player, message);
		} else {
			if (!Core.getPlayerSettingsManager().getPlayerSettings(player).isMuted())
				player.sendMessage(message);
		}
	}
	
	/**
	 * Show learning messages to the player
	 * 
	 * @param player
	 * @param text
	 * @param args
	 */
	public void learn(Player player, String text, Object... args) {
		if (player != null && !CitizensCompat.isNPC(player)
				&& Core.getPlayerSettingsManager().getPlayerSettings(player).isLearningMode() && !isEmpty(text))
			playerBossbarMessage(player, text, args);
	}
	
	/**
	 * Show message to the player using the BossBar. If no BossBar plugin is
	 * available the player chat will be used.
	 * 
	 * @param player
	 * @param message
	 * @param args
	 */
	public void playerBossbarMessage(Player player, String message, Object... args) {
		if (isEmpty(message))
			return;

		message = Strings.convertColors(PlaceholderAPICompat.setPlaceholders(player, message));

		if (BossBarAPICompat.isSupported()) {
			BossBarAPICompat.addBar(player, String.format(message, args));
		} else if (BarAPICompat.isSupported()) {
			BarAPICompat.setMessageTime(player, String.format(message, args), 5);
		} else if (CMICompat.isSupported()) {
			CMICompat.sendBossBarMessage(player, String.format(message, args));
		} else {
			player.sendMessage(
					ChatColor.AQUA + getString("bagofgold.learn.prefix") + " " + String.format(message, args));
		}
	}

}
