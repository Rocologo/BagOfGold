package au.com.mineauz.MobHunting.leaderboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.util.BlockVector;

import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.DataCallback;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class Leaderboard implements DataCallback<List<StatStore>>
{
	private World mWorld;
	private BlockVector mMinCorner;
	private BlockVector mMaxCorner;
	
	private boolean mHorizontal;

	private TimePeriod mPeriod;
	private StatType mType;
	
	public Leaderboard(StatType type, TimePeriod period, Location pointA, Location pointB, boolean horizontal) throws IllegalArgumentException
	{
		mType = type;
		mPeriod = period;
		mWorld = pointA.getWorld();
		
		mMinCorner = new BlockVector(Math.min(pointA.getBlockX(), pointB.getBlockX()), Math.min(pointA.getBlockY(), pointB.getBlockY()), Math.min(pointA.getBlockZ(), pointB.getBlockZ()));
		mMaxCorner = new BlockVector(Math.max(pointA.getBlockX(), pointB.getBlockX()), Math.max(pointA.getBlockY(), pointB.getBlockY()), Math.max(pointA.getBlockZ(), pointB.getBlockZ()));
		
		if(mMaxCorner.getBlockX() - mMinCorner.getBlockX() > 1 && mMaxCorner.getBlockZ() - mMaxCorner.getBlockZ() > 1)
			throw new IllegalArgumentException("The selection is too thick. Either the X dimension or the Z dimension of the selection must be 1 thick");
		
		mHorizontal = horizontal;
	}
	
	private List<Sign> getSigns()
	{
		ArrayList<Sign> signs = new ArrayList<Sign>();
		if(mHorizontal)
		{
			for(int y = mMaxCorner.getBlockY(); y >= mMinCorner.getBlockY(); --y)
			{
				for(int x = mMinCorner.getBlockX(); x <= mMaxCorner.getBlockX(); ++x)
				{
					for(int z = mMinCorner.getBlockZ(); z <= mMaxCorner.getBlockZ(); ++z)
					{
						BlockState state = mWorld.getBlockAt(x,y,z).getState();
						
						if(state instanceof Sign)
							signs.add((Sign)state);
					}
				}
			}
		}
		else
		{
			for(int z = mMinCorner.getBlockZ(); z <= mMaxCorner.getBlockZ(); ++z)
			{
				for(int x = mMinCorner.getBlockX(); x <= mMaxCorner.getBlockX(); ++x)
				{
					for(int y = mMaxCorner.getBlockY(); y >= mMinCorner.getBlockY(); --y)
					{
						BlockState state = mWorld.getBlockAt(x,y,z).getState();
						
						if(state instanceof Sign)
							signs.add((Sign)state);
					}
				}
			}
		}
		
		return signs;
	}
	
	private int countSigns()
	{
		int count = 0;
		for(int x = mMinCorner.getBlockX(); x <= mMaxCorner.getBlockX(); ++x)
		{
			for(int y = mMinCorner.getBlockY(); y <= mMaxCorner.getBlockY(); ++y)
			{
				for(int z = mMinCorner.getBlockZ(); z <= mMaxCorner.getBlockZ(); ++z)
				{
					Block block = mWorld.getBlockAt(x,y,z); 
					if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST)
						++count;
				}
			}
		}
		
		return count;
	}
	
	public void updateBoard()
	{
		MobHunting.instance.getDataStore().requestStats(mType, mPeriod, countSigns() * 4, this);
	}
	
	@Override
	public void onCompleted( List<StatStore> data )
	{
		List<Sign> signs = getSigns();
		
		// Clear the signs
		for(Sign sign : signs)
		{
			sign.setLine(0, "");
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
		}
		
		if(mHorizontal)
		{
			int startSign = 0;
			int sign = 0;
			int line = 0;
			int y = signs.get(0).getY();
			int returnSign = 0;
			
			for(StatStore stat : data)
			{
				if(sign >= signs.size() || signs.get(sign).getY() != y)
				{
					returnSign = sign;
					sign = startSign;
					
					++line;
					
					if(line >= 4)
					{
						sign = startSign = returnSign;
						
						if(sign >= signs.size())
							break;
						else
							y = signs.get(sign).getY();
					}
				}
				
				signs.get(sign).setLine(line, stat.amount + " " + stat.playerName);
				
				++sign;
			}
		}
		else
		{
			int sign = 0;
			int line = 0;
			for(StatStore stat : data)
			{
				if(line >= 4)
				{
					line = 0;
					++sign;
				}
				
				if(sign >= signs.size())
					break;
				
				signs.get(sign).setLine(line, stat.amount + " " + stat.playerName);
				
				++line;
			}
		}
		
		for(Sign sign : signs)
			sign.update(true, false);
	}
	
	@Override
	public void onError( Throwable error )
	{
		error.printStackTrace();
	}
}
