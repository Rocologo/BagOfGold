package one.lindegaard.MobHunting.rewards;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import one.lindegaard.MobHunting.MobHunting;

public class CustomItems {

	/**
	 * Return an ItemStack with the Players head texture.
	 * 
	 * @param offlinePlayer
	 * @return
	 */
	public static ItemStack getPlayerHead(String name, double money) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1);
		skull.setDurability((short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setOwner(name);
		if (money == 0)
			skullMeta.setDisplayName(name);
		else
			skullMeta.setDisplayName(name + " (" + RewardManager.getEconomy().format(money) + ")");
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
	 * 
	 * @return ItemStack with custom texture.
	 */
	public static ItemStack getCustomtexture(String mPlayerUUID, String mDisplayName, String mTextureValue,
			String mTextureSignature, double money, UUID uniqueRewardUuid) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		if (mTextureSignature.isEmpty() || mTextureValue.isEmpty())
			return skull;

		ItemMeta skullMeta = skull.getItemMeta();

		GameProfile profile = new GameProfile(UUID.fromString(mPlayerUUID), mDisplayName);
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

		skullMeta.setLore(new ArrayList<String>(
				Arrays.asList("Hidden:" + MobHunting.getConfigManager().dropMoneyOnGroundSkullRewardName,
						"Hidden:" + String.valueOf(money), "Hidden:" + mPlayerUUID, "Hidden:" + uniqueRewardUuid)));
		if (money == 0)
			skullMeta.setDisplayName(
					ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor) + mDisplayName);
		else
			skullMeta.setDisplayName(ChatColor.valueOf(MobHunting.getConfigManager().dropMoneyOnGroundTextColor)
					+ mDisplayName + " (" + MobHunting.getRewardManager().format(Double.valueOf(money)) + " )");

		skull.setItemMeta(skullMeta);
		return skull;
	}

}
