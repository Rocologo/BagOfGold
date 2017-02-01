package one.lindegaard.MobHunting;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

public class Fishing implements Listener {

	public Fishing() {
		
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void Fish(PlayerFishEvent e) {
		if (e.getCaught().equals(State.CAUGHT_FISH)) {
			Player p = e.getPlayer();
			Entity fish = e.getCaught();
			p.sendMessage(ChatColor.DARK_AQUA + "You caught a " + fish.getName());
		}
	}
}
