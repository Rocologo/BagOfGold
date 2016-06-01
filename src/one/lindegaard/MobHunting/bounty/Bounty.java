package one.lindegaard.MobHunting.bounty;

import org.bukkit.OfflinePlayer;

public class Bounty {

	private int bountyOwnerId;
	private OfflinePlayer bountyOwner;
	private String mobtype;
	private int wantedPlayerId;
	private OfflinePlayer wantedPlayer;
	private int npcId;
	private String mobId;
	private String worldGroup;
	private long createdDate;
	private long endDate;
	private double prize;
	private String message;
	private BountyStatus status;

	public Bounty() {
	}

	/**
	 * Contructor for Bounty
	 * 
	 * @param bountyOwner
	 *            - the bounty owner
	 * @param prize
	 *            - the prize set by bounty owner on the wanted player
	 * @param message
	 *            - the message to the wanted owner
	 */
	public Bounty(String worldGroup, OfflinePlayer bountyOwner, OfflinePlayer wantedPlayer, double prize,
			String message) {
		// Bounty on a Player
		this.worldGroup = worldGroup;
		this.bountyOwner = bountyOwner;
		this.mobtype = "Player";
		this.wantedPlayer = wantedPlayer;
		this.createdDate = System.currentTimeMillis();
		this.endDate = this.createdDate + 30L * 86400000L;
		this.prize = prize;
		this.message = message;
		this.status = BountyStatus.open;
	}

	public Bounty(String worldGroup, OfflinePlayer randomPlayer, double prize, String message) {
		// Random Bounty on a Player
		this.worldGroup = worldGroup;
		this.bountyOwner = null;
		this.mobtype = "Random";
		this.wantedPlayer = randomPlayer;
		this.createdDate = System.currentTimeMillis();
		this.endDate = this.createdDate + 30L * 86400000L;
		this.prize = prize;
		this.message = message;
		this.status = BountyStatus.open;
	}

	public Bounty(String worldGroup, OfflinePlayer bountyOwner, int npcId, double prize, String message) {
		// Bounty on a NPC
		this.worldGroup = worldGroup;
		this.bountyOwner = bountyOwner;
		this.mobtype = "NPC";
		this.npcId = npcId;
		this.createdDate = System.currentTimeMillis();
		this.endDate = this.createdDate + 30L * 86400000L;
		this.prize = prize;
		this.message = message;
		this.status = BountyStatus.open;
	}

	public Bounty(String worldGroup, OfflinePlayer bountyOwner, String mobId, double prize, String message) {
		// Bounty on a Mob
		this.worldGroup = worldGroup;
		this.bountyOwner = bountyOwner;
		this.mobtype = "Mob";
		this.mobId = mobId;
		this.createdDate = System.currentTimeMillis();
		this.endDate = this.createdDate + 30L * 86400000L;
		this.prize = prize;
		this.message = message;
		this.status = BountyStatus.open;
	}

	public Bounty(Bounty bounty) {
		bountyOwnerId = bounty.getBountyOwnerId();
		// bountyId = bounty.getBountyId();
		bountyOwner = bounty.getBountyOwner();
		mobtype = bounty.getMobtype();
		wantedPlayerId = bounty.getWantedPlayerId();
		wantedPlayer = bounty.getWantedPlayer();
		npcId = bounty.getNpcId();
		mobId = bounty.mobId;
		worldGroup = bounty.getWorldGroup();
		this.createdDate = System.currentTimeMillis();
		this.endDate = this.createdDate + 30L * 86400000L;
		prize = bounty.getPrize();
		message = bounty.getMessage();
		status = bounty.getStatus();
	}

