package one.lindegaard.BagOfGold;

import org.bukkit.OfflinePlayer;

public class PlayerSettings_old {
	private OfflinePlayer player;
	private int playerId;
	private boolean learning_mode = false;
	private boolean mute = false;
	private String lastKnownWorldGrp;
	private String texture;
	private String signature;
	private long last_logon;
	private long last_interest;

	public PlayerSettings_old(OfflinePlayer player) {
		this.player = player;
		this.setLastKnownWorldGrp("default");
		this.setLearningMode(BagOfGold.getInstance().getConfigManager().learningMode);
		this.setMuteMode(false);
	}

	public PlayerSettings_old(OfflinePlayer player, String lastKnownWorldGrp, boolean learning_mode, boolean mute, String texture, String signature, long last_logon, long last_interest) {
		this.player = player;
		this.setLastKnownWorldGrp(lastKnownWorldGrp);
		this.setLearningMode(learning_mode);
		this.setMuteMode(mute);
		this.setTexture(texture);
		this.setSignature(signature);
		this.setLast_logon(last_logon==0?last_logon=System.currentTimeMillis():last_logon);
		this.setLast_interest(last_interest==0?System.currentTimeMillis():last_interest);
	}
	
	public PlayerSettings_old(OfflinePlayer player, PlayerSettings_old ps) {
		this.player = ps.getPlayer();
		this.setLearningMode(ps.isLearningMode());
		this.setMuteMode(ps.isMuted());
		this.setLastKnownWorldGrp(ps.getLastKnownWorldGrp());
		this.setTexture(ps.getTexture());
		this.setSignature(ps.getSignature());
		this.setLast_logon(last_logon==0?last_logon=System.currentTimeMillis():last_logon);
		this.setLast_interest(last_interest==0?System.currentTimeMillis():last_interest);
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

	/**
	 * @return the last_logon
	 */
	public long getLast_logon() {
		return last_logon;
	}

	/**
	 * @param last_logon the last_logon to set
	 */
	public void setLast_logon(long last_logon) {
		this.last_logon = last_logon;
	}

	/**
	 * @return the last_interest
	 */
	public long getLast_interest() {
		return last_interest;
	}

	/**
	 * @param last_interest the last_interest to set
	 */
	public void setLast_interest(long last_interest) {
		this.last_interest = last_interest;
	}

}
