package one.lindegaard.BagOfGold.rewards;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.Reward;
import one.lindegaard.BagOfGold.skins.Skins;
import one.lindegaard.BagOfGold.skins.Skins_1_10_R1;
import one.lindegaard.BagOfGold.skins.Skins_1_11_R1;
import one.lindegaard.BagOfGold.skins.Skins_1_12_R1;
import one.lindegaard.BagOfGold.skins.Skins_1_13_R1;
import one.lindegaard.BagOfGold.skins.Skins_1_13_R2;
import one.lindegaard.BagOfGold.skins.Skins_1_8_R1;
import one.lindegaard.BagOfGold.skins.Skins_1_8_R2;
import one.lindegaard.BagOfGold.skins.Skins_1_8_R3;
import one.lindegaard.BagOfGold.skins.Skins_1_9_R1;
import one.lindegaard.BagOfGold.util.Misc;
import one.lindegaard.MobHunting.MobHunting;
import one.lindegaard.MobHunting.PlayerSettings;

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
	private Skins getSkinsClass() {
		String version;
		Skins sk = null;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
			whatVersionAreYouUsingException.printStackTrace();
			return null;
		}
		Bukkit.getLogger().info("[MobHunting] Your server is running version " + version);
		if (version.equals("v1_13_R2")) {
			sk = new Skins_1_13_R2();
		} else if (version.equals("v1_13_R1")) {
			sk = new Skins_1_13_R1();
		} else if (version.equals("v1_12_R1")) {
			sk = new Skins_1_12_R1();
		} else if (version.equals("v1_11_R1")) {
			plugin.getMessages().debug("v1_11_R1");
			sk = new Skins_1_11_R1();
		} else if (version.equals("v1_10_R1")) {
			sk = new Skins_1_10_R1();
		} else if (version.equals("v1_9_R1")) {
			sk = new Skins_1_9_R1();
		} else if (version.equals("v1_8_R3")) {
			sk = new Skins_1_8_R3();
		} else if (version.equals("v1_8_R2")) {
			sk = new Skins_1_8_R2();
		} else if (version.equals("v1_8_R1")) {
			sk = new Skins_1_8_R1();
		}
		return sk;
	}

	/**
	 * Return an ItemStack with the Players head texture.
	 *
	 * @param name
	 * @param money
	 * @return
	 */
	public ItemStack getPlayerHead(UUID uuid, int amount, double money) {

		// TODO: Which skull is working
		ItemStack skull;
		if (Misc.isMC113OrNewer())
			skull = new ItemStack(Material.PLAYER_HEAD);
		else
			skull = new ItemStack(Material.LEGACY_SKULL, (short) 3);
		skull.setAmount(amount);

		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

		PlayerSettings ps = MobHunting.getInstance().getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		String[] skinCache = new String[2];

		if (ps.getTexture() == null || ps.getSignature() == null || ps.getTexture().isEmpty()
				|| ps.getSignature().isEmpty()) {
			if (offlinePlayer.isOnline()) {
				Player player = (Player) offlinePlayer;
				Skins sk = getSkinsClass();
				if (sk != null) {
					plugin.getMessages().debug("Trying to fecth skin from Online Player Profile");
					skinCache = sk.getSkin(player);
				} else {
					plugin.getMessages().debug("Trying to fecth skin from Minecraft Servers");
					skinCache = getSkinFromUUID(uuid);
				}
			}

			if ((skinCache == null || skinCache[0] == null || skinCache[0].isEmpty() || skinCache[1] == null
					|| skinCache[1].isEmpty()) && Misc.isMC112OrNewer())
				return getPlayerHeadOwningPlayer(uuid, amount, money);

			if (skinCache != null && !skinCache[0].isEmpty() && !skinCache[1].isEmpty()) {
				ps.setTexture(skinCache[0]);
				ps.setSignature(skinCache[1]);
				MobHunting.getInstance().getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
				MobHunting.getInstance().getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
			} else {
				plugin.getMessages().debug("Empty skin");
				return skull;
			}
		} else {
			if (offlinePlayer.isOnline()) {
				Player player = (Player) offlinePlayer;
				Skins sk = getSkinsClass();
				if (sk != null) {
					String[] skinOnline = sk.getSkin(player);
					if (skinOnline != null && !skinOnline.equals(skinCache)) {
						plugin.getMessages().debug("%s has changed skin, updating MobHunting Skin cache");
						ps.setTexture(skinOnline[0]);
						ps.setSignature(skinOnline[1]);
						MobHunting.getInstance().getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
						MobHunting.getInstance().getDataStoreManager().updatePlayerSettings(offlinePlayer, ps);
					}
				}
			}
			skinCache[0] = ps.getTexture();
			skinCache[1] = ps.getSignature();
			plugin.getMessages().debug("%s using skin from MobHunting Skin Cache", offlinePlayer.getName());
		}

		skull = new ItemStack(getCustomtexture(UUID.fromString(Reward.MH_REWARD_KILLED_UUID), offlinePlayer.getName(),
				skinCache[0], skinCache[1], money, UUID.randomUUID(), uuid));
		skull.setAmount(amount);
		return skull;
	}

	private String[] getSkinFromUUID(UUID uuid) {
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
			} else {
				plugin.getMessages().debug("(1) Could not get skin data from session servers!");
				return null;
			}

		} catch (IOException e) {
			plugin.getMessages().debug("(2)Could not get skin data from session servers!");
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

		// TODO: Which SKULL is working??
		ItemStack skull;
		if (Misc.isMC113OrNewer())
			skull = new ItemStack(Material.PLAYER_HEAD);
		else
			skull = new ItemStack(Material.LEGACY_SKULL, (short) 3);

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
				money == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(), "Hidden:" + uuid,
				plugin.getMessages().getString("mobhunting.reward.name"))));
		ChatColor color = ChatColor.GOLD;
		try {
			color = ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor.toUpperCase());
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "drop-money-on-ground-text-color in your config.yml cant be read.");
		}
		if (money == 0)
			skullMeta.setDisplayName(color + offlinePlayer.getName());
		else
			skullMeta.setDisplayName(color + offlinePlayer.getName() + " (" + Misc.format(money) + ")");
		if (money == 0) {
			skullMeta.setDisplayName(offlinePlayer.getName());
			skull.setAmount(amount);
		} else {
			skullMeta.setDisplayName(offlinePlayer.getName() + " (" + Misc.format(money) + ")");
			skull.setAmount(1);
		}
		skull.setItemMeta(skullMeta);
		plugin.getMessages().debug("CustomItems: set the skin using GameProfile (%s,%s)", offlinePlayer.getName(),
				uuid.toString());
		return skull;
	}

	// TODO: Which skull is working
	public ItemStack getPlayerHeadOwningPlayer(UUID uuid, int amount, double money) {
		ItemStack skull;
		if (Misc.isMC113OrNewer())
			skull = new ItemStack(Material.PLAYER_HEAD);
		else
			skull = new ItemStack(Material.LEGACY_SKULL, (short) 3);

		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + name,
				"Hidden:" + String.format(Locale.ENGLISH, "%.5f", money), "Hidden:" + Reward.MH_REWARD_KILLER_UUID,
				money == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(), "Hidden:" + uuid,
				plugin.getMessages().getString("mobhunting.reward.name"))));
		skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		if (money == 0) {
			skullMeta.setDisplayName(name);
			skull.setAmount(amount);
		} else {
			skullMeta.setDisplayName(name + " (" + Misc.format(money) + ")");
			skull.setAmount(1);
		}
		skull.setItemMeta(skullMeta);
		plugin.getMessages().debug("CustomItems: set the skin using OwningPlayer (%s)", name);
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
		// TODO: which head is working
		ItemStack skull;
		if (Misc.isMC113OrNewer())
			skull = new ItemStack(Material.PLAYER_HEAD);
		else
			skull = new ItemStack(Material.LEGACY_SKULL, (short) 3);

		if (mTextureSignature.isEmpty() || mTextureValue.isEmpty())
			return skull;

		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

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

		if (mPlayerUUID.equals(UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID)))
			skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + mDisplayName,
					"Hidden:" + String.format(Locale.ENGLISH, "%.5f", money), "Hidden:" + mPlayerUUID,
					money == 0 ? "Hidden:" : "Hidden:" + uniqueRewardUuid, "Hidden:" + skinUuid)));
		else
			skullMeta.setLore(new ArrayList<String>(
					Arrays.asList("Hidden:" + mDisplayName, "Hidden:" + String.format(Locale.ENGLISH, "%.5f", money),
							"Hidden:" + mPlayerUUID, money == 0 ? "Hidden:" : "Hidden:" + uniqueRewardUuid,
							"Hidden:" + skinUuid, plugin.getMessages().getString("mobhunting.reward.name"))));

		ChatColor color = ChatColor.GOLD;
		try {
			color = ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor.toUpperCase());
		} catch (Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[BagOfGold] " + ChatColor.RED
					+ "drop-money-on-ground-text-color in your config.yml cant be read.");
		}
		if (money == 0)
			skullMeta.setDisplayName(color + mDisplayName);
		else
			skullMeta.setDisplayName(color + mDisplayName + " (" + Misc.format(money) + ")");

		skull.setItemMeta(skullMeta);
		return skull;
	}

}
