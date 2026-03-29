package one.lindegaard.BagOfGold.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.CustomItemsLib.Core;
import one.lindegaard.CustomItemsLib.rewards.Reward;

public class TokenCommand implements ICommand {

	private final BagOfGold plugin;

	public TokenCommand(BagOfGold plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getName() {
		return "token";
	}

	@Override
	public String[] getAliases() {
		return new String[] { "tokens" };
	}

	@Override
	public String getPermission() {
		return "bagofgold.token";
	}

	@Override
	public String[] getUsageString(String label, CommandSender sender) {
		return new String[] {
				ChatColor.GOLD + label + ChatColor.GREEN + " audit [player]",
				ChatColor.GOLD + label + ChatColor.GREEN + " migrate [player]",
				ChatColor.GOLD + label + ChatColor.GREEN + " verify",
				ChatColor.GOLD + label + ChatColor.GREEN + " revoke <token-uuid>" };
	}

	@Override
	public String getDescription() {
		return "Token security tools (audit/migrate/verify/revoke).";
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
		if (args.length == 0) {
			return false;
		}

		String sub = args[0].toLowerCase();
		switch (sub) {
		case "audit":
			return onAudit(sender, args);
		case "migrate":
			return onMigrate(sender, args);
		case "verify":
			return onVerify(sender);
		case "revoke":
			return onRevoke(sender, args);
		default:
			return false;
		}
	}

	private boolean onAudit(CommandSender sender, String[] args) {
		Player target = resolveTarget(sender, args, 1);
		if (target == null) {
			return true;
		}

		TokenStats inv = scanInventory(target.getInventory());
		TokenStats ender = scanInventory(target.getEnderChest());

		plugin.getMessages().senderSendMessage(sender, ChatColor.YELLOW + "[BagOfGold] Token audit for "
				+ target.getName() + ":");
		plugin.getMessages().senderSendMessage(sender, ChatColor.GRAY + " Inventory -> v2: " + ChatColor.GREEN
				+ inv.v2 + ChatColor.GRAY + ", legacy: " + ChatColor.GOLD + inv.legacy + ChatColor.GRAY
				+ ", suspicious: " + ChatColor.RED + inv.suspicious);
		plugin.getMessages().senderSendMessage(sender, ChatColor.GRAY + " EnderChest -> v2: " + ChatColor.GREEN
				+ ender.v2 + ChatColor.GRAY + ", legacy: " + ChatColor.GOLD + ender.legacy + ChatColor.GRAY
				+ ", suspicious: " + ChatColor.RED + ender.suspicious);
		return true;
	}

	private boolean onMigrate(CommandSender sender, String[] args) {
		Player target = resolveTarget(sender, args, 1);
		if (target == null) {
			return true;
		}

		int migrated = migrateInventory(target.getInventory()) + migrateInventory(target.getEnderChest());
		plugin.getMessages().senderSendMessage(sender,
				ChatColor.GREEN + "[BagOfGold] Migrated " + migrated + " legacy tokens to v2 for "
						+ target.getName() + ".");
		return true;
	}

	private boolean onVerify(CommandSender sender) {
		if (!(sender instanceof Player)) {
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.RED + "Use /bag token verify as a player holding an item in hand.");
			return true;
		}

		Player player = (Player) sender;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null || item.getType().isAir()) {
			plugin.getMessages().senderSendMessage(sender, ChatColor.RED + "Hold a token item in your main hand.");
			return true;
		}

		if (Reward.isReward(item)) {
			Reward reward = Reward.getReward(item);
			boolean valid = reward != null && reward.checkHash();
			plugin.getMessages().senderSendMessage(sender,
					(valid ? ChatColor.GREEN : ChatColor.RED) + "[BagOfGold] v2 token "
							+ (valid ? "VALID" : "INVALID") + ChatColor.GRAY + " type="
							+ (reward == null ? "?" : reward.getRewardType().getType()) + " value="
							+ (reward == null ? "0" : reward.getMoney()) + " token="
							+ (reward == null ? "" : reward.getTokenUUID()));
			return true;
		}

		if (Reward.isLegacyReward(item)) {
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.GOLD + "[BagOfGold] Legacy token detected. Use /bag token migrate.");
			return true;
		}

		plugin.getMessages().senderSendMessage(sender, ChatColor.RED + "Item in hand is not a BagOfGold token.");
		return true;
	}

	private boolean onRevoke(CommandSender sender, String[] args) {
		if (args.length < 2) {
			return false;
		}
		if (Core.getTokenSpendStore() == null) {
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.RED + "Token spend store is not available in CustomItemsLib.");
			return true;
		}

		String tokenUuid = args[1].trim();
		boolean ok = Core.getTokenSpendStore().revokeToken(tokenUuid, "bagofgold-command-revoke");
		if (ok) {
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.GREEN + "[BagOfGold] Token revoked: " + tokenUuid);
		} else {
			plugin.getMessages().senderSendMessage(sender,
					ChatColor.RED + "[BagOfGold] Could not revoke token (already revoked or invalid): " + tokenUuid);
		}
		return true;
	}

	private int migrateInventory(Inventory inventory) {
		int migrated = 0;
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack is = inventory.getItem(slot);
			if (is == null || is.getType().isAir()) {
				continue;
			}
			if (Reward.isLegacyReward(is)) {
				ItemStack converted = Reward.migrateLegacyReward(is.clone());
				inventory.setItem(slot, converted);
				migrated++;
			}
		}
		return migrated;
	}

	private TokenStats scanInventory(Inventory inventory) {
		TokenStats stats = new TokenStats();
		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack is = inventory.getItem(slot);
			if (is == null || is.getType().isAir()) {
				continue;
			}
			if (Reward.isReward(is)) {
				stats.v2++;
				continue;
			}
			if (Reward.isLegacyReward(is)) {
				stats.legacy++;
				continue;
			}
			if (is.hasItemMeta() && is.getItemMeta().hasDisplayName()
					&& is.getItemMeta().getDisplayName().contains(Core.getConfigManager().bagOfGoldName)) {
				stats.suspicious++;
			}
		}
		return stats;
	}

	private Player resolveTarget(CommandSender sender, String[] args, int nameArgIndex) {
		if (args.length > nameArgIndex) {
			Player target = Bukkit.getPlayerExact(args[nameArgIndex]);
			if (target == null) {
				plugin.getMessages().senderSendMessage(sender,
						ChatColor.RED + "Player must be online for token audit/migration.");
			}
			return target;
		}
		if (sender instanceof Player) {
			return (Player) sender;
		}
		plugin.getMessages().senderSendMessage(sender, ChatColor.RED + "Specify an online player.");
		return null;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String label, String[] args) {
		ArrayList<String> items = new ArrayList<String>();
		if (args.length == 1) {
			items.addAll(Arrays.asList("audit", "migrate", "verify", "revoke"));
		} else if (args.length == 2 && (args[0].equalsIgnoreCase("audit") || args[0].equalsIgnoreCase("migrate"))) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				items.add(player.getName());
			}
		}
		if (!args[args.length - 1].trim().isEmpty()) {
			String match = args[args.length - 1].trim().toLowerCase();
			items.removeIf(name -> !name.toLowerCase().startsWith(match));
		}
		return items;
	}

	private static class TokenStats {
		int v2;
		int legacy;
		int suspicious;
	}
}
