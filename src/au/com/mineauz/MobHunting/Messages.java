package au.com.mineauz.MobHunting;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages
{
	private static final String			BUNDLE_NAME		= "au.com.mineauz.MobHunting.messages"; //$NON-NLS-1$

	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle
																.getBundle(BUNDLE_NAME);

	private Messages()
	{
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
			

			String str = RESOURCE_BUNDLE.getString(key);
			Matcher m = mPattern.matcher(str);
			
			String output = str;
			
			while(m.find())
			{
				name = m.group(1);
				Object replace = map.get(name);
				if(replace != null)
					output = output.replaceAll("\\$\\{" + name + "\\}", Matcher.quoteReplacement(replace.toString()));
			}
			
			return output;
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
			return RESOURCE_BUNDLE.getString(key);
		}
		catch ( MissingResourceException e )
		{
			return '!' + key + '!';
		}
	}
}
