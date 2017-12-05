package one.lindegaard.BagOfGold.compatibility;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.CitizensPlugin;
import net.citizensnpcs.api.event.CitizensDisableEvent;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.bank.BagOfGoldBankerTrait;
import one.lindegaard.BagOfGold.util.Misc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CitizensCompat implements Listener {
	
	private BagOfGold plugin;

	private boolean supported = false;
	private CitizensPlugin citizensAPI;
	//public static final String MH_CITIZENS = "BG:CITIZENS";

	public CitizensCompat(BagOfGold plugin) {
		this.plugin=plugin;
		if (isDisabledInConfig()) {
			Bukkit.getConsoleSender()
					.sendMessage("[MobHunting] Compatibility with Citizens2 is disabled in config.yml");
		} else {
			citizensAPI = (CitizensPlugin) Bukkit.getPluginManager().getPlugin(CompatPlugin.Citizens.getName());
			if (citizensAPI == null)
				return;

			TraitInfo trait = TraitInfo.create(BagOfGoldBankerTrait.class).withName("BagOfGoldBanker");
			citizensAPI.getTraitFactory().registerTrait(trait);
			Bukkit.getConsoleSender().sendMessage("[MobHunting] Enabling compatibility with Citizens2 ("
					+ getCitizensPlugin().getDescription().getVersion() + ")");

			Bukkit.getPluginManager().registerEvents(this, plugin);

		}
	}

	// **************************************************************************
	// LOAD & SAVE
	// **************************************************************************
	public static void loadCitizensData() {

	}

	public static void saveCitizensData() {

	}

	public void saveCitizensData(String key) {

	}

	// **************************************************************************
	// OTHER FUNCTIONS
	// **************************************************************************
	public void shutdown() {
		if (supported) {
			TraitInfo trait = TraitInfo.create(BagOfGoldBankerTrait.class).withName("BagOfGoldBanker");
			if (Misc.isMC18OrNewer())
				citizensAPI.getTraitFactory().deregisterTrait(trait);
		}
	}

	public CitizensPlugin getCitizensPlugin() {
		return citizensAPI;
	}

	public boolean isSupported() {
		if (supported && citizensAPI != null && CitizensAPI.hasImplementation())
			return supported;
		else
			return false;
	}

	public boolean isNPC(Entity entity) {
		if (isSupported())
			return citizensAPI.getNPCRegistry().isNPC(entity);
		return false;
	}

	public boolean isNPC(Integer id) {
		if (isSupported())
			return citizensAPI.getNPCRegistry().getById(id) != null;
		return false;
	}

	public int getNPCId(Entity entity) {
		return citizensAPI.getNPCRegistry().getNPC(entity).getId();
	}

	public String getNPCName(Entity entity) {
		return citizensAPI.getNPCRegistry().getNPC(entity).getName();
	}

	public NPC getNPC(Entity entity) {
		return citizensAPI.getNPCRegistry().getNPC(entity);
	}

	public boolean isSentryOrSentinelOrSentries(Entity entity) {
		if (isNPC(entity))
			return citizensAPI.getNPCRegistry().getNPC(entity)
					.hasTrait(citizensAPI.getTraitFactory().getTraitClass("Sentry"))
					|| citizensAPI.getNPCRegistry().getNPC(entity)
							.hasTrait(citizensAPI.getTraitFactory().getTraitClass("Sentinel"))
					|| citizensAPI.getNPCRegistry().getNPC(entity)
							.hasTrait(citizensAPI.getTraitFactory().getTraitClass("Sentries"));
		return false;
	}

	public boolean isSentryOrSentinelOrSentries(String mobtype) {
		if (isNPC(Integer.valueOf(mobtype)))
			return citizensAPI.getNPCRegistry().getById(Integer.valueOf(mobtype))
					.hasTrait(citizensAPI.getTraitFactory().getTraitClass("Sentry"))
					|| citizensAPI.getNPCRegistry().getById(Integer.valueOf(mobtype))
							.hasTrait(citizensAPI.getTraitFactory().getTraitClass("Sentinel"))
					|| citizensAPI.getNPCRegistry().getById(Integer.valueOf(mobtype))
							.hasTrait(citizensAPI.getTraitFactory().getTraitClass("Sentries"));
		else
			return false;
	}

	public boolean isDisabledInConfig() {
		return plugin.getConfigManager().disableIntegrationCitizens;
	}

	public boolean isEnabledInConfig() {
		return !plugin.getConfigManager().disableIntegrationCitizens;
	}

	// **************************************************************************
	// EVENTS
	// **************************************************************************

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensEnableEvent(CitizensEnableEvent event) {
		plugin.getMessages().debug("Citizens2 was enabled");

		supported = true;

		loadCitizensData();
		saveCitizensData();
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onCitizensDisableEvent(CitizensDisableEvent event) {

	}

}