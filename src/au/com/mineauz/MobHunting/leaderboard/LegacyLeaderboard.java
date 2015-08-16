package au.com.mineauz.MobHunting.leaderboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.util.BlockVector;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;
import au.com.mineauz.MobHunting.StatType;
import au.com.mineauz.MobHunting.storage.DataCallback;
import au.com.mineauz.MobHunting.storage.StatStore;
import au.com.mineauz.MobHunting.storage.TimePeriod;

public class LegacyLeaderboard implements DataCallback<List<StatStore>> {
	private String mId;
	private World mWorld;
	private BlockVector mMinCorner;
	private BlockVector mMaxCorner;

	private boolean mHorizontal;

	private TimePeriod mPeriod;
	private StatType mType;

	public LegacyLeaderboard(String id, StatType type, TimePeriod period,
			Location pointA, Location pointB, boolean horizontal)
			throws IllegalArgumentException {
		mId = id;
		mType = type;
		mPeriod = period;
		mWorld = pointA.getWorld();

		mMinCorner = new BlockVector(Math.min(pointA.getBlockX(),
				pointB.getBlockX()), Math.min(pointA.getBlockY(),
				pointB.getBlockY()), Math.min(pointA.getBlockZ(),
				pointB.getBlockZ()));
		mMaxCorner = new BlockVector(Math.max(pointA.getBlockX(),
				pointB.getBlockX()), Math.max(pointA.getBlockY(),
				pointB.getBlockY()), Math.max(pointA.getBlockZ(),
				pointB.getBlockZ()));

		if (mMaxCorner.getBlockX() - mMinCorner.getBlockX() > 1
				&& mMaxCorner.getBlockZ() - mMaxCorner.getBlockZ() > 1)
			throw new IllegalArgumentException(
					Messages.getString("leaderboard.thick")); 

		mHorizontal = horizontal;
	}

	LegacyLeaderboard() {
	}

	private List<Sign> getSigns() {
		ArrayList<Sign> signs = new ArrayList<Sign>();
		if (mHorizontal) {
			for (int y = mMaxCorner.getBlockY(); y >= mMinCorner.getBlockY(); --y) {
				for (int x = mMinCorner.getBlockX(); x <= mMaxCorner
						.getBlockX(); ++x) {
					for (int z = mMinCorner.getBlockZ(); z <= mMaxCorner
							.getBlockZ(); ++z) {
						BlockState state = mWorld.getBlockAt(x, y, z)
								.getState();

						if (state instanceof Sign)
							signs.add((Sign) state);
					}
				}
			}
		} else {
			for (int z = mMinCorner.getBlockZ(); z <= mMaxCorner.getBlockZ(); ++z) {
				for (int x = mMinCorner.getBlockX(); x <= mMaxCorner
						.getBlockX(); ++x) {
					for (int y = mMaxCorner.getBlockY(); y >= mMinCorner
							.getBlockY(); --y) {
						BlockState state = mWorld.getBlockAt(x, y, z)
								.getState();

						if (state instanceof Sign)
							signs.add((Sign) state);
					}
				}
			}
		}

		return signs;
	}

	private int countSigns() {
		int count = 0;
		for (int x = mMinCorner.getBlockX(); x <= mMaxCorner.getBlockX(); ++x) {
			for (int y = mMinCorner.getBlockY(); y <= mMaxCorner.getBlockY(); ++y) {
				for (int z = mMinCorner.getBlockZ(); z <= mMaxCorner
						.getBlockZ(); ++z) {
					Block block = mWorld.getBlockAt(x, y, z);
					if (block.getType() == Material.WALL_SIGN
							|| block.getType() == Material.SIGN_POST)
						++count;
				}
			}
		}

		return count;
	}

	public void updateBoard() {
		MobHunting.instance.getDataStore().requestStats(mType, mPeriod,
				countSigns() * 4, this);
	}

	public String getId() {
		return mId;
	}

	public World getWorld() {
		return mWorld;
	}

	public BlockVector getMin() {
		return mMinCorner;
	}

	public BlockVector getMax() {
		return mMaxCorner;
	}

