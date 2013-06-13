package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class SneakyBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.BLUE + "Sneaky!";
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo)
	{
		return MobHunting.config().bonusSneaky;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo )
	{
		if(!(deadEntity instanceof Creature))
			return false;
		
		if(extraInfo.mele || extraInfo.weapon.getType() == Material.POTION)
			return ((Creature)deadEntity).getTarget() == null;
		
		return false;
	}

}
