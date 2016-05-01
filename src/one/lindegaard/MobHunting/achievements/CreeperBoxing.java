package one.lindegaard.MobHunting.achievements;

import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;

public class CreeperBoxing implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.creeperboxing.name"); 
	}

	@Override
	public String getID()
	{
		return "creeperboxing"; 
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.creeperboxing.description"); 
	}

	@Override
	public double getPrize()
	{
		return MobHunting.getConfigManager().specialCreeperPunch;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event)
	{
		if(event.getKilledEntity() instanceof Creeper && !event.getDamageInfo().usedWeapon)
			MobHunting.getAchievements().awardAchievement(this, event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialCreeperPunchCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialCreeperPunchCmdDesc;
	}
}
