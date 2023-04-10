package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import one.lindegaard.CustomItemsLib.server.Servers;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.CustomItemsLib.compatibility.CompatPlugin;

public class WorldEditCompat {
	private static WorldEditPlugin mPlugin;
	private static boolean supported = false;

	public WorldEditCompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender()
					.sendMessage(BagOfGold.PREFIX + "Compatibility with WorldEdit is disabled in config.yml");
		} else {
			mPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin(CompatPlugin.WorldEdit.getName());
			if (Servers.isMC113OrNewer()) {
				if (mPlugin.getDescription().getVersion().compareTo("7.0.0") >= 0) {
					Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "Enabling compatibility with WorldEdit ("
							+ mPlugin.getDescription().getVersion() + ")");
					supported = true;
				} else if (mPlugin.getDescription().getVersion().compareTo("1.16") >= 0) {
					Bukkit.getConsoleSender()
							.sendMessage(BagOfGold.PREFIX + "Enabling compatibility with FastAsyncWorldEdit ("
									+ mPlugin.getDescription().getVersion() + ")");
					supported = true;
				} else {
					Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX_WARNING
							+ "Your current version of WorldEdit (" + mPlugin.getDescription().getVersion()
							+ ") is not supported by BagOfGold. BagOfGold 6.x does only support 7.0.0 and newer.");
				}
			} else {
				if (mPlugin.getDescription().getVersion().compareTo("6.1.0") >= 0) {
					Bukkit.getConsoleSender().sendMessage(BagOfGold.PREFIX + "Enabling compatibility with WorldEdit ("
							+ mPlugin.getDescription().getVersion() + ")");
					supported = true;
				} else {
					Bukkit.getConsoleSender()
							.sendMessage(BagOfGold.PREFIX_WARNING + "Your current version of WorldEdit ("
									+ mPlugin.getDescription().getVersion()
									+ ") is not supported by BagOfGold. BagOfGold does only support 6.1.0 and newer.");
				}
			}
		}
	}

	public static WorldEditPlugin getWorldEdit() {
		return mPlugin;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationWorldEdit;
	}

	public static boolean isSupported() {
		return supported;
	}
}
