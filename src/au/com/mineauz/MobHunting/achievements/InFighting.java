package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class InFighting implements Achievement
{

	@Override
	public String getName()
	{
		return "Infighting";
	}

	@Override
	public String getID()
	{
		return "infighting";
	}

	@Override
	public String getDescription()
	{
		return "Get a seleton to kill another skeleton";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialInfighting;
	}

}
