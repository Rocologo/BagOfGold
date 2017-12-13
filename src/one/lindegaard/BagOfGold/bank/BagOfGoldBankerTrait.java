package one.lindegaard.BagOfGold.bank;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mysql.jdbc.Messages;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.storage.PlayerSettings;
import one.lindegaard.MobHunting.util.Misc;

public class BagOfGoldBankerTrait extends Trait implements Listener {
	// http://wiki.citizensnpcs.co/API

	// This is your trait that will be applied to a npc using the /trait
	// mytraitname command. Each NPC gets its own instance of this class.
	// the Trait class has a reference to the attached NPC class through the
	// protected field 'npc' or getNPC().
	// The Trait class also implements Listener so you can add EventHandlers
	// directly to your trait.

	private BagOfGold plugin;

	public BagOfGoldBankerTrait() {
		super("BagOfGoldBanker");
		this.plugin = BagOfGold.getInstance();
	}

	// see the 'Persistence API' section
	// @Persist("stattype")
	// String stattype = "total_kill";
	// @Persist("period")
	// String period = "alltime";
	// @Persist("rank")
	// int rank = 1;
	// @Persist("noOfKills")
	// int noOfKills = 0;
	// @Persist("signLocations")
	// List<Location> signLocations = new ArrayList<Location>();
	@Persist("home")
	Location home = null;

	// boolean SomeSetting = false;

	// Here you should load up any values you have previously saved (optional).
	// This does NOT get called when applying the trait for the first time, only
	// loading onto an existing npc at server start.
	// This is called AFTER onAttach so you can load defaults in onAttach and
	// they will be overridden here.
	// This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
	public void load(DataKey key) {
		// SomeSetting = key.getBoolean("SomeSetting", false);
	}

	// Save settings for this NPC (optional). These values will be persisted to
	// the Citizens saves.yml file
	public void save(DataKey key) {
		// key.setBoolean("SomeSetting",SomeSetting);
	}

	@EventHandler
	public void clickRight(net.citizensnpcs.api.event.NPCRightClickEvent event) {
		// Handle a click on a NPC. The event has a getNPC() method.
		// Be sure to check event.getNPC() == this.getNPC() so you only handle
		// clicks on this NPC!
		if (event.getNPC() != this.getNPC())
			return;

		Player player = event.getClicker();
		plugin.getMessages().playerSendMessage(player, Messages.getString("bagofgold.banker.introduction"));
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(player);

		player.spigot()
				.sendMessage(new ComponentBuilder("Balance:" + Misc.format(ps.getBalance() + ps.getBalanceChanges())
						+ " BankBalance:" + Misc
								.format(plugin.getEconomyManager().bankBalance(player.getUniqueId().toString()).amount))
										.color(ChatColor.GREEN).bold(true).create());
		player.spigot()
				.sendMessage(new ComponentBuilder("Deposit:").color(ChatColor.GREEN).bold(true).append(" ")
						.append("[10]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new TextComponent("§cClick to deposit 10.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money deposit 10")).append(" ")
						.append("[100]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new TextComponent("§cClick to deposit 100") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money deposit 100")).append(" ")
						.append("[All]").color(ChatColor.RED).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new TextComponent("§cClick to deposit all.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money deposit all")).create());
		player.spigot()
				.sendMessage(new ComponentBuilder("Withdraw:").color(ChatColor.GREEN).bold(true).append(" ")
						.append("[10]").color(ChatColor.GREEN).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new TextComponent("§cClick to withdraw 10.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money withdraw 10")).append(" ")
						.append("[100]").color(ChatColor.GREEN).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new TextComponent("§cClick to withdraw 100.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money withdraw 100")).append(" ")
						.append("[All]").color(ChatColor.GREEN).bold(true)
						.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
								new BaseComponent[] { new TextComponent("§cClick to withdraw all.") }))
						.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mobhunt money withdraw all")).create());

	}

	// Called every tick
	@Override
	public void run() {
	}

	// Run code when your trait is attached to a NPC.
	// This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
	// This would be a good place to load configurable defaults for new NPCs.
	@Override
	public void onAttach() {
		// load(new net.citizensnpcs.api.util.MemoryDataKey());
	}

	// Run code when the NPC is despawned. This is called before the entity
	// actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
	}

	// Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be
	// null until this method is called.
	// This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {
	}

	// run code when the NPC is removed. Use this to tear down any repeating
	// tasks.
	@Override
	public void onRemove() {
	}

}
