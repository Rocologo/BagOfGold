package au.com.mineauz.MobHunting.achievements;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.ExtendedMobType;
import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;

public class FourthHuntAchievement implements ProgressAchievement, Listener {
	private ExtendedMobType mType;

	public FourthHuntAchievement(ExtendedMobType entity) {
		mType = entity;
	}

	@Override
	public String getName() {
		return Messages.getString("achievements.hunter.4.name", "mob",
				mType.getName());
	}

	@Override
	public String getID() {
		return "hunting-level4-" + mType.name().toLowerCase();
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.hunter.4.description", "count",
				getMaxProgress(), "mob", mType.getName());
	}

	@Override
	public double getPrize() {
		return MobHunting.config().specialHunter4;
	}

	@Override
	public int getMaxProgress() {
		return mType.getMax() * 10;
	}

	@Override
	public String inheritFrom() {
		return "hunting-level3-" + mType.name().toLowerCase();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	private void onKillCompleted(MobHuntKillEvent event) {
		if (mType.matches(event.getEntity()))
			MobHunting.instance.getAchievements().awardAchievementProgress(
					this, event.getPlayer(), 1);
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialHunter4Cmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialHunter4CmdDesc;
	}
}
