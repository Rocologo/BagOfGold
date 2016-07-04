package one.lindegaard.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.compatibility.MobStackerCompat;

public class StackedMobBonus implements IModifier {

	@Override
	public String getName() {
		return ChatColor.AQUA + Messages.getString("bonus.mobstacker.name");
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		if (MobStackerCompat.killHoleStackOnDeath(deadEntity) && MobStackerCompat.multiplyLoot()) {
			Messages.debug("StackedMobBonus: Pay reward for no %s mob", MobStackerCompat.getStackSize(deadEntity));
			return MobStackerCompat.getStackSize(deadEntity);
		} else {
			Messages.debug("StackedMobBonus: Pay reward for one mob");
			return 1;
		}
	}

	@Override
	public boolean doesApply(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		return MobStackerCompat.isStackedMob(deadEntity);
	}
}
