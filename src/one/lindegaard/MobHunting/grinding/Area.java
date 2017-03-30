package one.lindegaard.MobHunting.grinding;

import org.bukkit.Location;

public class Area {
	public Location center;
	public double range;
	public int count;
	
	public Area (Location location, double range, int count){
		center=location;
		this.range=range;
		this.count = count;
	}

	/**
	 * @return location of the center of the Area
	 */
	public Location getCenter() {
		return center;
	}

	/**
	 * @param location of the center of the Area
	 */
	public void setCenter(Location location) {
		this.center = location;
	}

	/**
	 * @return the range
	 */
	public double getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(double range) {
		this.range = range;
	}

	/**
	 * @return the count
	 */
	public int getCounter() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}
}	
