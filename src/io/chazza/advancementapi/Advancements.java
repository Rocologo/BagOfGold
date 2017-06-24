package io.chazza.advancementapi;

import java.util.Collection;

import org.bukkit.NamespacedKey;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.achievements.Achievement;
import one.lindegaard.MobHunting.achievements.ProgressAchievement;

public class Advancements {

	public static void updateAdvancements() {
		Collection<Achievement> achivements = MobHunting.getAchievementManager().getAllAchievements();

		int n = 0;
		for (Achievement achievement : achivements) {
			AdvancementBuilder ad = new AdvancementBuilder(new NamespacedKey(MobHunting.getInstance(), "root"));
			if (achievement instanceof ProgressAchievement
					&& ((ProgressAchievement) achievement).inheritFrom() != null) {
				ad.withTitle(achievement.getName()).withDescription(achievement.getDescription())
						.withIcon(NamespacedKey.minecraft("tripwire_hook"), 1)
						.withParent(((ProgressAchievement) achievement).inheritFrom())
						.withBackground(NamespacedKey.minecraft("textures/blocks/planks_oak.png"))
						.setAnnounceToChat(false).save();
			} else {
				ad.withTitle(achievement.getName()).withDescription(achievement.getDescription())
						.withIcon(NamespacedKey.minecraft("tripwire_hook"), 1)
						.withBackground(NamespacedKey.minecraft("textures/blocks/planks_oak.png"))
						.setAnnounceToChat(false).save();
				// Messages.debug("Advancement:%s", achievement.getID());
			}
			n++;
			if (n > 4)
				break;
		}

	}
}
