package one.lindegaard.BagOfGold.bank;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

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
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;

public class BankManager {

	BagOfGold plugin;

	public BankManager(BagOfGold plugin) {
		this.plugin = plugin;
	}

	public boolean isBagOfGoldBanker(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
			NPC npc = CitizensCompat.getCitizensPlugin().getNPCRegistry().getNPC(entity);
			return (npc.hasTrait(BagOfGoldBankerTrait.class));
		} else
			return false;
	}

	public void sendBankerMessage(Player player) {
		plugin.getMessages().playerSendMessage(player,
				" \n" + plugin.getMessages().getString("bagofgold.banker.introduction"));
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);

		player.spigot()
				.sendMessage(new ComponentBuilder("Balance: "
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

		ComponentBuilder withdraw = new ComponentBuilder("Withdraw: ").color(ChatColor.GREEN).bold(true).append(" ");
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

	}

}
