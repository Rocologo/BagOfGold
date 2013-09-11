package au.com.mineauz.MobHunting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class Messages
{
	private static HashMap<String, String> mTranslationTable;
	
	public static void exportDefaultLanguages()
	{
		File folder = new File(MobHunting.instance.getDataFolder(), "lang");
		if(!folder.exists())
			folder.mkdirs();
		
		String[] sources = new String[] {"en_US.lang"};
		
		for(String source : sources)
		{
			File dest = new File(folder, source);
			if(!dest.exists())
				MobHunting.instance.saveResource("lang/" + source, false);
		}
	}
	
	public static void setLanguage(String lang)
	{
		File file = new File(MobHunting.instance.getDataFolder(), "lang/" + lang + ".lang");
		if(!file.exists())
			file = new File(MobHunting.instance.getDataFolder(), "lang/en_US.lang");
		
		mTranslationTable = new HashMap<String, String>();
		
		// We tried
		if(!file.exists())
			return;
		
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while(reader.ready())
			{
				String line = reader.readLine();
				int index = line.indexOf('=');
				if(index == -1)
					continue;
				
				String key = line.substring(0, index);
				String value = line.substring(index + 1);
				
				mTranslationTable.put(key, value);
			}
			reader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private static String getStringInternal(String key)
	{
		String value = mTranslationTable.get(key);
		
		if(value == null)
			throw new MissingResourceException("", "", key);
		
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
				mPattern = Pattern.compile("\\$\\{([\\w\\.\\-]+)\\}");

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
					output = output.replaceAll("\\$\\{" + name + "\\}", Matcher.quoteReplacement(replace.toString()));
			}
			
			return ChatColor.translateAlternateColorCodes('&', output);
		}
		catch ( MissingResourceException e )
		{
			return '!' + key + '!';
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
			return '!' + key + '!';
		}
	}
}
