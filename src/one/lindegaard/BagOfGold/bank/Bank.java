package one.lindegaard.BagOfGold.bank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import one.lindegaard.BagOfGold.BagOfGold;

public class Bank {

	private BagOfGold plugin;
	private static final String VAULTDATA = "BAG:HiddenVaultData";

	private String bankId;
	private int banknumber;
	private String bankname;
	private OfflinePlayer owner;
	private String regionId;
	private double balance;
	private HashMap<String, Vault> vaults = new HashMap<>(); // unique vaultId , vaults

	
	public Bank() {
		this.plugin = BagOfGold.getInstance();
		this.bankId = UUID.randomUUID().toString();
		this.bankname = BagOfGold.getInstance().getConfigManager().bankname;
		this.balance = 0;
	}
	
	public Bank(Bank bank) {
		this.bankId=bank.bankId;
		this.banknumber=bank.banknumber;
		this.bankname=bank.bankname;
		this.owner=bank.owner;
		this.regionId=bank.regionId;
		this.balance=bank.balance;
		this.vaults=bank.vaults;
	}
	
	public Bank(BagOfGold plugin) {
		this.plugin = plugin;
		this.bankId = UUID.randomUUID().toString();
		this.bankname = BagOfGold.getInstance().getConfigManager().bankname;
		this.balance = 0;
	}

	public int getFreeVaultNumber() {
		int max = 0;
		ArrayList<Integer> a = new ArrayList<>();
		for (Entry<String, Vault> uuid : vaults.entrySet()) {
			Vault vault = uuid.getValue();
			a.add(vault.getNumber());
			max = Math.max(max, vault.getNumber());
		}

		for (int n = 1; n < a.size(); n++) {
			if (!a.contains(n))
				return n;
		}
		return max + 1;
	}

	public Vault getVault(String uuid) {
		return vaults.get(uuid);
	}

	public HashMap<String, Vault> getVaults() {
		return vaults;
	}

	/**
	 * @param vaults the vaults to set
	 */
	public void setVaults(HashMap<String, Vault> vaults) {
		this.vaults = vaults;
	}

	public static boolean isVault(Block block) {
		return block.hasMetadata(VAULTDATA);
	}

	public void addVault(Vault vault) {
		int n = getFreeVaultNumber();
		vault.setNumber(n);
		vault.getLocation().getBlock().setMetadata(VAULTDATA, new FixedMetadataValue(plugin, vault));
		vaults.put(vault.getUniqueId(), new Vault(vault));
	}

	public static Vault getVault(Block block) {
		if (block.hasMetadata(VAULTDATA))
			for (MetadataValue mv : block.getMetadata(VAULTDATA)) {
				if (mv.value() instanceof Vault)
					return (Vault) block.getMetadata(VAULTDATA).get(0).value();
			}
		return null;
	}

	/**
	 * Remove the vault block from the world and clean up in saved vaults.
	 * 
	 * @param block
	 */
	public void removeVault(Block block) {
		if (isVault(block)) {
			Vault vault = getVault(block);
			plugin.getMessages().debug("Removing vaultData from block (id=%s)", vault.getUniqueId());
			block.removeMetadata(VAULTDATA, plugin);
			if (vaults.containsKey(vault.getUniqueId())) {
				vaults.remove(vault.getUniqueId());
			}
		}
	}

	public void save(ConfigurationSection section) {
		section.set("uniqueid", getBankId());
		section.set("banknumber", getBanknumber());
		section.set("displayname", getDisplayName());
		section.set("owner", getOwner()!=null?getOwner().getUniqueId().toString():null);
		section.set("regionid", getRegionId());
		section.set("balance", getBalance());
		ConfigurationSection vaultSection = section.createSection("vaults");
		Iterator<Entry<String, Vault>> itr = vaults.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<String, Vault> id = itr.next();
			Vault vault = id.getValue();
			vault.save(vaultSection);
		}
	}

	public void read(ConfigurationSection section) {
		setBankId(section.getString("uniqueid"));
		setBanknumber(section.getInt("banknumber"));
		setDisplayName(section.getString("displayname"));
		setOwner(section.getString("owner")!=null?Bukkit.getOfflinePlayer(UUID.fromString(section.getString("owner"))):null);
		setRegionId(section.getString("regionid"));
		setBalance(section.getDouble("balance"));
		
		ConfigurationSection sectionVaults = section.getConfigurationSection("vaults");
		
		for (String key : sectionVaults.getKeys(false)) {
			ConfigurationSection vaultsec = sectionVaults.getConfigurationSection(key);
			Vault vault = new Vault();
			vault.read(vaultsec);
			vault.getLocation().getBlock().setMetadata(VAULTDATA, new FixedMetadataValue(plugin, vault));
			vaults.put(key, vault);
			
		}
	}

	/**
	 * @return the uniqueId
	 */
	public String getBankId() {
		return bankId;
	}

	/**
	 * @param bankId the uniqueId to set
	 */
	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public int getBanknumber() {
		return banknumber;
	}

	public void setBanknumber(int banknumber) {
		this.banknumber = banknumber;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return bankname;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.bankname = displayName;
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

	public String getRegionId() {
		return regionId;
	}

	public void setRegionId(String regionId) {
		this.regionId = regionId;
	}

	/**
	 * @return the value
	 */
	public Double getBalance() {
		return balance;
	}

	/**
	 * @param value the value to set
	 */
	public void setBalance(Double value) {
		this.balance = value;
	}
}
