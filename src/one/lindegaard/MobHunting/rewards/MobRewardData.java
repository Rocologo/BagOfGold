package one.lindegaard.MobHunting.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import one.lindegaard.MobHunting.MobPlugins;
import one.lindegaard.MobHunting.MobPlugins.MobPluginNames;

public class MobRewardData {
	private MobPluginNames mobPluginName;
	private String mobType = "";
	private String mobName = "";
	private String reward = "5";
	private String consoleRunCommand = "";
	private String rewardDescription = "";
	private int propability = 100;
	private int propabilityBase = 100;

	public MobRewardData() {
		super();
	}

	public MobRewardData(MobPluginNames pluginName, String mobType,
			String mobName, String rewardPrize, String cmd, String cmdDesc,
			int propability, int propabilityBase) {
		this.mobPluginName = pluginName;
		this.mobType = mobType;
		this.mobName = mobName;
		this.reward = rewardPrize;
		this.consoleRunCommand = cmd;
		this.rewardDescription = cmdDesc;
		this.propability = propability;
		this.setPropabilityBase(propabilityBase);
	}

	// **************************************************************************
	// Getters and Setters
	// **************************************************************************
	public MobPluginNames getMobPlugin() {
		return mobPluginName;
	}

	public void setMobPlugin(MobPluginNames mobPlugin) {
		this.mobPluginName = mobPlugin;
	}

	public String getMobType() {
		return mobType;
	}

	public void setMobType(String type) {
		this.mobType = type;
	}

	public String getMobName() {
		return mobName;
	}

	public void setMobName(String mobName) {
		this.mobName = mobName;
	}

	public String getRewardPrize() {
		return reward;
	}

	public void setRewardPrize(String rewardPrize) {
		this.reward = rewardPrize;
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

	public int getPropabilityBase() {
		return propabilityBase;
	}

	public void setPropabilityBase(int propabilityBase) {
		this.propabilityBase = propabilityBase;
	}

	// **************************************************************************
	// Load & Save
	// **************************************************************************
	public void save(ConfigurationSection section) {
		section.set("plugin", mobPluginName.toString());
		section.set("mobName", mobName);
		section.set("rewardPrize", reward);
		section.set("consoleRunCommand", consoleRunCommand);
		section.set("rewardDescription", rewardDescription);
		section.set("propability", propability);
		section.set("propabilityBase", propabilityBase);
	}

	@SuppressWarnings("unused")
	private long toLong(Object obj) {
		if (obj instanceof Long)
			return (Long) obj;
		else if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number");
	}

	@SuppressWarnings("unused")
	private double toDouble(Object obj) {
		if (obj instanceof Double)
			return (Double) obj;
		else if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number");
	}

	private int toInt(Object obj) {
		if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number");
	}

	@SuppressWarnings("unused")
	private boolean toBool(Object obj) {
		if (obj instanceof Boolean)
			return (Boolean) obj;
		return Boolean.parseBoolean(obj.toString());
	}

	public Map<String, Object> write() {
		HashMap<String, Object> objects = new HashMap<String, Object>();
		objects.put("plugin", mobPluginName);
		objects.put("mobName", mobName);
		objects.put("rewardPrize", reward);
		objects.put("consoleRunCommand", consoleRunCommand);
		objects.put("rewardDescription", rewardDescription);
		objects.put("propability", propability);
		objects.put("propabilityBase", propabilityBase);
		return objects;
	}

	public void read(Map<String, Object> data) {
		mobPluginName = (MobPluginNames) data.get("plugin");
		mobName = (String) data.get("mobName");
		reward = (String) data.get("rewardPrize");
		consoleRunCommand = (String) data.get("consoleRunCommand");
		rewardDescription = (String) data.get("rewardDescription");
		propability = toInt(data.get("propability"));
		propabilityBase = toInt(data.get("propabilityBase"));
	}

	public void read(ConfigurationSection section)
			throws InvalidConfigurationException, IllegalStateException {
		
		mobPluginName = MobPluginNames
				.valueOf(section.get("plugin").toString());
		mobName = section.getString("mobName");
		reward = section.getString("rewardPrize");
		consoleRunCommand = section.getString("consoleRunCommand");
		rewardDescription = section.getString("rewardDescription");
		propability = section.getInt("propability");
		propabilityBase = section.getInt("propabilityBase");
	}

}
