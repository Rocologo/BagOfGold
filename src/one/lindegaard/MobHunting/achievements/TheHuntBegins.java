package one.lindegaard.MobHunting.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;

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
		MobHunting.getInstance().getAchievements().awardAchievement(this, event.getPlayer());
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
