package au.com.mineauz.MobHunting;

import java.io.File;

import org.bukkit.configuration.InvalidConfigurationException;

import au.com.mineauz.MobHunting.util.AutoConfig;
import au.com.mineauz.MobHunting.util.ConfigField;

public class Config extends AutoConfig {
	public Config(File file) {
		super(file);

		setCategoryComment(
				"mobs",
				"Here is where you set the base prize in $ for killing a mob of each type"
						+ "\nYou can either set a decimal number ex 1.23 or a range 1.23:2.23"
						+ "\nFor each kill you can run a console command to give the player a reward."
						+ "\nYou can use the following variables {player},{world}."
						+ "\nAn example could be to give the player permission to fly"
						+ "\nfor 1 hour or use give command to the player items."
						+ "\nYou can also specify the message send to the player."
						+ "\nYou can run many console commands on each line, each command"
						+ "\nmust be separated by |"
						+ "\nThe player will have the cmd run in {mob-cmd-run-frequency} out of"
						+ "\n{mob-cmd-run-frequency-base} times in average. If mob-cmd-run-frequency=0 it"
						+ "\nwill never run. If f.ex. mob-cmd-run-frequency=50 and "
						+ "\nmob-cmd-run-frequency-base=100 it will run run every second time.");
		setCategoryComment("boss",
				"Here is where you set the base prize in $ for killing the bosses");
		setCategoryComment(
				"bonus",
				"These are bonus multipliers that can modify the base prize. "
						+ "\nREMEMBER: These are not in $ but they are a multiplier. "
						+ "\nSetting to 1 will disable them.");
		setCategoryComment(
				"penalty",
				"These are penalty multipliers that can modify the base prize. "
						+ "\nREMEMBER: These are not in $ but they are a multiplier. "
						+ "\nSetting to 1 will disable them.");
		setCategoryComment(
				"special",
				"Here is where you set the prize in $ for achieving a special kill. "
						+ "\nFor each achievment you can run a console command to give the player a reward. "
						+ "\nYou can use the following variables {player},{world}."
						+ "\nAn example could be to give the player permission to fly "
						+ "\nfor 1 hour or use give command to the player items."
						+ "\nYou can also specify the message send to the player."
						+ "\nYou can run many console commands on each line, each command"
						+ "\nmust be separated by |");
		setCategoryComment("assists",
				"They players can get an extra reward if they help each other killing mobs.");
		setCategoryComment(
				"killstreak",
				"Set the multiplier when the player kills 1,2,3,4 mob in a row without getting damage.");

		setCategoryComment(
				"pvp",
				"Pvp configuration. Set pvp-allowed = true if you want give the players a reward when they kill eachother."
						+ "\nYou can alsp run a console command when this happens to give the player a reward or punish him."
						+ "\nYou can you the following variables {player},{world},{killed_player}."
						+ "\nAn example could be to give the player permission to fly "
						+ "\nfor 1 hour or use give command to the player items."
						+ "\nYou can also specify the message send to the player."
						+ "\nYou can run many console commands on each line, each command"
						+ "\nmust be separated by |");

		setCategoryComment(
				"plugins",
				"########################################################################"
						+ "\nIntegration to otherplugins."
						+ "\n########################################################################");

		setCategoryComment(
				"database",
				"########################################################################"
						+ "\nDatabase Settings."
						+ "\n########################################################################");

		setCategoryComment(
				"general",
				"########################################################################"
						+ "\nGeneral Setting."
						+ "\n########################################################################");

	}

	// #####################################################################################
	// Mobs
	// #####################################################################################
	@ConfigField(name = "blaze", category = "mobs")
	public String blazePrize = "1.0";
	@ConfigField(name = "blaze-cmd", category = "mobs")
	public String blazeCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Blaze\"}|give {player} iron_ingot 1";
	@ConfigField(name = "blaze-cmd-desc", category = "mobs")
	public String blazeCmdDesc = "You got a Blaze skull and an Iron ingot.";
	@ConfigField(name = "blaze-cmd-run-frequency", category = "mobs")
	public int blazeFrequency = 10;
	@ConfigField(name = "blaze-cmd-run-frequency-base", category = "mobs")
	public int blazeFrequencyBase = 100;

