package au.com.mineauz.MobHunting.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;
import au.com.mineauz.MobHunting.util.Misc;

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
			MobHunting.instance.getAchievements().awardAchievement(this,
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
