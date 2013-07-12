package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class ReturnToSenderBonus implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.GOLD + "Return To Sender!";
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause)
	{
		return MobHunting.config().bonusReturnToSender;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		if(!(deadEntity instanceof Ghast))
			return false;
		if(!(deadEntity.getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return false;

		return (((EntityDamageByEntityEvent)deadEntity.getLastDamageCause()).getDamager() instanceof LargeFireball);
	}

}
