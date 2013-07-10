package au.com.mineauz.MobHunting.achievements;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import au.com.mineauz.MobHunting.MobHunting;

public class AchievementManager implements Listener
{
	private HashMap<String, Achievement> mAchievements = new HashMap<String, Achievement>();
	
	public void initialize()
	{
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}
	
	public Achievement getAchievement(String id)
	{
		if(!mAchievements.containsKey(id))
			throw new IllegalArgumentException("There is no achievement by the id: " + id);
		
		return mAchievements.get(id);
	}
	
	public void registerAchievement(Achievement achievement)
	{
		Validate.notNull(achievement);
		
		if(achievement instanceof ProgressAchievement)
		{
			if(((ProgressAchievement)achievement).inheritFrom() != null)
			{
				Validate.isTrue(mAchievements.containsKey(((ProgressAchievement)achievement).inheritFrom()));
				Validate.isTrue(mAchievements.get(((ProgressAchievement)achievement).inheritFrom()) instanceof ProgressAchievement);
			}
		}
		
		mAchievements.put(achievement.getID(), achievement);
		
		if(achievement instanceof Listener)
			Bukkit.getPluginManager().registerEvents((Listener)achievement, MobHunting.instance);
	}
	
	public boolean hasAchievement(String achievement, Player player)
	{
		return hasAchievement(getAchievement(achievement), player);
	}
	public boolean hasAchievement(Achievement achievement, Player player)
	{
		if(!player.hasMetadata("MH:achievement-" + achievement.getID()))
			return false;
		
		for(MetadataValue value : player.getMetadata("MH:achievement-" + achievement.getID()))
		{
			if(value.getOwningPlugin() == MobHunting.instance)
			{
				if(value.value() instanceof Boolean)
					return value.asBoolean();
				
				return false;
			}
		}
		
		return false;
	}
	
	public int getProgress(String achievement, Player player)
	{
		Achievement a = getAchievement(achievement);
		Validate.isTrue(a instanceof ProgressAchievement, "This achievement does not have progress");
		
		return getProgress((ProgressAchievement)a, player);
	}
	
	public int getProgress(ProgressAchievement achievement, Player player)
	{
		if(!player.hasMetadata("MH:achievement-" + achievement.getID()))
			return 0;
		
		for(MetadataValue value : player.getMetadata("MH:achievement-" + achievement.getID()))
		{
			if(value.getOwningPlugin() == MobHunting.instance)
			{
				if(value.value() instanceof Boolean)
					return (value.asBoolean() ? achievement.getMaxProgress() : 0);
				else if(value.value() instanceof Integer)
					return value.asInt();
				
				return 0;
			}
		}
		
		return 0;
	}
	
