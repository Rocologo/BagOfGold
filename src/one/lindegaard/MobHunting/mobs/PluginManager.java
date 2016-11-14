package one.lindegaard.MobHunting.mobs;

import java.util.HashMap;

public class PluginManager {
	
	HashMap <Integer, String> pluginManager = new HashMap<Integer, String>();

	private MobPlugin mobPlugin;
	private String mobType;
	private int max;

	public PluginManager(MobPlugin mobPlugin, String mobType, int max) {
		this.mobPlugin = mobPlugin;
		this.mobType = mobType;
		this.max = max;
	}

	public void set(MobPlugin mobPlugin, String mobType, int max) {
		this.mobPlugin = mobPlugin;
		this.mobType = mobType;
		this.max = max;
	}

	public PluginManager get() {
		return new PluginManager(mobPlugin, mobType, max);
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

	public static MobPlugin valueOf(int i) {
		return MobPlugin.values()[i];
	}

}
