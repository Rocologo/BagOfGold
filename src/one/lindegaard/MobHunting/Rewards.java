package one.lindegaard.MobHunting;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class Rewards implements Listener {

	public static final String MH_MONEY = "MH:Money";

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPickupMoney(PlayerPickupItemEvent e) {
		double money = 0;
		Item item = e.getItem();
		if (item.hasMetadata(MH_MONEY)) {
			List<MetadataValue> metadata = item.getMetadata(MH_MONEY);
			for (MetadataValue mdv : metadata) {
				if (mdv.getOwningPlugin() == MobHunting.getInstance()) {
					money = (Double) mdv.value();
					e.getItem().remove();
					Player player = e.getPlayer();
					MobHunting.getEconomy().depositPlayer(player, money);
					MobHunting.playerActionBarMessage(player, Messages.getString("mobhunting.moneypickup", "money",
							MobHunting.getEconomy().format(money)));
					break;
				}
			}

		}
	}

	public static void dropMoneyOnGround(Entity entity, double money) {
		Location location = entity.getLocation();
		//ItemStack is = new ItemStack(Material.GOLD_NUGGET, 1);
		ItemStack is = new ItemStack(Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem), 1);
		Item item = location.getWorld().dropItem(location, is);
		item.setMetadata(MH_MONEY, new FixedMetadataValue(MobHunting.getInstance(), money));
		item.setCustomName(MobHunting.getEconomy().format(money));
		item.setCustomNameVisible(true);
	}

}
