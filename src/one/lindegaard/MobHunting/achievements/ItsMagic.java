package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;

public class ItsMagic implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.itsmagic.name"); 
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
		return MobHunting.getConfigManager().specialItsMagic;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event) {
		if (event.getDamageInfo().weapon.getType() == Material.POTION)
			MobHunting.getAchievements().awardAchievement(this,
					event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialItsMagicCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialItsMagicCmdDesc;
	}
}
