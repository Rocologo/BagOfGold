package au.com.mineauz.MobHunting;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;

public enum ExtendedMobType {
	// Minecraft 1.0.0
	// Giant is unsupported by in the original game and Giants can only be
	// spawnwed through plugins.
	Giant("GIANT", 100), EnderDragon("ENDER_DRAGON", 80),
	// Minecraft 1.7
	Slime("SLIME", 100), MagmaCube("MAGMA_CUBE", 100), Ghast("GHAST", 80), Blaze(
			"BLAZE", 80), Creeper("CREEPER", 100), Enderman("ENDERMAN", 100), Silverfish(
			"SILVERFISH", 100), Skeleton("SKELETON", 100), WitherSkeleton(
			"SKELETON", 80), Spider("SPIDER", 100), CaveSpider("CAVE_SPIDER",
			100), Witch("WITCH", 80), Wither("WITHER", 20), ZombiePigman(
			"PIG_ZOMBIE", 100), Zombie("ZOMBIE", 100), BonusMob("UNKNOWN", 20), IronGolem(
			"IRON_GOLEM", 100),
	// Passive Mobs
	Bat("BAT", 100), Chicken("CHICKEN", 100), Cow("COW", 100), Horse("HORSE",
			100), MushroomCow("MUSHROOM_COW", 100), Ocelot("OCELOT", 100), Pig(
			"PIG", 100), PassiveRabbit("RABBIT", 100), Sheep("SHEEP", 100), Snowman(
			"SNOWMAN", 100), Squid("SQUID", 100), Villager("VILLAGER", 100), Wolf(
			"WOLF", 100),
	// Minecraft 1.8 Entity's
	Endermite("ENDERMITE", 100), Guardian("GUARDIAN", 100), KillerRabbit(
			"RABBIT", 100), PvpPlayer("PLAYER", 100);

	private String mType;
	private int mMax;

	private ExtendedMobType(String type, int max) {
		mType = type;
		mMax = max;
	}

	public String getEntType() {
		return mType;
	}

	public int getMax() {
		return mMax;
	}

	@SuppressWarnings("rawtypes")
	public boolean matches(Entity ent) {
		// test if MC 1.8 classes exists
		try {
			@SuppressWarnings("unused")
			Class cls = Class.forName("org.bukkit.entity.Rabbit");
			if (this == KillerRabbit)
				return ent instanceof Rabbit
						&& (((Rabbit) ent).getRabbitType()) == Rabbit.Type.THE_KILLER_BUNNY;

		} catch (ClassNotFoundException e) {
			// not MC 1.8
		}
		if (this == WitherSkeleton)
			return ent instanceof Skeleton
					&& ((Skeleton) ent).getSkeletonType() == SkeletonType.WITHER;
		else if (this == Skeleton)
			return ent instanceof Skeleton
					&& ((Skeleton) ent).getSkeletonType() == SkeletonType.NORMAL;
		else if (this == BonusMob)
			return ent.hasMetadata("MH:hasBonus");
		else {
			return ent.getType().toString() == mType;
		}
	}

	public String getName() {
		return Messages.getString("mobs." + name() + ".name");
	}

	public static ExtendedMobType fromEntity(Entity entity) {
		for (ExtendedMobType type : values()) {
			if (type.matches(entity))
				return type;
		}
		MobHunting
				.debug("ERROR!!! - Unhandled Entity: %s(%s) Type:%s", entity
						.getName(), entity.getCustomName(), entity.getType()
						.toString());
		return null;
	}
}
