package au.com.mineauz.MobHunting.storage;

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
}
