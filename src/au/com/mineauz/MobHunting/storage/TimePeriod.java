package au.com.mineauz.MobHunting.storage;

import au.com.mineauz.MobHunting.Messages;

public enum TimePeriod
{
	Day("Daily"), //$NON-NLS-1$
	Week("Weekly"), //$NON-NLS-1$
	Month("Monthly"), //$NON-NLS-1$
	Year("Yearly"), //$NON-NLS-1$
	AllTime("AllTime"); //$NON-NLS-1$
	
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
		return Messages.getString("stats." + name().toLowerCase()); //$NON-NLS-1$
	}
	
	public String translateNameFriendly()
	{
		return Messages.getString("stats." + name().toLowerCase() + ".friendly"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static TimePeriod parsePeriod( String period )
	{
		for(TimePeriod p : values())
		{
			if(period.equalsIgnoreCase(p.translateName().replaceAll(" ", "_"))) //$NON-NLS-1$ //$NON-NLS-2$
				return p;
		}
		
		return null;
	}
}
