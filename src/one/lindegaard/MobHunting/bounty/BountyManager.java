package one.lindegaard.MobHunting.bounty;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
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

	private HashMap<OfflinePlayer, Bounties> mBounties = new HashMap<OfflinePlayer, Bounties>();
	private File file = new File(MobHunting.getInstance().getDataFolder(), "bounties.yml");
	private YamlConfiguration config = new YamlConfiguration();

	public BountyManager(MobHunting instance) {
		this.instance = instance;
		// loadData();
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
	}

	public static void initialize(MobHunting instance) {
		mBountyManager = new BountyManager(instance);
		addTestData();
	}
	
	private static void addTestData(){
		OfflinePlayer p1 = Bukkit.getOfflinePlayer("MrDanielBoy");
		OfflinePlayer p2 = Bukkit.getOfflinePlayer("JeansenDK");
		OfflinePlayer p3 = Bukkit.getOfflinePlayer("Gabriel333");
		Bounty b1 = new Bounty(p1, 101, "he he he");
		Bounty b2 = new Bounty(p1, 102, "ho ho ho");
		Bounty b3 = new Bounty(p2, 200, "ha ha ha");
		Bounty b4 = new Bounty(p3, 300, "hi hi hi");

		mBountyManager.putBountyOnWantedPlayer(p1, b3);
		mBountyManager.saveBounties(p1);
		mBountyManager.putBountyOnWantedPlayer(p1, b4);
		mBountyManager.saveBounties(p1);
		mBountyManager.putBountyOnWantedPlayer(p2, b1);
		mBountyManager.saveBounties(p2);
		mBountyManager.putBountyOnWantedPlayer(p3, b2);
		mBountyManager.saveBounties(p3);

		mBountyManager.loadBounties(p1);
		mBountyManager.loadBounties(p2);
		mBountyManager.loadBounties(p3);
	}

	public static void shutdown() {
		mBountyManager.saveBounties();
	}

	public static BountyManager getBountyManager() {
		return mBountyManager;
	}

	public void putBountyOnWantedPlayer(OfflinePlayer wantedPlayer, Bounty bounty) {
		Bounties bounties;
		if (!mBounties.containsKey(wantedPlayer)) {
			bounties = new Bounties();
		} else {
			bounties = mBounties.get(wantedPlayer);
		}
		bounties.putBounty(bounty.getBountyOwner(), bounty);
		mBounties.put(wantedPlayer, bounties);

	}

	public Set<OfflinePlayer> getAllWantedPlayers() {
		return mBounties.keySet();
	}

	public HashMap<OfflinePlayer, Bounties> getBounties() {
		return mBounties;
	}

	public void removeBounty(OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		mBounties.get(wantedPlayer).removeBounty(bountyOwner);
	}

	// Tests

	public boolean hasBounties(UUID uuid) {
		return mBounties.containsKey(uuid);
	}

	public boolean hasBounties(OfflinePlayer wantedPlayer) {
		return mBounties.containsKey(wantedPlayer);
	}

	public boolean hasBounty(OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		if (hasBounties(wantedPlayer))
			return mBounties.get(wantedPlayer).hasBounty(bountyOwner);
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
		loadBounties(e.getPlayer());
	}

	public void onPlayerLeave(PlayerQuitEvent e) {
		saveBounties(e.getPlayer());
	}

	// ****************************************************************************
	// Save & Load
	// ****************************************************************************

	public void loadBounties(OfflinePlayer offlinePlayer) {
		try {
			if (!file.exists())
				return;
			MobHunting.debug("Loading bounties for %s.", offlinePlayer.getName());
			config.load(file);
			ConfigurationSection section = config
					.getConfigurationSection("wantedplayers." + offlinePlayer.getUniqueId().toString() + ".bounties");
			Bounties bounties = new Bounties();
			// Set<String> keys = section.getKeys(false);
			// for (String uuid : keys) {
			bounties.read(section);
			mBounties.put(offlinePlayer, bounties);
			// }
			MobHunting.debug("Loaded %s bounties on player %s", mBounties.get(offlinePlayer).getBounties().size(),
					offlinePlayer.getName());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveBounties(OfflinePlayer offlinePlayer) {
		try {
			config.options().header("----------------------------------------------------------"
					+ "\nBounties put on wantedplayers. You are not allowed to change this file."
					+ "\n----------------------------------------------------------");
			if (mBounties.containsKey(offlinePlayer)) {
				MobHunting.debug("Saving wantedPlayer (%s) to file.", offlinePlayer.getName());
				ConfigurationSection section = config
						.createSection("wantedplayers." + offlinePlayer.getUniqueId().toString());
				section.set("name", offlinePlayer.getName());
				mBounties.get(offlinePlayer).write(section);
				config.save(file);
			} else if (config.contains(offlinePlayer.getUniqueId().toString())) {
				config.set(offlinePlayer.getUniqueId().toString(), null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveBounties() {
		try {
			config.options().header("----------------------------------------------------------"
					+"\nBounties put on wantedplayers. You are not allowed to change this file."+
					"\n----------------------------------------------------------");
			int n = 0;
			if (mBounties.size() > 0) {
				for (OfflinePlayer offlinePlayer : mBounties.keySet()) {
					ConfigurationSection section = config
							.createSection("wantedplayers." + offlinePlayer.getUniqueId().toString());
					section.set("name", offlinePlayer.getName());
					mBounties.get(offlinePlayer).write(section);
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
