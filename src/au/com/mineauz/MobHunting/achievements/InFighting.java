package au.com.mineauz.MobHunting.achievements;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class InFighting implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.infighting.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "infighting"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.infighting.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialInfighting;
	}

	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onDeath(EntityDeathEvent event)
	{
		if(!(event.getEntity() instanceof Skeleton) || !MobHunting.isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;
		
		Skeleton killed = (Skeleton)event.getEntity();
		
		if(!(killed.getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;
		
		EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent)killed.getLastDamageCause();
		
		if(damage.getDamager() instanceof Arrow && ((Arrow)damage.getDamager()).getShooter() instanceof Skeleton)
		{
			Skeleton skele = (Skeleton)((Arrow)damage.getDamager()).getShooter();
			
			if(killed.getTarget() == skele && skele.getTarget() == killed)
			{
				DamageInformation a,b;
				a = MobHunting.instance.getDamageInformation(killed);
				b = MobHunting.instance.getDamageInformation(skele);
				
				Player initiator = null;
				if(a != null)
					initiator = a.attacker;
				
				if(b != null && initiator == null)
					initiator = b.attacker;
				
				if(initiator != null && MobHunting.isHuntEnabled(initiator))
					MobHunting.instance.getAchievements().awardAchievement(this, initiator);
			}
		}
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialInfightingCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialInfightingCmdDesc;
	}
}
