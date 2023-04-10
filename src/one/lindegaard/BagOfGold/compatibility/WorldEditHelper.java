package one.lindegaard.BagOfGold.compatibility;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import one.lindegaard.MobHunting.MobHunting;

public class WorldEditHelper {

	public static BlockVector3 getPointA(Player player) throws IllegalArgumentException {
		if (WorldEditCompat.isSupported())
			throw new IllegalArgumentException("WorldEdit is not present");

		com.sk89q.worldedit.world.World wor = WorldEditCompat.getWorldEdit().getSession(player).getSelectionWorld();
		Region sel = null;
		try {
			sel = WorldEditCompat.getWorldEdit().getSession(player).getSelection(wor);
		} catch (IncompleteRegionException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		if (sel == null)
			throw new IllegalArgumentException(
					MobHunting.getInstance().getMessages().getString("mobhunting.commands.select.no-select"));

		if (!(sel instanceof CuboidRegion))
			throw new IllegalArgumentException(
					MobHunting.getInstance().getMessages().getString("mobhunting.commands.select.select-type"));

		return sel.getMinimumPoint();
	}

	public static BlockVector3 getPointB(Player player) throws IllegalArgumentException {
		if (WorldEditCompat.isSupported())
			throw new IllegalArgumentException("WorldEdit is not present");

		com.sk89q.worldedit.world.World wor = WorldEditCompat.getWorldEdit().getSession(player).getSelectionWorld();
		Region sel = null;
		try {
			sel = WorldEditCompat.getWorldEdit().getSession(player).getSelection(wor);
		} catch (IncompleteRegionException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		if (sel == null)
			throw new IllegalArgumentException(
					MobHunting.getInstance().getMessages().getString("mobhunting.commands.select.no-select"));

		if (!(sel instanceof CuboidRegion))
			throw new IllegalArgumentException(
					MobHunting.getInstance().getMessages().getString("mobhunting.commands.select.select-type"));

		return sel.getMaximumPoint();
	}
}
