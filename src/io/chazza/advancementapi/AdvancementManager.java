package io.chazza.advancementapi;

import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.achievements.Achievement;
import one.lindegaard.MobHunting.achievements.ProgressAchievement;

public class AdvancementManager {

	private final String worldName = "world";
	ArrayList<AdvancementAPI> apiList = new ArrayList<AdvancementAPI>();

	public AdvancementManager() {

	}

	public void updateAdvancements() {

		AdvancementAPI parent = AdvancementAPI.builder(new NamespacedKey(MobHunting.getInstance(), "my/firststeps"))
				.title("First Steps").description("Starting").icon("minecraft:wood_sword")
				.trigger(Trigger.builder(Trigger.TriggerType.CONSUME_ITEM, "test")
						.condition(Condition.builder("potion", new ItemStack(Material.BREAD, 1))))
				.hidden(false).toast(false).background("minecraft:textures/gui/advancements/backgrounds/stone.png")
				.frame(FrameType.TASK).build();

		parent.save(worldName);
		apiList.add(parent);

		// you're able to use TextComponents @see
		// https://www.spigotmc.org/wiki/the-chat-component-api/#colors-and-formatting
		TextComponent textComponent = new TextComponent("Addiction!");
		textComponent.setBold(true);
		textComponent.setColor(ChatColor.GOLD);

		AdvancementAPI child = AdvancementAPI.builder(new NamespacedKey(MobHunting.getInstance(), "my/addiction"))
				.title(textComponent) // the TextComponent define above
				.description("Eat an Apple") // you can also use a normal String
												// instead of the TextComponent
				.icon("minecraft:golden_apple")
				.trigger(Trigger.builder(Trigger.TriggerType.CONSUME_ITEM, "test") // triggers
																					// when
																					// consuming
																					// an
																					// item
						.condition(Condition.builder("potion", new ItemStack(Material.APPLE, 1)))) // 1
																									// x
																									// apple
				.hidden(true) // Advancement is hidden before completed
				.toast(true) // should send a Toast Message -> popup right upper
								// corner
				.background("minecraft:textures/gui/advancements/backgrounds/stone.png").frame(FrameType.GOAL)
				.parent(parent.getId().toString()) // define a parent! example
													// above
				.build();

		child.save(worldName);
		apiList.add(child);

	}

	public void createAndSave() {

		AdvancementAPI parent = AdvancementAPI.builder(new NamespacedKey("test", "my/firststeps")).title("First Steps")
				.description("Starting").icon("minecraft:wood_sword")
				.trigger(Trigger.builder(Trigger.TriggerType.CONSUME_ITEM, "test")
						.condition(Condition.builder("potion", new ItemStack(Material.BREAD, 1))))
				.hidden(false).toast(false).background("minecraft:textures/gui/advancements/backgrounds/stone.png")
				.frame(FrameType.TASK).build();

		parent.save(worldName);
		parent.add();

		// you're able to use TextComponents @see
		// https://www.spigotmc.org/wiki/the-chat-component-api/#colors-and-formatting
		TextComponent textComponent = new TextComponent("Addiction!");
		textComponent.setBold(true);
		textComponent.setColor(ChatColor.GOLD);

		AdvancementAPI advancementAPI = AdvancementAPI.builder(new NamespacedKey("test", "my/addiction"))
				.title(textComponent) // the TextComponent define above
				.description("Eat an Apple") // you can also use a normal String
												// instead of the TextComponent
				.icon("minecraft:golden_apple")
				.trigger(Trigger.builder(Trigger.TriggerType.CONSUME_ITEM, "test") // triggers
																					// when
																					// consuming
																					// an
																					// item
						.condition(Condition.builder("potion", new ItemStack(Material.APPLE, 1)))) // 1
																									// x
																									// apple
				.hidden(true) // Advancement is hidden before completed
				.toast(true) // should send a Toast Message -> popup right upper
								// corner
				.background("minecraft:textures/gui/advancements/backgrounds/stone.png").frame(FrameType.GOAL)
				.parent(parent.getId().toString()) // define a parent! example
													// above
				.build();

		advancementAPI.save(worldName);
		advancementAPI.add();

	}

	/**
	 * updatePlayerAdvancements is run after Achievements is loaded from disk,
	 * when the player joins the server
	 * 
	 * @param player
	 */
	public void updatePlayerAdvancements(Player player) {

		Collection<Achievement> achivements = MobHunting.getAchievementManager().getAllAchievements();

		for (AdvancementAPI api : apiList) {
			if (api.getDescription().getText().equalsIgnoreCase("Starting")) {
				Messages.debug("Granting %s", api.getDescription().getText());
				api.grant(player);
			} else {
				Messages.debug("Not granting %s", api.getDescription().getText());
			}
		}

		for (Achievement achievement : achivements) {
			if (MobHunting.getAchievementManager().hasAchievement(achievement, player)) {
				//Messages.debug("Handle: %s", achievement.getID());
				if (achievement.getID().equalsIgnoreCase("huntbegins")) {
					// do nothing, manually added
				} else if (achievement instanceof ProgressAchievement
						&& ((ProgressAchievement) achievement).inheritFrom() != null) {

				} else {

				}
			}
		}

	}

}
