package au.com.mineauz.MobHunting.compatability;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.event.CitizensDisableEvent;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCDespawnEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.event.PlayerCreateNPCEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.MobPlugins;
import au.com.mineauz.MobHunting.MobRewardData;
import au.com.mineauz.MobHunting.npc.MasterMobHunterManager;
import au.com.mineauz.MobHunting.npc.MobHuntingTrait;
import au.com.mineauz.MobHunting.npc.MasterMobHunterData;

public class CitizensCompat implements Listener {

	private static boolean supported = false;
	private static CitizensPlugin mPlugin;
	private static HashMap<String, MobRewardData> mMobRewardData = new HashMap<String, MobRewardData>();
	private File fileMobRewardData = new File(
			MobHunting.instance.getDataFolder(), "citizens-rewards.yml");
	private YamlConfiguration config = new YamlConfiguration();

	private static MasterMobHunterManager masterMobHunterManager = new MasterMobHunterManager();

	public CitizensCompat() {
		if (isDisabledInConfig()) {
			MobHunting.instance.getLogger().info(
					"Compatability with Citizens2 is disabled in config.yml");
		} else {
			mPlugin = (CitizensPlugin) Bukkit.getPluginManager().getPlugin(
					"Citizens");

			// Register MobHunting Trait with Citizens.
			net.citizensnpcs.api.CitizensAPI.getTraitFactory().registerTrait(
					net.citizensnpcs.api.trait.TraitInfo.create(
							MobHuntingTrait.class).withName("MasterMobHunter"));

			// wait 5 seconds or until Citizens is fully loaded.
			MobHunting.instance
					.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(MobHunting.instance,
							new Runnable() {
								public void run() {

									MobHunting.instance
											.getLogger()
											.info("Enabling compatability with Citizens ("
													+ getCitizensPlugin()
															.getDescription()
															.getVersion() + ")");

									supported = true;

									loadCitizensData();
									saveCitizensData();

									masterMobHunterManager.initialize();
									masterMobHunterManager.saveData();

									findMissingNPC();

								}
							}, 20 * 10); // 20ticks/sec * 5 sec.

		}
	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public void loadCitizensData() {
		try {
			if (!fileMobRewardData.exists())
				return;
			MobHunting.debug("Loading Sentry Traits.");

			config.load(fileMobRewardData);
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config
						.getConfigurationSection(key);
				MobRewardData mrd = new MobRewardData();
				mrd.read(section);
				mMobRewardData.put(key, mrd);
			}
			MobHunting.debug("Loaded %s Sentry Traits.", mMobRewardData.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveCitizensData() {
		try {
			config.options()
					.header("This a extra MobHunting config data for the Citizens/NPC's on your server.");

			if (mMobRewardData.size() > 0) {

				int n = 0;
				for (String key : mMobRewardData.keySet()) {
					ConfigurationSection section = config.createSection(key);
					mMobRewardData.get(key).save(section);
					n++;
				}

				if (n != 0) {
					MobHunting.debug(
							"Saving %s Sentry Trait Reward data to file.",
							mMobRewardData.size());
					config.save(fileMobRewardData);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveCitizensData(String key) {
		try {
			if (mMobRewardData.containsKey(key)) {
				ConfigurationSection section = config.createSection(key);
				mMobRewardData.get(key).save(section);
				MobHunting.debug("Saving Sentry Trait Reward data for ID=%s.",
						key);
				config.save(fileMobRewardData);
			} else {
				MobHunting.debug(
						"ERROR! Mob ID (%s) is not found in mMobRewardData",
						key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static void shutdown() {
		if (supported) {
			masterMobHunterManager.shutdown();
		}
	}

	public CitizensPlugin getCitizensPlugin() {
		return mPlugin;
	}

	public static boolean isCitizensSupported() {
		return supported;
	}

	public static MasterMobHunterManager getManager() {
		return masterMobHunterManager;
	}

	public static boolean isNPC(Entity entity) {
		if (isCitizensSupported())
			return CitizensAPI.getNPCRegistry().isNPC(entity);
		else
			return false;
	}

	public static int getNPCId(Entity entity) {
		return CitizensAPI.getNPCRegistry().getNPC(entity).getId();
	}

	public static String getNPCName(Entity entity) {
		return CitizensAPI.getNPCRegistry().getNPC(entity).getName();
	}

	public static NPC getNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().getNPC(entity);
	}

	public static boolean isSentry(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity))
			return CitizensAPI
					.getNPCRegistry()
					.getNPC(entity)
					.hasTrait(
							CitizensAPI.getTraitFactory().getTraitClass(
									"Sentry"));
		else
			return false;
	}

	public static boolean isMasterMobHunter(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
			NPC npc = CitizensCompat.getNPC(entity);
			return (npc.hasTrait(MobHuntingTrait.class));
		} else
			return false;
	}

	public static HashMap<String, MobRewardData> getMobRewardData() {
		return mMobRewardData;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.config().disableIntegrationCitizens;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.config().disableIntegrationCitizens;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDeathEvent(NPCDeathEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDamageEvent(NPCDamageEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDamageByEntityEvent(NPCDamageByEntityEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensEnableEvent(CitizensEnableEvent event) {

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensDisableEvent(CitizensDisableEvent event) {
		// MobHunting.debug("CitizensDisableEvent - saving");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCSpawnEvent(NPCSpawnEvent event) {
		NPC npc = event.getNPC();
		// NPCRegistry n = CitizensAPI.getNPCRegistry();
		// for (Iterator<NPC> npcList = n.iterator(); npcList.hasNext();) {
		// NPC npc = npcList.next();
		if (npc.getId() == event.getNPC().getId()) {
			MobHunting.debug("NPC=%s was spawned: ID=%s", npc.getName(),
					npc.getId());
			if (isSentry(npc.getEntity())) {
				if (mMobRewardData != null
						&& !mMobRewardData.containsKey(String.valueOf(npc
								.getId()))) {
					MobHunting.debug("A new Sentry NPC found. ID=%s,%s",
							npc.getId(), npc.getName());
					mMobRewardData.put(String.valueOf(npc.getId()),
							new MobRewardData(
									MobPlugins.MobPluginNames.Citizens, "npc",
									npc.getFullName(), "10",
									"give {player} iron_sword 1",
									"You got an Iron sword.", 100, 100));
					saveCitizensData(String.valueOf(npc.getId()));
				}
			} else if (isMasterMobHunter(npc.getEntity())) {
				if (!masterMobHunterManager.contains(npc.getId())) {
					MobHunting.debug(
							"A New MasterMobHunter NPC found. ID=%s,%s",
							npc.getId(), npc.getName());
					masterMobHunterManager.put(npc.getId(),
							new MasterMobHunterData(npc.getId()));
					masterMobHunterManager.saveData(npc.getId());
				}
			} else {
				MobHunting
						.debug("The spawned NPC was not Sentry and MasterMobHunter. Traits=s%",
								npc.getTraits().toString());
			}
		}
		// }
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDespawnEvent(NPCDespawnEvent event) {
		// MobHunting.debug("NPCDespawnEvent");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerCreateNPCEvent(PlayerCreateNPCEvent event) {
		// MobHunting.debug("NPCCreateNPCEvent");
	}

	private void findMissingNPC() {
		NPCRegistry n = CitizensAPI.getNPCRegistry();
		for (Iterator<NPC> npcList = n.iterator(); npcList.hasNext();) {
			NPC npc = npcList.next();
			if (isSentry(npc.getEntity())) {
				if (mMobRewardData != null
						&& !mMobRewardData.containsKey(String.valueOf(npc
								.getId()))) {
					MobHunting.debug("A new Sentry NPC found. ID=%s,%s",
							npc.getId(), npc.getName());
					mMobRewardData.put(String.valueOf(npc.getId()),
							new MobRewardData(
									MobPlugins.MobPluginNames.Citizens, "npc",
									npc.getFullName(), "10",
									"give {player} iron_sword 1",
									"You got an Iron sword.", 100, 100));
					saveCitizensData(String.valueOf(npc.getId()));
				}
			} else if (isMasterMobHunter(npc.getEntity())) {
				if (!masterMobHunterManager.contains(npc.getId())) {
					MobHunting.debug(
							"A New MasterMobHunter NPC found. ID=%s,%s",
							npc.getId(), npc.getName());
					masterMobHunterManager.put(npc.getId(),
							new MasterMobHunterData(npc.getId()));
					masterMobHunterManager.saveData(npc.getId());
				}
			}
		}
	}

}
