package au.com.mineauz.MobHunting.modifier;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.DamageInformation;
import au.com.mineauz.MobHunting.HuntData;

public interface IModifier
{
	public String getName();
	
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo);
	
	public boolean doesApply(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo);
}
