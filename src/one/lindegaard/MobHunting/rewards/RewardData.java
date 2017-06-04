package one.lindegaard.MobHunting.rewards;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import one.lindegaard.MobHunting.mobs.MobPlugin;

public class RewardData {
	private final static int PROGRESS_ACHIEVEMENT_LEVEL1=50;
	private MobPlugin mobPluginName;
	private String mobType = "";
	private String mobName = "";
	private String reward = "5";
	private String consoleRunCommand = "";
	private String rewardDescription = "";
	@Deprecated
	private int propability = 100;
	@Deprecated
	private int propabilityBase = 100;
	private double chance = 1;
	private int progressAchievementLevel1 = PROGRESS_ACHIEVEMENT_LEVEL1;
	// McMMO
	private double mcMMOSkillRewardChance = 0.02;
	private int mcMMOSkillRewardAmount = 1;

	public RewardData() {
		super();
	}

	public RewardData(MobPlugin pluginName, String mobType, String mobName, String rewardPrize, String cmd,
			String cmdDesc, double chance, int mcmmo_xp, double mcmmo_chance) {
		this.mobPluginName = pluginName;
		this.mobType = mobType;
		this.mobName = mobName;
		this.reward = rewardPrize;
		this.consoleRunCommand = cmd;
		this.rewardDescription = cmdDesc;
		this.chance = chance;
		this.setMcMMOSkillRewardAmount(mcmmo_xp);
		this.setMcMMOSkillRewardChance(mcmmo_chance);
	}

	// **************************************************************************
	// Getters and Setters
	// **************************************************************************
	public MobPlugin getMobPlugin() {
		return mobPluginName;
	}

	public void setMobPlugin(MobPlugin mobPlugin) {
		this.mobPluginName = mobPlugin;
	}

	public String getMobType() {
		return mobType;
	}

	public void setMobType(String type) {
		this.mobType = type;
	}

	public String getMobName() {
		if (mobName == null || mobName.equals(""))
			return mobType;
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

	public double getChance() {
		return chance;
	}

	public void setChance(int chance) {
		this.chance = chance;
	}

	public int getAchivementLevel1() {
		return progressAchievementLevel1;
	}

	public void setAchivementLevel1(int achivementLevel1) {
		this.progressAchievementLevel1 = achivementLevel1;
	}

	// **************************************************************************
	// Load & Save
	// **************************************************************************
	@SuppressWarnings("unused")
	private long toLong(Object obj) {
		if (obj instanceof Long)
			return (Long) obj;
		else if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number");
	}

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

	public void save(ConfigurationSection section) {
		section.set("plugin", mobPluginName.toString());
		section.set("mobName", mobName);
		section.set("rewardPrize", reward);
		section.set("consoleRunCommand", consoleRunCommand);
		section.set("rewardDescription", rewardDescription);
		section.set("chance", chance);
		section.set("mcmmo_chance", mcMMOSkillRewardChance);
		section.set("mcmmo_xp", mcMMOSkillRewardAmount);
		section.set("achievement_level1", progressAchievementLevel1);
	}

	public Map<String, Object> write() {
		HashMap<String, Object> objects = new HashMap<String, Object>();
		objects.put("plugin", mobPluginName);
		objects.put("mobName", mobName);
		objects.put("rewardPrize", reward);
		if (consoleRunCommand != null)
			objects.put("consoleRunCommand", consoleRunCommand);
		else
			objects.put("consoleRunCommand", "''");
		if (rewardDescription != null)
			objects.put("rewardDescription", rewardDescription);
		else
			objects.put("rewardDescription", "''");
		objects.put("chance", chance);
		objects.put("mcmmo_chance", mcMMOSkillRewardChance);
		objects.put("mcmmo_xp", mcMMOSkillRewardAmount);
		objects.put("achievement_level1", progressAchievementLevel1);
		return objects;
	}

	public void read(Map<String, Object> data) {
		mobPluginName = (MobPlugin) data.get("plugin");
		mobName = (String) data.get("mobName");
		reward = (String) data.get("rewardPrize");
		consoleRunCommand = (String) data.get("consoleRunCommand");
		rewardDescription = (String) data.get("rewardDescription");
		if (data.get("chance") == null) {
			propability = toInt(data.get("propability"));
			propabilityBase = toInt(data.get("propabilityBase"));
			chance = propability / propabilityBase;
		} else
			chance = toDouble(data.get("chance"));
		mcMMOSkillRewardChance = data.get("mcmmo_chance") != null ? (Double) toDouble(data.get("mcmmo_chance")) : 0.02;
		mcMMOSkillRewardAmount = data.get("mcmmo_xp") != null ? (int) toInt(data.get("mcmmo_xp")) : 1;
		progressAchievementLevel1 = data.get("achivement_level1") != null ? (int) toInt(data.get("achivement_level1")) : PROGRESS_ACHIEVEMENT_LEVEL1;
	}

	public void read(ConfigurationSection section) throws InvalidConfigurationException, IllegalStateException {
		mobPluginName = MobPlugin.valueOf(section.get("plugin").toString());
		mobName = section.getString("mobName");
		reward = section.getString("rewardPrize");
		consoleRunCommand = section.getString("consoleRunCommand", "");
		rewardDescription = section.getString("rewardDescription", "");
		if (section.get("chance") != null)
			chance = section.getDouble("chance");
		else {
			propability = section.getInt("propability");
			propabilityBase = section.getInt("propabilityBase");
			chance = propability / propabilityBase;
		}
		mcMMOSkillRewardChance = section.getDouble("mcmmo_chance", 0.02);
		mcMMOSkillRewardAmount = section.getInt("mcmmo_xp", 1);
		progressAchievementLevel1 = section.getInt("achivement_level1",PROGRESS_ACHIEVEMENT_LEVEL1);
	}

	public double getMcMMOSkillRewardChance() {
		return mcMMOSkillRewardChance;
	}

	public void setMcMMOSkillRewardChance(double mcMMOSkillRewardChance) {
		this.mcMMOSkillRewardChance = mcMMOSkillRewardChance;
	}

	public int getMcMMOSkillRewardAmount() {
		return mcMMOSkillRewardAmount;
	}

	public void setMcMMOSkillRewardAmount(int mcMMOSkillRewardAmount) {
		this.mcMMOSkillRewardAmount = mcMMOSkillRewardAmount;
	}

}
