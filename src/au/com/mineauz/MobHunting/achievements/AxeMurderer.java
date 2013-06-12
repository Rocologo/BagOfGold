package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class AxeMurderer implements Achievement
{

	@Override
	public String getName()
	{
		return "Axe Murderer";
	}

	@Override
	public String getID()
	{
		return "axemurderer";
	}

	@Override
	public String getDescription()
	{
		return "Kill a mob with an axe";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialAxeMurderer;
	}

}
