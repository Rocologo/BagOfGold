package one.lindegaard.MobHunting.modifier;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatibility.InfernalMobsCompat;

public class InfernalMobBonus implements IModifier {

	@Override
	public String getName() {
		return ChatColor.AQUA + Messages.getString("bonus.infernalmob.name");
	}

	@SuppressWarnings("unchecked")
	@Override
	public double getMultiplier(Entity entity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		double mul = 1;
		if (InfernalMobsCompat.isSupported()) {
			if (entity.hasMetadata(InfernalMobsCompat.MH_INFERNALMOBS)) {
				ArrayList<String> list = new ArrayList<>();
				if (entity.getMetadata(InfernalMobsCompat.MH_INFERNALMOBS).get(0).value() instanceof ArrayList<?>)
					list = (ArrayList<String>) entity.getMetadata(InfernalMobsCompat.MH_INFERNALMOBS).get(0).value();
				mul = Math.pow(MobHunting.getConfigManager().multiplierPerInfernalLevel, list.size());
			}
		}
		return mul;
	}

	@Override
	public boolean doesApply(Entity entity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		return InfernalMobsCompat.isInfernalMob(entity);
	}
}
