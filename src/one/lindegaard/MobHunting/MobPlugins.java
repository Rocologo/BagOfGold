package one.lindegaard.MobHunting;

public class MobPlugins {

	public enum MobPluginNames {
		Minecraft("Minecraft"), MythicMobs("MythicMobs"), Citizens("Citizens"), TARDISWeepingAngels("TARDISWeepingAngels");

		private final String name;

		private MobPluginNames(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
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

	public MobPluginNames valueOf(String str) {
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
