package one.lindegaard.MobHunting.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.md_5.bungee.api.chat.ComponentBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import de.Ste3et_C0st.AdvancementMSG.main.AdvancementAPI;
import de.Ste3et_C0st.AdvancementMSG.main.AdvancementAPIMain;

public class MobHuntingAdvancement implements ICommand {

	@SuppressWarnings({ "deprecation", "unused" })
	@Override
	public boolean onCommand(final CommandSender sender, String label, String[] args) {
		// ad say <material> <player> <msg>
		// ad test <player> namespacedkey
		//if (label.equalsIgnoreCase("ad")) {
			if (args.length > 1) {
				if (args[0].equalsIgnoreCase("say")) {
					if (args.length >= 4) {
						MaterialData material = null;
						try {
							if (args[1].contains(":")) {
								Integer i = Integer.parseInt(args[1].split(":")[0]);
								material = new MaterialData(Material.getMaterial(i),
										(byte) Integer.parseInt(args[1].split(":")[1]));
							} else {
								material = new MaterialData(Material.getMaterial(Integer.parseInt(args[1])), (byte) 0);
							}
						} catch (NumberFormatException ex) {
							String materialName = "";
							byte subID = 0;
							if (args[1].contains(":")) {
								materialName = args[1].split(":")[0].toUpperCase();
								subID = (byte) Integer.parseInt(args[1].split(":")[1]);
							}
							material = new MaterialData(Material.getMaterial(materialName), subID);
						}

						if (material == null) {
							material = new MaterialData(Material.DIAMOND);
						}

						String title = "";
						for (String str : Arrays.copyOfRange(args, 3, args.length)) {
							title += str + " ";
						}

						title = title.substring(0, title.length() - 1);
						title = ChatColor.translateAlternateColorCodes('&', title);

						if (args[2].equalsIgnoreCase("global")) {
							Player[] player = new Player[Bukkit.getOnlinePlayers().size()];
							player = Bukkit.getOnlinePlayers().toArray(player);
							AdvancementAPIMain.send(title, "505 Title not found", material, player);
						} else {
							Player player = Bukkit.getPlayer(args[2]);
							if (player == null || !player.isOnline()) {
								sender.sendMessage("the player " + args[2] + " is not online");
								return true;
							} else {
								AdvancementAPIMain.send(title, "505 Title not found", material, player);
							}
						}
					}
				} else if (args[0].equalsIgnoreCase("next")) {
					if (args.length == 3) {
						if (!args[1].contains(":")) {
							sender.sendMessage("NameSpacedKey is missing: (plugin:key1/key2)");
							return true;
						}
						if (!args[1].contains("/")) {
							sender.sendMessage("NameSpacedKey is missing: (plugin:key1/key2)");
							return true;
						}
						Player player = Bukkit.getPlayer(args[2]);
						if (player == null || !player.isOnline()) {
							sender.sendMessage("the player " + args[2] + " is not online");
							return true;
						} else {
							String[] split = args[1].split(":");
							new AdvancementAPI(new NamespacedKey(split[0], split[1])).next(player);
						}
					} else if (args.length == 5) {
						Long timeDif = null;
						Boolean b = false;
						try {
							timeDif = Long.parseLong(args[3]);
						} catch (Exception ex) {
							sendHelp(sender);
							return true;
						}

						try {
							b = Boolean.parseBoolean(args[4]);
						} catch (Exception ex) {
							sendHelp(sender);
							return true;
						}

						Player player = Bukkit.getPlayer(args[2]);
						if (player == null || !player.isOnline()) {
							sender.sendMessage("the player " + args[2] + " is not online");
							return true;
						} else {
							if (!args[1].contains(":")) {
								sender.sendMessage("NameSpacedKey is missing: (plugin:key1/key2)");
								return true;
							}
							if (!args[1].contains("/")) {
								sender.sendMessage("NameSpacedKey is missing: (plugin:key1/key2)");
								return true;
							}
							String[] split = args[1].split(":");
							new AdvancementAPI(new NamespacedKey(split[0], split[1])).next(player, timeDif, b);
						}
					} else {
						sendHelp(sender);
					}
				} else if (args[0].equalsIgnoreCase("help")) {
					sendHelp(sender);
				}
			} else {
				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("list")) {
						Iterator<Advancement> adIterator = Bukkit.advancementIterator();
						while (adIterator.hasNext()) {
							Advancement ad = adIterator.next();
							if (ad.getKey().getNamespace().toLowerCase().startsWith("dice")) {
								if (ad.getCriteria().size() > 1) {
									sender.sendMessage("§7-§6 " + ad.getKey().toString());
								}
							}
						}
						return true;
					}
				}
				sendHelp(sender);
			}
			//return true;
		//}
		return false;
	}

	public void sendHelp(CommandSender sender) {
		sender.sendMessage("§7§m+--------------------------------------------------+");
		sender.spigot().sendMessage(new ComponentBuilder("§2/ad §7help").create());
		sender.spigot().sendMessage(new ComponentBuilder("§2/ad §7list").create());
		sender.spigot()
				.sendMessage(new ComponentBuilder(
						"§2/ad §7say <§cmaterialName§7/§cid:§csubid§7> <§cplayer§7/§cglobal§7> <§cargs...§7>")
								.create());
		sender.spigot().sendMessage(new ComponentBuilder("§2/ad §7next <§cnamespacedkey§7> <§cplayer§7>").create());
		sender.spigot().sendMessage(
				new ComponentBuilder("§2/ad §7next <§cnamespacedkey§7> <§cplayer§7> <§ctimeMilli§7> <§closebefore§7>")
						.create());
		sender.sendMessage("§7§m+--------------------------------------------------+");
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "advancement";
	}

	@Override
	public String[] getAliases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPermission() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		if (sender instanceof ConsoleCommandSender)
			return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " <player>" };
		else {
			if (sender.hasPermission("mobhunting.listachievements.other"))
				return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " [<player>] [nogui|gui]" };
			else
				return new String[] { ChatColor.GOLD + label + ChatColor.GREEN + " [nogui|gui]" };
		}
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Send advancements";
	}

	@Override
	public boolean canBeConsole() {
		return false;
	}

	@Override
	public boolean canBeCommandBlock() {
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

}
