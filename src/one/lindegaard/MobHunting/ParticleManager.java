package one.lindegaard.MobHunting;

import java.util.Map.Entry;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitTask;

public class ParticleManager {
	private WeakHashMap<LivingEntity, Effect> mEffects = new WeakHashMap<LivingEntity, Effect>();

	private BukkitTask mTask;

	public void attachEffect(LivingEntity entity, Effect effect) {
		mEffects.put(entity, effect);

		if (mTask == null)
			mTask = Bukkit.getScheduler().runTaskTimer(MobHunting.getInstance(),
					new EffectApplyTask(), 10L, 10L);
	}

	public void removeEffect(LivingEntity entity) {
		mEffects.remove(entity);
	}

	private class EffectApplyTask implements Runnable {
		@Override
		public void run() {
			for (Entry<LivingEntity, Effect> entry : mEffects.entrySet()) {
				if (entry.getKey().isValid())
					entry.getKey()
							.getWorld()
							.playEffect(entry.getKey().getLocation(),
									entry.getValue(), 0);
			}

			if (mEffects.isEmpty()) {
				mTask.cancel();
				mTask = null;
			}
		}
	}
}
