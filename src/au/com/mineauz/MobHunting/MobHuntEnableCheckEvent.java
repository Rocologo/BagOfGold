package au.com.mineauz.MobHunting;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobHuntEnableCheckEvent extends Event
{
	private static HandlerList handlers = new HandlerList();
	
	private boolean mEnable = true;
	private final Player mPlayer;
	
	public MobHuntEnableCheckEvent(Player player)
	{
		mPlayer = player;
	}
	
	public boolean isEnabled()
	{
		return mEnable;
	}
	
	public void setEnabled(boolean enable)
	{
		mEnable = enable;
	}
	
	public Player getPlayer()
	{
		return mPlayer;
	}
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}

}
