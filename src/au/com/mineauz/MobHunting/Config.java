package au.com.mineauz.MobHunting;

import java.io.File;

import org.bukkit.configuration.InvalidConfigurationException;

import au.com.mineauz.MobHunting.util.AutoConfig;
import au.com.mineauz.MobHunting.util.ConfigField;

public class Config extends AutoConfig
{
	public Config( File file )
	{
		super(file);
		
		setCategoryComment("mobs", "Here is where you set the base prize in $ for killing a mob of each type"); //$NON-NLS-1$ //$NON-NLS-2$
		setCategoryComment("boss", "Here is where you set the base prize in $ for killing the bosses"); //$NON-NLS-1$ //$NON-NLS-2$
		setCategoryComment("bonus", "These are bonus multipliers that can modify the base prize. \nREMEMBER: These are not in $ but they are a multiplier. Setting to 1 will disable them."); //$NON-NLS-1$ //$NON-NLS-2$
		setCategoryComment("penalty", "These are penalty multipliers that can modify the base prize. \nREMEMBER: These are not in $ but they are a multiplier. Setting to 1 will disable them."); //$NON-NLS-1$ //$NON-NLS-2$
		
		setCategoryComment("special", "Here is where you set the prize in $ for achieving a special kill. \nFor each achievment you can run a console command to give the player a reward. \nYou can you the following variables {player},{world}.\nAn example could be to give the player permission to fly \nfor 1 hour or use give command to the player items.\nYou can also specify the message send to the player."); //$NON-NLS-1$ //$NON-NLS-2$
		
	}
	
	@ConfigField(name="blaze", category="mobs")
	public double blazePrize = 1.0;
	@ConfigField(name="creeper", category="mobs")
	public double creeperPrize = 1.0;
	@ConfigField(name="silverfish", category="mobs")
	public double silverfishPrize = 0.1;
	@ConfigField(name="pigman", category="mobs")
	public double pigMan = 0.5;
	@ConfigField(name="endermen", category="mobs")
	public double endermenPrize = 1.0;
	@ConfigField(name="giant", category="mobs")
	public double giantPrize = 2.0;
	@ConfigField(name="skeleton", category="mobs")
	public double skeletonPrize = 0.5;
	@ConfigField(name="wither-skeleton", category="mobs")
	public double witherSkeletonPrize = 1.0;
	@ConfigField(name="spider", category="mobs")
	public double spiderPrize = 0.5;
	@ConfigField(name="cave-spider", category="mobs")
	public double caveSpiderPrize = 0.8;
	@ConfigField(name="witch", category="mobs")
	public double witchPrize = 1.0;
	@ConfigField(name="zombie", category="mobs")
	public double zombiePrize = 0.5;
	@ConfigField(name="ghast", category="mobs")
	public double ghastPrize = 2.0;
	@ConfigField(name="endermite", category="mobs")
	public double endermitePrize = 1.0;
	@ConfigField(name="guardian", category="mobs")
	public double guardianPrize = 1.0;
	@ConfigField(name="killerrabbit", category="mobs")
	public double killerrabbitPrize = 0.5;
	
	@ConfigField(name="slime-base", category="mobs", comment="This is multiplied by the size of the slime. So a big natural slime is 4x this value")
	public double slimeTinyPrize = 0.25;
	
	@ConfigField(name="wither", category="boss")
	public double witherPrize = 500.0;
	@ConfigField(name="enderdragon", category="boss")
	public double enderdragonPrize = 1000.0;
	
	@ConfigField(name="sneaky", category="bonus")
	public double bonusSneaky = 2.0;
	@ConfigField(name="return-to-sender", category="bonus")
	public double bonusReturnToSender = 2.0;
	@ConfigField(name="push-off-cliff", category="bonus")
	public double bonusSendFalling = 2.0;
	@ConfigField(name="no-weapon", category="bonus")
	public double bonusNoWeapon = 2.0;
	@ConfigField(name="far-shot", category="bonus")
	public double bonusFarShot = 4.0;
	@ConfigField(name="mounted", category="bonus")
	public double bonusMounted = 1.5;
	@ConfigField(name="friendly-fire", category="bonus")
	public double bonusFriendlyFire = 4;
	@ConfigField(name="bonus-mob", category="bonus")
	public double bonusBonusMob = 10;
	@ConfigField(name="critical", category="bonus")
	public double bonusCritical = 2;
	
	@ConfigField(name="bonus-mob-chance", category="bonus", comment="This is the chance (% chance 0-100) that a bonus mob will spawn.")
	public double bonusMobChance = 0.2;
	
