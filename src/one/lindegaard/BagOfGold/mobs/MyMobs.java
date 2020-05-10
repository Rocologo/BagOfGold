package one.lindegaard.BagOfGold.mobs;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.mobs.MobType;

public class MyMobs {

	MobType mob;
	
	public MyMobs(MobType mob) {
		this.mob=mob;
	}
	
	public String getFriendlyName() {
		return BagOfGold.getInstance().getMessages().getString("mobs." + mob.name() + ".name");
	}
	
	public String getTexture(String displayname) {
		for (MobType mob : MobType.values()) {
			if (mob.getDisplayName().equalsIgnoreCase(displayname)
					|| getFriendlyName().equalsIgnoreCase(displayname)) {
				return String.valueOf(mob.getTextureValue());
			}
		}
		return "";
	}

	public String getSignature(String displayname) {
		for (MobType mob : MobType.values()) {
			if (mob.getDisplayName().equalsIgnoreCase(displayname)
					|| getFriendlyName().equalsIgnoreCase(displayname)) {
				return String.valueOf(mob.getTextureSignature());
			}
		}
		return "";

	}

	public MobType getMinecraftMobType(String name) {
		String name1 = name.replace(" ", "_");
		for (MobType type : MobType.values())
			if (getFriendlyName().replace(" ", "_").equalsIgnoreCase(name1)
					|| type.getDisplayName().replace(" ", "_").equalsIgnoreCase(name1)
					|| type.name().equalsIgnoreCase(name1))
				return type;
		return null;
	}

}
