package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class BonusMobBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.translateAlternateColorCodes('&', "&9B&2O&3N&4U&5S &6M&eO&bB");
	}

	@Override
	public double getMultiplier( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		return MobHunting.config().bonusBonusMob;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		return deadEntity.hasMetadata("MH:hasBonus");
	}

}