	public Map<String, Object> write() {
		HashMap<String, Object> objects = new HashMap<String, Object>();
		objects.put("id", mId); 
		objects.put("world-l", mWorld.getUID().getLeastSignificantBits()); 
		objects.put("world-h", mWorld.getUID().getMostSignificantBits()); 
		objects.put("mi-x", mMinCorner.getBlockX()); 
		objects.put("mi-y", mMinCorner.getBlockY()); 
		objects.put("mi-z", mMinCorner.getBlockZ()); 

		objects.put("ma-x", mMaxCorner.getBlockX()); 
		objects.put("ma-y", mMaxCorner.getBlockY()); 
		objects.put("ma-z", mMaxCorner.getBlockZ()); 

		objects.put("hor", mHorizontal); 

		objects.put("period", mPeriod.ordinal()); 
		objects.put("type", mType.getDBColumn()); 
		
		return objects;
	}

	private long toLong(Object obj) {
		if (obj instanceof Long)
			return (Long) obj;
		else if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number"); //$NON-NLS-1$
	}

	private int toInt(Object obj) {
		if (obj instanceof Integer)
			return (int) (Integer) obj;

		throw new IllegalArgumentException("Not a number"); //$NON-NLS-1$
	}

	private boolean toBool(Object obj) {
		if (obj instanceof Boolean)
			return (Boolean) obj;

		return Boolean.parseBoolean(obj.toString());
	}

	public void read(Map<String, Object> data) {
		UUID worldId = new UUID(toLong(data.get("world-h")),
				toLong(data.get("world-l")));
		mWorld = Bukkit.getWorld(worldId);

		mMinCorner = new BlockVector(toInt(data.get("mi-x")),
				toInt(data.get("mi-y")), toInt(data.get("mi-z")));
		mMaxCorner = new BlockVector(toInt(data.get("ma-x")),
				toInt(data.get("ma-y")), toInt(data.get("ma-z")));

		mHorizontal = toBool(data.get("hor"));

		mPeriod = TimePeriod.values()[toInt(data.get("period"))];
		mType = StatType.fromColumnName((String) data.get("type"));

		mId = (String) data.get("id");
	}

	@Override
	public void onCompleted(List<StatStore> data) {
		List<Sign> signs = getSigns();

		// Clear the signs
		for (Sign sign : signs) {
			sign.setLine(0, ""); //$NON-NLS-1$
			sign.setLine(1, ""); //$NON-NLS-1$
			sign.setLine(2, ""); //$NON-NLS-1$
			sign.setLine(3, ""); //$NON-NLS-1$
		}

		if (signs.isEmpty())
			return;

		if (mHorizontal) {
			int startSign = 0;
			int sign = 0;
			int line = 0;
			int y = signs.get(0).getY();
			int returnSign = 0;

			for (StatStore stat : data) {
				if (stat.getAmount() == 0)
					continue;

				if (sign >= signs.size() || signs.get(sign).getY() != y) {
					returnSign = sign;
					sign = startSign;

					++line;

					if (line >= 4) {
						sign = startSign = returnSign;

						if (sign >= signs.size())
							break;
						else
							y = signs.get(sign).getY();
					}
				}

				signs.get(sign).setLine(line,
						stat.getAmount() + " " + stat.getPlayer().getName());

				++sign;
			}
		} else {
			int sign = 0;
			int line = 0;
			for (StatStore stat : data) {
				if (stat.getAmount() == 0)
					continue;

				if (line >= 4) {
					line = 0;
					++sign;
				}

				if (sign >= signs.size())
					break;

				signs.get(sign).setLine(line,
						stat.getAmount() + " " + stat.getPlayer().getName());

				++line;
			}
		}

		for (Sign sign : signs)
			sign.update(true, false);
	}

	@Override
	public void onError(Throwable error) {
		error.printStackTrace();
	}

	public void setType(StatType type) {
		mType = type;
	}

	public void setPeriod(TimePeriod period) {
		mPeriod = period;
	}

	public void setHorizontal(boolean horizontal) {
		mHorizontal = horizontal;
	}

	public StatType getType() {
		return mType;
	}

	public TimePeriod getPeriod() {
		return mPeriod;
	}

	public boolean getHorizontal() {
		return mHorizontal;
	}
}
