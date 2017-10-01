package one.lindegaard.MobHunting.rewards;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.ChatColor;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.mobs.MinecraftMob;
import one.lindegaard.MobHunting.util.Misc;

public class CustomItems {

	private MobHunting plugin;

	public CustomItems(MobHunting plugin) {
		this.plugin = plugin;
	}

	/**
	 * Return an ItemStack with the Players head texture.
	 *
	 * @param name
	 * @param money
	 * @return
	 */
	public ItemStack getPlayerHead(String name, double money) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
		skull.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(name);
		if (money == 0)
			skullMeta.setDisplayName(name);
		else
			skullMeta.setDisplayName(name + " (" + plugin.getRewardManager().getEconomy().format(money) + ")");
		skull.setItemMeta(skullMeta);
		return skull;
	}

	/**
	 * Return an ItemStack with a custom texture. If Mojang changes the way they
	 * calculate Signatures this method will stop working.
	 *
	 * @param mPlayerUUID
	 * @param mDisplayName
	 * @param mTextureValue
	 * @param mTextureSignature
	 * @param money
	 * @return ItemStack with custom texture.
	 */
	public ItemStack getCustomtexture(UUID mPlayerUUID, String mDisplayName, String mTextureValue,
			String mTextureSignature, double money, UUID uniqueRewardUuid) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		if (mTextureSignature.isEmpty() || mTextureValue.isEmpty())
			return skull;

		ItemMeta skullMeta = skull.getItemMeta();

		GameProfile profile = new GameProfile(mPlayerUUID, mDisplayName);
		profile.getProperties().put("textures", new Property("textures", mTextureValue, mTextureSignature));
		Field profileField = null;

		try {
			profileField = skullMeta.getClass().getDeclaredField("profile");
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			return skull;
		}

		profileField.setAccessible(true);

		try {
			profileField.set(skullMeta, profile);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		DecimalFormat df = new DecimalFormat("#.#####");
		skullMeta.setLore(new ArrayList<String>(
				Arrays.asList("Hidden:" + mDisplayName, "Hidden:" + df.format(money),
						"Hidden:" + mPlayerUUID, money == 0 ? "Hidden:" : "Hidden:" + uniqueRewardUuid)));
		if (money == 0)
			skullMeta.setDisplayName(
					ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor) + mDisplayName);
		else
			skullMeta.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
					+ mDisplayName + " (" + plugin.getRewardManager().format(money) + ")");

		skull.setItemMeta(skullMeta);
		return skull;
	}

	public ItemStack getCustomHead(MinecraftMob minecraftMob, String name, int amount, double money) {
		ItemStack skull;
		switch (minecraftMob) {
		case Skeleton:
			skull = new ItemStack(Material.SKULL_ITEM, amount, (short) 0);
			skull = plugin.getRewardManager().setDisplayNameAndHiddenLores(skull, minecraftMob.getFriendlyName(), money,
					UUID.fromString(RewardManager.MH_REWARD_KILLED_UUID));
			break;

		case WitherSkeleton:
			skull = new ItemStack(Material.SKULL_ITEM, amount, (short) 1);
			skull = plugin.getRewardManager().setDisplayNameAndHiddenLores(skull, minecraftMob.getFriendlyName(), money,
					UUID.fromString(RewardManager.MH_REWARD_KILLED_UUID));
			break;

		case Zombie:
			skull = new ItemStack(Material.SKULL_ITEM, amount, (short) 2);
			skull = plugin.getRewardManager().setDisplayNameAndHiddenLores(skull, minecraftMob.getFriendlyName(), money,
					UUID.fromString(RewardManager.MH_REWARD_KILLED_UUID));
			break;

		case PvpPlayer:
			skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta sm = (SkullMeta) skull.getItemMeta();
			sm.setOwner(name);
			skull.setItemMeta(sm);
			break;

		case Creeper:
			skull = new ItemStack(Material.SKULL_ITEM, amount, (short) 4);
			skull = plugin.getRewardManager().setDisplayNameAndHiddenLores(skull, minecraftMob.getFriendlyName(), money,
					UUID.fromString(RewardManager.MH_REWARD_KILLED_UUID));
			break;

		case EnderDragon:
			skull = new ItemStack(Material.SKULL_ITEM, amount, (short) 5);
			skull = plugin.getRewardManager().setDisplayNameAndHiddenLores(skull, minecraftMob.getFriendlyName(), money,
					UUID.fromString(RewardManager.MH_REWARD_KILLED_UUID));
			break;

		default:
			ItemStack is = new ItemStack(getCustomtexture(UUID.fromString(RewardManager.MH_REWARD_KILLED_UUID),
					minecraftMob.getFriendlyName(), minecraftMob.getTextureValue(), minecraftMob.getTextureSignature(),
					money, UUID.randomUUID()));
			is.setAmount(amount);
			return is;
		}
		return skull;
	}

}
