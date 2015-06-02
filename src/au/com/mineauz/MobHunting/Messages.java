package au.com.mineauz.MobHunting;

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
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class Messages {
	private static Map<String, String> mTranslationTable;
	private static String[] mValidEncodings = new String[] { "UTF-16",
			"UTF-16BE", "UTF-16LE", "UTF-8", "ISO646-US" };

	public static void exportDefaultLanguages() {
		File folder = new File(MobHunting.instance.getDataFolder(), "lang"); //$NON-NLS-1$
		if (!folder.exists())
			folder.mkdirs();

		String[] sources = new String[] { "en_US.lang", "zh_CN.lang" }; //$NON-NLS-1$ //$NON-NLS-2$

		for (String source : sources) {
			File dest = new File(folder, source);
			if (!dest.exists())
				MobHunting.instance.saveResource("lang/" + source, false); //$NON-NLS-1$
			else {
				if (!injectChanges(
						MobHunting.instance.getResource("lang/" + source), new File(MobHunting.instance.getDataFolder(), "lang/" + source))) //$NON-NLS-1$ //$NON-NLS-2$
					MobHunting.instance.saveResource("lang/" + source, true); //$NON-NLS-1$
			}
		}
	}

	private static boolean injectChanges(InputStream inJar, File onDisk) {
		try {
			Map<String, String> source = loadLang(inJar, "UTF-8"); //$NON-NLS-1$
			Map<String, String> dest = loadLang(onDisk);

			if (dest == null)
				return false;

			HashMap<String, String> newEntries = new HashMap<String, String>();
			for (String key : source.keySet()) {
				if (!dest.containsKey(key))
					newEntries.put(key, source.get(key));
			}

			if (!newEntries.isEmpty()) {
				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(onDisk,
								true)));
				for (Entry<String, String> entry : newEntries.entrySet())
					writer.append("\n" + entry.getKey() + "=" + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$

				writer.close();

				MobHunting.instance.getLogger().info(
						"Updated " + onDisk.getName() + " translation"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private static Map<String, String> loadLang(InputStream stream,
			String encoding) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				stream, encoding));

		while (reader.ready()) {
			String line = reader.readLine();
			int index = line.indexOf('=');
			if (index == -1)
				continue;

			String key = line.substring(0, index).toLowerCase().trim();
			String value = line.substring(index + 1).trim();

			map.put(key, value);
		}
		reader.close();

		return map;
	}

	private static Pattern mDetectEncodingPattern = Pattern
			.compile("^[a-zA-Z\\.\\-0-9_]+=.+$");

	private static String detectEncoding(File file) throws IOException {
		for (String charset : mValidEncodings) {
			FileInputStream input = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input, charset));
			String line = null;
			boolean ok = true;

			while (reader.ready()) {
				line = reader.readLine();
				if (line.trim().isEmpty())
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
				MobHunting.instance
						.getLogger()
						.warning(
								"Could not detect encoding of lang file. Defaulting to UTF-8");
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
		File file = new File(MobHunting.instance.getDataFolder(),
				"lang/" + lang + ".lang"); //$NON-NLS-1$ //$NON-NLS-2$
		if (!file.exists()) {
			MobHunting.instance.getLogger().severe(
					"Language file does not exist.");
			file = new File(MobHunting.instance.getDataFolder(),
					"lang/en_US.lang"); //$NON-NLS-1$
		}

		if (file.exists()) {
			mTranslationTable = loadLang(file);
		} else {
			MobHunting.instance.getLogger().warning(
					"Could not read the translation file:" + file.getName());
		}

		if (mTranslationTable == null) {
			mTranslationTable = new HashMap<String, String>();
			MobHunting.instance.getLogger().warning(
					"Creating new translation table.");
		}
	}

	private static String getStringInternal(String key) {
		String value = mTranslationTable.get(key);

		if (value == null) {
			MobHunting.instance.getLogger().warning(
					"mTranslationTable has not key: " + key.toString());
			throw new MissingResourceException("", "", key); //$NON-NLS-1$ //$NON-NLS-2$
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
				mPattern = Pattern.compile("\\$\\{([\\w\\.\\-]+)\\}"); //$NON-NLS-1$

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
					output = output.replaceAll("\\$\\{" + name + "\\}",
							Matcher.quoteReplacement(replace.toString()));
			}

			return ChatColor.translateAlternateColorCodes('&', output);
		} catch (MissingResourceException e) {
			MobHunting.instance.getLogger().warning(
					"Mobhunt could not find key: " + key.toString());
			return key;
		}
	}

	public static String getString(String key) {
		try {
			return ChatColor.translateAlternateColorCodes('&',
					getStringInternal(key));
		} catch (MissingResourceException e) {
			return key;
		}
	}
}
