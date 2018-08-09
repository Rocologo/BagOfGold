package one.lindegaard.BagOfGold.placeholder;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import me.clip.placeholderapi.external.EZPlaceholderHook;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.util.Misc;

public class BagOfGoldPlaceholderHook extends EZPlaceholderHook implements Listener {
	
	public BagOfGoldPlaceholderHook(Plugin plugin) {
		super(plugin, "bagofgold");
		BagOfGold.getInstance().getMessages().debug("PlaceHolderHook started");
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {

		// Remember to update the documentation when adding new placeholders
		// https://www.spigotmc.org/wiki/mobhunting-placeholders/

		// placeholder: %bagofgold_ping%
		if (identifier.equals("ping")) {
			return "pong";
		}

		// always check if the player is null for placeholders related to the
		// player!
		if (player == null) {
			return "";
		}

		// placeholder: %bagofgold_balance%
		if (identifier.equals("balance")) {
			return Misc.format(BagOfGold.getInstance().getPlayerBalanceManager().getPlayerBalance(player).getBalance());
		}

		// placeholder: %bagofgold_bank_balance%
		if (identifier.equals("bank_balance")) {
			return Misc.format(BagOfGold.getInstance().getPlayerBalanceManager().getPlayerBalance(player).getBankBalance());
		}

		// anything else someone types is invalid because we never defined
		// %customplaceholder_<what they want a value for>%
		// we can just return null so the placeholder they specified is not
		// replaced.
		return null;
	}

}