	@ConfigField(name = "creeper", category = "mobs")
	public String creeperPrize = "1.0";
	@ConfigField(name = "creeper-cmd", category = "mobs")
	public String creeperCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Creeper\"}|give {player} iron_ingot 1";
	@ConfigField(name = "creeper-cmd-desc", category = "mobs")
	public String creeperCmdDesc = "You got a Creeper skull and an Iron ingot.";
	@ConfigField(name = "creeper-cmd-run-frequency", category = "mobs")
	public int creeperFrequency = 10;
	@ConfigField(name = "creeper-cmd-run-frequency-base", category = "mobs")
	public int creeperFrequencyBase = 100;

	@ConfigField(name = "silverfish", category = "mobs")
	public String silverfishPrize = "0.1";
	@ConfigField(name = "silverfish-cmd", category = "mobs")
	public String silverfishCmd = "";
	@ConfigField(name = "silver-cmd-desc", category = "mobs")
	public String silverfishCmdDesc = "";
	@ConfigField(name = "silverfish-cmd-run-frequency", category = "mobs")
	public int silverfishFrequency = 20;
	@ConfigField(name = "silverfish-cmd-run-frequency-base", category = "mobs")
	public int silverfishFrequencyBase = 100;

	@ConfigField(name = "zombie-pigman", category = "mobs")
	public String zombiePigmanPrize = "0.5";
	@ConfigField(name = "zombie-pigman-cmd", category = "mobs")
	public String zombiePigmanCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_PigZombie\"}|give {player} iron_ingot 1";
	@ConfigField(name = "zombie-pigman-cmd-desc", category = "mobs")
	public String zombiePigmanCmdDesc = "You got a Zombie Pigman skull and an Iron ingot.";
	@ConfigField(name = "zombie-pigman-cmd-run-frequency", category = "mobs")
	public int zombiePigmanFrequency = 10;
	@ConfigField(name = "zombie-pigman-cmd-run-frequency-base", category = "mobs")
	public int zombiePigmanFrequencyBase = 100;

	@ConfigField(name = "enderman", category = "mobs")
	public String endermanPrize = "1.0";
	@ConfigField(name = "enderman-cmd", category = "mobs")
	public String endermanCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Enderman\"}|give {player} iron_ingot 1";
	@ConfigField(name = "enderman-cmd-desc", category = "mobs")
	public String endermanCmdDesc = "You got a Enderman skull and an Iron ingot.";
	@ConfigField(name = "enderman-cmd-run-frequency", category = "mobs")
	public int endermanFrequency = 20;
	@ConfigField(name = "enderman-cmd-run-frequency-base", category = "mobs")
	public int endermanFrequencyBase = 100;

	@ConfigField(name = "giant", category = "mobs")
	public String giantPrize = "2.0";
	@ConfigField(name = "giant-cmd", category = "mobs")
	public String giantCmd = "give {player} iron_ingot 1";
	@ConfigField(name = "giant-cmd-desc", category = "mobs")
	public String giantCmdDesc = "You got an Iron ingot.";
	@ConfigField(name = "giant-cmd-run-frequency", category = "mobs")
	public int giantFrequency = 10;
	@ConfigField(name = "giant-cmd-run-frequency-base", category = "mobs")
	public int giantFrequencyBase = 100;

	@ConfigField(name = "skeleton", category = "mobs")
	public String skeletonPrize = "0.5";
	@ConfigField(name = "skeleton-cmd", category = "mobs")
	public String skeletonCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Skeleton\"}|give {player} iron_ingot 1";
	@ConfigField(name = "skeleton-cmd-desc", category = "mobs")
	public String skeletonCmdDesc = "You got a Skeleton skull and an Iron ingot.";
	@ConfigField(name = "skeleton-cmd-run-frequency", category = "mobs")
	public int skeletonFrequency = 10;
	@ConfigField(name = "skeleton-cmd-run-frequency-base", category = "mobs")
	public int skeletonFrequencyBase = 100;

