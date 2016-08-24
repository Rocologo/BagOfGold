package one.lindegaard.MobHunting.compatibility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.MobPlugins;
import one.lindegaard.MobHunting.rewards.MobRewardData;
import me.eccentric_nz.tardisweepingangels.TARDISWeepingAngelSpawnEvent;
import me.eccentric_nz.tardisweepingangels.TARDISWeepingAngels;
import me.eccentric_nz.tardisweepingangels.utils.Monster;

public class TARDISWeepingAngelsCompat implements Listener {

	private static Plugin mPlugin;
	private static boolean supported = false;
	private static HashMap<String, MobRewardData> mMobRewardData = new HashMap<String, MobRewardData>();
	private File file = new File(MobHunting.getInstance().getDataFolder(), "TARDISWeepingAngels-rewards.yml");
	private YamlConfiguration config = new YamlConfiguration();
	public static final String MH_TARDISWEEPINGANGELS = "MH:TARDISWeepingAngels";

	// http://dev.bukkit.org/bukkit-plugins/tardisweepingangels/

	public TARDISWeepingAngelsCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with TARDISWeepingAngels is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("TARDISWeepingAngels");

			if (mPlugin != null) {
				Bukkit.getLogger().info("[MobHunting] Enabling compatibility with TARDISWeepingAngelsAPI ("
						+ mPlugin.getDescription().getVersion() + ")");

				supported = true;

				Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

				loadTARDISWeepingAngelsMobsData();
				saveTARDISWeepingAngelsMobsData();
			}
		}
	}

	// **************************************************************************
	// OTHER
	// **************************************************************************

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationTARDISWeepingAngels;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationTARDISWeepingAngels;
	}

	/**
	 * Returns whether an entity is a TARDISWeepingAngels entity.
	 *
	 * @param entity
	 *            the entity to check
	 * @return true if the entity is a TARDISWeepingAngels entity
	 */
	public static boolean isWeepingAngelMonster(Entity entity) {
		return entity.hasMetadata(TARDISWeepingAngelsCompat.MH_TARDISWEEPINGANGELS);
		//return ((TARDISWeepingAngels) mPlugin).getWeepingAngelsAPI().isWeepingAngelMonster(entity);
	}
	
	/**
	 * Returns the Monster type for a TARDISWeepingAngels entity.
	 *
	 * @param entity
	 *            the entity to get the Monster type for
	 * @return the Monster type or null if it is not TARDISWeepingAngels entity
	 */
	public static Monster getWeepingAngelMonsterType(Entity entity) {
		return ((TARDISWeepingAngels) mPlugin).getWeepingAngelsAPI().getWeepingAngelMonsterType(entity);
	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public void loadTARDISWeepingAngelsMobsData() {
		try {
			if (!file.exists()) {
				for (Monster monster : Monster.getValues()) {
					mMobRewardData.put(monster.name(),
							new MobRewardData(MobPlugins.MobPluginNames.TARDISWeepingAngels, monster.name(),
									monster.name(), "40:60", "minecraft:give {player} iron_sword 1",
									"You got an Iron sword.", 1));
					saveTARDISWeepingAngelsMobsData(mMobRewardData.get(monster.name()).getMobName());
				}
				return;
			}

			config.load(file);
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				MobRewardData mob = new MobRewardData();
				mob.read(section);
				mob.setMobType(key);
				mMobRewardData.put(key, mob);
			}
			Messages.debug("Loaded %s TARDISWeepingAngels-Mobs", mMobRewardData.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void loadTARDISWeepingAngelsMobsData(String key) {
		try {
			if (!file.exists()) {
				return;
			}

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
	}

	public void saveTARDISWeepingAngelsMobsData() {
		try {
			config.options().header("This a extra MobHunting config data for the TARDISWeepingAngels on your server.");

			if (mMobRewardData.size() > 0) {

				int n = 0;
				for (String str : mMobRewardData.keySet()) {
					ConfigurationSection section = config.createSection(str);
					mMobRewardData.get(str).save(section);
					n++;
				}

				if (n != 0) {
					Messages.debug("Saving Mobhunting extra TARDISWeepingAngels data.");
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveTARDISWeepingAngelsMobsData(String key) {
		try {
			if (mMobRewardData.containsKey(key)) {
				ConfigurationSection section = config.createSection(key);
				mMobRewardData.get(key).save(section);
				Messages.debug("Saving extra TARDISWeepingAngels data for mob=%s", key);
				config.save(file);
			} else {
				Messages.debug("ERROR! TARDISWeepingAngels ID (%s) is not found in mNPCData", key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onTARDISWeepingAngelSpawnEvent(TARDISWeepingAngelSpawnEvent event) {

		Entity entity = event.getEntity();
		Monster monster = getWeepingAngelMonsterType(entity);

		Messages.debug("TARDISWeepingAngelSpawnEvent: MinecraftMobtype=%s WeepingAngelsMobType=%s",
				event.getEntityType(), monster.name());

		if (mMobRewardData != null && !mMobRewardData.containsKey(monster.name())) {
			Messages.debug("New TARDIS mob found=%s", monster.name());
			mMobRewardData.put(monster.name(),
					new MobRewardData(MobPlugins.MobPluginNames.TARDISWeepingAngels, monster.name(), monster.name(),
							"40:60", "minecraft:give {player} iron_sword 1", "You got an Iron sword.", 1));
			saveTARDISWeepingAngelsMobsData(monster.name());
		}

		event.getEntity().setMetadata(MH_TARDISWEEPINGANGELS,
				new FixedMetadataValue(mPlugin, mMobRewardData.get(monster.name())));
	}

}
