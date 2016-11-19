package one.lindegaard.MobHunting.mobs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatibility.CitizensCompat;
import one.lindegaard.MobHunting.compatibility.CustomMobsCompat;
import one.lindegaard.MobHunting.compatibility.MythicMobsCompat;
import one.lindegaard.MobHunting.compatibility.TARDISWeepingAngelsCompat;
import one.lindegaard.MobHunting.storage.DataStoreException;

public class ExtendedMobManager {

	private HashMap<Integer, ExtendedMob> mobs = new HashMap<Integer, ExtendedMob>();

	public ExtendedMobManager() {

		Set<ExtendedMob> set = new HashSet<ExtendedMob>();

		try {
			set = (HashSet<ExtendedMob>) MobHunting.getStoreManager().loadMobs();
		} catch (DataStoreException e) {
			Bukkit.getLogger().severe("[MobHunting] Could not load data from mh_Mobs");
			e.printStackTrace();
		}

		MobHunting.getStoreManager().insertMissingVanillaMobs();

		Iterator<ExtendedMob> mobset = set.iterator();
		while (mobset.hasNext()) {
			ExtendedMob mob = (ExtendedMob) mobset.next();
			switch (mob.mobPlugin) {
			case MythicMobs:
				if (!MythicMobsCompat.isSupported() || MythicMobsCompat.isDisabledInConfig())
					continue;
				//TODO: check if mobtype still exists and continue; if not.
				//if (MythicMobsCompat.getMythicMobs().)
				break;
			case CustomMobs:
				if (!CustomMobsCompat.isSupported() || CustomMobsCompat.isDisabledInConfig())
					continue;
				//TODO: check if mobtype still exists and continue; if not.
				//if (CustomMobsCompat.getCustomMobs())
				break;
			case TARDISWeepingAngels:
				if (!TARDISWeepingAngelsCompat.isSupported() || TARDISWeepingAngelsCompat.isDisabledInConfig())
					continue;
				break;
			case Citizens:
				if (!CitizensCompat.isSupported() || CitizensCompat.isDisabledInConfig())
					//Citizens not installed or disabled
					continue;
				if (CitizensCompat.getCitizensPlugin().getNPCRegistry().getById(Integer.valueOf(mob.mobtype))==null)
					//NPC is deleted but data is still in citizenz_rewards.yml 
					continue;
				break;
			case Minecraft:

			}
			if (!mobs.containsKey(mob.mob_id)){
				mobs.put(mob.mob_id, mob);
			}
		}
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
			if (mob.getMobPlugin().equals(mobPlugin) && mob.mobtype.equalsIgnoreCase(mobtype))
				return mob.mob_id;
		}
		Messages.debug("The %s mobtype %s was not found.", mobPlugin.name(), mobtype);
		return 0;
	}

	public ExtendedMob getExtendedMobFromEntity(LivingEntity entity) {
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
		} else {
			// StatType
			mobPlugin = MobPlugin.Minecraft;
			mobtype = MinecraftMob.getExtendedMobType(entity).name();
		}
		mob_id = getMobIdFromMobTypeAndPluginID(mobtype, mobPlugin);
		return new ExtendedMob(mob_id, mobPlugin, mobtype);
	}

}