	@ConfigField(name = "wither-skeleton", category = "mobs")
	public String witherSkeletonPrize = "1.0";
	@ConfigField(name = "wither-skeleton-cmd", category = "mobs")
	public String witherSkeletonCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_WSkeleton\"}|give {player} iron_ingot 1";
	@ConfigField(name = "wither-skeleton-cmd-desc", category = "mobs")
	public String witherSkeletonCmdDesc = "You got a Wither Skeleton skull and an Iron ingot.";
	@ConfigField(name = "wither-skeleton-cmd-run-frequency", category = "mobs")
	public int witherSkeletonFrequency = 20;
	@ConfigField(name = "wither-skeleton-cmd-run-frequency-base", category = "mobs")
	public int witherSkeletonFrequencyBase = 100;

	@ConfigField(name = "spider", category = "mobs")
	public String spiderPrize = "0.5:1.1";
	@ConfigField(name = "spider-cmd", category = "mobs")
	public String spiderCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Spider\"}|give {player} iron_ingot 1";
	@ConfigField(name = "spider-cmd-desc", category = "mobs")
	public String spiderCmdDesc = "You got a Spider skull and an Iron ingot.";
	@ConfigField(name = "spider-cmd-run-frequency", category = "mobs")
	public int spiderFrequency = 10;
	@ConfigField(name = "spider-cmd-run-frequency-base", category = "mobs")
	public int spiderFrequencyBase = 100;

	@ConfigField(name = "cave-spider", category = "mobs")
	public String caveSpiderPrize = "0.8";
	@ConfigField(name = "cave-spider-cmd", category = "mobs")
	public String caveSpiderCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_CaveSpider\"}|give {player} iron_ingot 1";
	@ConfigField(name = "cave-spider-cmd-desc", category = "mobs")
	public String caveSpiderCmdDesc = "You got a Cave Spider skull and an Iron ingot.";
	@ConfigField(name = "cave-spider-cmd-run-frequency", category = "mobs")
	public int caveSpiderFrequency = 20;
	@ConfigField(name = "cave-spider-cmd-run-frequency-base", category = "mobs")
	public int caveSpiderFrequencyBase = 100;

	@ConfigField(name = "witch", category = "mobs")
	public String witchPrize = "1.0";
	@ConfigField(name = "witch-cmd", category = "mobs")
	public String witchCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Witch\"}|give {player} iron_ingot 1";
	@ConfigField(name = "witch-cmd-desc", category = "mobs")
	public String witchCmdDesc = "You got a Witch skull and an Iron ingot.";
	@ConfigField(name = "witch-cmd-run-frequency", category = "mobs")
	public int witchFrequency = 10;
	@ConfigField(name = "witch-cmd-run-frequency-base", category = "mobs")
	public int witchFrequencyBase = 100;

	@ConfigField(name = "zombie", category = "mobs")
	public String zombiePrize = "0.5";
	@ConfigField(name = "zombie-cmd", category = "mobs")
	public String zombieCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Zombie\"}|give {player} iron_ingot 1";
	@ConfigField(name = "zombie-cmd-desc", category = "mobs")
	public String zombieCmdDesc = "You got a Zombie skull and an Iron ingot.";
	@ConfigField(name = "zombie-cmd-run-frequency", category = "mobs")
	public int zombieFrequency = 10;
	@ConfigField(name = "zombie-cmd-run-frequency-base", category = "mobs")
	public int zombieFrequencyBase = 10;

