package au.com.mineauz.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;
import au.com.mineauz.MobHunting.MobHunting;

public class GrindingPenalty implements IModifier
{

	@Override
	public String getName()
	{
		return ChatColor.RED + "Grinding Penalty"; 
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo)
	{
		return data.getDampnerMultiplier();
	}

	@Override
	public boolean doesApply( LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo )
	{
		if(MobHunting.config().penaltyGrindingEnable)
			return data.getDampnerMultiplier() < 1;
		return false;
	}

}
