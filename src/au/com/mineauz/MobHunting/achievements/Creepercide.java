package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class Creepercide implements Achievement
{

	@Override
	public String getName()
	{
		return "Creepercide";
	}

	@Override
	public String getID()
	{
		return "creepercide";
	}

	@Override
	public String getDescription()
	{
		return "Kill a creeper with another creeper";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialCreepercide;
	}

}
