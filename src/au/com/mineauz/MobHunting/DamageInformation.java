package au.com.mineauz.MobHunting;

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
	public Player assister;
	public Location attackerPosition;
	public boolean wolfAssist;
	
	public boolean wasFlying;
}
