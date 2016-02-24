package au.com.mineauz.MobHunting.npc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.Sign;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.compatability.CitizensCompat;
import au.com.mineauz.MobHunting.storage.DataCallback;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class MasterMobHunterManager implements Listener,
		DataCallback<List<StatStore>> {

	private HashMap<Integer, MasterMobHunterData> mMasterMobHunterData = new HashMap<Integer, MasterMobHunterData>();
	private File file = new File(MobHunting.instance.getDataFolder(),
			"citizens-MasterMobHunter.yml");
	private YamlConfiguration config = new YamlConfiguration();

	private TimePeriod[] mPeriod = { TimePeriod.AllTime };
	private int mPeriodIndex = 0;
	private StatType[] mType = { StatType.KillsTotal };
	private int mTypeIndex = 0;

	private List<StatStore> mData;

	public MasterMobHunterManager() {
		mPeriodIndex = 0;
		mTypeIndex = 0;
		loadData();
	}

	public List<StatStore> getCurrentStats() {
		if (mData == null)
			return Collections.emptyList();
		return mData;
	}

	@Override
	public void onCompleted(List<StatStore> data) {
		ArrayList<StatStore> altData = new ArrayList<StatStore>(data.size());
		for (StatStore stat : data) {
			if (stat.getAmount() != 0)
				altData.add(stat);
		}
		mData = altData;
		refresh();
	}

	@Override
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	public void refresh() {
		if (mData != null) {
			// MobHunting.debug("mData.size()=%s", mData.size());
			//for (int i = 0; i < mData.size(); i++) {
			//	MobHunting.debug("mData[%s]=%s", i, mData.get(i));
			//}
		}
		NPCRegistry n = CitizensAPI.getNPCRegistry();
		for (Iterator<NPC> npcList = n.iterator(); npcList.hasNext();) {
			NPC npc = npcList.next();
			if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
				int id = npc.getId();
				// MobHunting.debug("id=%s,name=%s,no of npc=%s", npc.getId(),
				// npc.getName(), getAll().size());
				MasterMobHunterData mmhd = mMasterMobHunterData.get(id);
				// MobHunting.debug("mmhd=%s", mmhd);
				int rank = mmhd.getRank();
				if (mData.get(rank - 1).getPlayer().getName() != npc.getName()) {
					// MobHunting.debug("leftName=%s rightName=%s",
					// mData.get(rank - 1).getPlayer().getName(),
					// npc.getName());
					npc.setName(mData.get(rank - 1).getPlayer().getName());
				}
				int kills=mData.get(rank-1).getAmount();
				mmhd.setNumberOfKills(kills);
				//MobHunting.debug("amount = %s , %s", mData.get(rank-1).getAmount(),mmhd.getNumberOfKills());
				mMasterMobHunterData.put(id, mmhd);
				//int kills = mData.get(rank - 1).getAmount();
				//mmhd.setRank(rank);
				//mmhd.setNumberOfKills(kills);
				//mMasterMobHunterData.put(id, mmhd);
				
			}
		}

	}

	public void update() {
		++mTypeIndex;
		if (mTypeIndex >= mType.length) {
			mTypeIndex = 0;
			++mPeriodIndex;
			if (mPeriodIndex >= mPeriod.length)
				mPeriodIndex = 0;
		}

		MobHunting.instance.getDataStore().requestStats(getType(), getPeriod(),
				25, this);
	}

	public StatType getType() {
		return mType[mTypeIndex];
	}

	public StatType[] getTypes() {
		return mType;
	}

	public TimePeriod getPeriod() {
		return mPeriod[mPeriodIndex];
	}

	public TimePeriod[] getPeriods() {
		return mPeriod;
	}

	public MasterMobHunterData get(int id) {
		return mMasterMobHunterData.get(id);
	}

	public HashMap<Integer, MasterMobHunterData> getAll() {
		return mMasterMobHunterData;
	}

	public void put(Integer id, MasterMobHunterData mmhd) {
		mMasterMobHunterData.put(id, mmhd);
	}

	// ****************************************************************************
	// Save & Load
	// ****************************************************************************

	public void loadData() {
		try {
			if (!file.exists())
				return;
			MobHunting.debug("Loading MasterMobHunter Traits.");

			config.load(file);
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config
						.getConfigurationSection(key);
				MasterMobHunterData mmhd = new MasterMobHunterData(
						Integer.valueOf(key), StatType.KillsTotal,
						TimePeriod.AllTime, 0, 0, false);
				mmhd.read(section);
				mMasterMobHunterData.put(Integer.valueOf(key), mmhd);
			}
			MobHunting.debug("Loaded %s MasterMobHunter Traits's",
					mMasterMobHunterData.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveData(int id) {
		try {
			String key = String.valueOf(id);
			if (mMasterMobHunterData.containsKey(key)) {
				ConfigurationSection section = config.createSection(key);
				mMasterMobHunterData.get(key).save(section);
				MobHunting.debug("Saving MasterMobhunterData.");
				config.save(file);
			} else {
				MobHunting.debug("ERROR! Mob ID (%s) is not found", key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try {
			config.options().header("MasterMobHunter data.");
			if (mMasterMobHunterData.size() > 0) {
				MobHunting.debug("size=%s", mMasterMobHunterData.size());
				int n = 0;
				for (Integer key : mMasterMobHunterData.keySet()) {
					ConfigurationSection section = config.createSection(String
							.valueOf(key));
					MobHunting.debug("saving %s", key);
					mMasterMobHunterData.get(key).save(section);
					n++;
				}
				if (n != 0) {
					MobHunting.debug("Saving MasterMobhunterData to file.");
					config.save(file);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ****************************************************************************
	// Events
	// ****************************************************************************
	@EventHandler
	public void onClick(NPCLeftClickEvent event) {
		NPC npc = event.getNPC();
		if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
			npc.setName("Thor");
			MasterMobHunterData npcData = get(npc.getId());
			event.getClicker().sendMessage(
					"You LEFT clicked a MasterMobHunter ("
							+ event.getNPC().getName() + ")" + " rank="
							+ npcData.getRank());
		}
		MobHunting.debug("MasterMobHunterManager - Leftclick :"
				+ event.getNPC().getId());
	}

	@EventHandler
	public void onClick(NPCRightClickEvent event) {
		NPC npc = event.getNPC();
		MasterMobHunterData mmhd = get(npc.getId());
		if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
			MobHunting.debug("This is a MasterMobHunter Trait");
			event.getClicker().sendMessage(
					"You RIGHT clicked a MasterMobHunter ("
							+ event.getNPC().getName() + ")" + " rank="
							+ mmhd.getRank());
			// masterMobHunterManager.update();
			event.getClicker()
					.sendMessage(
							"Name after update() is ("
									+ event.getNPC().getName() + ")");
		}
		MobHunting.debug("MasterMobHunterManager - Rightclick");
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		MobHunting.debug("onSignChange in MasterMobHunter");
		String l0 = event.getLine(0);
		if (!l0.matches("\\[(MH|mh|Mh|mH)(\\d+)\\]")) {
			return;
		}
		Player p = event.getPlayer();
		Sign sign = (Sign) event.getBlock().getState().getData();
		Block attached = event.getBlock().getRelative(sign.getAttachedFace());
		int id = Integer.valueOf(l0.substring(3, l0.length() - 1));
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		// TODO: test if integer
		NPC npc = registry.getById(id);
		if (npc != null) {
			if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
				Location location = attached.getLocation();
				MobHunting.debug("size=%s val=%s", mMasterMobHunterData.size(),
						id);
				//MasterMobHunterData mmhd = new MasterMobHunterData();
				MasterMobHunterData mmhd = mMasterMobHunterData.get(id);
				if (mmhd != null) {
					mmhd.addLocation(location);
					mMasterMobHunterData.put(id, mmhd);
				} else
					MobHunting.debug("mmhd is null");
				p.sendMessage(p.getName() + " placed a MobHunting Sign (" + id
						+ ")");
				// event.setLine(0, "");
				event.setLine(1, (mmhd.getRank() + "." + npc.getName()));
				event.setLine(2, (mmhd.getPeriod().translateNameFriendly()));
				event.setLine(3, (mmhd.getNumberOfKills() + " " + mmhd
						.getStatType().translateName()));
				// write rank,name,period,stattype, number of kills on sign
			} else {

			}
		}

	}

}
