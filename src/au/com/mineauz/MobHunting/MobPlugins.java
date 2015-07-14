package au.com.mineauz.MobHunting;

public class MobPlugins {

	public enum PluginNames {
		Minecraft("Minecraft"), MythicMobs("MythicMobs"), Citizens("Citizens");

		private final String name;

		private PluginNames(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		public String toString() {
			return name;
		}
	}

	private PluginNames mobPlugin;
	private String mobType;
	private int max;

	public MobPlugins(PluginNames mobPlugin, String mobType, int max) {
		this.mobPlugin = mobPlugin;
		this.mobType = mobType;
		this.max = max;
	}

	public void set(PluginNames mobPlugin, String mobType, int max) {
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

	public PluginNames getMobPlugin() {
		return mobPlugin;
	}

	public PluginNames valueOf(String str) {
		switch (str) {
		case "MythicMobs":
			return PluginNames.MythicMobs;
		case "Citizens":
			return PluginNames.Citizens;
		default:
			return PluginNames.Minecraft;
		}
	}
}
