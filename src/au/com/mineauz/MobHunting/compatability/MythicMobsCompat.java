package au.com.mineauz.MobHunting.compatability;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.MobType;
import de.Keyle.MyPet.api.entity.MyPetEntity;
import de.Keyle.MyPet.entity.types.MyPetType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.elseland.xikage.MythicMobs.API.Events.MythicMobCustomSkillEvent;
import net.elseland.xikage.MythicMobs.API.Events.MythicMobDeathEvent;
import net.elseland.xikage.MythicMobs.API.Events.MythicMobSkillEvent;
import net.elseland.xikage.MythicMobs.API.Events.MythicMobSpawnEvent;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;

public class MythicMobsCompat implements Listener {

	private static boolean supported = false;
	private static Plugin mPlugin;
	private static HashMap<String, NPCData> mNPCData = new HashMap<String, NPCData>();
	private File file = new File(MobHunting.instance.getDataFolder(),
			"mythicmobs-rewards.yml");
	private YamlConfiguration config = new YamlConfiguration();

	public MythicMobsCompat() {

		mPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs");

		Bukkit.getPluginManager().registerEvents(this, MobHunting.instance);

		MobHunting.instance.getLogger().info(
				"Enabling Compatability with MythicMobs ("
						+ getMythicMobs().getDescription().getVersion() + ")");
		// API:
		// http://xikage.elseland.net/viewgit/?a=tree&p=MythicMobs&h=dec796decd1ef71fdd49aed69aef85dc7d82b1c1&hb=ffeb51fb84e882365846a30bd2b9753716faf51e&f=MythicMobs/src/net/elseland/xikage/MythicMobs/API
		supported = true;

		loadMythicMobsData();
		saveMythicMobsData();

	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public void loadMythicMobsData() {
		try {
			if (!file.exists())
				return;

			config.load(file);
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config
						.getConfigurationSection(key);
				NPCData npc = new NPCData();
				npc.read(section);
				mNPCData.put(key, npc);
			}
			MobHunting.debug("Loaded %s MythicMobs", mNPCData.size());
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
			NPCData npc = new NPCData();
			npc.read(section);
			mNPCData.put(key, npc);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveMythicMobsData() {
		try {
			config.options()
					.header("This a extra MobHunting config data for the MythicMobs on your server.");

			if (mNPCData.size() > 0) {

				int n = 0;
				for (String str : mNPCData.keySet()) {
					ConfigurationSection section = config.createSection(str);
					mNPCData.get(str).save(section);
					n++;
				}

				if (n != 0) {
					MobHunting
							.debug("Saving Mobhunting extra MythicMobs data.");
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveMythicMobsData(String key) {
		try {
			if (mNPCData.containsKey(key)) {
				ConfigurationSection section = config.createSection(key);
				mNPCData.get(key).save(section);
				MobHunting.debug("Saving Mobhunting extra MythicMobs data.");
				config.save(file);
			} else {
				MobHunting.debug(
						"ERROR! MythicMobs ID (%s) is not found in mNPCData",
						key);
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

	public static boolean isMythicMobsSupported() {
		return supported;
	}

	public static HashMap<String, NPCData> getNPCData() {
		return mNPCData;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobDeathEvent(MythicMobDeathEvent event) {
		if (event.getKiller() != null)
			MobHunting
					.debug("MythicMob Death event, killer is %s, mobname=%s, Mobname=%s",
							event.getKiller().getName(), event.MobName,
							event.getMobType().MobName);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobSpawnEvent(MythicMobSpawnEvent event) {
		MobHunting.debug("MythicMob spawn event: name=%s Mobtype=%s", event
				.getLivingEntity().getName(), event.getMobType().MobName);

		if (mNPCData != null
				&& !mNPCData.containsKey(event.getMobType().MobName)) {
			MobHunting.debug("New MythicMobType found=%s,%s", event
					.getMobType().MobName, event.getMobType().getDisplayName());
			mNPCData.put(event.getMobType().MobName, new NPCData(
					MobType.MobPlugin.MythicMobs, event.getMobType()
							.getDisplayName(), "10",
					"give {player} iron_sword 1", "You got an Iron sword.",
					100, 100));
			saveMythicMobsData(event.getMobType().MobName);
		}

		event.getLivingEntity().setMetadata(
				"MH:MythicMob",
				new FixedMetadataValue(mPlugin,
						mNPCData.get(event.getMobType().MobName)));
		// MobHunting.debug("MythicMob Spawned-%s",
		// event.getLivingEntity().hasMetadata("MH:MythicMob"));

	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobSkillEvent(MythicMobSkillEvent event) {
		MobHunting.debug("MythicMob Skill event");
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onMythicMobCustomSkillEvent(MythicMobCustomSkillEvent event) {
		MobHunting.debug("MythicMob Custom Skill event");
	}

	// @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
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
