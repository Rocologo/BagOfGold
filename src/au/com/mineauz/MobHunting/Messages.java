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

public class Messages
{
	private static Map<String, String> mTranslationTable;
	
	public static void exportDefaultLanguages()
	{
		File folder = new File(MobHunting.instance.getDataFolder(), "lang"); //$NON-NLS-1$
		if(!folder.exists())
			folder.mkdirs();
		
		String[] sources = new String[] {"en_US.lang"}; //$NON-NLS-1$
		
		for(String source : sources)
		{
			File dest = new File(folder, source);
			if(!dest.exists())
				MobHunting.instance.saveResource("lang/" + source, false); //$NON-NLS-1$
			else
				injectChanges(MobHunting.instance.getResource("lang/" + source), new File(MobHunting.instance.getDataFolder(), "lang/" + source)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private static void injectChanges(InputStream inJar, File onDisk)
	{
		try
		{
			Map<String, String> source = loadLang(inJar);
			Map<String, String> dest = loadLang(onDisk);
			
			HashMap<String, String> newEntries = new HashMap<String, String>();
			for(String key : source.keySet())
			{
				if(!dest.containsKey(key))
					newEntries.put(key, source.get(key));
			}
			
			if(!newEntries.isEmpty())
			{
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(onDisk, true)));
				for(Entry<String, String> entry : newEntries.entrySet())
					writer.append("\n" + entry.getKey() + "=" + entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$

				writer.close();
				
				MobHunting.instance.getLogger().info("Updated " + onDisk.getName() + " translation"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static Map<String, String> loadLang(InputStream stream) throws IOException
	{
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		while(reader.ready())
		{
			String line = reader.readLine();
			int index = line.indexOf('=');
			if(index == -1)
				continue;
			
			String key = line.substring(0, index).trim();
			String value = line.substring(index + 1).trim();
			
			map.put(key, value);
		}
		reader.close();
		
		return map;
	}
	
	private static Map<String, String> loadLang(File file)
	{
		Map<String, String> map;
		
		try
		{
			FileInputStream input = new FileInputStream(file);
			map = loadLang(input);
			input.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return null;
		}
		
		return map;
	}
	
	public static void setLanguage(String lang)
	{
		File file = new File(MobHunting.instance.getDataFolder(), "lang/" + lang + ".lang"); //$NON-NLS-1$ //$NON-NLS-2$
		if(!file.exists())
			file = new File(MobHunting.instance.getDataFolder(), "lang/en_US.lang"); //$NON-NLS-1$
		
		if(file.exists())
			mTranslationTable = loadLang(file);

		if(mTranslationTable == null)
			mTranslationTable = new HashMap<String, String>();
	}
	
	private static String getStringInternal(String key)
	{
		String value = mTranslationTable.get(key);
		
		if(value == null)
			throw new MissingResourceException("", "", key); //$NON-NLS-1$ //$NON-NLS-2$
		
		return value;
	}
	
	private static Pattern mPattern;
	
	/**
	 * Gets the message and replaces specified values
	 * @param key The message key to find
	 * @param values these are key-value pairs, they should be like: {key1, value1, key2, value2,..., keyN,valueN}. keys must be strings
	 */
	public static String getString( String key, Object... values )
	{
		try
		{
			if(mPattern == null)
				mPattern = Pattern.compile("\\$\\{([\\w\\.\\-]+)\\}"); //$NON-NLS-1$

			HashMap<String, Object> map = new HashMap<String, Object>();
			
			String name = null;
			for(Object value : values)
			{
				if(name == null)
					name = (String)value; // This must be a string
				else
				{
					map.put(name, value);
					name = null;
				}
			}
			

			String str = getStringInternal(key);
			Matcher m = mPattern.matcher(str);
			
			String output = str;
			
			while(m.find())
			{
				name = m.group(1);
				Object replace = map.get(name);
				if(replace != null)
					output = output.replaceAll("\\$\\{" + name + "\\}", Matcher.quoteReplacement(replace.toString())); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			return ChatColor.translateAlternateColorCodes('&', output);
		}
		catch ( MissingResourceException e )
		{
			return key;
		}
	}
	
	public static String getString( String key )
	{
		try
		{
			return ChatColor.translateAlternateColorCodes('&', getStringInternal(key));
		}
		catch ( MissingResourceException e )
		{
			return key;
		}
	}
}
