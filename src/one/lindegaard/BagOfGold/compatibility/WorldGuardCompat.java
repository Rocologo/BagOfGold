package one.lindegaard.BagOfGold.compatibility;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import one.lindegaard.CustomItemsLib.server.Servers;
import one.lindegaard.CustomItemsLib.compatibility.CompatPlugin;
import one.lindegaard.BagOfGold.BagOfGold;

public class WorldGuardCompat {

	private static boolean supported = false;
	private static WorldGuardPlugin mPlugin;

	public WorldGuardCompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender()
					.sendMessage(BagOfGold.PREFIX_WARNING + "Compatibility with WorldGuard is disabled in config.yml");
		} else {
			mPlugin = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin(CompatPlugin.WorldGuard.getName());
			if (Servers.isMC113OrNewer()) {
				if (mPlugin.getDescription().getVersion().compareTo("7.0.0") >= 0) {
					Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "Enabling compatibility with WorldGuard ("
							+ mPlugin.getDescription().getVersion() + ")");
					supported = true;
				} else if (mPlugin.getDescription().getVersion().compareTo("1.16") >= 0) {
					Bukkit.getConsoleSender()
							.sendMessage(BagOfGold.PREFIX + "Enabling compatibility with FastAsyncWorldGuard ("
									+ mPlugin.getDescription().getVersion() + ")");
					supported = true;
				} else {
					Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX_WARNING
							+ "Your current version of WorldGuard (" + mPlugin.getDescription().getVersion()
							+ ") is not supported by BagOfGold. BagOfGold 6.x does only support 7.0.0 beta 1 and newer.");
				}
			} else {
				if (mPlugin.getDescription().getVersion().compareTo("6.0.0") >= 0) {
					Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "Enabling compatibility with WorldGuard ("
							+ mPlugin.getDescription().getVersion() + ")");
					supported = true;
				} else {
					Bukkit.getConsoleSender()
							.sendMessage(BagOfGold.PREFIX_WARNING + "Your current version of WorldGuard ("
									+ mPlugin.getDescription().getVersion()
									+ ") is not supported by BagOfGold. BagOfGold does only support 6.0.0 newer.");
				}
			}

		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static WorldGuardPlugin getWorldGuardPlugin() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationWorldGuard;
	}

	public static String returnRegion(Player player, String regionId) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		LocalPlayer localPlayer = getWorldGuardPlugin().wrapPlayer(player);
		// RegionManager regions = container.get(localPlayer.getWorld());
		RegionQuery query = container.createQuery();
		ApplicableRegionSet set = query.getApplicableRegions(localPlayer.getLocation());
		if (set.size() == 1) {
			BagOfGold.getInstance().getMessages().debug("1 region");
			// player is standing on a location with single region
			ProtectedRegion region = set.getRegions().iterator().next();
			if (regionId == null)
				return region.getId();
			else
				return region.getId().equalsIgnoreCase(regionId) ? region.getId() : null;
		} else {
			BagOfGold.getInstance().getMessages().debug("Set.size=%s", set.size());
			Iterator<ProtectedRegion> i = set.getRegions().iterator();
			while (i.hasNext()) {
				ProtectedRegion pr = i.next();
				BagOfGold.getInstance().getMessages().debug("id=%s, regionId=%s", pr.getId(), regionId);
				if (pr.getId().equalsIgnoreCase(regionId)) {
					return pr.getId();
				}
			}

		}
		return null;
	}

}
