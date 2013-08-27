package au.com.mineauz.MobHunting.achievements;

import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHuntKillEvent;
import au.com.mineauz.MobHunting.MobHunting;

public class CreeperBoxing implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.creeperboxing.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "creeperboxing"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.creeperboxing.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialCreeperPunch;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event)
	{
		if(event.getEntity() instanceof Creeper && !event.getDamageInfo().usedWeapon)
			MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
	}
}
