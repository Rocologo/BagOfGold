package one.lindegaard.BagOfGold;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.GameMode;

public class PlayerBalances {

	private HashMap<String, PlayerBalance> playerBalances = new HashMap<String, PlayerBalance>();

	public HashMap<String, PlayerBalance> getPlayerBalances() {
		return playerBalances;
	}

	public PlayerBalance getPlayerBalance(String worldGroup, GameMode gamemode) {
		return playerBalances.get(String.valueOf(gamemode) + worldGroup);
	}

	public void putPlayerBalance(PlayerBalance playerBalance) {
		playerBalances.put(String.valueOf(playerBalance.getGamemode()) + playerBalance.getWorldGroup(), playerBalance);
	}

	public boolean has(String worldGroup, GameMode gamemode) {
		return playerBalances.containsKey(String.valueOf(gamemode) + worldGroup);
	}

	public boolean isEmpty() {
		return playerBalances.isEmpty();
	}

	public String toString() {
		StringBuilder str = new StringBuilder().append("PlayerBalances: ");
		for (Entry<String, PlayerBalance> pb : playerBalances.entrySet()) {
			str.append("\n" + pb.getValue().toString());
		}
		str.append("}");
		return str.toString();
	}

}
