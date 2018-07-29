package one.lindegaard.BagOfGold;

import org.bukkit.OfflinePlayer;

public class PlayerSettings {
	private OfflinePlayer player;
	private int playerId;
	private boolean learning_mode = false;
	private boolean mute = false;
	private String lastKnownWorldGrp;
	private String texture;
	private String signature;

	public PlayerSettings(OfflinePlayer player) {
		this.player = player;
		this.setLastKnownWorldGrp("default");
		this.setLearningMode(BagOfGold.getInstance().getConfigManager().learningMode);
		this.setMuteMode(false);
	}

	public PlayerSettings(OfflinePlayer player, String lastKnownWorldGrp, boolean learning_mode, boolean mute) {
		this.player = player;
		this.setLastKnownWorldGrp(lastKnownWorldGrp);
		this.setLearningMode(learning_mode);
		this.setMuteMode(mute);
	}
	
	public PlayerSettings(OfflinePlayer player, PlayerSettings ps) {
		this.player = ps.getPlayer();
		this.setLearningMode(ps.isLearningMode());
		this.setMuteMode(ps.isMuted());
	}

	public String getLastKnownWorldGrp() {
		return lastKnownWorldGrp;
	}

	public void setLastKnownWorldGrp(String lastKnownWorldGrp) {
		this.lastKnownWorldGrp = lastKnownWorldGrp;
	}

	/**
	 * @return the learning mode (0:false, 1: true (in learning mode))
	 */
	public boolean isLearningMode() {
		return learning_mode;
	}

	/**
	 * @param learning_mode
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
	 * @param mute
	 *            the mute to set (0:false (unmuted), 1: true (muted))
	 */
	public void setMuteMode(boolean mute) {
		this.mute = mute;
	}

	/**
	 * @return the player
	 */
	public OfflinePlayer getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}

	/**
	 * convert data to a readable format.
	 */
	@Override
	public String toString() {
		return String.format(
				"PlayerSettings: {player: Id:%s Name:%s, Learning: %s, Muted: %s, Last Known WorldGrp: %s}",
				playerId, player.getName(), learning_mode, mute, lastKnownWorldGrp);
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

}
