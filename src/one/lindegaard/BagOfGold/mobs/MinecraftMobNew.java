package one.lindegaard.BagOfGold.mobs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import one.lindegaard.BagOfGold.BagOfGold;
import one.lindegaard.BagOfGold.rewards.CustomItems;
import one.lindegaard.BagOfGold.rewards.Reward;
import one.lindegaard.Core.Tools;
import one.lindegaard.Core.mobs.MobType;
import one.lindegaard.Core.rewards.CoreCustomItems;

public class MinecraftMobNew {

	MobType mMob;

	public MinecraftMobNew(MobType mob) {
		mMob = mob;
	}

	public String getFriendlyName() {
		return BagOfGold.getInstance().getMessages().getString("mobs." + mMob.name() + ".name");
	}

	public String getTexture(String displayname) {
		for (MobType mob : MobType.values()) {
			if (mob.getDisplayName().equalsIgnoreCase(displayname) || getFriendlyName().equalsIgnoreCase(displayname)) {
				return String.valueOf(mob.getTextureValue());
			}
		}
		return "";
	}

	public String getSignature(String displayname) {
		for (MobType mob : MobType.values()) {
			if (mob.getDisplayName().equalsIgnoreCase(displayname) || getFriendlyName().equalsIgnoreCase(displayname)) {
				return String.valueOf(mob.getTextureSignature());
			}
		}
		return "";

	}

	// TODO: HEADS ??? and is this in CustomItems???
	public ItemStack getCustomHead(String name, int amount, double money) {
		ItemStack skull;
		switch (mMob) {
		case Skeleton:
			skull = CoreCustomItems.getDefaultSkeletonHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), mMob.getPlayerUUID()));
			break;

		case WitherSkeleton:
			skull = CoreCustomItems.getDefaultWitherSkeletonHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), mMob.getPlayerUUID()));
			break;

		case Zombie:
			skull = CoreCustomItems.getDefaultZombieHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), mMob.getPlayerUUID()));
			break;

		case PvpPlayer:
			skull = CoreCustomItems.getDefaultPlayerHead(amount);
			SkullMeta sm = (SkullMeta) skull.getItemMeta();
			sm.setOwner(name);
			skull.setItemMeta(sm);
			break;

		case Creeper:
			skull = CoreCustomItems.getDefaultCreeperHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), mMob.getPlayerUUID()));
			break;

		case EnderDragon:
			skull = CoreCustomItems.getDefaultEnderDragonHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), mMob.getPlayerUUID()));
			break;

		default:
			ItemStack is = new ItemStack(new CustomItems().getCustomtexture(
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), getFriendlyName(), mMob.getTextureValue(),
					mMob.getTextureSignature(), money, UUID.randomUUID(), mMob.getPlayerUUID()));
			is.setAmount(amount);
			return is;
		}
		return skull;
	}

	/**
	 * setDisplayNameAndHiddenLores: add the Display name and the (hidden) Lores.
	 * The lores identifies the reward and contain secret information.
	 * 
	 * @param skull  - The base itemStack without the information.
	 * @param reward - The reward information is added to the ItemStack
	 * @return the updated ItemStack.
	 */
	public ItemStack setDisplayNameAndHiddenLores(ItemStack skull, Reward reward) {
		ItemMeta skullMeta = skull.getItemMeta();
		if (reward.getRewardType().equals(UUID.fromString(Reward.MH_REWARD_BAG_OF_GOLD_UUID)))
			skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + reward.getDisplayname(),
					"Hidden:" + reward.getMoney(), "Hidden:" + reward.getRewardType(),
					reward.getMoney() == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(),
					"Hidden:" + reward.getSkinUUID())));
		else
			skullMeta.setLore(new ArrayList<String>(Arrays.asList("Hidden:" + reward.getDisplayname(),
					"Hidden:" + reward.getMoney(), "Hidden:" + reward.getRewardType(),
					reward.getMoney() == 0 ? "Hidden:" : "Hidden:" + UUID.randomUUID(),
					"Hidden:" + reward.getSkinUUID(),
					BagOfGold.getInstance().getConfigManager().dropMoneyOnGroundSkullRewardName)));

		if (reward.getMoney() == 0)
			skullMeta.setDisplayName(
					ChatColor.valueOf(BagOfGold.getInstance().getConfigManager().dropMoneyOnGroundTextColor)
							+ reward.getDisplayname());
		else
			skullMeta.setDisplayName(ChatColor
					.valueOf(BagOfGold.getInstance().getConfigManager().dropMoneyOnGroundTextColor)
					+ (BagOfGold.getInstance().getConfigManager().dropMoneyOnGroundItemtype.equalsIgnoreCase("ITEM")
							? Tools.format(reward.getMoney())
							: reward.getDisplayname() + " (" + Tools.format(reward.getMoney()) + ")"));
		skull.setItemMeta(skullMeta);
		return skull;
	}

	public static MobType getMinecraftMobType(String name) {
		String name1 = name.replace(" ", "_");
		for (MobType type : MobType.values())
			if (BagOfGold.getInstance().getMessages().getString("mobs." + type.name() + ".name").replace(" ", "_")
					.equalsIgnoreCase(name1) || type.getDisplayName().replace(" ", "_").equalsIgnoreCase(name1)
					|| type.name().equalsIgnoreCase(name1))
				return type;
		return null;
	}

	public ItemStack getCustomHead(String name, int amount, double money, UUID skinUUID) {
		ItemStack skull;
		switch (mMob) {
		case Skeleton:
			skull = CoreCustomItems.getDefaultSkeletonHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), skinUUID));
			break;

		case WitherSkeleton:
			skull = CoreCustomItems.getDefaultWitherSkeletonHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), skinUUID));
			break;

		case Zombie:
			skull = CoreCustomItems.getDefaultZombieHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), skinUUID));
			break;

		case PvpPlayer:
			skull = new CustomItems().getPlayerHead(skinUUID, amount, money);
			break;

		case Creeper:
			skull = CoreCustomItems.getDefaultCreeperHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), skinUUID));
			break;

		case EnderDragon:
			skull = CoreCustomItems.getDefaultEnderDragonHead(amount);
			skull = setDisplayNameAndHiddenLores(skull, new Reward(getFriendlyName(), money,
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), UUID.randomUUID(), skinUUID));
			break;

		default:
			ItemStack is = new ItemStack(new CustomItems().getCustomtexture(
					UUID.fromString(Reward.MH_REWARD_KILLED_UUID), getFriendlyName(), mMob.getTextureValue(),
					mMob.getTextureSignature(), money, UUID.fromString(Reward.MH_REWARD_KILLED_UUID), skinUUID));
			is.setAmount(amount);
			return is;
		}
		return skull;
	}

}
