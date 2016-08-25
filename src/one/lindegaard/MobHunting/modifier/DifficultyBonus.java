package one.lindegaard.MobHunting.modifier;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Difficulty;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class DifficultyBonus implements IModifier {

	@Override
	public String getName() {
		return Messages.getString("bonus.difficulty.name");
	}

	@Override
	public double getMultiplier(LivingEntity deadEntity, Player killer,
			HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		Difficulty worldDifficulty = killer.getWorld().getDifficulty();
		Iterator<Entry<String, String>> difficulties = MobHunting.getConfigManager().difficultyMultiplier
				.entrySet().iterator();
		String multiplierStr = "1";
		while (difficulties.hasNext()) {
			Entry<String, String> difficulty = difficulties.next();
			if (!difficulty.getKey().equalsIgnoreCase("difficulty")
					&& !difficulty.getKey().equalsIgnoreCase(
							"difficulty.multiplier")
							&& (difficulty.getKey().equals("difficulty.multiplier."
									+ worldDifficulty.name().toLowerCase()))) {
				Messages.debug("WorldDifficulty=%s, multiplier=%s", worldDifficulty, difficulty.getValue() );
				multiplierStr = difficulty.getValue();
				break;
			}
		}
		if (multiplierStr != null && Double.valueOf(multiplierStr) != 0)
			return Double.valueOf(multiplierStr);
		else
			return 1;
	}

	@Override
	public boolean doesApply(LivingEntity deadEntity, Player killer,
			HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		Difficulty worldDifficulty = killer.getWorld().getDifficulty();
		Iterator<Entry<String, String>> difficulties = MobHunting.getConfigManager().difficultyMultiplier
				.entrySet().iterator();
		String multiplierStr = "1";
		while (difficulties.hasNext()) {
			Entry<String, String> difficulty = difficulties.next();
			if (!difficulty.getKey().equalsIgnoreCase("difficulty")
					&& !difficulty.getKey().equalsIgnoreCase(
							"difficulty.multiplier")
					&& (difficulty.getKey().equals("difficulty.multiplier."
							+ worldDifficulty.name().toLowerCase()))) {
				multiplierStr = difficulty.getValue();
				Messages.debug("DifficultyMultiplier: %s=%s",difficulty.getKey(),multiplierStr);
				break;
			}
		}
		if (multiplierStr != null && Double.valueOf(multiplierStr) != 0)
			return true;
		else
			return false;
	}

}
