package one.lindegaard.MobHunting;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import one.lindegaard.MobHunting.mobs.MobPlugin;
import one.lindegaard.MobHunting.mobs.ExtendedMob;
import one.lindegaard.MobHunting.mobs.MinecraftMob;

public class StatType {
	public static final StatType AchievementCount = new StatType("achievement_count", "stats.achievement_count");
	public static final StatType KillsTotal = new StatType("total_kill", "stats.total_kill");
	public static final StatType AssistsTotal = new StatType("total_assist", "stats.total_assist");
	private static StatType[] mValues = new StatType[3 + MobPlugin.values().length * 2
			+ MobHunting.getExtendedMobManager().getAllMobs().size() * 2];
	private static HashMap<String, StatType> mNameLookup = new HashMap<String, StatType>();

	static {
		mValues[0] = KillsTotal;
		mValues[1] = AssistsTotal;
		mValues[2] = AchievementCount;

		int offset = 3;
		// Adding plugin types (Minecraft_kills, MythicMob_kills, .....)
		for (int i = 0; i < MobPlugin.values().length; ++i)
			mValues[offset + i] = new StatType(MobPlugin.values()[i] + "_kill",
					"stats." + MobPlugin.values()[i].name() + ".kill");

		for (int i = 0; i < MobPlugin.values().length; ++i)
			mValues[offset + i + MobPlugin.values().length] = new StatType(MobPlugin.values()[i] + "_assist",
					"stats." + MobPlugin.values()[i].name() + ".assist");

		// Adding Vanilla Minecraft mobTypes
		offset = offset + MobPlugin.values().length * 2;
		for (int i = 0; i < MinecraftMob.values().length; ++i)
			mValues[offset + i] = new StatType(MinecraftMob.values()[i] + "_kill", "stats.name-format", "mob",
					"mobs." + MinecraftMob.values()[i].name() + ".name", "stattype", "stats.kills");

		for (int i = 0; i < MinecraftMob.values().length; ++i)
			mValues[offset + i + MinecraftMob.values().length] = new StatType(MinecraftMob.values()[i] + "_assist",
					"stats.name-format", "mob", "mobs." + MinecraftMob.values()[i].name() + ".name", "stattype",
					"stats.assists");

		// adding other mobtypes from other plugins
		Iterator<Entry<Integer, ExtendedMob>> itr = MobHunting.getExtendedMobManager().getAllMobs().entrySet()
				.iterator();
		offset = offset + MinecraftMob.values().length * 2;
		while (itr.hasNext()) {
			ExtendedMob mob = (ExtendedMob) itr.next().getValue();
			if (mob.getMobPlugin() != MobPlugin.Minecraft) {
				mValues[offset] = new StatType(mob.getMobPlugin().name() + "_" + mob.getMobtype() + "_Kill",
						"stats.name-format", "mob",
						"mobs." + mob.getMobPlugin().name() + "_" + mob.getMobtype() + ".name", "stattype",
						"stats.kills");

				mValues[offset + 1] = new StatType(mob.getMobPlugin().name() + "_" + mob.getMobtype() + "_Assist",
						"stats.name-format", "mob",
						"mobs." + mob.getMobPlugin().name() + "_" + mob.getMobtype() + ".name", "stattype",
						"stats.assists");

				offset = offset + 2;
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

	public static StatType fromMobType(ExtendedMob mob, boolean kill) {
		if (mob.getMobPlugin() == MobPlugin.Minecraft) {
			return new StatType(mob.getMobtype() + "_Kill", "stats.name-format", "mob",
					"mobs." + mob.getMobPlugin().name() + "_" + mob.getMobtype() + ".name", "stattype", "stats.kills");
		} else {
			return new StatType(mob.getMobPlugin().name() + "_" + mob.getMobtype() + "_Kill", "stats.name-format",
					"mob", "mobs." + mob.getMobPlugin().name() + "_" + mob.getMobtype() + ".name", "stattype",
					"stats.kills");
		}
	}

	public static StatType fromColumnName(String columnName) {
		return mNameLookup.get(columnName);
	}

	public String getDBColumn() {
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
