package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class ByTheBook implements Achievement
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

}
