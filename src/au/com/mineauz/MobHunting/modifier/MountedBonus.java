package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class MountedBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.GOLD + "Mounted";
	}

	@Override
	public double getMultiplier( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo )
	{
		return MobHunting.config().bonusMounted;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo )
	{
		if(killer.isInsideVehicle() && killer.getVehicle() instanceof Horse)
			return true;
		
		return false;
	}

}
