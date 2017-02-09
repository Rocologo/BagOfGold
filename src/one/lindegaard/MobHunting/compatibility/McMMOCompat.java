package one.lindegaard.MobHunting.compatibility;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingTreasureEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerMagicHunterEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerShakeEvent;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class McMMOCompat implements Listener {

	// https://www.spigotmc.org/resources/conquesita-mobs.21307/

	private static boolean supported = false;
	private static Plugin mPlugin;
	public static final String MH_MCMMO = "MH:MCMMO";

	public McMMOCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with McMMO is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("mcMMO");

			if (mPlugin.getDescription().getVersion().compareTo("1.5.00") >= 0) {
				Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
				Bukkit.getLogger().info("[MobHunting] Enabling Compatibility with McMMO ("
						+ getCustomMobs().getDescription().getVersion() + ")");
				supported = true;
			} else {
				ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
				console.sendMessage(ChatColor.RED + "[MobHunting] Your current version of McMMO ("
						+ mPlugin.getDescription().getVersion()
						+ ") is not supported by MobHunting. Please update McMMO to version 1.5.00 or newer.");
			}
		}

	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static Plugin getCustomMobs() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isMcMMO(Entity entity) {
		if (isSupported())
			return entity.hasMetadata(MH_MCMMO);
		return false;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationMcMMO;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMcMMO;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void Fish2(McMMOPlayerFishingTreasureEvent event) {
		Player p = event.getPlayer();
		ItemStack s = event.getTreasure();
		Messages.debug("FISH2: %s caught a %s", p.getName(), s.getType());
		// if (s.getType() != Material.RAW_FISH)
		// playerTreasures.put(p, s);
		// if (event.isCancelled()) {
		// Messages.debug("McMMOPlayerFishingTreasureEvent is cancelled");
		// return;
		// }
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void Fish3(McMMOPlayerFishingEvent event) {
		Player p = event.getPlayer();
		Messages.debug("FISH3: %s is fishing", p.getName());
		// if (event.isCancelled()) {
		// Messages.debug("McMMOPlayerFishingEvent is cancelled");
		// return;
		// }
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void Fish4(McMMOPlayerMagicHunterEvent event) {
		Player p = event.getPlayer();
		ItemStack is = event.getTreasure();
		Messages.debug("FISH4: %s, Treasure = %s", p.getName(), is.getType());
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
	public void Fish5(McMMOPlayerShakeEvent event) {
		Player p = event.getPlayer();
		ItemStack is = event.getDrop();
		Messages.debug("FISH5: %s, Drop = %s", p.getName(), is.getType());
	}



}
