package one.lindegaard.MobHunting.achievements;

import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;

public class Electrifying implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.electrifying.name");
	}

	@Override
	public String getID() {
		return "electrifying";
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.electrifying.description");
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialCharged;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event) {
		if (event.getKilledEntity() instanceof Creeper && ((Creeper) event.getKilledEntity()).isPowered())
			MobHunting.getAchievements().awardAchievement(this, event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialChargedCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialChargedCmdDesc;
	}
}
