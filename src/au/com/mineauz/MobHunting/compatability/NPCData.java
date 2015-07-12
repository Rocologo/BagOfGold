package au.com.mineauz.MobHunting.compatability;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.leaderboard.Leaderboard;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class NPCData {
	private String npcName = "";
	private double rewardPrize = 5;
	private String consoleRunCommand = "";
	private String rewardDescription = "";
	private int propability = 100;

	public NPCData(String npcName, double rewardPrize, String cmd,
			String cmdDesc, int propability) {
		this.npcName = npcName;
		this.rewardPrize = rewardPrize;
		this.consoleRunCommand = cmd;
		this.rewardDescription = cmdDesc;
		this.propability = propability;
	}

	public String getNpcName() {
		return npcName;
	}

	public void setNpcName(String npcName) {
		this.npcName = npcName;
	}

	public double getRewardPrize() {
		return rewardPrize;
	}

	public void setRewardPrize(double rewardPrize) {
		this.rewardPrize = rewardPrize;
	}

	public String getConsoleRunCommand() {
		return consoleRunCommand;
	}

	public void setConsoleRunCommand(String consoleRunCommand) {
		this.consoleRunCommand = consoleRunCommand;
	}

	public String getRewardDescription() {
		return rewardDescription;
	}

	public void setRewardDescription(String rewardDescription) {
		this.rewardDescription = rewardDescription;
	}

	public int getPropability() {
		return propability;
	}

	public void setPropability(int propability) {
		this.propability = propability;
	}

	// **************************************************************************
	// Load & Save
	// **************************************************************************
	public NPCData() {
		super();
	}

	public static HashMap<Integer, NPCData> load() {
		HashMap<Integer, NPCData> mNPCData = new HashMap<Integer, NPCData>();
		try {
			File file = new File(MobHunting.instance.getDataFolder(),
					"citizens.yml");

			if (!file.exists())
				return null;

			YamlConfiguration config = new YamlConfiguration();
			config.load(file);

			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config
						.getConfigurationSection(key);
				NPCData npc = new NPCData();
				npc.read(section);
				mNPCData.put(Integer.valueOf(key), npc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return mNPCData;
	}

	public void save(ConfigurationSection section) {
		section.set("npcName", npcName);
		section.set("rewardPrize", rewardPrize);
		section.set("consoleRunCommand", consoleRunCommand);
		section.set("rewardDescription", rewardDescription);
		section.set("propability", propability);
	}

	private long toLong(Object obj) {
		if (obj instanceof Long)
			return (Long) obj;
		else if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number"); //$NON-NLS-1$
	}

	private double toDouble(Object obj) {
		if (obj instanceof Double)
			return (Double) obj;
		else if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number"); //$NON-NLS-1$
	}

	private int toInt(Object obj) {
		if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number"); //$NON-NLS-1$
	}

	private boolean toBool(Object obj) {
		if (obj instanceof Boolean)
			return (Boolean) obj;
		return Boolean.parseBoolean(obj.toString());
	}

	public Map<String, Object> write() {
		HashMap<String, Object> objects = new HashMap<String, Object>();
		objects.put("npcName", npcName);
		objects.put("rewardPrize", rewardPrize);
		objects.put("consoleRunCommand", consoleRunCommand);
		objects.put("rewardDescription", rewardDescription);
		objects.put("propability", propability);
		return objects;
	}

	public void read(Map<String, Object> data) {
		npcName = (String) data.get("npcName");
		rewardPrize = toDouble(data.get("rewardPrize"));
		consoleRunCommand = (String) data.get("consoleRunCommand");
		rewardDescription = (String) data.get("rewardDescription");
		propability = toInt(data.get("propability"));
	}

	public void read(ConfigurationSection section)
			throws InvalidConfigurationException, IllegalStateException {

		npcName = section.getString("npcName");
		rewardPrize = section.getDouble("rewardPrize");
		consoleRunCommand = section.getString("consoleRunCommand");
		rewardDescription = section.getString("rewardDescription");
		propability = section.getInt("propability");
	}

}
