package one.lindegaard.BagOfGold.rewards;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.Bukkit;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.PlayerSettings;
import one.lindegaard.Core.Strings;
import one.lindegaard.Core.Tools;
import one.lindegaard.Core.mobs.MobType;
import one.lindegaard.Core.server.Servers;
import one.lindegaard.Core.shared.Skins;
import one.lindegaard.Core.rewards.CoreCustomItems;
import one.lindegaard.Core.rewards.Reward;
import one.lindegaard.Core.rewards.RewardType;

public class CustomItems {

	private BagOfGold plugin;

	public CustomItems() {
		this.plugin = BagOfGold.getInstance();
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
	public ItemStack getPlayerHead(UUID uuid, String name, int amount, double money) {
		ItemStack skull = CoreCustomItems.getDefaultPlayerHead(amount);
		skull.setAmount(amount);
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
		PlayerSettings ps = plugin.getPlayerSettingsManager().getPlayerSettings(offlinePlayer);
		if (ps.getTexture() == null || ps.getSignature() == null || ps.getTexture().isEmpty()
				|| ps.getSignature().isEmpty()) {
			plugin.getMessages().debug("No skin found i database");
			String[] onlineSkin = new String[2];
			if (offlinePlayer.isOnline()) {
				Player player = (Player) offlinePlayer;
				Skins sk = CoreCustomItems.getSkinsClass();
				if (sk != null) {
					plugin.getMessages().debug("Trying to fecth skin from Online Player Profile");
					onlineSkin = sk.getSkin(player);
				} else {
					plugin.getMessages().debug("Trying to fecth skin from Minecraft Servers");
					onlineSkin = getSkinFromUUID(uuid);
				}
			}

			if ((onlineSkin == null || onlineSkin[0] == null || onlineSkin[0].isEmpty() || onlineSkin[1] == null
					|| onlineSkin[1].isEmpty()) && Servers.isMC112OrNewer())
				return getPlayerHeadOwningPlayer(uuid, name, amount, money);

			if (onlineSkin != null && onlineSkin[0] != null && !onlineSkin[0].isEmpty() && onlineSkin[1] != null
					&& !onlineSkin[1].isEmpty()) {
				ps.setTexture(onlineSkin[0]);
				ps.setSignature(onlineSkin[1]);
				plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
			} else {
				plugin.getMessages().debug("Empty skin");
				return skull;
			}
		} else {
			if (offlinePlayer.isOnline()) {
				Player player = (Player) offlinePlayer;
				Skins sk = CoreCustomItems.getSkinsClass();
				if (sk != null) {
					String[] skin = sk.getSkin(player);
					if (skin != null && skin[0] != null && !skin[0].equals(ps.getTexture())) {
						plugin.getMessages().debug("%s has changed skin, updating database with new skin. (%s,%s)",
								player.getName(), ps.getTexture(), skin[0]);
						ps.setTexture(skin[0]);
						ps.setSignature(skin[1]);
						plugin.getPlayerSettingsManager().setPlayerSettings(offlinePlayer, ps);
					}
				}
			} else
				plugin.getMessages().debug("%s using skin from skin Cache", offlinePlayer.getName());
		}

		skull = new ItemStack(new CoreCustomItems().getCustomtexture(
				new Reward(offlinePlayer.getName(), money, RewardType.KILLED, uuid), ps.getTexture(),
				ps.getSignature()));
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

	private ItemStack getPlayerHeadOwningPlayer(UUID uuid, String name, int amount, double money) {
		ItemStack skull = CoreCustomItems.getDefaultPlayerHead(amount);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden(0):" + name,
				"Hidden(1):" + String.format(Locale.ENGLISH, "%.5f", money), "Hidden(2):" + RewardType.KILLED.getType(),
				"Hidden(4):" + uuid,
				"Hidden(5):"
						+ Strings.encode(String.format(Locale.ENGLISH, "%.5f", money) + RewardType.KILLED.getType()),
				Core.getMessages().getString("core.reward.lore"))));

		if (Bukkit.getOfflinePlayer(uuid) != null)
			skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
		else
			skullMeta.setOwner(name);

		if (money == 0) {
			skullMeta.setDisplayName(name);
			skull.setAmount(amount);
		} else {
			skullMeta.setDisplayName(name + " (" + Tools.format(money) + ")");
			skull.setAmount(1);
		}

