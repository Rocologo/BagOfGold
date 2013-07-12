package au.com.mineauz.MobHunting.achievements;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import au.com.mineauz.MobHunting.MobHunting;

public class WolfKillAchievement implements ProgressAchievement, Listener
{

	@Override
	public String getName()
	{
		return "Master of the Fang";
	}

	@Override
	public String getID()
	{
		return "fangmaster";
	}

	@Override
	public String getDescription()
	{
		return "Use your tamed wolves to kill 500 mobs";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialFangMaster;
	}

	@Override
	public int getMaxProgress()
	{
		return 500;
	}

	@Override
	public String inheritFrom() { return null; }

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onWolfKillMob(EntityDeathEvent event)
	{
		if(!MobHunting.isHuntEnabledInWorld(event.getEntity().getWorld()) || !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;
		
		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent)event.getEntity().getLastDamageCause();
		
		if(!(dmg.getDamager() instanceof Wolf))
			return;
		
		Wolf killer = (Wolf)dmg.getDamager();
		
		if(killer.isTamed() && killer.getOwner() != null)
		{
			Player owner = Bukkit.getPlayerExact(killer.getOwner().getName());
			
			if(owner != null && MobHunting.isHuntEnabled(owner))
			{
				MobHunting.instance.getAchievements().awardAchievementProgress(this, owner, 1);
			}
		}
		
	}
}