	@ConfigField(name="charged-kill", category="special")
	public double specialCharged = 100;
	@ConfigField(name="charged-kill-cmd", category="special")
	public String specialChargedCmd = "";
	@ConfigField(name="charged-kill-cmd-desc", category="special")
	public String specialChargedCmdDesc = "";
	@ConfigField(name="creeper-punch", category="special")
	public double specialCreeperPunch = 100;
	@ConfigField(name="creeper-punch-cmd", category="special")
	public String specialCreeperPunchCmd = "";
	@ConfigField(name="creeper-punch-cmd-desc", category="special")
	public String specialCreeperPunchCmdDesc = "";
	@ConfigField(name="axe-murderer", category="special")
	public double specialAxeMurderer = 20;
	@ConfigField(name="axe-murderer-cmd", category="special")
	public String specialAxeMurdererCmd = "";
	@ConfigField(name="axe-murderer-cmd-desc", category="special")
	public String specialAxeMurdererCmdDesc = "";
	@ConfigField(name="recordhungry", category="special")
	public double specialRecordHungry = 50;
	@ConfigField(name="recordhungry-cmd", category="special")
	public String specialRecordHungryCmd = "";
	@ConfigField(name="recordhungry-cmd-desc", category="special")
	public String specialRecordHungryCmdDesc = "";
	@ConfigField(name="infighting", category="special")
	public double specialInfighting = 50;
	@ConfigField(name="infighting-cmd", category="special")
	public String specialInfightingCmd = "";
	@ConfigField(name="infighting-cmd-desc", category="special")
	public String specialInfightingCmdDesc = "";
	@ConfigField(name="by-the-book", category="special")
	public double specialByTheBook = 20;
	@ConfigField(name="by-the-book-cmd", category="special")
	public String specialByTheBookCmd = "";
	@ConfigField(name="by-the-book-cmd-desc", category="special")
	public String specialByTheBookCmdDesc = "";
	@ConfigField(name="creepercide", category="special")
	public double specialCreepercide = 50;
	@ConfigField(name="creepercide-cmd", category="special")
	public String specialCreepercideCmd = "";
	@ConfigField(name="creepercide-cmd-desc", category="special")
	public String specialCreepercideCmdDesc = "";
	@ConfigField(name="hunt-begins", category="special")
	public double specialHuntBegins = 10;
	@ConfigField(name="hunt-begins-cmd", category="special")
	public String specialHuntBeginsCmd = "";
	@ConfigField(name="hunt-begins-cmd-desc", category="special")
	public String specialHuntBeginsCmdDesc = "";
	@ConfigField(name="itsmagic", category="special")
	public double specialItsMagic = 20;
	@ConfigField(name="itsmagic-cmd", category="special")
	public String specialItsMagicCmd = "";
	@ConfigField(name="itsmagic-cmd-desc", category="special")
	public String specialItsMagicCmdDesc = "";
	@ConfigField(name="fancypants", category="special")
	public double specialFancyPants = 50;
	@ConfigField(name="fancypants-cmd", category="special")
	public String specialFancyPantsCmd = "";
	@ConfigField(name="fancypants-cmd-desc", category="special")
	public String specialFancyPantsCmdDesc = "";
	@ConfigField(name="master-sniper", category="special")
	public double specialMasterSniper = 200;
	@ConfigField(name="master-sniper-cmd", category="special")
	public String specialMasterSniperCmd = "";
	@ConfigField(name="master-sniper-cmd-desc", category="special")
	public String specialMasterSniperCmdDesc = "";
	@ConfigField(name="fangmaster", category="special")
	public double specialFangMaster = 50;
	@ConfigField(name="fangmaster-cmd", category="special")
	public String specialFangMasterCmd = "";
	@ConfigField(name="fangmaster-cmd-desc", category="special")
	public String specialFangMasterCmdDesc = "";
	
	@ConfigField(name="hunter1", category="special")
	public double specialHunter1 = 100;
	@ConfigField(name="hunter1-cmd", category="special")
	public String specialHunter1Cmd = "";
	@ConfigField(name="hunter1-cmd-desc", category="special")
	public String specialHunter1CmdDesc = "";
	@ConfigField(name="hunter2", category="special")
	public double specialHunter2 = 250;
	@ConfigField(name="hunter2-cmd", category="special")
	public String specialHunter2Cmd = "";
	@ConfigField(name="hunter2-cmd-desc", category="special")
	public String specialHunter2CmdDesc = "";
	@ConfigField(name="hunter3", category="special")
	public double specialHunter3 = 500;
	@ConfigField(name="hunter3-cmd", category="special")
	public String specialHunter3Cmd = "";
	@ConfigField(name="hunter3-cmd-desc", category="special")
	public String specialHunter3CmdDesc = "";
	@ConfigField(name="hunter4", category="special")
	public double specialHunter4 = 1000;
	@ConfigField(name="hunter4-cmd", category="special")
	public String specialHunter4Cmd = "";
	@ConfigField(name="hunter4-cmd-desc", category="special")
	public String specialHunter4CmdDesc = "";
	
