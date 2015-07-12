package au.com.mineauz.MobHunting.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public abstract class AutoConfig
{
	private File mFile;
	private HashMap<String, String> mCategoryComments;
	private List<String> mCategories;
	private HashMap<String, String> mCategoryNodes;
	
	protected AutoConfig(File file)
	{
		mFile = file;
		mCategories = new ArrayList<String>();
		mCategoryComments = new HashMap<String, String>();
		mCategoryNodes = new HashMap<String, String>();
	}
	
	protected void setCategoryComment(String category, String comment)
	{
		mCategoryComments.put(category, comment);
	}
	
	protected List<String> getCategories(String category, String[] list)
	{
		return mCategories;
	}
	
	protected List<String> getNodes(String category) {
		List<String> nodes = new ArrayList<String>();
		for(Entry<String, String> e : mCategoryNodes.entrySet()) {
	        String key = e.getKey();
	        if (key.equals(category)){
	        	String value = e.getValue();
	        	nodes.add(value);
	        }
	    }
		return nodes; 
	}
	
	protected void onPostLoad() throws InvalidConfigurationException {};
	protected void onPreSave(){};
	public boolean loadConfig()
	{
		FileConfiguration yml = new YamlConfiguration();
		try
		{
			// Make sure the file exists
			if(!mFile.exists())
			{
				mFile.getParentFile().mkdirs();
				mFile.createNewFile();
			}
			
			// Parse the config
			yml.load(mFile);
			for(Field field : getClass().getDeclaredFields())
			{
				ConfigField configField = field.getAnnotation(ConfigField.class);
				if(configField == null)
					continue;
				
				String optionName = configField.name();
				if(optionName.isEmpty())
					optionName = field.getName();
				if (!mCategories.contains(configField.category())){
					//InfoSigns.debug("Category found: %s", configField.category());
					mCategories.add(configField.category());
				}
				
				
				field.setAccessible(true);
				
				String path = (configField.category().isEmpty() ? "" : configField.category() + ".") + optionName; //$NON-NLS-1$ //$NON-NLS-2$
				mCategoryNodes.put(path, optionName);
				if(!yml.contains(path))
				{
					if(field.get(this) == null)
						throw new InvalidConfigurationException(path + " is required to be set! Info:\n" + configField.comment()); //$NON-NLS-1$
				}
				else
				{
					// Parse the value
					
					if(field.getType().isArray())
					{
						// Integer
						if(field.getType().getComponentType().equals(Integer.TYPE))
							field.set(this, yml.getIntegerList(path).toArray(new Integer[0]));
						
						// Float
						else if(field.getType().getComponentType().equals(Float.TYPE))
							field.set(this, yml.getFloatList(path).toArray(new Float[0]));
						
						// Double
						else if(field.getType().getComponentType().equals(Double.TYPE))
							field.set(this, yml.getDoubleList(path).toArray(new Double[0]));
						
						// Long
						else if(field.getType().getComponentType().equals(Long.TYPE))
							field.set(this, yml.getLongList(path).toArray(new Long[0]));
						
						// Short
						else if(field.getType().getComponentType().equals(Short.TYPE))
							field.set(this, yml.getShortList(path).toArray(new Short[0]));
						
						// Boolean
						else if(field.getType().getComponentType().equals(Boolean.TYPE))
							field.set(this, yml.getBooleanList(path).toArray(new Boolean[0]));
						
						// String
						else if(field.getType().getComponentType().equals(String.class))
						{
							field.set(this, yml.getStringList(path).toArray(new String[0]));
						}
						else
							throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + " for AutoConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
					}
					else
					{
						// Integer
						if(field.getType().equals(Integer.TYPE))
							field.setInt(this, yml.getInt(path));
						
						// Float
						else if(field.getType().equals(Float.TYPE))
							field.setFloat(this, (float)yml.getDouble(path));
						
						// Double
						else if(field.getType().equals(Double.TYPE))
							field.setDouble(this, yml.getDouble(path));
						
						// Long
						else if(field.getType().equals(Long.TYPE))
							field.setLong(this, yml.getLong(path));
						
						// Short
						else if(field.getType().equals(Short.TYPE))
							field.setShort(this, (short)yml.getInt(path));
						
						// Boolean
						else if(field.getType().equals(Boolean.TYPE))
							field.setBoolean(this, yml.getBoolean(path));
						
						// ItemStack
						else if(field.getType().equals(ItemStack.class))
							field.set(this, yml.getItemStack(path));
						
						// String
						else if(field.getType().equals(String.class))
							field.set(this, yml.getString(path));
						else
							throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + " for AutoConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
			
			onPostLoad();
			
			return true;
		}
		catch( IOException e )
		{
			e.printStackTrace();
			return false;
		}
		catch ( InvalidConfigurationException e )
		{
			e.printStackTrace();
			return false;
		}
		catch ( IllegalArgumentException e )
		{
			e.printStackTrace();
			return false;
		}
		catch ( IllegalAccessException e )
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean saveConfig()
	{
		try
		{
			onPreSave();
			
			YamlConfiguration config = new YamlConfiguration();
			Map<String, String> comments = new HashMap<String, String>();
			
			// Add all the category comments
			comments.putAll(mCategoryComments);
			
			// Add all the values
			for(Field field : getClass().getDeclaredFields())
			{
				ConfigField configField = field.getAnnotation(ConfigField.class);
				if(configField == null)
					continue;
				
				String optionName = configField.name();
				if(optionName.isEmpty())
					optionName = field.getName();
				
				field.setAccessible(true);
				
				String path = (configField.category().isEmpty() ? "" : configField.category() + ".") + optionName; //$NON-NLS-1$ //$NON-NLS-2$

				// Ensure the secion exists
				if(!configField.category().isEmpty() && !config.contains(configField.category()))
					config.createSection(configField.category());
				
				if(field.getType().isArray())
				{
					// Integer
					if(field.getType().getComponentType().equals(Integer.TYPE))
						config.set(path, Arrays.asList((Integer[])field.get(this)));
					
					// Float
					else if(field.getType().getComponentType().equals(Float.TYPE))
						config.set(path, Arrays.asList((Float[])field.get(this)));
					
					// Double
					else if(field.getType().getComponentType().equals(Double.TYPE))
						config.set(path, Arrays.asList((Double[])field.get(this)));
					
					// Long
					else if(field.getType().getComponentType().equals(Long.TYPE))
						config.set(path, Arrays.asList((Long[])field.get(this)));
					
					// Short
					else if(field.getType().getComponentType().equals(Short.TYPE))
						config.set(path, Arrays.asList((Short[])field.get(this)));
					
					// Boolean
					else if(field.getType().getComponentType().equals(Boolean.TYPE))
						config.set(path, Arrays.asList((Boolean[])field.get(this)));
					
					// String
					else if(field.getType().getComponentType().equals(String.class))
						config.set(path, Arrays.asList((String[])field.get(this)));
					else
						throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + " for AutoConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				else
				{
					// Integer
					if(field.getType().equals(Integer.TYPE))
						config.set(path, field.get(this));
					
					// Float
					else if(field.getType().equals(Float.TYPE))
						config.set(path, field.get(this));
					
					// Double
					else if(field.getType().equals(Double.TYPE))
						config.set(path, field.get(this));
					
					// Long
					else if(field.getType().equals(Long.TYPE))
						config.set(path, field.get(this));
					
					// Short
					else if(field.getType().equals(Short.TYPE))
						config.set(path, field.get(this));
					
					// Boolean
					else if(field.getType().equals(Boolean.TYPE))
						config.set(path, field.get(this));
					
					// ItemStack
					else if(field.getType().equals(ItemStack.class))
						config.set(path, field.get(this));
					
					// String
					else if(field.getType().equals(String.class))
						config.set(path, field.get(this));
					else
						throw new IllegalArgumentException("Cannot use type " + field.getType().getSimpleName() + " for AutoConfiguration"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				// Record the comment
				if(!configField.comment().isEmpty())
					comments.put(path,configField.comment());
			}
			
			String output = config.saveToString();
			
			// Apply comments
			String category = ""; //$NON-NLS-1$
			List<String> lines = new ArrayList<String>(Arrays.asList(output.split("\n"))); //$NON-NLS-1$
			for(int l = 0; l < lines.size(); l++)
			{
				String line = lines.get(l);
				
				if(line.startsWith("#")) //$NON-NLS-1$
					continue;
				
				if(line.trim().startsWith("-")) //$NON-NLS-1$
					continue;
				
				if(!line.contains(":")) //$NON-NLS-1$
					continue;
				
				String path = ""; //$NON-NLS-1$
				line = line.substring(0, line.indexOf(":")); //$NON-NLS-1$
				
				if(line.startsWith("  ")) //$NON-NLS-1$
					path = category + "." + line.substring(2).trim(); //$NON-NLS-1$
				else
				{
					category = line.trim();
					path = line.trim();
				}
				
				if(comments.containsKey(path))
				{
					String indent = ""; //$NON-NLS-1$
					for(int i = 0; i < line.length(); i++)
					{
						if(line.charAt(i) == ' ')
							indent += " "; //$NON-NLS-1$
						else
							break;
					}
					
					// Add in the comment lines
					String[] commentLines = comments.get(path).split("\n"); //$NON-NLS-1$
					lines.add(l++, ""); //$NON-NLS-1$
					for(int i = 0; i < commentLines.length; i++)
					{
						commentLines[i] = indent + "# " + commentLines[i]; //$NON-NLS-1$
						lines.add(l++,commentLines[i]);
					}
				}
			}
			output = ""; //$NON-NLS-1$
			for(String line : lines)
				output += line + "\n"; //$NON-NLS-1$
			
			FileWriter writer = new FileWriter(mFile);
			writer.write(output);
			writer.close();
			return true;
		}
		catch ( IllegalArgumentException e )
		{
			e.printStackTrace();
		}
		catch ( IllegalAccessException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		return false;
	}
}
