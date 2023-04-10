package one.lindegaard.BagOfGold.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.CustomItemsLib.config.AutoConfig;
import one.lindegaard.CustomItemsLib.config.ConfigField;

public class ConfigManager extends AutoConfig {

	BagOfGold plugin;

	public ConfigManager(BagOfGold plugin, File file) {

		super(file);

		this.plugin = plugin;

		setCategoryComment("economy", "########################################################################"
				+ "\nEconomy Settings" + "\n########################################################################");

		setCategoryComment("dropmoneyonground",
				"########################################################################"
						+ "\nDropMoneyOnGround Settings / BagOfGold Item Settings"
						+ "\n########################################################################");

		setCategoryComment("gringotts", "########################################################################"
				+ "\nGringotts Style" + "\n########################################################################"
				+ "\nOBS DONT USE GRINGOTTS_STYLE YET! Gringotts seems to die with Minecraft 1.13, but I have made a Gringotts style"
				+ "\nfor Gringotts lovers (https://www.spigotmc.org/resources/gringotts.42071/)");

		setCategoryComment("banker",
				"########################################################################" + "\nBank settings"
						+ "\n########################################################################"
						+ "\nThe Banker is dependend on Citizens2 plugin.");

		setCategoryComment("plugins",
				"########################################################################"
						+ "\nIntegration to other plugins."
						+ "\n########################################################################");

		setCategoryComment("plugins.citizens",
				"########################################################################"
						+ "\nCitizens / MasterMobHunter settings."
						+ "\n########################################################################");

		setCategoryComment("plugins.shopkeepers",
				"########################################################################" + "\nShopkeepers settings."
						+ "\n########################################################################");

		setCategoryComment("plugins.essentials",
				"########################################################################" + "\nEssentials settings."
						+ "\n########################################################################");

		setCategoryComment("plugins.perworldinventory",
				"########################################################################"
						+ "\nPerWorldInventory settings."
						+ "\n########################################################################");

		setCategoryComment("plugins.worldguard",
				"########################################################################" + "\nWorldguard"
						+ "\n########################################################################");

		setCategoryComment("plugins.worldedit",
				"########################################################################" + "\nWorldedit"
						+ "\n########################################################################");

		setCategoryComment("plugins.bossbarapi",
				"########################################################################" + "\nBossBarAPI"
						+ "\n########################################################################");

		setCategoryComment("plugins.barapi", "########################################################################"
				+ "\nBarApi" + "\n########################################################################");

		setCategoryComment("plugins.titlemanager",
				"########################################################################" + "\nTitleManager"
						+ "\n########################################################################");

		setCategoryComment("plugins.titleapi",
				"########################################################################" + "\nTitleApi"
						+ "\n########################################################################");

		setCategoryComment("plugins.actionbar",
				"########################################################################" + "\nActionbar"
						+ "\n########################################################################");

		setCategoryComment("plugins.actionbarapi",
				"########################################################################" + "\nActionbarAPI"
						+ "\n########################################################################");

		setCategoryComment("plugins.actionannouncer",
				"########################################################################" + "\nActionAnnouncer"
						+ "\n########################################################################");

		setCategoryComment("plugins.cmi", "########################################################################"
				+ "\nCMI" + "\n########################################################################");

		setCategoryComment("plugins.placeholderapi",
				"########################################################################" + "\nPlaceholderApi"
						+ "\n########################################################################");

		setCategoryComment("general", "########################################################################"
				+ "\nGeneral Settings" + "\n########################################################################");

		setCategoryComment("database",
				"########################################################################" + "\nDatabase Settings."
						+ "\n########################################################################");

		setCategoryComment("updates", "########################################################################"
				+ "\nUpdate settings" + "\n########################################################################");

	}

	// #####################################################################################
	// Economy settings
	// #####################################################################################

	@ConfigField(name = "use-bagofgold-as-an-economy-plugin", category = "economy", comment = "Set this to true if you want tha BagOfGold to be a real Economy, which can be used for "
			+ "\nbuying/selling other items. If you want the BagOfGold to be items which can be bought/sold,"
			+ "\nset this to false")
	public boolean useBagOfGoldAsAnEconomyPlugin = true;

