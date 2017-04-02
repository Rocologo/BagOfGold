package one.lindegaard.MobHunting.modifier;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffectType;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class CriticalModifier implements IModifier {

	@Override
	public String getName() {
		return ChatColor.LIGHT_PURPLE + Messages.getString("bonus.critical.name"); //$NON-NLS-1$
	}

	private boolean isInWater(Player player) {
		Block block = player.getLocation().getBlock();
		return block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER;
	}

	private boolean isOnLadder(Player player) {
		Block block = player.getLocation().getBlock();
		return block.getType() == Material.LADDER || block.getType() == Material.VINE;
	}

	private boolean canCriticalHit(Player player) {
		return player.getFallDistance() > 0 && !((Entity) player).isOnGround() && !isInWater(player)
				&& !isOnLadder(player) && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
				&& !player.isInsideVehicle();
	}

	@Override
	public double getMultiplier(Entity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		return MobHunting.getConfigManager().bonusCritical;
	}

	@Override
	public boolean doesApply(Entity deadEntity, Player killer, HuntData data, DamageInformation extraInfo,
			EntityDamageByEntityEvent lastDamageCause) {
		return (canCriticalHit(killer) && extraInfo.isMeleWeapenUsed());
	}

}
