package one.lindegaard.BagOfGold;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * @author Rocologo
 *
 */
public class WorldGroupManager {

	private BagOfGold plugin;
	private File file;
	private YamlConfiguration config = new YamlConfiguration();
	private HashMap<String, List<String>> worldGroups = new HashMap<String, List<String>>();
	private HashMap<String, GameMode> defaultGameMode = new HashMap<String, GameMode>();
	private HashMap<String, Double> startBalance = new HashMap<String, Double>();

	public WorldGroupManager(BagOfGold plugin) {
		this.plugin = plugin;
		file = new File(plugin.getDataFolder(), "worldgroups.yml");
		load();
		if (worldGroups.isEmpty()) {
			// TODO: check if worldgroups is the same as PerWorldInventory and MyPet if
			// supported.
			/**
			 * if (PerWorldInventoryCompat.isSupported()) { YamlConfiguration pwi_groups =
			 * PerWorldInventoryCompat.getWorldsFile(); ConfigurationSection section =
			 * pwi_groups.getConfigurationSection("groups"); for (String wg :
			 * section.getKeys(false)) { plugin.getMessages().debug("PWI wg=%s",
			 * wg); @SuppressWarnings("unchecked") List<String> worlds = (List<String>)
			 * section.get(wg + ".worlds"); worldGroups.put(wg, worlds); GameMode gamemode =
			 * GameMode.valueOf((String) section.get(wg + ".default-gamemode"));
			 * defaultGameMode.put(wg, gamemode); double startingBalance =
			 * section.getDouble(wg + ".starting-balance"); startBalance.put(wg,
			 * startingBalance); } } else
			 **/
			{
				worldGroups.put(getDefaultWorldgroup(), Arrays.asList("world", "world_nether", "world_the_end"));
				startBalance.put(getDefaultWorldgroup(), 3000.0);
				defaultGameMode.put(getDefaultWorldgroup(), GameMode.SURVIVAL);

				worldGroups.put("survival", Arrays.asList("survival"));
				startBalance.put("survival", 3000.0);
				defaultGameMode.put("survival", GameMode.SURVIVAL);

				worldGroups.put("creative", Arrays.asList("creative"));
				startBalance.put("creative", 3000.0);
				defaultGameMode.put("creative", GameMode.CREATIVE);
			}
		} else {
			// check if startBalance and defaultGamemode exists for all worldGroups
			for (String wg : worldGroups.keySet()) {
				if (!startBalance.containsKey(wg))
					startBalance.put(wg, 3000.0);
				if (!defaultGameMode.containsKey(wg))
					if (wg.equalsIgnoreCase("creative"))
						defaultGameMode.put(wg, GameMode.CREATIVE);
					else
						defaultGameMode.put(wg, GameMode.SURVIVAL);
			}
		}
		save();
	}

	public void add(String world) {
		List<String> list = worldGroups.get(getDefaultWorldgroup() + ".worlds");
		if (!list.contains(world))
			list.add(world);
		worldGroups.put(getDefaultWorldgroup() + ".worlds", list);
	}

	public void add(String world, String worldGroup) {
		List<String> list = worldGroups.get(worldGroup);
		if (!list.contains(world))
			list.add(world);
		worldGroups.put(worldGroup, list);
	}

	public List<String> getDefaultWorlds() {
		return worldGroups.get(getDefaultWorldgroup());
	}

	public String getDefaultWorldgroup() {
		return "default";
	}

	public GameMode getDefaultGameMode() {
		return defaultGameMode.get(getDefaultWorldgroup());
	}

	public double getDefaultStartingBalance() {
		return startBalance.get(getDefaultWorldgroup());
	}

	public List<String> getWorlds(String worldGroup) {
		return worldGroups.get(worldGroup);
	}

	public String getWorldGroup(String world) {
		for (Entry<String, List<String>> worldGroup : worldGroups.entrySet()) {
			if (worldGroup.getValue().contains(world))
				return worldGroup.getKey();
		}
		worldGroups.get(getDefaultWorldgroup()).add(world);
		save();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED + " World:" + world
				+ " was missing in the worldgroups.yml file. It has beed added to the default group. Please review the worldgroups.");
		return getDefaultWorldgroup();
	}

	public String getCurrentWorldGroup(OfflinePlayer player) {
		if (player.isOnline())
			return getWorldGroup(((Player) player).getWorld().getName());
		else
			return getDefaultWorldgroup();
	}

	public GameMode getCurrentGameMode(OfflinePlayer player) {
		if (player.isOnline())
			return ((Player) player).getGameMode();
		else
			return getDefaultGameMode();
	}

	public double getCurrentStartingBalance(OfflinePlayer player) {
		if (player.isOnline())
			return startBalance.get(getWorldGroup(((Player) player).getWorld().getName()));
		else
			return getDefaultStartingBalance();
	}

	public double getCurrentStartingBalance(String worldgroup) {
		return startBalance.get(worldgroup);
	}

	// ***************************************************************
	// write & read
	// ***************************************************************
	public void save() {
		try {
			config.options()
					.header("----------------------------------------------------------"
							+ "\nWorldGroups. New worlds are added in the Default Group"
							+ "\n----------------------------------------------------------"
							+ "\nThese worldgroups which worlds share the players "
							+ "\ntheir economy balance and bank-balance. If you use"
							+ "\nMyPet, PerWorldInventory or similar, the world " //
							+ "\nshould be the grouped the same way here." + "\n"
							+ "\nBounties in MobHunting is shared the same way." + "\n");
			// plugin.getMessages().debug("Saving worldGroups");

			Set<String> groups = worldGroups.keySet();
			ConfigurationSection section = config.createSection("groups");
			for (String wg : groups) {
				section.set(wg + ".worlds", worldGroups.get(wg));
				section.set(wg + ".default-gamemode", defaultGameMode.get(wg).toString());
				section.set(wg + ".starting-balance", startBalance.get(wg));
			}
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void load() {
		if (!file.exists()) {
			File fileMobHunting = new File(plugin.getDataFolder(), "../MobHunting/worldgroups.yml");
			if (!fileMobHunting.exists()) {
				return;
			} else {
				// plugin.getMessages().debug("Copy WorldGroups from MobHunting.");
				try {
					config.load(fileMobHunting);
				} catch (IllegalStateException | InvalidConfigurationException | IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			// plugin.getMessages().debug("Loading WorldGroups from BagOfGoldCore.");
			try {
				config.load(file);
			} catch (IllegalStateException | InvalidConfigurationException | IOException e) {
				e.printStackTrace();
			}

		}

		ConfigurationSection section = config.getConfigurationSection("groups");
		for (String wg : section.getKeys(false)) {
			@SuppressWarnings("unchecked")
			List<String> worlds = (List<String>) section.get(wg + ".worlds");
			worldGroups.put(wg, worlds);
			GameMode gamemode = GameMode.valueOf((String) section.get(wg + ".default-gamemode"));
			defaultGameMode.put(wg, gamemode);
			double startingBalance = section.getDouble(wg + ".starting-balance");
			startBalance.put(wg, startingBalance);
		}
	}

}
