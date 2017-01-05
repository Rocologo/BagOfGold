package one.lindegaard.MobHunting.modifier;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatibility.ConquestiaMobsCompat;

public class ConquestiaBonus implements IModifier {

	@Override
	public String getName() {
		return Messages.getString("bonus.conquestiamobs.name");
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		Messages.debug("ConquestiaMob total multiplier = %s", Math.pow(
				MobHunting.getConfigManager().mulitiplierPerLevel, ConquestiaMobsCompat.getCqLevel(deadEntity)-1));
		return Math.pow(MobHunting.getConfigManager().mulitiplierPerLevel,
				ConquestiaMobsCompat.getCqLevel(deadEntity)-1);
	}

	@Override
	public boolean doesApply(LivingEntity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		Messages.debug("%s killed a ConquestiaMob %s level %s", killer.getName(), deadEntity.getType(),
				ConquestiaMobsCompat.getCqLevel(deadEntity));
		return deadEntity.hasMetadata(ConquestiaMobsCompat.MH_CONQUESTIAMOBS);
	}

}