	@ConfigField(name = "ghast", category = "mobs")
	public String ghastPrize = "2.0";
	@ConfigField(name = "ghast-cmd", category = "mobs")
	public String ghastCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Ghast\"}|give {player} iron_ingot 1";
	@ConfigField(name = "ghast-cmd-desc", category = "mobs")
	public String ghastCmdDesc = "You got a Ghast skull and an Iron ingot.";
	@ConfigField(name = "ghast-cmd-run-frequency", category = "mobs")
	public int ghastFrequency = 10;
	@ConfigField(name = "ghast-cmd-run-frequency-base", category = "mobs")
	public int ghastFrequencyBase = 100;

	@ConfigField(name = "iron-golem", category = "mobs")
	public String ironGolemPrize = "2.0";
	@ConfigField(name = "iron-golem-cmd", category = "mobs")
	public String ironGolemCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Golem\"}|give {player} iron_ingot 1";
	@ConfigField(name = "iron-golem-cmd-desc", category = "mobs")
	public String ironGolemCmdDesc = "You got an Iron Golem skull and an Iron ingot.";
	@ConfigField(name = "iron-golem-cmd-run-frequency", category = "mobs")
	public int ironGolemFrequency = 10;
	@ConfigField(name = "iron-golem-cmd-run-frequency-base", category = "mobs")
	public int ironGolemFrequencyBase = 100;

	@ConfigField(name = "magma-cube", category = "mobs")
	public String magmaCubePrize = "4.0";
	@ConfigField(name = "magma-cube-cmd", category = "mobs")
	public String magmaCubeCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_LavaSlime\"}|give {player} iron_ingot 1";
	@ConfigField(name = "magma-cube-cmd-desc", category = "mobs")
	public String magmaCubeCmdDesc = "You got a Magma Cube skull and an Iron ingot.";
	@ConfigField(name = "magma-cube-cmd-run-frequency", category = "mobs")
	public int magmaCubeFrequency = 10;
	@ConfigField(name = "magma-cube-cmd-run-frequency-base", category = "mobs")
	public int magmaCubeFrequencyBase = 100;

	@ConfigField(name = "endermite", category = "mobs")
	public String endermitePrize = "1.0";
	@ConfigField(name = "endermite-cmd", category = "mobs")
	public String endermiteCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Endermite\"}|give {player} iron_ingot 1";
	@ConfigField(name = "endermite-cmd-desc", category = "mobs")
	public String endermiteCmdDesc = "You got a Endermite skull and an Iron ingot.";
	@ConfigField(name = "endermite-cmd-run-frequency", category = "mobs")
	public int endermiteFrequency = 10;
	@ConfigField(name = "endermite-cmd-run-frequency-base", category = "mobs")
	public int endermiteFrequencyBase = 100;

	@ConfigField(name = "guardian", category = "mobs")
	public String guardianPrize = "1.0";
	@ConfigField(name = "guardian-cmd", category = "mobs")
	public String guardianCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Guardian\"}|give {player} iron_ingot 1";
	@ConfigField(name = "guardian-cmd-desc", category = "mobs")
	public String guardianCmdDesc = "You got a Guardian skull and an Iron ingot.";
	@ConfigField(name = "guardian-cmd-run-frequency", category = "mobs")
	public int guardianFrequency = 10;
	@ConfigField(name = "guardian-cmd-run-frequency-base", category = "mobs")
	public int guardianFrequencyBase = 100;

	@ConfigField(name = "killerrabbit", category = "mobs")
	public String killerrabbitPrize = "5";
	@ConfigField(name = "killerrabbit-cmd", category = "mobs")
	public String killerrabbitCmd = "";
	@ConfigField(name = "killerrabbit-cmd-desc", category = "mobs")
	public String killerrabbitCmdDesc = "";
	@ConfigField(name = "killerrabbit-cmd-run-frequency", category = "mobs")
	public int killerrabbitFrequency = 50;
	@ConfigField(name = "killerrabbit-cmd-run-frequency-base", category = "mobs")
	public int killerrabbitFrequencyBase = 100;

