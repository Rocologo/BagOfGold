package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class CreeperBoxing implements Achievement
{

	@Override
	public String getName()
	{
		return "Creeper Boxing";
	}

	@Override
	public String getID()
	{
		return "creeperboxing";
	}

	@Override
	public String getDescription()
	{
		return "Box with a creeper and win!";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialCreeperPunch;
	}

}