	// @ConfigField(name = "number-format", category = "economy", comment = "Here
	// you can change the way the numbers is formatted when you use BagOfGold as an
	// EconomyPlugin.")
	// public String numberFormat = "#.#####";

	// @ConfigField(name = "reward_rounding", category = "economy", comment =
	// "Rounding of rewards when you uses a range or %. (ex creeperPrize=10:30) the
	// reward."
	// + "\nAll numbers except 0 can be used. "
	// + "\nSet rounding_reward=1 if you want integers. IE. 10,11,12,13,14..."
	// + "\nSet rounding_reward=0.01 if you want 2 decimals 10.00, 10.01, 10.02...
	// integers."
	// + "\nSet rounding_reward=5 if you want multipla of 5 IE. 10,15,20,25..."
	// + "\nSet rounding_reward=2 if you want multipla of 2 IE. 10,12,14,16...")
	// public double rewardRounding = 1;

	// @ConfigField(name = "minimum_reward", category = "economy", comment = "This
	// is the minimum reward which will which will be paid to the player 0.01 will
	// be fine"
	// + "\nin most installation, but Gringott users who want very low rewards (like
	// 0.001 for killing"
	// + "\na mob) will have to lower the minimum reward. Remember that some
	// multipliers are less than 1"
	// + "\n and grinding detection and penalties. The minimum_reward should
	// therefor be less than 10%"
	// + "\n of smallest reward. In the Gringotts example minimum_reward should be
	// 0.0001 or 0.00005.")
	// public double minimumReward = 0.01;

	// @ConfigField(name = "limit_per_bag", category = "economy", comment = "If you
	// only want the bags to be able to contain a "
	// + "\ncertain amount of gold you can set the limit here. Set limit_per_bag:
	// 9999999999 to disable the limit.")
	// public double limitPerBagOld = 10000;

	@ConfigField(name = "bank_name_on_sign", category = "economy", comment = "Here you can change then name of your sign banks.")
	public String bankname = "BagOfGold Bank";

	// #####################################################################################
	// Gringotts style
	// #####################################################################################
	@ConfigField(name = "denomination", category = "gringotts", comment = "This is deprecated. Please use bagofgoldcore.yml settings in BagOfGold folder instead.")
	public LinkedHashMap<String, String> gringottsDenominationOld = new LinkedHashMap<String, String>();
	{
		gringottsDenominationOld.put("EMERALD_BLOCK", "9");
		gringottsDenominationOld.put("EMERALD", "1");
	}

	// #####################################################################################
	// Banker Settings
	// #####################################################################################
	@ConfigField(name = "banker-name", category = "banker", comment = "This is the name/displayname of the banker.")
	public String bankerName = "Bank Manager";

	@ConfigField(name = "banker-texture", category = "banker", comment = "This is the bankers Skin. You can find other skins at https://mineskin.org/")
	public String bankerTexture = "ewogICJ0aW1lc3RhbXAiIDogMTYxMzc0MDE5NjE5MywKICAicHJvZmlsZUlkIiA6ICIwNWQ0NTNiZWE0N2Y0MThiOWI2ZDUzODg0MWQxMDY2MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJFY2hvcnJhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzcwMTkyODkwNTU4OTBjMTIxMWFjZDM3MjVhNzBmMDUxNTVlNmVjMTZlYTgzMjZjMzdmNDFiMjE2MTI2NjFkYjgiCiAgICB9CiAgfQp9";

