package one.lindegaard.MobHunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import one.lindegaard.MobHunting.grinding.Area;

public class HuntData{

	private static final String HUNTDATA = "MH:HuntData";
	
	private int killStreak;
	private int dampenedKills;
	private double cDampnerRange;
	private Location lastKillAreaCenter;
	private ArrayList<Area> lastGridingAreas;
	private double reward;
	private HashMap<String, Double> modifiers;

	public HuntData() {
		killStreak = 0;
		dampenedKills = 0;
		cDampnerRange = MobHunting.getConfigManager().grindingDetectionRange;
		lastKillAreaCenter = null;
		lastGridingAreas = new ArrayList<Area>();
		reward = 0;
		modifiers = new HashMap<String, Double>();

	}

	public HuntData(Player player) {
		killStreak = 0;
		dampenedKills = 0;
		cDampnerRange = MobHunting.getConfigManager().grindingDetectionRange;
		lastKillAreaCenter = null;
		lastGridingAreas = new ArrayList<Area>();
		reward = 0;
		modifiers = new HashMap<String, Double>();
		getHuntDataFromPlayer(player);
	}

	public void setHuntData(HuntData data) {
		killStreak = data.getKillStreak();
		dampenedKills = data.getDampenedKills();
		cDampnerRange = data.getcDampnerRange();
		lastKillAreaCenter = data.getLastKillAreaCenter();
		lastGridingAreas = data.getLastGridingAreas();
		reward = data.getReward();
		modifiers = data.getModifiers();
	}

	public Area getPlayerSpecificGrindingArea(Location location) {
		for (Area area : lastGridingAreas) {
			if (area.getCenter().getWorld().equals(location.getWorld())) {
				if (area.getCenter().distance(location) < area.getRange()) {
					Messages.debug("Found a blacklisted player specific grinding Area: (%s,%s,%s,%s)",
							area.getCenter().getWorld().getName(), area.getCenter().getBlockX(),
							area.getCenter().getBlockY(), area.getCenter().getBlockZ());
					return area;
				}
			}
		}

		return null;
	}

	public void clearGrindingArea(Location location) {
		Iterator<Area> it = lastGridingAreas.iterator();
		while (it.hasNext()) {
			Area area = it.next();

			if (area.getCenter().getWorld().equals(location.getWorld())) {
				if (area.getCenter().distance(location) < area.getRange())
					it.remove();
			}
		}
	}

	public void recordGrindingArea() {
		for (Area area : lastGridingAreas) {
			if (lastKillAreaCenter.getWorld().equals(area.getCenter().getWorld())) {
				double dist = lastKillAreaCenter.distance(area.getCenter());

				double remaining = dist;
				remaining -= area.getRange();
				remaining -= cDampnerRange;

				if (remaining < 0) {
					if (dist > area.getRange())
						area.setRange(dist);

					area.setCounter(dampenedKills + 1);

					return;
				}
			}
		}

		Area area = new Area(lastKillAreaCenter, cDampnerRange, dampenedKills);
		lastGridingAreas.add(area);
	}

	/**
	 * @return the lastKillAreaCenter
	 */
	public Location getLastKillAreaCenter() {
		return lastKillAreaCenter;
	}

	/**
	 * @param lastKillAreaCenter
	 *            the lastKillAreaCenter to set
	 */
	public void setLastKillAreaCenter(Location lastKillAreaCenter) {
		this.lastKillAreaCenter = lastKillAreaCenter;
	}

	/**
	 * Gets the basic reward in cash - without multipliers
	 * 
	 * @return
	 */
	public double getReward() {
		return reward;
	}

	/**
	 * Set the basic reward for this kill.
	 * 
	 * @param reward
	 */
	public void setReward(double reward) {
		this.reward = reward;
	}

	/**
	 * Gets a HashMap containing the names and modifiers/multipliers for this
	 * kill.
	 * 
	 * @return
	 */
	public HashMap<String, Double> getModifiers() {
		return modifiers;
	}

