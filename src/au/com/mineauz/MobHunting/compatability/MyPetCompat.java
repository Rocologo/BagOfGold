package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import au.com.mineauz.MobHunting.MobHunting;

import de.Keyle.MyPet.api.entity.MyPetEntity;
import de.Keyle.MyPet.entity.types.MyPetType;

public class MyPetCompat implements Listener
{
	public MyPetCompat()
	{
		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);
	}
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	private void onWolfKillMob(EntityDeathEvent event)
	{
		if(!MobHunting.isHuntEnabledInWorld(event.getEntity().getWorld()) || !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;
		
		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent)event.getEntity().getLastDamageCause();
		
		if(!(dmg.getDamager() instanceof MyPetEntity))
			return;
		
		MyPetEntity killer = (MyPetEntity)dmg.getDamager();
		
		if(killer.getPetType() != MyPetType.Wolf)
			return;

		
		if(killer.getOwner() != null)
		{
			Player owner = killer.getOwner().getPlayer();
			
			if(owner != null && MobHunting.isHuntEnabled(owner))
			{
				MobHunting.instance.getAchievements().awardAchievementProgress("fangmaster", owner, 1); //$NON-NLS-1$
			}
		}
	}
}