	@ConfigField(name = "slime-base", category = "mobs", comment = "This is multiplied by the size of the slime. So a big natural slime is 4x this value")
	public String slimeTinyPrize = "0.25";
	@ConfigField(name = "slime-cmd", category = "mobs")
	public String slimeCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Slime\"}|give {player} iron_ingot 1";
	@ConfigField(name = "slime-cmd-desc", category = "mobs")
	public String slimeCmdDesc = "You got a Slime skull and an Iron ingot.";
	@ConfigField(name = "slime-cmd-run-frequency", category = "mobs")
	public int slimeFrequency = 3;
	@ConfigField(name = "slime-cmd-run-frequency-base", category = "mobs")
	public int slimeFrequencyBase = 100;

	// #####################################################################################
	// Bosses
	// #####################################################################################
	@ConfigField(name = "wither", category = "boss")
	public String witherPrize = "500.0:1000.0";
	@ConfigField(name = "wither-cmd", category = "boss")
	public String witherCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Wither\"}|give {player} diamond 2";
	@ConfigField(name = "wither-cmd-desc", category = "boss")
	public String witherCmdDesc = "You got a Wither skull and two Diamonds.";
	@ConfigField(name = "wither-cmd-run-frequency", category = "boss")
	public int witherFrequency = 100;
	@ConfigField(name = "wither-cmd-run-frequency-base", category = "boss")
	public int witherFrequencyBase = 100;

	@ConfigField(name = "enderdragon", category = "boss")
	public String enderdragonPrize = "1000.0:2000.0";
	@ConfigField(name = "enderdragon-cmd", category = "boss")
	public String enderdragonCmd = "give {player} 397 1 3 {SkullOwner:\"MHF_Enderdragon\"}|give {player} diamond 2";
	@ConfigField(name = "enderdragon-cmd-desc", category = "boss")
	public String enderdragonCmdDesc = "You got a Enderdragon skull and two Diamonds.";
	@ConfigField(name = "enderdragon-cmd-run-frequency", category = "boss")
	public int enderdragonFrequency = 100;
	@ConfigField(name = "enderdragon-cmd-run-frequency-base", category = "boss")
	public int enderdragonFrequencyBase = 100;

	// #####################################################################################
	// Bonuses
	// #####################################################################################
	@ConfigField(name = "sneaky", category = "bonus")
	public double bonusSneaky = 2.0;
	@ConfigField(name = "return-to-sender", category = "bonus")
	public double bonusReturnToSender = 2.0;
	@ConfigField(name = "push-off-cliff", category = "bonus")
	public double bonusSendFalling = 2.0;
	@ConfigField(name = "no-weapon", category = "bonus")
	public double bonusNoWeapon = 2.0;
	@ConfigField(name = "far-shot", category = "bonus")
	public double bonusFarShot = 4.0;
	@ConfigField(name = "mounted", category = "bonus")
	public double bonusMounted = 1.5;
	@ConfigField(name = "friendly-fire", category = "bonus")
	public double bonusFriendlyFire = 4;
	@ConfigField(name = "bonus-mob", category = "bonus")
	public double bonusBonusMob = 10;
	@ConfigField(name = "critical", category = "bonus")
	public double bonusCritical = 2;
	@ConfigField(name = "bonus-mob-chance", category = "bonus", comment = "This is the chance (% chance 0-100) that a bonus mob will spawn.")
	public double bonusMobChance = 0.2;