	/**
	 * Sets the names and the modifiers/multipliers
	 * 
	 * @param modifiers
	 */
	public void setModifiers(HashMap<String, Double> modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Get number of dampended kills.
	 * 
	 * @returnnumber of dampended kills
	 */
	public int getDampenedKills() {
		return dampenedKills;
	}

	/**
	 * Set number of dampended kills.
	 */
	public void setDampenedKills(int kills) {
		dampenedKills = kills;
	}

	/**
	 * Get the number of kills in a row.
	 * 
	 * @return
	 */
	public int getKillStreak() {
		return killStreak;
	}

	/**
	 * Set the number of kills in a row.
	 */
	public void setKillStreak(int kills) {
		killStreak = kills;
	}

	public ArrayList<Area> getLastGridingAreas() {
		return lastGridingAreas;
	}

	public void setLastGridingAreas(ArrayList<Area> lastGridingAreas) {
		this.lastGridingAreas = lastGridingAreas;
	}

	public int getKillstreakLevel() {
		if (killStreak < MobHunting.getConfigManager().killstreakLevel1)
			return 0;
		else if (killStreak < MobHunting.getConfigManager().killstreakLevel2)
			return 1;
		else if (killStreak < MobHunting.getConfigManager().killstreakLevel3)
			return 2;
		else if (killStreak < MobHunting.getConfigManager().killstreakLevel4)
			return 3;
		else
			return 4;
	}

	/**
	 * Get the multiplier for the number of kills in a row
	 * 
	 * @return
	 */
	public double getKillstreakMultiplier() {
		int level = getKillstreakLevel();

		switch (level) {
		case 0:
			return 1.0;
		case 1:
			return MobHunting.getConfigManager().killstreakLevel1Mult;
		case 2:
			return MobHunting.getConfigManager().killstreakLevel2Mult;
		case 3:
			return MobHunting.getConfigManager().killstreakLevel3Mult;
		default:
			return MobHunting.getConfigManager().killstreakLevel4Mult;
		}
	}

	/**
	 * Gets the multiplier for a Dampend kill.
	 * 
	 * @return The first 10 kills = 1, 10% less per kill, after 20 kill the
	 *         multiplier is 0
	 */
	public double getDampnerMultiplier() {
		if (dampenedKills < 10)
			return 1.0;
		else if (dampenedKills < 20)
			return (1 - ((dampenedKills - 10) / 10.0));
		else
			return 0;
	}

	public double getcDampnerRange() {
		return cDampnerRange;
	}

	public void addModifier(String name, double modifier) {
		modifiers.put(name, modifier);
	}

	public void resetKillStreak(Player player) {
		killStreak = 0;
		putHuntDataToPlayer(player);
	}

	public double handleKillstreak(Player player) {
		int lastKillstreakLevel = getKillstreakLevel();
		killStreak++;
		putHuntDataToPlayer(player);
		
		// Killstreak can be disabled by setting the multiplier to 1
		double multiplier = getKillstreakMultiplier();
		if (multiplier != 1) {
			// Give a message notifying of killstreak increase
			if (getKillstreakLevel() != lastKillstreakLevel) {
				switch (getKillstreakLevel()) {
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
	 * get the HuntData() stored on the player.
	 * 
	 * @param player
	 * @return HuntData
	 */
	public void getHuntDataFromPlayer(Player player) {
		if (!player.hasMetadata(HUNTDATA)) {
			putHuntDataToPlayer(player);
		} else {
			HuntData data = new HuntData();
			List<MetadataValue> md = player.getMetadata(HUNTDATA);
			for (MetadataValue mdv : md) {
				if (mdv.value() instanceof HuntData) {
					data = (HuntData) mdv.value();
					break;
				}
			}
			setHuntData(data);
		}
	}

	public void putHuntDataToPlayer(Player player) {
		player.setMetadata(HUNTDATA, new FixedMetadataValue(MobHunting.getInstance(), this));
	}

}
