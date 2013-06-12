package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class ShoveBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.AQUA + "A Shove";
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo)
	{
		return MobHunting.config().bonusSendFalling;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo )
	{
		if(extraInfo.attacker != killer)
			return false;
		
		return deadEntity.getLastDamageCause().getCause() == DamageCause.FALL;
	}

}
