package au.com.mineauz.MobHunting;

import java.util.HashMap;

import au.com.mineauz.MobHunting.MobType.MobPlugin;

public class MobTypes {
	
	public MobTypes(HashMap<String, MobType> mobTypes) {
		super();
		this.mobTypes = mobTypes;
		addMobType(MobType.MobPlugin.Minecraft, "GIANT", 100);
		addMobType(MobType.MobPlugin.Minecraft, "SLIME", 100);
		addMobType(MobType.MobPlugin.Minecraft, "MAGMA_CUBE", 100);
		addMobType(MobType.MobPlugin.Minecraft, "GHAST", 80);
		addMobType(MobType.MobPlugin.Minecraft, "BLAZE", 80);
		addMobType(MobType.MobPlugin.Minecraft, "CREEPER", 100);
		addMobType(MobType.MobPlugin.Minecraft, "ENDERMAN", 100);
		addMobType(MobType.MobPlugin.Minecraft, "SILVERFISH", 100);
		addMobType(MobType.MobPlugin.Minecraft, "SKELETON", 100);
		addMobType(MobType.MobPlugin.Minecraft, "WITHERSKELETON", 80);
		addMobType(MobType.MobPlugin.Minecraft, "SPIDER", 100);
		addMobType(MobType.MobPlugin.Minecraft, "CAVE_SPIDER", 100);
		addMobType(MobType.MobPlugin.Minecraft, "WITCH", 80);
		addMobType(MobType.MobPlugin.Minecraft, "WITHER", 20);
		addMobType(MobType.MobPlugin.Minecraft, "PIG_ZOMBIE", 100);
		addMobType(MobType.MobPlugin.Minecraft, "ZOMBIE", 100);
		addMobType(MobType.MobPlugin.Minecraft, "UNKNOWN", 100);
		addMobType(MobType.MobPlugin.Minecraft, "IRON_GOLEM", 100);
		addMobType(MobType.MobPlugin.Minecraft, "ENDERMITE", 100);
		addMobType(MobType.MobPlugin.Minecraft, "GUARDIAN", 100);
		addMobType(MobType.MobPlugin.Minecraft, "RABBIT", 100);
		addMobType(MobType.MobPlugin.Minecraft, "PLAYER", 100);
		
	}


	private HashMap<String,MobType> mobTypes = new HashMap<String,MobType>();

	public void addMobType(MobPlugin mobPlugin, String type, int max){
		if (!mobTypes.containsKey(type)){
			mobTypes.put(type, new MobType(mobPlugin,type,max));
		}
	}
	
	public String[] values(){
		//TODO:
		return null;
	}
	
}
