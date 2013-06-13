package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class ItsMagic implements Achievement
{

	@Override
	public String getName()
	{
		return "Its Magic!";
	}

	@Override
	public String getID()
	{
		return "itsmagic";
	}

	@Override
	public String getDescription()
	{
		return "Kill a mob with a potion";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialItsMagic;
	}

}
