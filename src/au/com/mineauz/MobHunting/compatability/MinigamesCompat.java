package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHunting;

import com.pauldavdesign.mineauz.minigames.events.EndMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.JoinMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.QuitMinigameEvent;
import com.pauldavdesign.mineauz.minigames.events.SpectateMinigameEvent;

public class MinigamesCompat implements Listener
{
	public MinigamesCompat()
	{
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
		MobHunting.instance.getLogger().info("Enabling Minigames Compatability");
	}
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerJoinMinigame(JoinMinigameEvent event)
	{
		if(event.isCancelled())
			return;
		
		MobHunting.setHuntEnabled(event.getPlayer(), false);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerSpectateMinigame(SpectateMinigameEvent event)
	{
		if(event.isCancelled())
			return;

		MobHunting.setHuntEnabled(event.getPlayer(), false);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerLeaveMinigame(EndMinigameEvent event)
	{
		if(event.isCancelled())
			return;
		
		MobHunting.setHuntEnabled(event.getPlayer(), true);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onPlayerLeaveMinigame(QuitMinigameEvent event)
	{
		if(event.isCancelled())
			return;
		
		MobHunting.setHuntEnabled(event.getPlayer(), true);
	}
}
