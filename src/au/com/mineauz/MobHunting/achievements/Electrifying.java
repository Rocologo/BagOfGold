package au.com.mineauz.MobHunting.achievements;

import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHuntKillEvent;
import au.com.mineauz.MobHunting.MobHunting;

public class Electrifying implements Achievement, Listener
{
	
	@Override
	public String getName()
	{
		return "Electrifying";
	}

	@Override
	public String getID()
	{
		return "electrifying";
	}

	@Override
	public String getDescription()
	{
		return "Kill a charged creeper";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialCharged;
	}
	
	@EventHandler
	private void onKill(MobHuntKillEvent event)
	{
		if(event.getEntity() instanceof Creeper && ((Creeper)event.getEntity()).isPowered())
			MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
	}

}