		skull.setItemMeta(skullMeta);
		plugin.getMessages().debug("CustomItems: set the skin using OwningPlayer/Owner (%s)", name);
		return skull;
	}

	/**
	 * Return an ItemStack with a custom texture. If Mojang changes the way they
	 * calculate Signatures this method will stop working.
	 *
	 * @param mDisplayName
	 * @param money
	 * @param mRewardType
	 * @param mTextureValue
	 * @param mTextureSignature
	 * @return ItemStack with custom texture.
	 */
	/**
	 * public ItemStack getCustomtexture(String mDisplayName, double money,
	 * RewardType mRewardType, UUID skinUuid, String mTextureValue, String
	 * mTextureSignature) { ItemStack skull =
	 * CoreCustomItems.getDefaultPlayerHead(1); if (mTextureSignature.isEmpty() ||
	 * mTextureValue.isEmpty()) return skull;
	 * 
	 * SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
	 * 
	 * GameProfile profile = new GameProfile(skinUuid, mDisplayName); if
	 * (mTextureSignature.isEmpty()) profile.getProperties().put("textures", new
	 * Property("textures", mTextureValue)); else
	 * profile.getProperties().put("textures", new Property("textures",
	 * mTextureValue, mTextureSignature)); Field profileField = null;
	 * 
	 * try { profileField = skullMeta.getClass().getDeclaredField("profile"); }
	 * catch (NoSuchFieldException | SecurityException e) { e.printStackTrace();
	 * return skull; }
	 * 
	 * profileField.setAccessible(true);
	 * 
	 * try { profileField.set(skullMeta, profile); } catch (IllegalArgumentException
	 * | IllegalAccessException e) { e.printStackTrace(); } if (mRewardType ==
	 * RewardType.BAGOFGOLD) skullMeta.setLore(new
	 * ArrayList<String>(Arrays.asList("Hidden(0):" + mDisplayName, "Hidden(1):" +
	 * String.format(Locale.ENGLISH, "%.5f", money), "Hidden(2):" +
	 * mRewardType.getType(), "Hidden(4):" + skinUuid, "Hidden(5):" +
	 * Strings.encode(String.format(Locale.ENGLISH, "%.5f", money) +
	 * mRewardType.getType())))); else skullMeta.setLore(new
	 * ArrayList<String>(Arrays.asList("Hidden(0):" + mDisplayName, "Hidden(1):" +
	 * String.format(Locale.ENGLISH, "%.5f", money), "Hidden(2):" + mRewardType,
	 * "Hidden(4):" + skinUuid, "Hidden(5):" +
	 * Strings.encode(String.format(Locale.ENGLISH, "%.5f", money) + mRewardType),
	 * Core.getMessages().getString("core.reward.lore")))); ChatColor color =
	 * ChatColor.GOLD; try { color =
	 * ChatColor.valueOf(plugin.getConfigManager().dropMoneyOnGroundTextColor.toUpperCase());
	 * } catch (Exception e) { Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD
	 * + "[BagOfGold] " + ChatColor.RED + "drop-money-on-ground-text-color in your
	 * config.yml cant be read."); } if (money == 0) skullMeta.setDisplayName(color
	 * + mDisplayName); else skullMeta.setDisplayName(color + mDisplayName + " (" +
	 * Tools.format(money) + ")");
	 * 
	 * skull.setItemMeta(skullMeta); return skull; }
	 **/

	public ItemStack getCustomHead(MobType minecraftMob, String name, int amount, double money, UUID skinUUID) {
		ItemStack skull;
		switch (minecraftMob) {
		case Skeleton:
			skull = CoreCustomItems.getDefaultSkeletonHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getFriendlyName(), money, RewardType.KILLED, skinUUID));
			break;

		case WitherSkeleton:
			skull = CoreCustomItems.getDefaultWitherSkeletonHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getFriendlyName(), money, RewardType.KILLED, skinUUID));
			break;

		case Zombie:
			skull = CoreCustomItems.getDefaultZombieHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getFriendlyName(), money, RewardType.KILLED, skinUUID));
			break;

		case PvpPlayer:
			skull = getPlayerHead(skinUUID, name, amount, money);
			break;

		case Creeper:
			skull = CoreCustomItems.getDefaultCreeperHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getFriendlyName(), money, RewardType.KILLED, skinUUID));
			break;

		case EnderDragon:
			skull = CoreCustomItems.getDefaultEnderDragonHead(amount);
			skull = Reward.setDisplayNameAndHiddenLores(skull,
					new Reward(minecraftMob.getFriendlyName(), money, RewardType.KILLED, skinUUID));
			break;

		default:
			ItemStack is = new ItemStack(new CoreCustomItems().getCustomtexture(
					new Reward(minecraftMob.getFriendlyName(), money, RewardType.KILLED, skinUUID),
					minecraftMob.getTextureValue(), minecraftMob.getTextureSignature()));
			is.setAmount(amount);
			return is;
		}
		return skull;
	}

}
