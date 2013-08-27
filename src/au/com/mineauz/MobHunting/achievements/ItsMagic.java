package au.com.mineauz.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHuntKillEvent;
import au.com.mineauz.MobHunting.MobHunting;

public class ItsMagic implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.itsmagic.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "itsmagic"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.itsmagic.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialItsMagic;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event)
	{
		if(event.getDamageInfo().weapon.getType() == Material.POTION)
			MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
	}
}
