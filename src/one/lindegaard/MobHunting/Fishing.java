package one.lindegaard.MobHunting;

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
import one.lindegaard.MobHunting.mobs.ExtendedMob;

public class Fishing implements Listener {

	public Fishing() {

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Fish(PlayerFishEvent e) {
		if (e.isCancelled())
			return;

		Player p = e.getPlayer();

		State state = e.getState();

		switch (state) {
		case CAUGHT_FISH:
			
			if (p.getGameMode() != GameMode.SURVIVAL) {
				Messages.debug("FishingBlocked: %s is not in survival mode", p.getName());
				Messages.learn(p, Messages.getString("mobhunting.learn.survival"));
				return;
			}

			Entity entity = e.getCaught();
			ItemStack is = ((Item) entity).getItemStack();
			if (is.getType() == Material.RAW_FISH) {
				Messages.debug("%s caught a %s ", p.getName(), is.getData());

				// Calculate basic the reward
				ExtendedMob fish = MobHunting.getExtendedMobManager().getExtendedMobFromEntity(entity);
				if (fish.getMob_id() == 0) {
					Bukkit.getLogger().warning("Unknown Mob:" + fish.getName() + " from plugin " + fish.getMobPlugin());
					Bukkit.getLogger().warning("Please report this to developer!");
					return;
				}
				double cash = MobHunting.getConfigManager().getBaseKillPrize(entity);

				Messages.debug("Basic Prize=%s for catching a %s", cash, fish.getName());

				// Pay the reward to player and assister
				if ((cash >= MobHunting.getConfigManager().minimumReward)
						|| (cash <= -MobHunting.getConfigManager().minimumReward)) {

					// Handle MobHuntFishingEvent
					// MobHuntKillEvent event2 = new MobHuntKillEvent(data,
					// info, killed, killer);
					// Bukkit.getPluginManager().callEvent(event2);
					// if (event2.isCancelled()) {
					// Messages.debug("KillBlocked %s: MobHuntKillEvent was
					// cancelled", killer.getName());
					// return;
					// }

					if (cash >= MobHunting.getConfigManager().minimumReward) {
						MobHunting.getRewardManager().depositPlayer(p, cash);
						Messages.debug("%s got a reward (%s)", p.getName(), MobHunting.getRewardManager().format(cash));
					} else if (cash <= -MobHunting.getConfigManager().minimumReward) {
						MobHunting.getRewardManager().withdrawPlayer(p, -cash);
						Messages.debug("%s got a penalty (%s)", p.getName(),
								MobHunting.getRewardManager().format(cash));
					}

					// Record the kill in the Database
					if (p != null) {
						Messages.debug("RecordFishing: %s caught a %s (%s)", p.getName(), fish.getName(),
								fish.getMobPlugin().name());
						MobHunting.getDataStoreManager().recordKill(p, fish, p.hasMetadata("MH:hasBonus"));
					}

					// Handle Muted mode
					boolean fisherman_muted = false;
					if (MobHunting.getPlayerSettingsmanager().containsKey(p))
						fisherman_muted = MobHunting.getPlayerSettingsmanager().getPlayerSettings(p).isMuted();

					// Tell the player that he got the reward/penalty, unless
					// muted
					if (!fisherman_muted)
						if (cash >= MobHunting.getConfigManager().minimumReward) {
							Messages.playerActionBarMessage(p,
									ChatColor.GREEN + "" + ChatColor.ITALIC + Messages.getString("mobhunting.moneygain",
											"prize", MobHunting.getRewardManager().format(cash)));
						} else if (cash <= -MobHunting.getConfigManager().minimumReward) {
							Messages.playerActionBarMessage(p,
									ChatColor.RED + "" + ChatColor.ITALIC + Messages.getString("mobhunting.moneylost",
											"prize", MobHunting.getRewardManager().format(cash)));
						} else
							Messages.debug("FishingBlocked %s: Reward was less than %s", p.getName(),
									MobHunting.getConfigManager().minimumReward);

					String fishermanPos = p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " "
							+ p.getLocation().getBlockZ();
					if (MobHunting.getConfigManager().isCmdGointToBeExcuted(entity)) {
						String worldname = p.getWorld().getName();
						String prizeCommand = MobHunting.getConfigManager().getKillConsoleCmd(entity)
								.replaceAll("\\{player\\}", p.getName()).replaceAll("\\{killer\\}", p.getName())
								.replaceAll("\\{world\\}", worldname)
								.replace("\\{prize\\}", MobHunting.getRewardManager().format(cash))
								.replaceAll("\\{killerpos\\}", fishermanPos);
						Messages.debug("command to be run is:" + prizeCommand);
						if (!MobHunting.getConfigManager().getKillConsoleCmd(entity).equals("")) {
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
					if (!MobHunting.getConfigManager().getKillRewardDescription(entity).equals("")
							&& !fisherman_muted) {
						String worldname = p.getWorld().getName();
						String message = ChatColor.GREEN + "" + ChatColor.ITALIC
								+ MobHunting.getConfigManager().getKillRewardDescription(entity)
										.replaceAll("\\{player\\}", p.getName()).replaceAll("\\{killer\\}", p.getName())
										.replace("\\{prize\\}", MobHunting.getRewardManager().format(cash))
										.replaceAll("\\{world\\}", worldname)
										.replaceAll("\\{killerpos\\}", fishermanPos);

						Messages.debug("Description to be send:" + message);

						p.sendMessage(message);
						//Messages.playerActionBarMessage(p, message);
					}
				}

			}

			break;
		case BITE:
			break;
		case CAUGHT_ENTITY:
			break;
		case FAILED_ATTEMPT:
			break;
		case FISHING:
			break;
		case IN_GROUND:
			break;
		default:
			break;

		}
	}
}
