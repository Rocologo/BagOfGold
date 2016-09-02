package one.lindegaard.MobHunting;

public class MobPlugins {

	public enum MobPluginNames {
		Minecraft("Minecraft",0), MythicMobs("MythicMobs",1), Citizens("Citizens",2), TARDISWeepingAngels(
				"TARDISWeepingAngels",3), CustomMobs("CustomMobs",4);

		private final String name;
		private final Integer id;

		private MobPluginNames(String s, Integer id) {
			name = s;
			this.id = id;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}

		public Integer getId() {
			return id;
		}
	}

	private MobPluginNames mobPlugin;
	private String mobType;
	private int max;

	public MobPlugins(MobPluginNames mobPlugin, String mobType, int max) {
		this.mobPlugin = mobPlugin;
		this.mobType = mobType;
		this.max = max;
	}

	public void set(MobPluginNames mobPlugin, String mobType, int max) {
		this.mobPlugin = mobPlugin;
		this.mobType = mobType;
		this.max = max;
	}

	public MobPlugins get() {
		return new MobPlugins(mobPlugin, mobType, max);
	}

	public String getMobType() {
		return mobType;
	}

	public int getMax() {
		return max;
	}

	public MobPluginNames getMobPlugin() {
		return mobPlugin;
	}

	//TODO: missing plugins must be added (?)
	public MobPluginNames valueOfXXX(String str) {
		switch (str) {
		case "MythicMobs":
			return MobPluginNames.MythicMobs;
		case "Citizens":
			return MobPluginNames.Citizens;
		default:
			return MobPluginNames.Minecraft;
		}
	}
	
}
