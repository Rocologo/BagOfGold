package one.lindegaard.MobHunting.compatibility;

import org.bukkit.entity.Player;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyUniverse;

import one.lindegaard.MobHunting.Messages;

public class TownyHelper {

	public static boolean isInHomeTome(Player player) {
		if (TownyCompat.isSupported()) {
			Resident resident;
			Town homeTown = null;
			try {
				resident = TownyUniverse.getDataSource().getResident(player.getName());
			} catch (NotRegisteredException ex) {
				// Messages.debug("Could not find the Resident (%s)",
				// player.getName());
				return false;
			}

			if (resident != null) {
				try {
					homeTown = resident.getTown();
				} catch (NotRegisteredException e) {
					// Messages.debug("%s has no town", player.getName());
					return false;
				}
			}

			TownBlock tb = TownyUniverse.getTownBlock(player.getLocation());
			if (tb != null) {
				// Location is within a town
				try {
					Messages.debug("%s is in a town (%s)", player.getName(), tb.getTown().getName());
				} catch (NotRegisteredException e) {
				}
			} else {
				// Messages.debug("The player is not in a town");
				return false;
			}

			try {
				// Check if the town is the residents town.
				if (tb.getTown().equals(homeTown)) {
					// check if town is protected against mob damage
					TownyPermission p1 = homeTown.getPermissions();
					Boolean protected_mob = p1.mobs;
					Messages.debug("%s is in his HomeTown. Mob spawns:%s", player.getName(),
							protected_mob ? "On" : "Off");
					return true;
				} else {
					// Messages.debug("%s is not in his home town",
					// player.getName());
					return false;
				}
			} catch (NotRegisteredException e) {
				return false;
			}
		}
		return false;
	}

}