	@ConfigField(name="level1", category="killstreak")
	public int killstreakLevel1 = 5;
	@ConfigField(name="level1-multiplier", category="killstreak")
	public double killstreakLevel1Mult = 1.5;
	@ConfigField(name="level2", category="killstreak")
	public int killstreakLevel2 = 10;
	@ConfigField(name="level2-multiplier", category="killstreak")
	public double killstreakLevel2Mult = 2;
	@ConfigField(name="level3", category="killstreak")
	public int killstreakLevel3 = 20;
	@ConfigField(name="level3-multiplier", category="killstreak")
	public double killstreakLevel3Mult = 3;
	@ConfigField(name="level4", category="killstreak")
	public int killstreakLevel4 = 40;
	@ConfigField(name="level4-multiplier", category="killstreak")
	public double killstreakLevel4Mult = 4;
	
	@ConfigField(name="enable-grinding-penalty", category="penalty", comment="Enabling this prevents a player from earning too much money from using a mob grinder")
	public boolean penaltyGrindingEnable = true;
	
	@ConfigField(name="flyingPenalty", category="penalty", comment="If a player flies at any point in a fight, this penalty will be applied")
	public double penaltyFlying = 0.5;
	
	@ConfigField(name="disabled-in-worlds", category="general", comment="Put the names of the worlds here that you do not wish for mobhunting to be enabled in.")
	public String[] disabledInWorlds = new String[0];
	
	@ConfigField(name="language", category="general", comment="The language (file) to use. You can put the name of the language file as the language code (eg. en_US, de_DE, fr_FR, ect.) or you can specify the name of a custom file without the .lang\nPlease check the lang/ folder for a list of all available translations.")
	public String language = "en_US"; //$NON-NLS-1$
	
	@ConfigField(name="broadcast-achievement", category="general", comment="Should achievements be broadcasted?")
	public boolean broadcastAchievement = true;
	
	@ConfigField(name="broadcast-first-achievement", category="general", comment="Should the hunt begins achievement be broadcasted?")
	public boolean broadcastFirstAchievement = true;
	
	@ConfigField(name="save-period", category="general", comment="Time between saves in ticks")
	public int savePeriod = 1200;
	
	@ConfigField(name="leaderboard-update-period", category="general", comment="Time between leaderboard updates in ticks")
	public int leaderboardUpdatePeriod = 1200;
	
	@ConfigField(name="type", category="database", comment="Type of database to use. Valid values are: sqlite, mysql")
	public String databaseType = "sqlite"; //$NON-NLS-1$
	@ConfigField(name="username", category="database")
	public String databaseUsername = "user"; //$NON-NLS-1$
	@ConfigField(name="password", category="database")
	public String databasePassword = "password"; //$NON-NLS-1$
	
	@ConfigField(name="host", category="database")
	public String databaseHost = "localhost:3306"; //$NON-NLS-1$
	@ConfigField(name="database", category="database")
	public String databaseName = "mobhunting"; //$NON-NLS-1$
	
	@ConfigField(name="enable", category="assists", comment="Enabling assist allows the second last player to attack a mob to get some money from it")
	public boolean enableAssists = true;
	@ConfigField(name="multiplier", category="assists", comment="This should be a value that is multiplied against the mobs base kill value. This is used to determine how much money an assister gets.")
	public double assistMultiplier = 0.25;
	@ConfigField(name="allow-killstreak", category="assists", comment="Should killstreak be applied to assists")
	public boolean assistAllowKillstreak = false;
	@ConfigField(name="timeout", category="assists", comment="Time in seconds after attacking a mob that can be counted as an assist")
	public int assistTimeout = 4;
	
	@ConfigField(name="kill-timeout", category="general", comment="Time in seconds after attacking a mob that can be counted as a kill")
	public int killTimeout = 4;
	
	@ConfigField(name="kill-debug", category="general", comment="If kills are not being registered in mob hunting. Enable this to see why they arent")
	public boolean killDebug = false;
	
	@Override
	protected void onPostLoad() throws InvalidConfigurationException
	{
		Messages.setLanguage(language);
	}
}
