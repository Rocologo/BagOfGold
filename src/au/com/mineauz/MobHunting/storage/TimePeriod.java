package au.com.mineauz.MobHunting.storage;

import au.com.mineauz.MobHunting.Messages;

public enum TimePeriod
{
	Day("Daily"),
	Week("Weekly"),
	Month("Monthly"),
	Year("Yearly"),
	AllTime("AllTime");
	
	private String mTable;
	
	private TimePeriod(String table)
	{
		mTable = table;
	}
	
	public String getTable()
	{
		return mTable;
	}
	
	public String translateName()
	{
		return Messages.getString("stats." + name().toLowerCase());
	}
	
	public String translateNameFriendly()
	{
		return Messages.getString("stats." + name().toLowerCase() + ".friendly");
	}

	public static TimePeriod parsePeriod( String period )
	{
		for(TimePeriod p : values())
		{
			if(period.equalsIgnoreCase(p.translateName().replaceAll(" ", "_")))
				return p;
		}
		
		return null;
	}
}
