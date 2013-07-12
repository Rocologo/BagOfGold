package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class FriendleFireBonus implements IModifier
{
	@Override
	public String getName()
	{
		return ChatColor.DARK_GREEN + "Friendly Fire";
	}

	@Override
	public double getMultiplier( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		return MobHunting.config().bonusFriendlyFire;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo, EntityDamageByEntityEvent lastDamageCause )
	{
		if(lastDamageCause != null && lastDamageCause.getDamager() instanceof Creature || (lastDamageCause.getDamager() instanceof Projectile && (((Projectile)lastDamageCause.getDamager()).getShooter() instanceof Creature || ((Projectile)lastDamageCause.getDamager()).getShooter() instanceof Ghast)))
			return true;
		return false;
	}

}
