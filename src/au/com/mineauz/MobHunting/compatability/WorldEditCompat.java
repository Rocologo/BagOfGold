package au.com.mineauz.MobHunting.compatability;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import au.com.mineauz.MobHunting.Messages;
import au.com.mineauz.MobHunting.MobHunting;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class WorldEditCompat {
	private static WorldEditPlugin mPlugin;

	public WorldEditCompat() {
		mPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin(
				"WorldEdit");

		MobHunting.instance.getLogger().info(
				"Enabling compatability with WorldEdit ("
						+ getWorldEdit().getDescription().getVersion() + ")");
	}

	public static WorldEditPlugin getWorldEdit() {
		return mPlugin;
	}

	public static Location getPointA(Player player)
			throws IllegalArgumentException {
		if (mPlugin == null)
			throw new IllegalArgumentException("WorldEdit is not present"); //$NON-NLS-1$

		Selection sel = mPlugin.getSelection(player);

		if (sel == null)
			throw new IllegalArgumentException(
					Messages.getString("mobhunting.commands.select.no-select")); //$NON-NLS-1$

		if (!(sel instanceof CuboidSelection))
			throw new IllegalArgumentException(
					Messages.getString("mobhunting.commands.select.select-type")); //$NON-NLS-1$

		return sel.getMinimumPoint();
	}

	public static Location getPointB(Player player)
			throws IllegalArgumentException {
		if (mPlugin == null)
			throw new IllegalArgumentException("WorldEdit is not present"); //$NON-NLS-1$

		Selection sel = mPlugin.getSelection(player);

		if (sel == null)
			throw new IllegalArgumentException(
					Messages.getString("mobhunting.commands.select.no-select")); //$NON-NLS-1$

		if (!(sel instanceof CuboidSelection))
			throw new IllegalArgumentException(
					Messages.getString("mobhunting.commands.select.select-type")); //$NON-NLS-1$

		return sel.getMaximumPoint();
	}
}
