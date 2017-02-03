package one.lindegaard.MobHunting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import one.lindegaard.MobHunting.compatibility.ActionAnnouncerCompat;
import one.lindegaard.MobHunting.compatibility.ActionBarAPICompat;
import one.lindegaard.MobHunting.compatibility.ActionbarCompat;
import one.lindegaard.MobHunting.compatibility.BarAPICompat;
import one.lindegaard.MobHunting.compatibility.BossBarAPICompat;
import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.TitleAPICompat;
import one.lindegaard.MobHunting.compatibility.TitleManagerCompat;
import one.lindegaard.MobHunting.mobs.ExtendedMob;
import one.lindegaard.MobHunting.mobs.MobPlugin;

public class Messages {
	private static Map<String, String> mTranslationTable;
	private static String[] mValidEncodings = new String[] { "UTF-16", "UTF-16BE", "UTF-16LE", "UTF-8", "ISO646-US" };
	private static final String PREFIX = "[MobHunting]";
	private static String[] sources = new String[] { "en_US.lang", "zh_CN.lang" };

	public static void exportDefaultLanguages(MobHunting plugin) {
		File folder = new File(plugin.getDataFolder(), "lang");
		if (!folder.exists())
			folder.mkdirs();

		for (String source : sources) {
			File dest = new File(folder, source);
			if (!dest.exists()) {
				Bukkit.getLogger().info(PREFIX + " Creating language file " + source + " from JAR.");
				plugin.saveResource("lang/" + source, false);
				mTranslationTable = loadLang(dest);
			} else {
				if (!injectChanges(

						plugin.getResource("lang/" + source), new File(plugin.getDataFolder(), "lang/" + source))) {

					plugin.saveResource("lang/" + source, true);
				}
			}
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
					Bukkit.getLogger().info(PREFIX + " Missing key in language file: " + key);
					newEntries.put(key, source.get(key));
				}
			}

