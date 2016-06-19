package one.lindegaard.MobHunting;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;

import one.lindegaard.MobHunting.util.Misc;

import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;

public enum ExtendedMobType {
	// Minecraft 1.10
	PolarBear("POLAR_BEAR"), Husk("HUSK"), Stray("STRAY"),
	// Minecraft 1.9 Entity
	Shulker("SHULKER"),
	// Minecraft 1.8 Entity's
	Endermite("ENDERMITE"), Guardian("GUARDIAN"), KillerRabbit("RABBIT"), PvpPlayer("PLAYER"),
	// Minecraft 1.7
	Slime("SLIME"), MagmaCube("MAGMA_CUBE"), Ghast("GHAST"), Blaze("BLAZE"), Creeper("CREEPER"), Enderman(
			"ENDERMAN"), Silverfish("SILVERFISH"), Skeleton("SKELETON"), WitherSkeleton("SKELETON"), Spider(
					"SPIDER"), CaveSpider("CAVE_SPIDER"), Witch("WITCH"), Wither("WITHER"), ZombiePigman(
							"PIG_ZOMBIE"), Zombie("ZOMBIE"), BonusMob("UNKNOWN"), IronGolem("IRON_GOLEM"),
	// Passive Mobs
	Bat("BAT"), Chicken("CHICKEN"), Cow("COW"), Horse("HORSE"), MushroomCow("MUSHROOM_COW"), Ocelot("OCELOT"), Pig(
			"PIG"), PassiveRabbit(
					"RABBIT"), Sheep("SHEEP"), Snowman("SNOWMAN"), Squid("SQUID"), Villager("VILLAGER"), Wolf("WOLF"),

	// Minecraft 1.0.0
	// Giant is unsupported by in the original game and Giants can only be
	// spawnwed through plugins.
	Giant("GIANT"), EnderDragon("ENDER_DRAGON");

	private String mType;

	private ExtendedMobType(String type) {
		mType = type;
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
		case "POLAR_BEAR":
			return MobHunting.getConfigManager().polarBearLevel1;
		case "STRAY":
			return MobHunting.getConfigManager().strayLevel1;
		case "HUSK":
			return MobHunting.getConfigManager().huskLevel1;
		default:
			Bukkit.getLogger().warning("[MobHunting] WARNING: Missing type in ExtendedMobType:" + mType);
		}
		return 100;
	}

	public boolean matches(Entity ent) {
		if (Misc.isMC110OrNewer())
			if (this == PolarBear)
				return ent instanceof org.bukkit.entity.PolarBear;
			else if (this == Stray)
				return ent instanceof org.bukkit.entity.Skeleton
						&& (((Skeleton) ent).getSkeletonType() == SkeletonType.STRAY);
			else if (this == Husk)
				return ent instanceof org.bukkit.entity.Zombie
						&& ((Zombie) ent).getVillagerProfession() == Profession.HUSK;

		if (Misc.isMC19OrNewer())
			if (this == Shulker)
				return ent instanceof org.bukkit.entity.Shulker;

		if (Misc.isMC18OrNewer())
			if (this == KillerRabbit)
				return ent instanceof Rabbit && (((Rabbit) ent).getRabbitType()) == Rabbit.Type.THE_KILLER_BUNNY;
			else if (this == PassiveRabbit)
				return ent instanceof Rabbit && (((Rabbit) ent).getRabbitType()) != Rabbit.Type.THE_KILLER_BUNNY;

		// MC 1.7.10 and older entities
		if (this == WitherSkeleton)
			return ent instanceof Skeleton && ((Skeleton) ent).getSkeletonType() == SkeletonType.WITHER;
		else if (this == Skeleton)
			return ent instanceof Skeleton && ((Skeleton) ent).getSkeletonType() == SkeletonType.NORMAL;
		else if (this == BonusMob)
			return ent.hasMetadata("MH:hasBonus");
		else
			return ent.getType().toString().equals(mType);
	}

	public String getName() {
		return Messages.getString("mobs." + name() + ".name");
	}

	public static ExtendedMobType getExtendedMobType(Entity entity) {
		for (ExtendedMobType type : values())
			if (type.matches(entity))
				return type;
		MobHunting.debug("ERROR!!! - Unhandled Entity: %s(%s) Type:%s", entity.getName(), entity.getCustomName(),
				entity.getType().toString());
		return null;
	}

}
