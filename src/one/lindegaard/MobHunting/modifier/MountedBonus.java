package one.lindegaard.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class MountedBonus implements IModifier {

	@Override
	public String getName() {
		return ChatColor.GOLD + Messages.getString("bonus.mounted.name"); //$NON-NLS-1$
	}

	@Override
	public double getMultiplier(Entity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		return MobHunting.getConfigManager().bonusMounted;
	}

	@Override
	public boolean doesApply(Entity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		if (killer != null && killer.isInsideVehicle() && killer.getVehicle() instanceof Creature)
			return true;

		return false;
	}

}
