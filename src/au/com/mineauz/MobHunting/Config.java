package au.com.mineauz.MobHunting;

import java.io.File;

import au.com.mineauz.MobHunting.util.AutoConfig;
import au.com.mineauz.MobHunting.util.ConfigField;

public class Config extends AutoConfig
{
	public Config( File file )
	{
		super(file);
		
		setCategoryComment("mobs", "Here is where you set the base prize in $ for killing a mob of each type");
		setCategoryComment("boss", "Here is where you set the base prize in $ for killing the bosses");
		setCategoryComment("bonus", "These are bonus multipliers that can modify the base prize. \nREMEMBER: These are not in $ but they are a multiplier. Setting to 1 will disable them.");
		setCategoryComment("penalty", "These are penalty multipliers that can modify the base prize. \nREMEMBER: These are not in $ but they are a multiplier. Setting to 1 will disable them.");
		
		setCategoryComment("special", "Here is where you set the prize in $ for achieving a special kill");
		
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
	
	@ConfigField(name="bonus-mob-chance", category="bonus", comment="This is the chance (% chance 0-100) that a bonus mob will spawn.")
	public double bonusMobChance = 0.2;
	
	@ConfigField(name="charged-kill", category="special")
	public double specialCharged = 100;
	@ConfigField(name="creeper-punch", category="special")
	public double specialCreeperPunch = 100;
	@ConfigField(name="axe-murderer", category="special")
	public double specialAxeMurderer = 20;
	@ConfigField(name="recordhungry", category="special")
	public double specialRecordHungry = 50;
	@ConfigField(name="infighting", category="special")
	public double specialInfighting = 50;
	@ConfigField(name="by-the-book", category="special")
	public double specialByTheBook = 20;
	@ConfigField(name="creepercide", category="special")
	public double specialCreepercide = 50;
	@ConfigField(name="hunt-begins", category="special")
	public double specialHuntBegins = 10;
	@ConfigField(name="itsmagic", category="special")
	public double specialItsMagic = 20;
	@ConfigField(name="fancypants", category="special")
	public double specialFancyPants = 50;
	@ConfigField(name="master-sniper", category="special")
	public double specialMasterSniper = 200;
	@ConfigField(name="fangmaster", category="special")
	public double specialFangMaster = 50;
	
	@ConfigField(name="hunter1", category="special")
	public double specialHunter1 = 100;
	@ConfigField(name="hunter2", category="special")
	public double specialHunter2 = 250;
	@ConfigField(name="hunter3", category="special")
	public double specialHunter3 = 500;
	@ConfigField(name="hunter4", category="special")
	public double specialHunter4 = 1000;
	
	@ConfigField(name="enable-grinding-penalty", category="penalty", comment="Enabling this prevents a player from earning too much money from using a mob grinder")
	public boolean penaltyGrindingEnable = true;
	
	@ConfigField(name="flyingPenalty", category="penalty", comment="If a player flies at any point in a fight, this penalty will be applied")
	public double penaltyFlying = 0.5;
	
	@ConfigField(name="disabled-in-worlds", category="general", comment="Put the names of the worlds here that you do not wish for mobhunting to be enabled in.")
	public String[] disabledInWorlds = new String[0];
}
