package one.lindegaard.MobHunting.bounty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.storage.IDataCallback;

public class BountyManager implements Listener {

	private MobHunting instance;

	private static final String MH_BOUNTY = "MH:bounty";

	//mBounties contains all bounties on the OfflinePlayer and the Bounties put on other players
	private static HashMap<OfflinePlayer, Bounties> mBounties = new HashMap<OfflinePlayer, Bounties>();
	//private static List<Bounty> mNewBounties;

	public BountyManager(MobHunting instance) {
		this.instance = instance;
		// loadData();
		addTestData();
		Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());
	}

	private void addTestData() {
		OfflinePlayer p1 = Bukkit.getOfflinePlayer("Gabriel333");
		OfflinePlayer p2 = Bukkit.getOfflinePlayer("JeansenDK");
		OfflinePlayer p3 = Bukkit.getOfflinePlayer("MrDanielBoy");

		Bounty b1 = new Bounty("Default", p3, p2, 101, "he he he");
		Bounty b2 = new Bounty("Default", p3, p1, 102, "ho ho ho");
		Bounty b3 = new Bounty("Default", p2, p3, 200, "ha ha ha");
		Bounty b4 = new Bounty("Default", p1, p3, 300, "hi hi hi");

		insertBountyOnWantedPlayer(b1);
		insertBountyOnWantedPlayer(b2);
		insertBountyOnWantedPlayer(b3);
		insertBountyOnWantedPlayer(b4);

		// putBountyOnWantedPlayer(p1, b3);
		// saveBounties(p1);
		// putBountyOnWantedPlayer(p1, b4);
		// saveBounties(p1);
		// putBountyOnWantedPlayer(p2, b1);
		// saveBounties(p2);
		// putBountyOnWantedPlayer(p3, b2);
		// saveBounties(p3);

		// loadBounties(p1);
		// loadBounties(p2);
		// loadBounties(p3);
	}

	public void shutdown() {
		// saveBounties();
	}

	public void insertBountyOnWantedPlayer(Bounty bounty) {
		MobHunting.getDataStoreManager().insertBounty(bounty);
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

	public HashMap<OfflinePlayer, Bounties> getBounties() {
		return mBounties;
	}

	public void removeBounty(OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		mBounties.get(wantedPlayer).removeBounty(bountyOwner);
	}

	// Tests

	public boolean hasBounties(OfflinePlayer wantedPlayer) {
		MobHunting.debug("mBounties.szie=%s", mBounties.size());
		return mBounties.containsKey(wantedPlayer);
	}

	public boolean hasBounty(OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		if (hasBounties(wantedPlayer))
			return mBounties.get(wantedPlayer).hasBounties(bountyOwner);
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerJoin(PlayerJoinEvent e) {
		addMarkOnWantedPlayer(e.getPlayer());
		loadBounties(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerLeave(PlayerQuitEvent e) {
		// saveBounties(e.getPlayer());
	}

	// ****************************************************************************
	// Save & Load
	// ****************************************************************************
	public void loadBounties(OfflinePlayer offlinePlayer) {

	}

	public void saveBounties(OfflinePlayer offlinePlayer) {

	}
	
	public void requestOpenBounties(OfflinePlayer player,
			final IDataCallback<List<Bounty>> callback) {
		if (player.isOnline()) {
			List<Bounty> achievements = new ArrayList<Bounty>();
			ArrayList<Bounty> toRemove = new ArrayList<Bounty>();

			for (Bounty bounty : mBounties.get(player).getBounties().values().iterator()) {
				
			}

			achievements.removeAll(toRemove);

			callback.onCompleted(achievements);
			return;
		}
	}
	
	
}
