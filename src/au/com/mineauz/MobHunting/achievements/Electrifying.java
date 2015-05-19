package au.com.mineauz.MobHunting.achievements;

import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;

public class Electrifying implements Achievement, Listener
{
	
	@Override
	public String getName()
	{
		return Messages.getString("achievements.electrifying.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "electrifying"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.electrifying.description"); //$NON-NLS-1$
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

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialChargedCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialChargedCmdDesc;
	}
}
