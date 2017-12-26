package one.lindegaard.BagOfGold;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import one.lindegaard.BagOfGold.util.Misc;
import one.lindegaard.MobHunting.Messages;
import one.lindegaard.MobHunting.rewards.Reward;

public class CustomItems {

	private BagOfGold plugin;

	public CustomItems(BagOfGold plugin) {
		this.plugin = plugin;
	}

	// How to get Playerskin
	// https://www.spigotmc.org/threads/how-to-get-a-players-texture.244966/

	/**
	 * Return an ItemStack with the Players head texture.
	 *
	 * @param name
	 * @param money
	 * @return
	 */
	public ItemStack getPlayerHead(UUID uuid, int amount, double money) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		String name = Bukkit.getOfflinePlayer(uuid).getName();

		String[] skin = getFromName(uuid);

		if (skin == null)
			return getPlayerHeadOwningPlayer(uuid, amount, money);

		if (skin[0].isEmpty() || skin[1].isEmpty())
			return skull;

		ItemMeta skullMeta = skull.getItemMeta();

		GameProfile profile = new GameProfile(uuid, name);
		profile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));
		Field profileField = null;

		try {
			profileField = skullMeta.getClass().getDeclaredField("profile");
		} catch (NoSuchFieldException | SecurityException e) {
			 return getPlayerHeadGameProfile(uuid, amount, money);
		}

		profileField.setAccessible(true);

		try {
			profileField.set(skullMeta, profile);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return getPlayerHeadGameProfile(uuid, amount, money);
		}

		skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + name,
				"Hidden:" + String.format(Locale.ENGLISH, "%.5f", money), "Hidden:" + Reward.MH_REWARD_KILLER_UUID,
				money == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(), "Hidden:" + uuid)));
		if (money == 0)
			skullMeta.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + name);
		else
			skullMeta.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + name
					+ " (" + Misc.format(money) + ")");

		skull.setItemMeta(skullMeta);
		Messages.debug("CustomItems: got the skin from URL database (%s)", name);
		return skull;
	}

	private String[] getFromName(UUID uuid) {
		try {
			URL url_1 = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
			InputStreamReader reader_1;
			reader_1 = new InputStreamReader(url_1.openStream());

			JsonElement json = new JsonParser().parse(reader_1);
			if (json.isJsonObject()) {
				JsonObject textureProperty = json.getAsJsonObject().get("properties").getAsJsonArray().get(0)
						.getAsJsonObject();
				String texture = textureProperty.get("value").getAsString();
				String signature = textureProperty.get("signature").getAsString();

				return new String[] { texture, signature };
			} else return null;

		} catch (IOException e) {
			Messages.debug("Could not get skin data from session servers!");
			//e.printStackTrace();
			return null;
		}
	}

	/**
	 * Return an ItemStack with the Players head texture.
	 *
	 * @param player
	 *            uuid
	 * @param money
	 * @return
	 */
	public ItemStack getPlayerHeadGameProfile(UUID uuid, int amount, double money) {

		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

		GameProfile profile = new GameProfile(uuid, offlinePlayer.getName());
		Field profileField = null;

		try {
			profileField = skullMeta.getClass().getDeclaredField("profile");
		} catch (NoSuchFieldException | SecurityException e) {
			return getPlayerHeadOwningPlayer(uuid, amount, money);
		}

		profileField.setAccessible(true);

		try {
			profileField.set(skullMeta, profile);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return getPlayerHeadOwningPlayer(uuid, amount, money);
		}

		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));

		skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + offlinePlayer.getName(),
				"Hidden:" + String.format(Locale.ENGLISH, "%.5f", money), "Hidden:" + Reward.MH_REWARD_KILLER_UUID,
				money == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(), "Hidden:" + uuid)));
		if (money == 0)
			skullMeta.setDisplayName(
					ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + offlinePlayer.getName());
		else
			skullMeta.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
					+ offlinePlayer.getName() + " (" + Misc.format(money) + ")");
		if (money == 0) {
			skullMeta.setDisplayName(offlinePlayer.getName());
			skull.setAmount(amount);
		} else {
			skullMeta.setDisplayName(
					offlinePlayer.getName() + " (" + Misc.format(money) + ")");
			skull.setAmount(1);
		}
		skull.setItemMeta(skullMeta);
		Messages.debug("CustomItems: got the skin from GameProfile (%s)", offlinePlayer.getName());
		return skull;
	}


	public ItemStack getPlayerHeadOwningPlayer(UUID uuid, int amount, double money) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + name,
				"Hidden:" + String.format(Locale.ENGLISH, "%.5f", money), "Hidden:" + Reward.MH_REWARD_KILLER_UUID,
				money == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(), "Hidden:" + uuid)));
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		if (money == 0) {
			skullMeta.setDisplayName(name);
			skull.setAmount(amount);
		} else {
			skullMeta.setDisplayName(name + " (" + Misc.format(money) + ")");
			skull.setAmount(1);
		}
		skull.setItemMeta(skullMeta);
		Messages.debug("CustomItems: got the skin using OwningPlayer (%s)", name);
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
			String mTextureSignature, double money, UUID uniqueRewardUuid, UUID skinUuid) {
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

		skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + mDisplayName,
				"Hidden:" + String.format(Locale.ENGLISH, "%.5f", money), "Hidden:" + mPlayerUUID,
				money == 0 ? "Hidden:" : "Hidden:" + uniqueRewardUuid, "Hidden:" + skinUuid)));
		if (money == 0)
			skullMeta.setDisplayName(
					ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor) + mDisplayName);
		else
			skullMeta.setDisplayName(ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor)
					+ mDisplayName + " (" + Misc.format(money) + ")");

		skull.setItemMeta(skullMeta);
		return skull;
	}

	}
