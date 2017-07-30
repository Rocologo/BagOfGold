package io.chazza.advancementapi;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.achievements.Achievement;
import one.lindegaard.MobHunting.achievements.ProgressAchievement;

public class AdvancementManager {

	private static ArrayList<AdvancementAPI> knowAdvancements = new ArrayList<AdvancementAPI>();

	public AdvancementManager() {
	}

	public void getAdvancementsFromAchivements() {

		ArrayList<Achievement> achivements = new ArrayList<>();
		achivements.addAll(MobHunting.getAchievementManager().getAllAchievements());

		AdvancementAPI huntbegins;
		Achievement ach = MobHunting.getAchievementManager().getAchievement("huntbegins");
		huntbegins = AdvancementAPI.builder(new NamespacedKey(MobHunting.getInstance(), "hunter/" + ach.getID()))
				.title(ach.getName()).description(ach.getDescription()).icon("minecraft:bow")
				.trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "default")
						.condition(Condition.builder("elytra", new ItemStack(Material.STONE, 1))))
				.hidden(false).toast(false).background(Background.STONE.toString()).frame(FrameType.CHALLENGE).build();
		huntbegins.add();
		knowAdvancements.add(huntbegins);
		achivements.remove(ach);

		for (Achievement achievement : achivements) {
			if (!(achievement instanceof ProgressAchievement)) {
				AdvancementAPI child = AdvancementAPI
						.builder(new NamespacedKey(MobHunting.getInstance(), "hunter/" + achievement.getID()))
						.title(achievement.getName()).description(achievement.getDescription()).icon("minecraft:stone")
						.trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "test")
								.condition(Condition.builder("elytra", achievement.getSymbol())))
						.hidden(false).toast(true).frame(FrameType.CHALLENGE).parent(huntbegins.getId().toString())
						.build();
				child.add();
				knowAdvancements.add(child);
			}
		}

		for (Achievement achievement : achivements) {
			if (achievement instanceof ProgressAchievement
					&& ((ProgressAchievement) achievement).inheritFrom() == null) {
				if (((ProgressAchievement) achievement).nextLevelId() != null) {
					AdvancementAPI child = AdvancementAPI
							.builder(new NamespacedKey(MobHunting.getInstance(), "hunter/" + achievement.getID()))
							.title(achievement.getName()).description(achievement.getDescription())
							.icon("minecraft:stone")
							.trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "test")
									.condition(Condition.builder("elytra", achievement.getSymbol())))
							.hidden(false).toast(true).frame(FrameType.TASK).parent(huntbegins.getId().toString())
							.build();
					child.add();
					knowAdvancements.add(child);
				} else {
					AdvancementAPI child = AdvancementAPI
							.builder(new NamespacedKey(MobHunting.getInstance(), "hunter/" + achievement.getID()))
							.title(achievement.getName()).description(achievement.getDescription())
							.icon("minecraft:stone")
							.trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "test")
									.condition(Condition.builder("elytra", achievement.getSymbol())))
							.hidden(false).toast(true).frame(FrameType.GOAL).parent(huntbegins.getId().toString())
							.build();
					child.add();
					knowAdvancements.add(child);
				}

				if ((achievement instanceof ProgressAchievement)
						&& ((ProgressAchievement) achievement).nextLevelId() != null)
					addNext(MobHunting.getAchievementManager()
							.getAchievement(((ProgressAchievement) achievement).nextLevelId()));
			}
		}
	}

	private void addNext(Achievement achievement) {
		if (achievement instanceof ProgressAchievement) {
			if (((ProgressAchievement) achievement).nextLevelId() != null) {
				AdvancementAPI child = AdvancementAPI
						.builder(new NamespacedKey(MobHunting.getInstance(), "hunter/" + achievement.getID()))
						.title(achievement.getName()).description(achievement.getDescription()).icon("minecraft:stone")
						.trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "test")
								.condition(Condition.builder("elytra", achievement.getSymbol())))
						.hidden(false).toast(true).frame(FrameType.TASK)
						.parent("mobhunting:hunter/" + ((ProgressAchievement) achievement).inheritFrom()).build();
				child.add();
				knowAdvancements.add(child);

				addNext(MobHunting.getAchievementManager()
						.getAchievement(((ProgressAchievement) achievement).nextLevelId()));

			} else {
				AdvancementAPI child = AdvancementAPI
						.builder(new NamespacedKey(MobHunting.getInstance(), "hunter/" + achievement.getID()))
						.title(achievement.getName()).description(achievement.getDescription()).icon("minecraft:stone")
						.trigger(Trigger.builder(Trigger.TriggerType.IMPOSSIBLE, "test")
								.condition(Condition.builder("elytra", achievement.getSymbol())))
						.hidden(false).toast(true).background(Background.STONE.toString()) // .background("minecraft:textures/gui/advancements/backgrounds/stone.png")
						.frame(FrameType.GOAL)
						.parent("mobhunting:hunter/" + ((ProgressAchievement) achievement).inheritFrom()).build();
				child.add();
				knowAdvancements.add(child);
			}
		}
	}

	/**
	 * updatePlayerAdvancements is run after Achievements is loaded from disk,
	 * when the player joins the server
	 * 
	 * @param player
	 */
	public void updatePlayerAdvancements(Player player) {
		for (AdvancementAPI api : knowAdvancements) {
			Achievement achievement = MobHunting.getAchievementManager()
					.getAchievement(api.getId().getKey().split("/")[1]);
			if (MobHunting.getAchievementManager().hasAchievement(achievement, player)) {
				Messages.debug("AdvancementManager: granting %s to player:%s", achievement.getID(), player.getName());
				api.grant(player);
			}
		}
	}

	/**
	 * updatePlayerAdvancements is run after Achievements is loaded from disk,
	 * when the player joins the server
	 * 
	 * @param player
	 */
	public void grantAdvancement(Player player, Achievement achievement) {
		for (AdvancementAPI api : knowAdvancements) {
			if (api.getId().getKey().split("/")[1].equalsIgnoreCase(achievement.getID())) {
				api.grant(player);
				break;
			}
		}
	}

}
