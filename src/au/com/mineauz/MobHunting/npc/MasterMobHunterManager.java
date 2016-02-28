package au.com.mineauz.MobHunting.npc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.compatability.CitizensCompat;

public class MasterMobHunterManager implements Listener {

	private HashMap<Integer, MasterMobHunterData> mMasterMobHunterData = new HashMap<Integer, MasterMobHunterData>();
	private File file = new File(MobHunting.instance.getDataFolder(),
			"citizens-MasterMobHunter.yml");
	private YamlConfiguration config = new YamlConfiguration();

	private BukkitTask mUpdater = null;

	public MasterMobHunterManager() {
		loadData();
	}

	public void initialize() {
		mUpdater = Bukkit.getScheduler().runTaskTimer(MobHunting.instance,
				new Updater(), 1L,
				MobHunting.config().masterMobHuntercheckEvery * 20);
		Bukkit.getPluginManager().registerEvents(new MobHuntingTrait(),
				MobHunting.instance);
		Bukkit.getPluginManager().registerEvents(new MasterMobHunterManager(),
				MobHunting.instance);
		Bukkit.getPluginManager().registerEvents(new MasterMobHunterData(),
				MobHunting.instance);

	}

	public void forceUpdate() {
		mUpdater = Bukkit.getScheduler().runTaskAsynchronously(
				MobHunting.instance, new Updater());
	}

	private class Updater implements Runnable {
		@Override
		public void run() {
			MobHunting.debug("Refreshing %s MasterMobHunter",
					mMasterMobHunterData.size());
			for (int id : mMasterMobHunterData.keySet()) {
				mMasterMobHunterData.get(id).update();
			}
		}
	}

	public MasterMobHunterData get(int id) {
		return mMasterMobHunterData.get(id);
	}

	public HashMap<Integer, MasterMobHunterData> getAll() {
		return mMasterMobHunterData;
	}

	public void put(int id, MasterMobHunterData mmhd) {
		mMasterMobHunterData.put(id, mmhd);
	}

	public boolean contains(int id) {
		return mMasterMobHunterData.containsKey(id);
	}

	public void remove(int id) {
		mMasterMobHunterData.remove(id);
	}

	public void shutdown() {
		mUpdater.cancel();
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
						Integer.valueOf(key));
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
			if (mMasterMobHunterData.containsKey(id)) {
				NPC npc = CitizensAPI.getNPCRegistry().getById(id);
				if (npc != null) {
					ConfigurationSection section = config.createSection(key);
					mMasterMobHunterData.get(id).write(section);
					MobHunting
							.debug("Saving MasterMobhunter (%s) to file.", id);
					config.save(file);
				} else if (config.contains(key)) {
					config.set(key, null);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try {
			config.options().header("MasterMobHunterData.");
			if (mMasterMobHunterData.size() > 0) {
				int n = 0;
				for (Integer key : mMasterMobHunterData.keySet()) {
					NPC npc = CitizensAPI.getNPCRegistry().getById(key);
					if (npc != null) {
						ConfigurationSection section = config
								.createSection(String.valueOf(key));
						mMasterMobHunterData.get(key).write(section);
						n++;
					} else if (config.contains(String.valueOf(key))) {
						config.set(String.valueOf(key), null);
					}
				}
				if (n != 0) {
					MobHunting.debug("Saving %s MasterMobhunters to file.", n);
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
		MobHunting.debug(
				"MasterMobHunterManager - Leftclick :%s, No of NPCs=%s", event
						.getNPC().getId(), mMasterMobHunterData.size());
		NPC npc = event.getNPC();
		if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
			npc.setName("MasterMobHunter");
			MasterMobHunterData mmhd = new MasterMobHunterData();
			mmhd = mMasterMobHunterData.get(event.getNPC().getId());
			mmhd.update();
			//TODO: wait 10 sec and continue
			// mmhd.refresh();
			event.getClicker().sendMessage(
					"You LEFT clicked a MasterMobHunter NPC(" + npc.getId()
							+ ")=" + event.getNPC().getName() + " rank="
							+ mmhd.getRank() + " kills="
							+ mmhd.getNumberOfKills() + " Period="
							+ mmhd.getPeriod().translateName() + " StatType="
							+ mmhd.getStatType().translateName());
			mMasterMobHunterData.put(event.getNPC().getId(), mmhd);
		}
	}

	@EventHandler
	public void onClick(NPCRightClickEvent event) {
		MobHunting.debug("MasterMobHunterManager - Rightclick :"
				+ event.getNPC().getId());
		NPC npc = event.getNPC();
		if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
			MasterMobHunterData mmhd = new MasterMobHunterData();
			mmhd = mMasterMobHunterData.get(event.getNPC().getId());
			mmhd.update();
			//TODO: wait 10 sec and continue
			// mmhd.refresh();
			if (mMasterMobHunterData.containsKey(npc.getId())) {
				// MasterMobHunterData mmhd = new MasterMobHunterData();
				// mmhd = mMasterMobHunterData.get(npc.getId());
				event.getClicker().sendMessage(
						"You RIGHT clicked a MasterMobHunter NPC("
								+ npc.getId() + ")=" + event.getNPC().getName()
								+ " rank=" + mmhd.getRank() + " kills="
								+ mmhd.getNumberOfKills() + " Period="
								+ mmhd.getPeriod().translateName()
								+ " StatType="
								+ mmhd.getStatType().translateName());
				mMasterMobHunterData.put(event.getNPC().getId(), mmhd);
			} else
				MobHunting.debug("NPC ID %s DOES NOT EXISTS!!!", event.getNPC()
						.getId());
		}
	}

	// ***************************************************************
	// Events
	// ***************************************************************
	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		String l0 = event.getLine(0);
		if (!l0.matches("\\[(MH|mh|Mh|mH)(\\d+)\\]")) {
			return;
		}
		Player p = event.getPlayer();
		// Sign sign = (Sign) event.getBlock().getState().getData();
		// Block attached =
		// event.getBlock().getRelative(sign.getAttachedFace());
		int id = Integer.valueOf(l0.substring(3, l0.length() - 1));
		NPCRegistry registry = CitizensAPI.getNPCRegistry();
		NPC npc = registry.getById(id);
		if (npc != null) {
			if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
				Location location = event.getBlock().getLocation();
				MasterMobHunterData mmhd = new MasterMobHunterData();
				mmhd = mMasterMobHunterData.get(id);
				mmhd.update();
				//TODO: wait 10 sec and continue
				// mmhd.refresh();
				mmhd.putLocation(location);
				mMasterMobHunterData.put(id, mmhd);
				saveData(id);
				p.sendMessage(p.getName() + " placed a MobHunting Sign (ID="
						+ id + ")");
				// event.setLine(0, "");
				event.setLine(1,
						(mMasterMobHunterData.get(id).getRank() + "." + npc
								.getName()));
				event.setLine(2, (mMasterMobHunterData.get(id).getPeriod()
						.translateNameFriendly()));
				event.setLine(3, (mMasterMobHunterData.get(id)
						.getNumberOfKills() + " " + mMasterMobHunterData
						.get(id).getStatType().translateName()));
			}

		}
	}

}
