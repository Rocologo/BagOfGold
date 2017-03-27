package one.lindegaard.MobHunting.mobs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.CustomMobsCompat;
import one.lindegaard.MobHunting.compatibility.MysteriousHalloweenCompat;
import one.lindegaard.MobHunting.compatibility.MythicMobsCompat;
import one.lindegaard.MobHunting.compatibility.TARDISWeepingAngelsCompat;
import one.lindegaard.MobHunting.storage.DataStoreException;
import one.lindegaard.MobHunting.util.Misc;

public class ExtendedMobManager {

	private static HashMap<Integer, ExtendedMob> mobs = new HashMap<Integer, ExtendedMob>();

	public ExtendedMobManager() {
		updateExtendedMobs();
	}

	public void updateExtendedMobs() {
		MobHunting.getStoreManager().insertMissingVanillaMobs();
		if (CitizensCompat.isSupported())
			MobHunting.getStoreManager().insertMissingCitizensMobs();
		if (MythicMobsCompat.isSupported())
			MobHunting.getStoreManager().insertMissingMythicMobs();
		if (CustomMobsCompat.isSupported())
			MobHunting.getStoreManager().insertCustomMobs();
		if (TARDISWeepingAngelsCompat.isSupported())
			MobHunting.getStoreManager().insertTARDISWeepingAngelsMobs();
		if (MysteriousHalloweenCompat.isSupported())
			MobHunting.getStoreManager().insertMysteriousHalloweenMobs();

		Set<ExtendedMob> set = new HashSet<ExtendedMob>();

		try {
			set = (HashSet<ExtendedMob>) MobHunting.getStoreManager().loadMobs();
		} catch (DataStoreException e) {
			Bukkit.getLogger().severe("[MobHunting] Could not load data from mh_Mobs");
			e.printStackTrace();
		}

		int n = 0;
		Iterator<ExtendedMob> mobset = set.iterator();
		while (mobset.hasNext()) {
			ExtendedMob mob = (ExtendedMob) mobset.next();
			switch (mob.getMobPlugin()) {
			case MythicMobs:
				if (!MythicMobsCompat.isSupported() || MythicMobsCompat.isDisabledInConfig()
						|| !MythicMobsCompat.isMythicMob(mob.getMobtype()))
					continue;
				break;
			case CustomMobs:
				if (!CustomMobsCompat.isSupported() || CustomMobsCompat.isDisabledInConfig())
					continue;
				break;
			case TARDISWeepingAngels:
				if (!TARDISWeepingAngelsCompat.isSupported() || TARDISWeepingAngelsCompat.isDisabledInConfig())
					continue;
				break;
			case Citizens:
				if (!CitizensCompat.isSupported() || CitizensCompat.isDisabledInConfig()
						|| !CitizensCompat.isSentryOrSentinelOrSentries(mob.getMobtype()))
					continue;
				break;
			case MysteriousHalloween:
				if (!MysteriousHalloweenCompat.isSupported() || MysteriousHalloweenCompat.isDisabledInConfig())
					continue;
				break;

			case Minecraft:
				break;
			default:
				break;

			}
			if (!mobs.containsKey(mob.getMob_id())) {
				n++;
				mobs.put(mob.getMob_id(), mob);
			}
		}
		Messages.debug("%s mobs was loaded into memory. Total mobs=%s", n, mobs.size());
	}

	public ExtendedMob getExtendedMobFromMobID(int i) {
		return mobs.get(i);
	}

	public HashMap<Integer, ExtendedMob> getAllMobs() {
		return mobs;
	}

	public int getMobIdFromMobTypeAndPluginID(String mobtype, MobPlugin mobPlugin) {

		Iterator<Entry<Integer, ExtendedMob>> mobset = mobs.entrySet().iterator();
		while (mobset.hasNext()) {
			ExtendedMob mob = (ExtendedMob) mobset.next().getValue();
			if (mob.getMobPlugin().equals(mobPlugin) && mob.getMobtype().equalsIgnoreCase(mobtype))
				return mob.getMob_id();
		}
		//Bukkit.getLogger().warning("[MobHunting] The " + mobPlugin.name() + " mobtype " + mobtype + " was not found.");
		return 0;
	}

	public ExtendedMob getExtendedMobFromEntity(Entity entity) {
		int mob_id;
		MobPlugin mobPlugin;
		String mobtype;

		if (MythicMobsCompat.isMythicMob(entity)) {
			mobPlugin = MobPlugin.MythicMobs;
			mobtype = MythicMobsCompat.getMythicMobType(entity);
		} else if (CitizensCompat.isNPC(entity)) {
			mobPlugin = MobPlugin.Citizens;
			mobtype = String.valueOf(CitizensCompat.getNPCId(entity));
		} else if (TARDISWeepingAngelsCompat.isWeepingAngelMonster(entity)) {
			mobPlugin = MobPlugin.TARDISWeepingAngels;
			mobtype = TARDISWeepingAngelsCompat.getWeepingAngelMonsterType(entity).name();
		} else if (CustomMobsCompat.isCustomMob(entity)) {
			mobPlugin = MobPlugin.CustomMobs;
			mobtype = CustomMobsCompat.getCustomMobType(entity);
		} else if (MysteriousHalloweenCompat.isMysteriousHalloween(entity)) {
			mobPlugin = MobPlugin.MysteriousHalloween;
			mobtype = MysteriousHalloweenCompat.getMysteriousHalloweenType(entity).name();
		} else {
			// StatType
			mobPlugin = MobPlugin.Minecraft;
			MinecraftMob mob = MinecraftMob.getExtendedMobType(entity);
			if (mob != null)
				mobtype = mob.name();
			else {
				//Messages.debug("ERROR!!! Unsupported mob/entity: '%s'", mob);
				mobtype = "";
			}
		}
		mob_id = getMobIdFromMobTypeAndPluginID(mobtype, mobPlugin);
		return new ExtendedMob(mob_id, mobPlugin, mobtype);
	}

	// This is only used to get a "random" mob_id stored when an Achievement is
	// stored in mh_Daily
	public static ExtendedMob getFirstMob() {
		int mob_id = mobs.keySet().iterator().next().intValue();
		return mobs.get(mob_id);
	}

	public static String getMobName(Entity mob) {
		if (Misc.isMC18OrNewer())
			return mob.getName();
		else
			return mob.getType().toString();
	}

}