	public List<Map.Entry<Achievement, Integer>> getCompletedAchievements(OfflinePlayer player)
	{
		List<Map.Entry<Achievement, Integer>> achievements = new ArrayList<Map.Entry<Achievement, Integer>>();
		
		if(player.isOnline())
		{
			for(Achievement achievement : mAchievements.values())
			{
				if(hasAchievement(achievement, player.getPlayer()))
					achievements.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(achievement, -1));
				else if(achievement instanceof ProgressAchievement && getProgress((ProgressAchievement)achievement, player.getPlayer()) > 0)
					achievements.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(achievement, getProgress((ProgressAchievement)achievement, player.getPlayer())));
			}
		}
		else
		{
			Set<Map.Entry<String, Integer>> ids = loadAchievements(player);
			
			for(Map.Entry<String, Integer> id : ids)
			{
				if(mAchievements.containsKey(id.getKey()))
					achievements.add(new AbstractMap.SimpleImmutableEntry<Achievement, Integer>(mAchievements.get(id.getKey()), id.getValue()));
			}
		}
		
		return achievements;
	}
	
	public Collection<Achievement> getAllAchievements()
	{
		return Collections.unmodifiableCollection(mAchievements.values());
	}
	
	public void awardAchievement(String achievement, Player player)
	{
		awardAchievement(getAchievement(achievement), player);
	}
	public void awardAchievement(Achievement achievement, Player player)
	{
		Validate.isTrue(!(achievement instanceof ProgressAchievement), "You need to award achievements with progress using awardAchievementProgress()");
		
		if(hasAchievement(achievement, player))
			return;
		
		addAchievement(player, achievement, -1);
		player.setMetadata("MH:achievement-" + achievement.getID(), new FixedMetadataValue(MobHunting.instance, true));
		
		player.sendMessage(ChatColor.GOLD + "Special Kill Awarded!" + ChatColor.WHITE + ChatColor.ITALIC + " " + achievement.getName());
		player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + achievement.getDescription());
		player.sendMessage(ChatColor.WHITE + "" + ChatColor.ITALIC + "You have been awarded $" + String.format("%.2f", achievement.getPrize()));
		
		player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
		FireworkEffect effect = FireworkEffect.builder().withColor(Color.BLUE, Color.YELLOW, Color.GREEN).flicker(true).trail(true).build();
		Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
		firework.getFireworkMeta().addEffect(effect);
		
	}
	
	public void awardAchievementProgress(String achievement, Player player, int amount)
	{
		Achievement a = getAchievement(achievement);
		Validate.isTrue(a instanceof ProgressAchievement, "You need to award normal achievements with awardAchievement()");
		
		awardAchievementProgress((ProgressAchievement)a, player, amount);
	}
	
	public void awardAchievementProgress(ProgressAchievement achievement, Player player, int amount)
	{
		if(hasAchievement(achievement, player))
			return;
		
		Validate.isTrue(amount > 0);
		
		int curProgress = getProgress(achievement, player);
		
		while(achievement.inheritFrom() != null && curProgress == 0)
		{
			// This allows us to just mark progress against the highest level version and have it automatically given to the lower level ones
			if(!hasAchievement(achievement.inheritFrom(), player))
			{
				achievement = (ProgressAchievement)getAchievement(achievement.inheritFrom());
				curProgress = getProgress(achievement, player);
			}
			else
			{
				curProgress = ((ProgressAchievement)getAchievement(achievement.inheritFrom())).getMaxProgress();
			}
		}

		int maxProgress = achievement.getMaxProgress();
		int nextProgress = Math.min(maxProgress, curProgress + amount);
		
		if(nextProgress == maxProgress)
		{
			player.setMetadata("MH:achievement-" + achievement.getID(), new FixedMetadataValue(MobHunting.instance, true));
			addAchievement(player, achievement, -1);
			
			player.sendMessage(ChatColor.GOLD + "Special Kill Complete!" + ChatColor.WHITE + ChatColor.ITALIC + " " + achievement.getName());
			player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + achievement.getDescription());
			player.sendMessage(ChatColor.WHITE + "" + ChatColor.ITALIC + "You have been awarded $" + String.format("%.2f", achievement.getPrize()));
			
			player.getWorld().playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
			FireworkEffect effect = FireworkEffect.builder().withColor(Color.BLUE, Color.YELLOW, Color.GREEN).flicker(true).trail(true).build();
			Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
			firework.getFireworkMeta().addEffect(effect);
		}
		else
		{
			player.setMetadata("MH:achievement-" + achievement.getID(), new FixedMetadataValue(MobHunting.instance, nextProgress));
			addAchievement(player, achievement, nextProgress);
			
			int segment = Math.min(25, maxProgress / 2);
			
			if(curProgress / segment < nextProgress / segment || curProgress == 0 && nextProgress > 0)
			{
				player.sendMessage(ChatColor.BLUE + "Special Kill Progress: " + ChatColor.WHITE + ChatColor.ITALIC + " " + achievement.getName());
				player.sendMessage(ChatColor.GRAY + "" + nextProgress + " / " + maxProgress);
			}
		}
	}
	
	@SuppressWarnings( "unchecked" )
	private Set<Map.Entry<String, Integer>> loadAchievements(OfflinePlayer player)
	{
		File file = new File(MobHunting.instance.getDataFolder(), "awards.yml");

		YamlConfiguration config = new YamlConfiguration();
		try
		{
			if(!file.exists())
				file.createNewFile();
			
			config.load(file);
			
			if(config.isList(player.getName()))
			{
				HashSet<Map.Entry<String, Integer>> ids = new HashSet<Map.Entry<String, Integer>>();
				for(Object obj : (List<Object>)config.getList(player.getName()))
				{
					if(obj instanceof String)
						ids.add(new AbstractMap.SimpleImmutableEntry<String, Integer>((String)obj, -1));
					else if(obj instanceof Map)
					{
						Map<String, Integer> map = (Map<String, Integer>)obj;
						String id = map.keySet().iterator().next();
						ids.add(new AbstractMap.SimpleImmutableEntry<String, Integer>(id, (Integer)map.get(id)));
					}
				}
				
				return ids;
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		catch ( InvalidConfigurationException e )
		{
			e.printStackTrace();
		}
		
		return Collections.EMPTY_SET;
	}
	
	private void addAchievement(Player player, Achievement achievement, int progress)
	{
		File file = new File(MobHunting.instance.getDataFolder(), "awards.yml");

		YamlConfiguration config = new YamlConfiguration();
		try
		{
			if(!file.exists())
				file.createNewFile();
			
			config.load(file);
			
			if(!config.isList(player.getName()))
			{
				ArrayList<Object> list = new ArrayList<Object>();
				
				if(progress == -1)
					list.add(achievement.getID());
				else
				{
					Map<String, Integer> obj = new HashMap<String, Integer>();
					obj.put(achievement.getID(), progress);
					list.add(obj);
				}
				
				config.set(player.getName(), list);
			}
			else
			{
				@SuppressWarnings( "unchecked" )
				List<Object> list = (List<Object>)config.getList(player.getName());
				
				ArrayList<Object> modList = new ArrayList<Object>(list);
				
				// Remove partial progress version
				for(Object obj : modList)
				{
					if(obj instanceof Map)
					{
						@SuppressWarnings( "unchecked" )
						Map<String, Integer> map = (Map<String, Integer>)obj;
						if(map.containsKey(achievement.getID()))
						{
							modList.remove(obj);
							break;
						}
					}
				}
				
				if(progress == -1)
					modList.add(achievement.getID());
				else
				{
					Map<String, Integer> obj = new HashMap<String, Integer>();
					obj.put(achievement.getID(), progress);
					modList.add(obj);
				}
				
				config.set(player.getName(), modList);
			}
			
			config.save(file);
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		catch ( InvalidConfigurationException e )
		{
			e.printStackTrace();
		}
	}
	
	public void load(Player player)
	{
		Set<Map.Entry<String, Integer>> achievements = loadAchievements(player);
		
		// Load them up into metadata
		for(Map.Entry<String, Integer> id : achievements)
		{
			if(id.getValue() == -1)
				player.setMetadata("MH:achievement-" + id.getKey(), new FixedMetadataValue(MobHunting.instance, true));
			else
				player.setMetadata("MH:achievement-" + id.getKey(), new FixedMetadataValue(MobHunting.instance, id.getValue()));
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	private void onPlayerJoin(PlayerLoginEvent event)
	{
		load(event.getPlayer());
		
	}
}
