package au.com.mineauz.MobHunting;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobHuntKillEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	private HuntData mHuntData;
	private DamageInformation mInfo;
	private LivingEntity mEntity;
	private Player mKiller;
	
	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
	
	public static HandlerList getHandlerList() 
	{
		return handlers;
	}
	
	public MobHuntKillEvent(HuntData huntData, DamageInformation info, LivingEntity deadEntity, Player killer)
	{
		mHuntData = huntData;
		mInfo = info;
		mEntity = deadEntity;
		mKiller = killer;
	}
	
	public HuntData getHuntData()
	{
		return mHuntData;
	}
	
	public DamageInformation getDamageInfo()
	{
		return mInfo;
	}
	
	public LivingEntity getEntity()
	{
		return mEntity;
	}
	
	public Player getPlayer()
	{
		return mKiller;
	}

}
