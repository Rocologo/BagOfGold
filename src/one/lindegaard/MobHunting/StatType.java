package one.lindegaard.MobHunting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import one.lindegaard.MobHunting.mobs.MobPlugin;
import one.lindegaard.MobHunting.mobs.MobStore;

public class StatType {
	public static final StatType AchievementCount = new StatType("achievement_count", "stats.achievement_count");
	public static final StatType KillsTotal = new StatType("total_kill", "stats.total_kill");
	public static final StatType AssistsTotal = new StatType("total_assist", "stats.total_assist");

	// TODO: change 500
	private static StatType[] mValues = new StatType[3 + ExtendedMobType.values().length * 2 + 500];
	private static HashMap<String, StatType> mNameLookup = new HashMap<String, StatType>();

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

		// adding other mobtype from other plugins
		Iterator<Entry<Integer, MobStore>> itr = MobHunting.getMobManager().getAllMobs().entrySet().iterator();
		int l = 3 + ExtendedMobType.values().length * 2;
		while (itr.hasNext()) {
			MobStore mob = (MobStore) itr.next().getValue();
			if (!mob.getMobPlugin().equals(MobPlugin.Minecraft)) {
				//Messages.debug("Adding new StatType(%s,%s)", mob.getMobtype(), mob.getMobPlugin().name());
				mValues[l + 1] = new StatType(mob.getMobPlugin().name() + "_" + mob.getMobtype() + "_Kill",
						"stats." + mob.getMobPlugin().name() + "_" + mob.getMobtype() + "_Kill");
				mValues[l + 1] = new StatType(mob.getMobPlugin().name() + "_" + mob.getMobtype() + "_Assist",
						"stats." + mob.getMobPlugin().name() + "_" + mob.getMobtype() + "_Assist");
				l = l + 2;
			}
		}

		for (int i = 0; i < mValues.length; ++i)
			if (mValues[i] != null)
				mNameLookup.put(mValues[i].mColumnName, mValues[i]);

	}

	private String mColumnName;
	private String mName;
	private String[] mExtra;

	public StatType(String columnName, String name, String... extra) {
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
	
	public boolean equals(StatType other){
		return mColumnName.equals(other.mColumnName);
	}

}
