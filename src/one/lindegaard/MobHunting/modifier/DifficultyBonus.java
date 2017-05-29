package one.lindegaard.MobHunting.modifier;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatibility.ExtraHardModeCompat;

public class DifficultyBonus implements IModifier {

	@Override
	public String getName() {
		return Messages.getString("bonus.difficulty.name");
	}

	@Override
	public double getMultiplier(Entity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {

		Difficulty worldDifficulty = killer.getWorld().getDifficulty();
		Iterator<Entry<String, String>> difficulties = MobHunting.getConfigManager().difficultyMultiplier.entrySet()
				.iterator();
		String multiplierStr = "1";
		while (difficulties.hasNext()) {
			Entry<String, String> difficulty = difficulties.next();
			if (!difficulty.getKey().equalsIgnoreCase("difficulty")
					&& !difficulty.getKey().equalsIgnoreCase("difficulty.multiplier")) {
				try {
					if (ExtraHardModeCompat.isEnabledForWorld(killer.getWorld())) {
						if (difficulty.getKey().equalsIgnoreCase("difficulty.multiplier.extrahard")) {
							multiplierStr = difficulty.getValue();
							break;
						}
					} else if ((difficulty.getKey()
							.equalsIgnoreCase("difficulty.multiplier." + worldDifficulty.name().toLowerCase()))) {
						multiplierStr = difficulty.getValue();
						break;
					}
				} catch (Exception e) {
					ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
					console.sendMessage(ChatColor.RED
							+ "[MobHunting] The difficulty section in your config.yml could not be read. Check the section for errors. Make sure that you have surrounded the number with a '. Ex.: '1.1'");
				}
			}
		}
		if (multiplierStr != null && Double.valueOf(multiplierStr) != 0)
			return Double.valueOf(multiplierStr);
		else
			return 1;
	}

	@Override
	public boolean doesApply(Entity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		Difficulty worldDifficulty = killer.getWorld().getDifficulty();
		Iterator<Entry<String, String>> difficulties = MobHunting.getConfigManager().difficultyMultiplier.entrySet()
				.iterator();
		String multiplierStr = "1";
		while (difficulties.hasNext()) {
			Entry<String, String> difficulty = difficulties.next();
			if (!difficulty.getKey().equalsIgnoreCase("difficulty")
					&& !difficulty.getKey().equalsIgnoreCase("difficulty.multiplier")) {
				try {
					if (ExtraHardModeCompat.isEnabledForWorld(killer.getWorld())) {
						if (difficulty.getKey().equalsIgnoreCase("difficulty.multiplier.extrahard")) {
							multiplierStr = difficulty.getValue();
							break;
						}
					} else if ((difficulty.getKey()
							.equalsIgnoreCase("difficulty.multiplier." + worldDifficulty.name().toLowerCase()))) {
						multiplierStr = difficulty.getValue();
						break;
					}
				} catch (Exception e) {
					if (MobHunting.getConfigManager().killDebug)
						e.printStackTrace();
				}
			}
		}
		if (multiplierStr != null && Double.valueOf(multiplierStr) != 0)
			return true;
		else
			return false;
	}

}
