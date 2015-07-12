package au.com.mineauz.MobHunting.compatability;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import au.com.mineauz.MobHunting.Config;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.leaderboard.Leaderboard;
import au.com.mineauz.MobHunting.leaderboard.LegacyLeaderboard;
import de.Keyle.MyPet.api.entity.MyPetEntity;
import de.Keyle.MyPet.entity.types.MyPetType;
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

public class CitizensCompat implements Listener {

	private static boolean supported = false;
	private static CitizensPlugin mPlugin;
	//private static Configuration config;
	private HashMap<Integer, NPCData> mNPCData = new HashMap<Integer, NPCData>();

	public CitizensCompat() {
		mPlugin = (CitizensPlugin) Bukkit.getPluginManager().getPlugin(
				"Citizens");

		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

		MobHunting.instance.getLogger().info(
				"Enabling compatability with Citizens ("
						+ getCitizensPlugin().getDescription().getVersion()
						+ ")");
		supported = true;

		loadCitizensData();
		//saveCitizensData();
	}

	public void saveCitizensData() {
		try {
			File file = new File(MobHunting.instance.getDataFolder(),
					"citizens.yml");
			YamlConfiguration config = new YamlConfiguration();
			config.options()
					.header("This a extra MobHunting config data for the NPCs on your server.");

			if (mNPCData.size() > 0) {
				int n = 0;
				for (int i = n; i < mNPCData.size(); i++) {
					MobHunting.instance.getLogger().info("i=" + i);
					ConfigurationSection section = config.createSection(String
							.valueOf(i));
					mNPCData.get(i).save(section);
					n++; 
				}

				if (n != 0) {
					MobHunting.instance.getLogger().info(
							"Saving Mobhunting extra NPC data.");
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadCitizensData() {
		try {
			File file = new File(MobHunting.instance.getDataFolder(),
					"citizens.yml");

			if (!file.exists())
				return;

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);
			
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config
						.getConfigurationSection(key);
				NPCData npc = new NPCData();
				npc.read(section);
				mNPCData.put(Integer.valueOf(key), npc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

	}

	public static CitizensPlugin getCitizensPlugin() {
		return mPlugin;
	}

	public static boolean isCitizensSupported() {
		return supported;
	}

	public static boolean isNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().isNPC(entity);
	}

	public static boolean isSentry(Entity entity) {
		return CitizensAPI
				.getNPCRegistry()
				.getNPC(entity)
				.hasTrait(CitizensAPI.getTraitFactory().getTraitClass("Sentry"));
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDeathEvent(NPCDeathEvent event) {
		MobHunting
				.debug("NPCDeathEvent, type=%s, fullname=%s, name=%s hasTrait(Sentry)=%s",
						event.getNPC().getEntity().getType(),
						event.getNPC().getFullName(),
						event.getNPC().getName(),
						event.getNPC().hasTrait(
								CitizensAPI.getTraitFactory().getTraitClass(
										"Sentry")));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDamageEvent(NPCDamageEvent event) {
		MobHunting
				.debug("NPCDamageEvent, type=%s, fullname=%s, name=%s hasTrait(Sentry)=%s",
						event.getNPC().getEntity().getType(),
						event.getNPC().getFullName(),
						event.getNPC().getName(),
						event.getNPC().hasTrait(
								CitizensAPI.getTraitFactory().getTraitClass(
										"Sentry")));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDamageByEntityEvent(NPCDamageByEntityEvent event) {
		MobHunting
				.debug("NPCDamageByEntityEvent, type=%s, fullname=%s, name=%s hasTrait(Sentry)=%s",
						event.getNPC().getEntity().getType(),
						event.getNPC().getFullName(),
						event.getNPC().getName(),
						event.getNPC().hasTrait(
								CitizensAPI.getTraitFactory().getTraitClass(
										"Sentry")));
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensEnableEvent(CitizensEnableEvent event) {
		//MobHunting.debug("MobHunting Datafolder=%s", MobHunting.instance
		//		.getDataFolder().getName());
		//MobHunting.debug("Citizens Datafolder=%s", CitizensAPI.getDataFolder()
		//		.getName());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensDisableEvent(CitizensDisableEvent event) {
		MobHunting.debug("CitizensDisableEvent - saving");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCSpawnEvent(NPCSpawnEvent event) {
		MobHunting.debug("NPCSpawnEvent");
		
		for (Iterator<NPCRegistry> registry = CitizensAPI.getNPCRegistries()
				.iterator(); registry.hasNext();) {
			NPCRegistry n = registry.next();
			for (Iterator<NPC> npcList = n.iterator(); npcList.hasNext();) {
				NPC npc = npcList.next();
				MobHunting.debug("NPC=%s", npc.getFullName());

				if (mNPCData != null && !mNPCData.containsKey(npc.getId())) {
					MobHunting.debug("New NPC found=%s,%s", npc.getId(),npc.getFullName());
					mNPCData.put(npc.getId(), new NPCData(npc.getFullName(), 10,
							"give {player} diamond_sword 1", "You got a diamond sword.", 100));
				}
			}
		}

		//MobHunting.debug("Size of nNPCData = %s", mNPCData.size());

		saveCitizensData();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onNPCDespawnEvent(NPCDespawnEvent event) {
		MobHunting.debug("NPCDespawnEvent");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerCreateNPCEvent(PlayerCreateNPCEvent event) {
		MobHunting.debug("NPCDespawnEvent");
	}

	// @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	@SuppressWarnings("unused")
	private void onWolfKillMob(EntityDeathEvent event) {
		if (!MobHunting.isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) event
				.getEntity().getLastDamageCause();

		if (!(dmg.getDamager() instanceof MyPetEntity))
			return;

		MyPetEntity killer = (MyPetEntity) dmg.getDamager();

		if (killer.getPetType() != MyPetType.Wolf)
			return;

		if (killer.getOwner() != null) {
			Player owner = killer.getOwner().getPlayer();

			if (owner != null && MobHunting.isHuntEnabled(owner)) {
				MobHunting.instance.getAchievements().awardAchievementProgress(
						"fangmaster", owner, 1); //$NON-NLS-1$
			}
		}
	}

}
