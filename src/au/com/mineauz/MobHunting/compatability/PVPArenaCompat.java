package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHuntEnableCheckEvent;
import au.com.mineauz.MobHunting.MobHunting;

public class PVPArenaCompat implements Listener
{
	public PVPArenaCompat()
	{
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
		MobHunting.instance.getLogger().info("Enabling PVPArena Compatability"); //$NON-NLS-1$
	}
	/**
	@EventHandler(priority=EventPriority.NORMAL)
	private void onPlayerJoinMinigame(MobHuntEnableCheckEvent event)
	{
		MinigamePlayer player = Minigames.plugin.pdata.getMinigamePlayer(event.getPlayer());
		if(player != null && player.isInMinigame())
			event.setEnabled(false);
	}**/
}
