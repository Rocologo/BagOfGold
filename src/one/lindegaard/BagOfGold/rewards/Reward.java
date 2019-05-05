package one.lindegaard.BagOfGold.rewards;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.mobs.MinecraftMob;

public class Reward {

	public final static String MH_REWARD_DATA = "MH:HiddenRewardData";
	// Unique random generated UUID for "Bag of gold" rewards

	public final static String MH_REWARD_BAG_OF_GOLD_UUID = "b3f74fad-429f-4801-9e31-b8879cbae96f";
	// Unique random generated UUID for MobHead/Playerhead rewards
	public final static String MH_REWARD_KILLED_UUID = "2351844f-f400-4fa4-9642-35169c5b048a";
	// Unique random generated UUID for ITEM rewards
	public final static String MH_REWARD_ITEM_UUID = "3ffe9c3b-0445-4c35-a952-c2aaf5aeac76";
	// Unique random generated UUID for KILLER head rewards
	public final static String MH_REWARD_KILLER_UUID = "d81f1076-c91c-44c0-98c3-02a2ee88aa97";

	public final static String MH_REWARD_COINS_UUID = "cd05873a-b50e-4be2-9582-71770fab4034";
	public final static String MH_REWARD_SACK_UUID = "a3cf92ff-af45-458a-a633-f71760adee6f";
	public final static String MH_REWARD_DUST_UUID = "19e165cb-e47f-4f68-8e96-f13e28c07c08";

	private String displayname = "";;
	private double money = 0;
	private UUID uuid = null;
	private UUID uniqueId;
	private UUID skinUUID;

	public Reward() {
		this.displayname = "Skull";
		this.money = 0;
		this.uuid = UUID.randomUUID();
		this.uniqueId = UUID.randomUUID();
	}

	public Reward(Reward reward) {
		this.displayname = reward.getDisplayname();
		this.money = reward.getMoney();
		this.uuid = reward.getRewardType();
		this.skinUUID = reward.getSkinUUID();
		this.uniqueId = reward.getUniqueUUID();
	}

	public Reward(String displayName, double money, UUID uuid, UUID uniqueId, UUID skinUUID) {
		this.displayname = displayName.startsWith("Hidden:") ? displayName.substring(7) : displayName;
		this.money = money;
		this.uuid = uuid;
		this.uniqueId = uniqueId;
		this.skinUUID = skinUUID;
	}

	public Reward(List<String> lore) {
		this.displayname = lore.get(0).startsWith("Hidden:") ? lore.get(0).substring(7) : lore.get(0);
		this.money = Double.valueOf(lore.get(1).startsWith("Hidden:") ? lore.get(1).substring(7) : lore.get(1));
		this.uuid = (lore.get(2).startsWith("Hidden:")) ? UUID.fromString(lore.get(2).substring(7))
				: UUID.fromString(lore.get(2));
		if (this.money == 0)
			this.uniqueId = UUID.randomUUID();
		else
			this.uniqueId = (lore.get(3).startsWith("Hidden:")) ? UUID.fromString(lore.get(3).substring(7))
					: UUID.fromString(lore.get(3));
		if (lore.size() >= 5 && !lore.get(4).equalsIgnoreCase("Hidden:")
				&& !lore.get(4).equalsIgnoreCase("Hidden:null"))
			this.skinUUID = (lore.get(4).startsWith("Hidden:")) ? UUID.fromString(lore.get(4).substring(7))
					: UUID.fromString(lore.get(4));
		else {
			if (uuid.equals(UUID.fromString(MH_REWARD_BAG_OF_GOLD_UUID)))
				this.skinUUID = UUID.fromString(MH_REWARD_BAG_OF_GOLD_UUID);
		}
	}

	public void setReward(List<String> lore) {
		this.displayname = lore.get(0).startsWith("Hidden:") ? lore.get(0).substring(7) : lore.get(0);
		this.money = Double.valueOf(lore.get(1).startsWith("Hidden:") ? lore.get(1).substring(7) : lore.get(1));
		this.uuid = (lore.get(2).startsWith("Hidden:")) ? UUID.fromString(lore.get(2).substring(7))
				: UUID.fromString(lore.get(2));
		if (this.money == 0)
			this.uniqueId = UUID.randomUUID();
		else
			this.uniqueId = (lore.get(3).startsWith("Hidden:")) ? UUID.fromString(lore.get(3).substring(7))
					: UUID.fromString(lore.get(3));
		if (lore.size() >= 5 && !lore.get(4).equalsIgnoreCase("Hidden:"))
			this.skinUUID = (lore.get(4).startsWith("Hidden:")) ? UUID.fromString(lore.get(4).substring(7))
					: UUID.fromString(lore.get(4));
		else {
			if (uuid.equals(UUID.fromString(MH_REWARD_BAG_OF_GOLD_UUID)))
				this.skinUUID = UUID.fromString(MH_REWARD_BAG_OF_GOLD_UUID);
		}
	}

	public ArrayList<String> getHiddenLore() {
		if (uuid.equals(UUID.fromString(MH_REWARD_BAG_OF_GOLD_UUID)))
			return new ArrayList<String>(Arrays.asList("Hidden:" + displayname, // displayname
					"Hidden:" + String.format(Locale.ENGLISH, "%.5f", money), // value
					"Hidden:" + uuid.toString(), // type
					money == 0 ? "Hidden:" : "Hidden:" + uniqueId.toString(), // unique
																				// id
					"Hidden:" + (skinUUID == null ? "" : skinUUID.toString()))); // skin
		else
			return new ArrayList<String>(Arrays.asList("Hidden:" + displayname, // displayname
					"Hidden:" + String.format(Locale.ENGLISH, "%.5f", money), // value
					"Hidden:" + uuid.toString(), // type
					money == 0 ? "Hidden:" : "Hidden:" + uniqueId.toString(), // unique
																				// id
					"Hidden:" + (skinUUID == null ? "" : skinUUID.toString()),
					BagOfGold.getAPI().getMessages().getString("bagofgold.reward.name"))); // skin

	}

