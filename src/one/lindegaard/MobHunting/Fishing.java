package one.lindegaard.MobHunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.events.MobHuntFishingEvent;
import one.lindegaard.MobHunting.mobs.ExtendedMob;
import one.lindegaard.MobHunting.modifier.IModifier;

public class Fishing implements Listener {

	public Fishing() {

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	// @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void Fish(PlayerFishEvent event) {
		if (event.isCancelled())
			return;

		Player player = event.getPlayer();
		if (player == null)
			return;

		Entity fish = event.getCaught();
		if (fish == null || !(fish instanceof Item) || ((Item) fish).getItemStack().getType() != Material.RAW_FISH)
			return;

		State state = event.getState();
		Messages.debug("FishingEvent: %s %s, caught=%s", player.getName(), state, event.getCaught());
		if (event.getCaught() != null)
			Messages.debug("FishingEvent: Caught=%s ", (Item) event.getCaught());

		switch (state) {
		case CAUGHT_FISH:
			// When a player has successfully caught a fish and is reeling it
			// in.
			// break;
		case CAUGHT_ENTITY:
			// When a player has successfully caught an entity
			if (player.getGameMode() != GameMode.SURVIVAL) {
				Messages.debug("FishingBlocked: %s is not in survival mode", player.getName());
				Messages.learn(player, Messages.getString("mobhunting.learn.survival"));
				return;
			}

			ItemStack is = ((Item) fish).getItemStack();
			Messages.debug("%s caught a %s ", player.getName(), is.getData());

			// Calculate basic the reward
			ExtendedMob eMob = MobHunting.getExtendedMobManager().getExtendedMobFromEntity(fish);
			if (eMob.getMob_id() == 0) {
				Bukkit.getLogger().warning("Unknown Mob:" + eMob.getName() + " from plugin " + eMob.getMobPlugin());
				Bukkit.getLogger().warning("Please report this to developer!");
				return;
			}
			double cash = MobHunting.getConfigManager().getBaseKillPrize(fish);

			Messages.debug("Basic Prize=%s for catching a %s", cash, eMob.getName());

			// Pay the reward to player and assister
			if ((cash >= MobHunting.getConfigManager().minimumReward)
					|| (cash <= -MobHunting.getConfigManager().minimumReward)) {

				// Apply the modifiers to Basic reward
				double multiplier = 1.0;
				HashMap<String, Double> multiplierList = new HashMap<String, Double>();
				ArrayList<String> modifiers = new ArrayList<String>();
				for (IModifier mod : MobHunting.getMobHuntingManager().getFishingModifiers()) {
					if (mod.doesApply(fish, player, null, null, null)) {
						double amt = mod.getMultiplier(fish, player, null, null, null);
						if (amt != 1.0) {
							Messages.debug("Multiplier: %s = %s", mod.getName(), amt);
							modifiers.add(mod.getName());
							multiplierList.put(mod.getName(), amt);
							multiplier *= amt;
						}
					}
				}

				// Handle MobHuntFishingEvent
				MobHuntFishingEvent event2 = new MobHuntFishingEvent(player, fish, cash, multiplierList);
				Bukkit.getPluginManager().callEvent(event2);
				if (event2.isCancelled()) {
					Messages.debug("FishingBlocked %s: MobHuntFishingEvent was cancelled", player.getName());
					return;
				}

				String extraString = "";

				// Only display the multiplier if its not 1
				if (Math.abs(multiplier - 1) > 0.05)
					extraString += String.format("x%.1f", multiplier);

				// Add on modifiers
				for (String modifier : modifiers)
					extraString += ChatColor.WHITE + " * " + modifier;

				cash *= multiplier;

				if (cash >= MobHunting.getConfigManager().minimumReward) {
					MobHunting.getRewardManager().depositPlayer(player, cash);
					Messages.debug("%s got a reward (%s)", player.getName(),
							MobHunting.getRewardManager().format(cash));
				} else if (cash <= -MobHunting.getConfigManager().minimumReward) {
					MobHunting.getRewardManager().withdrawPlayer(player, -cash);
					Messages.debug("%s got a penalty (%s)", player.getName(),
							MobHunting.getRewardManager().format(cash));
				}

				// Record the kill in the Database
				if (player != null) {
					Messages.debug("RecordFishing: %s caught a %s (%s)", player.getName(), eMob.getName(),
							eMob.getMobPlugin().name());
					MobHunting.getDataStoreManager().recordKill(player, eMob, player.hasMetadata("MH:hasBonus"));
				}

				// Handle Muted mode
				boolean fisherman_muted = false;
				if (MobHunting.getPlayerSettingsmanager().containsKey(player))
					fisherman_muted = MobHunting.getPlayerSettingsmanager().getPlayerSettings(player).isMuted();

				// Tell the player that he got the reward/penalty,
				// unless
				// muted
				if (!fisherman_muted)
					if (extraString.trim().isEmpty()) {
						if (cash >= MobHunting.getConfigManager().minimumReward) {
							if (!MobHunting.getConfigManager().dropMoneyOnGroup)
								Messages.playerActionBarMessage(player,
										ChatColor.GREEN + "" + ChatColor.ITALIC
												+ Messages.getString("mobhunting.fishcaught.reward", "prize",
														MobHunting.getRewardManager().format(cash)));
						} else if (cash <= -MobHunting.getConfigManager().minimumReward) {
							Messages.playerActionBarMessage(player,
									ChatColor.RED + "" + ChatColor.ITALIC
											+ Messages.getString("mobhunting.fishcaught.penalty", "prize",
													MobHunting.getRewardManager().format(cash)));
						}

					} else {
						if (cash >= MobHunting.getConfigManager().minimumReward) {
							Messages.playerActionBarMessage(player,
									ChatColor.GREEN + "" + ChatColor.ITALIC
											+ Messages.getString("mobhunting.fishcaught.reward", "prize",
													MobHunting.getRewardManager().format(cash)));
						} else if (cash <= -MobHunting.getConfigManager().minimumReward) {
							Messages.playerActionBarMessage(player,
									ChatColor.RED + "" + ChatColor.ITALIC
											+ Messages.getString("mobhunting.fishcaught.penalty", "prize",
													MobHunting.getRewardManager().format(cash)));
						} else
							Messages.debug("FishingBlocked %s: Reward was less than %s", player.getName(),
									MobHunting.getConfigManager().minimumReward);
					}

				String fishermanPos = player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + " "
						+ player.getLocation().getBlockZ();
				if (MobHunting.getConfigManager().isCmdGointToBeExcuted(fish)) {
					String worldname = player.getWorld().getName();
					String prizeCommand = MobHunting.getConfigManager().getKillConsoleCmd(fish)
							.replaceAll("\\{player\\}", player.getName()).replaceAll("\\{killer\\}", player.getName())
							.replaceAll("\\{world\\}", worldname)
							.replace("\\{prize\\}", MobHunting.getRewardManager().format(cash))
							.replaceAll("\\{killerpos\\}", fishermanPos);
					Messages.debug("command to be run is:" + prizeCommand);
					if (!MobHunting.getConfigManager().getKillConsoleCmd(fish).equals("")) {
						String str = prizeCommand;
						do {
							if (str.contains("|")) {
								int n = str.indexOf("|");
								Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(),
										str.substring(0, n));
								str = str.substring(n + 1, str.length()).toString();
							}
						} while (str.contains("|"));
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), str);
					}
				}
				// send a message to the player
				if (!MobHunting.getConfigManager().getKillRewardDescription(fish).equals("") && !fisherman_muted) {
					String worldname = player.getWorld().getName();
					String message = ChatColor.GREEN + "" + ChatColor.ITALIC
							+ MobHunting.getConfigManager().getKillRewardDescription(fish)
									.replaceAll("\\{player\\}", player.getName())
									.replaceAll("\\{killer\\}", player.getName())
									.replace("\\{prize\\}", MobHunting.getRewardManager().format(cash))
									.replaceAll("\\{world\\}", worldname).replaceAll("\\{killerpos\\}", fishermanPos);

					Messages.debug("Description to be send:" + message);
					player.sendMessage(message);
				}
			}

			break;
		case BITE:
			// Called when there is a bite on the hook and it is ready to be
			// reeled in.
			break;
		case FAILED_ATTEMPT:
			// When a player fails to catch anything while fishing usually due
			// to poor aiming or timing
			break;
		case FISHING:
			// When a player is fishing, ie casting the line out.
			break;
		case IN_GROUND:
			// When a bobber is stuck in the ground
			break;
		// default:
		// break;

		}
	}
}
