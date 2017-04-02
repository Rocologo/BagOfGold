package one.lindegaard.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class ProSniperBonus implements IModifier {

	@Override
	public String getName() {
		return ChatColor.GRAY + Messages.getString("bonus.prosniper.name");
	}

	@Override
	public double getMultiplier(Entity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		return MobHunting.getConfigManager().bonusFarShot;
	}

	@Override
	public boolean doesApply(Entity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		if (extraInfo.getWeapon().getType() == Material.BOW && !extraInfo.isMeleWeapenUsed()) {
			double dist = extraInfo.getAttackerPosition().distance(deadEntity.getLocation());
			if (dist >= 50)
				return true;
		}

		return false;
	}

}
