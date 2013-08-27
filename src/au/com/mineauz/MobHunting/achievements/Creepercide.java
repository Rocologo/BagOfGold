package au.com.mineauz.MobHunting.achievements;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class Creepercide implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.creepercide.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "creepercide"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.creepercide.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialCreepercide;
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onDeath(EntityDeathEvent event)
	{
		if(!(event.getEntity() instanceof Creeper) || !MobHunting.isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;
		
		Creeper killed = (Creeper)event.getEntity();
		
		if(!(killed.getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;
		
		EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent)killed.getLastDamageCause();
		
		if(damage.getDamager() instanceof Creeper)
		{
			Player initiator = null;
			
			if(((Creeper)event.getEntity()).getTarget() instanceof Player)
				initiator = (Player)((Creeper)event.getEntity()).getTarget();
			else
			{
				DamageInformation a,b;
				a = MobHunting.instance.getDamageInformation(killed);
				b = MobHunting.instance.getDamageInformation((Creeper)damage.getDamager());
				
				if(a != null)
					initiator = a.attacker;
				
				if(b != null && initiator == null)
					initiator = b.attacker;
			}
			
			if(initiator != null && MobHunting.isHuntEnabled(initiator))
				MobHunting.instance.getAchievements().awardAchievement("creepercide", initiator); //$NON-NLS-1$
		}
	}
}
