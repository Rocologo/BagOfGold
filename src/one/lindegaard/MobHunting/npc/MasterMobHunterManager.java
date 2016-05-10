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
import org.bukkit.entity.Entity;
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
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatability.CitizensCompat;

public class MasterMobHunterManager implements Listener {

	private HashMap<Integer, MasterMobHunter> mMasterMobHunter = new HashMap<Integer, MasterMobHunter>();

	private File file = new File(MobHunting.getInstance().getDataFolder(), "citizens-MasterMobHunter.yml");
	private YamlConfiguration config = new YamlConfiguration();

	private BukkitTask mUpdater = null;

	public MasterMobHunterManager() {
	}

	public void initialize() {
		if (CitizensCompat.isCitizensSupported()) {
			mUpdater = Bukkit.getScheduler().runTaskTimer(MobHunting.getInstance(), new Updater(), 1L,
					MobHunting.getConfigManager().masterMobHuntercheckEvery * 20);
			Bukkit.getPluginManager().registerEvents(new MasterMobHunterTrait(), MobHunting.getInstance());
			Bukkit.getPluginManager().registerEvents(new MasterMobHunterManager(), MobHunting.getInstance());
			Bukkit.getPluginManager().registerEvents(new MasterMobHunterSign(MobHunting.getInstance()),
					MobHunting.getInstance());
			loadData();
		}
	}

	public void forceUpdate() {
		mUpdater = Bukkit.getScheduler().runTaskAsynchronously(MobHunting.getInstance(), new Updater());
	}

	private class Updater implements Runnable {
		@Override
		public void run() {
			int n = 0;
			for (Iterator<NPC> npcList = CitizensAPI.getNPCRegistry().iterator(); npcList.hasNext();) {
				NPC npc = npcList.next();
				if (isMasterMobHunter(npc.getEntity())) {
					update(npc);
					n++;
				}
			}
			if (n > 0)
				MobHunting.debug("Refreshed %s MasterMobHunters", n);
		}
	}

	public void update(NPC npc) {
		if (hasMasterMobHunterData(npc)) {
			MasterMobHunter mmh = new MasterMobHunter(npc);
			if (mmh != null) {
				mmh.update();
				mMasterMobHunter.put(npc.getId(), mmh);
			}
		}
	}

	public static boolean hasMasterMobHunterData(NPC npc) {
		return (npc.getTrait(MasterMobHunterTrait.class).stattype != null);
	}

	public MasterMobHunter get(int id) {
		return mMasterMobHunter.get(id);
	}

	public HashMap<Integer, MasterMobHunter> getAll() {
		return mMasterMobHunter;
	}

	public void put(int id, MasterMobHunter mmh) {
		mMasterMobHunter.put(id, mmh);
	}

	public boolean contains(int id) {
		return mMasterMobHunter.containsKey(id);
	}

	public void remove(int id) {
		mMasterMobHunter.remove(id);
	}

	public void shutdown() {
		mUpdater.cancel();
	}

	public boolean isMasterMobHunter(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
			NPC npc = CitizensCompat.getNPC(entity);
			return (npc.hasTrait(MasterMobHunterTrait.class));
		} else
			return false;
	}

	public static boolean isMasterMobHunter(NPC npc) {
		return (npc.hasTrait(MasterMobHunterTrait.class));
	}

	// ****************************************************************************
	// Save & Load
	// ****************************************************************************

	public void loadData() {
		try {
			if (!file.exists())
				return;
			config.load(file);
			int n = 0;
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(key));
				if (npc != null && npc.hasTrait(MasterMobHunterTrait.class)) {
					MasterMobHunter mmh = new MasterMobHunter(npc);
					if (npc.getTrait(MasterMobHunterTrait.class).stattype == null) {
						mmh.read(section);
						n++;
						section.set(key, null);
						config.save(file);
					}
					mMasterMobHunter.put(Integer.valueOf(key), mmh);
				}
			}
			MobHunting.debug("The file citizens-MasterMobHunter.yml is not used anymore and can be deleted.");
			if (n > 0)
				MobHunting.debug("Loaded %s MasterMobHunter Traits's from file.", n);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	// ****************************************************************************
	// Events
	// ****************************************************************************

	@EventHandler
	public void onClick(NPCLeftClickEvent event) {
		NPC npc = event.getNPC();
		if (isMasterMobHunter(npc)) {
			npc.setName("UPDATING...");
			update(npc);
			MasterMobHunter mmh = new MasterMobHunter(npc);
			event.getClicker()
					.sendMessage(Messages.getString("mobhunting.npc.clickednpc", "killer", mmh.getNpc().getName(), "rank",
							mmh.getRank(), "numberofkills", mmh.getNumberOfKills(), "stattype",
							mmh.getStatType().translateName(), "period", mmh.getPeriod().translateNameFriendly()));
			mMasterMobHunter.put(event.getNPC().getId(), mmh);
		} else {
			MobHunting.debug("ID=%s is not a masterMobHunterNPC.", event.getNPC().getId());
		}
	}

	@EventHandler
	public void onClick(NPCRightClickEvent event) {
		NPC npc = event.getNPC();
		if (isMasterMobHunter(npc)) {
			update(npc);
			MasterMobHunter mmh = new MasterMobHunter(npc);
			mmh.update();
			event.getClicker()
					.sendMessage(Messages.getString("mobhunting.npc.clickednpc", "killer", mmh.getNpc().getName(), "rank",
							mmh.getRank(), "numberofkills", mmh.getNumberOfKills(), "stattype",
							mmh.getStatType().translateName(), "period", mmh.getPeriod().translateNameFriendly()));
			mMasterMobHunter.put(event.getNPC().getId(), mmh);
		} else {
			MobHunting.debug("ID=%s is not a masterMobHunterNPC.", event.getNPC().getId());
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSignChangeEvent(SignChangeEvent event) {
		Player p = event.getPlayer();
		int id = MasterMobHunterSign.getNPCIdOnSign(event.getLine(0));
		boolean powered = MasterMobHunterSign.isPowerSetOnSign(event.getLine(0));
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if (npc != null) {
			if (isMasterMobHunter(npc)) {
				update(npc);
				MasterMobHunter mmh = new MasterMobHunter(npc);
				mmh.putLocation(event.getBlock().getLocation());
				mMasterMobHunter.put(id, mmh);
				p.sendMessage(p.getName() + " placed a MobHunting Sign (ID=" + id + ")");
				event.setLine(1, (mmh.getRank() + "." + npc.getName()));
				event.setLine(2, (mmh.getPeriod().translateNameFriendly()));
				event.setLine(3, (mmh.getNumberOfKills() + " " + mmh.getStatType().translateName()));
				if (powered) {
					OfflinePlayer player = Bukkit.getPlayer(npc.getName());
					if (player != null && player.isOnline())
						MasterMobHunterSign.setPower(event.getBlock(), MasterMobHunterSign.POWER_FROM_SIGN);
				} else
					MasterMobHunterSign.removePower(event.getBlock());
			} else
				p.sendMessage(Messages.getString("mobhunting.npc.notamastermobhunter", "id", npc.getId()));
		}
	}

	@EventHandler
	public void onBlockBreak(final BlockBreakEvent event) {
		// TODO: Test if MMH sign at remove from NPC list. Maybe.
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onPlayerJoin(PlayerJoinEvent event) {
		Iterator<NPC> itr = CitizensAPI.getNPCRegistry().iterator();
		while (itr.hasNext()) {
			NPC npc = (NPC) itr.next();
			if (event.getPlayer().getName().equals(npc.getName()) && isMasterMobHunter(npc))
				update(npc);
		}
	}
}
