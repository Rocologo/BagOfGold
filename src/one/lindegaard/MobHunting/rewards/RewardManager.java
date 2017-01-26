package one.lindegaard.MobHunting.rewards;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.gestern.gringotts.Configuration;
import org.gestern.gringotts.currency.Denomination;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.compatibility.GringottsCompat;
import one.lindegaard.MobHunting.mobs.MinecraftMob;
import one.lindegaard.MobHunting.util.Misc;

public class RewardManager implements Listener {

	public static final String MH_MONEY = "MH:Money";

	private static Economy mEconomy;

	private static HashMap<Integer, Double> droppedMoney = new HashMap<Integer, Double>();

	public RewardManager(MobHunting instance) {
		RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
		if (economyProvider == null) {
			Bukkit.getLogger().severe(Messages.getString(instance.getName().toLowerCase() + ".hook.econ"));
			Bukkit.getPluginManager().disablePlugin(instance);
			return;
		}
		mEconomy = economyProvider.getProvider();

		Bukkit.getPluginManager().registerEvents(new RewardListeners(), instance);
		if (Misc.isMC18OrNewer())
			Bukkit.getPluginManager().registerEvents(new MoneyMergeEventListener(), MobHunting.getInstance());

	}

	public Economy getEconomy() {
		return mEconomy;
	}

	public static HashMap<Integer, Double> getDroppedMoney() {
		return droppedMoney;
	}

	public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double amount) {
		EconomyResponse result = mEconomy.withdrawPlayer(offlinePlayer, amount);
		if (!result.transactionSuccess() && offlinePlayer.isOnline())
			((Player) offlinePlayer).sendMessage(ChatColor.RED + "Unable to remove money: " + result.errorMessage);
		return result;
	}

	public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double amount) {
		EconomyResponse result = mEconomy.depositPlayer(offlinePlayer, amount);
		if (!result.transactionSuccess() && offlinePlayer.isOnline())
			((Player) offlinePlayer).sendMessage(ChatColor.RED + "Unable to add money: " + result.errorMessage);
		return result;
	}

	public String format(double amount) {
		return mEconomy.format(amount);
	}

	public double getBalance(OfflinePlayer offlinePlayer) {
		return mEconomy.getBalance(offlinePlayer);
	}

	public boolean has(OfflinePlayer offlinePlayer, double amount) {
		return mEconomy.has(offlinePlayer, amount);
	}

	public static void dropMoneyOnGround(Player player, Entity entity, double money) {
		Item item = null;
		if (GringottsCompat.isSupported()) {
			List<Denomination> denoms = Configuration.CONF.currency.denominations();
			int unit = Configuration.CONF.currency.unit;
			double rest = money;
			Location location = entity.getLocation();
			for (Denomination d : denoms) {
				ItemStack is = new ItemStack(d.key.type.getType(), 1);
				while (rest >= (d.value / unit)) {
					item = location.getWorld().dropItem(location, is);
					item.setMetadata(MH_MONEY, new FixedMetadataValue(MobHunting.getInstance(), (double) 0));
					rest = rest - (d.value / unit);
				}
			}
		} else {
			Location location = entity.getLocation();
			ItemStack is;
			if (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLED")) {
				MinecraftMob mob = MinecraftMob.getExtendedMobType(entity);
				if (mob != null)
					is = mob.getCustomProfileHead();
				else // return texture https://mineskin.org/4539
					is = CustomItems.getCustomtexture("5db7c779-322a-3d6b-9ebc-5ed947982b85",
							Messages.getString("mobhunting.reward.name"),
							"eyJ0aW1lc3RhbXAiOjE0Nzg5NzgwNzg4MDQsInByb2ZpbGVJZCI6ImRhNzQ2NWVkMjljYjRkZTA5MzRkOTIwMTc0NDkxMzU1IiwicHJvZmlsZU5hbWUiOiJJc2F5bGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2FiODg0NmU2MmY5ZjU5ODFiOTVkZGExYzFlYjU1YzUxZGRhYjEwNGU4MmYzMWFiM2QxMWJkZjhjZDc2N2VlNjEifX19",
							"pGbvG0ULeTi9G2HYzaPt7UvFuqXudnDbHS9ppWiMjdozfoKXu2wraEeiVkd5Qw3MCcyAi6M/+YMOwa3Yb9N+mBvkUTAhT4AXBGt4OjPNfkEl4bbP4iVpaZNHA+Id+hPlFHKIzIhQ4wi75WuhkIkEtj7HiGoIipeSQOudr/p3AtzNYRoSGci78YZLblXAjK8FnevdTy2zKHIqg96c3D3d8Nu4qaZIPcUeIB7LFZNem9xjCIqPwQqjRIxg9qF4xxiJa7SHVJETvj7DP4GOLee87vaae5amgpa+doYH8ecx9qatzr6QSlrVoIA1zndDV1uf7nY6jdTiPVnAM3tdk9HZ5yiQ0yOpj1sPh7NAhwp70cBCH+owH8aSqdykhe3Kk8WbVodEwbN5CMkYpI9DnceWovtGNHS9yZam8VmZXM7VevYt49jwzTPaQrzCyjlEOW7wpMxedJ/T9346c03IjybWW7e+G8YPwJ9tgo5tcFHx1QReIv7qcNJ54dIM06JxeG/CnIeLBrAOK6A2eWw6/QYzpPdD8+xZgvA8nBE8NpNRXhPxSBYEIT9YhPesnjC5nHACBaY9OtRJHLvTQQICJaMmjTX6xJJpczU0I8USOVjZAPSGTwkymS+DTAp2oBFsdWBOkeXcCrT56sj5pl/uLyNS2X9WDkCisEiG3eznZ1lQ4Tw=");
			} else if (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")) {
				is = CustomItems.getCustomtexture(MobHunting.getConfigManager().dropMoneyOnGroundSkullPlayerUUID,
						Messages.getString("mobhunting.reward.name"),
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature);
			} else if (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLER")) {
				is = CustomItems.getPlayerHead(player.getName());
			} else { // ITEM
				is = new ItemStack(Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem), 1);
			}
			item = location.getWorld().dropItem(location, is);
			RewardManager.getDroppedMoney().put(item.getEntityId(), money);
			item.setMetadata(MH_MONEY, new FixedMetadataValue(MobHunting.getInstance(), money));
			if (Misc.isMC18OrNewer()) {
				item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ MobHunting.getRewardManager().format(money));
				item.setCustomNameVisible(true);
			}
		}
		if (item != null)
			Messages.debug("%s dropped %s on the ground as item %s (# of rewards=%s)", entity.getType(),
					MobHunting.getRewardManager().format(money), item.getType(), droppedMoney.size());
	}

}
