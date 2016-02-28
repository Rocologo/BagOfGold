package au.com.mineauz.MobHunting.npc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.compatability.CitizensCompat;
import au.com.mineauz.MobHunting.storage.DataCallback;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class MasterMobHunterData implements Listener,
		DataCallback<List<StatStore>> {

	private int id;
	private StatType statType;
	private TimePeriod period;
	private int numberOfKills;
	private int rank;
	private World world;
	private List<Location> signLocations = new ArrayList<Location>();
	private boolean redstonePoweredSign;

	private List<StatStore> stats;

	public MasterMobHunterData() {
	}

	public MasterMobHunterData(int id, StatType statType, TimePeriod period,
			int numberOfKills, int rank, boolean redstonePowered) {
		this.id = id;
		this.statType = statType;
		this.period = period;
		this.numberOfKills = numberOfKills;
		this.rank = rank;
		this.world = null;
		this.signLocations.clear();
		this.redstonePoweredSign = redstonePowered;
	}

	public MasterMobHunterData(int id) {
		this.id = id;
		this.statType = StatType.KillsTotal;
		this.period = TimePeriod.AllTime;
		this.numberOfKills = 0;
		this.rank = 1;
		this.world = null;
		this.signLocations.clear();
		this.redstonePoweredSign = false;
	}

	public int getId() {
		return this.id;
	}

	public StatType getStatType() {
		return this.statType;
	}

	public void setStatType(StatType statType) {
		this.statType = statType;
	}

	public TimePeriod getPeriod() {
		return this.period;
	}

	public void setPeriod(TimePeriod period) {
		this.period = period;
	}

	public int getNumberOfKills() {
		return this.numberOfKills;
	}

	public void setNumberOfKills(int numberOfKills) {
		this.numberOfKills = numberOfKills;
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public List<Location> getLocations() {
		return signLocations;
	}

	public void putLocation(Location location) {
		if (!this.signLocations.contains(location))
			this.signLocations.add(location);
	}

	public void removeLocation(Location location) {
		this.signLocations.remove(location);
	}

	public boolean isRedstonePoweredSign() {
		return this.redstonePoweredSign;
	}

	public void setRedstonePoweredSign(boolean redstonePoweredSign) {
		this.redstonePoweredSign = redstonePoweredSign;
	}

	// private boolean isLoaded(Block block) {
	// return (mLocation.getWorld().isChunkLoaded(block.getX() >> 4,
	// block.getZ() >> 4));
	// }

	// ***********************************************************************************
	// RequestStats / DataCallBack
	// ***********************************************************************************
	public void update() {
		MobHunting.instance.getDataStore().requestStats(statType, period, 25,
				this);
	}

	public List<StatStore> getCurrentStats() {
		if (stats == null)
			return Collections.emptyList();
		return stats;
	}

	@Override
	public void onCompleted(List<StatStore> data) {
		ArrayList<StatStore> altData = new ArrayList<StatStore>(data.size());
		for (StatStore stat : data) {
			if (stat.getAmount() != 0) {
				//MobHunting.debug("Stat=%s", stat);
				altData.add(stat);
			}
		}
		stats = altData;
		refresh();
	}

	@Override
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	public void refresh() {
		NPCRegistry n = CitizensAPI.getNPCRegistry();
		NPC npc = n.getById(id);
		if (npc != null) {
			MobHunting.debug("rank=%s stats.size()=%s", rank,stats.size());
			if (rank < stats.size() + 1) {
				if (rank != 0) {
					if (!stats.get(rank - 1).getPlayer().getName()
							.equals(npc.getName())) {
						npc.setName(stats.get(rank - 1).getPlayer().getName());
					}
					//MobHunting.debug("Set No of kills=%s", stats.get(rank-1).getAmount());
					this.numberOfKills = stats.get(rank - 1).getAmount();
				}
				if (signLocations.size() > 0) {
					for (Location loc : signLocations) {
						if (loc.getBlock().getState() instanceof org.bukkit.block.Sign) {
							org.bukkit.block.Sign s = (org.bukkit.block.Sign) loc
									.getBlock().getState();
							s.setLine(1, (this.rank + ". " + npc.getName()));
							s.setLine(2, (this.period.translateNameFriendly()));
							s.setLine(3, (stats.get(rank - 1).getAmount() + " " + this.statType
									.translateName()));
							s.update();
						} else {
							CitizensCompat.getManager().get(id)
									.removeLocation(loc);
						}
					}
				}
			}

		}
	}

	// ***************************************************************
	// write & read
	// ***************************************************************
	public void write(ConfigurationSection section) {
		section.set("id", id);
		section.set("stattype", statType.getDBColumn());
		section.set("period", period.toString());
		section.set("kills", numberOfKills);
		section.set("rank", rank);
		if (world != null)
			section.set("world", world.getUID().toString());
		if (signLocations.size() > 0)
			section.set("signs", signLocations);
		section.set("redstone_powered_sign", redstonePoweredSign);
	}

	@SuppressWarnings("unchecked")
	public void read(ConfigurationSection section)
			throws InvalidConfigurationException, IllegalStateException {
		id = Integer.valueOf(section.getString("id"));
		statType = StatType.fromColumnName(section.getString("stattype"));
		period = TimePeriod.parsePeriod(section.getString("period"));
		numberOfKills = Integer.valueOf(section.getInt("kills"));
		rank = Integer.valueOf(section.getInt("rank"));
		if (section.contains("world"))
			world = Bukkit
					.getWorld(UUID.fromString(section.getString("world")));
		if (section.contains("signs")) {
			signLocations = (List<Location>) section
					.get("signs", signLocations);
		}
		redstonePoweredSign = section.getBoolean("redstone_powered_sign");
	}

}
