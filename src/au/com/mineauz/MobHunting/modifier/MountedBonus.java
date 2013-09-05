package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class MountedBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.GOLD + Messages.getString("bonus.mounted.name"); //$NON-NLS-1$
	}

	@Override
	public double getMultiplier( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		return MobHunting.config().bonusMounted;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		if(killer.isInsideVehicle() && killer.getVehicle() instanceof Creature)
			return true;
		
		return false;
	}

}
