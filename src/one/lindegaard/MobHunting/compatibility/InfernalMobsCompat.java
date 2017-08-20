package one.lindegaard.MobHunting.compatibility;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import io.hotmail.com.jacob_vejvoda.infernal_mobs.infernal_mobs;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MinecraftMob;

public class InfernalMobsCompat implements Listener {

	// https://www.spigotmc.org/resources/infernal-mobs.2156/

	private static boolean supported = false;
	private static Plugin mPlugin;
	private static HashMap<String, Double> mMobRewardData = new HashMap<String, Double>();
	private static infernal_mobs api;
	public static final String MH_INFERNALMOBS = "MH:INFERNALMOBS";

	public InfernalMobsCompat() {
		if (isDisabledInConfig()) {
			Bukkit.getConsoleSender()
					.sendMessage("[MobHunting] Compatibility with InfernalMobs is disabled in config.yml");
		} else {
			mPlugin = Bukkit.getPluginManager().getPlugin(CompatPlugin.InfernalMobs.getName());

			Bukkit.getPluginManager().registerEvents(this, MobHunting.getInstance());

			Bukkit.getConsoleSender().sendMessage("[MobHunting] Enabling Compatibility with InfernalMobs ("
					+ getInfernalMobs().getDescription().getVersion() + ")");

			api = (infernal_mobs) mPlugin;

			loadInfernalMobsData();

			MobHunting.getStoreManager().insertInfernalMobs();
			Messages.injectMissingMobNamesToLangFiles();

			supported = true;
		}
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public static infernal_mobs getInfernalMobs() {
		return (infernal_mobs) mPlugin;
	}

	public static boolean isSupported() {
		return supported;
	}

	public static boolean isInfernalMob(Entity entity) {
		if (isSupported())
			return entity.hasMetadata(MH_INFERNALMOBS) || api.idSearch(entity.getUniqueId()) != -1;
		return false;
	}

	public static HashMap<String, Double> getMobRewardData() {
		return mMobRewardData;
	}

	public static boolean isDisabledInConfig() {
		return MobHunting.getConfigManager().disableIntegrationInfernalMobs;
	}

	public static boolean isEnabledInConfig() {
		return !MobHunting.getConfigManager().disableIntegrationInfernalMobs;
	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public static void loadInfernalMobsData() {
		for (MinecraftMob mob : MinecraftMob.values()) {
			String key = mob.getName();
			mMobRewardData.put(key, 1.0);
		}
		Messages.debug("Loaded %s InfernalMobs", mMobRewardData.size());
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onInfernalMobDeathEvent(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (isInfernalMob(entity)) {
			if (api.findMobAbilities(entity.getUniqueId()) != null)
				entity.setMetadata(MH_INFERNALMOBS,
						new FixedMetadataValue(MobHunting.getInstance(), api.findMobAbilities(entity.getUniqueId())));
		}
	}

	public static int getProgressAchievementLevel1(String mobtype) {
		MinecraftMob mob = MinecraftMob.valueOf(mobtype);
		if (mob != null)
			return mob.getProgressAchievementLevel1();
		else
			return 100;
	}

}
