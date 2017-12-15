package one.lindegaard.BagOfGold.bank;

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
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
import one.lindegaard.BagOfGold.util.Misc;

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
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(player);

		player.spigot()
				.sendMessage(new ComponentBuilder("Balance: " + Misc.format(ps.getBalance() + ps.getBalanceChanges())
						+ " BankBalance: " + Misc.format(ps.getBankBalance() + ps.getBankBalanceChanges()))
								.color(ChatColor.GREEN).bold(true).create());
		player.spigot()
				.sendMessage(new ComponentBuilder("Deposit: ").color(ChatColor.GREEN).bold(true).append(" ").append("[10]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[] { new TextComponent("§cClick to deposit 10.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money deposit 10")).append(" ").append("[100]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[] { new TextComponent("§cClick to deposit 100") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money deposit 100")).append(" ").append("[1000]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[] { new TextComponent("§cClick to deposit 1000") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money deposit 1000")).append(" ").append("[All]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[] { new TextComponent("§cClick to deposit all.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money deposit all")).create());
		
		player.spigot()
				.sendMessage(new ComponentBuilder("Withdraw: ").color(ChatColor.GREEN).bold(true).append(" ").append("[10]").color(ChatColor.GREEN).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[] { new TextComponent("§cClick to withdraw 10.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money withdraw 10")).append(" ").append("[100]").color(ChatColor.GREEN).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[] { new TextComponent("§cClick to withdraw 100.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money withdraw 100")).append(" ").append("[1000]").color(ChatColor.GREEN).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[] { new TextComponent("§cClick to withdraw 1000.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money withdraw 1000")).append(" ").append("[All]").color(ChatColor.GREEN).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[] { new TextComponent("§cClick to withdraw all.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money withdraw all")).create());
	}

}
