package au.com.mineauz.MobHunting.achievements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.metadata.FixedMetadataValue;

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
		return player.hasMetadata("MH:achievement-" + achievement.getID());
	}
	
	public List<Achievement> getCompletedAchievements(OfflinePlayer player)
	{
		List<Achievement> achievements = new ArrayList<Achievement>();
		
		if(player.isOnline())
		{
			for(Achievement achievement : mAchievements.values())
			{
				if(hasAchievement(achievement, player.getPlayer()))
					achievements.add(achievement);
			}
		}
		else
		{
			Set<String> ids = loadAchievements(player);
			
			for(String id : ids)
			{
				if(mAchievements.containsKey(id))
					achievements.add(mAchievements.get(id));
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
		if(hasAchievement(achievement, player))
			return;
		
		addAchievement(player, achievement);
		player.setMetadata("MH:achievement-" + achievement.getID(), new FixedMetadataValue(MobHunting.instance, true));
		
		player.sendMessage(ChatColor.GOLD + "Special Kill Awarded!" + ChatColor.WHITE + ChatColor.ITALIC + " " + achievement.getName());
		player.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + achievement.getDescription());
		player.sendMessage(ChatColor.WHITE + "" + ChatColor.ITALIC + "You have been awarded $" + String.format("%.2f", achievement.getPrize()));
	}
	
	@SuppressWarnings( "unchecked" )
	private Set<String> loadAchievements(OfflinePlayer player)
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
				HashSet<String> ids = new HashSet<String>();
				for(String id : (List<String>)config.getList(player.getName()))
					ids.add(id);
				
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
	
	private void addAchievement(Player player, Achievement achievement)
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
				ArrayList<String> list = new ArrayList<String>();
				list.add(achievement.getID());
				
				config.set(player.getName(), list);
			}
			else
			{
				@SuppressWarnings( "unchecked" )
				List<String> list = (List<String>)config.getList(player.getName());
				
				ArrayList<String> modList = new ArrayList<String>(list);
				modList.add(achievement.getID());
				
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
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	private void onPlayerJoin(PlayerLoginEvent event)
	{
		Set<String> achievements = loadAchievements(event.getPlayer());
		
		// Load them up into metadata
		for(String id : achievements)
			event.getPlayer().setMetadata("MH:achievement-" + id, new FixedMetadataValue(MobHunting.instance, true));
	}
}
