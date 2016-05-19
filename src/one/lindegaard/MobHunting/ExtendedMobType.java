package one.lindegaard.MobHunting;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
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
			"RABBIT", 100), PvpPlayer("PLAYER", 100),
			// Minecraft 1.9 Entity
			Shulker("SHULKER",100);

	private String mType;
	private int mMax;

	private ExtendedMobType(String type, int max) {
		mType = type;
		mMax = getMax();
	}

	public String getEntType() {
		return mType;
	}

	public int getMax() {
		switch (mType) {
		case "GIANT":
			return MobHunting.getConfigManager().giantLevel1;
		case "ENDER_DRAGON":
			return MobHunting.getConfigManager().enderdragonLevel1;
		case "SLIME":
			return MobHunting.getConfigManager().slimeLevel1;
		case "MAGMA_CUBE":
			return MobHunting.getConfigManager().magmaCubeLevel1;
		case "GHAST":
			return MobHunting.getConfigManager().ghastLevel1;
		case "BLAZE":
			return MobHunting.getConfigManager().blazeLevel1;
		case "CREEPER":
			return MobHunting.getConfigManager().creeperLevel1;
		case "ENDERMAN":
			return MobHunting.getConfigManager().endermanLevel1;
		case "SILVERFISH":
			return MobHunting.getConfigManager().silverfishLevel1;
		case "SKELETON":
			return MobHunting.getConfigManager().skeletonLevel1;
		case "WITHERSKELETON":
			return MobHunting.getConfigManager().witherSkeletonLevel1;
		case "SPIDER":
			return MobHunting.getConfigManager().spiderLevel1;
		case "CAVE_SPIDER":
			return MobHunting.getConfigManager().caveSpiderLevel1;
		case "WITCH":
			return MobHunting.getConfigManager().witchLevel1;
		case "WITHER":
			return MobHunting.getConfigManager().witherLevel1;
		case "PIG_ZOMBIE":
			return MobHunting.getConfigManager().zombiePigmanLevel1;
		case "ZOMBIE":
			return MobHunting.getConfigManager().zombieLevel1;
		case "UNKNOWN":
			return MobHunting.getConfigManager().bonusMobLevel1;
		case "IRON_GOLEM":
			return MobHunting.getConfigManager().ironGolemLevel1;
		case "BAT":
			return MobHunting.getConfigManager().batLevel1;
		case "CHICKEN":
			return MobHunting.getConfigManager().chickenLevel1;
		case "COW":
			return MobHunting.getConfigManager().cowLevel1;
		case "HORSE":
			return MobHunting.getConfigManager().horseLevel1;
		case "MUSHROOM_COW":
			return MobHunting.getConfigManager().mushroomCowLevel1;
		case "OCELOT":
			return MobHunting.getConfigManager().ocelotLevel1;
		case "PIG":
			return MobHunting.getConfigManager().pigLevel1;
		case "RABBIT":
			return MobHunting.getConfigManager().rabbitLevel1;
		case "SHEEP": 
			return MobHunting.getConfigManager().sheepLevel1;
		case "SNOWMAN":
			return MobHunting.getConfigManager().snowmanLevel1;
		case "SQUID":
			return MobHunting.getConfigManager().squidLevel1;
		case "VILLAGER":
			return MobHunting.getConfigManager().villagerLevel1;
		case "WOLF":
			return MobHunting.getConfigManager().wolfLevel1;
		case "ENDERMITE":
			return MobHunting.getConfigManager().endermiteLevel1;
		case "GUARDIAN":
			return MobHunting.getConfigManager().guardianLevel1;
		case "KILLERRABBIT":
			return MobHunting.getConfigManager().killerRabbitLevel1;
		case "PLAYER":
			return MobHunting.getConfigManager().pvpPlayerLevel1;
		case "SHULKER":
			return MobHunting.getConfigManager().shulkerLevel1;
		default:
			Bukkit.getLogger().warning("[MobHunting] WARNING: Missing type in ExtendedMobType:"+mType);
		}
		return 100;
	}

	@SuppressWarnings("rawtypes")
	public boolean matches(Entity ent) {
		// test if MC 1.9 classes exists
		try {
			@SuppressWarnings("unused")
			Class cls = Class.forName("org.bukkit.entity.Shulker");
			if (this == Shulker)
				return ent instanceof org.bukkit.entity.Shulker;
		} catch (ClassNotFoundException e) {
			// not MC 1.9
		}
		// test if MC 1.8 classes exists
		try {
			@SuppressWarnings("unused")
			Class cls = Class.forName("org.bukkit.entity.Rabbit");
			if (this == KillerRabbit)
				return ent instanceof Rabbit
						&& (((Rabbit) ent).getRabbitType()) == Rabbit.Type.THE_KILLER_BUNNY;
			else if (this == PassiveRabbit)
				return ent instanceof Rabbit
						&& (((Rabbit) ent).getRabbitType()) != Rabbit.Type.THE_KILLER_BUNNY;
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

	public static ExtendedMobType getExtendedMobType(Entity entity) {
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
