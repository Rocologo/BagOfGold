package one.lindegaard.MobHunting.bounty;

import java.util.Date;

import org.bukkit.OfflinePlayer;

import one.lindegaard.MobHunting.WorldGroupManager;

public class Bounty {

	private int bountyId;
	private int bountyOwnerId;
	private OfflinePlayer bountyOwner;
	private String mobtype;
	private int wantedPlayerId;
	private OfflinePlayer wantedPlayer;
	private int npcId;
	private String mobId;
	private String worldGroup;
	private Date createdDate;
	private long endDate;
	private double prize;
	private String message;
	private boolean completed;

	public Bounty() {
		bountyId=0;
		bountyOwnerId=0;
		bountyOwner=null;
		mobtype="";
		wantedPlayerId=0;
		wantedPlayer=null;
		npcId=0;
		mobId="";
		worldGroup="";
		createdDate=null;
		endDate=0;
		prize=0;
		message="";
		completed=true;
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
		//Bounty on a Player
		this.worldGroup = worldGroup;
		this.bountyOwner = bountyOwner;
		this.mobtype = "Player";
		this.wantedPlayer = wantedPlayer;
		this.prize = prize;
		this.message = message;
	}
	
	public Bounty(String worldGroup, OfflinePlayer bountyOwner, int npcId, double prize,
			String message) {
		//Bounty on a NPC
		this.worldGroup = worldGroup;
		this.bountyOwner = bountyOwner;
		this.mobtype = "NPC";
		this.npcId = npcId;
		this.prize = prize;
		this.message = message;
	}
	public Bounty(String worldGroup, OfflinePlayer bountyOwner, String mobId, double prize,
			String message) {
		//Bounty on a Mob
		this.worldGroup = worldGroup;
		this.bountyOwner = bountyOwner;
		this.mobtype = "Mob";
		this.mobId = mobId;
		this.prize = prize;
		this.message = message;
	}


	public Bounty(Bounty bounty) {
		bountyOwnerId = bounty.getBountyId();
		bountyId = bounty.getBountyId();
		bountyOwner = bounty.getBountyOwner();
		mobtype = bounty.getMobtype();
		wantedPlayerId = bounty.getWantedPlayerId();
		wantedPlayer = bounty.getWantedPlayer();
		npcId = bounty.getNpcId();
		mobId = bounty.mobId;
		worldGroup = bounty.getWorldGroup();
		createdDate = bounty.getCreatedDate();
		endDate = bounty.getEndDate();
		prize = bounty.getPrize();
		message = bounty.getMessage();
		completed = bounty.isCompleted();
	}
	
	@Override
	public int hashCode() {
		//bountyId is uniqe
		return bountyId;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Bounty))
			return false;
		Bounty other = (Bounty) obj;
		return bountyId==other.bountyId;
	}

	@Override
	public String toString() {
		return String.format("Bounty: {Id:%s Owner:%s Wanted:%s NPC:%s Mob:%s Completed:%s}",
				bountyId, bountyOwner.getName(), wantedPlayer.getName(),
				npcId, mobId, completed);
	}

	/**
	 * @return the bountyId
	 */
	public int getBountyId() {
		return bountyId;
	}

	/**
	 * @param bountyId
	 *            the bountyId to set
	 */
	public void setBountyId(int bountyId) {
		this.bountyId = bountyId;
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
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
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
	public void setEndDate(Long endDate) {
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
	public boolean isCompleted() {
		return completed;
	}

	/**
	 * @param completed
	 *            the completed to set
	 */
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

}