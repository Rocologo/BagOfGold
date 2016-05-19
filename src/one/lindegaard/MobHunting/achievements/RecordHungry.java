package one.lindegaard.MobHunting.achievements;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class RecordHungry implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.recordhungry.name");
	}

	@Override
	public String getID() {
		return "recordhungry";
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.recordhungry.description");
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialRecordHungry;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Creeper)
				|| !MobHunting.getMobHuntingManager().isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;

		Creeper killed = (Creeper) event.getEntity();

		if (!(killed.getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) killed.getLastDamageCause();

		if (damage.getDamager() instanceof Arrow && ((Arrow) damage.getDamager()).getShooter() instanceof Skeleton) {
			Skeleton skele = (Skeleton) ((Arrow) damage.getDamager()).getShooter();

			if (killed.getTarget() instanceof Player) {
				Player target = (Player) killed.getTarget();

				if (skele.getTarget() == target && target.getGameMode() != GameMode.CREATIVE
						&& MobHunting.getMobHuntingManager().isHuntEnabled(target))
					MobHunting.getAchievements().awardAchievement(this, target);
			}
		}
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialRecordHungryCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialRecordHungryCmdDesc;
	}
	
	@Override
	public ItemStack getSymbol() {
		return new ItemStack(Material.BREAD);
	}
}
