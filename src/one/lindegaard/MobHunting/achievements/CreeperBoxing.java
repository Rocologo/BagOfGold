package one.lindegaard.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.events.MobHuntKillEvent;

public class CreeperBoxing implements Achievement, Listener {

	@Override
	public String getName() {
		return Messages.getString("achievements.creeperboxing.name");
	}

	@Override
	public String getID() {
		return "creeperboxing";
	}

	@Override
	public String getDescription() {
		return Messages.getString("achievements.creeperboxing.description");
	}

	@Override
	public double getPrize() {
		return MobHunting.getConfigManager().specialCreeperPunch;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event) {
		if (event.getKilledEntity() instanceof Creeper && !event.getDamageInfo().hasUsedWeapon()
				&& MobHunting.getConfigManager().getBaseKillPrize(event.getKilledEntity()) > 0)
			MobHunting.getAchievementManager().awardAchievement(this, event.getPlayer(),
					MobHunting.getExtendedMobManager().getExtendedMobFromEntity(event.getKilledEntity()));
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.getConfigManager().specialCreeperPunchCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.getConfigManager().specialCreeperPunchCmdDesc;
	}

	@Override
	public ItemStack getSymbol() {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 4);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner("MHF_Creeper");
		skull.setItemMeta(skullMeta);
		return skull;
	}
}
