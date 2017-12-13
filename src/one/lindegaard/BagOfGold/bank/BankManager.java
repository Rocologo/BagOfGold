package one.lindegaard.BagOfGold.bank;

import org.bukkit.entity.Entity;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.compatibility.CitizensCompat;

public class BankManager {

	BagOfGold plugin;
	
	public BankManager(BagOfGold plaugin) {
		this.plugin=plugin;
	}
	
	public boolean isBagOfGoldBanker(Entity entity) {
		if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
			NPC npc = CitizensCompat.getCitizensPlugin().getNPCRegistry().getNPC(entity);
			return (npc.hasTrait(BagOfGoldBankerTrait.class));
		} else
			return false;
	}

}
