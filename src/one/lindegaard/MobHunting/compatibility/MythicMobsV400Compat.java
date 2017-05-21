package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MobPlugin;
import one.lindegaard.MobHunting.rewards.RewardData;

public class MythicMobsV400Compat implements Listener {

	private static Plugin mPlugin;

	public MythicMobsV400Compat() {
		mPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	private static MythicMobs getMythicMobsV400() {
		return (MythicMobs) mPlugin;
	}

	public static boolean isMythicMobV400(String killed) {
		if (MythicMobsCompat.isSupported())
			return getMythicMobV400(killed) != null;
		return false;
	}

	public static MythicMob getMythicMobV400(String killed) {
		if (MythicMobsCompat.isSupported())
			return getMythicMobsV400().getAPIHelper().getMythicMob(killed);
		return null;
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
	private void onMythicMobV400SpawnEvent(MythicMobSpawnEvent event) {
		String mobtype = event.getMobType().getInternalName();
		Messages.debug("MythicMobSpawnEvent: MythicMobType=%s", mobtype);

		if (!MythicMobsCompat.getMobRewardData().containsKey(mobtype)) {
			Messages.debug("New MythicMobType found=%s (%s)", mobtype, event.getMobType().getDisplayName());
			MythicMobsCompat.getMobRewardData().put(mobtype,
					new RewardData(MobPlugin.MythicMobs, mobtype, event.getMobType().getDisplayName(), "10",
							"minecraft:give {player} iron_sword 1", "You got an Iron sword.", 1, 1, 0.02));
			MythicMobsCompat.saveMythicMobsData(mobtype);
			MobHunting.getStoreManager().insertMissingMythicMobs(mobtype);
			// Update mob loaded into memory
			MobHunting.getExtendedMobManager().updateExtendedMobs();
			Messages.injectMissingMobNamesToLangFiles();
		}

		event.getEntity().setMetadata(MythicMobsCompat.MH_MYTHICMOBS, new FixedMetadataValue(mPlugin,
				MythicMobsCompat.getMobRewardData().get(event.getMobType().getInternalName())));
	}

	@SuppressWarnings("unused")
	private void onMythicMobV400DeathEvent(MythicMobDeathEvent event) {

	}

}
