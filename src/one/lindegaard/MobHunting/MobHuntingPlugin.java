package one.lindegaard.MobHunting;

import org.slf4j.Logger;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "mobhunting_plugin", name = "MobHunting Project", version = "1.0")
public class MobHuntingPlugin {

	// ************************************************************************************
	// SPONGE PROJECT
	// ************************************************************************************

	private Logger logger;

	@org.spongepowered.api.event.Listener
	public void onServerStart(GameStartedServerEvent event) {
		// Hey! The server has started!
		// Try instantiating your logger in here.
		// (There's a guide for that)
		logger.info("[MobHunting] Hello World!");
	}

	@org.spongepowered.api.event.Listener
	public void onServerStop(GameStoppedEvent event) {
		// Hey! The server has stopped!
		// Try instantiating your logger in here.
		// (There's a guide for that)
		logger.info("[MobHunting] Goodbye World!");
	}

}
