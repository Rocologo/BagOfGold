package au.com.mineauz.MobHunting;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;

public enum ExtendedMobType
{
	Slime(EntityType.SLIME, 1000),
	MagmaCube(EntityType.MAGMA_CUBE, 1000),
	Ghast(EntityType.GHAST, 800),
	Blaze(EntityType.BLAZE, 800),
	Creeper(EntityType.CREEPER, 1000),
	Enderman(EntityType.ENDERMAN, 1000),
	Silverfish(EntityType.SILVERFISH, 1000),
	Skeleton(EntityType.SKELETON, 1000),
	WitherSkeleton(EntityType.SKELETON, 800),
	Spider(EntityType.SPIDER, 1000),
	CaveSpider(EntityType.CAVE_SPIDER, 1000),
	Witch(EntityType.WITCH, 800),
	Wither(EntityType.WITHER, 200),
	Zombie(EntityType.ZOMBIE, 1000),
	ZombiePigman(EntityType.PIG_ZOMBIE, 1000),
	BonusMob(EntityType.UNKNOWN, 200);
	
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
		else if(this == BonusMob)
			return ent.hasMetadata("MH:hasBonus"); //$NON-NLS-1$
		else
			return ent.getType() == mType;
	}
	
	public String getName()
	{
		return Messages.getString("mobs." + name() + ".name");
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
