package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

import net.md_5.bungee.api.chat.TextComponent;

public class BossBarAPICompatHelper {

	public static void addBar(Player player, String text) {
		if (BossBarAPICompat.isSupported())
			try {
				Class.forName("org.inventivetalent.bossbar.BossBarAPI");
				// Create a new BossBar
				BossBarAPI.addBar(player, // The receiver of the BossBar
						new TextComponent(text), // Displayed message
						BossBarAPI.Color.BLUE, // Color of the bar
						BossBarAPI.Style.NOTCHED_20, // Bar style
						1.0f, // Progress (0.0 - 1.0)
						100, // Timeout in ticks
						2); // Timeout-interval
			} catch (ClassNotFoundException
					// | NoSuchMethodException
					| SecurityException e) {
				Bukkit.getLogger()
						.warning("[MobHunting] Your version of BossBarAPI is not compatible with MobHunting.");
				player.sendMessage(text);
				BossBarAPICompat.setSupported(false);
			}

	}
}
