package one.lindegaard.MobHunting.mobs;

import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.CustomMobsCompat;
import one.lindegaard.MobHunting.compatibility.MythicMobsCompat;
import one.lindegaard.MobHunting.compatibility.TARDISWeepingAngelsCompat;

public class ExtendedMob {

	Integer mob_id; // The unique mob_id from mh_Mobs
	MobPlugin mobPlugin; // Plugin_id from mh_Plugins
	String mobtype; // mobtype NOT unique

	public ExtendedMob(Integer mob_id, MobPlugin mobPlugin, String mobtype) {
		this.mob_id = mob_id;
		this.mobPlugin = mobPlugin;
		this.mobtype = mobtype;
	}

	/**
	 * @return the mob_id
	 */
	public Integer getMob_id() {
		return mob_id;
	}

	/**
	 * @return the plugin_id
	 */
	public MobPlugin getMobPlugin() {
		return mobPlugin;
	}

	/**
	 * @return the mobtype
	 */
	public String getMobtype() {
		return mobtype;
	}

	/**
	 * @param mobtype
	 *            the mobtype to set
	 */
	public void setMobtype(String mobtype) {
		this.mobtype = mobtype;
	}

	@Override
	public int hashCode() {
		return mob_id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ExtendedMob))
			return false;

		ExtendedMob other = (ExtendedMob) obj;

		return mob_id.equals(other.mob_id);
	}

	@Override
	public String toString() {
		return String.format("MobStore: {mob_id: %s, plugin_id: %s, mobtype: %s}", this.mob_id, mobPlugin.name(),
				mobtype);
	}

	public String getName() {
		switch (mobPlugin.getId()) {
		case 1:
			// MythicMobs
			return MythicMobsCompat.getMobRewardData().get(mobtype).getMobName();
		case 2:
			// Citizens
			return CitizensCompat.getCitizensPlugin().getNPCRegistry().getById(Integer.valueOf(mobtype)).getName();
		case 3:
			// TARDISWeepingAngels
			return TARDISWeepingAngelsCompat.getMobRewardData().get(mobtype).getMobName();
		case 4:
			// CustomMobs
			return CustomMobsCompat.getMobRewardData().get(mobtype).getMobName();
		default:
			// Minecraft
			return mobtype;
		}
	}
}
