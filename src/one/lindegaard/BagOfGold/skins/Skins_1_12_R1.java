package one.lindegaard.BagOfGold.skins;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_12_R1.EntityPlayer;

public class Skins_1_12_R1 implements Skins {

	// How to get Playerskin
	// https://www.spigotmc.org/threads/how-to-get-a-players-texture.244966/

	@Override
	public String[] getSkin(Player player) {
		EntityPlayer playerNMS = ((CraftPlayer) player).getHandle();
		GameProfile profile = playerNMS.getProfile();
		Property property = profile.getProperties().get("textures").iterator().next();
		String[] result = { property.getValue(), property.getValue() };
		return result;
	}

}
