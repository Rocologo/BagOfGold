package one.lindegaard.MobHunting.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;
import one.lindegaard.MobHunting.util.Misc;

public class AxeMurderer implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.axemurderer.name"); //$NON-NLS-1$
	}

	@Override
	public String getID() {
		return "axemurderer"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.axemurderer.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize() {
		return MobHunting.config().specialAxeMurderer;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event) {
		if (Misc.isAxe(event.getDamageInfo().weapon))
			MobHunting.getInstance().getAchievements().awardAchievement(this,
					event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialAxeMurdererCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialAxeMurdererCmdDesc;
	}
	
}
