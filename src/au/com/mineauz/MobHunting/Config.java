package au.com.mineauz.MobHunting;

import java.io.File;

import au.com.mineauz.MobHunting.util.AutoConfig;
import au.com.mineauz.MobHunting.util.ConfigField;

public class Config extends AutoConfig
{
	public Config( File file )
	{
		super(file);
	}
	
	@ConfigField(name="blaze", category="mobs")
	public double blazePrize = 1.0;
	@ConfigField(name="creeper", category="mobs")
	public double creeperPrize = 1.0;
	@ConfigField(name="silverfish", category="mobs")
	public double silverfishPrize = 0.1;
	
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
	
	@ConfigField(name="enable-grinding-penalty", category="penalty", comment="Enabling this prevents a player from earning too much money from using a mob grinder")
	public boolean penaltyGrindingEnable = true;
	
	@ConfigField(name="flyingPenalty", category="penalty", comment="If a player flies at any point in a fight, this penalty will be applied")
	public double penaltyFlying = 0.5;
	
	@ConfigField(name="disabled-in-worlds", category="general", comment="Put the names of the worlds here that you do not wish for mobhunting to be enabled in.")
	public String[] disabledInWorlds = new String[0];
}
