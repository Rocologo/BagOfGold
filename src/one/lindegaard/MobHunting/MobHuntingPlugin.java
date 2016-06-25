package one.lindegaard.MobHunting;

import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import com.google.inject.Inject;

@Plugin(id = "mobhunting_plugin", name = "MobHunting Project", version = "1.0")
public class MobHuntingPlugin {

	// ************************************************************************************
	// SPONGE PROJECT
	// ************************************************************************************

	@Inject
	private Logger logger;

	@org.spongepowered.api.event.Listener
	public void onServerStart(GameStartedServerEvent event) {
		// Hey! The server has started!
		// Try instantiating your logger in here.
		// (There's a guide for that)
		//logger.info("[MobHunting] Hello World!");
		logger.info("GameStartedServerEvent");
	}

	@org.spongepowered.api.event.Listener
	public void onServerStop(GameStoppedEvent event) {
		// Hey! The server has stopped!
		// Try instantiating your logger in here.
		// (There's a guide for that)
		//logger.info("[MobHunting] Goodbye World!");
		logger.info("GameStoppedServerEvent");
	}
	
	//-----------------------------------
	
	//private AdminMessageChannel adminChannel = new AdminMessageChannel();

	@org.spongepowered.api.event.Listener
	public void onClientConnectionJoin(ClientConnectionEvent.Join event) {
		logger.info("ClientConnectionEvent");
	    Player player = event.getTargetEntity();
	    if(player.hasPermission("mobhunting.admin")) {
	        MessageChannel originalChannel = event.getOriginalChannel();
	        //MessageChannel newChannel = MessageChannel.combined(originalChannel,adminChannel);
	        player.setMessageChannel(originalChannel);
	        Text text =  Text.of("This my first text");
	        Text text2 = Text.builder("This second my text").color(TextColors.GOLD).build();
	        player.sendMessage(text);
	        player.sendMessage(text2);
	    }
	}

}
