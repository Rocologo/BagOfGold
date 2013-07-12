package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class ProSniperBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.GRAY + "Pro Sniper";
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause)
	{
		return MobHunting.config().bonusFarShot;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		if(extraInfo.weapon.getType() == Material.BOW && !extraInfo.mele)
		{
			double dist = extraInfo.attackerPosition.distance(deadEntity.getLocation());
			if(dist >= 50)
				return true;
		}
		
		return false;
	}

}
