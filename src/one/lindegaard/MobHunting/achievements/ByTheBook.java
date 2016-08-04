package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;

public class ByTheBook implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.bythebook.name");
	}

	@Override
	public String getID() {
		return "bythebook"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.bythebook.description");
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialByTheBook;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event) {
		if (event.getDamageInfo().weapon.getType() == Material.BOOK
				|| event.getDamageInfo().weapon.getType() == Material.WRITTEN_BOOK
				|| event.getDamageInfo().weapon.getType() == Material.BOOK_AND_QUILL)
			MobHunting.getAchievementManager().awardAchievement(this, event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialByTheBookCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialByTheBookCmdDesc;
	}
	
	@Override
	public ItemStack getSymbol() {
		return new ItemStack(Material.BOOK);
	}
}
