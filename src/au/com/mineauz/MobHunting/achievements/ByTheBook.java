package au.com.mineauz.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;

public class ByTheBook implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.bythebook.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "bythebook"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.bythebook.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialByTheBook;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event)
	{
		if(event.getDamageInfo().weapon.getType() == Material.BOOK || event.getDamageInfo().weapon.getType() == Material.WRITTEN_BOOK || event.getDamageInfo().weapon.getType() == Material.BOOK_AND_QUILL)
			MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialByTheBookCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialByTheBookCmdDesc;
	}
}
