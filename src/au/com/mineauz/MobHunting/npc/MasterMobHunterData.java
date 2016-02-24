package au.com.mineauz.MobHunting.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class MasterMobHunterData {

	private int id;
	private StatType statType;
	private TimePeriod period;
	private int numberOfKills;
	private int rank;
	private World world;
	private List<Location> signLocations = new ArrayList<Location>();
	private boolean redstonePoweredSign;

	public MasterMobHunterData() {
	}

	public MasterMobHunterData(int id, StatType statType, TimePeriod period, int numberOfKills,
			int rank, boolean redstonePowered) {
		this.id = id;
		this.statType = statType;
		this.period = period;
		this.numberOfKills=numberOfKills;
		this.rank = rank;
		this.world = null;
		this.signLocations.clear();
		this.redstonePoweredSign = redstonePowered;
	}

	public MasterMobHunterData(int id) {
		this.id = id;
		this.statType = StatType.KillsTotal;
		this.period = TimePeriod.AllTime;
		this.numberOfKills=0;
		this.rank = 0;
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

	public List<Location> getLocation() {
		return signLocations;
	}

	public void addLocation(Location location) {
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

	public void save(ConfigurationSection section) {
		section.set("id", id);
		if (statType != null)
			section.set("stattype", statType.getDBColumn());
		section.set("period", period.translateName());
		section.set("kills",numberOfKills);
		section.set("rank", rank);
		if (world != null)
			section.set("world", world.getUID().toString());
		if (!signLocations.isEmpty())
			section.set("signs", signLocations);
			//section.createSection("signs");
			//for (int i=0; i < signLocations.size(); i++){
			//	section.set("", signLocations.get(i).toVector());
			//}
		section.set("redstone_powered_sign", redstonePoweredSign);
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	public void read(ConfigurationSection section)
			throws InvalidConfigurationException, IllegalStateException {
		id = Integer.valueOf(section.getString("id"));
		if (section.contains("stattype"))
			statType.fromColumnName(section.getString("stattype"));
		else
			statType = StatType.KillsTotal;
		if (section.contains("period"))
			period = TimePeriod.parsePeriod(section.getString("period"));
		else
			period = TimePeriod.AllTime;
		numberOfKills=Integer.valueOf(section.getInt("kills"));
		rank = Integer.valueOf(section.getInt("rank"));
		MobHunting.debug("reading rank = %s for id=%s", rank,id);
		if (section.contains("world"))
			world = Bukkit
					.getWorld(UUID.fromString(section.getString("world")));
		if (section.contains("signs")) {
			signLocations = (List<Location>) section.get("signs",signLocations);
			//Vector pos = section.getVector("position");
			//signLocation.add(pos);
		}
		redstonePoweredSign = section.getBoolean("redstone_powered_sign");
	}

}
