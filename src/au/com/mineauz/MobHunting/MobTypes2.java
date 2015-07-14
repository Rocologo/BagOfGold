package au.com.mineauz.MobHunting;

import java.util.HashMap;

import au.com.mineauz.MobHunting.MobPlugins.PluginNames;

// OBS!!! THIS CLASS IS NOT USED FOR NOW!
public class MobTypes2 {
	
	public MobTypes2(HashMap<String, MobPlugins> mobTypes) {
		super();
		this.mobTypes = mobTypes;
		addMobType(MobPlugins.PluginNames.Minecraft, "GIANT", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "SLIME", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "MAGMA_CUBE", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "GHAST", 80);
		addMobType(MobPlugins.PluginNames.Minecraft, "BLAZE", 80);
		addMobType(MobPlugins.PluginNames.Minecraft, "CREEPER", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "ENDERMAN", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "SILVERFISH", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "SKELETON", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "WITHERSKELETON", 80);
		addMobType(MobPlugins.PluginNames.Minecraft, "SPIDER", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "CAVE_SPIDER", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "WITCH", 80);
		addMobType(MobPlugins.PluginNames.Minecraft, "WITHER", 20);
		addMobType(MobPlugins.PluginNames.Minecraft, "PIG_ZOMBIE", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "ZOMBIE", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "UNKNOWN", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "IRON_GOLEM", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "ENDERMITE", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "GUARDIAN", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "RABBIT", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "PLAYER", 100);
		addMobType(MobPlugins.PluginNames.Minecraft, "ENDER_DRAGON", 100);
		
	}


	private HashMap<String,MobPlugins> mobTypes = new HashMap<String,MobPlugins>();

	public void addMobType(PluginNames mobPlugin, String type, int max){
		if (!mobTypes.containsKey(type)){
			mobTypes.put(type, new MobPlugins(mobPlugin,type,max));
		}
	}
	
	public String[] values(){
		//TODO:
		return null;
	}
	
}
