package de.Ste3et_C0st.AdvancementMSG.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import de.Ste3et_C0st.AdvancementMSG.main.AdvancementAPI.FrameType;
import one.lindegaard.MobHunting.MobHunting;

public class AdvancementAPIMain {
	
	public static List<AdvancementAPI> apiList = new ArrayList<AdvancementAPI>();
	
	
	public static void send(String title, String description, MaterialData material, Player ... player){
		AdvancementAPI test = new AdvancementAPI(new NamespacedKey(MobHunting.getInstance(), "story/" + UUID.randomUUID().toString()))
        .withFrame(FrameType.CHALLANGE)
        .withTrigger("minecraft:impossible")
        .withIcon(material)
        .withTitle(title)
        .withDescription(description)
        .withAnnouncement(false)
		.withBackground("minecraft:textures/blocks/bedrock.png");
		test.loadAdvancement();
		test.sendPlayer(player);
		
		Bukkit.getScheduler().runTaskLater(MobHunting.getInstance(), new Runnable() {
			@Override
			public void run() {
				test.delete(player);
			}
		}, 10);
		
	}

	public static void AddAdvancment(AdvancementAPI api){
		NamespacedKey key = api.getID();
		for(AdvancementAPI adAPI : apiList){
			if(adAPI.getID().toString().equalsIgnoreCase(key.toString())){
				return;
			}
		}
		apiList.add(api);
	}

}