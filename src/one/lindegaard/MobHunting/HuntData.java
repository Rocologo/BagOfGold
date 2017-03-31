package one.lindegaard.MobHunting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Location;

import one.lindegaard.MobHunting.grinding.Area;

public class HuntData {

	MobHunting instance;
	private int killStreak = 0;
	private int dampenedKills = 0;
	private static double cDampnerRange = MobHunting.getConfigManager().grindingDetectionRange;
	private Location lastKillAreaCenter;
	private ArrayList<Area> lastGridingAreas = new ArrayList<Area>();
	private double reward = 0;
	private HashMap<String, Double> modifiers = new HashMap<String, Double>();

	public HuntData(MobHunting instance) {
		this.instance = instance;
	}

	public Area getGrindingArea(Location location) {
		for (Area area : lastGridingAreas) {
			if (area.center.getWorld().equals(location.getWorld())) {
				if (area.center.distance(location) < area.range) {
					Messages.debug("Players HuntData contain a Grinding Area: (%s,%s,%s,%s)",
							area.center.getWorld().getName(), area.center.getBlockX(), area.center.getBlockY(),
							area.center.getBlockZ());
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

			if (area.center.getWorld().equals(location.getWorld())) {
				if (area.center.distance(location) < area.range)
					it.remove();
			}
		}
	}

	public void recordGrindingArea() {
		for (Area area : lastGridingAreas) {
			if (lastKillAreaCenter.getWorld().equals(area.center.getWorld())) {
				double dist = lastKillAreaCenter.distance(area.center);

				double remaining = dist;
				remaining -= area.range;
				remaining -= cDampnerRange;

				if (remaining < 0) {
					if (dist > area.range)
						area.range = dist;

					area.count += dampenedKills;

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

}