	@ConfigField(name = "banker-signature", category = "banker", comment = "This is the skin signature. Make sure that the signature matches the texture from https://mineskin.org/")
	public String bankerSignature = "dVWdZ9OaO0PySSQO3TyfTh0sB6faiXvuMAGFALNw48lWehuZsCpIIGjxaGHr+i/VR+xXW5lrKIB82TedbfGy8wyhYXmfbo9b8BtXMlRye0duwyMim2Bug1H62BB3VD/Z/4n8wF+Y3r8dfrSCCX8b3B6Rr2C3Y1i086mVD7a4g42Cj/1e+nlXosNenqkm0vKtvUfb0vBVIAx4xuOjexRyCwGEpcDW30n70lLzuljtDE0nrhfuyvhKif7HDyLrh+22nunkVv5KjdK5UqABcEXz8SB0mdEVhxzwNOPZYzylVMYuwOXFhG6Q3EavqRnpNP6E8VLvuJ9qqmnStE1ajRLwphDdbOnoh3EogBkhKwh0ayy0s/ZM8IxPvQAUXKDUlf71j/IeArS8nBfTO7U8pNPGbC/9NnRxa9vvqiEECxzqFwrDigEstZFENUqzlWby6MZFN08xqySaSahPi7FACmVCn9wkcImtWs+N7y2N7Mk4spX3uv5lwhC5taj+/WqZVxKLGreCs3pnIKnX8+wmEEYT2FIZHXJAWGrqizEp6/gpCkwl9X2pgrj89yG4NpaYiNAyjMu9tXgfoBOfbaRriSomk5jJLu5C93Grvd98LKmXWpOj1KunwmST4JdtF/BTk/sSCnr9RXDf9iMbnSM/ZC4PFgKQyzzJ1PC8yC9FuUhk4WM=";

	@ConfigField(name = "banker-actions", category = "banker", comment = "Change the numbers or add new lines if you like.")
	public LinkedHashMap<String, String> actions = new LinkedHashMap<String, String>();
	{
		actions.put("deposit10", "10");
		actions.put("deposit100", "100");
		actions.put("deposit1000", "1000");
		actions.put("depositall", "All");
		actions.put("withdraw10", "10");
		actions.put("withdraw100", "100");
		actions.put("withdraw1000", "1000");
		actions.put("withdrawall", "All");

	}

	@ConfigField(name = "calculate-interest", category = "banker.interest", comment = "Enable interst calculation. Players can get an interest per day/week")
	public boolean calculateInterests = false;

	@ConfigField(name = "period", category = "banker.interest", comment = "How often do you want the Interest to be calculated and added to your"
			+ "\naccount: DAY, WEEK, MONTH, YEAR. 1 Minecraft day = 20 min, "//
			+ "\n1 Minecraft week = 2.3 hours, 1 Minecraft nmonth = 10 hours,"//
			+ "\n1 Minecraft year = 121.75 hours"//
			+ "\nSource: https://minecraft.gamepedia.com/Day-night_cycle")
	public String interestPeriod = "WEEK";

	@ConfigField(name = "interest", category = "banker.interest", comment = "This is the interest in % per Minecraft year (121.75 hours). Be careful not to make"
			+ "\nit too big, it can ruin your server economy. Default is 20%")
	public double interest = 20;

	// #####################################################################################
	// Plugin integration
	// #####################################################################################
	@ConfigField(name = "enable-integration-citizens", category = "plugins.citizens", comment = "Enable integration with Citizens2")
	public boolean enableIntegrationCitizens = true;

	@ConfigField(name = "enable-integration-shopkeepers-beta", category = "plugins.shopkeepers", comment = "Enable integration with Shopkeepers. This is still in BETA TEST and "
			+ "\nyou should not use the Shopkeeper integration. "
			+ "\nYou can only SELL items and get BagOfGold at the moment!!!")
	public boolean enableIntegrationShopkeepersBETA = false;

	@ConfigField(name = "enable-integration-essentials", category = "plugins.essentials", comment = "Enable integration with Essentials"
			+ "\nhttp://dev.bukkit.org/bukkit-plugins/essentialsx/")
	public boolean enableIntegrationEssentials = true;

	@ConfigField(name = "enable-integration-perworldinventory", category = "plugins.perworldinventory", comment = "Enable integration with PerWorldInventory"
			+ "\nhttp://dev.bukkit.org/bukkit-plugins/essentialsx/")
	public boolean enableIntegrationPerWorldInventory = true;

	@ConfigField(name = "enable_integration_placeholderapi", category = "plugins.placeholderapi", comment = "Enable/Disable integration with PlaceholderAPI."
			+ "\nhttps://www.spigotmc.org/resources/placeholderapi.6245/")
	public boolean enableIntegrationPlaceholderAPI = true;

	@ConfigField(name = "worldguard.enable_integration_worldguard", category = "plugins", comment = "Enable/Disable integration with WorldGuard")
	public boolean enableIntegrationWorldGuard = true;

	@ConfigField(name = "worldedit.enable_integration_worldedit", category = "plugins", comment = "Enable/Disable integration with WorldEdit")
	public boolean enableIntegrationWorldEdit = true;

