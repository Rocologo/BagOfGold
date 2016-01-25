package au.com.mineauz.MobHunting.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;

public class TheHuntBegins implements Achievement, Listener
{
	@Override
	public String getName()
	{
		return Messages.getString("achievements.huntbegins.name"); 
	}

	@Override
	public String getID()
	{
		return "huntbegins"; 
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.huntbegins.description"); 
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

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialHuntBeginsCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialHuntBeginsCmdDesc;
	}
}