	public int HashCode() {
		return wantedPlayer.hashCode() | bountyOwner.hashCode() | worldGroup.hashCode();

	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Bounty))
			return false;
		Bounty other = (Bounty) obj;
		if (bountyOwner == null)
			if (other.bountyOwner == null)
				return wantedPlayer.equals(other.wantedPlayer)
						&& worldGroup.equals(other.worldGroup);
			else
				return false;
		else
			return (bountyOwner.equals(other.bountyOwner) && wantedPlayer.equals(other.wantedPlayer)
					&& worldGroup.equals(other.worldGroup));
	}

	@Override
	public String toString() {
		if (bountyOwner != null)
			return String.format(
					"Bounty:{WorldGroup:%s,WantedPlayer:%s,BountyOwner:%s,NpcId:%s,MobId:%s,Prize:%s,Completed:%s}",
					worldGroup, wantedPlayer.getName(), bountyOwner.getName(), npcId, mobId, prize, status);
		else
			return String.format(
					"Bounty:{WorldGroup:%s,WantedPlayer:%s,BountyOwner:%s,NpcId:%s,MobId:%s,Prize:%s,Completed:%s}",
					worldGroup, wantedPlayer.getName(), "Random Bounty", npcId, mobId, prize, status);
	}

	/**
	 * @return the bountyOwnerId
	 */
	public int getBountyOwnerId() {
		return bountyOwnerId;
	}

	/**
	 * @param bountyOwnerId
	 *            the bountyOwnerId to set
	 */
	public void setBountyOwnerId(int bountyOwnerId) {
		this.bountyOwnerId = bountyOwnerId;
	}

	/**
	 * @return the bountyOwner
	 */
	public OfflinePlayer getBountyOwner() {
		return bountyOwner;
	}

	/**
	 * @param bountyOwner
	 *            the bountyOwner to set
	 */
	public void setBountyOwner(OfflinePlayer bountyOwner) {
		this.bountyOwner = bountyOwner;
	}

	/**
	 * @return the mobtype
	 */
	public String getMobtype() {
		return mobtype;
	}

	/**
	 * @param mobtype
	 *            the mobtype to set
	 */
	public void setMobtype(String mobtype) {
		this.mobtype = mobtype;
	}

	/**
	 * @return the wantedPlayerId
	 */
	public int getWantedPlayerId() {
		return wantedPlayerId;
	}

	/**
	 * @param wantedPlayerId
	 *            the wantedPlayerId to set
	 */
	public void setWantedPlayerId(int wantedPlayerId) {
		this.wantedPlayerId = wantedPlayerId;
	}

	/**
	 * @return the wantedPlayer
	 */
	public OfflinePlayer getWantedPlayer() {
		return wantedPlayer;
	}

	/**
	 * @param wantedPlayer
	 *            the wantedPlayer to set
	 */
	public void setWantedPlayer(OfflinePlayer wantedPlayer) {
		this.wantedPlayer = wantedPlayer;
	}

	/**
	 * @return the npcId
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * @param npcId
	 *            the npcId to set
	 */
	public void setNpcId(int npcId) {
		this.npcId = npcId;
	}

	/**
	 * @return the mobId
	 */
	public String getMobId() {
		return mobId;
	}

	/**
	 * @param mobId
	 *            the mobId to set
	 */
	public void setMobId(String mobId) {
		this.mobId = mobId;
	}

	/**
	 * @return the worldGroup
	 */
	public String getWorldGroup() {
		return worldGroup;
	}

	/**
	 * @param worldGroup
	 *            the worldGroup to set
	 */
	public void setWorldGroup(String worldGroup) {
		this.worldGroup = worldGroup;
	}

	/**
	 * @return the createdDate
	 */
	public long getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the endDate
	 */
	public long getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the prize
	 */
	public double getPrize() {
		return prize;
	}

	/**
	 * @param prize
	 *            the prize to set
	 */
	public void setPrize(double prize) {
		this.prize = prize;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the completed
	 */
	public BountyStatus getStatus() {
		return status;
	}

	/**
	 * @param completed
	 *            the completed to set
	 */
	public void setStatus(BountyStatus status) {
		this.status = status;
	}

	public boolean isCompleted() {
		return status.equals(BountyStatus.completed);
	}

	public boolean isOpen() {
		return status.equals(BountyStatus.open);
	}

}