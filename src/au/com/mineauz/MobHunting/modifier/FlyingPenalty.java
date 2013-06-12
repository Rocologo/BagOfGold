package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class FlyingPenalty implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.RED + "Flying Penalty";
	}

	@Override
	public double getMultiplier( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo )
	{
		return MobHunting.config().penaltyFlying;
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo )
	{
		return extraInfo.wasFlying;
	}

}
