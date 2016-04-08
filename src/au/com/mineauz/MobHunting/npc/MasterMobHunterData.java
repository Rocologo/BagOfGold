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
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.IDataCallback;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class MasterMobHunterData implements IDataCallback<List<StatStore>> {

	private int id;
	private StatType statType;
	private TimePeriod period;
	private int numberOfKills;
	private int rank;
	private World world;
	private List<Location> signLocations = new ArrayList<Location>();

	private List<StatStore> stats;

	public MasterMobHunterData() {
	}

	public MasterMobHunterData(int id, StatType statType, TimePeriod period,
			int numberOfKills, int rank) {
		this.id = id;
		this.statType = statType;
		this.period = period;
		this.numberOfKills = numberOfKills;
		this.rank = rank;
		this.world = null;
		this.signLocations.clear();
	}

	public MasterMobHunterData(int id) {
		this.id = id;
		this.statType = StatType.KillsTotal;
		this.period = TimePeriod.AllTime;
		this.numberOfKills = 0;
		this.rank = 1;
		this.world = null;
		this.signLocations.clear();
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

	private boolean isLoaded(Block block) {
		return (block.getWorld().isChunkLoaded(block.getX() >> 4,
				block.getZ() >> 4));
	}

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
			if (stat.getAmount() != 0 && stat.getPlayer().getName() != null) {
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
			if (rank < stats.size() + 1) {
				if (rank != 0) {
					if (!stats.get(rank - 1).getPlayer().getName()
							.equals(npc.getName())) {
						npc.setName(stats.get(rank - 1).getPlayer().getName());
					}
					this.numberOfKills = stats.get(rank - 1).getAmount();
				}
				if (signLocations.size() > 0) {
					for (Location loc : signLocations) {
						Block sb = loc.getBlock();
						if (isLoaded(sb)) {
							if (MasterMobhunterSign.isSign(sb)) {
								org.bukkit.block.Sign s = (org.bukkit.block.Sign) sb.getState();
								s.setLine(1, (this.rank + ". " + npc.getName()));
								s.setLine(2,
										(this.period.translateNameFriendly()));
								s.setLine(3, (stats.get(rank - 1).getAmount()
										+ " " + this.statType.translateName()));
								s.update();
								if (MasterMobhunterSign.isMHSign(sb))
									MasterMobhunterSign.setPower(sb, MasterMobhunterSign.POWER_FROM_SIGN);
							} else {
								loc.zero();
							}
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
	}

}
