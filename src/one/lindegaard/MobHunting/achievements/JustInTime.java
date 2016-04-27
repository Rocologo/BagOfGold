package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;

public class JustInTime implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.justintime.name");
	}

	@Override
	public String getID() {
		return "justintime";
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.justintime.description");
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialJustInTime;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event) {
		if (event.getKilledEntity().getWorld().getEnvironment() == World.Environment.NORMAL
				&& event.getKilledEntity().getWorld().getFullTime() >= 0
				&& event.getKilledEntity().getWorld().getFullTime() <= 500
				&& event.getKilledEntity().getFireTicks() > 0)
			MobHunting.getInstance().getAchievements().awardAchievement(this, event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialJustInTimeCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialJustInTimeCmdDesc;
	}
}