	// #####################################################################################
	// Generel settings
	// #####################################################################################

	@ConfigField(name = "language", category = "general", comment = "The language (file) to use. You can put the name of the language file as the language code "
			+ "\n(eg. en_US, fr_FR, hu_HU, pt_BR, zh_CN, ru_RU ect.) or you can specify the name of a custom file without the .lang\nPlease check the lang/ folder for a list of all available translations.")
	public String language = "en_US";

	// @ConfigField(name = "save-period", category = "general", comment = "Time
	// between saves in ticks (20 ticks ~ 1 sec) This number must be higher that
	// 1200 ticks = 2 minutes,"
	// + "\nbut I recommend to save every 5th minute = 6000 ticks")
	// public int savePeriod_old = 6000;

	@ConfigField(name = "debug", category = "general", comment = "If kills are not being registered in mob hunting. Enable this to see why they arent")
	public boolean debug = false;

	@ConfigField(name = "newplayer_learning_mode", category = "general", comment = "When a new playerjoins the server he will by default start"
			+ "\nin 'LEARNING MODE' and get extra information about when he get rewards and not,"
			+ "\nwhen killing Mobs. The player can disable this InGame by using the command '/mobhunt learn'")
	public boolean learningMode = true;

	@ConfigField(name = "backup", category = "general", comment = "Backup config on each server start / reload")
	public boolean backup = true;

	// #####################################################################################
	// Database
	// #####################################################################################
	@ConfigField(name = "type", category = "database", comment = "Type of database to use. Valid values are: sqlite, mysql")
	public String databaseType = "sqlite";

	@ConfigField(name = "database_name", category = "database")
	public String databaseName = "bagofgold";

	@ConfigField(name = "username", category = "database.mysql")
	public String databaseUsername = "user";

	@ConfigField(name = "password", category = "database.mysql")
	public String databasePassword = "password";

	@ConfigField(name = "host", category = "database.mysql")
	public String databaseHost = "localhost:3306";

	@ConfigField(name = "useSSL", category = "database.mysql")
	public String databaseUseSSL = "false";

	@ConfigField(name = "database_version", category = "database", comment = "This is the database layout version. Mostly for internal use and you should not need"
			+ "\nto change this value. In case you decide to delete your database and let it recreate"
			+ "\nor if you change database type sqlite/mysql you should set this value to 0 again.")
	public int databaseVersion = 0;

	// #####################################################################################
	// Update Settings
	// #####################################################################################
	@ConfigField(name = "update-check", category = "updates", comment = "Check if there is a new version of the plugin available.")
	public boolean updateCheck = true;

	@ConfigField(name = "check_every", category = "updates", comment = "Set the number of seconds between each check. Recommended setting is"
			+ "\ncheck_every: 7200 ~ to check every second hour.")
	public int checkEvery = 7200;

	@ConfigField(name = "autoupdate", category = "updates", comment = "Set 'autoupdate: true' if you want new updates downloaded and installed."
			+ "\nYou will still have to reboot the server manually.")
	public boolean autoupdate = false;

	@Override
	protected void onPostLoad() throws InvalidConfigurationException {
		plugin.getMessages().setLanguage(language + ".lang");
	}

	public void backupConfig(File mFile) {
		File backupFile = new File(mFile.toString());
		int count = 0;
		while (backupFile.exists() && count++ < 1000) {
			backupFile = new File("plugins/BagOfGold/backup/" + mFile.getName() + ".bak" + count);
		}
		if (mFile.exists())
			try {
				if (!backupFile.exists())
					backupFile.mkdirs();
				Files.copy(mFile.toPath(), backupFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
						StandardCopyOption.REPLACE_EXISTING);
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RESET
						+ " Config.yml was backed up to " + backupFile.getPath());
			} catch (IOException e1) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold]" + ChatColor.RED
						+ "[ERROR] - Could not backup config.yml file to plugins/bagofgold/config.yml. Delete some old backups");
				e1.printStackTrace();
			}
	}

	public static int getConfigVersion(File file) {
		if (!file.exists())
			return -1;

		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		return config.getInt("general.config_version", config.contains("general.kill-debug") == true ? 0 : -1);
	}

}
