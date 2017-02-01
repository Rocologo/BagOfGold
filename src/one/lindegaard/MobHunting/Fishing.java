package one.lindegaard.MobHunting;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.inventory.ItemStack;

public class Fishing implements Listener {

	public Fishing() {

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Fish(PlayerFishEvent e) {
		if (e.isCancelled())
			return;
		State state = e.getState();
		Messages.debug("Fishing state is: %s", state);

		switch (state) {
		case CAUGHT_FISH:
			Player p = e.getPlayer();
			Entity entity = e.getCaught();
			Item fish = (Item) entity;
			ItemStack is = fish.getItemStack();
			//Messages.debug("%s caught a %s ", p.getName(), entity.getType());
			//Messages.debug("%s caught a %s ", p.getName(), fish.getName());
			Messages.debug("%s caught a %s ", p.getName(), is.getType());
			
			break;
		case BITE:
			break;
		case CAUGHT_ENTITY:
			break;
		case FAILED_ATTEMPT:
			break;
		case FISHING:
			break;
		case IN_GROUND:
			break;
		default:
			break;

		}
	}
}
