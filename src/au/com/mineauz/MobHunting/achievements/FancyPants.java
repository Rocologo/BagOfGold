package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class FancyPants implements Achievement
{

	@Override
	public String getName()
	{
		return "Fancy Pants";
	}

	@Override
	public String getID()
	{
		return "fancypants";
	}

	@Override
	public String getDescription()
	{
		return "Complete a kill with complete set of diamond armour, and a diamond sword, all enchanted";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialFancyPants;
	}

}
