package one.lindegaard.BagOfGold.placeholder;

public class PlaceHolderData {
	
	int rank=9999;
	int total_kills=0;
	int total_assists=0;
	double total_cash=0;
	
	int total_killed_players=0;
		int total_killed_mobs;
	

	public PlaceHolderData() {
		
	}


	public int getRank() {
		return rank;
	}


	public void setRank(int rank) {
		this.rank = rank;
	}


	public int getTotal_kills() {
		return total_kills;
	}


	public void setTotal_kills(int total_kills) {
		this.total_kills = total_kills;
	}


	public int getTotal_assists() {
		return total_assists;
	}


	public void setTotal_assists(int total_assists) {
		this.total_assists = total_assists;
	}


	public double getTotal_cash() {
		return total_cash;
	}


	public void setTotal_cash(double total_cash) {
		this.total_cash = total_cash;
	}

	/**
	 * @return the total_killed_players
	 */
	public int getTotal_killed_players() {
		return total_killed_players;
	}


	/**
	 * @param total_killed_players the total_killed_players to set
	 */
	public void setTotal_killed_players(int total_killed_players) {
		this.total_killed_players = total_killed_players;
	}


	/**
	 * @return the total_killed_mobs
	 */
	public int getTotal_killed_mobs() {
		return total_killed_mobs;
	}


	/**
	 * @param total_killed_mobs the total_killed_mobs to set
	 */
	public void setTotal_killed_mobs(int total_killed_mobs) {
		this.total_killed_mobs = total_killed_mobs;
	}
	
}
