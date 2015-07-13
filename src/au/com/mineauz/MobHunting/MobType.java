package au.com.mineauz.MobHunting;

public class MobType {

	public enum MobPlugin {
		Minecraft("Minecraft"), MythicMobs("MythicMobs"), Citizens("Citizens");

		private final String name;

		private MobPlugin(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}
	}

	private MobPlugin mobPlugin;
	private String mobType;
	private int max;

	public MobType(MobPlugin mobPlugin, String mobType, int max) {
		this.mobPlugin = mobPlugin;
		this.mobType = mobType;
		this.max = max;
	}

	public void set(MobPlugin mobPlugin, String mobType, int max) {
		this.mobPlugin = mobPlugin;
		this.mobType = mobType;
		this.max = max;
	}

	public MobType get() {
		return new MobType(mobPlugin, mobType, max);
	}

	public String getMobType() {
		return mobType;
	}

	public int getMax() {
		return max;
	}

	public MobPlugin getMobPlugin() {
		return mobPlugin;
	}

	public MobPlugin valueOf(String str) {
		switch (str) {
		case "MythicMobs":
			return MobPlugin.MythicMobs;
		case "Citizens":
			return MobPlugin.Citizens;
		default:
			return MobPlugin.Minecraft;
		}
	}
}
