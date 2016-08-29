package one.lindegaard.MobHunting;

import java.util.Arrays;
import java.util.HashMap;

public class StatType {
	public static final StatType AchievementCount = new StatType("achievement_count", "stats.achievement_count");
	public static final StatType KillsTotal = new StatType("total_kill", "stats.total_kill");
	public static final StatType AssistsTotal = new StatType("total_assist", "stats.total_assist");

	private static final StatType[] mValues = new StatType[3 + ExtendedMobType.values().length * 2];
	private static final HashMap<String, StatType> mNameLookup = new HashMap<String, StatType>();

	static {
		mValues[0] = AchievementCount;
		mValues[1] = KillsTotal;
		mValues[2] = AssistsTotal;

		//Adding Vanilla Minecraft mobTypes
		for (int i = 0; i < ExtendedMobType.values().length; ++i)
			mValues[3 + i] = new StatType(ExtendedMobType.values()[i] + "_kill", "stats.name-format", "mob",
					"mobs." + ExtendedMobType.values()[i].name() + ".name", "stattype", "stats.kills");

		for (int i = 0; i < ExtendedMobType.values().length; ++i)
			mValues[3 + i + ExtendedMobType.values().length] = new StatType(ExtendedMobType.values()[i] + "_assist",
					"stats.name-format", "mob", "mobs." + ExtendedMobType.values()[i].name() + ".name", "stattype",
					"stats.assists");
		
		//TODO: Add found MythicMob names 
		//TODO: Add found CustomMob names 
		//TODO: Add found Citizens names 

		for (int i = 0; i < mValues.length; ++i)
			mNameLookup.put(mValues[i].mColumnName, mValues[i]);
	}

	//TODO: private String mMobType;
	private String mColumnName;
	private String mName;
	private String[] mExtra;

	StatType(String columnName, String name, String... extra) {
		//TODO: mMobType = mobType
		mColumnName = columnName;
		mName = name;
		mExtra = extra;
	}

	public static StatType fromMobType(ExtendedMobType mob, boolean kill) {
		int index = mob.ordinal();
		if (!kill)
			index += ExtendedMobType.values().length;
		return mValues[index + 3];
	}

	public static StatType fromColumnName(String columnName) {
		return mNameLookup.get(columnName);
	}

	public String getDBColumn() {
		//TODO: chance returned column 
		// if mColumnName endsWith("_kill")
		// return "total_kill"; 
		// else if mColumnName endsWith("_assist")
		// return "total_assist"; 
		// else return "achievement_count";
		return mColumnName;
	}

	public String translateName() {
		if (mExtra == null)
			return Messages.getString(mName);

		String[] extra = Arrays.copyOf(mExtra, mExtra.length);
		for (int i = 1; i < extra.length; i += 2)
			extra[i] = Messages.getString(extra[i]);

		return Messages.getString(mName, (Object[]) extra);
	}

	public static StatType[] values() {
		return mValues;
	}

	public static StatType getNextStatType(StatType st) {
		for (int i = 0; i < mValues.length; i++) {
			if (st.equals(mValues[i])) {
				if (i == mValues.length)
					return mValues[0];
				else
					return mValues[i + 1];
			}
		}
		return st;
	}

	public static StatType parseStat(String typeName) {
		for (StatType type : mValues) {
			if (typeName.equalsIgnoreCase(type.translateName().replace(" ", "_")))
				return type;
		}

		return null;
	}
}
