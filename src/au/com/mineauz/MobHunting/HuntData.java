package au.com.mineauz.MobHunting;

import java.util.ArrayList;
import java.util.Iterator;

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
	
	public void clearGrindingArea(Location location)
	{
		Iterator<Area> it = lastGridingAreas.iterator();
		while(it.hasNext())
		{
			Area area = it.next();
			
			if(area.center.getWorld().equals(location.getWorld()))
			{
				if(area.center.distance(location) < area.range)
					it.remove();
			}
		}
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
		if(killStreak < MobHunting.config().killstreakLevel1)
			return 0;
		else if(killStreak < MobHunting.config().killstreakLevel2)
			return 1;
		else if(killStreak < MobHunting.config().killstreakLevel3)
			return 2;
		else if(killStreak < MobHunting.config().killstreakLevel4)
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
			return MobHunting.config().killstreakLevel1Mult;
		case 2:
			return MobHunting.config().killstreakLevel2Mult;
		case 3:
			return MobHunting.config().killstreakLevel3Mult;
		default:
			return MobHunting.config().killstreakLevel4Mult;
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
