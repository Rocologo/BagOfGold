package au.com.mineauz.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;

public class ItsMagic implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.itsmagic.name"); //$NON-NLS-1$
	}

	@Override
	public String getID() {
		return "itsmagic";
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.itsmagic.description");
	}

	@Override
	public double getPrize() {
		return MobHunting.config().specialItsMagic;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event) {
		if (event.getDamageInfo().weapon.getType() == Material.POTION)
			MobHunting.instance.getAchievements().awardAchievement(this,
					event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialItsMagicCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialItsMagicCmdDesc;
	}
}
