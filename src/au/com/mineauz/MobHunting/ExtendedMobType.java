package au.com.mineauz.MobHunting;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;

public enum ExtendedMobType
{
	// Giant is unsupported by in the original game and Giants can only be spawnwed through plugins.
	Giant(EntityType.GIANT, 100),
	// Minecraft 1.7
	Slime(EntityType.SLIME, 100),
	MagmaCube(EntityType.MAGMA_CUBE, 100),
	Ghast(EntityType.GHAST, 80),
	Blaze(EntityType.BLAZE, 80),
	Creeper(EntityType.CREEPER, 100),
	Enderman(EntityType.ENDERMAN, 100),
	Silverfish(EntityType.SILVERFISH, 100),
	Skeleton(EntityType.SKELETON, 100),
	WitherSkeleton(EntityType.SKELETON, 80),
	Spider(EntityType.SPIDER, 100),
	CaveSpider(EntityType.CAVE_SPIDER, 100),
	Witch(EntityType.WITCH, 80),
	Wither(EntityType.WITHER, 20),
	Zombie(EntityType.ZOMBIE, 100),
	ZombiePigman(EntityType.PIG_ZOMBIE, 100),
	BonusMob(EntityType.UNKNOWN, 20),
	// Minecraft 1.8 Entity's
	Endermite(EntityType.ENDERMITE,100),
	Guardian(EntityType.GUARDIAN,100),
	KillerRabbit(EntityType.RABBIT,100),
	PvpPlayer(EntityType.PLAYER,100);
	
	private EntityType mType;
	private int mMax;
	
	private ExtendedMobType(EntityType type, int max)
	{
		mType = type;
		mMax = max;
	}
	
	public EntityType getEntType()
	{
		return mType;
	}
	
	public int getMax()
	{
		return mMax;
	}
	
	public boolean matches(Entity ent)
	{
		if(this == WitherSkeleton)
			return ent instanceof Skeleton && ((Skeleton)ent).getSkeletonType() == SkeletonType.WITHER;
		else if(this == Skeleton)
			return ent instanceof Skeleton && ((Skeleton)ent).getSkeletonType() == SkeletonType.NORMAL;
		else if(this == KillerRabbit)
			//return ent instanceof Rabbit && (((Rabbit) ent).getRabbitType().equals(Rabbit.Type.THE_KILLER_BUNNY));
			return ent instanceof Rabbit && (((Rabbit) ent).getRabbitType())== Rabbit.Type.THE_KILLER_BUNNY;
		else if(this == BonusMob)
			return ent.hasMetadata("MH:hasBonus"); //$NON-NLS-1$
		else
			return ent.getType() == mType;
	}
	
	public String getName()
	{
		return Messages.getString("mobs." + name() + ".name"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static ExtendedMobType fromEntity(Entity entity)
	{
		for(ExtendedMobType type : values())
		{
			if(type.matches(entity))
				return type;
		}
		
		return null;
	}
}
