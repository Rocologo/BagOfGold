package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import net.elseland.xikage.MythicMobs.MythicMobs;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobSpawnEvent;
import net.elseland.xikage.MythicMobs.API.Exceptions.InvalidMobTypeException;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MobPlugin;
import one.lindegaard.MobHunting.rewards.MobRewardData;

public class MythicMobsV251Compat implements Listener {

	private static Plugin mPlugin;

	public MythicMobsV251Compat() {
		mPlugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	private static MythicMobs getMythicMobsV251() {
		return (MythicMobs) mPlugin;
	}

	public static boolean isMythicMobV251(String killed) {
		if (MythicMobsCompat.isSupported())
			return getMythicMobV251(killed) != null;
		return false;
	}

	public static MythicMob getMythicMobV251(String killed) {
		if (MythicMobsCompat.isSupported())
			try {
				return getMythicMobsV251().getAPI().getMobAPI().getMythicMob(killed);
			} catch (InvalidMobTypeException e) {
				e.printStackTrace();
			}
		return null;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onMythicMobV251SpawnEvent(MythicMobSpawnEvent event) {
		String mobtype = event.getMobType().getInternalName();
		Messages.debug("MythicMobSpawnEvent: MinecraftMobtype=%s MythicMobType=%s", event.getLivingEntity().getType(),
				mobtype);
		if (!MythicMobsCompat.getMobRewardData().containsKey(mobtype)) {
			Messages.debug("New MythicMobType found=%s (%s)", mobtype, event.getMobType().getDisplayName());
			MythicMobsCompat.getMobRewardData().put(mobtype,
					new MobRewardData(MobPlugin.MythicMobs, mobtype, event.getMobType().getDisplayName(), "10",
							"minecraft:give {player} iron_sword 1", "You got an Iron sword.", 1));
			MythicMobsCompat.saveMythicMobsData(mobtype);
			MobHunting.getStoreManager().insertMissingMythicMobs(mobtype);
			// Update mob loaded into memory
			MobHunting.getExtendedMobManager().updateExtendedMobs();
			Messages.injectMissingMobNamesToLangFiles();
		}

		event.getLivingEntity().setMetadata(MythicMobsCompat.MH_MYTHICMOBS, new FixedMetadataValue(mPlugin,
				MythicMobsCompat.getMobRewardData().get(event.getMobType().getInternalName())));
	}
	
	private void onMythicMobV251DeathEvent(MythicMobDeathEvent event) {
		
	}

}
