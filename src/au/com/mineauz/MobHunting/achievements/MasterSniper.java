package au.com.mineauz.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;

public class MasterSniper implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.master-sniper.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "master-sniper"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.master-sniper.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialMasterSniper;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	private void onKillCompleted(MobHuntKillEvent event)
	{
		if(event.getPlayer().isInsideVehicle() && event.getDamageInfo().weapon.getType() == Material.BOW && !event.getDamageInfo().mele && event.getPlayer().getVehicle().getVelocity().length() > 0.2)
		{
			double dist = event.getDamageInfo().attackerPosition.distance(event.getEntity().getLocation());
			if(dist >= 40)
			{
				MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
			}
		}
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialMasterSniperCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialMasterSniperCmdDesc;
	}
}
