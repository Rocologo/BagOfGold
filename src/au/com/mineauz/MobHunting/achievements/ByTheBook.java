package au.com.mineauz.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHuntKillEvent;
import au.com.mineauz.MobHunting.MobHunting;

public class ByTheBook implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return "By the Book";
	}

	@Override
	public String getID()
	{
		return "bythebook";
	}

	@Override
	public String getDescription()
	{
		return "Kill an enemy using a book";
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
}
