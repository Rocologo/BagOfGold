package au.com.mineauz.MobHunting.npc;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import au.com.mineauz.MobHunting.MobHunting;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class MobHuntingTrait extends Trait implements Listener {
	// http://wiki.citizensnpcs.co/API

	// This is your trait that will be applied to a npc using the /trait
	// mytraitname command. Each NPC gets its own instance of this class.
	// the Trait class has a reference to the attached NPC class through the
	// protected field 'npc' or getNPC().
	// The Trait class also implements Listener so you can add EventHandlers
	// directly to your trait.

	MobHunting plugin = null;

	public MobHuntingTrait() {
		super("MasterMobHunter");
		plugin = MobHunting.instance;
	}

	// see the 'Persistence API' section
	@Persist("mysettingname")
	boolean automaticallyPersistedSetting = false;

	// Here you should load up any values you have previously saved (optional).
	// This does NOT get called when applying the trait for the first time, only
	// loading onto an existing npc at server start.
	// This is called AFTER onAttach so you can load defaults in onAttach and
	// they will be overridden here.
	// This is called BEFORE onSpawn, npc.getBukkitEntity() will return null.
	public void load(DataKey key) {
		MobHunting.debug("MobHuntingTrait.load(DataKey)");
		// npcData1 = (MasterMobHunterData) key.getRaw("npcData");
	}

	// Save settings for this NPC (optional). These values will be persisted to
	// the Citizens saves.yml file
	public void save(DataKey key) {
		MobHunting.debug("MobHuntingTrait.save(DataKey)");
		// key.setRaw("npcData", npcData1);
	}

	// An example event handler. All traits will be registered automatically as
	// Bukkit Listeners.
	@EventHandler
	public void click(net.citizensnpcs.api.event.NPCClickEvent event) {
		// Handle a click on a NPC. The event has a getNPC() method.
		// Be sure to check event.getNPC() == this.getNPC() so you only handle
		// clicks on this NPC!

	}

	// Called every tick
	@Override
	public void run() {
		// MobHunting.debug("MobHuntingTrait is running each tick");
	}

	// Run code when your trait is attached to a NPC.
	// This is called BEFORE onSpawn, so npc.getBukkitEntity() will return null
	// This would be a good place to load configurable defaults for new NPCs.
	@Override
	public void onAttach() {
		plugin.getServer().getLogger()
				.info(npc.getName() + " has been assigned MasterMobHunter");
		MobHunting.debug("MobHuntingTrait.load(MemoryDataKey)");
		load(new net.citizensnpcs.api.util.MemoryDataKey());
	}

	// Run code when the NPC is despawned. This is called before the entity
	// actually despawns so npc.getBukkitEntity() is still valid.
	@Override
	public void onDespawn() {
		MobHunting.debug("MobHuntingTrait - NPC %s despawned",this.getNPC().getId());
	}

	// Run code when the NPC is spawned. Note that npc.getBukkitEntity() will be
	// null until this method is called.
	// This is called AFTER onAttach and AFTER Load when the server is started.
	@Override
	public void onSpawn() {
		MobHunting.debug("MobHuntingTrait - NPC %s spawned",this.getNPC().getId());
	}

	// run code when the NPC is removed. Use this to tear down any repeating
	// tasks.
	@Override
	public void onRemove() {
		MobHunting.debug("MobHuntingTrait - NPC %s removed",this.getNPC().getId());
	}

}
