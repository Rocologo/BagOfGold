package one.lindegaard.MobHunting.rewards;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
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

	// public static final String MH_MONEY = "MH:Money";
	public static final String MH_HIDDEN_REWARD_DATA = "MH:HiddenRewardData";
	public static final String MH_REWARD_UUID = "b3f74fad-429f-4801-9e31-b8879cbae96f"; // Unique
																						// randomgenerated
																						// UUID
	// public static final String MH_REWARD_UUID =
	// "3eb9e46c-72ca-374d-8314-058a96cd0e8d"; //UUID from mineskin

	private static File file = new File(MobHunting.getInstance().getDataFolder(), "rewards.yml");
	private static YamlConfiguration config = new YamlConfiguration();

	private static Economy mEconomy;

	private static HashMap<Integer, Double> droppedMoney = new HashMap<Integer, Double>();
	private static HashMap<UUID, HiddenRewardData> placedMoney_hiddenRewardData = new HashMap<UUID, HiddenRewardData>();
	private static HashMap<UUID, Location> placedMoney_Location = new HashMap<UUID, Location>();

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

		loadAllStoredRewards();
	}

	public static Economy getEconomy() {
		return mEconomy;
	}

	public static HashMap<Integer, Double> getDroppedMoney() {
		return droppedMoney;
	}

	public static HashMap<UUID, HiddenRewardData> getLocations() {
		return placedMoney_hiddenRewardData;
	}

	public static HashMap<UUID, Location> getHiddenRewardData() {
		return placedMoney_Location;
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

	public static void dropMoneyOnGround(Player player, Entity killedEntity, Location location, double money) {
		Item item = null;
		if (GringottsCompat.isSupported()) {
			List<Denomination> denoms = Configuration.CONF.currency.denominations();
			int unit = Configuration.CONF.currency.unit;
			double rest = money;
			for (Denomination d : denoms) {
				ItemStack is = new ItemStack(d.key.type.getType(), 1);
				while (rest >= (d.value / unit)) {
					item = location.getWorld().dropItem(location, is);
					item.setMetadata(MH_HIDDEN_REWARD_DATA,
							new FixedMetadataValue(MobHunting.getInstance(),
									new HiddenRewardData(MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
											money, UUID.fromString(MH_REWARD_UUID), UUID.randomUUID())));
					rest = rest - (d.value / unit);
				}
			}
		} else {
			ItemStack is;
			if (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLED")) {
				MinecraftMob mob = MinecraftMob.getExtendedMobType(killedEntity);
				if (mob != null)
					is = mob.getCustomProfileHead(money);
				else // return texture https://mineskin.org/3384
					is = CustomItems.getCustomtexture(MH_REWARD_UUID,
							MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
							"eyJ0aW1lc3RhbXAiOjE0NzQzMzI0MzY1MDYsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg2NzczZDc0Y2Y1MDhmZDc3Yzc4MmZmZDI5ZGYyZmU0N2ZiNzE0YjViMGQ3ZGU2N2Q1Mjg2OTMxZTJmMWRmMiJ9fX0=",
							"JdvJksowuxYQ0eqf56J+Dmczg7zvlw2DbIc58Q33kRt65uMUNn2iRCQsbNpztC1cAAgyYMOyFDiOUZQeIK03CSRoPLDtWp2u501YoGKqhjgrE0V0UDh3JetWKz4Ob0KmATtY+4R2vSoMjHFEFppM0Oq+8ZER12FAiVEMAzeseFN3Z9fWAMc/V10LoquGBpq6ExTfSCEEMDEGZopF1T8ZBKL0vf4DVendfz4v3yl7bRBzISZEAnF+ECTa9z36r8HRqS8+s0eO/AWYQcRaKIu9H+wSK5F/1v+rgifeSlMAnt1Na8m1b5tMfNuq6pXxWCq4nUGgYVTOLUinqs9ZcFz3Z6Mtx5YtymKk2M0mzxmTm9+AeOL4s3K/UrJYQlcmLBJSv4hd6EigJXoashzWNCHKmFDYCdEhh4FArq4G9vRZtoudcTeMsvi0VmXIgER8U5iSfoTtzXcGbf/GT0ECtgfeA40f5oCqyE4nXreudMmvlDCBr/KHbILQWeeH/jhtYqQ6OwJb3Ji2Bs9F5fQmICSqk7X4yKzexf8rdDhOG1z+/TCot7K8unPVuQx46sXPeP7t2hCiHOXMAnOMt8vuL3gQUURIEM6fMryjmlKsgvk8Jo0gawavRCIZQtA6vT0JRRnSAchzEOA7QP1iiVV3LnwX9Yqw7oMJ/+REV1hWesuzDOc=",
							money, UUID.randomUUID());
			} else if (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("SKULL")) {
				is = CustomItems.getCustomtexture(MH_REWARD_UUID,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureValue,
						MobHunting.getConfigManager().dropMoneyOnGroundSkullTextureSignature, money, UUID.randomUUID());
			} else if (MobHunting.getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("KILLER")) {
				is = CustomItems.getPlayerHead(player.getName(), money);
			} else { // ITEM
				is = new ItemStack(Material.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundItem), 1);
			}
			item = location.getWorld().dropItem(location, is);
			RewardManager.getDroppedMoney().put(item.getEntityId(), money);
			item.setMetadata(MH_HIDDEN_REWARD_DATA,
					new FixedMetadataValue(MobHunting.getInstance(),
							new HiddenRewardData(MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName, money,
									UUID.fromString(MH_REWARD_UUID), UUID.randomUUID())));
			if (Misc.isMC18OrNewer()) {
				item.setCustomName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
						+ MobHunting.getRewardManager().format(money));
				item.setCustomNameVisible(true);
			}
		}
		if (item != null)
			Messages.debug("%s was dropped on the ground as item %s (# of rewards=%s)",
					MobHunting.getRewardManager().format(money),
					MobHunting.getConfigManager().dropMoneyOnGroundItemtype, droppedMoney.size());
	}

	public static void saveReward(UUID uuid) {
		try {
			config.options().header("This the rewards placed as blocks. Do not edit this file manually!");
			if (placedMoney_hiddenRewardData.containsKey(uuid)) {
				Location location = placedMoney_Location.get(uuid);
				HiddenRewardData hiddenRewardData = placedMoney_hiddenRewardData.get(uuid);
				ConfigurationSection section = config.createSection(uuid.toString());
				section.set("location", location);
				hiddenRewardData.save(section);
				Messages.debug("Saving Placed reward.");
				config.save(file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadReward(UUID uuid) {
		try {

			if (!file.exists())
				return;

			config.load(file);
			ConfigurationSection section = config.getConfigurationSection(uuid.toString());

			HiddenRewardData hiddenRewardData = new HiddenRewardData();
			hiddenRewardData.read(section);
			placedMoney_hiddenRewardData.put(uuid, hiddenRewardData);

			Location location = (Location) section.get("location");
			placedMoney_Location.put(uuid, location);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public static void loadAllStoredRewards() {
		try {

			if (!file.exists())
				return;

			config.load(file);
			int n = 0;
			int deleted = 0;
			for (String key : config.getKeys(false)) {
				ConfigurationSection section = config.getConfigurationSection(key);
				HiddenRewardData hiddenRewardData = new HiddenRewardData();
				hiddenRewardData.read(section);
				Location location = (Location) section.get("location");
				if (location.getBlock().getType() == Material.SKULL) {
					location.getBlock().setMetadata(MH_HIDDEN_REWARD_DATA,
							new FixedMetadataValue(MobHunting.getInstance(), new HiddenRewardData(hiddenRewardData)));
					placedMoney_hiddenRewardData.put(UUID.fromString(key), hiddenRewardData);
					placedMoney_Location.put(UUID.fromString(key), location);
					n++;
				} else {
					deleted++;
					config.set(key, null);
				}
			}

			if (deleted > 0) {
				Messages.debug("Deleted %s rewards from rewards.yml", deleted);
				File file_copy = new File(MobHunting.getInstance().getDataFolder(), "rewards.yml.old");
				Files.copy(file.toPath(), file_copy.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
						StandardCopyOption.REPLACE_EXISTING);
				config.save(file);
			}
			if (n > 0) {
				Messages.debug("Loaded %s \"bags of gold\" from disk.", n);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

}
