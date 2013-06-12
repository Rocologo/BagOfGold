package au.com.mineauz.MobHunting.achievements;

import au.com.mineauz.MobHunting.MobHunting;

public class RecordHungry implements Achievement
{

	@Override
	public String getName()
	{
		return "Record Hungry";
	}

	@Override
	public String getID()
	{
		return "recordhungry";
	}

	@Override
	public String getDescription()
	{
		return "Get a seleton to kill a creeper";
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialRecordHungry;
	}

}