			if (!newEntries.isEmpty()) {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(onDisk, true)));
				for (Entry<String, String> entry : newEntries.entrySet())
					writer.append("\n" + entry.getKey() + "=" + entry.getValue());

				writer.close();

				Bukkit.getLogger().info(PREFIX + " Updated " + onDisk.getName() + " translation");
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static void injectMissingMobNamesToLangFiles() {
		File folder = new File(MobHunting.getInstance().getDataFolder(), "lang");
		if (!folder.exists())
			folder.mkdirs();

		for (String source : sources) {
			File dest = new File(folder, source);
			injectMissingMobNamesToLangFile(dest);
		}
	}

	private static boolean injectMissingMobNamesToLangFile(File onDisk) {
		try {
			Map<String, String> dest = loadLang(onDisk);

			if (dest == null)
				return false;

			HashMap<String, String> newEntries = new HashMap<String, String>();
			for (Entry<Integer, ExtendedMob> key : MobHunting.getExtendedMobManager().getAllMobs().entrySet()) {
				String k;
				if (key.getValue().getMobPlugin() == MobPlugin.Minecraft)
					k = "mobs." + key.getValue().getMobtype() + ".name";
				else
					k = "mobs." + key.getValue().getMobPlugin().name() + "_" + key.getValue().getMobtype() + ".name";
				if (!dest.containsKey(k)) {
					Bukkit.getLogger()
							.info(PREFIX + " Creating missing key (" + k + ") in language file " + onDisk.getName());
					if (key.getValue() != null && key.getValue().getName() != null
							&& !key.getValue().getName().equals(""))
						newEntries.put(k, key.getValue().getName());
				}
			}

			for (MobPlugin p : MobPlugin.values()) {
				String k = "stats." + p.name() + ".kill";
				if (!dest.containsKey(k)) {
					Bukkit.getLogger()
							.info(PREFIX + " Creating missing key (" + k + ") in language file" + onDisk.getName());
					newEntries.put(k, p.name() + " kills");
				}
				k = "stats." + p.name() + ".assist";
				if (!dest.containsKey(k)) {
					Bukkit.getLogger()
							.info(PREFIX + " Creating missing key (" + k + ") in language file " + onDisk.getName());
					newEntries.put(k, p.name() + " assists");
				}
			}

			if (!newEntries.isEmpty()) {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(onDisk, true)));
				for (Entry<String, String> entry : newEntries.entrySet()) {
					writer.append("\n" + entry.getKey() + "=" + entry.getValue());
				}
				writer.close();

				// add new mobs to the TranslationTable
				mTranslationTable.putAll(newEntries);

				Bukkit.getLogger().info(PREFIX + " Updated " + onDisk.getName() + " translation");
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Map<String, String> loadLang(InputStream stream, String encoding) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
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
				Bukkit.getLogger().warning(PREFIX + " Could not detect encoding of lang file. Defaulting to UTF-8");
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

	public static void setLanguage(String lang) {
		File file = new File(MobHunting.getInstance().getDataFolder(), "lang/" + lang + ".lang");
		if (!file.exists()) {
			Bukkit.getLogger().severe(PREFIX + " Language file does not exist.");
			file = new File(MobHunting.getInstance().getDataFolder(), "lang/en_US.lang");
		}

		if (file.exists()) {
			mTranslationTable = loadLang(file);
		} else {
			Bukkit.getLogger().warning(PREFIX + " Could not read the translation file:" + file.getName());
		}

		if (mTranslationTable == null) {
			mTranslationTable = new HashMap<String, String>();
			Bukkit.getLogger().warning(PREFIX + " Creating new translation table.");
		}
	}

	private static String getStringInternal(String key) {
		String value = mTranslationTable.get(key);

		if (value == null) {
			Bukkit.getLogger().warning(PREFIX + " mTranslationTable has not key: " + key.toString());
			throw new MissingResourceException("", "", key);
		}

		return value;
	}

	private static Pattern mPattern;

	/**
	 * Gets the message and replaces specified values
	 * 
	 * @param key
	 *            The message key to find
	 * @param values
	 *            these are key-value pairs, they should be like: {key1, value1,
	 *            key2, value2,..., keyN,valueN}. keys must be strings
	 */
	public static String getString(String key, Object... values) {
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

			return ChatColor.translateAlternateColorCodes('&', output);
		} catch (MissingResourceException e) {
			Bukkit.getLogger().warning(PREFIX + " Mobhunt could not find key: " + key.toString());
			return key;
		}
	}

	public static String getString(String key) {
		try {
			return ChatColor.translateAlternateColorCodes('&', getStringInternal(key));
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Broadcast message to all players except Player using the ActionBar. if
	 * the no plugins for the actionbar is available the chat will be used.
	 * 
	 * @param message
	 * @param except
	 */
	public static void broadcast(String message, Player except) {
		Iterator<Player> players = MobHunting.getMobHuntingManager().getOnlinePlayers().iterator();
		while (players.hasNext()) {
			Player player = players.next();
			if (player.equals(except) || MobHunting.getPlayerSettingsmanager().getPlayerSettings(player).isMuted())
				continue;
			if (MobHunting.getConfigManager().useActionBarforBroadcasts)
				playerActionBarMessage(player, message);
			else
				player.sendMessage(message);
		}
	}

	/**
	 * Show debug information in the Server console log
	 * 
	 * @param text
	 * @param args
	 */
	public static void debug(String text, Object... args) {
		if (MobHunting.getConfigManager().killDebug)
			Bukkit.getLogger().info(PREFIX + "[Debug] " + String.format(text, args));
	}

	/**
	 * Show learning messages to the player
	 * 
	 * @param player
	 * @param text
	 * @param args
	 */
	public static void learn(Player player, String text, Object... args) {
		if (player != null && !CitizensCompat.isNPC(player)
				&& MobHunting.getPlayerSettingsmanager().getPlayerSettings(player).isLearningMode())
			playerBossbarMessage(player, text, args);
	}

	/**
	 * Show message to the player using the BossBar. If no BossBar plugin is
	 * available the player chat will be used.
	 * 
	 * @param player
	 * @param text
	 * @param args
	 */
	public static void playerBossbarMessage(Player player, String text, Object... args) {
		if (BossBarAPICompat.isSupported()) {
			BossBarAPICompat.addBar(player, String.format(text, args));
		} else if (BarAPICompat.isSupported()) {
			BarAPICompat.setMessageTime(player, String.format(text, args), 5);
		} else {
			player.sendMessage(
					ChatColor.AQUA + Messages.getString("mobhunting.learn.prefix") + " " + String.format(text, args));
		}
	}

	/**
	 * Show message to the player using the ActionBar
	 * 
	 * @param player
	 * @param message
	 */
	public static void playerActionBarMessage(Player player, String message) {
		if (TitleManagerCompat.isSupported()) {
			TitleManagerCompat.setActionBar(player, message);
		} else if (ActionbarCompat.isSupported()) {
			ActionbarCompat.setMessage(player, message);
		} else if (ActionAnnouncerCompat.isSupported()) {
			ActionAnnouncerCompat.setMessage(player, message);
		} else if (ActionBarAPICompat.isSupported()) {
			ActionBarAPICompat.setMessage(player, message);
		} else {
			player.sendMessage(message);
		}
	}

	public static void playerSendTitlesMessage(Player player, String title, String subtitle, int fadein, int stay,
			int fadeout) {
		if (TitleManagerCompat.isSupported()) {
			TitleManagerCompat.sendTitles(player, title, subtitle, fadein, stay, fadeout);
		} else if (TitleAPICompat.isSupported()) {
			TitleAPICompat.sendTitles(player, title, subtitle, fadein, stay, fadeout);
		} else {
			player.sendMessage(new String[] { title, subtitle });
		}
	}

}
