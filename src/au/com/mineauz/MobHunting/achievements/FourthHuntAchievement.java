package au.com.mineauz.MobHunting.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.MobHuntKillEvent;
import au.com.mineauz.MobHunting.MobHunting;

public class FourthHuntAchievement implements ProgressAchievement, Listener
{
	private ExtendedMobType mType;
	
	public FourthHuntAchievement(ExtendedMobType entity)
	{
		mType = entity;
	}
	@Override
	public String getName()
	{
		return "Master " + mType.getName() + " Hunter";
	}

	@Override
	public String getID()
	{
		return "hunting-level4-" + mType.getName().toLowerCase();
	}

	@Override
	public String getDescription()
	{
		return "Hunt " + getMaxProgress() + " " + mType.getName() + "s";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialHunter4;
	}

	@Override
	public int getMaxProgress()
	{
		return mType.getMax();
	}

	@Override
	public String inheritFrom() { return "hunting-level3-" + mType.getName().toLowerCase(); }
	
	@EventHandler(priority=EventPriority.MONITOR)
	private void onKillCompleted(MobHuntKillEvent event)
	{
		if(mType.matches(event.getEntity()))
			MobHunting.instance.getAchievements().awardAchievementProgress(this, event.getPlayer(), 1);
	}
}
