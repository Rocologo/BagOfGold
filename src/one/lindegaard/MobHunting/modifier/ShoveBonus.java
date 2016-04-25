package one.lindegaard.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class ShoveBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.AQUA + Messages.getString("bonus.ashove.name"); //$NON-NLS-1$
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause)
	{
		return MobHunting.getConfigManager().bonusSendFalling;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		if(extraInfo.attacker != killer)
			return false;
		
		if(deadEntity.getLastDamageCause() != null)
			return deadEntity.getLastDamageCause().getCause() == DamageCause.FALL;
		return false;
	}

}
