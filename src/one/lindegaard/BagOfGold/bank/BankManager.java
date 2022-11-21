package one.lindegaard.BagOfGold.bank;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.PlayerBalances;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.PlayerSettings;
import one.lindegaard.CustomItemsLib.Tools;

public class BankManager {

	BagOfGold plugin;
	long period = 168000;
	private BukkitTask mBankInterestCalculator = null;

	public BankManager(BagOfGold plugin) {
		this.plugin = plugin;
		start();
	}

	public void start() {
		if (plugin.getConfigManager().calculateInterests) {
			// https://minecraft.gamepedia.com/Day-night_cycle
			switch (plugin.getConfigManager().interestPeriod) {
			case "DAY":
				period = 24000; // 1 minecraft day = 20 min
				break;

			case "WEEK":
				period = 168000; // 1 minecraft week = 2.3 hours
				break;

			case "MONTH":
				period = 720000; // 1 minecraft months = 10 hours
				break;

			case "YEAR":
				period = 8766000; // 1 minecraaddTaskft year = 121.75 hours
				break;

			default:

				if (plugin.getConfigManager().interestPeriod.matches("\\d+$")) {
					period = Integer.valueOf(plugin.getConfigManager().interestPeriod) * 20;
				} else {
					Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
							+ "The interest period in the config.yml must be an integer (seconds) or 'DAY/WEEK/MONTH/YEAR'.");
				}

				break;
			}
			mBankInterestCalculator = Bukkit.getScheduler().runTaskTimer(plugin, new InterestUpdater2(), period,
					period);
		}

	}

	public void shutdown() {
		if (mBankInterestCalculator != null)
			mBankInterestCalculator.cancel();
	}

	private class InterestUpdater2 implements Runnable {

		@Override
		public void run() {
			plugin.getMessages().debug(ChatColor.BLUE + "Start bank interest calculation.");
			Collection<Player> onlinePlayers = Tools.getOnlinePlayers();
			for (Player p : onlinePlayers) {
				PlayerSettings ps = Core.getPlayerSettingsManager().getPlayerSettings(p);
				if (ps.getLast_interest() == 0)
					ps.setLast_interest(System.currentTimeMillis() - period);
				PlayerBalances pbs = plugin.getPlayerBalanceManager().getBalances().get(p.getUniqueId());
				for (PlayerBalance pb : pbs.getPlayerBalances().values()) {
					plugin.getMessages()
							.debug(ChatColor.BLUE
									+ "Calculating Bank interest for %s in worldGroup:%s (Balance=%s, new balance=%s",
									p.getName(), pb.getWorldGroup(), pb.getBankBalance(),
									Tools.round(pb.getBankBalance() * (1 + plugin.getConfigManager().interest / 100)));
					pb.setBankBalance(Tools.round(pb.getBankBalance() * (1 + plugin.getConfigManager().interest / 100)));
					plugin.getPlayerBalanceManager().setPlayerBalance(p, pb);
				}
				ps.setLast_interest(ps.getLast_interest() + period);
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

		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);

		player.spigot()
				.sendMessage(new ComponentBuilder(plugin.getMessages().getString("bagofgold.banker.balance") + ": "
						+ plugin.getEconomyManager().format(ps.getBalance() + ps.getBalanceChanges()) + " "
						+ plugin.getMessages().getString("bagofgold.banker.bankbalance") + ": "
						+ plugin.getEconomyManager().format(ps.getBankBalance() + ps.getBankBalanceChanges()))
						.color(ChatColor.GREEN).bold(true).create());
		
		plugin.getMessages().debug("BankManager actions(" + plugin.getMessages().getString("bagofgold.banker.deposit") + "/"
				+ plugin.getMessages().getString("bagofgold.banker.withdraw") + ")="
				+ plugin.getConfigManager().actions.entrySet().toString());

		ComponentBuilder deposit = new ComponentBuilder(
				plugin.getMessages().getString("bagofgold.banker.deposit") + ": ").color(ChatColor.GREEN).bold(true)
				.append(" ");
		Iterator<Entry<String, String>> itr1 = plugin.getConfigManager().actions.entrySet().iterator();
		while (itr1.hasNext()) {
			Entry<String, String> set = itr1.next();
			if (set.getKey().toLowerCase()
					.startsWith(plugin.getMessages().getString("bagofgold.banker.deposit").toLowerCase())) {
				Text clickToDeposit = new Text(
						plugin.getMessages().getString("bagofgold.banker.click2deposit") + " " + set.getValue() + ".");
				deposit.append("[" + set.getValue() + "]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickToDeposit))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/bagofgold money deposit " + set.getValue()))
						.append(" ");
			}
		}
		player.spigot().sendMessage(deposit.create());

		ComponentBuilder withdraw = new ComponentBuilder(
				plugin.getMessages().getString("bagofgold.banker.withdraw") + ": ").color(ChatColor.GREEN).bold(true)
				.append(" ");
		Iterator<Entry<String, String>> itr2 = plugin.getConfigManager().actions.entrySet().iterator();
		while (itr2.hasNext()) {
			Entry<String, String> set = itr2.next();
			if (set.getKey().toLowerCase()
					.startsWith(plugin.getMessages().getString("bagofgold.banker.withdraw").toLowerCase())) {
				Text clickToWithdraw = new Text(
						plugin.getMessages().getString("bagofgold.banker.click2withdraw") + " " + set.getValue() + ".");
				withdraw.append("[" + set.getValue() + "]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, clickToWithdraw))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/bagofgold money withdraw " + set.getValue()))
						.append(" ");
			}
		}
		player.spigot().sendMessage(withdraw.create());

	}

}
