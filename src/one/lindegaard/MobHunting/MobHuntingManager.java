package one.lindegaard.MobHunting;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.events.MobHuntEnableCheckEvent;
import one.lindegaard.MobHunting.update.UpdateHelper;

public class MobHuntingManager implements Listener {

	private MobHunting instance;
	public Random mRand = new Random();
	private final String HUNTDATA = "MH:HuntData";

	/**
	 * Constructor for MobHuntingManager
	 * 
	 * @param instance
	 */
	public MobHuntingManager(MobHunting instance) {
		this.instance = instance;
		Bukkit.getServer().getPluginManager().registerEvents(this, instance);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		setHuntEnabled(player, true);
		if (player.hasPermission("mobhunting.update")) {
			new BukkitRunnable() {
				@Override
				public void run() {
					UpdateHelper.pluginUpdateCheck(player, true, true);
				}
			}.runTaskLater(instance, 20L);
		}
	}

	/**
	 * Set if MobHunting is allowed for the player
	 * 
	 * @param player
	 * @param enabled
	 *            = true : means the MobHunting is allowed
	 */
	public void setHuntEnabled(Player player, boolean enabled) {
		player.setMetadata("MH:enabled", new FixedMetadataValue(MobHunting.getInstance(), enabled));
	}

	/**
	 * Gets the online player (backwards compatibility)
	 * 
	 * @return number of players online
	 */
	public int getOnlinePlayersAmount() {
		try {
			Method method = Server.class.getMethod("getOnlinePlayers");
			if (method.getReturnType().equals(Collection.class)) {
				return ((Collection<?>) method.invoke(Bukkit.getServer())).size();
			} else {
				return ((Player[]) method.invoke(Bukkit.getServer())).length;
			}
		} catch (Exception ex) {
			Messages.debug(ex.getMessage().toString());
		}
		return 0;
	}

	/**
	 * Gets the online player (for backwards compatibility)
	 * 
	 * @return all online players as a Java Collection, if return type of
	 *         Bukkit.getOnlinePlayers() is Player[] it will be converted to a
	 *         Collection.
	 */
	@SuppressWarnings({ "unchecked" })
	public Collection<Player> getOnlinePlayers() {
		Method method;
		try {
			method = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
			Object players = method.invoke(null);
			Collection<Player> newPlayers;
			if (players instanceof Player[])
				newPlayers = Arrays.asList((Player[]) players);
			else
				newPlayers = (Collection<Player>) players;
			return newPlayers;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return Collections.emptyList();
	}

	/**
	 * Checks if MobHunting is enabled for the player
	 * 
	 * @param player
	 * @return true if MobHunting is enabled for the player, false if not.
	 */
	public boolean isHuntEnabled(Player player) {
		if (CitizensCompat.isNPC(player))
			return false;

		if (!player.hasMetadata("MH:enabled")) {
			Messages.debug("KillBlocked %s: Player doesnt have MH:enabled", player.getName());
			return false;
		}

		List<MetadataValue> values = player.getMetadata("MH:enabled");

		// Use the first value that matches the required type
		boolean enabled = false;
		for (MetadataValue value : values) {
			if (value.value() instanceof Boolean)
				enabled = value.asBoolean();
		}

		if (enabled && !player.hasPermission("mobhunting.enable")) {
			Messages.debug("KillBlocked %s: Player doesnt have permission mobhunting.enable", player.getName());
			return false;
		}

		if (!enabled) {
			Messages.debug("KillBlocked %s: MH:enabled is false", player.getName());
			return false;
		}

		MobHuntEnableCheckEvent event = new MobHuntEnableCheckEvent(player);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isEnabled())
			Messages.debug("KillBlocked %s: Plugin cancelled check", player.getName());
		return event.isEnabled();
	}

	/**
	 * get the HuntData() stored on the player.
	 * 
	 * @param player
	 * @return HuntData
	 */
	public HuntData getHuntData(Player player) {
		HuntData data = new HuntData(instance);
		if (!player.hasMetadata(HUNTDATA)) {
			player.setMetadata(HUNTDATA, new FixedMetadataValue(instance, data));
		} else {
			List<MetadataValue> md = player.getMetadata(HUNTDATA);
			for (MetadataValue mdv : md) {
				if (mdv.value() instanceof HuntData) {
					data = (HuntData) mdv.value();
					break;
				}
			}
		}
		return data;
	}

	public double handleKillstreak(Player player) {
		HuntData data = getHuntData(player);

		// Killstreak can be disabled by setting the multiplier to 1

		int lastKillstreakLevel = data.getKillstreakLevel();
		Messages.debug("lastKillstreakLevel=%s", lastKillstreakLevel);

		data.setKillStreak(data.getKillStreak() + 1);
		player.setMetadata(HUNTDATA, new FixedMetadataValue(instance, data));

		Messages.debug("newKillstreakLevel=%s", data.getKillstreakLevel());

		double multiplier = data.getKillstreakMultiplier();
		if (multiplier != 1) {
			// Give a message notifying of killstreak increase
			if (data.getKillstreakLevel() != lastKillstreakLevel) {
				switch (data.getKillstreakLevel()) {
				case 1:
					Messages.playerBossbarMessage(player,
							ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.1") + " " + ChatColor.GRAY
									+ Messages.getString("mobhunting.killstreak.activated", "multiplier",
											String.format("%.1f", multiplier)));
					break;
				case 2:
					Messages.playerBossbarMessage(player,
							ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.2") + " " + ChatColor.GRAY
									+ Messages.getString("mobhunting.killstreak.activated", "multiplier",
											String.format("%.1f", multiplier)));
					break;
				case 3:
					Messages.playerBossbarMessage(player,
							ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.3") + " " + ChatColor.GRAY
									+ Messages.getString("mobhunting.killstreak.activated", "multiplier",
											String.format("%.1f", multiplier)));
					break;
				default:
					Messages.playerBossbarMessage(player,
							ChatColor.BLUE + Messages.getString("mobhunting.killstreak.level.4") + " " + ChatColor.GRAY
									+ Messages.getString("mobhunting.killstreak.activated", "multiplier",
											String.format("%.1f", multiplier)));
					break;
				}

			}
		}

		return multiplier;
	}

	/**
	 * Check if MobHunting is allowed in world
	 * 
	 * @param world
	 * @return true if MobHunting is allowed.
	 */
	public boolean isHuntEnabledInWorld(World world) {
		if (world != null)
			for (String worldName : MobHunting.getConfigManager().disabledInWorlds) {
				if (world.getName().equalsIgnoreCase(worldName))
					return false;
			}

		return true;
	}

	public boolean isKillRewareded(Player killer, LivingEntity killed, EntityDeathEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void rewardKill(Player killer, LivingEntity killed, EntityDeathEvent event) {
		// TODO Auto-generated method stub
	}

}
