package au.com.mineauz.MobHunting.achievements;

import org.bukkit.GameMode;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class RecordHungry implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.recordhungry.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "recordhungry"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.recordhungry.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialRecordHungry;
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
		
		if(damage.getDamager() instanceof Arrow && ((Arrow)damage.getDamager()).getShooter() instanceof Skeleton)
		{
			Skeleton skele = (Skeleton)((Arrow)damage.getDamager()).getShooter();
			
			if(killed.getTarget() instanceof Player)
			{
				Player target = (Player)killed.getTarget();
				
				if(skele.getTarget() == target && target.getGameMode() != GameMode.CREATIVE && MobHunting.isHuntEnabled(target))
					MobHunting.instance.getAchievements().awardAchievement(this, target);
			}
		}
	}
}
