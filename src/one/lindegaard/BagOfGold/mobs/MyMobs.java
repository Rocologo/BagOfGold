package one.lindegaard.BagOfGold.mobs;

import one.lindegaard.BagOfGold.BagOfGold;

public class MyMobs {

	MinecraftMob mob;
	
	public MyMobs(MinecraftMob mob) {
		this.mob=mob;
	}
	
	public String getFriendlyName() {
		return BagOfGold.getInstance().getMessages().getString("mobs." + mob.name() + ".name");
	}
	
	public String getTexture(String displayname) {
		for (MinecraftMob mob : MinecraftMob.values()) {
			if (mob.getDisplayName().equalsIgnoreCase(displayname)
					|| getFriendlyName().equalsIgnoreCase(displayname)) {
				return String.valueOf(mob.getTextureValue());
			}
		}
		return "";
	}

	public String getSignature(String displayname) {
		for (MinecraftMob mob : MinecraftMob.values()) {
			if (mob.getDisplayName().equalsIgnoreCase(displayname)
					|| getFriendlyName().equalsIgnoreCase(displayname)) {
				return String.valueOf(mob.getTextureSignature());
			}
		}
		return "";

	}

	public MinecraftMob getMinecraftMobType(String name) {
		String name1 = name.replace(" ", "_");
		for (MinecraftMob type : MinecraftMob.values())
			if (getFriendlyName().replace(" ", "_").equalsIgnoreCase(name1)
					|| type.getDisplayName().replace(" ", "_").equalsIgnoreCase(name1)
					|| type.name().equalsIgnoreCase(name1))
				return type;
		return null;
	}

}
