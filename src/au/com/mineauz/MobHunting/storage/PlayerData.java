package au.com.mineauz.MobHunting.storage;

import org.bukkit.OfflinePlayer;

import au.com.mineauz.MobHunting.MobHunting;

public class PlayerData {
	private OfflinePlayer player;
	private boolean learning_mode;
	private boolean mute;

	public PlayerData(OfflinePlayer player, boolean learning_mode, boolean mute) {
		this.player = player;
		this.learning_mode = learning_mode;
		this.mute = mute;
	}

	public PlayerData(OfflinePlayer player) {
		this.player = player;
		this.learning_mode = true;
		this.mute = false;
	}

	/**
	 * @return the learning mode (0:false, 1: true (in learning mode))
	 */
	public boolean isLearningMode() {
		return learning_mode;
	}

	/**
	 * @param set
	 *            learning mode for player (0:false, 1: true (in learning mode))
	 */
	public void setLearningMode(boolean learning_mode) {
		this.learning_mode = learning_mode;
	}

	/**
	 * @return the mute status (0:false (unmuted), 1: true (muted))
	 */
	public boolean isMuted() {
		return mute;
	}

	/**
	 * @param type
	 *            the type to set (0:false (unmuted), 1: true (muted))
	 */
	public void setMuteMode(boolean mute) {
		this.mute = mute;
	}

	/**
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		if (player.getName().isEmpty())
			MobHunting.debug("PlayerStore-Playername for ID:%s was empty (%s)",
					player.getUniqueId(), player.getName());
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}
	
	@Override
	public String toString() {
		return String.format(
				"PlayerStore: {player: %s, Learning Mode: %s, Mute Mode: %s}",
				player.getName(), learning_mode, mute);
	}
}
