package one.lindegaard.MobHunting.bounty;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.storage.IDataCallback;
import one.lindegaard.MobHunting.storage.UserNotFoundException;
import one.lindegaard.MobHunting.storage.asynch.BountyRetrieverTask.BountyMode;

public class BountyManager implements Listener {

	private MobHunting instance;

	private static final String MH_BOUNTY = "MH:bounty";

	// mBounties contains all bounties on the OfflinePlayer and the Bounties put
	// on other players
	private static Set<Bounty> mBounties = new HashSet<Bounty>();

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

		// insertBountyOnWantedPlayer(b1);
		// insertBountyOnWantedPlayer(b2);
		// insertBountyOnWantedPlayer(b3);
		// insertBountyOnWantedPlayer(b4);

		// putBountyOnWantedPlayer(p1, b3);
		// saveBounties(p1);
		// putBountyOnWantedPlayer(p1, b4);
		// saveBounties(p1);
		// putBountyOnWantedPlayer(p2, b1);
		// saveBounties(p2);
		// putBountyOnWantedPlayer(p3, b2);
		// saveBounties(p3);

		loadBounties(p1);
		loadBounties(p2);
		loadBounties(p3);

		MobHunting.debug("Number of bounties = ", mBounties.size());
	}

	public void shutdown() {
	}

	public void insertBountyOnWantedPlayer(Bounty bounty) {
		MobHunting.getDataStoreManager().insertBounty(bounty);
	}

	/**
	 * put/add a bounty on the set of Bounties.
	 * 
	 * @param offlinePlayer
	 * @param bounty
	 */
	public void addBounty(Bounty bounty) {
		Bounty tempBounty = getBounty(bounty.getWantedPlayer(), bounty.getBountyOwner());
		if (tempBounty == null) {
			MobHunting.getDataStoreManager().insertBounty(bounty);
		} else {
			tempBounty = getBounty(bounty.getWantedPlayer(), bounty.getBountyOwner());
			tempBounty.setPrize(tempBounty.getPrize() + bounty.getPrize());
			tempBounty.setMessage(bounty.getMessage());
			tempBounty.setEndDate(bounty.getEndDate() + Date.UTC(0, 0, 90, 0, 0, 0));
			// TODO: Set new date too
			mBounties.add(tempBounty);
			MobHunting.getDataStoreManager().updateBounty(tempBounty);
		}

	}

	public Set<Bounty> getBounties() {
		return mBounties;
	}

	public Bounty getBounty(OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mBounties) {
			if (bounty.getBountyOwner().equals(bountyOwner) && bounty.getWantedPlayer().equals(wantedPlayer))
				return bounty;
		}
		return null;
	}

	public Set<Bounty> getBounties(OfflinePlayer wantedPlayer) {
		Set<Bounty> bounties = new HashSet<Bounty>();
		for (Bounty bounty : mBounties) {
			if (bounty.getWantedPlayer().equals(wantedPlayer)) {
				bounties.add(bounty);
			}
		}
		return bounties;
	}

	public void markBountyCompleted(OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mBounties) {
			if (bounty.getBountyOwner().equals(bountyOwner) && bounty.getWantedPlayer().equals(wantedPlayer)) {
				bounty.setCompleted(true);
				break;
			}
		}
	}

	public void removeBounty(Bounty bounty) {
		for (Bounty b : mBounties) {
			if (b.getBountyId() == bounty.getBountyId()) {
				mBounties.remove(bounty);
				MobHunting.getDataStoreManager().deleteBounty(bounty);
				break;
			}
		}
	}

	// Tests

	public boolean hasBounty(OfflinePlayer wantedPlayer, OfflinePlayer bountyOwner) {
		for (Bounty bounty : mBounties) {
			if (bounty.getBountyOwner().equals(bountyOwner) && bounty.getWantedPlayer().equals(wantedPlayer))
				return true;
		}
		return false;
	}

	public boolean hasBounties(OfflinePlayer wantedPlayer) {
		for (Bounty bounty : mBounties) {
			if (bounty.getWantedPlayer().equals(wantedPlayer))
				return true;
		}
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
	// public void saveBounties(final OfflinePlayer offlinePlayer) {
	// MobHunting.getDataStoreManager().
	// }

	public void loadBounties(final OfflinePlayer offlinePlayer) {
		MobHunting.getDataStoreManager().requestBounties(BountyMode.Open, offlinePlayer,
				new IDataCallback<Set<Bounty>>() {

					@Override
					public void onCompleted(Set<Bounty> data) {
						MobHunting.debug("loadBounties data.size=%s", data.size());
						for (Bounty bounty : data) {
							if (!bounty.isCompleted() && !mBounties.contains(bounty)) {
								mBounties.add(bounty);
								MobHunting.debug("addBounty no=%s", bounty.getBountyId());
							}
						}

					}

					@Override
					public void onError(Throwable error) {
						if (error instanceof UserNotFoundException)
							if (offlinePlayer.isOnline()) {
								Player p = (Player) offlinePlayer;
								p.sendMessage(Messages.getString("mobhunting.bounty.user-not-found"));
							} else {
								error.printStackTrace();
								if (offlinePlayer.isOnline()) {
									Player p = (Player) offlinePlayer;
									p.sendMessage(Messages.getString("mobhunting.bounty.load-fail"));
								}
							}

					}

				});
	}

}
