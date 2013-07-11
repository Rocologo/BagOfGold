package au.com.mineauz.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHuntKillEvent;
import au.com.mineauz.MobHunting.MobHunting;

public class MasterSniper implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return "Master Sniper";
	}

	@Override
	public String getID()
	{
		return "master-sniper";
	}

	@Override
	public String getDescription()
	{
		return "Snipe a mob over 40 blocks away while in a moving vehicle or on a moving horse.";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialMasterSniper;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	private void onKillCompleted(MobHuntKillEvent event)
	{
		if(event.getPlayer().isInsideVehicle() && event.getDamageInfo().weapon.getType() == Material.BOW && !event.getDamageInfo().mele && event.getPlayer().getVehicle().getVelocity().length() > 0.2)
		{
			double dist = event.getDamageInfo().attackerPosition.distance(event.getEntity().getLocation());
			if(dist >= 40)
			{
				MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
			}
		}
	}
}
