package one.lindegaard.MobHunting.achievements;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.MobHuntingManager;

public class Creepercide implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.creepercide.name");
	}

	@Override
	public String getID() {
		return "creepercide";
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.creepercide.description");
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialCreepercide;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onDeath(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Creeper)
				|| !MobHuntingManager.isHuntEnabledInWorld(event.getEntity().getWorld()))
			return;

		Creeper killed = (Creeper) event.getEntity();

		if (!(killed.getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		EntityDamageByEntityEvent damage = (EntityDamageByEntityEvent) killed.getLastDamageCause();

		if (damage.getDamager() instanceof Creeper) {
			Player initiator = null;

			if (((Creeper) event.getEntity()).getTarget() instanceof Player)
				initiator = (Player) ((Creeper) event.getEntity()).getTarget();
			else {
				DamageInformation a, b;
				a = MobHunting.getInstance().getDamageInformation(killed);
				b = MobHunting.getInstance().getDamageInformation((Creeper) damage.getDamager());

				if (a != null)
					initiator = a.attacker;

				if (b != null && initiator == null)
					initiator = b.attacker;
			}

			if (initiator != null && MobHunting.getInstance().getMobHuntingManager().isHuntEnabled(initiator))
				MobHunting.getInstance().getAchievements().awardAchievement("creepercide", initiator);
		}
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialCreepercideCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialCreepercideCmdDesc;
	}
}
