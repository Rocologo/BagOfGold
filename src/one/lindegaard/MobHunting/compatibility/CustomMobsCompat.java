package one.lindegaard.MobHunting.compatibility;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import de.hellfirepvp.api.data.ICustomMob;
import de.hellfirepvp.api.event.CustomMobDeathEvent;
import de.hellfirepvp.api.event.CustomMobSpawnEvent;
import de.hellfirepvp.api.event.CustomMobSpawnEvent.SpawnReason;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MobPlugin;
import one.lindegaard.MobHunting.mobs.PluginManager;
import one.lindegaard.MobHunting.rewards.MobRewardData;

public class CustomMobsCompat implements Listener {

	// https://www.spigotmc.org/resources/custommobs.7339/

	private static boolean supported = false;
	private static Plugin mPlugin;
	private static HashMap<String, MobRewardData> mMobRewardData = new HashMap<String, MobRewardData>();
	private File file = new File(MobHunting.getInstance().getDataFolder(), "custommobs-rewards.yml");
	private YamlConfiguration config = new YamlConfiguration();
	public static final String MH_CUSTOMMOBS = "MH:CUSTOMMOBS";

	public CustomMobsCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with CustomMobs is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("CustomMobs");

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getLogger().info("[MobHunting] Enabling Compatibility with CustomMobs ("
					+ getCustomMobs().getDescription().getVersion() + ")");

			supported = true;

			loadCustomMobsData();
			saveCustomMobsData();
		}
	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public void loadCustomMobsData() {
		try {
			if (!file.exists())
				return;

			config.load(file);
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				MobRewardData mob = new MobRewardData();
				mob.read(section);
				mob.setMobType(key);
				mMobRewardData.put(key, mob);
				try {
					if (mMobRewardData.size() > 0)
						MobHunting.getStoreManager().insertCustomMobs(key);
				} catch (SQLException e) {
					Messages.debug("Error on creating CustomMobs in Database");
					e.printStackTrace();
				}
			}
			Messages.debug("Loaded %s CustomMobs", mMobRewardData.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}

	public void loadCustomMobsData(String key) {
		try {
			if (!file.exists())
				return;

			config.load(file);
			ConfigurationSection section = config.getConfigurationSection(key);
			MobRewardData mob = new MobRewardData();
			mob.read(section);
			mob.setMobType(key);
			mMobRewardData.put(key, mob);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		try {
			if (mMobRewardData.size() > 0)
				MobHunting.getStoreManager().insertCustomMobs(key);
		} catch (SQLException e) {
			Messages.debug("Error on creating CustomMobs in Database");
			e.printStackTrace();
		}
	}

	public void saveCustomMobsData() {
		try {
			config.options().header("This a extra MobHunting config data for the CustomMobs on your server.");

			if (mMobRewardData.size() > 0) {

				int n = 0;
				for (String str : mMobRewardData.keySet()) {
					ConfigurationSection section = config.createSection(str);
					mMobRewardData.get(str).save(section);
					n++;
				}

				if (n != 0) {
					Messages.debug("Saving Mobhunting extra CustomMobs data.");
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCustomMobsData(String key) {
		try {
			if (mMobRewardData.containsKey(key)) {
				ConfigurationSection section = config.createSection(key);
				mMobRewardData.get(key).save(section);
				Messages.debug("Saving Mobhunting extra CustomMobs data.");
				config.save(file);
			} else {
				Messages.debug("ERROR! CustomMobs ID (%s) is not found in mNPCData", key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static Plugin getCustomMobs() {
		return mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isCustomMob(Entity killed) {
		return killed.hasMetadata(MH_CUSTOMMOBS);
	}

	public static String getCustomMobType(Entity killed) {
		List<MetadataValue> data = killed.getMetadata(MH_CUSTOMMOBS);
		MetadataValue value = data.get(0);
		return ((MobRewardData) value.value()).getMobType();
	}

	public static HashMap<String, MobRewardData> getMobRewardData() {
		return mMobRewardData;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationCustomMobs;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationCustomMobs;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCustomMobDeathEvent(CustomMobDeathEvent event) {
		// ICustomMob mob = event.getMob(); // The ICustomMob that was killed.
		// Player killer = event.getKiller(); // The Player that killed the
		// // CustomMob;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCustomMobSpawnEvent(CustomMobSpawnEvent event) {
		// The ICustomMob that spawned.
		ICustomMob mob = event.getMob();

		// The LivingEntity instance that was spawned.
		LivingEntity entity = event.getEntity();

		Messages.debug("CustomMobSpawnEvent: MinecraftMobtype=%s CustomMobName=%s", entity.getType(), mob.getName());

		// Specific reason why the mob was spawned
		CustomMobSpawnEvent.SpawnReason reason = event.getReason();
		if (reason.equals(SpawnReason.SPAWNER) && !MobHunting.getConfigManager().allowCustomMobsSpawners) {
			entity.setMetadata("MH:blocked", new FixedMetadataValue(MobHunting.getInstance(), true));
			// Block spawner = event.getSpawner();
			// Is only defined when the spawnReason is SPAWNER.
		}

		if (mMobRewardData != null && !mMobRewardData.containsKey(mob.getName()))

		{
			Messages.debug("New CustomMobName found=%s,%s", mob.getName(), mob.getDisplayName());
			mMobRewardData.put(mob.getName(),
					new MobRewardData(MobPlugin.CustomMobs, mob.getName(), mob.getDisplayName(), "10",
							"minecraft:give {player} iron_sword 1", "You got an Iron sword.", 1));
			saveCustomMobsData(mob.getName());
			try {
				MobHunting.getStoreManager().insertCustomMobs(mob.getName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		entity.setMetadata(MH_CUSTOMMOBS, new FixedMetadataValue(mPlugin, mMobRewardData.get(mob.getName())));
	}

}
