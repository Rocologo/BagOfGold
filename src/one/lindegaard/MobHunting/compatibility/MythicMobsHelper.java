package one.lindegaard.MobHunting.compatibility;

import net.elseland.xikage.MythicMobs.API.Exceptions.InvalidMobTypeException;

public class MythicMobsHelper {

	public static boolean isMythicMob(String killed) {
		if (MythicMobsCompat.isSupported())
			try {
				return MythicMobsCompat.getAPI().getMythicMob(killed) != null;
			} catch (InvalidMobTypeException e) {
				e.printStackTrace();
			}
		return false;
	}

}
