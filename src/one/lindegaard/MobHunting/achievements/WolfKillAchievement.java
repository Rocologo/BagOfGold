package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;

public class WolfKillAchievement implements ProgressAchievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.fangmaster.name");
	}

	@Override
	public String getID() {
		return "fangmaster";
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.fangmaster.description");
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialFangMaster;
	}

	@Override
	public int getMaxProgress() {
		return 500;
	}

	@Override
	public String inheritFrom() {
		return null;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	private void onWolfKillMob(EntityDeathEvent event) {
		if (!MobHunting.getMobHuntingManager().isHuntEnabledInWorld(event.getEntity().getWorld())
				|| !(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
			return;

		EntityDamageByEntityEvent dmg = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();

		if (!(dmg.getDamager() instanceof Wolf))
			return;

		Wolf killer = (Wolf) dmg.getDamager();

		if (killer.isTamed() && killer.getOwner() instanceof OfflinePlayer) {
			Player owner = ((OfflinePlayer) killer.getOwner()).getPlayer();

			if (owner != null && MobHunting.getMobHuntingManager().isHuntEnabled(owner)) {
				MobHunting.getAchievements().awardAchievementProgress(this, owner, 1);
			}
		}

	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialFangMasterCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialFangMasterCmdDesc;
	}
	
	@Override
	public ItemStack getSymbol() {
		return new ItemStack(Material.STRING);
	}
}