	// #####################################################################################
	// Specials
	// #####################################################################################
	@ConfigField(name = "charged-kill", category = "special")
	public double specialCharged = 100;
	@ConfigField(name = "charged-kill-cmd", category = "special")
	public String specialChargedCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "charged-kill-cmd-desc", category = "special")
	public String specialChargedCmdDesc = "";
	@ConfigField(name = "creeper-punch", category = "special")
	public double specialCreeperPunch = 100;
	@ConfigField(name = "creeper-punch-cmd", category = "special")
	public String specialCreeperPunchCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "creeper-punch-cmd-desc", category = "special")
	public String specialCreeperPunchCmdDesc = "";
	@ConfigField(name = "axe-murderer", category = "special")
	public double specialAxeMurderer = 20;
	@ConfigField(name = "axe-murderer-cmd", category = "special")
	public String specialAxeMurdererCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "axe-murderer-cmd-desc", category = "special")
	public String specialAxeMurdererCmdDesc = "";
	@ConfigField(name = "recordhungry", category = "special")
	public double specialRecordHungry = 50;
	@ConfigField(name = "recordhungry-cmd", category = "special")
	public String specialRecordHungryCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "recordhungry-cmd-desc", category = "special")
	public String specialRecordHungryCmdDesc = "";
	@ConfigField(name = "infighting", category = "special")
	public double specialInfighting = 50;
	@ConfigField(name = "infighting-cmd", category = "special")
	public String specialInfightingCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "infighting-cmd-desc", category = "special")
	public String specialInfightingCmdDesc = "";
	@ConfigField(name = "by-the-book", category = "special")
	public double specialByTheBook = 20;
	@ConfigField(name = "by-the-book-cmd", category = "special")
	public String specialByTheBookCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "by-the-book-cmd-desc", category = "special")
	public String specialByTheBookCmdDesc = "";
	@ConfigField(name = "creepercide", category = "special")
	public double specialCreepercide = 50;
	@ConfigField(name = "creepercide-cmd", category = "special")
	public String specialCreepercideCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "creepercide-cmd-desc", category = "special")
	public String specialCreepercideCmdDesc = "";
	@ConfigField(name = "hunt-begins", category = "special")
	public double specialHuntBegins = 10;
	@ConfigField(name = "hunt-begins-cmd", category = "special")
	public String specialHuntBeginsCmd = "";
	@ConfigField(name = "hunt-begins-cmd-desc", category = "special")
	public String specialHuntBeginsCmdDesc = "";
	@ConfigField(name = "itsmagic", category = "special")
	public double specialItsMagic = 20;
	@ConfigField(name = "itsmagic-cmd", category = "special")
	public String specialItsMagicCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "itsmagic-cmd-desc", category = "special")
	public String specialItsMagicCmdDesc = "Enjoy you Gold Ingot";
	@ConfigField(name = "fancypants", category = "special")
	public double specialFancyPants = 50;
	@ConfigField(name = "fancypants-cmd", category = "special")
	public String specialFancyPantsCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "fancypants-cmd-desc", category = "special")
	public String specialFancyPantsCmdDesc = "Enjoy you Gold Ingots";
	@ConfigField(name = "master-sniper", category = "special")
	public double specialMasterSniper = 200;
	@ConfigField(name = "master-sniper-cmd", category = "special")
	public String specialMasterSniperCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "master-sniper-cmd-desc", category = "special")
	public String specialMasterSniperCmdDesc = "Enjoy you Gold Ingots";
	@ConfigField(name = "fangmaster", category = "special")
	public double specialFangMaster = 50;
	@ConfigField(name = "fangmaster-cmd", category = "special")
	public String specialFangMasterCmd = "give {player} gold_ingot 1";
	@ConfigField(name = "fangmaster-cmd-desc", category = "special")
	public String specialFangMasterCmdDesc = "Enjoy your Gold Ingot";
	@ConfigField(name = "hunter1", category = "special")
	public double specialHunter1 = 100;
	@ConfigField(name = "hunter1-cmd", category = "special")
	public String specialHunter1Cmd = "give {player} gold_ingot 5";
	@ConfigField(name = "hunter1-cmd-desc", category = "special")
	public String specialHunter1CmdDesc = "Enjoy your 5 Gold Ingots";
	@ConfigField(name = "hunter2", category = "special")
	public double specialHunter2 = 250;
	@ConfigField(name = "hunter2-cmd", category = "special")
	public String specialHunter2Cmd = "give {player} gold_ingot 10";
	@ConfigField(name = "hunter2-cmd-desc", category = "special")
	public String specialHunter2CmdDesc = "Enjoy your 10 Gold Ingots";
	@ConfigField(name = "hunter3", category = "special")
	public double specialHunter3 = 500;
	@ConfigField(name = "hunter3-cmd", category = "special")
	public String specialHunter3Cmd = "give {player} gold_ingot 20";
	@ConfigField(name = "hunter3-cmd-desc", category = "special")
	public String specialHunter3CmdDesc = "Enjoy your 20 Gold Ingots";
	@ConfigField(name = "hunter4", category = "special")
	public double specialHunter4 = 1000;
	@ConfigField(name = "hunter4-cmd", category = "special")
	public String specialHunter4Cmd = "give {player} gold_ingot 25";
	@ConfigField(name = "hunter4-cmd-desc", category = "special")
	public String specialHunter4CmdDesc = "Enjoy your 25 Gold Ingots";
	@ConfigField(name = "hunter5", category = "special")
	public double specialHunter5 = 2000;
	@ConfigField(name = "hunter5-cmd", category = "special")
	public String specialHunter5Cmd = "give {player} gold_ingot 40";
	@ConfigField(name = "hunter5-cmd-desc", category = "special")
	public String specialHunter5CmdDesc = "Enjoy your 40 Gold Ingots";
	@ConfigField(name = "hunter6", category = "special")
	public double specialHunter6 = 4000;
	@ConfigField(name = "hunter6-cmd", category = "special")
	public String specialHunter6Cmd = "give {player} gold_ingot 50";
	@ConfigField(name = "hunter6-cmd-desc", category = "special")
	public String specialHunter6CmdDesc = "Enjoy your 50 Gold Ingots";
	@ConfigField(name = "hunter7", category = "special")
	public double specialHunter7 = 8000;
	@ConfigField(name = "hunter7-cmd", category = "special")
	public String specialHunter7Cmd = "give {player} gold_ingot 60";
	@ConfigField(name = "hunter7-cmd-desc", category = "special")
	public String specialHunter7CmdDesc = "Enjoy your 60 Gold Ingots";

	// #####################################################################################
	// Assists
	// #####################################################################################
	@ConfigField(name = "enable", category = "assists", comment = "Enabling assist allows the second last player to attack a mob to get some money from it")
	public boolean enableAssists = true;
	@ConfigField(name = "multiplier", category = "assists", comment = "This should be a value that is multiplied against the mobs base kill value."
			+ "\nThis is used to determine how much money an assister gets.")
	public double assistMultiplier = 0.25;
	@ConfigField(name = "allow-killstreak", category = "assists", comment = "Should killstreak be applied to assists")
	public boolean assistAllowKillstreak = false;
	@ConfigField(name = "timeout", category = "assists", comment = "Time in seconds after attacking a mob that can be counted as an assist")
	public int assistTimeout = 4;

	// #####################################################################################
	// Penalties
	// #####################################################################################
	@ConfigField(name = "enable-grinding-penalty", category = "penalty", comment = "Enabling this prevents a player from earning too much money from using a mob grinder")
	public boolean penaltyGrindingEnable = true;
	@ConfigField(name = "flyingPenalty", category = "penalty", comment = "If a player flies at any point in a fight, this penalty will be applied")
	public double penaltyFlying = 0.5;

	// #####################################################################################
	// Killstreaks
	// #####################################################################################
	@ConfigField(name = "level1", category = "killstreak")
	public int killstreakLevel1 = 5;
	@ConfigField(name = "level1-multiplier", category = "killstreak")
	public double killstreakLevel1Mult = 1.5;
	@ConfigField(name = "level2", category = "killstreak")
	public int killstreakLevel2 = 10;
	@ConfigField(name = "level2-multiplier", category = "killstreak")
	public double killstreakLevel2Mult = 2;
	@ConfigField(name = "level3", category = "killstreak")
	public int killstreakLevel3 = 20;
	@ConfigField(name = "level3-multiplier", category = "killstreak")
	public double killstreakLevel3Mult = 3;
	@ConfigField(name = "level4", category = "killstreak")
	public int killstreakLevel4 = 40;
	@ConfigField(name = "level4-multiplier", category = "killstreak")
	public double killstreakLevel4Mult = 4;

	// #####################################################################################
	// PVP
	// #####################################################################################
	@ConfigField(name = "pvp-allowed", category = "pvp", comment = "Set pvpAllowed=false to disable rewards on killing other players.")
	public boolean pvpAllowed = true;
	@ConfigField(name = "pvp-kill-prize", category = "pvp", comment = "The kill prize kan be a number to stel x dollars from the killed player,"
			+ "\nor it kan be a cut in percent of his balance.")
	public String pvpKillPrize = "1.5%";
	@ConfigField(name = "pvp-kill-cmd", category = "pvp", comment = "One or more console commands to be run when a player kills another player.")
	public String pvpKillCmd = "give {player} 397 1 3 {SkullOwner:\"{killed_player}\"}|give {player} diamond 1";
	@ConfigField(name = "pvp-kill-cmd-desc", category = "pvp", comment = "Write the message to the killer, describing the reward / console commands")
	public String pvpKillCmdDesc = "You got {killed_player}\'s skull";

	// #####################################################################################
	// Plugin integration
	// #####################################################################################
	@ConfigField(name = "mobarena-get-rewards", category = "plugins", comment = "Set to true if you want the players to get rewards while playing MobArena.")
	public boolean mobarenaGetRewards = false;
	@ConfigField(name = "pvparena-get-rewards", category = "plugins", comment = "Set to true if you want the players to get rewards while playing pvpArena.")
	public boolean pvparenaGetRewards = false;

	// #####################################################################################
	// Database
	// #####################################################################################
	@ConfigField(name = "type", category = "database", comment = "Type of database to use. Valid values are: sqlite, mysql")
	public String databaseType = "sqlite";
	@ConfigField(name = "username", category = "database")
	public String databaseUsername = "user";
	@ConfigField(name = "password", category = "database")
	public String databasePassword = "password";
	@ConfigField(name = "host", category = "database")
	public String databaseHost = "localhost:3306";
	@ConfigField(name = "database", category = "database")
	public String databaseName = "mobhunting";

	// #####################################################################################
	// Generel settings
	// #####################################################################################
	@ConfigField(name = "disabled-in-worlds", category = "general", comment = "Put the names of the worlds here that you do not wish for mobhunting to be enabled in.")
	public String[] disabledInWorlds = new String[0];
	@ConfigField(name = "language", category = "general", comment = "The language (file) to use. You can put the name of the language file as the language code "
			+ "\n(eg. en_US, de_DE, fr_FR, ect.) or you can specify the name of a custom file without the .lang\nPlease check the lang/ folder for a list of all available translations.")
	public String language = "en_US";
	@ConfigField(name = "broadcast-achievement", category = "general", comment = "Should achievements be broadcasted?")
	public boolean broadcastAchievement = true;
	@ConfigField(name = "broadcast-first-achievement", category = "general", comment = "Should the hunt begins achievement be broadcasted?")
	public boolean broadcastFirstAchievement = true;
	@ConfigField(name = "save-period", category = "general", comment = "Time between saves in ticks (20 ticks ~ 1 sec)")
	public int savePeriod = 6000;
	@ConfigField(name = "leaderboard-update-period", category = "general", comment = "Time between leaderboard updates in ticks (20 ticks ~ 1 sec)")
	public int leaderboardUpdatePeriod = 1200;
	@ConfigField(name = "kill-timeout", category = "general", comment = "Time in seconds after attacking a mob that can be counted as a kill")
	public int killTimeout = 4;
	@ConfigField(name = "kill-debug", category = "general", comment = "If kills are not being registered in mob hunting. Enable this to see why they arent")
	public boolean killDebug = false;
	@ConfigField(name = "update-check", category = "general", comment = "Check if there is a new version of the plugin available.")
	public boolean updateCheck = true;

	@Override
	protected void onPostLoad() throws InvalidConfigurationException {
		Messages.setLanguage(language);
	}
}
