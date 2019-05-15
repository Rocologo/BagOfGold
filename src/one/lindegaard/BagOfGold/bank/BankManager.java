package one.lindegaard.BagOfGold.bank;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.PlayerBalances;
import one.lindegaard.BagOfGold.PlayerSettings;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.Core.Tools;
import one.lindegaard.Core.Server.Servers;

public class BankManager {

	BagOfGold plugin;
	long period = 168000;
	private BukkitTask mBankInterestCalculator = null;

	public BankManager(BagOfGold plugin) {
		this.plugin = plugin;
		if (plugin.getConfigManager().calculateInterests) {
			switch (plugin.getConfigManager().interestPeriod) {
			case "TEST":
				period = 1200;
				break;

			case "DAY":
				period = 24000;
				break;

			case "WEEK":
				period = 168000;
				break;

			case "MONTH":
				period = 720000;
				break;

			case "YEAR":
				period = 8766000;
				break;

			default:
				period = 168000;
				break;
			}
			mBankInterestCalculator = Bukkit.getScheduler().runTaskTimer(plugin, new InterestUpdater(), 120L, period);
		}
	}

	public void shutdown() {
		if (mBankInterestCalculator != null)
			mBankInterestCalculator.cancel();
	}

	private class InterestUpdater implements Runnable {

		@Override
		public void run() {
			Set<String> worldGroups = plugin.getWorldGroupManager().getAllWorldGroups();
			for (String worldGroup : worldGroups) {
				List<String> worlds = plugin.getWorldGroupManager().getWorlds(worldGroup);
				if (!worlds.isEmpty()) {
					World world = null;
					for (String w : worlds) {
						world = Bukkit.getServer().getWorld(w);
						if (world != null)
							break;
						else
							Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
									+ "Warning you have a non existing world (" + w + ") in WorldGroup " + worldGroup);
					}
					if (world != null) {
						plugin.getMessages().debug(
								"Calculating interest for players in worldGroup:%s, world=%s, worldTime=%s, fullTime=%s",
								worldGroup, world.getName(), world.getTime(), world.getFullTime());
						Collection<Player> onlinePlayers = Tools.getOnlinePlayers();
						for (Player p : onlinePlayers) {
							PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(p);
							PlayerBalances pbs = plugin.getPlayerBalanceManager().getBalances().get(p.getUniqueId());
							while (ps.getLast_interest() + period < System.currentTimeMillis()) {
								for (PlayerBalance pb : pbs.getPlayerBalances().values()) {
									pb.setBankBalance(
											pb.getBankBalance() * (1 + plugin.getConfigManager().interest / 100));
									plugin.getPlayerBalanceManager().setPlayerBalance(p, pb);
								}
								ps.setLast_interest(ps.getLast_interest() + period);
							}
						}
					}
				}
			}
		}
	}

	public boolean isBagOfGoldBanker(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
			NPC npc = CitizensCompat.getCitizensPlugin().getNPCRegistry().getNPC(entity);
			return (npc.hasTrait(BagOfGoldBankerTrait.class));
		} else
			return false;
	}

	public void sendBankerMessage(Player player) {
		if (Servers.isSpigotServer()) {
			plugin.getMessages().playerSendMessage(player,
					" \n" + plugin.getMessages().getString("bagofgold.banker.introduction"));
			PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);

			player.spigot().sendMessage(new ComponentBuilder("Balance: "
					+ plugin.getEconomyManager().format(ps.getBalance() + ps.getBalanceChanges()) + " BankBalance: "
					+ plugin.getEconomyManager().format(ps.getBankBalance() + ps.getBankBalanceChanges()))
							.color(ChatColor.GREEN).bold(true).create());
			ComponentBuilder deposit = new ComponentBuilder("Deposit: ").color(ChatColor.GREEN).bold(true).append(" ");
			Iterator<Entry<String, String>> itr1 = plugin.getConfigManager().actions.entrySet().iterator();
			while (itr1.hasNext()) {
				Entry<String, String> set = itr1.next();
				if (set.getKey().startsWith("deposit")) {
					deposit.append("[" + set.getValue() + "]").color(ChatColor.RED).bold(true)
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new BaseComponent[] {
											new TextComponent("§cClick to deposit " + set.getValue() + ".") }))
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
									"/bagofgold money deposit " + set.getValue()))
							.append(" ");
				}
			}
			player.spigot().sendMessage(deposit.create());

			ComponentBuilder withdraw = new ComponentBuilder("Withdraw: ").color(ChatColor.GREEN).bold(true)
					.append(" ");
			Iterator<Entry<String, String>> itr2 = plugin.getConfigManager().actions.entrySet().iterator();
			while (itr2.hasNext()) {
				Entry<String, String> set = itr2.next();
				if (set.getKey().startsWith("withdraw")) {
					withdraw.append("[" + set.getValue() + "]").color(ChatColor.RED).bold(true)
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new BaseComponent[] {
											new TextComponent("§cClick to withdraw " + set.getValue() + ".") }))
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
									"/bagofgold money withdraw " + set.getValue()))
							.append(" ");
				}
			}
			player.spigot().sendMessage(withdraw.create());
		} else {
			player.sendMessage("The Banker only works on SpigotMC serverse");
		}

	}

}
