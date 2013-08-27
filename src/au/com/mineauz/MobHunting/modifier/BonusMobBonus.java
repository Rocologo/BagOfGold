package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

public class BonusMobBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.translateAlternateColorCodes('&', Messages.getString("bonus.bonusmob.name")); //$NON-NLS-1$
	}

	@Override
	public double getMultiplier( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		return MobHunting.config().bonusBonusMob;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		return deadEntity.hasMetadata("MH:hasBonus"); //$NON-NLS-1$
	}

}
