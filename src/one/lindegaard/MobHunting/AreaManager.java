package one.lindegaard.MobHunting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import one.lindegaard.MobHunting.util.Misc;

public class AreaManager implements Listener {

	private MobHunting instance;
	private static ArrayList<Area> mKnownGrindingSpots = new ArrayList<Area>();
	private static HashMap<UUID, LinkedList<Area>> mWhitelistedAreas = new HashMap<UUID, LinkedList<Area>>();

	public AreaManager(MobHunting instance) {
		this.instance = instance;
		if (!loadWhitelist())
			throw new RuntimeException();
		instance.getServer().getPluginManager().registerEvents(this, instance);
	}

	public ArrayList<Area> getKnownGrindingSpots() {
		return mKnownGrindingSpots;
	}

	public void addKnownGrindingSpot(Area area) {
		mKnownGrindingSpots.add(area);
	}

	public LinkedList<Area> getWhitelistedAreas(World world) {
		return mWhitelistedAreas.get(world.getUID());
	}

	public void shutdown() {
		saveWhitelist();
	}

	private boolean saveWhitelist() {
		YamlConfiguration whitelist = new YamlConfiguration();
		File file = new File(MobHunting.getInstance().getDataFolder(), "whitelist.yml");

		for (Entry<UUID, LinkedList<Area>> entry : mWhitelistedAreas.entrySet()) {
			ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
			for (Area area : entry.getValue()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("Center", Misc.toMap(area.center));
				map.put("Radius", area.range);
				list.add(map);
			}

			whitelist.set(entry.getKey().toString(), list);
		}

		try {
			whitelist.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	private boolean loadWhitelist() {
		YamlConfiguration whitelist = new YamlConfiguration();
		File file = new File(instance.getDataFolder(), "whitelist.yml");

		if (!file.exists())
			return true;

		try {
			whitelist.load(file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			return false;
		}

		mWhitelistedAreas.clear();

		for (String worldId : whitelist.getKeys(false)) {
			UUID world = UUID.fromString(worldId);
			List<Map<String, Object>> list = (List<Map<String, Object>>) whitelist.getList(worldId);
			LinkedList<Area> areas = new LinkedList<Area>();

			if (list == null)
				continue;

			for (Map<String, Object> map : list) {
				Area area = new Area();
				area.center = Misc.fromMap((Map<String, Object>) map.get("Center"));
				area.range = (Double) map.get("Radius");
				areas.add(area);
			}

			mWhitelistedAreas.put(world, areas);
		}

		return true;
	}

	public boolean isWhitelisted(Location location) {
		LinkedList<Area> areas = mWhitelistedAreas.get(location.getWorld().getUID());

		if (areas == null)
			return false;

		for (Area area : areas) {
			if (area.center.distance(location) < area.range)
				return true;
		}

		return false;
	}

	public void whitelistArea(Area newArea) {
		LinkedList<Area> areas = mWhitelistedAreas.get(newArea.center.getWorld().getUID());

		if (areas == null) {
			areas = new LinkedList<Area>();
			mWhitelistedAreas.put(newArea.center.getWorld().getUID(), areas);
		}

		for (Area area : areas) {
			if (newArea.center.getWorld().equals(area.center.getWorld())) {
				double dist = newArea.center.distance(area.center);

				double remaining = dist;
				remaining -= area.range;
				remaining -= newArea.range;

				if (remaining < 0) {
					if (dist > area.range)
						area.range = dist;

					area.count += newArea.count;

					return;
				}
			}
		}

		areas.add(newArea);

		saveWhitelist();
	}

	public void unWhitelistArea(Location location) {
		LinkedList<Area> areas = mWhitelistedAreas.get(location.getWorld().getUID());

		if (areas == null)
			return;

		Iterator<Area> it = areas.iterator();
		while (it.hasNext()) {
			Area area = it.next();

			if (area.center.getWorld().equals(location.getWorld())) {
				if (area.center.distance(location) < area.range)
					it.remove();
			}
		}

		if (areas.isEmpty())
			mWhitelistedAreas.remove(location.getWorld().getUID());

		saveWhitelist();
	}
	
	public void registerKnownGrindingSpot(Area newArea) {
		for (Area area : getKnownGrindingSpots()) {
			if (newArea.center.getWorld().equals(area.center.getWorld())) {
				double dist = newArea.center.distance(area.center);

				double remaining = dist;
				remaining -= area.range;
				remaining -= newArea.range;

				if (remaining < 0) {
					if (dist > area.range)
						area.range = dist;

					area.count += newArea.count;

					return;
				}
			}
		}

		addKnownGrindingSpot(newArea);
	}

	public Area getGrindingArea(Location location) {
		for (Area area : getKnownGrindingSpots()) {
			if (area.center.getWorld().equals(location.getWorld())) {
				if (area.center.distance(location) < area.range){
					MobHunting.debug("Found a grinding area = %s", area.center);
					return area;
				}
			}
		}

		return null;
	}

	public void clearGrindingArea(Location location) {
		Iterator<Area> it = getKnownGrindingSpots().iterator();
		while (it.hasNext()) {
			Area area = it.next();

			if (area.center.getWorld().equals(location.getWorld())) {
				if (area.center.distance(location) < area.range)
					it.remove();
			}
		}
	}



	@EventHandler
	private void onWorldLoad(WorldLoadEvent event) {
		List<Area> areas = getWhitelistedAreas(event.getWorld());
		if (areas != null) {
			for (Area area : areas)
				area.center.setWorld(event.getWorld());
		}
	}

	@EventHandler
	private void onWorldUnLoad(WorldUnloadEvent event) {
		List<Area> areas = getWhitelistedAreas(event.getWorld());
		if (areas != null) {
			for (Area area : areas)
				area.center.setWorld(null);
		}
	}

}
