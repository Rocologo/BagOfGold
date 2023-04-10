package one.lindegaard.BagOfGold.bank;

import org.bukkit.Material;
import org.bukkit.block.Block;

public enum VaultType {

	SAFE(0, "Safe", "3259c5a7-7cf0-4bde-a518-4a01ef640699", Material.PLAYER_HEAD), // PLAYER_HEAD - Custom skin (uuid)
	BANKVAULT(1, "Bankvault", "a63864ea-193a-4b04-b022-a5734f769590", Material.CHEST), // CHEST
	PIRATE_TREASURE(2, "Pirate treasure", "96257689-d4f1-4505-a777-7cd6658f18e2", Material.ENDER_CHEST), // ENDER CHEST
	BARREL(3, "Barrel", "d81f1076-c91c-44c0-98c3-02a2ee88aa97", Material.BARREL); // Barrel

	private int n;
	private String displayname;
	private String uuid;
	private Material material;

	private VaultType(int n, String name, String uuid, Material material) {
		this.n = n;
		this.displayname = name;
		this.uuid = uuid;
		this.material = material;
	}

	/**
	 * Vault Id
	 * 
	 * @return
	 */
	public int getInt() {
		return n;
	}

	/**
	 * Return the Vault Type
	 * 
	 * @return
	 */
	public String getType() {
		return displayname;
	}

	/**
	 * Vault Id
	 * 
	 * This is used for the skin uuid when vault id 0.
	 * 
	 * @return
	 */
	public String getUUID() {
		return uuid;
	}

	/**
	 * get the Material used for this VaultType
	 * 
	 * @return
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Return the VautlType from the DisplayName
	 * 
	 * @param displayName
	 * @return the VaultType or null if the displayName does not match any known
	 *         VaulktType
	 */
	public static VaultType fromString(String displayName) {
		for (VaultType type : values()) {
			if (type.getType().equalsIgnoreCase(displayName))
				return type;
		}
		return null;
	}

	/**
	 * Return the VaultType from the vaultId
	 * @param id
	 * @return
	 */
	public static VaultType fromIntXXX(int id) {
		
		for (VaultType type : values()) {
			if (type.getInt() == id)
				return type;
		}
		return null;
	}
	
	public static VaultType fromBlock(Block block) {
		for (VaultType type : values()) {
			if (type.getMaterial()==block.getType())
				return type;
		}
		return null;
	}
	
	public static VaultType fromMaterial(Material material) {
		for (VaultType type : values()) {
			if (type.getMaterial()==material)
				return type;
		}
		return null;
	}
	
	public static boolean isValidMaterial(Block block) {
		for (VaultType type: values()) {
			if (type.getMaterial()==block.getType())
				return true;
		}
		return false;
	}

}
