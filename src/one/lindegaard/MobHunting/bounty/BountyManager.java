package one.lindegaard.MobHunting.bounty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.MobHunting;

public class BountyManager implements Listener {

	private MobHunting instance;

	private static BountyManager mBountyManager;
	private static final String MH_BOUNTY = "MH:bounty";

	private HashMap<UUID, WantedPlayer> mWantedPlayers = new HashMap<UUID, WantedPlayer>();
	private File file = new File(MobHunting.getInstance().getDataFolder(),
			"bounties.yml");
	private YamlConfiguration config = new YamlConfiguration();

	public BountyManager(MobHunting instance) {
		this.instance = instance;
		loadData();
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
	}

	public static void initialize(MobHunting instance) {
		mBountyManager = new BountyManager(instance);
	}

	public static void shutdown() {
		mBountyManager.saveData();
	}

	public static BountyManager getHandler() {
		return mBountyManager;
	}

	// ****************************************************************************
	// Getters and setters
	// ****************************************************************************

	public Collection<WantedPlayer> getAllWantedPlayers() {
		return mWantedPlayers.values();
	}

	public WantedPlayer getWantedPlayer(UUID wantedPlayer) {
		return mWantedPlayers.get(wantedPlayer);
	}

	public WantedPlayer getWantedPlayer(OfflinePlayer wantedPlayer) {
		return mWantedPlayers.get(wantedPlayer.getUniqueId());
	}

	public String getWantedPlayerName(UUID wantedPlayer) {
		return mWantedPlayers.get(wantedPlayer).getWantedPlayer().getName();
	}

	public Bounty getBounty(OfflinePlayer wantedPlayer,
			OfflinePlayer bountyOwner) {
		return mWantedPlayers.get(wantedPlayer).getBounty(bountyOwner);
	}

	public ArrayList<Bounty> getAllBounties(OfflinePlayer wantedPlayer) {
		return mWantedPlayers.get(wantedPlayer).getAllBounties();
	}

	public void addWantedPlayer(WantedPlayer wantedPlayer) {
		this.mWantedPlayers.put(wantedPlayer.getUniqueId(), wantedPlayer);
	}

	public void addBountyOnWantedPlayer(OfflinePlayer wantedPlayer, Bounty b) {
		if (!wantedPlayer.isOnline())
			loadData(wantedPlayer.getUniqueId());
		this.mWantedPlayers.put(
				wantedPlayer.getUniqueId(),
				new WantedPlayer(wantedPlayer, new Bounty(wantedPlayer, b
						.getPrize(), b.getMessage())));
		if (!wantedPlayer.isOnline()) {
			saveData(wantedPlayer.getUniqueId());
			mWantedPlayers.remove(wantedPlayer.getUniqueId());
		}
	}

	// public void addAllWantedPlayers(HashMap<UUID, WantedPlayer>
	// mWantedPlayers) {
	// this.mWantedPlayers = mWantedPlayers;
	// }

	public void removeBounty(OfflinePlayer wantedPlayer,
			OfflinePlayer bountyOwner) {
		if (getWantedPlayer(wantedPlayer).hasBounty(bountyOwner))
			getWantedPlayer(wantedPlayer).removeBounty(bountyOwner);
	}

	// Tests

	public boolean hasBounties(UUID uuid) {
		return mWantedPlayers.containsKey(uuid);
	}

	public boolean hasBounties(OfflinePlayer wantedPlayer) {
		return mWantedPlayers.containsValue(wantedPlayer);
	}

	public boolean hasBounty(OfflinePlayer wantedPlayer,
			OfflinePlayer bountyOwner) {
		if (mWantedPlayers.get(wantedPlayer) != null)
			return mWantedPlayers.get(wantedPlayer.getUniqueId()).hasBounty(
					bountyOwner);
		else
			return false;
	}

	// Metadata Methods

	public void addMarkOnWantedPlayer(Player player) {
		player.setMetadata(MH_BOUNTY, new FixedMetadataValue(instance, true));
	}

	public void removeMarkFromWantedPlayer(Player player) {
		if (player.hasMetadata(MH_BOUNTY))
			player.removeMetadata(MH_BOUNTY, instance);
	}

	public boolean isMarkedWantedPlayer(Player player) {
		return player.hasMetadata(MH_BOUNTY);
	}

	// ****************************************************************************
	// Events
	// ****************************************************************************

	public void onPlayerJoin(PlayerJoinEvent e) {
		addMarkOnWantedPlayer(e.getPlayer());
		// loadData(e.getPlayer().getUniqueId());
	}

	public void onPlayerLeave(PlayerQuitEvent e) {
		saveData(e.getPlayer().getUniqueId());
	}

	// ****************************************************************************
	// Save & Load
	// ****************************************************************************

	public void loadData() {
		try {
			if (!file.exists())
				return;
			MobHunting.debug("Loading bounties.");
			config.load(file);
			for (String uuid : config.getKeys(false)) {
				ConfigurationSection section = config
						.getConfigurationSection(uuid);
				WantedPlayer wantedPlayer = new WantedPlayer();
				wantedPlayer.read(section);
				mWantedPlayers.put(wantedPlayer.getUniqueId(), wantedPlayer);
			}
			MobHunting.debug("Loaded %s bounties", mWantedPlayers.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void loadData(UUID uuid) {
		try {
			if (!file.exists())
				return;
			MobHunting.debug("Loading bounties.");
			config.load(file);
			ConfigurationSection section = config.getConfigurationSection(uuid
					.toString());
			WantedPlayer wantedPlayer = new WantedPlayer();
			wantedPlayer.read(section);
			mWantedPlayers.put(wantedPlayer.getUniqueId(), wantedPlayer);
			MobHunting.debug("Loaded %s bounties", mWantedPlayers.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveData(UUID uuid) {
		try {
			if (mWantedPlayers.containsKey(uuid)) {
				ConfigurationSection section = config.createSection(uuid
						.toString());
				mWantedPlayers.get(uuid).write(section);
				MobHunting.debug("Saving wantedPlayer (%s) to file.", uuid);
				config.save(file);
			} else if (config.contains(uuid.toString())) {
				config.set(uuid.toString(), null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try {
			config.options().header("Bounties put on players.");
			int n = 0;
			if (mWantedPlayers.size() > 0) {
				for (UUID wantedPlayer : mWantedPlayers.keySet()) {
					ConfigurationSection section = config
							.createSection(wantedPlayer.toString());
					mWantedPlayers.get(wantedPlayer).write(section);
					n++;
				}
			}
			if (n != 0) {
				MobHunting.debug("Saving %s Bounties to file.", n);
				config.save(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
