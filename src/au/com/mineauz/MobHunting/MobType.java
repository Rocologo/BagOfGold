package au.com.mineauz.MobHunting;

import au.com.mineauz.MobHunting.MobType.MobPlugin;

public class MobType {

	public enum MobPlugin {
		Minecraft, MythicMobs, Citizens2
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
	
	public MobType get(){
		return new MobType(mobPlugin,mobType,max);
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
}
