package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class Electrifying implements Achievement
{

	@Override
	public String getName()
	{
		return "Electrifying";
	}

	@Override
	public String getID()
	{
		return "electrifying";
	}

	@Override
	public String getDescription()
	{
		return "Kill a charged creeper";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialCharged;
	}

}
