package one.lindegaard.MobHunting.rewards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class HiddenRewardData {

	private String description = "";;
	private double money = 0;
	private UUID uuid = null;
	private UUID uniqueId;
	private final static int NUMBER_OF_DATA = 4;

	HiddenRewardData() {
		this.description = "Bag of gold";
		this.money = 0;
		this.uuid = UUID.randomUUID();
		this.uniqueId = UUID.randomUUID();
	}

	HiddenRewardData(HiddenRewardData hiddenRewardData) {
		this.description = hiddenRewardData.getDisplayname();
		this.money = hiddenRewardData.getMoney();
		this.uuid = hiddenRewardData.getUuid();
		this.uniqueId = hiddenRewardData.uniqueId;
	}

	HiddenRewardData(String displayName, double money, UUID uuid, UUID uniqueId) {
		this.description = displayName.startsWith("Hidden:") ? displayName.substring(7) : displayName;
		this.money = money;
		this.uuid = uuid;
		this.uniqueId = uniqueId;
	}

	HiddenRewardData(List<String> lore) {
		this.description = lore.get(0).startsWith("Hidden:") ? lore.get(0).substring(7) : lore.get(0);
		this.money = Double.valueOf(lore.get(1).startsWith("Hidden:") ? lore.get(1).substring(7) : lore.get(1));
		this.uuid = (lore.get(2).startsWith("Hidden:")) ? UUID.fromString(lore.get(2).substring(7))
				: UUID.fromString(lore.get(2));
		this.uniqueId = (lore.get(3).startsWith("Hidden:")) ? UUID.fromString(lore.get(3).substring(7))
				: UUID.fromString(lore.get(3));
	}

	public void setHiddenRewardData(List<String> lore) {
		this.description = lore.get(0).startsWith("Hidden:") ? lore.get(0).substring(7) : lore.get(0);
		this.money = Double.valueOf(lore.get(1).startsWith("Hidden:") ? lore.get(1).substring(7) : lore.get(1));
		this.uuid = (lore.get(2).startsWith("Hidden:")) ? UUID.fromString(lore.get(2).substring(7))
				: UUID.fromString(lore.get(2));
		this.uniqueId = (lore.get(3).startsWith("Hidden:")) ? UUID.fromString(lore.get(3).substring(7))
				: UUID.fromString(lore.get(3));
	}

	public void setHiddenRewardData(String displayName, double money, UUID uuid, UUID uniqueId) {
		this.description = (displayName.startsWith("Hidden:")) ? displayName.substring(7) : displayName;
		this.money = money;
		this.uuid = uuid;
		this.uniqueId = uniqueId;
	}

	public ArrayList<String> getHiddenLore() {
		return new ArrayList<String>(Arrays.asList("Hidden:" + description, "Hidden:" + String.valueOf(money),
				"Hidden:" + uuid.toString(), "Hidden:" + uniqueId.toString()));
	}

	/**
	 * @return the description
	 */
	public String getDisplayname() {
		return description;
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
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * @return the Unique
	 */
	public UUID getUniqueId() {
		return uniqueId;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String displayName) {
		this.description = displayName.startsWith("Hidden:") ? displayName.substring(7) : displayName;
	}

	/**
	 * @param money
	 *            the money to set
	 */
	public void setMoney(double money) {
		this.money = money;
	}

	/**
	 * @param uuid
	 *            the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * @param uniqueId
	 *            the uniqueId to set
	 */
	public void setUniqueId(UUID uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String toString() {
		return "{Description=" + description + ", money=" + money + ", UUID=" + uuid.toString() + "}";
	}

	public void save(ConfigurationSection section) {
		section.set("description", description);
		section.set("money", money);
		section.set("uuid", uuid.toString());
		section.set("uniqueid", uniqueId.toString());
	}

	public void read(ConfigurationSection section) throws InvalidConfigurationException {
		description = section.getString("description");
		money = section.getDouble("money");
		uuid = UUID.fromString(section.getString("uuid"));
		uniqueId = UUID.fromString(section.getString("uniqueid"));
	}

	public static boolean hasHiddenRewardData(Item item) {
		return hasHiddenRewardData(item.getItemStack());
	}

	public static boolean hasHiddenRewardData(ItemStack itemStack) {
		return itemStack!=null && itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()
				&& itemStack.getItemMeta().getLore().size() == NUMBER_OF_DATA
				&& (itemStack.getItemMeta().getLore().get(2).equals("Hidden:" + RewardManager.MH_REWARD_BAG_OF_GOLD_UUID)
						|| itemStack.getItemMeta().getLore().get(2).equals("Hidden:" + RewardManager.MH_REWARD_HEAD_UUID));
	}
	
	public static HiddenRewardData getHiddenRewardData(ItemStack itemStack) {
		return new HiddenRewardData(itemStack.getItemMeta().getLore());
	}
	
	public static boolean hasHiddenRewardData(Block block) {
		return block.getType() == Material.SKULL && block.hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA);
	}

	public static HiddenRewardData getHiddenRewardData(Block block){ 
		return (HiddenRewardData) block.getMetadata(RewardManager.MH_HIDDEN_REWARD_DATA).get(0).value();
	}
	
	public static boolean hasHiddenRewardData(Entity entity) {
		return entity.hasMetadata(RewardManager.MH_HIDDEN_REWARD_DATA);
	}
	public static HiddenRewardData getHiddenRewardData(Entity entity){
		return  (HiddenRewardData) entity.getMetadata(RewardManager.MH_HIDDEN_REWARD_DATA).get(0).value();
	}
			

}
