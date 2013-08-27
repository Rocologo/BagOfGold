package au.com.mineauz.MobHunting.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHuntKillEvent;
import au.com.mineauz.MobHunting.MobHunting;

public class TheHuntBegins implements Achievement, Listener
{
	@Override
	public String getName()
	{
		return Messages.getString("achievements.huntbegins.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "huntbegins"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.huntbegins.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialHuntBegins;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event)
	{
		MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
	}
}
