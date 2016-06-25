package one.lindegaard.MobHunting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import one.lindegaard.MobHunting.util.Misc;

import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Villager.Profession;

public enum ExtendedMobType {
	// PlayerName and texture can be found here:
	// http://heads.freshcoal.com/index.php

	// ******************************************************************
	// Minecraft 1.10
	// ******************************************************************
	// Polar Bear
	PolarBear("POLAR_BEAR", "UNKNOWN", ""),
	// Husk
	Husk("HUSK", "UNKNOWN", ""),
	// Stray
	Stray("STRAY", "UNKNOWN", ""),
	// ******************************************************************
	// Minecraft 1.9
	// *******************************************************************
	// Shulker
	Shulker("SHULKER", "MHF_Shulker",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjFkMzUzNGQyMWZlODQ5OTI2MmRlODdhZmZiZWFjNGQyNWZmZGUzNWM4YmRjYTA2OWU2MWUxNzg3ZmYyZiJ9fX0="),
	// ******************************************************************
	// Minecraft 1.8
	// *******************************************************************
	// Endermite
	Endermite("ENDERMITE", "MHF_Endermite",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZThjNmNiOGNlYWFkNWEyYWQ1Y2M5YTY3YmNlNmQ1YmRiZjVjYmI3ZTMxMjk1NWNjZjlmMTYyNTA5MzU1YjEifX19"),
	// Guardian
	Guardian("GUARDIAN", "MHF_Guardian",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTMyYzI0NTI0YzgyYWIzYjNlNTdjMjA1MmM1MzNmMTNkZDhjMGJlYjhiZGQwNjM2OWJiMjU1NGRhODZjMTIzIn19fQ=="),
	// KillerRabbit
	KillerRabbit("RABBIT", "MHF_KillerRabbit",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzA4OGNkNjE0MTg5NDU4ZDk5YzJmYmVkNTg0NDg4OTVlYTZiMjZmYzY2N2EyYzU5MTlmNzE0Y2VlNjQ4ZDExIn19fQ=="),
	// Player
	PvpPlayer("PLAYER", "MHF_Alex",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjNiMDk4OTY3MzQwZGFhYzUyOTI5M2MyNGUwNDkxMDUwOWIyMDhlN2I5NDU2M2MzZWYzMWRlYzdiMzc1MCJ9fX0="),
	// ******************************************************************
	// Minecraft 1.7
	// ******************************************************************
	// Slime
	Slime("SLIME", "MHF_Slime",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZhZDIwZmMyZDU3OWJlMjUwZDNkYjY1OWM4MzJkYTJiNDc4YTczYTY5OGI3ZWExMGQxOGM5MTYyZTRkOWI1In19fQ=="),
	// MagmaCube
	MagmaCube("MAGMA_CUBE", "MHF_LavaSlime",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzgyMTk0MTEyZDVmNzA3NTIwNThlZGQ3Mzc0NTdmOGFmNjFmMmFiMWE5MmI2MjRmYjFjOWUyMjkzODYxMjE0In19fQ=="),
	// Ghast
	Ghast("GHAST", "MHF_Ghast",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGI2YTcyMTM4ZDY5ZmJiZDJmZWEzZmEyNTFjYWJkODcxNTJlNGYxYzk3ZTVmOTg2YmY2ODU1NzFkYjNjYzAifX19"),
	// Blaze
	Blaze("BLAZE", "MHF_Blaze",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=="),
	// Creeper
	Creeper("CREEPER", "MHF_Creeper",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjk1ZWY4MzYzODlhZjk5MzE1OGFiYTI3ZmYzN2I2NTY3MTg1ZjdhNzIxY2E5MGZkZmViOTM3YTdjYjU3NDcifX19"),
	// Enderman
	Enderman("ENDERMAN", "MHF_Enderman",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0="),
	// Silverfish
	Silverfish("SILVERFISH", "MHF_Silverfish",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTBlOTE5ZDE5MTJlM2Q4YTNmNmZlNGRkNmVlYWYxMTk5NTIxNGY3ZmM4OWUzM2U0NDU2MDhiZjQ1M2QzZTAifX19"),
	// Skeleton
	Skeleton("SKELETON", "MHF_Skeleton",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmU1YmU2YTNjMDE1OWQyYzFmM2IxZTRlMWQ4Mzg0YjZmN2ViYWM5OTNkNThiMTBiOWY4OTg5Yzc4YTIzMiJ9fX0="),
	// WitherSkeleton
	WitherSkeleton("WITHERSKELETON", "MHF_WSkeleton",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjMzYjQxZmE3OWNkNTNhMjMwZTJkYjk0Mjg2Mzg0MzE4M2E3MDQwNDUzM2JiYzAxZmFiNzQ0NzY5YmNiIn19fQ=="),
	// Spider
	Spider("SPIDER", "MHF_Spider",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Q1NDE1NDFkYWFmZjUwODk2Y2QyNThiZGJkZDRjZjgwYzNiYTgxNjczNTcyNjA3OGJmZTM5MzkyN2U1N2YxIn19fQ=="),
	// CaveSpider
	CaveSpider("CAVE_SPIDER", "MHF_CaveSpider",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDE2NDVkZmQ3N2QwOTkyMzEwN2IzNDk2ZTk0ZWViNWMzMDMyOWY5N2VmYzk2ZWQ3NmUyMjZlOTgyMjQifX19"),
	// Witch
	Witch("WITCH", "ScrafBrothers4",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWVjNThiNWVmZDJiYjcyODEzYTRlYWMwZDY2YTgyMTcyZmQ0NjY0YTE0MzNkYjUxOTU5MzRiNzY0YjM5NyJ9fX0="),
	// Wither
	Wither("WITHER", "MHF_Wither",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RmNzRlMzIzZWQ0MTQzNjk2NWY1YzU3ZGRmMjgxNWQ1MzMyZmU5OTllNjhmYmI5ZDZjZjVjOGJkNDEzOWYifX19"),
	// Zombie Pigman
	ZombiePigman("PIG_ZOMBIE", "MHF_PigZombie",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRlOWM2ZTk4NTgyZmZkOGZmOGZlYjMzMjJjZDE4NDljNDNmYjE2YjE1OGFiYjExY2E3YjQyZWRhNzc0M2ViIn19fQ=="),
	// Zombie
	Zombie("ZOMBIE", "MHF_Zombie",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmYzg1NGJiODRjZjRiNzY5NzI5Nzk3M2UwMmI3OWJjMTA2OTg0NjBiNTFhNjM5YzYwZTVlNDE3NzM0ZTExIn19fQ=="),
	// BonusMob
	BonusMob("UNKNOWN", "UNKNOWN", ""),
	// Golem
	IronGolem("IRON_GOLEM", "MHF_Golem",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19"),
	// Passive Mobs
	// Bat
	Bat("BAT", "UNKNOWN", ""),
	// Chicken
	Chicken("CHICKEN", "MHF_Chicken",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ=="),
	// Cow
	Cow("COW", "MHF_Cow",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWQ2YzZlZGE5NDJmN2Y1ZjcxYzMxNjFjNzMwNmY0YWVkMzA3ZDgyODk1ZjlkMmIwN2FiNDUyNTcxOGVkYzUifX19"),
	// Horse
	Horse("HORSE", "UNKNOWN", ""),
	// Mushroom
	MushroomCow("MUSHROOM_COW", "MHF_MushroomCow",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDBiYzYxYjk3NTdhN2I4M2UwM2NkMjUwN2EyMTU3OTEzYzJjZjAxNmU3YzA5NmE0ZDZjZjFmZTFiOGRiIn19fQ=="),
	// Ocelot
	Ocelot("OCELOT", "MHF_Ocelot",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTY1N2NkNWMyOTg5ZmY5NzU3MGZlYzRkZGNkYzY5MjZhNjhhMzM5MzI1MGMxYmUxZjBiMTE0YTFkYjEifX19"),
	// Pig
	Pig("PIG", "MHF_Pig",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0="),
	// Rabbit
	PassiveRabbit("RABBIT", "MH_Rabbit",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2VjMjQyZTY2N2FlZTQ0NDkyNDEzZWY0NjFiODEwY2FjMzU2Yjc0ZDg3MThlNWNlYzFmODkyYTZiNDNlNWUxIn19fQ=="),
	// Sheep
	Sheep("SHEEP", "MHF_Sheep",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19"),
	// Snowman
	Snowman("SNOWMAN", "", ""),
	// Squid
	Squid("SQUID", "MHF_Squid",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ=="),
	// Villager
	Villager("VILLAGER", "MHF_Villager",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19"),
	// Wolf
	Wolf("WOLF", "MHF_Wolf",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjc2OGNiZDUyMWNiMTg1YjI3NjcyY2U0MzBmYzQ4NTBkNzU5NWM2ZTg3NzhlMDcyNzFjOTU3OWVkMWY1YWZiNSJ9fX0="),

	// Minecraft 1.0.0
	// Giant is unsupported by in the original game and Giants can only be
	// spawnwed through plugins.
	// Giant
	Giant("GIANT", "MHF_Giant",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTViNDczNGY4MTY5ZjE5NDU4NjJjNzU1YjM5NWE4YTIxMmIzMzhmYmY1MDc4MDMyYzFkNjNhYTlhZGFlZiJ9fX0="),
	// EnderDragon
	EnderDragon("ENDER_DRAGON", "MHF_EnderDragon",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBmMWI3ZGQzZWIxZGNiZjJmZTZkZTk2ZWVjYjM0MjZkNGM2NTlhZDljZDM0MWRiM2M2MzljMmQ1ZGY2NWE2In19fQ==");

	private String mType;
	private String mPlayerName;
	@SuppressWarnings({ "unused" })
	private String mTexture;

	private ExtendedMobType(String type, String playerName, String texture) {
		mType = type;
		mPlayerName = playerName;
		mTexture = texture;
	}

	public String getEntType() {
		return mType;
	}

	public int getMax() {
		switch (this) {
		case Giant:
			return MobHunting.getConfigManager().giantLevel1;
		case EnderDragon:
			return MobHunting.getConfigManager().enderdragonLevel1;
		case Slime:
			return MobHunting.getConfigManager().slimeLevel1;
		case MagmaCube:
			return MobHunting.getConfigManager().magmaCubeLevel1;
		case Ghast:
			return MobHunting.getConfigManager().ghastLevel1;
		case Blaze:
			return MobHunting.getConfigManager().blazeLevel1;
		case Creeper:
			return MobHunting.getConfigManager().creeperLevel1;
		case Enderman:
			return MobHunting.getConfigManager().endermanLevel1;
		case Silverfish:
			return MobHunting.getConfigManager().silverfishLevel1;
		case Skeleton:
			return MobHunting.getConfigManager().skeletonLevel1;
		case WitherSkeleton:
			return MobHunting.getConfigManager().witherSkeletonLevel1;
		case Spider:
			return MobHunting.getConfigManager().spiderLevel1;
		case CaveSpider:
			return MobHunting.getConfigManager().caveSpiderLevel1;
		case Witch:
			return MobHunting.getConfigManager().witchLevel1;
		case Wither:
			return MobHunting.getConfigManager().witherLevel1;
		case ZombiePigman:
			return MobHunting.getConfigManager().zombiePigmanLevel1;
		case Zombie:
			return MobHunting.getConfigManager().zombieLevel1;
		case BonusMob:
			return MobHunting.getConfigManager().bonusMobLevel1;
		case IronGolem:
			return MobHunting.getConfigManager().ironGolemLevel1;
		case Bat:
			return MobHunting.getConfigManager().batLevel1;
		case Chicken:
			return MobHunting.getConfigManager().chickenLevel1;
		case Cow:
			return MobHunting.getConfigManager().cowLevel1;
		case Horse:
			return MobHunting.getConfigManager().horseLevel1;
		case MushroomCow:
			return MobHunting.getConfigManager().mushroomCowLevel1;
		case Ocelot:
			return MobHunting.getConfigManager().ocelotLevel1;
		case Pig:
			return MobHunting.getConfigManager().pigLevel1;
		case PassiveRabbit:
			return MobHunting.getConfigManager().rabbitLevel1;
		case Sheep:
			return MobHunting.getConfigManager().sheepLevel1;
		case Snowman:
			return MobHunting.getConfigManager().snowmanLevel1;
		case Squid:
			return MobHunting.getConfigManager().squidLevel1;
		case Villager:
			return MobHunting.getConfigManager().villagerLevel1;
		case Wolf:
			return MobHunting.getConfigManager().wolfLevel1;
		case Endermite:
			return MobHunting.getConfigManager().endermiteLevel1;
		case Guardian:
			return MobHunting.getConfigManager().guardianLevel1;
		case KillerRabbit:
			return MobHunting.getConfigManager().killerRabbitLevel1;
		case PvpPlayer:
			return MobHunting.getConfigManager().pvpPlayerLevel1;
		case Shulker:
			return MobHunting.getConfigManager().shulkerLevel1;
		case PolarBear:
			return MobHunting.getConfigManager().polarBearLevel1;
		case Stray:
			return MobHunting.getConfigManager().strayLevel1;
		case Husk:
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

	public static ExtendedMobType getExtendedMobType(String name) {
		for (ExtendedMobType type : values())
			if (type.getName().equals(name))
				return type;
		return null;
	}

	public ItemStack getHead() {
		ItemStack skull;
		SkullMeta skullMeta;
		switch (this) {
		case Skeleton:
			skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 0);
			break;
		case WitherSkeleton:
			skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
			break;
		case Zombie:
			skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 2);
			break;
		case PvpPlayer:
			skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			break;
		case Creeper:
			skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 4);
			break;
		default:
			skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			//skull = new ItemStack(Material.SKULL);
		}
		//GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		
		skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(mPlayerName);
		skull.setItemMeta(skullMeta);
		return skull;
	}
}
