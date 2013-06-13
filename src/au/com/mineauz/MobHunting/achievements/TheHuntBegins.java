package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class TheHuntBegins implements Achievement
{

	@Override
	public String getName()
	{
		return "The Hunt Begins";
	}

	@Override
	public String getID()
	{
		return "huntbegins";
	}

	@Override
	public String getDescription()
	{
		return "Make your first hunt. There are more achievments to get all to do with interesting or unique ways to kill mobs.";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialHuntBegins;
	}

}