	/**
	 * @return the displayname
	 */
	public String getDisplayname() {
		return displayname;
	}

	/**
	 * @return the money
	 */
	public double getMoney() {
		return money;
	}

	/**
	 * @return the uuid
	 */
	public UUID getRewardType() {
		return uuid;
	}

	/**
	 * @return the Unique
	 */
	public UUID getUniqueUUID() {
		return uniqueId;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayname(String displayName) {
		this.displayname = displayName.startsWith("Hidden:") ? displayName.substring(7) : displayName;
	}

	/**
	 * @param money the money to set
	 */
	public void setMoney(double money) {
		this.money = money;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @param uniqueId the uniqueId to set
	 */
	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * Get the skin UUID for the reward
	 * 
	 * @return
	 */
	public UUID getSkinUUID() {
		return skinUUID;
	}

	/**
	 * Set the skin UUID for the reward
	 * 
	 * @param skinUUID
	 */
	public void setSkinUUID(UUID skinUUID) {
		this.skinUUID = skinUUID;
	}

	public String toString() {
		return "{Description=" + displayname + ", money=" + String.format(Locale.ENGLISH, "%.5f", money) + ", UUID="
				+ uuid + ", UniqueID=" + uniqueId + ", Skin=" + skinUUID + "}";
	}

	public void save(ConfigurationSection section) {
		section.set("description", displayname);
		section.set("money", String.format(Locale.ENGLISH, "%.5f", money));
		section.set("uuid", uuid.toString());
		section.set("uniqueid", uniqueId.toString());
		section.set("skinuuid", skinUUID == null ? "" : skinUUID.toString());
	}

	public void read(ConfigurationSection section) throws InvalidConfigurationException {
		displayname = section.getString("description");
		money = Double.valueOf(section.getString("money").replace(",", "."));
		uuid = UUID.fromString(section.getString("uuid"));
		uniqueId = UUID.fromString(section.getString("uniqueid"));
		String str = section.getString("skinuuid", "");
		if (str.equalsIgnoreCase("")) {
			if (uuid.equals(UUID.fromString(MH_REWARD_BAG_OF_GOLD_UUID)))
				this.skinUUID = UUID.fromString(MH_REWARD_BAG_OF_GOLD_UUID);
			else if (uuid.equals(UUID.fromString(MH_REWARD_KILLER_UUID))) {
				@SuppressWarnings("deprecation")
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(displayname);
				if (offlinePlayer != null)
					skinUUID = offlinePlayer.getUniqueId();
			} else if (uuid.equals(UUID.fromString(MH_REWARD_KILLED_UUID))) {
				MinecraftMob mob = MinecraftMob.getMinecraftMobType(displayname);
				if (mob != null) {
					skinUUID = mob.getPlayerUUID();
				} else
					this.skinUUID = null;
			} else
				this.skinUUID = null;
		} else
			skinUUID = UUID.fromString(section.getString("skinuuid"));
	}

	public boolean isBagOfGoldReward() {
		return uuid.toString().equalsIgnoreCase(MH_REWARD_BAG_OF_GOLD_UUID);
	}

	public boolean isKilledHeadReward() {
		return uuid.toString().equalsIgnoreCase(MH_REWARD_KILLED_UUID);
	}

	public boolean isKillerHeadReward() {
		return uuid.toString().equalsIgnoreCase(MH_REWARD_KILLER_UUID);
	}

	public boolean isItemReward() {
		return uuid.toString().equalsIgnoreCase(MH_REWARD_ITEM_UUID);
	}

	public static boolean isReward(Item item) {
		return item.hasMetadata(MH_REWARD_DATA) || isReward(item.getItemStack());
	}

	public static Reward getReward(Item item) {
		if (item.hasMetadata(MH_REWARD_DATA))
			for (MetadataValue mv : item.getMetadata(MH_REWARD_DATA)) {
				if (mv.value() instanceof Reward)
					return (Reward) item.getMetadata(MH_REWARD_DATA).get(0).value();
			}
		return getReward(item.getItemStack());
	}

	public static boolean isReward(ItemStack itemStack) {
		if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
			Iterator<String> itr = itemStack.getItemMeta().getLore().iterator();
			while (itr.hasNext()) {
				String lore = itr.next();
				if (lore.equals("Hidden:" + MH_REWARD_BAG_OF_GOLD_UUID)
						|| lore.equals("Hidden:" + MH_REWARD_KILLED_UUID)
						|| lore.equals("Hidden:" + MH_REWARD_KILLER_UUID)
						|| lore.equals("Hidden:" + MH_REWARD_ITEM_UUID)) {
					return true;
				}
			}
		}
		return false;
	}

	public static Reward getReward(ItemStack itemStack) {
		return new Reward(itemStack.getItemMeta().getLore());
	}

	public static boolean hasReward(Block block) {
		return (block.getType() == Material.PLAYER_HEAD || block.getType() == Material.PLAYER_WALL_HEAD)
				&& block.hasMetadata(MH_REWARD_DATA);
	}

	public static Reward getReward(Block block) {
		return (Reward) block.getMetadata(MH_REWARD_DATA).get(0).value();
	}

	public static boolean isReward(Entity entity) {
		return entity.hasMetadata(MH_REWARD_DATA);
	}

	public static Reward getReward(Entity entity) {
		return (Reward) entity.getMetadata(MH_REWARD_DATA).get(0).value();
	}

}
