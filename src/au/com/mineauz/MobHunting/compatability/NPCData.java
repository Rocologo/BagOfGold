package au.com.mineauz.MobHunting.compatability;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import au.com.mineauz.MobHunting.MobType.MobPlugin;

public class NPCData {
	private MobPlugin mobPlugin;
	private String npcName = "";
	private String rewardPrize = "5";
	private String consoleRunCommand = "";
	private String rewardDescription = "";
	private int propability = 100;
	private int propabilityBase = 100;

	public NPCData(MobPlugin citizens, String npcName, String rewardPrize,
			String cmd, String cmdDesc, int propability, int propabilityBase) {
		this.mobPlugin = citizens;
		this.npcName = npcName;
		this.rewardPrize = rewardPrize;
		this.consoleRunCommand = cmd;
		this.rewardDescription = cmdDesc;
		this.propability = propability;
		this.setPropabilityBase(propabilityBase);
	}

	// **************************************************************************
	// Getters and Setters
	// **************************************************************************
	public MobPlugin getMobPlugin() {
		return mobPlugin;
	}

	public void setMobPlugin(MobPlugin mobPlugin) {
		this.mobPlugin = mobPlugin;
	}

	public String getMobName() {
		return npcName;
	}

	public void setMobName(String npcName) {
		this.npcName = npcName;
	}

	public String getRewardPrize() {
		return rewardPrize;
	}

	public void setRewardPrize(String rewardPrize) {
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

	public int getPropabilityBase() {
		return propabilityBase;
	}

	public void setPropabilityBase(int propabilityBase) {
		this.propabilityBase = propabilityBase;
	}

	// **************************************************************************
	// Load & Save
	// **************************************************************************
	public NPCData() {
		super();
	}

	public void save(ConfigurationSection section) {
		section.set("plugin", mobPlugin.toString());
		section.set("mobName", npcName);
		section.set("rewardPrize", rewardPrize);
		section.set("consoleRunCommand", consoleRunCommand);
		section.set("rewardDescription", rewardDescription);
		section.set("propability", propability);
		section.set("propabilityBase", propabilityBase);
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
		objects.put("plugin", mobPlugin);
		objects.put("mobName", npcName);
		objects.put("rewardPrize", rewardPrize);
		objects.put("consoleRunCommand", consoleRunCommand);
		objects.put("rewardDescription", rewardDescription);
		objects.put("propability", propability);
		objects.put("propabilityBase", propabilityBase);
		return objects;
	}

	public void read(Map<String, Object> data) {
		mobPlugin = (MobPlugin) data.get("plugin");
		npcName = (String) data.get("mobName");
		rewardPrize = (String) data.get("rewardPrize");
		consoleRunCommand = (String) data.get("consoleRunCommand");
		rewardDescription = (String) data.get("rewardDescription");
		propability = toInt(data.get("propability"));
		propabilityBase = toInt(data.get("propabilityBase"));
	}

	public void read(ConfigurationSection section)
			throws InvalidConfigurationException, IllegalStateException {
		mobPlugin = MobPlugin.valueOf(section.get("plugin").toString());
		npcName = section.getString("mobName");
		rewardPrize = section.getString("rewardPrize");
		consoleRunCommand = section.getString("consoleRunCommand");
		rewardDescription = section.getString("rewardDescription");
		propability = section.getInt("propability");
		propabilityBase = section.getInt("propabilityBase");
	}

}
