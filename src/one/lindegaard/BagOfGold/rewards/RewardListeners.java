package one.lindegaard.BagOfGold.rewards;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.PlayerBalance;
import one.lindegaard.BagOfGold.compatibility.PerWorldInventoryCompat;
import one.lindegaard.Core.Core;
import one.lindegaard.Core.rewards.Reward;

public class RewardListeners implements Listener {

	private BagOfGold plugin;

	public RewardListeners(BagOfGold plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryCloseEvent(InventoryCloseEvent event) {

		Player player = (Player) event.getPlayer();
		PlayerBalance ps = plugin.getPlayerBalanceManager().getPlayerBalance(player);
		if (player.isOnline() && player.isValid()) {
			if (player.getGameMode() == GameMode.SURVIVAL) {
				plugin.getMessages().debug("Adjusting Player Balance to Amount of BagOfGold in Inventory: %s",
						ps.toString());
				plugin.getRewardManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
			} else if (player.getGameMode() == GameMode.SPECTATOR) {
				plugin.getMessages().debug("Player is in spectator mode. BagOfGold is not changed in Inventory: %s",
						ps.toString());
			} else {
				plugin.getMessages().debug("Adjusting Amount of BagOfGold in Inventory to Balance: %s", ps.toString());
				plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onGameModeChange(PlayerGameModeChangeEvent event) {

		if (event.isCancelled() || PerWorldInventoryCompat.isSupported())
			return;

		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {

				Player player = event.getPlayer();
				if (player.getGameMode() == GameMode.SURVIVAL) {
					plugin.getMessages().debug(
							"PlayerGameModeChange %s adjusting Player Balance to Amount of BagOfGold in Inventory",
							player.getName());
					plugin.getRewardManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
				} else if (player.getGameMode() == GameMode.SPECTATOR) {
					plugin.getMessages().debug(
							"PlayerGameModeChange %s is in Spectator mode. BagOfGold is not changed.",
							player.getName());
				} else {
					plugin.getMessages().debug(
							"PlayerGameModeChange %s adjusting Amount of BagOfGold in Inventory To Balance",
							player.getName());
					plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
				}
			}
		}, 3);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChange(PlayerChangedWorldEvent event) {

		if (PerWorldInventoryCompat.isSupported())
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.SURVIVAL) {
			plugin.getMessages().debug("Adjusting %s's balance to amount of BagOfGold in Inventory", player.getName());
			plugin.getRewardManager().adjustPlayerBalanceToAmounOfMoneyInInventory(player);
		} else if (player.getGameMode() == GameMode.SPECTATOR) {
			plugin.getMessages().debug("%s is in spectator mode. BagOfGold is not changed.", player.getName());
		} else {
			plugin.getMessages().debug("Adjusting %s's amount of BagOfGold in Inventory to balance", player.getName());
			plugin.getRewardManager().adjustAmountOfMoneyInInventoryToPlayerBalance(player);
		}
		plugin.getMessages().debug("Adjusting %s's balance from %s to %s, new balance is %s", player.getName(),
				event.getFrom(), event.getPlayer().getWorld(), plugin.getRewardManager().getBalance(player));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPhysicsEvent(BlockPhysicsEvent event) {

		if (event.isCancelled())
			return;

		if (event.getChangedType() != Material.matchMaterial("PLAYER_HEAD"))
			return;

		Block block = event.getBlock();

		if (Reward.isReward(block)) {
			Reward reward = Reward.getReward(block);

			// plugin.getMessages().debug("RewardListernes: Changed:%s, Src=%s, blk=%s" ,
			// event.getChangedType(), event.getSourceBlock().getType(),
			// event.getBlock().getType());

			if (event.getSourceBlock().getType() == Material.DISPENSER
					|| event.getSourceBlock().getType() == Material.matchMaterial("WATER")) {
				if (!Reward.isReward(event.getSourceBlock())) {
					// plugin.getMessages().debug("RewardListeners: a %s changed a %s(%s)",
					// event.getSourceBlock().getType(), block.getType(), reward.getMoney());
					Core.getRewardBlockManager().removeReward(block);
					plugin.getRewardManager().dropRewardOnGround(block.getLocation(), reward);
				}
			} else if (event.getSourceBlock().getType() == Material.matchMaterial("PLAYER_HEAD")) {
				// plugin.getMessages().debug("PLAYER_HEAD changed PLAYER_HEAD");
				return;
			} else {
				// plugin.getMessages().debug("RewardListeners: Event Cancelled - a %s tried to
				// change a %s(%s)",
				// event.getSourceBlock().getType(), block.getType(), reward.getMoney());
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) { 
		if (event.isCancelled())
			return;
		@NotNull
		List<Block> changedBlocks = event.getBlocks();
		if (!changedBlocks.isEmpty())
			for (Block b : changedBlocks) {
				if (Reward.isReward(b)) {
					plugin.getMessages().debug("Is not possible to move a Reward with a Piston");
					event.setCancelled(true);
					return;
				}
			}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRewardBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;

		Block block = event.getBlock();
		if (Reward.isReward(block)) {
			Reward reward = Reward.getReward(block);
			Core.getRewardBlockManager().removeReward(block);
			plugin.getRewardManager().dropRewardOnGround(block.getLocation(), reward);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = (Player) event.getPlayer();
		if (player.getOpenInventory() != null) {
			if (player.getOpenInventory().getCursor() == null)
				return;
			if (!Reward.isReward(player.getOpenInventory().getCursor()))
				return;
			player.getOpenInventory().setCursor(null);
		}
	}

}
