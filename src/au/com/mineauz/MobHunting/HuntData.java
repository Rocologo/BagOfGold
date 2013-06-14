package au.com.mineauz.MobHunting;

import java.util.ArrayList;

import org.bukkit.Location;

public class HuntData
{
	public int killStreak = 0;
	public int dampenedKills = 0;
	
	public Location lastKillAreaCenter;
	
	public ArrayList<Area> lastGridingAreas = new ArrayList<Area>(); 
	
	public boolean enabled = true;
	
	public Area getGrindingArea(Location location)
	{
		for(Area area : lastGridingAreas)
		{
			if(area.center.getWorld().equals(location.getWorld()))
			{
				if(area.center.distance(location) < area.range)
					return area;
			}
		}
		
		return null;
	}
	public void recordGrindingArea()
	{
		for(Area area : lastGridingAreas)
		{
			if(lastKillAreaCenter.getWorld().equals(area.center.getWorld()))
			{
				double dist = lastKillAreaCenter.distance(area.center);
				
				double remaining = dist;
				remaining -= area.range;
				remaining -= MobHunting.cDampnerRange;
				
				if(remaining < 0)
				{
					if(dist > area.range)
						area.range = dist;
					
					area.count += dampenedKills;
					
					return;
				}
			}
		}
		
		Area area = new Area();
		area.center = lastKillAreaCenter;
		area.range = MobHunting.cDampnerRange;
		area.count = dampenedKills;
		lastGridingAreas.add(area);
	}
	
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
