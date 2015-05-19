package au.com.mineauz.MobHunting.achievements;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.events.MobHuntKillEvent;

public class FancyPants implements Achievement, Listener
{

	@Override
	public String getName()
	{
		return Messages.getString("achievements.fancypants.name"); //$NON-NLS-1$
	}

	@Override
	public String getID()
	{
		return "fancypants"; //$NON-NLS-1$
	}

	@Override
	public String getDescription()
	{
		return Messages.getString("achievements.fancypants.description"); //$NON-NLS-1$
	}

	@Override
	public double getPrize()
	{
		return MobHunting.config().specialFancyPants;
	}

	@EventHandler
	private void onKill(MobHuntKillEvent event)
	{
		if(event.getDamageInfo().weapon.getType() == Material.DIAMOND_SWORD && !event.getDamageInfo().weapon.getEnchantments().isEmpty() && 
		   event.getPlayer().getInventory().getHelmet() != null && event.getPlayer().getInventory().getHelmet().getType() == Material.DIAMOND_HELMET && !event.getPlayer().getInventory().getHelmet().getEnchantments().isEmpty() &&
		   event.getPlayer().getInventory().getChestplate() != null && event.getPlayer().getInventory().getChestplate().getType() == Material.DIAMOND_CHESTPLATE && !event.getPlayer().getInventory().getChestplate().getEnchantments().isEmpty() &&
		   event.getPlayer().getInventory().getLeggings() != null && event.getPlayer().getInventory().getLeggings().getType() == Material.DIAMOND_LEGGINGS && !event.getPlayer().getInventory().getLeggings().getEnchantments().isEmpty() &&
		   event.getPlayer().getInventory().getBoots() != null && event.getPlayer().getInventory().getBoots().getType() == Material.DIAMOND_BOOTS && !event.getPlayer().getInventory().getBoots().getEnchantments().isEmpty())
			MobHunting.instance.getAchievements().awardAchievement(this, event.getPlayer());
	}

	@Override
	public String getPrizeCmd() {
		return MobHunting.config().specialFancyPantsCmd;
	}

	@Override
	public String getPrizeCmdDescription() {
		return MobHunting.config().specialFancyPantsCmdDesc;
	}
}
