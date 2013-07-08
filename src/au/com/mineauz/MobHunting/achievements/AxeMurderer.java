package au.com.mineauz.MobHunting.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHuntKillEvent;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.util.Misc;

public class AxeMurderer implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return "Axe Murderer";
	}

	@Override
	public String getID()
	{
		return "axemurderer";
	}

	@Override
	public String getDescription()
	{
		return "Kill a mob with an axe";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialAxeMurderer;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event)
	{
		if(Misc.isAxe(event.getDamageInfo().weapon))
			MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
	}
}
