package one.lindegaard.BagOfGold.compatibility;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import one.lindegaard.BagOfGold.BagOfGold;

public class EssentialsCompat {

	BagOfGold plugin;
	private static Essentials mPlugin;
	private static boolean supported = false;

	public EssentialsCompat() {
		if (!isEnabledInConfig()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ "Compatibility with Essentials is disabled in config.yml");
			this.plugin = BagOfGold.getInstance();
		} else {
			mPlugin = (Essentials) Bukkit.getPluginManager().getPlugin(CompatPlugin.Essentials.getName());

			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RESET
					+ "Enabling compatibility with Essentials (" + getEssentials().getDescription().getVersion() + ")");
			if (mPlugin.getDescription().getVersion().compareTo("2.17.0") <= 0
					&& BagOfGold.getAPI().getEconomyManager().getEconomyAPI().endsWith("Reserve"))
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
						+ "This version of Essentials is not compatible with Reserve. You have 2 economy providers: Essentials and BagOfGold.");
			supported = true;
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static Essentials getEssentials() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isEnabledInConfig() {
		return BagOfGold.getInstance().getConfigManager().enableIntegrationEssentials;
	}

	public static boolean isGodModeEnabled(Player player) {
		if (isSupported()) {
			User user = getEssentials().getUser(player);
			return user.isGodModeEnabled();
		}
		return false;
	}

	public static boolean isVanishedModeEnabled(Player player) {
		if (isSupported()) {
			User user = getEssentials().getUser(player);
			return user.isVanished();
		}
		return false;
	}

	public static double getBalance(Player player) {
		double bal = mPlugin.getOfflineUser(player.getName()).getMoney().doubleValue();
		return bal;
	}

	public static double getEssentialsBalance(OfflinePlayer offlinePlayer) {
		if (supported) {
			if (EssentialsCompat.getEssentials().getOfflineUser(offlinePlayer.getName()) != null) {
				String uuid = EssentialsCompat.getEssentials().getOfflineUser(offlinePlayer.getName()).getConfigUUID()
						.toString();
				File datafolder = EssentialsCompat.getEssentials().getDataFolder();
				if (datafolder.exists()) {
					File configfile = new File(datafolder + "/userdata/" + uuid + ".yml");
					if (configfile.exists()) {
						YamlConfiguration config = new YamlConfiguration();
						try {
							config.load(configfile);
						} catch (IOException | InvalidConfigurationException e) {
							e.printStackTrace();
							return 0;
						}
						return Double.valueOf(config.getString("money", "0"));
					}
				}
			}
		}
		return 0;
	}

	public static void setEssentialsBalance(OfflinePlayer offlinePlayer, double amount) {
		if (supported) {
			String uuid = EssentialsCompat.getEssentials().getOfflineUser(offlinePlayer.getName()).getConfigUUID()
					.toString();
			File datafolder = EssentialsCompat.getEssentials().getDataFolder();
			if (datafolder.exists()) {
				File configfile = new File(datafolder + "/userdata/" + uuid + ".yml");
				if (configfile.exists()) {
					YamlConfiguration config = new YamlConfiguration();
					try {
						config.load(configfile);
						config.set("money", String.valueOf(amount));
						config.save(configfile);
						BagOfGold.getAPI().getMessages().debug("Updated %s essentials balance to %s",
								offlinePlayer.getName(), BagOfGold.getAPI().getEconomyManager().format(amount));
					} catch (IOException | InvalidConfigurationException e) {
						e.printStackTrace();
						return;
					}
				}
			}
		}
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

}
