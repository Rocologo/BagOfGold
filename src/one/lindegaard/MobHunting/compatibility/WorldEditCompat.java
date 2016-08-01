package one.lindegaard.MobHunting.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

import one.lindegaard.MobHunting.Messages;

public class WorldEditCompat {
	private static WorldEditPlugin mPlugin;
	private static boolean supported = false;

	public WorldEditCompat() {
		mPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

		Bukkit.getLogger()
				.info("[MobHunting] Enabling compatibility with WorldEdit (" + getWorldEdit().getDescription().getVersion() + ")");
		supported = true;
	}

	public static WorldEditPlugin getWorldEdit() {
		return mPlugin;
	}

	public static Location getPointA(Player player) throws IllegalArgumentException {
		if (mPlugin == null)
			throw new IllegalArgumentException("WorldEdit is not present");

		Selection sel = mPlugin.getSelection(player);

		if (sel == null)
			throw new IllegalArgumentException(Messages.getString("mobhunting.commands.select.no-select"));

		if (!(sel instanceof CuboidSelection))
			throw new IllegalArgumentException(Messages.getString("mobhunting.commands.select.select-type"));

		return sel.getMinimumPoint();
	}

	public static Location getPointB(Player player) throws IllegalArgumentException {
		if (mPlugin == null)
			throw new IllegalArgumentException("WorldEdit is not present");

		Selection sel = mPlugin.getSelection(player);

		if (sel == null)
			throw new IllegalArgumentException(Messages.getString("mobhunting.commands.select.no-select"));

		if (!(sel instanceof CuboidSelection))
			throw new IllegalArgumentException(Messages.getString("mobhunting.commands.select.select-type"));

		return sel.getMaximumPoint();
	}

	public static boolean isSupported() {
		return supported;
	}
}
