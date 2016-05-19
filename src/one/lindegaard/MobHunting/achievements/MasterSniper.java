package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;

public class MasterSniper implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.master-sniper.name"); 
	}

	@Override
	public String getID()
	{
		return "master-sniper"; 
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.master-sniper.description"); 
	}

	@Override
	public double getPrize()
	{
		return MobHunting.getConfigManager().specialMasterSniper;
	}

	@EventHandler(priority=EventPriority.MONITOR)
	private void onKillCompleted(MobHuntKillEvent event)
	{
		if(event.getPlayer().isInsideVehicle() && event.getDamageInfo().weapon.getType() == Material.BOW && !event.getDamageInfo().mele && event.getPlayer().getVehicle().getVelocity().length() > 0.2)
		{
			double dist = event.getDamageInfo().attackerPosition.distance(event.getKilledEntity().getLocation());
			if(dist >= 40)
			{
				MobHunting.getAchievements().awardAchievement(this, event.getPlayer());
			}
		}
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialMasterSniperCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialMasterSniperCmdDesc;
	}
	
	@Override
	public ItemStack getSymbol() {
		return new ItemStack(Material.BOW);
	}
}
