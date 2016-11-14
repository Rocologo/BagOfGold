package one.lindegaard.MobHunting.compatibility;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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
import net.citizensnpcs.api.trait.TraitInfo;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MobPlugin;
import one.lindegaard.MobHunting.mobs.PluginManager;
import one.lindegaard.MobHunting.npc.MasterMobHunter;
import one.lindegaard.MobHunting.npc.MasterMobHunterManager;
import one.lindegaard.MobHunting.npc.MasterMobHunterTrait;
import one.lindegaard.MobHunting.rewards.MobRewardData;
import one.lindegaard.MobHunting.util.Misc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CitizensCompat implements Listener {

	private static boolean supported = false;
	private static CitizensPlugin citizensAPI;
	private static HashMap<String, MobRewardData> mMobRewardData = new HashMap<String, MobRewardData>();
	private File fileMobRewardData = new File(MobHunting.getInstance().getDataFolder(), "citizens-rewards.yml");
	private YamlConfiguration config = new YamlConfiguration();

	private static MasterMobHunterManager masterMobHunterManager = new MasterMobHunterManager();

	public CitizensCompat() {
		initialize();
	}

	private void initialize() {
		if (isDisabledInConfig()) {
			Bukkit.getLogger().info("[MobHunting] Compatibility with Citizens2 is disabled in config.yml");
		} else {
			citizensAPI = (CitizensPlugin) Bukkit.getPluginManager().getPlugin("Citizens");
			if (citizensAPI == null)
				return;

			TraitInfo trait = TraitInfo.create(MasterMobHunterTrait.class).withName("MasterMobHunter");
			citizensAPI.getTraitFactory().registerTrait(trait);

			Bukkit.getLogger().info("[MobHunting] Enabling compatibility with Citizens ("
					+ getCitizensPlugin().getDescription().getVersion() + ")");

			supported = true;

			loadCitizensData();
			saveCitizensData();

			// wait x seconds or until Citizens is fully loaded.
			// TODO: wait until MasterMobHunterTrait is loaded.
			Bukkit.getScheduler().scheduleSyncDelayedTask(MobHunting.getInstance(), new Runnable() {
				public void run() {
					masterMobHunterManager.initialize();
					findMissingSentry();
					loadBountyDataForSentryOrSentinel();
				}
			}, 20 * 3); // 20ticks/sec * 10 sec.

		}
	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public void loadCitizensData() {
		try {
			if (!fileMobRewardData.exists())
				return;
			Messages.debug("Loading extra MobRewards.");

			config.load(fileMobRewardData);
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				MobRewardData mrd = new MobRewardData();
				mrd.read(section);
				mMobRewardData.put(key, mrd);
				try {
					if (mMobRewardData.size() > 0)
						MobHunting.getStoreManager().insertCitizensMobs(key);
				} catch (SQLException e) {
					Messages.debug("Error on creating Citizens in Database");
					e.printStackTrace();
				}
			}
			Messages.debug("Loaded %s extra MobRewards.", mMobRewardData.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}

	public void saveCitizensData() {
		try {
			config.options().header("This a extra MobHunting config data for the Citizens/NPC's on your server.");

			if (mMobRewardData.size() > 0) {

				int n = 0;
				for (String key : mMobRewardData.keySet()) {
					ConfigurationSection section = config.createSection(key);
					mMobRewardData.get(key).save(section);
					n++;
				}

				if (n != 0) {
					Messages.debug("Saving %s MobRewards to file.", mMobRewardData.size());
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
				Messages.debug("Saving Sentry/Sentinel Trait Reward data for ID=%s.", key);
				config.save(fileMobRewardData);
			} else {
				Messages.debug("ERROR! Sentry/Sentinel ID (%s) is not found in mMobRewardData", key);
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
			TraitInfo trait = TraitInfo.create(MasterMobHunterTrait.class).withName("MasterMobHunter");
			if (Misc.isMC18OrNewer())
				citizensAPI.getTraitFactory().deregisterTrait(trait);
		}
	}

	public static CitizensPlugin getCitizensPlugin() {
		return citizensAPI;
	}

	public static boolean isSupported() {
		if (supported && citizensAPI != null && CitizensAPI.hasImplementation())
			return supported;
		else
			return false;
	}

	public static MasterMobHunterManager getManager() {
		return masterMobHunterManager;
	}

	public static boolean isNPC(Entity entity) {
		if (isSupported())
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

	public static boolean isSentryOrSentinel(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity))
			return CitizensAPI.getNPCRegistry().getNPC(entity)
					.hasTrait(CitizensAPI.getTraitFactory().getTraitClass("Sentry"))
					|| CitizensAPI.getNPCRegistry().getNPC(entity)
							.hasTrait(CitizensAPI.getTraitFactory().getTraitClass("Sentinel"));
		                                                 //TODO: is it "sentinel" or "Sentinel"
		else
			return false;
	}

	public static HashMap<String, MobRewardData> getMobRewardData() {
		return mMobRewardData;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationCitizens;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationCitizens;
	}

	public void findMissingSentry() {
		NPCRegistry n = CitizensAPI.getNPCRegistry();
		for (Iterator<NPC> npcList = n.iterator(); npcList.hasNext();) {
			NPC npc = npcList.next();
			if (isSentryOrSentinel(npc.getEntity())) {
				if (mMobRewardData != null && !mMobRewardData.containsKey(String.valueOf(npc.getId()))) {
					Messages.debug("A new Sentinel or Sentry NPC was found. ID=%s,%s", npc.getId(), npc.getName());
					mMobRewardData.put(String.valueOf(npc.getId()),
							new MobRewardData(MobPlugin.Citizens, "npc", npc.getFullName(), "10",
									"give {player} iron_sword 1", "You got an Iron sword.", 1));
					saveCitizensData(String.valueOf(npc.getId()));
				}
			}
		}
	}

	private void loadBountyDataForSentryOrSentinel() {
		NPCRegistry n = CitizensAPI.getNPCRegistry();
		for (Iterator<NPC> npcList = n.iterator(); npcList.hasNext();) {
			NPC npc = npcList.next();
			if (isSentryOrSentinel(npc.getEntity())) {
				// MobHunting.getBountyManager().loadBounties(npc);
			}
		}
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
		Messages.debug("onCitizensEnableEvent:%s", event.getEventName());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensDisableEvent(CitizensDisableEvent event) {
		// Messages.debug("CitizensDisableEvent - saving");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCSpawnEvent(NPCSpawnEvent event) {
		NPC npc = event.getNPC();
		if (npc.getId() == event.getNPC().getId()) {
			if (isSentryOrSentinel(npc.getEntity())) {
				if (mMobRewardData != null && !mMobRewardData.containsKey(String.valueOf(npc.getId()))) {
					Messages.debug("A new Sentinel or Sentry NPC was found. ID=%s,%s", npc.getId(), npc.getName());
					mMobRewardData.put(String.valueOf(npc.getId()),
							new MobRewardData(MobPlugin.Citizens, "npc", npc.getFullName(), "0",
									"give {player} iron_sword 1", "You got an Iron sword.", 0));
					saveCitizensData(String.valueOf(npc.getId()));
					try {
						MobHunting.getStoreManager().insertCitizensMobs(String.valueOf(npc.getId()));
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (masterMobHunterManager.isMasterMobHunter(npc.getEntity())) {
				if (!masterMobHunterManager.contains(npc.getId())) {
					Messages.debug("A New MasterMobHunter NPC was found. ID=%s,%s", npc.getId(), npc.getName());
					masterMobHunterManager.put(npc.getId(), new MasterMobHunter(npc));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDespawnEvent(NPCDespawnEvent event) {
		// Messages.debug("NPCDespawnEvent");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerCreateNPCEvent(PlayerCreateNPCEvent event) {
		// Messages.debug("NPCCreateNPCEvent");
	}

}
