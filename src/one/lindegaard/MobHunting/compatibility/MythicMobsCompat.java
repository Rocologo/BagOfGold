package one.lindegaard.MobHunting.compatibility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import net.elseland.xikage.MythicMobs.MythicMobs;
import net.elseland.xikage.MythicMobs.API.IMobsAPI;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.*;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.StatType;
import one.lindegaard.MobHunting.mobs.ExtendedMobManager;
import one.lindegaard.MobHunting.mobs.MobPlugin;
import one.lindegaard.MobHunting.rewards.MobRewardData;

public class MythicMobsCompat implements Listener {

	private static boolean supported = false;
	private static Plugin mPlugin;
	private static HashMap<String, MobRewardData> mMobRewardData = new HashMap<String, MobRewardData>();
	private File file = new File(MobHunting.getInstance().getDataFolder(), "mythicmobs-rewards.yml");
	private YamlConfiguration config = new YamlConfiguration();
	public static final String MH_MYTHICMOBS = "MH:MYTHICMOBS";
	private static MythicMobs mythicMobs;
	private static IMobsAPI mobsAPI;

	public MythicMobsCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with MythicMobs is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
			if (mPlugin.getDescription().getVersion().compareTo("2.5.1") >= 0) {
				mythicMobs = (MythicMobs) mPlugin;
				mobsAPI = mythicMobs.getAPI().getMobAPI();
				Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

				Bukkit.getLogger().info("[MobHunting] Enabling Compatibility with MythicMobs ("
						+ getMythicMobs().getDescription().getVersion() + ")");
				// API:
				// http://xikage.elseland.net/viewgit/?a=tree&p=MythicMobs&h=dec796decd1ef71fdd49aed69aef85dc7d82b1c1&hb=ffeb51fb84e882365846a30bd2b9753716faf51e&f=MythicMobs/src/net/elseland/xikage/MythicMobs/API
				supported = true;

				loadMythicMobsData();
				saveMythicMobsData();
			} else {
				Bukkit.getLogger().warning(
						"[MobHunting] MythicMobs is outdated. Please update to V2.5.1 or newer. Integration will be disabled");
			}
		}
	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public void loadMythicMobsData() {
		try {
			if (!file.exists())
				return;
			Messages.debug("Loading extra MobRewards for Citizens MythicMobs mobs.");

			config.load(file);
			int n = 0;
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				if (MythicMobsHelper.isMythicMob(key)) {
					MobRewardData mob = new MobRewardData();
					mob.read(section);
					mob.setMobType(key);
					mMobRewardData.put(key, mob);
					MobHunting.getStoreManager().insertMissingMythicMobs(key);
					n++;
				} else {
					Messages.debug("The mob=%s cant be found in MythicMobs configuration files", key);
				}
			}
			Messages.debug("Loaded %s MythicMobs", n);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

	}

	public void loadMythicMobsData(String key) {
		try {
			if (!file.exists())
				return;

			config.load(file);
			ConfigurationSection section = config.getConfigurationSection(key);
			if (MythicMobsHelper.isMythicMob(key)) {
			 MobRewardData mob = new MobRewardData();
				mob.read(section);
				mob.setMobType(key);
				mMobRewardData.put(key, mob);
				int n = StatType.values().length;
				StatType.values()[n + 1] = new StatType(mob.getMobType() + "_kill", mob.getMobName());
				StatType.values()[n + 2] = new StatType(mob.getMobType() + "_assist", mob.getMobName());
				MobHunting.getStoreManager().insertMissingMythicMobs(key);
			} else {
				Messages.debug("The mob=%s cant be found in MythicMobs configuration files", key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveMythicMobsData() {
		try {
			config.options().header("This a extra MobHunting config data for the MythicMobs on your server.");

			if (mMobRewardData.size() > 0) {

				int n = 0;
				for (String str : mMobRewardData.keySet()) {
					ConfigurationSection section = config.createSection(str);
					mMobRewardData.get(str).save(section);
					n++;
				}

				if (n != 0) {
					Messages.debug("Saving Mobhunting extra MythicMobs data.");
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveMythicMobsData(String key) {
		try {
			if (mMobRewardData.containsKey(key)) {
				ConfigurationSection section = config.createSection(key);
				mMobRewardData.get(key).save(section);
				Messages.debug("Saving Mobhunting extra MythicMobs data.");
				config.save(file);
			} else {
				Messages.debug("ERROR! MythicMobs ID (%s) is not found in mMobRewardData", key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static Plugin getMythicMobs() {
		return mPlugin;
	}

	public static IMobsAPI getAPI() {
		return mobsAPI;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isMythicMob(Entity killed) {
		if (isSupported())
			// return mobsAPI.isMythicMob(killed);
			return killed.hasMetadata(MH_MYTHICMOBS);
		return false;
	}

	public static String getMythicMobType(Entity killed) {
		List<MetadataValue> data = killed.getMetadata(MH_MYTHICMOBS);
		MetadataValue value = data.get(0);
		return ((MobRewardData) value.value()).getMobType();
	}

	public static HashMap<String, MobRewardData> getMobRewardData() {
		return mMobRewardData;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationMythicmobs;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationMythicmobs;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onMythicMobDeathEvent(MythicMobDeathEvent event) {
		// Messages.debug("MythicMob spawn event: MinecraftMobtype=%s
		// MythicMobType=%s", event
		// .getLivingEntity().getType(), event.getMobType().getInternalName());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onMythicMobSpawnEvent(MythicMobSpawnEvent event) {
		String mobtype = event.getMobType().getInternalName();
		Messages.debug("MythicMobSpawnEvent: MinecraftMobtype=%s MythicMobType=%s", event.getLivingEntity().getType(),
				mobtype);
		if (!mMobRewardData.containsKey(mobtype)) {
			Messages.debug("New MythicMobType found=%s (%s)", mobtype, event.getMobType().getDisplayName());
			mMobRewardData.put(mobtype,
					new MobRewardData(MobPlugin.MythicMobs, mobtype, event.getMobType().getDisplayName(), "10",
							"minecraft:give {player} iron_sword 1", "You got an Iron sword.", 1));
			saveMythicMobsData(mobtype);
			MobHunting.getStoreManager().insertMissingMythicMobs(mobtype);
			// Update mob loaded into memory
			ExtendedMobManager.updateExtendedMobs();
			Messages.injectMissingMobNamesToLangFiles();
		}

		event.getLivingEntity().setMetadata(MH_MYTHICMOBS,
				new FixedMetadataValue(mPlugin, mMobRewardData.get(event.getMobType().getInternalName())));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobSkillEvent(MythicMobSkillEvent event) {
		// Messages.debug("MythicMob Skill event");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobCustomSkillEvent(MythicMobCustomSkillEvent event) {
		// Messages.debug("MythicMob Custom Skill event");
	}

}
