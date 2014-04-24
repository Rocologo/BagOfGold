package au.com.mineauz.MobHunting.util;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class UUIDHelper
{
	private static HashMap<String, UUID> mKnown;
	
	public static void initialize()
	{
		mKnown = new HashMap<String, UUID>();
		for(OfflinePlayer player : Bukkit.getOfflinePlayers())
		{
			if(player.getName() != null && player.getUniqueId() != null)
				mKnown.put(player.getName().toLowerCase(), player.getUniqueId());
		}
	}
	
	public static UUID getKnown(String name)
	{
		return mKnown.get(name.toLowerCase());
	}
}
