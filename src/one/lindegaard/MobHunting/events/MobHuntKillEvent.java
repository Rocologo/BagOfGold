package one.lindegaard.MobHunting.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import one.lindegaard.MobHunting.DamageInformation;
import one.lindegaard.MobHunting.HuntData;

public class MobHuntKillEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private HuntData mHuntData;
	private DamageInformation mInfo;
	private LivingEntity mEntity;
	private Player mKiller;
	private boolean mIsCancelled = false;

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public MobHuntKillEvent(HuntData huntData, DamageInformation info,
			LivingEntity deadEntity, Player killer) {
		mHuntData = huntData;
		mInfo = info;
		mEntity = deadEntity;
		mKiller = killer;
	}

	public HuntData getHuntData() {
		return mHuntData;
	}

	public DamageInformation getDamageInfo() {
		return mInfo;
	}

	public LivingEntity getKilledEntity() {
		return mEntity;
	}

	public Player getPlayer() {
		return mKiller;
	}

	@Override
	public boolean isCancelled() {
		return mIsCancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		mIsCancelled = cancel;
	}

}
