package one.lindegaard.MobHunting.npc;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitTask;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatability.CitizensCompat;

public class MasterMobHunterManager implements Listener {

	private HashMap<Integer, MasterMobHunterData> mMasterMobHunterData = new HashMap<Integer, MasterMobHunterData>();
	private File file = new File(MobHunting.getInstance().getDataFolder(), "citizens-MasterMobHunter.yml");
	private YamlConfiguration config = new YamlConfiguration();

	private BukkitTask mUpdater = null;

	public MasterMobHunterManager() {
		loadData();
	}

	public void initialize() {
		if (CitizensCompat.isCitizensSupported()) {
			mUpdater = Bukkit.getScheduler().runTaskTimer(MobHunting.getInstance(), new Updater(), 1L,
					MobHunting.getConfigManager().masterMobHuntercheckEvery * 20);
			Bukkit.getPluginManager().registerEvents(new MobHuntingTrait(), MobHunting.getInstance());
			Bukkit.getPluginManager().registerEvents(new MasterMobHunterManager(), MobHunting.getInstance());
			Bukkit.getPluginManager().registerEvents(new MasterMobhunterSign(MobHunting.getInstance()),
					MobHunting.getInstance());
		}
	}

	public void forceUpdate() {
		mUpdater = Bukkit.getScheduler().runTaskAsynchronously(MobHunting.getInstance(), new Updater());
	}

	private class Updater implements Runnable {
		@Override
		public void run() {
			MobHunting.debug("Refreshing %s MasterMobHunter", mMasterMobHunterData.size());
			for (int id : mMasterMobHunterData.keySet()) {
				mMasterMobHunterData.get(id).update();
			}
		}
	}

	public void update(NPC npc) {
		if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
			MasterMobHunterData mmhd = new MasterMobHunterData();
			mmhd = mMasterMobHunterData.get(npc.getId());
			mmhd.update();
			mMasterMobHunterData.put(npc.getId(), mmhd);
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
				ConfigurationSection section = config.getConfigurationSection(key);
				MasterMobHunterData mmhd = new MasterMobHunterData(Integer.valueOf(key));
				mmhd.read(section);
				mMasterMobHunterData.put(Integer.valueOf(key), mmhd);
			}
			MobHunting.debug("Loaded %s MasterMobHunter Traits's", mMasterMobHunterData.size());
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
					MobHunting.debug("Saving MasterMobhunter (%s) to file.", id);
					config.save(file);
				} else if (config.contains(key)) {
					config.set(String.valueOf(key), null);
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
						ConfigurationSection section = config.createSection(String.valueOf(key));
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
		MobHunting.debug("MasterMobHunterManager - Leftclick :%s, No of NPCs=%s", event.getNPC().getId(),
				mMasterMobHunterData.size());
		NPC npc = event.getNPC();
		if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
			npc.setName("MasterMobHunter");
			MasterMobHunterData mmhd = new MasterMobHunterData();
			mmhd = mMasterMobHunterData.get(event.getNPC().getId());
			if (mMasterMobHunterData.containsKey(event.getNPC().getId())) {
				mmhd.update();
				event.getClicker()
						.sendMessage("You LEFT clicked a MasterMobHunter NPC(" + npc.getId() + ") rank="
								+ mmhd.getRank() + " kills=" + mmhd.getNumberOfKills() + " Period="
								+ mmhd.getPeriod().translateName() + " StatType=" + mmhd.getStatType().translateName());
			} else
				MobHunting.debug("ID=%s is missing in mMasterMobHunterData???", event.getNPC().getId());
			mMasterMobHunterData.put(event.getNPC().getId(), mmhd);
		}
	}

	@EventHandler
	public void onClick(NPCRightClickEvent event) {
		MobHunting.debug("MasterMobHunterManager - Rightclick :" + event.getNPC().getId());
		NPC npc = event.getNPC();
		if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
			MasterMobHunterData mmhd = new MasterMobHunterData();
			mmhd = mMasterMobHunterData.get(npc.getId());
			mmhd.update();
			event.getClicker()
					.sendMessage("You RIGHT clicked a MasterMobHunter NPC(" + npc.getId() + ") rank=" + mmhd.getRank()
							+ " kills=" + mmhd.getNumberOfKills() + " Period=" + mmhd.getPeriod().translateName()
							+ " StatType=" + mmhd.getStatType().translateName());
			mMasterMobHunterData.put(event.getNPC().getId(), mmhd);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSignChangeEvent(SignChangeEvent event) {
		Player p = event.getPlayer();
		int id = MasterMobhunterSign.getNPCIdOnSign(event.getLine(0));
		boolean powered = MasterMobhunterSign.isPowerSetOnSign(event.getLine(0));
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if (npc != null) {
			if (CitizensCompat.isMasterMobHunter(npc.getEntity())) {
				MasterMobHunterData mmhd = new MasterMobHunterData();
				mmhd = mMasterMobHunterData.get(id);
				if (mMasterMobHunterData.containsKey(npc.getId())) {
					mmhd.update();
					mmhd.putLocation(event.getBlock().getLocation());
					mMasterMobHunterData.put(id, mmhd);
					saveData(id);
					p.sendMessage(p.getName() + " placed a MobHunting Sign (ID=" + id + ")");
					event.setLine(1, (mMasterMobHunterData.get(id).getRank() + "." + npc.getName()));
					event.setLine(2, (mMasterMobHunterData.get(id).getPeriod().translateNameFriendly()));
					event.setLine(3, (mMasterMobHunterData.get(id).getNumberOfKills() + " "
							+ mMasterMobHunterData.get(id).getStatType().translateName()));
					if (powered) {
						OfflinePlayer player = Bukkit.getPlayer(npc.getName());
						if (player != null && player.isOnline())
							MasterMobhunterSign.setPower(event.getBlock(), MasterMobhunterSign.POWER_FROM_SIGN);
					} else
						MasterMobhunterSign.removePower(event.getBlock());
				}

			} else
				MobHunting.debug("ID=%s is missing in mMasterMobHunterData???", npc.getId());
		}
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		// TODO: Test if MMHD sign at remove from NPC list. Maybe.
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		Iterator<NPC> itr = CitizensAPI.getNPCRegistry().iterator();
		while (itr.hasNext()) {
			NPC npc = (NPC) itr.next();
			if (event.getPlayer().getName().equals(npc.getName()))
				update(npc);
		}
	}
}
