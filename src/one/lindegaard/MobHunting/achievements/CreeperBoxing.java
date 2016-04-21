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
		if(event.getKilledEntity() instanceof Creeper && !event.getDamageInfo().usedWeapon)
			MobHunting.getInstance().getAchievements().awardAchievement(this, event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialCreeperPunchCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialCreeperPunchCmdDesc;
	}
}
