package one.lindegaard.MobHunting.mobs;

public enum MobPlugin {
	Minecraft("Minecraft", 0), MythicMobs("MythicMobs", 1), Citizens("Citizens",
			2), TARDISWeepingAngels("TARDISWeepingAngels", 3), CustomMobs("CustomMobs", 4);

	private final String name;
	private final Integer id;

	private MobPlugin(String name, Integer id) {
		this.name = name;
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	
	public boolean equalsName(String otherName) {
		return (otherName == null) ? false : name.equals(otherName);
	}

	public String toString() {
		return name;
	}

	public MobPlugin valueOf(int id) {
		return MobPlugin.values()[id];
	}

}
