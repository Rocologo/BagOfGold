package one.lindegaard.BagOfGold.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import net.milkbowl.vault.economy.Economy;
import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.Economy_BagOfGold;

public class ConvertCommand implements ICommand {

	private BagOfGold plugin;

	public ConvertCommand(BagOfGold plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "convert";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "migrate" };
	}

	@Override
	public String getPermission() {
		return "bagofgold.convert";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " [from_economy] [to_economy] "
				+ ChatColor.WHITE + " - to copy all balances from one economy plugin to another" };
	}

	@Override
	public String getDescription() {
		return plugin.getMessages().getString("bagofgold.commands.convert.description");
	}

	@Override
	public boolean canBeConsole() {
		return true;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, String label, String[] args) {

		if (sender.hasPermission("bagofgold.convert")) {
			Economy from_economy = null, to_economy = null;
			boolean found = false;
			if (args.length > 1) {
				Iterator<RegisteredServiceProvider<Economy>> itr = Bukkit.getServicesManager()
						.getRegistrations(Economy.class).iterator();
				while (itr.hasNext()) {
					from_economy = itr.next().getProvider();
					if (from_economy.getName().replaceAll(" ", "_").equalsIgnoreCase(args[0])) {
						found = true;
						break;
					}
				}
			} else {
				return false;
			}
			if (!found) {
				plugin.getMessages().senderSendMessage(sender, "from_provider NOT found");
				return true;
			}
			found = false;
			if (args.length == 2) {
				Iterator<RegisteredServiceProvider<Economy>> itr = Bukkit.getServicesManager()
						.getRegistrations(Economy.class).iterator();
				while (itr.hasNext()) {
					to_economy = itr.next().getProvider();
					if (to_economy.getName().replaceAll(" ", "_").equalsIgnoreCase(args[1])) {
						found = true;
						break;
					}
				}
			} else {
				return false;
			}
			if (!found) {
				plugin.getMessages().senderSendMessage(sender, "to_provider NOT found");
				return true;
			}

			HashMap<UUID, Double> balances = new HashMap<UUID,Double>(); 
			if (from_economy.getName().equalsIgnoreCase("Essentials Economy")) {
				BagOfGold bPlugin = (BagOfGold) Bukkit.getServer().getPluginManager().getPlugin("BagOfGold");
				Bukkit.getServer().getPluginManager().disablePlugin(bPlugin);
				for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
					plugin.getMessages().debug("balance=%s", from_economy.getBalance(offlinePlayer));
					balances.put(offlinePlayer.getUniqueId(), from_economy.getBalance(offlinePlayer));
				}
				Bukkit.getServer().getPluginManager().enablePlugin(bPlugin);
				BagOfGold.hookEconomy(Economy_BagOfGold.class, ServicePriority.Normal, "one.lindegaard.BagOfGold.BagOfGoldEconomy");
			} else {
				for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
					plugin.getMessages().debug("balance=%s", from_economy.getBalance(offlinePlayer));
					balances.put(offlinePlayer.getUniqueId(), from_economy.getBalance(offlinePlayer));
				}
			}

			for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {

				double from_balance = balances.get(offlinePlayer.getUniqueId());
				double to_balance = from_economy.getBalance(offlinePlayer);
				plugin.getMessages().debug("%s: %s=%s and %s=%s", offlinePlayer.getName(), from_economy.getName(),
						from_balance, to_economy.getName(), to_balance);
				if (from_balance > to_balance)
					to_economy.depositPlayer(offlinePlayer, from_balance - to_balance);
				else
					to_economy.withdrawPlayer(offlinePlayer, to_balance - from_balance);
				plugin.getMessages().senderSendMessage(sender,
						offlinePlayer.getName() + ": " + String.valueOf(to_balance - from_balance));
			}
			plugin.getMessages().senderSendMessage(sender, Bukkit.getOfflinePlayers().length + " accounts converted");

		} else {
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.RED + plugin.getMessages().getString("bagofgold.commands.base.nopermission", "perm",
							"bagofgold.convert", "command", "convert"));
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		ArrayList<String> items = new ArrayList<String>();
		if (args.length >= 1) {
			if (Bukkit.getServicesManager().getRegistrations(Economy.class).size() > 1) {
				for (RegisteredServiceProvider<Economy> registation : Bukkit.getServicesManager()
						.getRegistrations(Economy.class)) {
					items.add(registation.getProvider().getName().replaceAll(" ", "_"));
				}
			}
		}

		if (!args[args.length - 1].trim().isEmpty()) {
			String match = args[args.length - 1].trim().toLowerCase();

			items.removeIf(name -> !name.toLowerCase().startsWith(match));
		}
		return items;
	}

}
