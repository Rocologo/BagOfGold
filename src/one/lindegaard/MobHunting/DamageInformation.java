package one.lindegaard.MobHunting;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DamageInformation
{
	public long time;
	
	public ItemStack weapon;
	public boolean usedWeapon;
	public boolean mele;
	public Player attacker;
	public long lastAttackTime = System.currentTimeMillis();
	public Player assister;
	public long lastAssistTime = System.currentTimeMillis();
	
	public Location attackerPosition;
	public boolean wolfAssist;
	
	public boolean wasFlying;
	
	//Disguises
	public boolean playerUndercover; //Player attacking undercover (disguise)
	public boolean mobCoverBlown; //Player attacked a disguised Mob/Player

}
