package one.lindegaard.BagOfGold.bank;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

public class Vault {

	private String uniqueId;
	private int vaultnumber;
	private Location location;
	private String displayName;
	private VaultType vaultType;
	private OfflinePlayer owner;
	private Double value;
	private String skin;

	public Vault() {	
	}
	
	public Vault(Vault vault) {
		this.uniqueId = vault.getUniqueId();
		this.vaultnumber=vault.getNumber();
		this.location = vault.getLocation();
		this.displayName = vault.getDisplayName();
		this.vaultType = vault.getVaultType();
		this.owner = vault.getOwner();
		this.value = vault.getValue();
		this.skin = vault.getSkin();
	}

	public Vault(Block block) {
		this.uniqueId = UUID.randomUUID().toString();
		this.vaultnumber=0;
		this.vaultType = VaultType.fromBlock(block);
		this.location = block.getLocation();
		this.value = (double) 0;
		this.skin = vaultType.getUUID();
	}

	public Vault(VaultType vaultType, Location location, int number) {
		this.uniqueId = UUID.randomUUID().toString();
		this.vaultnumber=number;
		this.vaultType = vaultType;
		this.location = location;
		this.value = (double) 0;
		this.skin = vaultType.getUUID();
	}

	public void save(ConfigurationSection section) {
		ConfigurationSection vaultSection = section.createSection(uniqueId);
		vaultSection.set("vaultnumber", vaultnumber);
		vaultSection.set("location", location.clone());
		vaultSection.set("displayname", displayName);
		vaultSection.set("vaulttype", vaultType.getMaterial().toString());
		vaultSection.set("owner", owner!=null?owner.getUniqueId().toString():null);
		vaultSection.set("value", value);
		vaultSection.set("skin", skin.toString());
	}

	public void read(ConfigurationSection section){
		uniqueId = section.getName();
		vaultnumber= section.getInt("vaultnumber");
		location = (Location) section.get("location");
		displayName = section.getString("displayname");
		vaultType = VaultType.fromMaterial(Material.valueOf(section.getString("vaulttype")));
		owner = section.getString("owner")!=null?Bukkit.getOfflinePlayer(UUID.fromString(section.getString("owner"))):null;
		value = section.getDouble("value");
		skin = section.getString("skin");
	}
	
	public boolean isChunkGenerated() {
		return location != null
				&& location.getWorld().isChunkGenerated(location.getChunk().getX(), location.getChunk().getZ());
	}

	public String toString() {
		return "{Vault: {VaultType=" + vaultType + ", Location=" + location.toString() + ", Value=" + value + "}}";
	}

	/**
	 * @return the uniqueId
	 */
	public String getUniqueId() {
		return uniqueId;
	}

	/**
	 * @param uniqueId the uniqueId to set
	 */
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return vaultnumber;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.vaultnumber = number;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the vaultType
	 */
	public VaultType getVaultType() {
		return vaultType;
	}

	/**
	 * @param vaultType the vaultType to set
	 */
	public void setVaultType(VaultType vaultType) {
		this.vaultType = vaultType;
	}

	/**
	 * @return the owner
	 */
	public OfflinePlayer getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(OfflinePlayer owner) {
		this.owner = owner;
	}

	/**
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Double value) {
		this.value = value;
	}

	/**
	 * @return the skin
	 */
	public String getSkin() {
		return skin;
	}

	/**
	 * @param skin the skin to set
	 */
	public void setSkin(String skin) {
		this.skin = skin;
	}
	
}
