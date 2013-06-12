package au.com.mineauz.MobHunting;

import org.bukkit.Location;

public class HuntData
{
	public int killStreak = 0;
	public int dampenedKills = 0;
	
	public Location lastKillAreaCenter;
	
	public boolean enabled = true;
	
	public int getKillstreakLevel()
	{
		if(killStreak < 5)
			return 0;
		else if(killStreak < 10)
			return 1;
		else if(killStreak < 20)
			return 2;
		else if(killStreak < 40)
			return 3;
		else
			return 4;
	}
	public double getKillstreakMultiplier()
	{
		int level = getKillstreakLevel();
		
		switch(level)
		{
		case 0:
			return 1.0;
		case 1:
			return 1.5;
		case 2:
			return 2.0;
		case 3:
			return 3.0;
		default:
			return 4.0;
		}
	}
	
	public double getDampnerMultiplier()
	{
		if(dampenedKills < 10)
			return 1.0;
		else if(dampenedKills < 20)
			return (1 - ((dampenedKills - 10) / 10.0));
		else
			return 0;
	}
	
}
